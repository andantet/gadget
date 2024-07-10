package io.wispforest.gadget.mappings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.wispforest.gadget.util.DownloadUtil;
import io.wispforest.gadget.util.ProgressToast;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.MappingVisitor;
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.format.ProGuardReader;
import net.fabricmc.mappingio.format.Tiny2Reader;
import net.fabricmc.mappingio.format.Tiny2Writer;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class MojangMappings extends LoadingMappings {
    public static final String VERSION_MANIFEST_ENDPOINT = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";

    @Override
    protected void load(ProgressToast toast, MappingVisitor visitor) {
        try {
            Path mappingsDir = FabricLoader.getInstance().getGameDir().resolve("gadget").resolve("mappings");

            Files.createDirectories(mappingsDir);

            Path mojPath = mappingsDir.resolve("mojmap-" + SharedConstants.getCurrentVersion().getName() + ".tiny");

            if (Files.exists(mojPath)) {
                try (BufferedReader br = Files.newBufferedReader(mojPath)) {
                    Tiny2Reader.read(br, visitor);
                    return;
                }
            }

            MemoryMappingTree tree = new MemoryMappingTree(true);

            IntermediaryLoader.loadIntermediary(toast, tree);

            toast.step(Component.translatable("message.gadget.progress.downloading_minecraft_versions"));
            JsonArray versions = GsonHelper.getAsJsonArray(DownloadUtil.read(toast, VERSION_MANIFEST_ENDPOINT), "versions");
            String chosenUrl = null;

            for (int i = 0; i < versions.size(); i++) {
                JsonObject version = versions.get(i).getAsJsonObject();

                if (GsonHelper.getAsString(version, "id").equals(SharedConstants.getCurrentVersion().getId()))
                    chosenUrl = GsonHelper.getAsString(version, "url");
            }

            if (chosenUrl == null)
                throw new UnsupportedOperationException("Couldn't find version " + SharedConstants.getCurrentVersion().getId() + " on Mojang's servers!");

            toast.step(Component.translatable("message.gadget.progress.downloading_minecraft_version_manifest"));
            JsonObject manifest = DownloadUtil.read(toast, chosenUrl);
            JsonObject downloads = GsonHelper.getAsJsonObject(manifest, "downloads");
            JsonObject clientMappings = GsonHelper.getAsJsonObject(downloads, "client_mappings");
            JsonObject serverMappings = GsonHelper.getAsJsonObject(downloads, "server_mappings");
            var sw = new MappingSourceNsSwitch(tree, "official");

            toast.step(Component.translatable("message.gadget.progress.downloading_client_mappings"));
            readProGuardInto(toast, GsonHelper.getAsString(clientMappings, "url"), sw);
            toast.step(Component.translatable("message.gadget.progress.downloading_server_mappings"));
            readProGuardInto(toast, GsonHelper.getAsString(serverMappings, "url"), sw);

            try (BufferedWriter bw = Files.newBufferedWriter(mojPath)) {
                tree.accept(new Tiny2Writer(bw, false));
            }

            tree.accept(visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readProGuardInto(ProgressToast toast, String url, MappingVisitor visitor) throws IOException {
        try (var is = toast.loadWithProgress(new URL(url))) {
            ProGuardReader.read(new InputStreamReader(new BufferedInputStream(is)), "named", "official", visitor);
        }
    }
}

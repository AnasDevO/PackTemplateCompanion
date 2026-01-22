package com.barebonium.packCompanion.utils;

import com.barebonium.packCompanion.PackCompanion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ModlistCheckProcessor {
    private static final Gson GSON = new Gson();
    public static void checkModList() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName = "companionOutputLog-" + timeStamp + ".log";

        File modListGuide = new File(Minecraft.getMinecraft().gameDir, "config/packCompanion/modListGuide.json");
        File outputLog = new File(Minecraft.getMinecraft().gameDir, "config/packCompanion/modListLog.json");
        if (!modListGuide.exists()) {
            PackCompanion.LOGGER.info("modList JSON could not be fetched at {}", modListGuide.getPath());
        }
        try{
            BufferedReader reader = new BufferedReader(new FileReader(modListGuide));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputLog)));

            List<ModEntry> entries = GSON.fromJson(reader, new TypeToken<List<ModEntry>>(){}.getType());
            writer.println("=== Pack Companion Debug Report ===");
            for (ModEntry entry : entries) {
                boolean loaded = ModHelper.isModLoaded(entry.modId, entry.version, false, true);
                if (loaded) {
                    if (entry.replacementModName == null){
                        writer.printf("MOD %s is DEPRECATED, please remove it. Reason: %s%n",
                                entry.modId, entry.message != null ? entry.message : "");
                    }
                    else {
                        writer.printf("MOD %s is DEPRECATED, Please use %s. Reason:%s%n", entry.modId, entry.version, entry.message);
                    }
                }
            }
            PackCompanion.LOGGER.info("Loaded mods Successfully Analysed");
            PackCompanion.LOGGER.info("Please consult the Output log at {}", outputLog.getPath());
            writer.close();
        } catch (IOException e){
            PackCompanion.LOGGER.error("Error while trying to read modlist guide", e);
        }
    }
}

package com.barebonium.packcompanion.utils;

import com.barebonium.packcompanion.PackCompanion;
import com.barebonium.packcompanion.enumstates.Status;
import com.barebonium.packcompanion.rendermd.HTMLGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModlistCheckProcessor {

    public static File HTMLReportFile;
    private static final Gson GSON = new Gson();
    public static void checkModList(File configDir, File gameDir) {
        boolean isSuccess = false;
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        String timeStampFile = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        String fileName = "companionOutput-" + timeStampFile + ".md";

        File modListGuide = new File(configDir, "packCompanion/modListGuide.json");
        if (!modListGuide.exists()) {
            PackCompanion.LOGGER.info("modList JSON could not be fetched at {}", modListGuide.getPath());
        }

        File logDir = new File(configDir, "packCompanion/output");
        if (!logDir.exists()) logDir.mkdirs();
        File outputLog = new File(logDir, fileName);

        ArrayList<HTMLEntry> htmlEntries = new ArrayList<>();
        try{
            JsonReader reader = new JsonReader(new FileReader(modListGuide));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputLog)));

            List<ModEntry> entries = GSON.fromJson(reader, new TypeToken<List<ModEntry>>(){}.getType());
            writer.println("# Pack Companion Debug Report");

            writer.printf("Generated on: %s%n",timeStamp);
            if(entries == null || entries.isEmpty()) {
                PackCompanion.LOGGER.info("There are no Mods in the modListGuide");
            }
            else {
                writer.println("| Mod Name | Status | Recommended Action |");
                writer.println("| :--- | :--- | :--- |");

                for (ModEntry entry : entries) {
                    boolean loaded = ModHelper.isModLoaded(entry.modId, entry.version, entry.isMinVersion, entry.isMaxVersion);

                    if (loaded) {
                        String modName = ModHelper.getModName(entry.modId);
                        String statusStr = entry.status.toString();
                        String actionMessage;
                        switch (entry.action) {
                            case REMOVE:
                                actionMessage = "Remove " + modName;
                                break;
                            case REPLACE:
                                actionMessage = String.format("Replace with [%s](https://www.curseforge.com/minecraft/mc-mods/%s)",
                                        entry.replacementModName, entry.replacementModLink);
                                break;
                            case INCLUDE:
                                actionMessage = String.format("Use [%s](https://www.curseforge.com/minecraft/mc-mods/%s)",
                                        entry.replacementModName, entry.replacementModLink);
                                break;
                            case UPGRADE:
                                actionMessage = "Upgrade to version " + entry.replacementModVersion;
                                break;
                            case DOWNGRADE:
                                actionMessage = "Downgrade to version " + entry.replacementModVersion;
                                break;
                            default:
                                actionMessage = "Check mod compatibility";
                                break;
                        }
                        writer.printf("| %s | %s | %s |%n", modName, statusStr, actionMessage);
                        htmlEntries.add(new HTMLEntry(
                                modName,
                                entry.status,
                                entry.version,
                                entry.replacementModName,
                                entry.replacementModLink,
                                entry.action,
                                entry.isMinVersion,
                                entry.isMaxVersion,
                                entry.replacementModVersion
                        ));
                    }
                }
                isSuccess = true;
            }
            PackCompanion.LOGGER.info("Loaded mods Successfully Analysed");
            PackCompanion.LOGGER.info("Please consult the Output log at {}", outputLog.getPath());
            writer.close();
            reader.close();

            if(isSuccess) {
                File htmlOutput = new File(logDir, fileName.replace(".md", ".html"));

                HTMLGenerator.saveAsHtml(htmlEntries, htmlOutput, timeStamp);
            }

        } catch (IOException e){
            PackCompanion.LOGGER.error("Error while trying to read modlist guide", e);
        }
    }
}

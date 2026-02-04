package com.barebonium.packcompanion.utils;

import com.barebonium.packcompanion.PackCompanion;
import com.barebonium.packcompanion.config.ConfigHandler;
import com.barebonium.packcompanion.enumstates.Action;
import com.barebonium.packcompanion.rendermd.HTMLGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.barebonium.packcompanion.configparse.ConfigParser.processConfigJsonToOutput;

public class ModlistCheckProcessor {

    /**
     * An example of how to check if a mod is loaded and whether it should be added to the html list.
     */
    public static boolean shouldGenerateEntry(ModEntry entry) {
        if(ModHelper.isModLoaded(entry.modId)) {
            //If the version is empty then the mod likely needs to be fully removed.
            if(entry.version == null) {
                return true;
            } else {
                boolean isSpecifiedVersion = ModHelper.isSpecifiedVersion(entry.modId, entry.version, entry.isMinVersion, entry.isMaxVersion);
                // Returns true if the entry is inclusive and the version is the specified version range or true
                // if the entry is not inclusive, and it is not in the specified version range.
                return (entry.isVersionInclusive && isSpecifiedVersion) || (!entry.isVersionInclusive && !isSpecifiedVersion);
            }
        }
        return false;
    }

    public static File HTMLReportFile;
    public static File GlobalOutputLog;
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
            if (ConfigHandler.mdFileReportEnabled){
                writer.println("# Pack Companion Report");
                writer.println("");
                writer.printf("### Generated on: %s%n",timeStamp);
                writer.println("");
            }
            if(entries == null || entries.isEmpty()) {
                PackCompanion.LOGGER.info("There are no Mods in the modListGuide");
            }
            else {
                if(ConfigHandler.modAnalysisEnabled && ConfigHandler.mdFileReportEnabled){
                    writer.println("## Mod Analysis");
                    writer.println("| Mod Name | Status | Recommended Action | Reason |");
                    writer.println("| :--- | :--- | :--- | :--- |");
                    List<ModEntry> ModPatchList = new ArrayList<>();
                    for (ModEntry entry : entries) {

                        if (shouldGenerateEntry(entry)) {
                            String modName = ModHelper.getModName(entry.modId);
                            String statusStr = entry.status.toString();
                            String actionMessage;
                            switch (entry.action) {
                                case REMOVE:
                                    actionMessage = "Remove " + modName;
                                    break;
                                case REPLACE:
                                    actionMessage = String.format("Replace with [%s](%s)",
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
                            if (entry.action != Action.INCLUDE){
                                writer.printf("| %s | %s | %s | %s |%n", modName, statusStr, actionMessage, MessageRegex.translateToMarkdown(entry.message));
                            } else {
                                ModPatchList.add(entry);
                            }
                            htmlEntries.add(new HTMLEntry(
                                    modName,
                                    entry.status,
                                    entry.version,
                                    entry.replacementModName,
                                    entry.replacementModLink,
                                    entry.action,
                                    entry.isMinVersion,
                                    entry.isMaxVersion,
                                    entry.replacementModVersion,
                                    entry.patchList,
                                    entry.message
                            ));
                        }
                    }
                    writer.println("## Mods and Patches to include");
                    writer.println("| Mod Name | Patch for | Description |");
                    writer.println("| :--- | :--- | :--- |");
                    for(ModEntry entry : ModPatchList) {
                        String modName = ModHelper.getModName(entry.modId);

                        for (ModPatchEntry patchEntry : entry.patchList){
                            String patchName = String.format("[%s](https://www.curseforge.com/minecraft/mc-mods/%s)",
                                    patchEntry.modName, patchEntry.modLink);
                            writer.printf("| %s | %s | %s |%n", patchName, modName, patchEntry.modDescription);
                        }

                    }
                }
                if(ConfigHandler.configAnalysisEnabled && ConfigHandler.mdFileReportEnabled){
                    writer.println("## Config Analysis");
                    writer.println("| Mod Name | Config Name | Reason |");
                    writer.println("| :--- | :--- | :--- |");
                    File configEntries = new File(Minecraft.getMinecraft().gameDir, "config/packCompanion/configEntries.json");
                    processConfigJsonToOutput(configEntries, outputLog);
                }
                isSuccess = true;
            }
            writer.println("");
            PackCompanion.LOGGER.info("Loaded mods Successfully Analysed");
            PackCompanion.LOGGER.info("Please consult the Output log at {}", outputLog.getPath());
            writer.close();
            reader.close();

            if(isSuccess) {
                File htmlOutput = new File(logDir, fileName.replace(".md", ".html"));
                GlobalOutputLog = htmlOutput;
                if(ConfigHandler.htmlFileReportEnabled){
                    HTMLGenerator.saveAsHtml(htmlEntries, htmlOutput, timeStamp);
                }
            }

        } catch (IOException e){
            PackCompanion.LOGGER.error("Error while trying to read modlist guide", e);
        }
    }
}

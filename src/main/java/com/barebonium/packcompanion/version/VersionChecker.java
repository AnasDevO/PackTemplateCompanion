package com.barebonium.packcompanion.version;

import com.barebonium.packcompanion.PackCompanion;
import com.barebonium.packcompanion.utils.FileHashCalculator;
import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {
    private static final String API_URL = "https://api.github.com/repos/AnasDevO/PackTemplateCompanion/releases/latest";
    private static boolean modListGuideIntegrity = true;
    private static boolean configEntriesIntegrity = true;
    private static boolean cleanroomListGuideIntegrity = true;
    public static void checkAndDownload() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");


            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject response = new JsonParser().parse(reader).getAsJsonObject();
                JsonArray assets = response.get("assets").getAsJsonArray();
                String currentVersion = getCachedVersion();
                String latestVersion = response.get("tag_name").getAsString();
                if (!latestVersion.equals(currentVersion)) {
                    PackCompanion.LOGGER.info("New version found: {}", latestVersion);

                    for (JsonElement asset : assets) {
                        JsonObject assetObject = asset.getAsJsonObject();
                        String filename = assetObject.get("name").getAsString();
                        switch (filename) {
                            case "modListGuide.json":
                            {
                                String downloadUrl = assetObject.get("browser_download_url").getAsString();
                                File targetFile = new File(PackCompanion.configDir, "modListGuide.json");
                                downloadFile(downloadUrl, targetFile);

                                String ModlistHashCache = FileHashCalculator.getFileHash(targetFile, "MD5");


                                saveToCache("modListGuideHash",ModlistHashCache);
                                modListGuideIntegrity = true;
                                break;
                            }
                            case "configEntries.json":
                            {
                                String downloadUrl = assetObject.get("browser_download_url").getAsString();
                                File targetFile = new File(PackCompanion.configDir, "configEntries.json");
                                downloadFile(downloadUrl, targetFile);

                                String ConfigHashCache = FileHashCalculator.getFileHash(targetFile, "MD5");

                                saveToCache("configEntriesHash",ConfigHashCache);
                                configEntriesIntegrity = true;
                                break;
                            }
                            case "CleanroomListGuide.json":
                            {
                                String downloadUrl = assetObject.get("browser_download_url").getAsString();
                                File targetFile = new File(PackCompanion.configDir, "CleanroomListGuide.json");
                                downloadFile(downloadUrl, targetFile);

                                String CleanroomListHashCache = FileHashCalculator.getFileHash(targetFile, "MD5");

                                saveToCache("CleanroomListGuideHash",CleanroomListHashCache);
                                cleanroomListGuideIntegrity = true;
                                break;
                            }
                        }

                    }
                    saveToCache("version",latestVersion);
                } else {
                    File targetModListGuide = new File(PackCompanion.configDir, "modListGuide.json");
                    String modListHashValue = FileHashCalculator.getFileHash(targetModListGuide, "MD5");

                    File targetConfigEntries = new File(PackCompanion.configDir, "configEntries.json");
                    String configHashValue = FileHashCalculator.getFileHash(targetConfigEntries, "MD5");

                    File targetCleanroomListGuide = new File(PackCompanion.configDir, "CleanroomListGuide.json");
                    String cleanroomListHashValue = FileHashCalculator.getFileHash(targetCleanroomListGuide, "MD5");

                    checkCache(targetModListGuide, modListHashValue, "modListGuideHash", "ModListGuide", modListGuideIntegrity);
                    checkCache(targetConfigEntries, configHashValue, "configEntriesHash", "ConfigEntries", configEntriesIntegrity);
                    checkCache(targetCleanroomListGuide, cleanroomListHashValue, "CleanroomListGuideHash", "CleanroomListGuide", cleanroomListGuideIntegrity);

                    if (modListGuideIntegrity && configEntriesIntegrity &&  cleanroomListGuideIntegrity) {
                        PackCompanion.LOGGER.warn("Already up to date.");
                    }
                }


            }
        } catch (Exception e) {
            PackCompanion.LOGGER.error("Error while fetching modlist guide", e);
        }
    }
    private static void downloadFile(String urlString, File destination) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(destination)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            PackCompanion.LOGGER.info("Successfully saved updated guide to: {}", destination.getAbsolutePath());
        } catch (IOException e) {
            PackCompanion.LOGGER.error("Error downloading modlist guide", e);
        }
    }
    private static File getVersionCacheFile() {
        return new File(PackCompanion.configDir, "version.json");
    }

    private static JsonObject getCacheJson() {
        File cacheFile = getVersionCacheFile();
        if (!cacheFile.exists()) return new JsonObject();

        try(FileReader reader = new FileReader(cacheFile)) {
            return new JsonParser().parse(reader).getAsJsonObject();
        } catch(IOException e) {
            return new JsonObject();
        }
    }
    private static String getCachedVersion() {
        JsonObject cacheFile = getCacheJson();
        String key = "version";
        if (cacheFile.has(key)) {
            return cacheFile.get(key).getAsString();
        }
        return "0.0.0";
    }

    private static String getCachedHash(String hashKey) {
        JsonObject cacheFile = getCacheJson();
        if (cacheFile.has(hashKey)) {
            return cacheFile.get(hashKey).getAsString();
        }
        return null;
    }

    private static void saveToCache(String key,String value) {
        JsonObject cacheFile = getCacheJson();
        cacheFile.addProperty(key, value);
        try(PrintWriter writer = new PrintWriter(new FileWriter(getVersionCacheFile()))){
            new GsonBuilder().setPrettyPrinting().create().toJson(cacheFile, writer);
        } catch (IOException e) {
            PackCompanion.LOGGER.error("Failed to update cache key: {}", key);
        }
    }
    private static void checkCache(File targetFile, String hashValue, String hashKey, String name, Boolean integrityValue){
        if(getCachedHash(hashKey)!= null ) {
            if (hashValue.equals(getCachedHash(hashKey))) {
                PackCompanion.LOGGER.info("{} Integrity Check pass", name);
            } else {
                PackCompanion.LOGGER.error("{} Integrity Check fail", name);
                if (targetFile.exists()) {
                    try{
                        targetFile.delete();
                    } catch (Exception e){
                        PackCompanion.LOGGER.error("Error deleting {}", name, e);
                    }
                }
                saveToCache("version","0.0.0");
                integrityValue = false;
                checkAndDownload();
            }
        } else {
            saveToCache("version","0.0.0");
            integrityValue = false;
            checkAndDownload();
        }
    }

}

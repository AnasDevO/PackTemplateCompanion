package com.barebonium.packcompanion.version;

import com.barebonium.packcompanion.PackCompanion;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {
    private static final String API_URL = "https://api.github.com/repos/AnasDevO/PackTemplateCompanion/releases/latest";

    public static void checkAndDownload() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject response = new JsonParser().parse(reader).getAsJsonObject();

                String currentVersion = getCachedVersion();
                String latestVersion = response.get("tag_name").getAsString();

                if (!latestVersion.equals(currentVersion)) {
                    PackCompanion.LOGGER.info("New version found: {}", latestVersion);

                    String downloadUrl = response.get("assets").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("browser_download_url").getAsString();

                    File targetFile = new File(PackCompanion.configDir, "modListGuide.json");
                    downloadFile(downloadUrl, targetFile);
                    saveVersionToCache(latestVersion);
                } else {
                    PackCompanion.LOGGER.warn("Already up to date.");
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
        return new File(PackCompanion.configDir, "version.cache");
    }
    private static File getModListGuide() {
        return new File(PackCompanion.configDir, "modListGuide.json");
    }

    private static String getCachedVersion() {
        File cacheFile = getVersionCacheFile();
        File actualFile = getModListGuide();
        if (!cacheFile.exists()){
            return "0.0.0";
        }
        else if (!actualFile.exists()){
            if (cacheFile.delete()) {
                PackCompanion.LOGGER.info("Deleted version.cache because modListGuide.json was missing.");
                return "0.0.0";
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
            return reader.readLine();
        } catch (IOException e) {
            return "0.0.0";
        }
    }

    private static void saveVersionToCache(String version) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(getVersionCacheFile()))) {
            writer.print(version);
        } catch (IOException e) {
            PackCompanion.LOGGER.error("Failed to cache version: {}", version);
        }
    }
}

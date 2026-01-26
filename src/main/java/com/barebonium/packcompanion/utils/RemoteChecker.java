package com.barebonium.packcompanion.utils;

import com.barebonium.packcompanion.PackCompanion;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;

public class RemoteChecker {
//    private static SIBConfig readRemoteConfig(File minecraftDir, String url) {
//
//
//        Gson gson = new Gson();
//        File cacheFile = new File(SerializationIsBad.getConfigDir(minecraftDir), "serializationisbad-remotecache.json");
//
//        try {
//            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
//            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//            sslContext.init(null, null, new SecureRandom());
//            connection.setSSLSocketFactory(sslContext.getSocketFactory());
//            connection.setConnectTimeout(60000);
//            connection.setReadTimeout(60000);
//
//            if (connection.getResponseCode() != 200) throw new IOException("Invalid response code: " + connection.getResponseCode());
//
//            byte[] configBytes = SerializationIsBad.readInputStream(connection.getInputStream());
//            SIBConfig remoteConfig = gson.fromJson(new String(configBytes, StandardCharsets.UTF_8), SIBConfig.class);
//
//            try (FileOutputStream fileOutputStream = new FileOutputStream(cacheFile)) {
//                fileOutputStream.write(configBytes);
//            }
//
//            return remoteConfig;
//        } catch (Exception e) {
//            PackCompanion.LOGGER.error("Failed to load remote config file", e);
//        }
//
//        if (cacheFile.isFile()) {
//            try (FileInputStream fileInputStream = new FileInputStream(cacheFile)) {
//                return gson.fromJson(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8), SIBConfig.class);
//            } catch (Exception e) {
//                PackCompanion.LOGGER.error("Failed to load cached remote config file", e);
//            }
//        }
//
//        return null;
//    }
}

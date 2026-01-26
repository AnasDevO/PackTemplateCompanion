package com.barebonium.packcompanion.utils;

import java.io.File;

public class ConfigInitialiser {
    public static void initialise(File configDir) {
        File logDir = new File(configDir, "packCompanion/output");
        if (!logDir.exists()) logDir.mkdirs();
    }
}

package com.barebonium.packcompanion;

import com.barebonium.packcompanion.config.ConfigHandler;
import com.barebonium.packcompanion.events.LoginHandler;
import com.barebonium.packcompanion.processors.OutputProcessor;
import com.barebonium.packcompanion.version.VersionChecker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = PackCompanion.MOD_ID,
        name = PackCompanion.MOD_NAME,
        version = PackCompanion.MOD_VERSION,
        dependencies = "required-before:universaltweaks@[1.19.0,)"
)
public class PackCompanion {
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String MOD_VERSION = Tags.VERSION;

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    public static boolean isCleanroomEnv;
    public static File configDir;
    public static File cacheDir;
    public static File outputDir;
    public static File gameDir;
    public static File logsDir;
    public static long preStartTime;
    public static long preEndTime;
    public static long postStartTime;
    public static long postEndTime;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        try {
            Class.forName("com.cleanroommc.boot.Main");
            isCleanroomEnv = true;
        } catch (ClassNotFoundException e) {
            isCleanroomEnv = false;
        }
        gameDir = event.getModConfigurationDirectory().getParentFile();
        configDir = OutputProcessor.initializeDirectory(event.getModConfigurationDirectory(), PackCompanion.MOD_ID);
        cacheDir = OutputProcessor.initializeDirectory(configDir, "cache");
        outputDir = OutputProcessor.initializeDirectory(configDir, "output");
        logsDir = OutputProcessor.initializeDirectory(gameDir, "crash-reports");
        if(ConfigHandler.enableLoginMessage) {
            MinecraftForge.EVENT_BUS.register(new LoginHandler());
        }
        preStartTime = System.currentTimeMillis();
        if (ConfigHandler.enableVersionChecker) {
            VersionChecker.checkAndDownload();
        }
        if (ConfigHandler.debugMode) {
            LOGGER.info("Running PreInitChecks");
        }

        LOGGER.info("{} is Checking your modlist!", Tags.MOD_NAME);
        OutputProcessor.runPreInitPackCompanionChecks();
        preEndTime = System.currentTimeMillis();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        if (ConfigHandler.debugMode) {
            LOGGER.info("Running PostInitChecks");
        }
        postStartTime = System.currentTimeMillis();
        OutputProcessor.runPostInitPackCompanionChecks();
        postEndTime = System.currentTimeMillis();
        LOGGER.info("{} took {}ms to complete its analysis.", PackCompanion.MOD_NAME, (preEndTime - preStartTime)+(postEndTime - postStartTime));
    }

}

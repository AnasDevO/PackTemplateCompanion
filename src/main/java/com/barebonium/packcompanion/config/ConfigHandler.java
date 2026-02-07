package com.barebonium.packcompanion.config;

import net.minecraftforge.common.config.Config;
import com.barebonium.packcompanion.Tags;

@Config(modid = Tags.MOD_ID)
public class ConfigHandler {
    @Config.RequiresMcRestart
    @Config.Comment("Enable PackCompanion Features")
    public static boolean packCompanionEnabled = true;

    @Config.RequiresMcRestart
    @Config.Comment("Enable PackCompanion Mod Analysis")
    public static boolean modAnalysisEnabled = true;

    @Config.RequiresMcRestart
    @Config.Comment("Enable PackCompanion Config Analysis")
    public static boolean configAnalysisEnabled = true;

    @Config.RequiresMcRestart
    @Config.Comment("Save PackCompanion Report as a MarkdownFile")
    public static boolean mdFileReportEnabled = true;

    @Config.RequiresMcRestart
    @Config.Comment("Save PackCompanion Report as an HTML file")
    public static boolean htmlFileReportEnabled = true;

    @Config.RequiresMcRestart
    @Config.Comment("Enable PackCompanion login messages")
    public static boolean globalOnLoginMessageEnabled = true;

    @Config.RequiresMcRestart
    @Config.Comment("Enable PackCompanion HTML login message")
    public static boolean htmlOnLoginMessageEnabled = true;

    @Config.RequiresMcRestart
    @Config.Comment("Enable PackCompanion Markdown login message")
    public static boolean mdOnLoginMessageEnabled = true;

    @Config.RequiresMcRestart
    @Config.Comment("Enable debug mode")
    public static boolean debugMode = false;
}
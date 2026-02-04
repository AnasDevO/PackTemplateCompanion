package com.barebonium.packcompanion;

import com.barebonium.packcompanion.config.ConfigHandler;
import com.barebonium.packcompanion.utils.ConfigInitialiser;
import com.barebonium.packcompanion.utils.ModlistCheckProcessor;
import com.barebonium.packcompanion.version.VersionChecker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class PackCompanion {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    public static File configDir;
    public static File baseConfig;
    public static File gameDir;

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        baseConfig = event.getModConfigurationDirectory();
        gameDir = event.getModConfigurationDirectory().getParentFile();

        configDir = new File(baseConfig, "packCompanion");
    }
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

        if(ConfigHandler.packCompanionEnabled && ConfigHandler.globalOnLoginMessageEnabled){
            ITextComponent textComponent = new TextComponentString(
                    TextFormatting.GOLD + "[Pack Companion] " + TextFormatting.GRAY + "Modlist analysis complete. Check your logs folder for the compatibility report!"
            );

            ITextComponent textComponentLink = new TextComponentString(TextFormatting.GOLD + "[Pack Companion] " + TextFormatting.GRAY + "Please click ");
            ITextComponent clickableHere = new TextComponentString(TextFormatting.RED + "[ HERE ]");

            File reportFile = ModlistCheckProcessor.HTMLReportFile;

            clickableHere.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, reportFile.getAbsolutePath()));
            clickableHere.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open report in your browser")));

            textComponentLink.appendSibling(clickableHere);
            textComponentLink.appendText(TextFormatting.GRAY + " to access the Web version of your report.");

            if(ConfigHandler.mdOnLoginMessageEnabled){
                event.player.sendMessage(textComponent);
            }

            if(ConfigHandler.htmlOnLoginMessageEnabled){
                event.player.sendMessage(textComponentLink);
            }
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){

        if(ConfigHandler.packCompanionEnabled){
            ConfigInitialiser.initialise(baseConfig);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
            VersionChecker.checkAndDownload();

            LOGGER.info("{} is Checking your modlist!", Tags.MOD_NAME);
            ModlistCheckProcessor.checkModList(baseConfig, gameDir);
        }
    }



}

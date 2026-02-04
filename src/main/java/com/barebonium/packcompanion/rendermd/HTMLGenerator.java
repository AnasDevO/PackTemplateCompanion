package com.barebonium.packcompanion.rendermd;


import com.barebonium.packcompanion.PackCompanion;
import com.barebonium.packcompanion.config.ConfigHandler;
import com.barebonium.packcompanion.configparse.ConfigParser;
import com.barebonium.packcompanion.enumstates.Action;
import com.barebonium.packcompanion.utils.HTMLEntry;
import com.barebonium.packcompanion.utils.MessageRegex;
import com.barebonium.packcompanion.utils.ModPatchEntry;
import com.barebonium.packcompanion.utils.ModlistCheckProcessor;
import net.minecraftforge.common.config.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HTMLGenerator {
    public static String GlobalHTML;
    public static void saveAsHtml(ArrayList<HTMLEntry> htmlEntries, File file, String timeStamp) {
        String htmlHeader = "<!DOCTYPE html><html><head><title>Pack Companion Report</title>" +
                "<style>" +
                "  :root { " +
                "    --bg-color: #0d1117; --text-color: #c9d1d9; --table-border: #30363d; " +
                "    --header-bg: #161b22; --row-even: #161b22; --accent: #58a6ff; --title-border: #30363d; " +
                "  }" +
                "  body.light-mode { " +
                "    --bg-color: #ffffff; --text-color: #24292f; --table-border: #d0d7de; " +
                "    --header-bg: #f6f8fa; --row-even: #f6f8fa; --accent: #0969da; --title-border: #d8dee4; " +
                "  }" +
                "  body { font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Helvetica, Arial, sans-serif; " +
                "         padding: 45px; line-height: 1.5; max-width: 1012px; margin: auto; " +
                "         background-color: var(--bg-color); color: var(--text-color); transition: background 0.3s, color 0.3s; }" +
                "  table { border-collapse: collapse; width: 100%; margin-bottom: 16px; border-spacing: 0; }" +
                "  th, td { padding: 6px 13px; border: 1px solid var(--table-border); }" +
                "  th { font-weight: 600; background-color: var(--header-bg); color: var(--accent); }" +
                " td { text-align: center }"+
                "  tr:nth-child(even) { background-color: var(--row-even); }" +
                "  h1 { border-bottom: 1px solid var(--title-border); padding-bottom: .3em; display: flex; justify-content: space-between; align-items: center; }" +
                "  .problematic { color: #ff7b72; font-weight: bold; } " +
                "  body.light-mode .problematic { color: #cf222e; }" +
                "  .deprecated { color: #ff7b72; font-weight: bold; } " +
                "  body.light-mode .deprecated { color: #cf222e; }" +
                "  a:link {color: #0969da; font-weight: bold;}"+
                "  body.light-mode a:link {color: #0969da; font-weight: bold;}"+
                "  .toggle-btn { padding: 8px 16px; font-size: 14px; border-radius: 6px; cursor: pointer; " +
                "                border: 1px solid var(--table-border); background: var(--header-bg); color: var(--text-color); }" +
                "</style></head><body>" +
                "<h1>Pack Companion Report <button class='toggle-btn' onclick='toggleTheme()'>Toggle Theme</button></h1>" +
                "<h4>Generated On: "+ timeStamp + "</h4>";


        StringBuilder tableHtml = new StringBuilder();
        StringBuilder patchListTable = new StringBuilder();
        List<HTMLEntry> modPatchList = new ArrayList<>();
        if (ConfigHandler.modAnalysisEnabled){
            tableHtml.append("<h2>Mod Analysis</h2> <table>");
            tableHtml.append("<tr>");
            tableHtml.append("<th>").append("Mod Name").append("</th>");
            tableHtml.append("<th>").append("Status").append("</th>");
            tableHtml.append("<th>").append("Recommended Action").append("</th>");
            tableHtml.append("<th>").append("Reason").append("</th>");
            tableHtml.append("</tr>");

            for(HTMLEntry htmlEntry : htmlEntries){
                String modName = htmlEntry.modName;
                String statusStr = htmlEntry.status.toString();
                String htmlClass;
                String actionMessage;


                tableHtml.append("<tr>");
                tableHtml.append("<td>").append(modName).append("</td>");




                switch (htmlEntry.action) {
                    case REMOVE:
                        actionMessage = "<p>Remove " + modName+ "</p>";
                        break;
                    case REPLACE:
                        actionMessage = String.format("<p>Replace with <a href=\"%s\">%s</a></p>",
                                htmlEntry.replacementModLink, htmlEntry.replacementModName);
                        break;
//                case INCLUDE:
//                    actionMessage = String.format("<p>Use <a href=\"https://www.curseforge.com/minecraft/mc-mods/%s\">%s</a></p>",
//                            htmlEntry.replacementModLink, htmlEntry.replacementModName);
//                    break;
                    case UPGRADE:
                        actionMessage = "<p>Upgrade to version " + htmlEntry.replacementModVersion+"</p>";
                        break;
                    case DOWNGRADE:
                        actionMessage = "<p>Downgrade to version " + htmlEntry.replacementModVersion+"</p>";
                        break;
                    default:
                        actionMessage = "<p>Check mod compatibility</p>";
                        break;
                }
                switch(htmlEntry.status){
                    case DEPRECATED:
                        htmlClass = "deprecated";
                        break;
                    case PROBLEMATIC:
                        htmlClass = "problematic";
                        break;
                    default:
                        htmlClass = "";
                        break;
                }
                if(htmlEntry.action != Action.INCLUDE){
                    tableHtml.append("<td class=")
                            .append(htmlClass)
                            .append(">")
                            .append(statusStr)
                            .append("</td>");

                    tableHtml.append("<td>")
                            .append(actionMessage)
                            .append("</td>");
                    tableHtml.append("<td>")
                            .append(MessageRegex.translateToHTML(htmlEntry.message))
                            .append("</td>");
                    tableHtml.append("</tr>");
                } else {
                    modPatchList.add(htmlEntry);
                }
            }
            tableHtml.append("</table>");

            patchListTable.append("<h2>Mods and Patches to include</h2> <table>");
            patchListTable.append("<tr>");
            patchListTable.append("<th>").append("Mod Name").append("</th>");
            patchListTable.append("<th>").append("Patch for mod").append("</th>");
            patchListTable.append("<th>").append("Description").append("</th>");
            patchListTable.append("</tr>");
            for (HTMLEntry htmlEntry : modPatchList){
                String modName = htmlEntry.modName;

                for (ModPatchEntry patchEntry : htmlEntry.patchList){
                    String patchModName = String.format("<p><a href=\"https://www.curseforge.com/minecraft/mc-mods/%s\">%s</a></p>",
                            patchEntry.modLink, patchEntry.modName);
                    patchListTable.append("<tr>");
                    patchListTable.append("<td>").append(patchModName).append("</td>");
                    patchListTable.append("<td>").append(modName).append("</td>");
                    patchListTable.append("<td>").append(patchEntry.modDescription).append("</td>");
                }



            }
            patchListTable.append("</table>");
        }

        String script = "<script>" +
                "  function toggleTheme() { document.body.classList.toggle('light-mode'); }" +
                "</script>";

        if(modPatchList.isEmpty()){
            patchListTable = new StringBuilder();
        }
        String ConfigTableFinal="";
        if(ConfigHandler.configAnalysisEnabled && ConfigParser.ConfigEntryIndex != 0){
            ConfigTableFinal = ConfigParser.ConfigTable.toString();
        }

        String finalHtml = htmlHeader + tableHtml + patchListTable  + ConfigTableFinal + script + "</body></html>";

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.println(finalHtml);
        } catch (IOException e) {
            PackCompanion.LOGGER.error("Failed to write HTML report", e);
        }
        ModlistCheckProcessor.HTMLReportFile = file;
    }
}

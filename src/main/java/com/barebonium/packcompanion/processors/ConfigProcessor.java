package com.barebonium.packcompanion.processors;

import com.barebonium.packcompanion.PackCompanion;
import com.barebonium.packcompanion.entries.ConfigEntry;
import com.barebonium.packcompanion.entries.ModDependency;
import com.barebonium.packcompanion.entries.ConfigSetting;
import com.barebonium.packcompanion.enumstates.DependencyMode;
import com.barebonium.packcompanion.utils.MessageRegex;
import com.barebonium.packcompanion.utils.helpers.ModHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class ConfigProcessor {
    private static final Gson GSON = new Gson();
    public static StringBuilder ConfigTable= new StringBuilder("<h2>Config Analysis</h2> <details class=\"dropdown\"> <summary>Show Config Analysis</summary> <table>");
    public static int ConfigEntryIndex = 0;


    public static boolean checkConfigs(PrintWriter writer) {
        PackCompanion.LOGGER.info("Beginning processing config json into Log Output");
        boolean isSuccess = false;
        try {
            writer.println("## Config Analysis");
            writer.printf("| %-25s | %-40s | %-150s |%n", "Mod Name", "Config Name", "Reason");
            writer.printf("| %-25s | %-40s | %-150s |%n", ":---", ":---", ":---");

            File configEntries = OutputProcessor.verifyCachedFile("masterlist_configs.json");
            JsonReader reader = new JsonReader(new FileReader(configEntries));
            List<ConfigEntry> entries = GSON.fromJson(reader, new TypeToken<List<ConfigEntry>>(){}.getType());
            reader.close();

            processConfigJsonToOutput(writer, entries);
            writer.println("");
            isSuccess = true;
        } catch (IOException e) {
            PackCompanion.LOGGER.error("Error while trying to process config json", e);
        }
        PackCompanion.LOGGER.info("Finished processing config json");
        return isSuccess;
    }

    public static void processConfigJsonToOutput(PrintWriter mdWriter, List<ConfigEntry> entries) throws FileNotFoundException {
        try{
            if (entries == null) return;
            ConfigTable.append("<tr>");
            ConfigTable.append("<th>").append("Mod Name").append("</th>");
            ConfigTable.append("<th>").append("Config Name").append("</th>");
            ConfigTable.append("<th>").append("Reason").append("</th>");
            ConfigTable.append("</tr>");
            for (ConfigEntry entry : entries) {
                boolean loaded = ModHelper.isModLoaded(entry.modId, entry.version, entry.isMinVersion, entry.isMaxVersion);

                if (!loaded) continue;

                for (ConfigSetting setting : entry.settings) {
                    boolean dependenciesLoaded = true;
                    String modName = ModHelper.getModName(entry.modId);
                    boolean anyOrMatched = false;

                    for (ModDependency dependency : setting.dependencies) {
                        boolean dependencyLoaded;
                        if (dependency.classLoaded){
                            dependencyLoaded = ModHelper.isClassLoaded(dependency.className);
                        } else {
                            dependencyLoaded = ModHelper.isModLoaded(
                                    dependency.modId,
                                    dependency.version,
                                    dependency.isMinVersion,
                                    dependency.isMaxVersion
                            );
                        }

                        if (!dependencyLoaded && setting.dependencyMode == DependencyMode.AND) {
                            PackCompanion.LOGGER.info("Dependency {} Not Found for Mod Id {} ",dependency.modId, entry.modId);
                            dependenciesLoaded = false;
                            break;
                        }else if (dependencyLoaded && setting.dependencyMode == DependencyMode.OR) {
                            anyOrMatched = true;
                            break;
                        }
                    }
                    if (setting.dependencyMode == DependencyMode.OR && !setting.dependencies.isEmpty() && !anyOrMatched) {
                        dependenciesLoaded = false;
                    }
                    if (dependenciesLoaded) {
                        PackCompanion.LOGGER.info("Dependencies Loaded for Mod Id: {}", entry.modId);
                        if (checkConfigMatch(setting)) {
                            if (setting.shouldMatch) {
                                mdWriter.printf("| %-25s | %-40s | %-150s |%n", modName, setting.name, setting.message);
                                ConfigTable.append("<td>")
                                        .append(modName)
                                        .append("</td>");

                                ConfigTable.append("<td class=\"config-name\">")
                                        .append(setting.name)
                                        .append("</td>");
                                ConfigTable.append("<td>")
                                        .append(MessageRegex.translateToHTML(setting.message))
                                        .append("</td>");
                                ConfigTable.append("</tr>");
                                ConfigEntryIndex +=1;

                            } else {
                                PackCompanion.LOGGER.info("Setting {} is correctly set", setting.field);
                            }

                        } else {
                            if (setting.shouldMatch) {
                                PackCompanion.LOGGER.info("Setting {} is correctly set", setting.field);
                            } else {
                                if (setting.name==null){
                                    String[] split = setting.field.split("#");
                                    setting.name = split[1];
                                }
                                mdWriter.printf("| %-25s | %-40s | %-150s |%n", modName, setting.name, setting.message);
                                ConfigTable.append("<td>")
                                        .append(modName)
                                        .append("</td>");

                                ConfigTable.append("<td class=\"config-name\">")
                                        .append(setting.name)
                                        .append("</td>");
                                ConfigTable.append("<td>")
                                        .append(MessageRegex.translateToHTML(setting.message))
                                        .append("</td>");
                                ConfigTable.append("</tr>");
                                ConfigEntryIndex +=1;
                            }
                        }
                    }
                }
            }
            ConfigTable.append("</table> </details>");
        } catch (Exception e){
            PackCompanion.LOGGER.error("Error while trying to process config json", e);
        }
    }

    private static boolean checkConfigMatch(ConfigSetting setting) {
        try {
            PackCompanion.LOGGER.warn("Checking config match for Mod Id: {}", setting.field);
            Object currentValue = null;
            try{
                currentValue  = getConfigObject(setting.field);
            } catch(Exception e){
                PackCompanion.LOGGER.error("Error while trying to process config setting", e);
            }
            PackCompanion.LOGGER.info("Current value object is {} ",currentValue);
            if (setting.type.startsWith("list")) {
                PackCompanion.LOGGER.info("The field is a List, Processing..");
                if (setting.value.equals("[]")) {
                    if (currentValue instanceof List)
                        return ((List<?>) currentValue).isEmpty();
                    if (currentValue.getClass().isArray())
                        return java.lang.reflect.Array.getLength(currentValue) == 0;
                }else if (setting.value.startsWith("Contains:")) {
                    String jsonPart = setting.value.substring("Contains:".length()).trim();

                    String subTypeName = setting.type.substring("list_".length()).trim();
                    Class<?> elementClass = getElementClass(subTypeName);

                    Type listType = new ListTypeToken(elementClass);
                    List<?> checkList = GSON.fromJson(jsonPart, listType);

                    if (currentValue instanceof List) {
                        List<?> actualList = (List<?>) currentValue;
                        PackCompanion.LOGGER.info("Returning List Match");
                        return actualList.containsAll(checkList);
                    }
                    PackCompanion.LOGGER.info("No List Match, returning False");
                    return false;
                }
            }
            PackCompanion.LOGGER.info("The field is not a List, Processing..");
            Object expectedValue = GSON.fromJson(setting.value, currentValue.getClass());
            PackCompanion.LOGGER.info("Expected value {} found {}", expectedValue, currentValue);
            PackCompanion.LOGGER.info("returning {}", Objects.equals(currentValue, expectedValue));
            return Objects.equals(currentValue, expectedValue);
        } catch (Exception e) {
            PackCompanion.LOGGER.error("Error while trying to process config setting {}",setting.field, e);
            return false;
        }
    }

    private static Object getConfigObject(String fieldStr) throws Exception {
        String[] split = fieldStr.split("#");
        Class<?> clazz = Class.forName(split[0]);

        Field field = clazz.getDeclaredField(split[1]);
        field.setAccessible(true);
        Object cfgObj = field.get(null);

        for (int i = 2; i < split.length; i++) {
            field = cfgObj.getClass().getDeclaredField(split[i]);
            field.setAccessible(true);
            cfgObj = field.get(cfgObj);
        }
        return cfgObj;
    }

    private static Class<?> getElementClass(String type) {
        switch (type.toLowerCase()) {
            case "int":     return Integer.class;
            case "int_p":   return int.class;
            case "boolean":    return Boolean.class;
            case "boolean_p":  return boolean.class;
            case "double":  return Double.class;
            case "double_p":return double.class;
            case "float":   return Float.class;
            case "float_p": return float.class;
            case "string":  return String.class;
            default:        return String.class;
        }
    }

    private static class ListTypeToken implements ParameterizedType {
        private final Class<?> wrapped;

        public ListTypeToken(Class<?> wrapped) { this.wrapped = wrapped; }

        @Override @Nonnull
        public Type[] getActualTypeArguments() { return new Type[] {wrapped}; }
        @Override @Nonnull
        public Type getRawType() { return List.class; }
        @Override
        public Type getOwnerType() { return null; }
    }
}
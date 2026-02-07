package com.barebonium.packcompanion.configparse;

import com.barebonium.packcompanion.PackCompanion;
import com.barebonium.packcompanion.entries.ConfigEntry;
import com.barebonium.packcompanion.utils.ConfigSetting;
import com.barebonium.packcompanion.entries.ModDependency;
import com.barebonium.packcompanion.utils.ModHelper;
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

public class ConfigParser {
    private static final Gson GSON = new Gson();
    public static StringBuilder ConfigTable= new StringBuilder("<h2>Config Analysis</h2> <table>");
    public static int ConfigEntryIndex = 0;


    public static void processConfigJsonToOutput(File ConfigGuide, File outputLog) throws FileNotFoundException {
        PackCompanion.LOGGER.info("Beginning processing config json into Log Output");
        JsonReader reader = new JsonReader(new FileReader(ConfigGuide));

        try{
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputLog)));
            List<ConfigEntry> entries = GSON.fromJson(reader, new TypeToken<List<ConfigEntry>>(){}.getType());

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
                    String modName = ModHelper.getModName(entry.modId);
                    boolean dependenciesLoaded = false;
                    if (!setting.dependencies.isEmpty()) {
                        for(ModDependency dependency: setting.dependencies){
                            boolean dependencyLoaded = ModHelper.isModLoaded(dependency.modId, dependency.version, dependency.isMinVersion, dependency.isMaxVersion);
                            if (!dependencyLoaded){
                                dependenciesLoaded = false;
                                break;
                            }else{
                                dependenciesLoaded = true;
                            }
                        }

                    } else {
                        dependenciesLoaded = true;
                    }
                    if (dependenciesLoaded) {
                        if (checkConfigMatch(setting)) {
                            if (setting.shouldMatch) {
                                writer.printf("| %s | %s | %s |%n", modName, setting.name, setting.message);
                                ConfigTable.append("<td>")
                                        .append(modName)
                                        .append("</td>");

                                ConfigTable.append("<td>")
                                        .append(setting.name)
                                        .append("</td>");
                                ConfigTable.append("<td>")
                                        .append(setting.message)
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
                                writer.printf("| %s | %s | %s |%n", modName, setting.name, setting.message);
                                ConfigTable.append("<td>")
                                        .append(modName)
                                        .append("</td>");

                                ConfigTable.append("<td>")
                                        .append(setting.name)
                                        .append("</td>");
                                ConfigTable.append("<td>")
                                        .append(setting.message)
                                        .append("</td>");
                                ConfigTable.append("</tr>");
                                ConfigEntryIndex +=1;
                            }
                        }
                    }
                }
            }
            ConfigTable.append("</table>");
            writer.close();
        } catch (IOException e){
            PackCompanion.LOGGER.error("Error while trying to process config json", e);
        }


        PackCompanion.LOGGER.info("Finished processing config json");
    }

    private static boolean checkConfigMatch(ConfigSetting setting) {
        try {
            Object currentValue = getConfigObject(setting.field);

            if (setting.type.startsWith("list")) {
                if (setting.value.equals("[]")) {
                    if (currentValue instanceof List) return ((List<?>) currentValue).isEmpty();
                    if (currentValue.getClass().isArray()) return java.lang.reflect.Array.getLength(currentValue) == 0;
                }else if (setting.value.startsWith("Contains:")) {
                    String jsonPart = setting.value.substring("Contains:".length()).trim();

                    String subTypeName = setting.type.substring("list_".length()).trim();
                    Class<?> elementClass = getElementClass(subTypeName);

                    Type listType = new ListTypeToken(elementClass);
                    List<?> checkList = GSON.fromJson(jsonPart, listType);

                    if (currentValue instanceof List) {
                        List<?> actualList = (List<?>) currentValue;
                        return actualList.containsAll(checkList);
                    }
                    return false;
                }
            }
            Object expectedValue = GSON.fromJson(setting.value, currentValue.getClass());
            return Objects.equals(currentValue, expectedValue);
        } catch (Exception e) {
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
            case "int":     return int.class;
            case "int_p":   return Integer.class;
            case "bool":    return boolean.class;
            case "bool_p":  return Boolean.class;
            case "double":  return double.class;
            case "double_p":return Double.class;
            case "float":   return float.class;
            case "float_p": return Float.class;
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
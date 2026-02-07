package com.barebonium.packcompanion.utils;

import com.barebonium.packcompanion.entries.ModDependency;

import java.util.List;

public class ConfigSetting {
    public String name;
    public String field;
    public String type;
    public String value;
    public Boolean shouldMatch;
    public String message;
    public List<ModDependency> dependencies;
}

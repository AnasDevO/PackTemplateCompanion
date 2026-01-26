package com.barebonium.packcompanion.utils;

import com.barebonium.packcompanion.enumstates.Status;

public class HTMLEntry {
    public String modName;
    public Status status;
    public String actualVersion;
    public String replacementModName;
    public String replacementModLink;

    public HTMLEntry(String modName, Status status, String actualVersion, String replacementModName, String replacementModLink) {
        this.modName = modName;
        this.status = status;
        this.actualVersion = actualVersion;
        this.replacementModName = replacementModName;
        this.replacementModLink = replacementModLink;
    }
}

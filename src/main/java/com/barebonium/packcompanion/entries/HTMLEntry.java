package com.barebonium.packcompanion.entries;

import com.barebonium.packcompanion.enumstates.Status;
import com.barebonium.packcompanion.enumstates.Action;

import java.util.List;

public class HTMLEntry {
    public String modName;
    public Status status;
    public String actualVersion;
    public String replacementModName;
    public String replacementModVersion;
    public String replacementModLink;
    public Action action;
    public boolean isMinVersion;
    public boolean isMaxVersion;
    public List<ModPatchEntry> patchList;
    public String message;

    public HTMLEntry(String modName,
                     Status status,
                     String actualVersion,
                     String replacementModName,
                     String replacementModLink,
                     Action action,
                     boolean isMinVersion,
                     boolean isMaxVersion,
                     String replacementModVersion,
                     List<ModPatchEntry> patchList,
                     String message) {
        this.modName = modName;
        this.status = status;
        this.actualVersion = actualVersion;
        this.replacementModName = replacementModName;
        this.replacementModLink = replacementModLink;
        this.action = action;
        this.isMinVersion = isMinVersion;
        this.isMaxVersion = isMaxVersion;
        this.replacementModVersion = replacementModVersion;
        this.patchList = patchList;
        this.message = message;
    }
}

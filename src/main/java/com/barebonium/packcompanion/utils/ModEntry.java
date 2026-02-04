package com.barebonium.packcompanion.utils;

import com.barebonium.packcompanion.enumstates.Action;
import com.barebonium.packcompanion.enumstates.Status;


import javax.annotation.Nullable;
import java.util.List;

public class ModEntry {
    /** The mod id of this mod entry. */
    public String modId;

    /** The mod version string. */
    public @Nullable String version;

    /** Whether this version is the earliest version accepted. */
    public boolean isMinVersion = false;

    /** Whether this mod version is the oldest version accepted. */
    public boolean isMaxVersion = false;

    /**
     * Whether the version check should be inclusive or exclusive.
     * <p>
     *     Inclusive check will return true if the mod version is within the version range.
     *     Exclusive check will return true if the mod version is <b>not</b> within the version range.
     * <p>
     *     This is useful to find any ranges outside preset limits, such as any Thaumic Wonders version
     *     that is not Thaumic Wonders Unofficial (starts at 2.0.0).
     * <pre>
     * {
     *     "modId": "thaumicwonders",
     *     "version": "2.0.0",
     *     "isMinVersion": true,
     *     "isMaxVersion": false,
     *     "isVersionInclusive": false
     * }
     * </pre>
     * <p>
     *     The logic feels a little backwards, but it's focused on HTML generation and not mod integration.
     */
    public boolean isVersionInclusive = true;

    /** The status of any mod that is not found in the specified version range.
     * <br>
     * Values include:
     * <li> DEPRECATED - used when a mod is forked or superseded and should be replaced </li>
     * <li> PROBLEMATIC - used when a mod has major bugs and should be removed or downgraded </li>
     */
    public Status status;

    /**
     * The recommended action to fix this issue.
     * <li>REMOVE - Remove the mod without replacement </li>
     * <li>DOWNGRADE - Roll back to a previous version of the mod </li>
     * <li>UPGRADE - Upgrade the mod to a new version </li>
     * <li>REPLACE - Replace the mod with a different mod </li>
     * <li>INCLUDE - Include an additional mod (such as patch mods) </li>
     */
    public Action action = Action.REMOVE;

    /** The name of the replacement mod, if applicable. Used to generate hyperlink text. */
    public @Nullable String replacementModName;

    /** The hyperlink to the replacement mod, if applicable. This should be the full hyperlink. */
    public @Nullable String replacementModLink;

    /** The replacement mod version. Should always be used with {Action.UPGRADE} or {Action.DOWNGRADE}. */
    public @Nullable String replacementModVersion;

    /** The message that will display in the output file. Usually the reason the mod is on this list. */
    public String message;

    /** A list of mods used that patch or fix the main mod. ThaumcraftFix is an example of this. */
    public @Nullable List<ModPatchEntry> patchList;
}

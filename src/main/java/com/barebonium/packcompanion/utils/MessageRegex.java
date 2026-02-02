package com.barebonium.packcompanion.utils;

import java.util.regex.Pattern;

public class MessageRegex {
    private static final Pattern LINK_PATTERN = Pattern.compile("#link:\\[([^]]+)]\\(([^)]+)\\)");

    public static String translateToMarkdown(String message) {
        if (message == null) return "-";

        return LINK_PATTERN.matcher(message).replaceAll("[$1]($2)");
    }


    public static String translateToHTML(String message) {
        if (message == null) return "-";

        return LINK_PATTERN.matcher(message).replaceAll("<a href=\"$2\" target=\"_blank\">$1</a>");
    }
}

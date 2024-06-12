package com.lhr.manager.util;

/**
 * @description:
 * @author: LHR
 * @date: 2024-04-30 01:57
 **/
public class FileCalc {

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final long TB = GB * 1024;

    public static String convert(long bytes) {
        if (bytes < KB) {
            return bytes + " B";
        } else if (bytes < MB) {
            return bytes / KB + " KB";
        } else if (bytes < GB) {
            return bytes / MB + " MB";
        } else if (bytes < TB) {
            return bytes / GB + " GB";
        } else {
            return bytes / TB + " TB";
        }
    }

}

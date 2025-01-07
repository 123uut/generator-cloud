package com.lyh.maker.generator.utils;

import java.io.File;

public class FilePathUtil {

    public static String normalizePath(String path) {
        if (path == null) {
            return null;
        }

        // 获取当前操作系统的文件分隔符
        String separator = File.separator;

        // 1. 将所有反斜杠（\）替换为正斜杠（/）
        path = path.replace("\\", "/");

        // 2. 将所有正斜杠（/）替换为当前操作系统的分隔符
        path = path.replace("/", separator);

        // 3. 去除路径中连续的重复分隔符（// 或 \\），保证路径规范
        while (path.contains(separator + separator)) {
            path = path.replace(separator + separator, separator);
        }

        // 4. 确保路径的开始部分不会多出分隔符
        if (path.startsWith(separator + separator)) {
            path = path.substring(1); // 去除多余的开头分隔符
        }

        return path;
    }




}

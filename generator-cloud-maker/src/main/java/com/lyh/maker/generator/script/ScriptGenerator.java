package com.lyh.maker.generator.script;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class ScriptGenerator {

    public static void doGenerateScript(String generatedPath,String jarPath){
        StringBuilder sb = new StringBuilder();
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.contains("win")){
            sb.append("@echo off").append("\n");
            sb.append(String.format("java -jar %s %%*",jarPath)).append("\n");
        }else {
            sb.append("#!/bin/bash").append("\n");
            sb.append(String.format("java -jar $s \"$@\"", jarPath)).append("\n");
        }
        String script = sb.toString();
        FileUtil.writeBytes(script.getBytes(StandardCharsets.UTF_8),new File(generatedPath));
        // 如果是linux操作系统，要给生成的脚本文件添加可执行权限
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(generatedPath), permissions);
        } catch (Exception e) {

        }


    }
}

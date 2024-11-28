package com.lyh.cli.subcommand;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "list",description = "查看要生成的所有文件列表",mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {


    @Override
    public void run() {
        // 整个项目的根路径
        String projectPath = System.getProperty("user.dir");

        // 输入路径
        String inputPath = new File(new File(projectPath).getParentFile(), "generator-cloud-demo-projects/acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }
    }
}



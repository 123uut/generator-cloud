package com.lyh.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.lyh.maker.generator.file.DynamicGenerator;
import com.lyh.maker.generator.jar.JarGenerator;
import com.lyh.maker.generator.script.ScriptGenerator;
import com.lyh.maker.model.meta.MetaInfo;
import com.lyh.maker.model.meta.MetaInfoManager;
import freemarker.template.TemplateException;


import java.io.File;
import java.io.IOException;


public class MainGenerator {

    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        MetaInfo meta = MetaInfoManager.getSingleton();
        System.out.println(meta);

        // 输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + meta.getFileConfig().getOutputRootPath() + File.separator + meta.getProjectName();
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 读取 resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // Java 包基础路径  com.lyh -> com/lyh
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // model.DataModel
        inputFilePath = inputResourcePath  + "template/java/model/GenericDataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/GenericDataModel.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/subcommand/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/subcommand/ConfigCommand.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/subcommand/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/subcommand/GenerateCommand.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/subcommand/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/subcommand/ListCommand.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // cli.CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "template/java/cli/executor/CommandLineExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/executor/CommandLineExecutor.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // Main
        inputFilePath = inputResourcePath + File.separator + "template/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/Main.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicGenerator.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // generator.StaticGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // 生成pom文件，没有就没法打jar包
        inputFilePath = inputResourcePath + File.separator + "template/pom.xml.ftl";
        outputFilePath = outputPath + "/pom.xml";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        //生成的项目打jar包
        JarGenerator.doGenerateJar(outputPath);

        //生成脚本
        String jarPath ="target/"+String.format("%s-%s-jar-with-dependencies.jar",meta.getProjectName(),meta.getVersion());
        String shellPath = outputPath+"/generate.bat";
        ScriptGenerator.doGenerateScript(shellPath,jarPath);
    }


}

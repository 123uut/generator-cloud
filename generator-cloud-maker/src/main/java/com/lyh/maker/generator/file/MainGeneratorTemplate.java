package com.lyh.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.lyh.maker.generator.jar.JarGenerator;
import com.lyh.maker.generator.script.ScriptGenerator;
import com.lyh.maker.model.meta.MetaInfo;
import com.lyh.maker.model.meta.MetaInfoManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 模板方法模式控制生成流程
 */
public abstract class MainGeneratorTemplate {


    public void mainGenerate() throws TemplateException, IOException, InterruptedException{
        MetaInfo meta = MetaInfoManager.getSingleton();
        System.out.println(meta);

        // 输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + meta.getFileConfig().getOutputRootPath() + File.separator + meta.getProjectName();
        String jarPath ="target/"+String.format("%s-%s-jar-with-dependencies.jar",meta.getProjectName(),meta.getVersion());
        String osName = System.getProperty("os.name").toLowerCase();
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        //生成所有动静文件
        String readmdPath = generateSourceFile(meta, outputPath);

        //生成的项目打jar包
        buildJar(outputPath);


        String shellPath = generateScript(outputPath, jarPath, osName, meta);

        String absolGeneratedOutputPath = copyTemplateProject(meta, outputPath);

        generateSimpleVersion(outputPath, readmdPath, jarPath, osName, shellPath, absolGeneratedOutputPath);
    }

    protected  void generateSimpleVersion(String outputPath, String readmdPath, String jarPath, String osName, String shellPath, String absolGeneratedOutputPath) {
        //生成精简版生成器项目代码
        String simpleOutputPath = outputPath + "-simple";
        //复制jar包、脚本文件、README、.source/源文件
        String generatedJarPath =  outputPath + File.separator+ jarPath;
        String targetJarPath = simpleOutputPath+File.separator+"target/";
        if(!FileUtil.exist(targetJarPath)){
            FileUtil.mkdir(targetJarPath);
        }
        FileUtil.copy(generatedJarPath,targetJarPath,true);
        if(osName.contains("win")){
            FileUtil.copy(shellPath,simpleOutputPath+"/generate.bat",true);
        }else {
            FileUtil.copy(shellPath,simpleOutputPath+"/generate",true);
        }
        FileUtil.copy(readmdPath,simpleOutputPath+"/README.md",true);
        FileUtil.copy(absolGeneratedOutputPath,simpleOutputPath,true);
    }

    protected  String copyTemplateProject(MetaInfo meta, String outputPath) {
        //把用户的原始模板代码文件copy到输出的代码生成器项目中
        String absolGeneratedOutputPath = outputPath +  File.separator + meta.getFileConfig().getGeneratedTemplatePath();
        FileUtil.copy(meta.getFileConfig().getInputRootPath(),absolGeneratedOutputPath,false);
        return absolGeneratedOutputPath;
    }


    protected  String generateScript(String outputPath, String jarPath, String osName, MetaInfo metaInfo){
        //生成脚本
        String shellPath;
        if(osName.contains("win")){
            shellPath = outputPath+"/generate.bat";
        }else {
            shellPath = outputPath+"/generate";
        }
        ScriptGenerator.doGenerateScript(shellPath,jarPath);
        return shellPath;
    }
    protected  void buildJar(String outputPath) throws IOException, InterruptedException {
        JarGenerator.doGenerateJar(outputPath);
    }

    protected  String generateSourceFile(MetaInfo meta, String outputPath) throws IOException, TemplateException {


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

        // generator.MainGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
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



        // generator.StaticGenerator
        inputFilePath = inputResourcePath + File.separator + "template/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // 生成pom文件，没有就没法打jar包
        inputFilePath = inputResourcePath + File.separator + "template/pom.xml.ftl";
        outputFilePath = outputPath + "/pom.xml";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);

        // 生成gitignore文件
        inputFilePath = inputResourcePath + File.separator + "template/.gitignore.ftl";
        outputFilePath = outputPath+ "/.gitignore";
        DynamicGenerator.doGenerate(inputFilePath,outputFilePath,meta);

        //生成read.md文件
        inputFilePath = inputResourcePath + File.separator + "template/README.md.ftl";
        outputFilePath = outputPath + "/README.md";
        DynamicGenerator.doGenerate(inputFilePath , outputFilePath, meta);
        return outputFilePath;
    }
}

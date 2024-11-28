package com.lyh.generator;

import com.lyh.model.AcmTemplateDataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 核心生成器
 */
public class MainGenerator {

    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws TemplateException, IOException {
        String inputRootPath = "D:\\javaproject\\generator-cloud\\generator-cloud-demo-projects\\acm-template-pro";
        String outputRootPath = "D:\\javaproject\\generator-cloud\\acm-template-pro";

        String inputPath;
        String outputPath;

        inputPath = new File(inputRootPath, "src/com/lyh/acm/AcmTemplate.java.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath, "src/com/lyh/acm/Main.java").getAbsolutePath();
        DynamicGenerator.doGenerate(inputPath, outputPath, model);

        inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
        outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();
        StaticGenerator.generateFilesByHutool(inputPath, outputPath,true);

        inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
        outputPath = new File(outputRootPath, "README.md").getAbsolutePath();
        StaticGenerator.generateFilesByHutool(inputPath, outputPath,true);
    }

    public static void main(String[] args) throws TemplateException, IOException {
        AcmTemplateDataModel mainTemplateConfig = new AcmTemplateDataModel();
        mainTemplateConfig.setAuthor("yupi");
        mainTemplateConfig.setIfLoop(false);
        mainTemplateConfig.setPrintText("求和结果：");
        doGenerate(mainTemplateConfig);
    }

}


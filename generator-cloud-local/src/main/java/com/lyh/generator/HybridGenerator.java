package com.lyh.generator;

import com.lyh.model.AcmTemplateDataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class HybridGenerator {

    //项目根目录
    private static final String rootPath = System.getProperty("user.dir");

    //拼接得到要生成的demo目录

    private static final String demoPath = new File(rootPath).getParent() + File.separator + "generator-cloud-demo-projects"+ File.separator + "acm-template";

    //拼接得到要生成的demo目录
    private static final String templatePath = rootPath + File.separator + "src/main/resources/template/AcmTemplate.java.ftl";


    public static void main(String[] args) {

        // 设置填充数据模型
        AcmTemplateDataModel acmTemplateDataModel = new AcmTemplateDataModel();
        acmTemplateDataModel.setAuthor("6李易昊6");
        acmTemplateDataModel.setPrintText("计算总和为:");
        acmTemplateDataModel.setIfLoop(true);

        try {
            doGenerator(demoPath,templatePath,rootPath,acmTemplateDataModel);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 动静结合生成模板
     * @param demoPath
     * @param templatePath
     * @param outputPath
     * @param dataModel
     */
    public static void doGenerator(String demoPath, String templatePath, String outputPath, Object dataModel) throws TemplateException, IOException {

        outputPath = new File(outputPath).getParent();
        //文件先全部拷贝
        File targetFile = StaticGenerator.generateFilesByHutool(demoPath, outputPath, true);

        String targetFilePath = targetFile.getAbsolutePath() + "/acm-template/"+"src/com/lyh/acm/Main.java";

        //动态文件单独生成并覆盖
        DynamicGenerator.doGenerate(templatePath,targetFilePath, dataModel);

    }



    public static void doGenerator(Object dataModel) throws TemplateException, IOException {

        doGenerator(demoPath,templatePath,rootPath,dataModel);
    }

}

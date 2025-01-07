package com.lyh.generator;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DynamicGenerator {

    public static void doGenerate(String templatePath, String targetPath, Object dataModel) throws IOException, TemplateException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        // 指定模板文件所在的路径
        File templateFile = new File(templatePath);
        String parent = templateFile.getParent();
        configuration.setDirectoryForTemplateLoading(new File(parent));

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 创建模板对象，加载指定模板
        Template template = configuration.getTemplate(templateFile.getName());

        //如果targetPath不存在则创建
        if(!FileUtil.exist(targetPath)){
            FileUtil.touch(targetPath);
        }

        //指定生成的文件路径和名称
        Writer out = new FileWriter(targetPath);

        //调用freemarker生成填充的文件
        template.process(dataModel,out);

        out.close();
    }


}

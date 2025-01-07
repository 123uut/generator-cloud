package com.lyh.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.lyh.maker.generator.file.DynamicGenerator;
import com.lyh.maker.generator.file.MainGeneratorTemplate;
import com.lyh.maker.generator.jar.JarGenerator;
import com.lyh.maker.generator.script.ScriptGenerator;
import com.lyh.maker.model.meta.MetaInfo;
import com.lyh.maker.model.meta.MetaInfoManager;
import freemarker.template.TemplateException;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


public class MainGenerator extends MainGeneratorTemplate {

    @Override
    protected void generateSimpleVersion(String outputPath, String readmdPath, String jarPath, String osName, String shellPath, String absolGeneratedOutputPath) {
        System.out.println("不生成简化版的项目啦--");
    }
}

package com.lyh.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.lyh.maker.filter.config.FileFilterConfig;
import com.lyh.maker.filter.config.TemplateMakerFileConfig;
import com.lyh.maker.filter.config.TemplateMakerModelConfig;
import com.lyh.maker.filter.enums.FileFilterRangeEnum;
import com.lyh.maker.filter.enums.FileFilterRuleEnum;
import com.lyh.maker.model.TemplateMakerParam;
import com.lyh.maker.model.meta.MetaInfo;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TemplateMakerTest {

    @Test
    public void testMakeTemplate() {
        MetaInfo meta = new MetaInfo();
        meta.setProjectName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
//        String originProjectPath = new File(projectPath).getParent() + File.separator + "generator-cloud-demo-projects/acm-template";
//        String inputFilePath = "src/com/lyh/acm/Main.java";

        String originProjectPath = new File(projectPath).getParent() + File.separator + "generator-cloud-demo-projects/springboot-init";
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
//        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/constant";
        String inputFilePath2 = "src/main/resources/application.yml";



        // 模型参数信息（首次）
//        MetaInfo.ModelConfig.CommandArgModel modelInfo = new MetaInfo.ModelConfig.CommandArgModel();
//        modelInfo.setFieldName("printText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum = ");

        // 模型参数信息（第二次）
//        MetaInfo.ModelConfig.CommandArgModel modelInfo = new MetaInfo.ModelConfig.CommandArgModel();
//        modelInfo.setFieldName("className");
//        modelInfo.setType("String");
//
//        // 替换变量（首次）
////        String searchStr = "Sum: ";
//        // 替换变量（第二次）
//        String searchStr = "BaseResponse";
        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // - 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        // - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplacedText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplacedText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);



        // 文件过滤
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        //1.common包下的只处理文件名称中包含Base的类
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .type(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        FileFilterConfig fileFilterConfig2 = FileFilterConfig.builder()
                .type(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.ENDS_WITH.getValue())
                .value("java")
                .build();
        fileFilterConfigList.add(fileFilterConfig);
        fileFilterConfigList.add((fileFilterConfig2));
        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);
        //2.config包下的处理所有（不设置过滤条件）
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1, fileInfoConfig2));

        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();

        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("测试分组");
        fileGroupConfig.setCondition("printText");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

//        long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig,templateMakerModelConfig, 1735281524670181376l);
//        System.out.println(id);
    }


    /**
     * 同文件目录多次生成时，会扫描新的 FTL 文件
     */
    @Test
    public void testMakeTemplateBug2() {
        MetaInfo meta = new MetaInfo();
        meta.setProjectName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "generator-cloud-demo-projects/springboot-init";

        // 文件参数配置，扫描目录
        String inputFilePath1 = "./";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplacedText("BaseResponse");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

//        long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, 1735281524670181376L);
//        System.out.println(id);
    }

    @Test
    public void test2(){
        String paramStr = ResourceUtil.readUtf8Str("templateMakerParam.json");
        TemplateMakerParam templateMakerParam = JSONUtil.toBean(paramStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerParam);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板-步骤1-空文件
     */
    @Test
    public void makeSpringBootTemplate1() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板-步骤2 替换包名
     */
    @Test
    public void makeSpringBootTemplate2() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker1.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板-步骤3 开题帖子功能
     */
    @Test
    public void makeSpringBootTemplate3() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker2.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板-步骤4 开启跨域
     */
    @Test
    public void makeSpringBootTemplate4() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker3.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板-步骤5 开启knile4j,先整一个Knife4jConfig.java.ftl模板 (也可以直接执行第6步)
     */
    @Test
    public void makeSpringBootTemplate5() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker4.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板-步骤6 对Knife4jConfig.java.ftl模板挖坑（自定义knife4j配置类）
     */
    @Test
    public void makeSpringBootTemplate6() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker5.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板-步骤7 对application.yml.ftl模板挖坑（自定义数据库配置类）
     */
    @Test
    public void makeSpringBootTemplate7() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker6.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }
    /**
     * 制作 SpringBoot 模板-步骤8 是否开启redis
     */
    @Test
    public void makeSpringBootTemplate8() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker7.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板-步骤8 是否开启ES
     */
    @Test
    public void makeSpringBootTemplate9() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "template-maker8.json");
        TemplateMakerParam templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerParam.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }




}

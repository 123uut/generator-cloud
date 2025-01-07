package com.lyh.maker.generator.validator;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lyh.maker.generator.enums.GeneratedFileType;
import com.lyh.maker.generator.enums.MetaFileType;
import com.lyh.maker.generator.exception.MetaArgInvalidException;
import com.lyh.maker.model.meta.MetaInfo;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元信息参数校验器
 */
public class MetaArgValidator {

    public static void doValidAndFill(MetaInfo meta) {
        validateBasicInfo(meta);

        validateFileInfo(meta);

        validateModelInfo(meta);

    }

    private static void validateModelInfo(MetaInfo meta) {
        // modelConfig 校验和默认值
        MetaInfo.ModelConfig modelConfig = meta.getModelConfig();
        if(modelConfig == null){
            return;
        }


        List<MetaInfo.ModelConfig.CommandArgModel> modelInfoList = modelConfig.getModels();
        if (CollectionUtil.isNotEmpty(modelInfoList)) {
            for (MetaInfo.ModelConfig.CommandArgModel modelInfo : modelInfoList) {

                if(StrUtil.isNotEmpty(modelInfo.getGroupKey())){
                    //如果是分组的参数拼接命令执行参数传给模板,如"--author --printText"
                    String collect = modelInfo.getModelConfigList().stream().map(item -> {
                        return "\"--"+item.getFieldName()+"\"";
                    }).collect(Collectors.joining(","));
                    modelInfo.setTriggeredCommand(collect);

                    continue;
                }

                // 输出路径默认值
                String fieldName = modelInfo.getFieldName();
                if (StrUtil.isBlank(fieldName)) {
                    throw new MetaArgInvalidException("未填写 fieldName");
                }

                String modelInfoType = modelInfo.getType();
                if (StrUtil.isEmpty(modelInfoType)) {
                    modelInfo.setType("String");
                }
            }
        }

    }

    private static void validateFileInfo(MetaInfo meta) {
        // fileConfig 校验和默认值
        MetaInfo.FileConfig fileConfig = meta.getFileConfig();
        if(fileConfig == null){
            return;
        }
        // sourceRootPath：必填
        String sourceRootPath = fileConfig.getInputRootPath();
        if (StrUtil.isBlank(sourceRootPath)) {
            throw new MetaArgInvalidException("未填写 sourceRootPath");
        }
        // generatedTemplatePath：输入模板项目相对outputRootPath的下一级存放路径（如：generated/name/.source）
        String generatedTemplatePath = fileConfig.getGeneratedTemplatePath();
        String defaultGeneratedTemplatePath = ".source";
        if (StrUtil.isEmpty(generatedTemplatePath)) {
            fileConfig.setGeneratedTemplatePath(defaultGeneratedTemplatePath);
        }
        // outputRootPath：默认为当前路径下的 generated
        String outputRootPath = fileConfig.getOutputRootPath();
        String defaultOutputRootPath = "generated";
        if (StrUtil.isEmpty(outputRootPath)) {
            fileConfig.setOutputRootPath(defaultOutputRootPath);
        }
        String fileConfigType = fileConfig.getType();
        String defaultType = MetaFileType.DIR.getValue();
        if (StrUtil.isEmpty(fileConfigType)) {
            fileConfig.setType(defaultType);
        }

        // fileInfo 默认值
        List<MetaInfo.FileConfig.MetaFileInfo> fileInfoList = fileConfig.getFiles();
        if (CollectionUtil.isNotEmpty(fileInfoList)) {
            for (MetaInfo.FileConfig.MetaFileInfo fileInfo : fileInfoList) {
                if(MetaFileType.GROUP.getValue().equals(fileInfo.getType())){
                    //如果是文件分组，则不用填写xxpath。。。
                    continue;
                }

                // inputPath: 必填
                String inputPath = fileInfo.getInputPath();
                if (StrUtil.isBlank(inputPath)) {
                    throw new MetaArgInvalidException("未填写文件的inputPath");
                }

                // outputPath: 默认等于 inputPath
                String outputPath = fileInfo.getOutputPath();
                if (StrUtil.isEmpty(outputPath)) {
                    fileInfo.setOutputPath(inputPath);
                }
                // type：默认 inputPath 有文件后缀（如 .java）为 file，否则为 dir
                String type = fileInfo.getType();
                if (StrUtil.isBlank(type)) {
                    // 无文件后缀
                    if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))) {
                        fileInfo.setType(MetaFileType.DIR.getValue());
                    } else {
                        fileInfo.setType(MetaFileType.FILE.getValue());
                    }
                }

                // generateType：如果文件结尾不为 Ftl，generateType 默认为 static，否则为 dynamic
                String generateType = fileInfo.getGenerateType();
                if (StrUtil.isBlank(generateType)) {
                    // 为动态模板
                    if (inputPath.endsWith(".ftl")) {
                        fileInfo.setGenerateType(GeneratedFileType.DYNAMIC.getValue());
                    } else {
                        fileInfo.setGenerateType(GeneratedFileType.STATIC.getValue());
                    }
                }
            }
        }

    }

    private static void validateBasicInfo(MetaInfo meta) {
        // 基础信息校验和默认值
        String toDefault;
        String name = meta.getProjectName();
        toDefault = StrUtil.blankToDefault(name, "my-generator");
        meta.setProjectName(toDefault);

        String description = meta.getDescription();
        toDefault = StrUtil.blankToDefault(description, "我的模板代码生成器");
        meta.setDescription(toDefault);

        String author = meta.getAuthor();
        toDefault = StrUtil.blankToDefault(author, "lyh");
        meta.setAuthor(toDefault);

        String basePackage = meta.getBasePackage();
        toDefault = StrUtil.blankToDefault(basePackage, "com.lyh");
        meta.setBasePackage(toDefault);

        String version = meta.getVersion();
        toDefault = StrUtil.blankToDefault(version, "1.0");
        meta.setVersion(toDefault);

        String createTime = meta.getCreateTime();
        toDefault = StrUtil.blankToDefault(createTime, DateUtil.now());
        meta.setCreateTime(toDefault);

    }

}


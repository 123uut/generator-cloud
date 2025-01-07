package com.lyh.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lyh.maker.filter.FileFilter;
import com.lyh.maker.filter.config.FileFilterConfig;
import com.lyh.maker.filter.config.TemplateMakerFileConfig;
import com.lyh.maker.filter.config.TemplateMakerModelConfig;
import com.lyh.maker.filter.config.TemplateMakerOutputConfig;
import com.lyh.maker.filter.enums.FileFilterRangeEnum;
import com.lyh.maker.filter.enums.FileFilterRuleEnum;
import com.lyh.maker.generator.utils.FilePathUtil;
import com.lyh.maker.model.TemplateMakerParam;
import com.lyh.maker.model.meta.MetaInfo;
import com.lyh.maker.generator.enums.GeneratedFileType;
import com.lyh.maker.generator.enums.MetaFileType;
import com.lyh.maker.util.TemplateMakerUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateMaker {


    public static long makeTemplate(TemplateMakerParam templateMakerParam) {
        Long id = templateMakerParam.getId();
        MetaInfo meta = templateMakerParam.getMeta();
        String originProjectPath = templateMakerParam.getOriginProjectPath();
        TemplateMakerFileConfig fileConfig = templateMakerParam.getFileConfig();
        TemplateMakerModelConfig modelConfig = templateMakerParam.getModelConfig();
        TemplateMakerOutputConfig outputConfig = templateMakerParam.getOutputConfig();
        long generatedId = makeTemplate(meta, originProjectPath, fileConfig, modelConfig, outputConfig, id);
        return generatedId;
    }

        /**
         * 制作模板:将原项目中的.java文件根据配置信息进行挖坑，得到.java.ftl模板文件，同时生成meta.json配置文件。
         * 所有生成的文件放在./tempate/{全局唯一id}/{原模板项目名称}下。
         * 没调用一次该方法，在ftl文件里面挖多个坑
         * @param newMeta
         * @param originProjectPath
         * @param templateMakerFileConfig
         * @param templateMakerModelConfig 用于控制给文件挖坑，坑的参数是什么
         * @param id
         * @return
         */
    public static long makeTemplate(MetaInfo newMeta,
                                    String originProjectPath,
                                    TemplateMakerFileConfig templateMakerFileConfig,
                                    TemplateMakerModelConfig templateMakerModelConfig,
                                    TemplateMakerOutputConfig outputConfig,
                                    Long id) {
        // 第一次制作，没有 id 则生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        // 是否为首次制作模板
        if (!FileUtil.exist(templatePath)) {
            // 目录不存在，则是首次制作
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }
        //非首次制作，拿到.temp/id/下面的第一个项目的路径
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();


        /// 一、输入信息
        // 输入文件信息
//        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();

        //挖坑，制作模板文件
        List<MetaInfo.FileConfig.MetaFileInfo> newFileInfoList = makeTemplateFiles(templateMakerFileConfig, templateMakerModelConfig, outputConfig, sourceRootPath);

        List<MetaInfo.ModelConfig.CommandArgModel> newModelInfoList = makeModelInfoList(templateMakerModelConfig);

        // 三、生成配置文件
        String metaOutputPath = templatePath + File.separator + "meta.json";

        // 如果已有 meta 文件，说明不是第一次制作，则在 meta 基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            MetaInfo oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), MetaInfo.class);
            //这里一定要注意，拷贝bean的时候设置不空null才覆盖的策略
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            // 1. 追加配置参数
            List<MetaInfo.FileConfig.MetaFileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<MetaInfo.ModelConfig.CommandArgModel> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            // 配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        } else {
            // 1. 构造配置参数
            MetaInfo.FileConfig fileConfig = new MetaInfo.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setInputRootPath(sourceRootPath);
            List<MetaInfo.FileConfig.MetaFileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            MetaInfo.ModelConfig modelConfig = new MetaInfo.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<MetaInfo.ModelConfig.CommandArgModel> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);
        }

        if(outputConfig != null){
            //用户开启组内外文件去重
            if(outputConfig.isIfRemoveGroupFilesFromRoot()){
                List<MetaInfo.FileConfig.MetaFileInfo> metaFileInfos = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(metaFileInfos));
            }
        }

        // 2. 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        return id;
    }

    private static List<MetaInfo.ModelConfig.CommandArgModel> makeModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {

        // 本次新增的模型配置列表
        List<MetaInfo.ModelConfig.CommandArgModel> newModelInfoList = new ArrayList<>();
        if (templateMakerModelConfig == null) {
            return newModelInfoList;
        }

        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if (CollUtil.isEmpty(models)) {
            return newModelInfoList;
        }

        // 处理模型配置信息
        // - 转换为配置接受的 ModelInfo 对象
        List<MetaInfo.ModelConfig.CommandArgModel> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            MetaInfo.ModelConfig.CommandArgModel modelInfo = new MetaInfo.ModelConfig.CommandArgModel();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());

        // - 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {

            MetaInfo.ModelConfig.CommandArgModel groupModelInfo = new MetaInfo.ModelConfig.CommandArgModel();
            BeanUtil.copyProperties(modelGroupConfig,groupModelInfo);

            // 模型全放到一个分组内
            groupModelInfo.setModelConfigList(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        } else {
            // 不分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /**
     * 批量制作模板文件
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @return
     */
    private static List<MetaInfo.FileConfig.MetaFileInfo> makeTemplateFiles(TemplateMakerFileConfig templateMakerFileConfig,
                                                                            TemplateMakerModelConfig templateMakerModelConfig,
                                                                            TemplateMakerOutputConfig templateMakerOutputConfig,
                                                                            String sourceRootPath) {
        List<MetaInfo.FileConfig.MetaFileInfo> newFileInfoList = new ArrayList<>();
        // 非空校验
        if (templateMakerFileConfig == null) {
            return newFileInfoList;
        }

        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        if (CollUtil.isEmpty(fileConfigInfoList)) {
            return newFileInfoList;
        }

        // 二、生成文件模板
        // 遍历输入文件
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();
            // 如果填的是相对路径，要改为绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)) {
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            // 获取过滤后的文件列表（不会存在目录）
            List<File> fileList = FileFilter.doFilter(inputFilePath, fileInfoConfig.getFilterConfigList());
            //过滤掉.ftl文件
            fileList = fileList.stream().filter(file -> !FileUtil.pathEndsWith(file,"ftl"))
                    .collect(Collectors.toList());
            for (File file : fileList) {
                MetaInfo.FileConfig.MetaFileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        }

        //因为我们是按照条件对各个目录的文件进行了过滤，我们希望一个功能下的所有file在配置文件中为一个组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        //有分组就按分组整合
        if(ObjectUtil.isNotEmpty(fileGroupConfig)){
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();
            MetaInfo.FileConfig.MetaFileInfo groupFileConfig = new MetaInfo.FileConfig.MetaFileInfo();
            groupFileConfig.setType(MetaFileType.GROUP.getValue());
            groupFileConfig.setCondition(condition);
            groupFileConfig.setGroupKey(groupKey);
            groupFileConfig.setGroupName(groupName);
            groupFileConfig.setFileInfos(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileConfig);
        }

        return newFileInfoList;
    }


    /**
     * 文件去重
     *
     * @param fileInfoList
     * @return
     */
    private static List<MetaInfo.FileConfig.MetaFileInfo> distinctFiles(List<MetaInfo.FileConfig.MetaFileInfo> fileInfoList) {
        // 策略：同分组内文件 merge，不同分组保留

        // 1. 有分组的，以组为单位划分
        Map<String, List<MetaInfo.FileConfig.MetaFileInfo>> groupKeyFileInfoListMap = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(MetaInfo.FileConfig.MetaFileInfo::getGroupKey)
                );


        // 2. 同组内的文件配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, MetaInfo.FileConfig.MetaFileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<MetaInfo.FileConfig.MetaFileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<MetaInfo.FileConfig.MetaFileInfo> tempFileInfoList = entry.getValue();
            //组内去重
            List<MetaInfo.FileConfig.MetaFileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    // [[1,2],[2,3]] -> [1,2,3]
                    .flatMap(fileInfo -> fileInfo.getFileInfos().stream())
                    .collect(
                            Collectors.toMap(MetaInfo.FileConfig.MetaFileInfo::getOutputPath, o -> o, (e, r) -> r)
                    ).values());

            // 使用新的 group 配置
            MetaInfo.FileConfig.MetaFileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFileInfos(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }

        // 3. 将文件分组添加到结果列表
        List<MetaInfo.FileConfig.MetaFileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        // 4. 将未分组的文件添加到结果列表
        List<MetaInfo.FileConfig.MetaFileInfo> noGroupFileInfoList = fileInfoList.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                //未分组的去重
                .collect(
                        Collectors.toMap(MetaInfo.FileConfig.MetaFileInfo::getOutputPath, o -> o, (e, r) -> r)
                ).values()));
        return resultList;
    }


    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static List<MetaInfo.ModelConfig.CommandArgModel> distinctModels(List<MetaInfo.ModelConfig.CommandArgModel> modelInfoList) {
        // 策略：同分组内模型 merge，不同分组保留

        // 1. 有分组的，以组为单位划分
        Map<String, List<MetaInfo.ModelConfig.CommandArgModel>> groupKeyModelInfoListMap = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(MetaInfo.ModelConfig.CommandArgModel::getGroupKey)
                );


        // 2. 同组内的模型配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, MetaInfo.ModelConfig.CommandArgModel> groupKeyMergedModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<MetaInfo.ModelConfig.CommandArgModel>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<MetaInfo.ModelConfig.CommandArgModel> tempModelInfoList = entry.getValue();
            List<MetaInfo.ModelConfig.CommandArgModel> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModelConfigList().stream())
                    .collect(
                            Collectors.toMap(MetaInfo.ModelConfig.CommandArgModel::getFieldName, o -> o, (e, r) -> r)
                    ).values());

            // 使用新的 group 配置
            MetaInfo.ModelConfig.CommandArgModel newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModelConfigList(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }

        // 3. 将模型分组添加到结果列表
        List<MetaInfo.ModelConfig.CommandArgModel> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

        // 4. 将未分组的模型添加到结果列表
        List<MetaInfo.ModelConfig.CommandArgModel> noGroupModelInfoList = modelInfoList.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
                .collect(
                        Collectors.toMap(MetaInfo.ModelConfig.CommandArgModel::getFieldName, o -> o, (e, r) -> r)
                ).values()));
        return resultList;
    }

    /**
     * 制作单文件FTL文件模板
     *
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static MetaInfo.FileConfig.MetaFileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File inputFile) {
        // 要挖坑的文件绝对路径（用于制作模板）
        // 注意 win 系统需要对路径进行转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath();
        fileInputAbsolutePath = FilePathUtil.normalizePath(fileInputAbsolutePath);
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        sourceRootPath = FilePathUtil.normalizePath(sourceRootPath);
        // 文件输入输出相对路径（用于生成json配置）
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + File.separator, "");
        String fileOutputPath = fileInputPath + ".ftl";

        // 使用字符串替换，生成模板文件
        String fileContent;
        // 如果已有模板文件，说明不是第一次制作，则在模板基础上再次挖坑
        //输入的是.java文件，说明是这个文件第一次挖坑
        boolean existFTL = FileUtil.exist(fileOutputAbsolutePath);
        //输入的是.ftl文件，说明这个文件挖过坑了
//        boolean inputIsFtl = fileInputAbsolutePath.endsWith("ftl") && FileUtil.exist(fileInputAbsolutePath);
        if (existFTL) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        // 支持多个模型：对同一个文件的内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            // 不是分组
            if (modelGroupConfig == null) {
                //如：${mysql}
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                // 是分组
                String groupKey = modelGroupConfig.getGroupKey();
                // 注意挖坑要多一个层级,如：${mysql.url} ${mysql.username}
                replacement = String.format("${%s.%s}", groupKey, modelInfoConfig.getFieldName());
            }
            // 多次替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplacedText(), replacement);
        }

        // 输出模板文件

        // 文件配置信息
        MetaInfo.FileConfig.MetaFileInfo fileInfo = new MetaInfo.FileConfig.MetaFileInfo();
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setType(MetaFileType.FILE.getValue());
        fileInfo.setGenerateType(GeneratedFileType.DYNAMIC.getValue());

        // 和原文件一致，没有挖坑，则为静态生成
        if (newFileContent.equals(fileContent) && !existFTL) {
            // 输出路径 = 输入路径
            fileInfo.setInputPath(fileInputPath);
            fileInfo.setGenerateType(GeneratedFileType.STATIC.getValue());
        } else {
            // 挖了新坑，没挖新坑有ftl文件，都是动态类型的文件
            // 生成模板文件
            fileInfo.setGenerateType(GeneratedFileType.DYNAMIC.getValue());
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return fileInfo;
    }



}



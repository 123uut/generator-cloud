package com.lyh.maker.util;



import cn.hutool.core.util.StrUtil;
import com.lyh.maker.model.meta.MetaInfo;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板制作工具类
 */
public class TemplateMakerUtils {

    /**
     * 从未分组文件中移除组内的同名文件
     *
     * @param fileInfoList
     * @return
     */
    public static List<MetaInfo.FileConfig.MetaFileInfo> removeGroupFilesFromRoot(List<MetaInfo.FileConfig.MetaFileInfo> fileInfoList) {
        // 先获取到所有分组
        List<MetaInfo.FileConfig.MetaFileInfo> groupFileInfoList = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());

        // 获取所有分组内的文件列表
        List<MetaInfo.FileConfig.MetaFileInfo> groupInnerFileInfoList = groupFileInfoList.stream()
                .flatMap(fileInfo -> fileInfo.getFileInfos().stream())
                .collect(Collectors.toList());

        // 获取所有分组内文件输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream()
                .map(MetaInfo.FileConfig.MetaFileInfo::getInputPath)
                .collect(Collectors.toSet());

        // 移除所有名称在 set 中的外层文件
        return fileInfoList.stream()
                .filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
    }
}

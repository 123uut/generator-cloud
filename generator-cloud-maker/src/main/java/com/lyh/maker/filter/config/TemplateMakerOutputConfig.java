package com.lyh.maker.filter.config;

import lombok.Data;

@Data
public class TemplateMakerOutputConfig {

    // 是否从未分组文件中移除组内的同名文件
    private boolean ifRemoveGroupFilesFromRoot = true;
}


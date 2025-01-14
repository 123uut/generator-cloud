package com.lyh.maker.filter.config;

import com.lyh.maker.model.meta.MetaInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    private FileGroupConfig fileGroupConfig;

    @NoArgsConstructor
    @Data
    public static class FileInfoConfig {

        private String path;

        private List<FileFilterConfig> filterConfigList;
    }

    @Data
    public static class FileGroupConfig{
        private String groupKey;

        private String groupName;

        private String condition;
    }
}

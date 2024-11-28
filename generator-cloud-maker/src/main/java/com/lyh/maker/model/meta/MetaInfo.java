package com.lyh.maker.model.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 项目元信息
 */
@NoArgsConstructor
@Data
public class MetaInfo {

    private String projectName;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;

    @NoArgsConstructor
    @Data
    public static class FileConfig {
        private String inputRootPath;
        private String outputRootPath;
        private String type;
        private List<MetaFileInfo> files;

        @NoArgsConstructor
        @Data
        public static class MetaFileInfo {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfig {
        private List<CommandArgModel> models;

        @NoArgsConstructor
        @Data
        public static class CommandArgModel {
            private String fieldName;
            private String type;
            private String description;
            private Object defaultValue;
            private String abbr;
        }
    }
}

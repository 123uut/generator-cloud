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
        /**
         * 模板项目文件在用户终端里的绝对路径
         */
        private String inputRootPath;
        /**
         * 要生成的代码生成器项目的相对路径
         */
        private String outputRootPath;
        /**
         * 生成的代码生成器项目中必须包含用户原始的模板项目文件，
         * 该路径指定原始文件在代码生成器项目文件中的相对路径
         */
        private String generatedTemplatePath;
        /**
         * dir、file、group
         */
        private String type;


        private List<MetaFileInfo> files;

        @NoArgsConstructor
        @Data
        public static class MetaFileInfo {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;

            private String groupKey;

            private String groupName;

            private String condition;

            private List<MetaFileInfo> fileInfos;

            @NoArgsConstructor
            @Data
            public static class FileInfo {

            }
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
            private String groupKey;
            private String groupName;
            private String className;
            private String condition;

            private List<CommandArgModel> modelConfigList;

            private String triggeredCommand;
        }
    }
}

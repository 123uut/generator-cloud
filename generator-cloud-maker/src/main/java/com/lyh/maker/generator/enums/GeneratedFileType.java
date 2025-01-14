package com.lyh.maker.generator.enums;

/**
 * 文件生成类型枚举
 */
public enum GeneratedFileType {

    DYNAMIC("动态", "dynamic"),
    STATIC("静态", "static");

    private final String text;

    private final String value;

    GeneratedFileType(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}




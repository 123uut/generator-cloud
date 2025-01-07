package com.lyh.maker.generator.enums;

public enum MetaFileType {
    FILE("file","文件"),
    DIR("dir","目录"),

    GROUP("group","文件组");

    private String value;

    private String desc;

    MetaFileType(String value,String desc){
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}

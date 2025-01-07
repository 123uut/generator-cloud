package com.lyh.maker.generator;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.mainGenerate();

//        String[] strs = new String[]{"aa","bb","cc"};
//        List<String> strings = new ArrayList<>(Arrays.asList(strs));
//        strings.add("d");

    }
}

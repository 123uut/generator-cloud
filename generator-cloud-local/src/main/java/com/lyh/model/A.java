package com.lyh.model;

import com.lyh.cli.subcommand.AcmDataModel;
import com.lyh.cli.subcommand.GenerateCommand;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class A {

    @SneakyThrows
    public static void main(String[] args) {
        AcmDataModel acmDataModel = new AcmDataModel();
        Class<? extends AcmDataModel> aClass = acmDataModel.getClass();
        Field author1 = aClass.getDeclaredField("author");

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            field.set(acmDataModel,"sadasd");
        }

        System.out.println(acmDataModel);
    }


}

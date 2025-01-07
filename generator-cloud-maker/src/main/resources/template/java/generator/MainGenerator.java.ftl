package ${basePackage}.generator;

import freemarker.template.TemplateException;


import java.io.File;
import java.io.IOException;
import cn.hutool.core.io.FileUtil;
import java.nio.file.Paths;
import com.lyh.model.GenericDataModel;
import com.lyh.generator.DynamicGenerator;
import com.lyh.generator.StaticGenerator;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "static">
${indent}StaticGenerator.generateFilesByHutool(inputPath, outputPath,true);
<#else>
${indent}DynamicGenerator.doGenerate(inputPath, outputPath, modelInfo);
</#if>
</#macro>



/**
 * 核心生成器
 */
public class MainGenerator {

    /**
     * 生成
     *
     * @param modelInfo 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(GenericDataModel modelInfo) throws TemplateException, IOException {
        String projectName = "${projectName}";
        String demoName = FileUtil.getLastPathEle(Paths.get("${fileConfig.inputRootPath}").getFileName()).toString();

        String inputRootPath = "${fileConfig.generatedTemplatePath}" + File.separator + demoName;
        String outputRootPath = "${fileConfig.outputRootPath}";
        inputRootPath = System.getProperty("user.dir") + File.separator+  inputRootPath;

        String inputPath;
        String outputPath;

        //获取模型变量
<#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
        <#list modelInfo.modelConfigList as modelConfig>
        ${modelConfig.type} ${modelConfig.fieldName} = modelInfo.${modelInfo.groupKey}.${modelConfig.fieldName};
        </#list>
    <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = modelInfo.${modelInfo.fieldName};
    </#if>

</#list>

    <#list fileConfig.files as item>
        <#if item.groupKey??>
        //groupKey = ${item.groupKey}
            <#if item.condition??>
        if(${item.condition}){
            <#list item.fileInfos as file>
            <@generateFile fileInfo= file indent="            "/>
            </#list>
        }
            <#else >
            <#list item.fileInfos as file>
                <@generateFile fileInfo= file indent="            "/>
            </#list>
            </#if>
        <#else >
            <#if item.condition??>
        if(${item.condition}){

            <@generateFile fileInfo= item indent="            "/>

        }
            <#else >
    <@generateFile fileInfo= item indent="            "/>
            </#if>
        </#if>

    </#list>
    }
}


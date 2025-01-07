# ${projectName}

> ${description}
>
> 作者：${author}
>
> 基于java大神李易昊的 [云代码生成器项目] 制作，感谢您的使用！

可以通过命令行交互式输入的方式动态生成想要的项目代码

## 使用说明

> 执行项目根目录下的脚本文件：
> 示例命令：
> generate generate <#list modelConfig.models as modelInfo><#if modelInfo.abbr??>-${modelInfo.abbr}</#if> </#list>
> Tips：可以通过命令：generate --help 查看所有支持的命令

## 生成文件可选参数：
<#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    ##参数组：{modelInfo.groupKey} --》<#if modelInfo.description??>${modelInfo.description}</#if>
        <#list modelInfo.modelConfigList as modelConfig>
    ${modelConfig?index + 1}）${modelConfig.fieldName}

    类型：${modelConfig.type}

    描述：${modelConfig.description}

    <#if modelConfig.defaultValue??>
    默认值：${modelConfig.defaultValue?c}
    </#if>
    <#if modelConfig.abbr??>
    缩写： -${modelConfig.abbr}
    </#if>
        </#list>
    ##参数组：${modelInfo.groupKey} --》${modelInfo.description?c}
    <#else>
    ${modelInfo?index + 1}）${modelInfo.fieldName}

    类型：${modelInfo.type}

    描述：${modelInfo.description}

    <#if modelInfo.defaultValue??>
        默认值：${modelInfo.defaultValue?c}
    </#if>

    <#if modelInfo.abbr??>
        缩写： -${modelInfo.abbr}
    </#if>
    </#if>



</#list>





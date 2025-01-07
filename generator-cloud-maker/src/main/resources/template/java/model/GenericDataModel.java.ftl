package ${basePackage}.model;
import lombok.Data;

@Data
public class GenericDataModel {
<#list modelConfig.models as commandArgModel>

<#if commandArgModel.groupKey??>
    public ${commandArgModel.className} ${commandArgModel.groupKey} = new ${commandArgModel.className}();
    <#if commandArgModel.description??>
        /**
        * ${commandArgModel.description}
        */
    </#if>
    @Data
    public static class ${commandArgModel.className}{
        <#list commandArgModel.modelConfigList as item>
        <#if item.description??>
        /**
         * ${item.description}
         */
        </#if>
        public ${item.type} ${item.fieldName}<#if item.defaultValue??> = ${item.defaultValue?c}</#if>;
        </#list>
    }
<#else>
    <#if commandArgModel.description??>
    /**
    * ${commandArgModel.description}
    */
    </#if>
    public ${commandArgModel.type} ${commandArgModel.fieldName}<#if commandArgModel.defaultValue??> = ${commandArgModel.defaultValue?c}</#if>;
</#if>
</#list>

}

package ${basePackage}.model;
import lombok.Data;

@Data
public class GenericDataModel {
<#list modelConfig.models as commandArgModel>

    <#if commandArgModel.description??>
    /**
    * ${commandArgModel.description}
    */
    </#if>
    private ${commandArgModel.type} ${commandArgModel.fieldName}<#if commandArgModel.defaultValue??> = ${commandArgModel.defaultValue?c}</#if>;
</#list>

}

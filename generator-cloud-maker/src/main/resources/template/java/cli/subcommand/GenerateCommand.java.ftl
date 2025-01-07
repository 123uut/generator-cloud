package ${basePackage}.cli.subcommand;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import freemarker.template.TemplateException;
import picocli.CommandLine.Option;
import lombok.SneakyThrows;
import picocli.CommandLine.Command;
import picocli.CommandLine;
import lombok.Data;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import ${basePackage}.model.GenericDataModel;

@Data
@Command(name = "generate",description = "代码生成option", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    <#list modelConfig.models as commandArgModel>
        <#if commandArgModel.groupKey??>
    private static GenericDataModel.${commandArgModel.className} ${commandArgModel.groupKey} = new GenericDataModel.${commandArgModel.className}();
        <#else>
    @Option(names = {<#if commandArgModel.abbr??>"-${commandArgModel.abbr}", </#if>"--${commandArgModel.fieldName}"}, arity = "0..1", <#if commandArgModel.description??>description = "${commandArgModel.description}", </#if>interactive = true, echo = true)
    private ${commandArgModel.type} ${commandArgModel.fieldName}<#if commandArgModel.defaultValue??> = ${commandArgModel.defaultValue?c}</#if>;
        </#if>


    </#list>
    /**
     * 该option命令被执行时的回调
     * @return
     * @throws Exception
     */
    @Override
    public Integer call() throws Exception {
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condition??>
        if(${modelInfo.condition}){
        CommandLine ${modelInfo.groupKey}CommandLine = new CommandLine(${modelInfo.className}Command.class);
            ${modelInfo.groupKey}CommandLine.execute(${modelInfo.triggeredCommand});
        }
        <#else >
        CommandLine ${modelInfo.groupKey}CommandLine = new CommandLine(${modelInfo.className}Command.class);
            ${modelInfo.groupKey}CommandLine.execute(${modelInfo.triggeredCommand});
        </#if>

        </#if>
        </#list>
        GenericDataModel genericDataModel = new GenericDataModel();
        BeanUtil.copyProperties(this,genericDataModel);
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        genericDataModel.set${modelInfo.className}(${modelInfo.groupKey});
        </#if>

    </#list>
        //动静结合生成代码
        try {
            MainGenerator.doGenerate(genericDataModel);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
    @Data
    @CommandLine.Command(name = "${modelInfo.groupKey}")
    public static class ${modelInfo.className}Command implements Runnable{
        <#list modelInfo.modelConfigList as modelConfig>
    @Option(names = {<#if modelConfig.abbr??>"-${modelConfig.abbr}", </#if>"--${modelConfig.fieldName}"}, arity = "0..1", <#if modelConfig.description??>description = "${modelConfig.description}", </#if>interactive = true, echo = true)
    private ${modelConfig.type} ${modelConfig.fieldName}<#if modelConfig.defaultValue??> = ${modelConfig.defaultValue?c}</#if>;
        </#list>

        @SneakyThrows
        @Override
        public void run() {
        //内层给外层赋值
            Class<? extends GenericDataModel.${modelInfo.className}> ${modelInfo.groupKey}Class = ${modelInfo.groupKey}.getClass();
            <#list modelInfo.modelConfigList as modelConfig>
            Field ${modelConfig.fieldName}Field =${modelInfo.groupKey}Class.getDeclaredField("${modelConfig.fieldName}");
            ${modelConfig.fieldName}Field.setAccessible(true);
            ${modelConfig.fieldName}Field.set(${modelInfo.groupKey},${modelConfig.fieldName});
            </#list>
        }
    }
        </#if>
    </#list>


}
package ${basePackage}.cli.subcommand;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import freemarker.template.TemplateException;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import lombok.Data;
import java.io.IOException;
import java.util.concurrent.Callable;
import ${basePackage}.model.GenericDataModel;

@Data
@Command(name = "generate",description = "代码生成option", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    <#list modelConfig.models as commandArgModel>
        @Option(names = {<#if commandArgModel.abbr??>"-${commandArgModel.abbr}", </#if>"--${commandArgModel.fieldName}"}, arity = "0..1", <#if commandArgModel.description??>description = "${commandArgModel.description}", </#if>interactive = true, echo = true)
        private ${commandArgModel.type} ${commandArgModel.fieldName}<#if commandArgModel.defaultValue??> = ${commandArgModel.defaultValue?c}</#if>;
    </#list>




    /**
     * 该option命令被执行时的回调
     * @return
     * @throws Exception
     */
    @Override
    public Integer call() throws Exception {
        GenericDataModel genericDataModel = new GenericDataModel();
        BeanUtil.copyProperties(this,genericDataModel);
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
}
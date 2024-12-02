package ${basePackage}.cli.subcommand;

import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.model.GenericDataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "config",description = "查看可配置参数", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("可配置参数信息：");
        Field[] fields = ReflectUtil.getFields(GenericDataModel.class);
        for (Field field : fields) {
            System.out.println("字段名称："+field.getName());
            System.out.println("字段类型："+field.getType());
            System.out.println("----");
        }
    }
}

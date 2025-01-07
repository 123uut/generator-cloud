package com.lyh.cli.subcommand;

import cn.hutool.core.bean.BeanUtil;
import com.lyh.generator.HybridGenerator;
import com.lyh.model.AcmTemplateDataModel;
import freemarker.template.TemplateException;
import lombok.Data;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;

@Data
@CommandLine.Command(name = "generate",description = "代码生成option", mixinStandardHelpOptions = true)
public class GenerateCommand implements Runnable{

    @CommandLine.Option(names = {"-g","--needGit"}, description = "是否生成git文件", arity = "0..1",interactive = true,echo = true)
    private Boolean needGit = false;

    @CommandLine.Option(names = {"-l","--ifloop"}, description = "是否开启循环", arity = "0..1",interactive = true,echo = true)
    private Boolean ifLoop = false;

    private static AcmDataModel acmDataModel = new AcmDataModel();

    /**
     * 该option命令被执行时的回调
     * @return
     * @throws Exception
     */
    @Override
    public void run() {
        System.out.println(needGit);
        System.out.println(ifLoop);
        //如果ifLoop为true引导用户输入
        if(ifLoop){
            CommandLine commandLine = new CommandLine(AcmModelCommand.class);
            commandLine.execute("-a","-o");
        }
    }


    @Data
    @CommandLine.Command(name = "acmCommand")
    public static class AcmModelCommand implements Runnable{
        @CommandLine.Option(names = {"-a","--author"}, description = "作者名称", arity = "0..1",interactive = true,echo = true)
        private String author  = "lyh123";

        @CommandLine.Option(names = {"-o","--output"}, description = "输出信息", arity = "0..1",interactive = true,echo = true)
        private String printText = "sum:";

        @SneakyThrows
        @Override
        public void run() {
            //内层给外层赋值
            acmDataModel.setAuthor(author);
            acmDataModel.setPrintTxt(printText);
        }
    }

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(GenerateCommand.class);
        commandLine.execute("-g","-l");
    }
}




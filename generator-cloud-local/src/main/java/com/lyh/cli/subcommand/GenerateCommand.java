package com.lyh.cli.subcommand;

import cn.hutool.core.bean.BeanUtil;
import com.lyh.generator.HybridGenerator;
import com.lyh.model.AcmTemplateDataModel;
import freemarker.template.TemplateException;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "generate",description = "代码生成option", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-a","--author"}, description = "作者名称", arity = "0..1",interactive = true,echo = true)
    private String author  = "lyh123";

    @CommandLine.Option(names = {"-o","--output"}, description = "输出信息", arity = "0..1",interactive = true,echo = true)
    private String printText = "sum:";

    @CommandLine.Option(names = {"-l","--ifloop"}, description = "是否开启循环", arity = "0..1",interactive = true,echo = true)
    private Boolean ifLoop = false;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrintText() {
        return printText;
    }

    public void setPrintText(String printText) {
        this.printText = printText;
    }

    public Boolean getIfLoop() {
        return ifLoop;
    }

    public void setIfLoop(Boolean ifLoop) {
        this.ifLoop = ifLoop;
    }

    /**
     * 该option命令被执行时的回调
     * @return
     * @throws Exception
     */
    @Override
    public Integer call() throws Exception {
        AcmTemplateDataModel acmTemplateDataModel = new AcmTemplateDataModel();
        BeanUtil.copyProperties(this,acmTemplateDataModel);
        //动静结合生成代码
        try {
            HybridGenerator.doGenerator(acmTemplateDataModel);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}

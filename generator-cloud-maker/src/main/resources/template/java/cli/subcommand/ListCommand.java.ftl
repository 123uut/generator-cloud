package ${basePackage}.cli.subcommand;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "list",description = "查看要生成的所有文件列表",mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    @Override
    public void run() {

        // 输入路径
        String inputPath = "${fileConfig.inputRootPath}";
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }
    }

}
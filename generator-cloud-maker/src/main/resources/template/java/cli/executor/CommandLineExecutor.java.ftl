package ${basePackage}.cli.executor;

import ${basePackage}.cli.subcommand.ConfigCommand;
import ${basePackage}.cli.subcommand.GenerateCommand;
import ${basePackage}.cli.subcommand.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "${projectName}",mixinStandardHelpOptions = true)
public class CommandLineExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }



    public Integer execute(String[] args){
        return commandLine.execute(args);
    }


    @Override
    public void run() {
        // 不输入子命令时，给出友好提示
        System.out.println("请输入具体命令，或者输入 --help 查看命令提示");
    }
}

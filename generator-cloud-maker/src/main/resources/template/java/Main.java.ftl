package ${basePackage};

import ${basePackage}.cli.executor.CommandLineExecutor;

public class Main {
    public static void main(String[] args) {

        CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        commandLineExecutor.execute(args);
    }
}
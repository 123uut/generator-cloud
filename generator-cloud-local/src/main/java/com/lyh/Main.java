package com.lyh;

import com.lyh.cli.excutor.CommandLineExecutor;

public class Main {
    public static void main(String[] args) {
//        args = new String[]{"generate","-a","-o","-l"};
//        args = new String[]{"list"};

        CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        commandLineExecutor.execute(args);
    }
}
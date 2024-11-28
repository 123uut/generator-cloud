package com.lyh.maker.generator.jar;

import java.io.*;

/**
 * 给生成的代码生成器项目打成jar包
 */
public class JarGenerator {


    public static void doGenerateJar(String generatedPath) throws IOException, InterruptedException {
        //打jar包的命令 (win/linux不同)
        String jarMvnCommandWin =  "mvn.cmd clean package -DskipTests=true";
        String jarMvnCommandOthers =  "mvn clean package -DskipTests=true";

        String osName = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder = null;
        // 判断是否是Windows操作系统
        if (osName.contains("win")) {
            processBuilder = new ProcessBuilder(jarMvnCommandWin.split(" "));
            //指定要执行命令的位置
            processBuilder.directory(new File(generatedPath));
            Process process = processBuilder.start();

            //读取命令的输出信息
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null){
                System.out.println(line);
            }

            //等待命令执行完
            int exitCode = process.waitFor();
            System.out.println("jar包命令已执行，状态码："+exitCode+"（0:成功，1:失败）");

        }else {
            processBuilder = new ProcessBuilder(jarMvnCommandOthers.split(" "));
            //指定要执行命令的位置
            processBuilder.directory(new File(generatedPath));
            Process process = processBuilder.start();

            //读取命令的输出信息
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null){
                System.out.println(line);
            }

            //等待命令执行完
            int exitCode = process.waitFor();
            System.out.println("jar包命令已执行，状态码："+exitCode+"（0:成功，1:失败）");
        }

    }
}

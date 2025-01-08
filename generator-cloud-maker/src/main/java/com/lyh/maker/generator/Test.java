package com.lyh.maker.generator;

public class Test extends Thread{

    volatile boolean flag = true;

    @Override
    public void run() {
        while(flag){
            System.out.println(1111);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(2222);
    }

    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();

        test.start();

        Thread.sleep(300);
        test.flag = false;
    }
}

package com.bjhy.fbackup.common.test.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadPool {  
	  
    private ExecutorService exe;  
    private static final int POOL_SIZE = 4;  
      
    public MyThreadPool() {  
        exe = Executors.newFixedThreadPool(POOL_SIZE);  
    }  
      
    public void doTask() {  
        int i = 0;  
        while (i < 10) {  
            exe.execute(new MyThread(i, exe));  
            i++;  
            System.out.println("111111111");
        }  
        System.out.println("00000000000000000000000000000");
    }  
      
    class MyThread implements Runnable  
    {  
        int id;  
        ExecutorService exe;  
        MyThread(int id, ExecutorService exe) {  
            this.exe = exe;  
            this.id = id;  
        }  
        public void run() {  
            System.out.println(id + "start");  
            try {  
                Thread.sleep(2000L);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
              
            System.out.println(id + "pass 5 second");  
            System.out.println("exe info:" + exe.toString());  
            try {  
                Thread.sleep(5000L);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
              
            System.out.println(id + "end");  
        }  
    }  
      
    public static void main(String[] args) {  
        new MyThreadPool().doTask();  
    }  
}
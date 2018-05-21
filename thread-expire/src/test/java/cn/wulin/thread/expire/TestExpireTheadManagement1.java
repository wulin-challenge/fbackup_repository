package cn.wulin.thread.expire;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import cn.wulin.thread.expire.thread.ExecuteTask;
import cn.wulin.thread.expire.thread.ExpireTheadManagement;

public class TestExpireTheadManagement1 {
	private ExpireTheadManagement<String> etm = new ExpireTheadManagement<>(3, 1000*5, 2);
	
	private ExpireTheadManagement<String> etm2 = new ExpireTheadManagement<>(3, 1000*5, 2);
	
	private ExpireTheadManagement<String> etm3 = new ExpireTheadManagement<>(3, 1000*5, 2);
	
	private ExpireTheadManagement<String> etm4 = new ExpireTheadManagement<>(3, 1000*5, 20);
	
	@Test
	public void test1() throws InterruptedException, ExecutionException, IOException{
		for (int i = 0; i < 30; i++) {
			FutureTask<String> executeTask = executeTask(i);
			if(executeTask == null){
				System.out.println("---------"+i+"--------------执行失败,1秒后继续执行---");
				Thread.sleep(1000);
				i--;
			}
		}
		System.out.println();
//		System.in.read();
	}
	
	@Test
	public void test2() throws InterruptedException, ExecutionException, IOException{
		for (int i = 0; i < 50000; i++) {
			FutureTask<String> executeTask = executeTask2(i);
			if(executeTask == null){
//				if( i%100==0){
					System.out.println("---------"+i+"--------------执行失败,1秒后继续执行---");
//				}
				Thread.sleep(10);
				i--;
			}
			
		}
		System.out.println(1111);
//		System.in.read();
	}
	
	@Test
	public void test3() throws InterruptedException, ExecutionException, IOException{
		for (int i = 0; i < 50000; i++) {
			FutureTask<String> executeTask = executeTask3(i);
			if(executeTask == null){
//				if( i%100==0){
					System.out.println("---------"+i+"--------------执行失败,1秒后继续执行---");
//				}
//				Thread.sleep(10);
				i--;
			}
			
		}
		System.out.println(1111);
//		System.in.read();
	}
	
	@Test
	public void test4() throws InterruptedException, ExecutionException, IOException{
		for (int i = 0; i < 50000; i++) {
			FutureTask<String> executeTask = executeTask4(i);
			if(executeTask == null){
					System.out.println("-----"+i+"-----嗯...这是什么情况,不是保证100%成功吗?居然出现bug了!!---");
				i--;
			}
		}
		System.out.println(1111);
//		System.in.read();
	}
	
	private FutureTask<String> executeTask(int i) throws InterruptedException, ExecutionException{
		return etm.newTask(new ExecuteTask<String>(){
			@Override
			public String callTask() throws InterruptedException {
				System.out.println(i+"---开始执行....");
				Thread.sleep(1000000);
//				System.out.println(i+"---执行结束....");
				return null;
			}
		});
	}
	
	private FutureTask<String> executeTask2(int i) throws InterruptedException, ExecutionException{
		return etm2.newTask(new ExecuteTask<String>(){
			@Override
			public String callTask() throws InterruptedException {
				System.out.println(i+"---开始执行....");
				Thread.sleep(1000000);
				System.out.println(i+"---执行结束....");
				return null;
			}
		});
	}
	
	private FutureTask<String> executeTask3(int i) throws InterruptedException, ExecutionException{
		return etm3.newTask(new ExecuteTask<String>(){
			@Override
			public String callTask() throws InterruptedException {
				System.out.println(i+"---开始执行....");
				Thread.sleep(1000000);
				System.out.println(i+"---执行结束....");
				return null;
			}
		},10,TimeUnit.MILLISECONDS);
	}
	
	private FutureTask<String> executeTask4(int i) throws InterruptedException, ExecutionException{
		return etm4.putNewTask(new ExecuteTask<String>(){
			@Override
			public String callTask() throws InterruptedException {
				System.out.println(i+"---开始执行....");
				Thread.sleep(15000);
				System.out.println(i+"---执行结束....");
				return null;
			}
		});
	}
	
	

}

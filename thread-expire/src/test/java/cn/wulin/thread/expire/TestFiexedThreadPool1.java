package cn.wulin.thread.expire;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.wulin.thread.expire.thread.ExecuteTask;
import cn.wulin.thread.expire.thread.FixedThreadPool;

/**
 * 测试固定线程池
 * @author wubo
 *
 */
public class TestFiexedThreadPool1 {
	private Logger logger = LoggerFactory.getLogger(TestFiexedThreadPool1.class);
	FixedThreadPool<String> fixedThreadPool = new FixedThreadPool<>();
	
	@Test
	public void test_fixed_Thead_pool() throws InterruptedException, ExecutionException{
		for (int i = 0; i < 2; i++) {
			System.out.println();
			fixedThreadPool.submit(getExcuteTask());
			System.out.println();
		}
	}
	
	
	@Test
	public void testLog(){
		logger.error("出错了!!");
	}
	
	@SuppressWarnings("unchecked")
	private ExecuteTask<String> getExcuteTask(){
		return new ExecuteTask<String>(){
			@Override
			public String callTask() throws InterruptedException {
				System.out.println("我是子线程,我开始执行....");
				Thread.sleep(2000);
				System.out.println("我是子线程,我已经执行完毕业务逻辑....");
				return "ok";
			}
		};
		
	}
	

}

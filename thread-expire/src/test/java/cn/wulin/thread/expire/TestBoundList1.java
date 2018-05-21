package cn.wulin.thread.expire;

import org.junit.Test;

import cn.wulin.thread.expire.thread.domain.BoundList;

public class TestBoundList1 {
	
	@Test
	public void test_getFirstValue(){
		BoundList<String> boundList = new BoundList<String>(2);
		boundList.add("1");
		boundList.add("2");
		boundList.add("3");
		
		String e0 = boundList.get(0);
		String firstElement = boundList.getFirstElement();
		System.out.println();
		
		
	}

}

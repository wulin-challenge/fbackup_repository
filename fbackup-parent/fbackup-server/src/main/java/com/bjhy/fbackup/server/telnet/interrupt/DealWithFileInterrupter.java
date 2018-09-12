package com.bjhy.fbackup.server.telnet.interrupt;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.interrupt.Interrupt;

import cn.wulin.brace.remoting.Channel;
import cn.wulin.ioc.Constants;
import cn.wulin.ioc.extension.Activate;
import cn.wulin.ioc.logging.Logger;
import cn.wulin.ioc.logging.LoggerFactory;
import cn.wulin.ioc.util.ConcurrentHashSet;

@Activate(group="com.bjhy.fbackup.common.domain.ClientFileTransfer")
public class DealWithFileInterrupter implements Interrupt<ClientFileTransfer>{
	private Logger logger = LoggerFactory.getLogger(DealWithFileInterrupter.class);
	
	private static final String DEAL_WITH_FILE_MAX = "deal.with.file.max";

	private static final String DEAL_WITH_FILE_COUNT = "deal.with.file.count";

	/**
	 * 正在传输的文件
	 */
	private static final ConcurrentHashSet<String> fileTransfering = new ConcurrentHashSet<String>();
	
	/**
	 * 需要打印的 telnet 客户端
	 */
	private static final ConcurrentHashSet<Channel> dealWithFiles = new ConcurrentHashSet<Channel>();
	
	/**
	 * key就代表监狱编号
	 * @param key
	 * @param channel
	 * @param max
	 */
	public static void addDealWithFile(Channel channel, int max){
		channel.setAttribute(DEAL_WITH_FILE_MAX, max);
        channel.setAttribute(DEAL_WITH_FILE_COUNT, new AtomicInteger());
        dealWithFiles.add(channel);
	}
	
	/**
	 * 得到正在传输的文件
	 * @return
	 */
	public static ConcurrentHashSet<String> getFileTransfering(){
		return fileTransfering;
	}
	
	@Override
	public void before(ClientFileTransfer entity, Class<ClientFileTransfer> clazz) {
		StringBuilder send = getTelnetShowInfo(entity);
		fileTransfering.add(send.toString());
		
		if(dealWithFiles.size()<=0){
			return;
		}
		
		for (Channel channel : new ArrayList<Channel>(dealWithFiles)) {
			if(channel.isConnected()){
				try {
                    int max = 1;
                    Integer m = (Integer) channel.getAttribute(DEAL_WITH_FILE_MAX);
                    if (m != null) {
                        max = (int) m;
                    }
                    int count = 0;
                    AtomicInteger c = (AtomicInteger) channel.getAttribute(DEAL_WITH_FILE_COUNT);
                    if (c == null) {
                        c = new AtomicInteger();
                        channel.setAttribute(DEAL_WITH_FILE_COUNT, c);
                    }
                    count = c.getAndIncrement();
                    if (count < max) {
                        String prompt = channel.getUrl().getParameter(Constants.PROMPT_KEY, Constants.DEFAULT_PROMPT);
                        channel.send("\r\n ["+count+"]:"+send.toString()+"\r\n\r\n" + prompt);
                    }
                    if (count >= max - 1) {
                    	dealWithFiles.remove(channel);
                    }
                } catch (Throwable e) {
                	dealWithFiles.remove(channel);
                    logger.warn(e.getMessage(), e);
                }
				
			}
		}
	}

	/**
	 * 得到telnet传输显示信息
	 * @param entity
	 * @return
	 */
	private StringBuilder getTelnetShowInfo(ClientFileTransfer entity) {
		StringBuilder send = new StringBuilder(entity.getClientName());
		send.append("("+entity.getClientNumber()+"): ");
		send.append(entity.getRelativeFilePath());
		return send;
	}

	@Override
	public void after(ClientFileTransfer entity, Class<ClientFileTransfer> clazz) {
		StringBuilder send = getTelnetShowInfo(entity);
		fileTransfering.remove(send.toString());
	}
}

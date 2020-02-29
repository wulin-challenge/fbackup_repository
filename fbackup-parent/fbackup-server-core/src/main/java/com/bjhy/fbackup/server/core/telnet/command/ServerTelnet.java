package com.bjhy.fbackup.server.core.telnet.command;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;
import com.bjhy.fbackup.server.core.base.BaseServerQueue;
import com.bjhy.fbackup.server.core.telnet.interrupt.DealWithFileInterrupter;

import cn.wulin.brace.remoting.Channel;
import cn.wulin.brace.remoting.RemotingException;
import cn.wulin.brace.remoting.telnet.TelnetHandler;
import cn.wulin.brace.remoting.telnet.support.Help;
import cn.wulin.ioc.Constants;
import cn.wulin.ioc.extension.Activate;
import cn.wulin.ioc.util.ConcurrentHashSet;
import cn.wulin.ioc.util.StringUtils;
@Activate
@Help(parameter="[transfering|trace_transfer_file max|waiting_client|waiting_transfer_file|zk_client]",summary="display the server to transfer information !",detail="display the server to transfer information !")
public class ServerTelnet implements TelnetHandler{

	@Override
	public String telnet(Channel channel, String message) throws RemotingException {
		String prompt = channel.getUrl().getParameter(Constants.PROMPT_KEY, Constants.DEFAULT_PROMPT);
		if(StringUtils.isBlank(message)){
			return "the parameter cannot be empty!";
		}else{
			String[] queueArray = message.split(" ");
			String queue = queueArray[0];
			int max = queueArray.length==2?Integer.parseInt(queueArray[1]):1;
			
			//追踪栈传输文件
			if("trace_transfer_file".equals(queue)){
				DealWithFileInterrupter.addDealWithFile(channel, max);
				
			//正在传输的文件
			}else if("transfering".equals(queue)){
				ConcurrentHashSet<String> fileTransfering = DealWithFileInterrupter.getFileTransfering();
				if(fileTransfering.size()<=0){
                    channel.send("\r\n no files transferred !\r\n\r\n" + prompt);
				}
				int count = 0;
				for (String send : fileTransfering) {
                    channel.send("\r\n ["+count+"]:"+send.toString()+"\r\n\r\n" + prompt);
                    count++;
				}
				
			//正在等待传输的客户端
			}else if("waiting_client".equals(queue)){
				BlockingQueue<XmlFbackup> clientQueue = BaseServerQueue.getClientQueue();
				int size = clientQueue.size();
				if(size<=0){
                    channel.send("\r\n no clients are waiting!\r\n\r\n" + prompt);
				}else{
					Iterator<XmlFbackup> iterator2 = clientQueue.iterator();
					int count = 0;
					while(iterator2.hasNext()){
						XmlFbackup next = iterator2.next();
						XmlClient xmlClient = next.getXmlClient();
						String clientName = xmlClient.getClientName();
						String clientNumber = xmlClient.getClientNumber();
						channel.send("["+count+"]"+clientName+","+clientNumber+"\r\n");
						count++;
					}
					channel.send("\r\n" + prompt);
				}
				
			//正在等待传输的客户端文件
			}else if("waiting_transfer_file".equals(queue)){
				BlockingQueue<ClientFileTransfer> clientFileTransferQueue = BaseServerQueue.getClientFileTransferQueue();
				int size = clientFileTransferQueue.size();
				if(size<=0){
                    channel.send("\r\n no client files are waiting!\r\n\r\n" + prompt);
				}else{
					Iterator<ClientFileTransfer> iterator = clientFileTransferQueue.iterator();
					while(iterator.hasNext()){
						ClientFileTransfer next = iterator.next();
						StringBuilder telnetShowInfo = getTelnetShowInfo(next);
						channel.send(telnetShowInfo.toString()+"\r\n");
					}
					channel.send("\r\n" + prompt);
				}
			
			//查看zk上的客户端
			}else if("zk_client".equals(queue)){
				ZookeeperCuratorConfig config = ExtensionLoader.getInstance(ZookeeperCuratorConfig.class);
				
				List<String> childrenFullPathList = config.getChildrenFullPathList(ZookeeperCuratorConfig.ROOT_NODE_CLIENT);
				
				int count = 0;
				for (String fullPath : childrenFullPathList) {
					byte[] nodeData = config.getNodeData(fullPath);
					XmlFbackup xmlFbackup = SeriUtil.unserializeProtoStuffToObj(nodeData, XmlFbackup.class);
					XmlClient xmlClient = xmlFbackup.getXmlClient();
					
					StringBuilder send = new StringBuilder("["+count+"]"+xmlClient.getClientName());
					send.append("("+xmlClient.getClientNumber()+")->");
					send.append(xmlFbackup.getServerIp()+":"+xmlFbackup.getServerPort());
					channel.send(send.toString()+"\r\n");
					count++;
				}
				channel.send("\r\n" + prompt);
			}
		}
		return null;
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

}

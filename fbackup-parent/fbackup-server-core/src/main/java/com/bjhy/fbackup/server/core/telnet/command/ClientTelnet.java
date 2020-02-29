package com.bjhy.fbackup.server.core.telnet.command;

import java.net.URLEncoder;
import java.util.List;

import com.bjhy.fbackup.common.domain.DerbyPage;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.HttpClientUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;
import com.bjhy.fbackup.common.zookeeper.curator.ZookeeperCuratorConfig;
import com.bjhy.fbackup.server.core.util.ClientHttpUtil;

import cn.wulin.brace.remoting.Channel;
import cn.wulin.brace.remoting.RemotingException;
import cn.wulin.brace.remoting.telnet.TelnetHandler;
import cn.wulin.brace.remoting.telnet.support.Help;
import cn.wulin.ioc.Constants;
import cn.wulin.ioc.extension.Activate;
import cn.wulin.ioc.util.StringUtils;

@Activate
@Help(parameter="[ls max sleepTime]",summary="show fbackup client information",detail="show fbackup client information")
public class ClientTelnet implements TelnetHandler{
	
	/**
	 * 本身的应用配置
	 */
	private static final XmlFbackup server = ExtensionLoader.getInstance(XmlFbackup.class); 
	
	@Override
	public String telnet(Channel channel, String message) throws RemotingException {
		String prompt = channel.getUrl().getParameter(Constants.PROMPT_KEY, Constants.DEFAULT_PROMPT);
		if(StringUtils.isBlank(message)){
			return "the parameter cannot be empty!";
		}else{
			String[] queueArray = message.split(" ");
			String command = queueArray[0];
			int max = queueArray.length>=1?Integer.parseInt(queueArray[1]):1;
			int sleepTime = queueArray.length>=3?Integer.parseInt(queueArray[2]):500;
			
			//追踪栈传输文件
			if("ls".equals(command)){
				for (int i = 0; i < max; i++) {
					channel.send("\r\n==========================================================\r\n\r\n");
					sendLsInfo(i,channel, prompt);
					if(max != 1){
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 发送ls命令的信息
	 * @param channel
	 * @param prompt
	 * @throws RemotingException
	 */
	private void sendLsInfo(int number,Channel channel, String prompt) throws RemotingException {
		ZookeeperCuratorConfig config = ExtensionLoader.getInstance(ZookeeperCuratorConfig.class);
		List<String> childrenFullPathList = config.getChildrenFullPathList(ZookeeperCuratorConfig.ROOT_NODE_CLIENT);
		
		int count = 0;
		for (String fullPath : childrenFullPathList) {
			byte[] nodeData = config.getNodeData(fullPath);
			XmlFbackup client = SeriUtil.unserializeProtoStuffToObj(nodeData, XmlFbackup.class);
			
			if(client != null){
				String serverNumber = server.getXmlServer().getServerNumber();//服务的编号
				try {
					serverNumber = URLEncoder.encode(serverNumber, "utf-8");
				} catch (Exception e) {
					LoggerUtils.error("编码失败",e);
				}
				StringBuffer clientHttpUrl = ClientHttpUtil.getClientHttpUrl(client);
				//得到 DerbyPage 的分页httpUrl
				String derbyPageHttpUrl = ClientHttpUtil.getDerbyPageHttpUrl(client, clientHttpUrl,serverNumber);
				//得到 PageclientFiles 分页httpUrl
				String pageHttpUrl = ClientHttpUtil.getPageclientFilesHttpUrl(client, clientHttpUrl,serverNumber);
				
				DerbyPage derbyPage = HttpClientUtil.sendHttpGet(derbyPageHttpUrl, DerbyPage.class);
				
				if(derbyPage == null){
					continue;
				}
				XmlClient xmlClient = client.getXmlClient();
				StringBuilder send = new StringBuilder("["+count+"]["+number+"]"+xmlClient.getClientName());
				send.append("("+xmlClient.getClientNumber()+")");
				send.append("->待传输的数据:"+derbyPage.getDataTotal());
				channel.send(send.toString()+"\r\n");
			}
			count++;
		}
		channel.send("\r\n" + prompt);
	}

}

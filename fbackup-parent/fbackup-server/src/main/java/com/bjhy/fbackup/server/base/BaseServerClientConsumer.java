package com.bjhy.fbackup.server.base;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.core.OrderComparator;

import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.domain.ClientFileTransfer;
import com.bjhy.fbackup.common.domain.DerbyPage;
import com.bjhy.fbackup.common.domain.FileStatus;
import com.bjhy.fbackup.common.domain.XmlClient;
import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.interrupt.Interrupt;
import com.bjhy.fbackup.common.thread.FixedThreadQueue;
import com.bjhy.fbackup.common.util.ConstantUtil;
import com.bjhy.fbackup.common.util.HttpClientUtil;
import com.bjhy.fbackup.common.util.ListenerUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.server.util.ClientHttpUtil;
import com.bjhy.fbackup.server.util.ServerCenterUtil;

import cn.wulin.ioc.extension.InterfaceExtensionLoader;
import cn.wulin.ioc.util.UrlUtils;
import cn.wulin.thread.expire.thread.ExecuteTask;
import cn.wulin.thread.expire.thread.ExpireTheadManagement;

/**
 * 客户端消费者
 * @author wubo
 *
 */
public class BaseServerClientConsumer {
	
	/**
	 * 本身的应用配置
	 */
	private static final XmlFbackup server = ExtensionLoader.getInstance(XmlFbackup.class); 
	
	private static final List<Class<? extends FileStoreType>> fileStoreTypeList = ListenerUtil.getListenerClass(FileStoreType.class);
	
	/**
	 * 要执行的文件存储类型实例列表
	 */
	private static final List<FileStoreType> executeFileStoreTypeInstances = new ArrayList<FileStoreType>();
	
	private static final List<Class<? extends StaticStoreType>> staticStoreTypeList = ListenerUtil.getListenerClass(StaticStoreType.class);
	
	/**
	 * 要执行的静态类型存储类型实例列表
	 */
	private static final List<StaticStoreType> executeStaticStoreTypeInstances = new ArrayList<StaticStoreType>();
	
	/**
	 * 客户端队列消费者(每次只开启一个线程其周期执行)
	 */
	private final ScheduledThreadPoolExecutor clientConsumerExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
	
	/**
	 * 客户端文件传输队列消费者(每次只开启1个线程其周期执行)
	 */
	private final ScheduledThreadPoolExecutor clientFileTransferExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
	
	/**
	 * 服务端版本处理
	 */
	private final ScheduledThreadPoolExecutor serverVersionExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
	
	/**
	 * 客户端传输的最大运行线程数
	 * 已经被废弃 被 下面 客户端文件传输管理者 替代
	 */
	@Deprecated
	private final FixedThreadQueue<String> clientFileTransferQueue = new FixedThreadQueue<String>(5);
	
	/**
	 * 客户端文件传输管理者
	 */
	private ExpireTheadManagement<String> clientFileTransferManagement = new ExpireTheadManagement<>(ServerCenterUtil.getManagementMaxSize(), ServerCenterUtil.getFirstElementSurvivalTime(), ServerCenterUtil.getThreadPoolCapacity());
	
	
	
	//执行消费者
	public void executorConsumer(){
		try {
			executorClientConsumer();//执行客户端队列消费者
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			executorClientFileTransferConsumer();//客户端文件传输队列消费者
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			excutorVersionScheduled();//执行服务端版本定时器
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行服务端版本定时器
	 */
	public void excutorVersionScheduled(){
		serverVersionExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ServerCenterUtil.checkSyncVersion();
				} catch (Exception e) {
					LoggerUtils.error("excutorVersionScheduled: ",e);
				}
			}
		}, 30, 30, TimeUnit.SECONDS);
	}
	
	/**
	 * 执行客户端队列消费者
	 */
	private void executorClientConsumer(){
		clientConsumerExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					dealWithClientConsumer();
				} catch (Exception e) {
					LoggerUtils.error("executorClientConsumer: ",e);
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}
	
	/**
	 * 客户端文件传输队列消费者(之所以采用定时器,是为了防止意外崩溃后,程序可以在一下个定时器到来时恢复任务)
	 */
	private void executorClientFileTransferConsumer(){
		clientFileTransferExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					dealWithFileTransferTask();
				} catch (Exception e) {
					LoggerUtils.error("executorClientFileTransferConsumer: ",e);
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}
	
	/**
	 * 多线程处理文件传输任务
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@SuppressWarnings({"rawtypes","unchecked"})
	private void dealWithFileTransferTask() throws InterruptedException, ExecutionException{
		while(true){
			final ClientFileTransfer clientFileTransfer = BaseServerQueue.getClientFileTransferQueue().take();
			clientFileTransferManagement.putNewTask(new ExecuteTask<String>(){
				@Override
				public String callTask() throws InterruptedException {
					if(checkClientFileStatus(clientFileTransfer)){
						dealWithFileTransferTask(clientFileTransfer);
					}
					return null;
				}

				private void dealWithFileTransferTask(final ClientFileTransfer clientFileTransfer) {
					//增加前置拦截
					List<Interrupt> interruptList = InterfaceExtensionLoader.getExtensionLoader(Interrupt.class).getActivateExtension(UrlUtils.getEmptyUrl("", ""), "", ClientFileTransfer.class.getName());
					try {
						if(ServerCenterUtil.isOpenDetailLogInfo()){
							LoggerUtils.info("传输开始: "+clientFileTransfer.toString());
						}
					
						for (Interrupt interrupt : interruptList) {
							interrupt.before(clientFileTransfer, ClientFileTransfer.class);
						}
						//处理文件传输
						deaWithFileTransferConsumer(clientFileTransfer);
					} catch (Exception e) {
						LoggerUtils.error(e.getMessage());
					}
					//增加后置拦截
					for (Interrupt interrupt : interruptList) {
						interrupt.after(clientFileTransfer, ClientFileTransfer.class);
					}
					if(ServerCenterUtil.isOpenDetailLogInfo()){
						LoggerUtils.info("传输结束: "+clientFileTransfer.toString());
					}
				}
			});
		}
	}
	
	private boolean checkClientFileStatus(ClientFileTransfer clientFileTransfer){
		String serverNumber = server.getXmlServer().getServerNumber();//服务的编号
		XmlFbackup client = clientFileTransfer.getClient();
		StringBuffer clientHttpUrl = ClientHttpUtil.getClientHttpUrl(client);
		
		String relativeFilePath = clientFileTransfer.getRelativeFilePath();
		try {
			relativeFilePath = URLEncoder.encode(relativeFilePath, "utf-8");
			serverNumber = URLEncoder.encode(serverNumber, "utf-8");
		} catch (Exception e) {
			LoggerUtils.error("编码失败",e);
		}
		String httpUrl = ClientHttpUtil.getCheckClientFileStatusUrl(client, clientHttpUrl, relativeFilePath, serverNumber);
		FileStatus fileStatus = HttpClientUtil.sendHttpGet(httpUrl, FileStatus.class);
		
		return fileStatus != null && fileStatus.isFileNormal();
	}
	
//	/**
//	 * 多线程处理文件传输任务
//	 * @throws InterruptedException
//	 * @throws ExecutionException
//	 */
//	private void dealWithFileTransferTask() throws InterruptedException, ExecutionException{
//		while(true){
//			final ClientFileTransfer clientFileTransfer = BaseServerQueue.getClientFileTransferQueue().take();
//			clientFileTransferQueue.execute(new FixedThreadExecute<String>(){
//				@Override
//				public String execute() {
//					try {
//						deaWithFileTransferConsumer(clientFileTransfer);
//					} catch (Exception e) {
//						LoggerUtils.error(e.getMessage());
//					}
//					return null;
//				}
//			});
//		}
//	}
	
	/**
	 * 处理文件传输的消费消费者
	 * @throws Exception 
	 */
	private void deaWithFileTransferConsumer(ClientFileTransfer clientFileTransfer) throws Exception{
		String directoryType = clientFileTransfer.getDirectoryType();
		if(XmlClient.DIRECTORY_TYPE_PICTURE.equals(directoryType)){
			
			if(executeStaticStoreTypeInstances.size()==0){
				synchronized (executeStaticStoreTypeInstances) {
					if(executeStaticStoreTypeInstances.size()==0){
						executeStaticStoreTypeInstances.addAll(staticStoreTypeInstances());
					}
				}
			}
			for (StaticStoreType staticStoreType : executeStaticStoreTypeInstances) {
				staticStoreType.dealWithStatic(clientFileTransfer, server);
			}
			
		}else if(XmlClient.DIRECTORY_TYPE_DATABASE.equals(directoryType)){
			
			if(executeFileStoreTypeInstances.size()==0){
				synchronized (executeFileStoreTypeInstances) {
					if(executeFileStoreTypeInstances.size()==0){
					    executeFileStoreTypeInstances.addAll(fileStoreTypeInstances());
					}
				}
			}
			for (FileStoreType fileStoreType : executeFileStoreTypeInstances) {
				fileStoreType.dealWithFile(clientFileTransfer, server);
			}
		}
	}
	
	/**
	 * 得到所有 实现 StaticStoreType 接口的实例
	 * @return
	 */
	private List<StaticStoreType> staticStoreTypeInstances(){
		List<StaticStoreType> staticStoreTypeInstances = new ArrayList<StaticStoreType>();
		for (Class<? extends StaticStoreType> clazz : getStaticStoreTypeList()) {
			StaticStoreType instance = ExtensionLoader.getInstance(clazz);
			if(instance == null){
				try {
					instance = clazz.newInstance();
					ExtensionLoader.setInterfaceInstance(clazz, instance);
				} catch (InstantiationException e) {
					LoggerUtils.error("InstantiationException 异常", e);
				} catch (IllegalAccessException e) {
					LoggerUtils.error("IllegalAccessException 异常", e);
				}
			}
			staticStoreTypeInstances.add(instance);
		}
		return staticStoreTypeInstances;
	}
	
	/**
	 * 得到所有 实现 FileStoreType 接口的实例
	 * @return
	 */
	private List<FileStoreType> fileStoreTypeInstances(){
		List<FileStoreType> fileStoreTypeInstances = new ArrayList<FileStoreType>();
		for (Class<? extends FileStoreType> clazz : getFileStoreTypeList()) {
			FileStoreType instance = ExtensionLoader.getInstance(clazz);
			if(instance == null){
				try {
					instance = clazz.newInstance();
					ExtensionLoader.setInterfaceInstance(clazz, instance);
				} catch (InstantiationException e) {
					LoggerUtils.error("InstantiationException 异常", e);
				} catch (IllegalAccessException e) {
					LoggerUtils.error("IllegalAccessException 异常", e);
				}
			}
			fileStoreTypeInstances.add(instance);
		}
		return fileStoreTypeInstances;
	}
	
	/**
	 * 得到文件存储类型实现列表
	 * @return
	 */
	private  List<Class<? extends FileStoreType>> getFileStoreTypeList(){
		//内部实现
		List<Class<? extends FileStoreType>> fbackupInternal = new ArrayList<Class<? extends FileStoreType>>();
		//非内部实现
		List<Class<? extends FileStoreType>> nonFbackupInternal = new ArrayList<Class<? extends FileStoreType>>();
		
		for (Class<? extends FileStoreType> clazz : fileStoreTypeList) {
			FBackupListener fBackupListener = clazz.getAnnotation(FBackupListener.class);
			if(fBackupListener != null){
				boolean internal = fBackupListener.isFbackupInternal();
				if(internal){
					fbackupInternal.add(clazz);
				}else{
					nonFbackupInternal.add(clazz);
				}
			}else{
				nonFbackupInternal.add(clazz);
			}
		}
		//若外部实现不为空,则使用外部实现,否则默认使用内部实现
		if(nonFbackupInternal.size()>0){
			OrderComparator.sort(nonFbackupInternal);
			return nonFbackupInternal;
		}
		OrderComparator.sort(fbackupInternal);
		return fbackupInternal;
	}
	
	/**
	 * 得到静态文件存储类型实现列表
	 * @return
	 */
	private  List<Class<? extends StaticStoreType>> getStaticStoreTypeList(){
		//内部实现
		List<Class<? extends StaticStoreType>> fbackupInternal = new ArrayList<Class<? extends StaticStoreType>>();
		//非内部实现
		List<Class<? extends StaticStoreType>> nonFbackupInternal = new ArrayList<Class<? extends StaticStoreType>>();
		
		for (Class<? extends StaticStoreType> clazz : staticStoreTypeList) {
			FBackupListener fBackupListener = clazz.getAnnotation(FBackupListener.class);
			if(fBackupListener != null){
				boolean internal = fBackupListener.isFbackupInternal();
				if(internal){
					fbackupInternal.add(clazz);
				}else{
					nonFbackupInternal.add(clazz);
				}
			}else{
				nonFbackupInternal.add(clazz);
			}
		}
		//若外部实现不为空,则使用外部实现,否则默认使用内部实现
		if(nonFbackupInternal.size()>0){
			OrderComparator.sort(nonFbackupInternal);
			return nonFbackupInternal;
		}
		OrderComparator.sort(fbackupInternal);
		return fbackupInternal;
	}
	
	/**
	 * 处理客户端消费者的业务
	 * @throws InterruptedException 
	 */
	private void dealWithClientConsumer() throws InterruptedException{
		XmlFbackup client = BaseServerQueue.getClientQueue().take();
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
				return;
			}
			
			//总的分页数
			int totalPageNumber = derbyPage.getTotalPageNumber();
			
			for (int currentPage = 1; currentPage <= totalPageNumber; currentPage++) {
				Map<String,String> pageParams = new HashMap<String,String>();
				pageParams.put("currentPage", Integer.toString(currentPage));
				pageParams.put("perPageTotal",Integer.toString(ConstantUtil.DERBY_PAGE_PER_PAGE_NUMBER) );
				List<ClientFileTransfer> ClientFileTransferList = HttpClientUtil.sendHttpGetList(pageHttpUrl, pageParams, ClientFileTransfer.class);
				clientFileTransferQueueProducer(ClientFileTransferList,client);
			}
		}
	}
	
	/**
	 * 客户端文件阐述队列列表的生成者
	 * @param ClientFileTransferList 客户端文件传输列表
	 * @throws InterruptedException 
	 */
	private void clientFileTransferQueueProducer(List<ClientFileTransfer> ClientFileTransferList,XmlFbackup client){
		for (ClientFileTransfer clientFileTransfer : ClientFileTransferList) {
			try {
				clientFileTransfer.setClient(client);
				BaseServerQueue.getClientFileTransferQueue().put(clientFileTransfer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

package com.bjhy.fbackup.common.zookeeper;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.proto.WatcherEvent;

import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.util.FileUtil;
import com.bjhy.fbackup.common.util.LoggerUtils;
import com.bjhy.fbackup.common.util.SeriUtil;

/**
 * 
 * @author wubo
 * 使用 curator 包的类来替代
 *
 */
@Deprecated
public class ZookeeperEventAdapter implements ZookeeperEvent{
	
	private ZookeeperCofig zookeeperCofig;
	
	public ZookeeperEventAdapter(ZookeeperCofig zookeeperCofig){
		this.zookeeperCofig = zookeeperCofig;
	}

	@Override
	public void connect(WatchedEvent event) {
//		String path = event.getPath();
//		EventType type = event.getType();
		KeeperState state = event.getState();
		if(state == KeeperState.SyncConnected){
			LoggerUtils.info("注册成功!");
		}
	}

	@Override
	public void nodeCreated(WatchedEvent event) {
		System.out.println(event);
	}

	@Override
	public void nodeChildrenChanged(WatchedEvent event) {
		KeeperState state = event.getState();
		int intValue = state.getIntValue();
		WatcherEvent wrapper = event.getWrapper();
		String path3 = wrapper.getPath();
		Class<KeeperState> declaringClass = state.getDeclaringClass();
		System.out.println(event);
		String path = event.getPath();
		try {
			List<String> childrenList = zookeeperCofig.getZookeeper().getChildren(path, true);
			for (String path2 : childrenList) {
				String path0 = FileUtil.replaceSpritAndEnd(path)+path2;
				byte[] data = zookeeperCofig.getZookeeper().getData(path0, true, null);
				XmlFbackup xmlFbackup = SeriUtil.unserializeProtoStuffToObj(data, XmlFbackup.class);
				System.out.println(xmlFbackup);
			}
			
			
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nodeDataChanged(WatchedEvent event) {
		System.out.println(event);
	}

	@Override
	public void nodeDeleted(WatchedEvent event) {
		System.out.println(event);
	}

}

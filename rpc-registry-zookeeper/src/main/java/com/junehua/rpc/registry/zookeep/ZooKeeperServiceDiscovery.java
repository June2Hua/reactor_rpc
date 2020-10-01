package com.junehua.rpc.registry.zookeep;

import com.junehua.rpc.registry.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author JuneHua
 * @Date 2020/9/26 17:55
 * @Version 1.0
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private final ZkClient client;

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private static long requstTime = 0;

    public ZooKeeperServiceDiscovery(String zkAddress) {
        this.client = new ZkClient(zkAddress);
        log.info("ZooKeeperServiceDiscovery.ZooKeeperServiceDiscovery connect, zkAddress:{}", zkAddress);
    }

    @Override
    public String discovery(String serverName) {
        log.info("ZooKeeperServiceDiscovery.discovery  serverName:{}", serverName);
        try {
            String nodePath = ZookeeperServiceRegistry.ZK_REGISTRY_PATH + "/" + serverName;
            if (!client.exists(nodePath)) {
                log.warn("ZooKeeperServiceDiscovery.discovery zookeeper不存在该节点 serverName:{} nodePath:{}", serverName, nodePath);
                return null;
            }
            List<String> children = client.getChildren(nodePath);
            if (CollectionUtils.isEmpty(children)) {
                log.warn("ZooKeeperServiceDiscovery.discovery 该节点下不存在子节点 serverName:{} nodePath:{}", serverName, nodePath);
                return null;
            }
            //轮询法获得子节点
            requstTime++;
            int selectedNodeIndex = (int)(requstTime % children.size());
            String selectedNodeAddress = children.get(selectedNodeIndex);
            String leafNodeAddress = nodePath + "/" + selectedNodeAddress;
            log.info("ZooKeeperServiceDiscovery.discovery selectedNodeAddress:{}, leafNodeAddress:{}", selectedNodeAddress, leafNodeAddress);
            return client.readData(leafNodeAddress);
        } catch (Exception exception) {
            return null;
        }
    }
}

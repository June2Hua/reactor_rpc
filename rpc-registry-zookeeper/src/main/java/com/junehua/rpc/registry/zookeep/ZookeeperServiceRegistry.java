package com.junehua.rpc.registry.zookeep;

import com.junehua.rpc.registry.ServiceRegistry;
import lombok.Data;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author JuneHua
 * @Date 2020/9/26 17:08
 * @Version 1.0
 *
 * 使用zookeeper作为服务发现
 */
@Data
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    private final ZkClient client;//zookeeper客户端

    private static final int SESSION_TIMEOUT = 50000;

    private static final int CONNETION_TIMEOUT = 10000;

    public static final String ZK_REGISTRY_PATH = "/registry";

    private static final String LEAF_PREFIX = "/address-";

    public ZookeeperServiceRegistry(String serverAddress) {
        log.info("ZookeeperServiceRegistry.construct start ... serverAddress", serverAddress);
        this.client = new ZkClient(serverAddress, SESSION_TIMEOUT, CONNETION_TIMEOUT);
        if (!client.exists(ZK_REGISTRY_PATH)) {
            log.info("ZookeeperServiceRegistry.construct start ...  客户端创建/registry ");
            client.createPersistent(ZK_REGISTRY_PATH);
        }
        log.info("ZookeeperServiceRegistry.ZookeeperServiceRegistry connect serverAddress:{}", serverAddress);
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        String newServicePath = ZK_REGISTRY_PATH  + "/" + serviceName;
        if (!client.exists(newServicePath)) {
            //永久节点
            client.createPersistent(newServicePath);
        }
        //临时有序节点
        String tmpNode = newServicePath + LEAF_PREFIX;
        client.createEphemeralSequential(tmpNode, serviceAddress);
    }

}

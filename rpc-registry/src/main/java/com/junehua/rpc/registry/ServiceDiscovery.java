package com.junehua.rpc.registry;

/**
 * @Author JuneHua
 * @Date 2020/9/26 17:04
 * @Version 1.0
 */
public interface ServiceDiscovery {

    /**
     * 服务发现
     * @param serverName    服务名称
     * @return              服务地址
     */
    String discovery(String serverName);
}

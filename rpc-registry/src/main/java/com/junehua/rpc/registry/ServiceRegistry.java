package com.junehua.rpc.registry;

/**
 * @Author JuneHua
 * @Date 2020/9/26 17:02
 * @Version 1.0
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     * @param serviceName       服务名称
     * @param serviceAddress    服务地址
     */
    void register(String serviceName, String serviceAddress);
}

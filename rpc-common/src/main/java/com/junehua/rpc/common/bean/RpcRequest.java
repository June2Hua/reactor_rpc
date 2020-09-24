package com.junehua.rpc.common.bean;

import lombok.Data;

/**
 * @Author JuneHua
 * @Date 2020/9/23 23:39
 * @Version 1.0
 */
@Data
public class RpcRequest {
    private String requestId;
    private String serviceVersion;
    private String className;
    private String methodName;
    private Class<?>[] argsType;
    private Object[] args;
}

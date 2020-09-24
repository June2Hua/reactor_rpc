package com.junehua.rpc.common.bean;

import lombok.Data;

/**
 * @Author JuneHua
 * @Date 2020/9/23 23:40
 * @Version 1.0
 */
@Data
public class RpcResponse {
    private String requestId;
    private Object result;
    private Exception exception;
}

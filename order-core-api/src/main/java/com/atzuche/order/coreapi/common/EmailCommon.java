package com.atzuche.order.coreapi.common;

import lombok.Data;

/**
 * email 参数
 *
 * @author pengcheng.fu
 * @date 2020/7/31 15:21
 */
@Data
public class EmailCommon {

    /**
     * host_name
     */
    private String hostName;

    /**
     * from_addr
     */
    private String fromAddr;

    /**
     * from_name
     */
    private String fromName;

    /**
     * from_pwd
     */
    private String fromPwd;
}

package com.atzuche.order.coreapi.entity.dto;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 2:05 下午
 **/
public class RenterInfo {
    /**
     * 会员号
     */
    private String memNo;

    private String phone;

    private String headUrl;

    private String realName;

    private String nickName;

    private LocalDateTime certificationTime;

    private int orderSuccessCount;


}

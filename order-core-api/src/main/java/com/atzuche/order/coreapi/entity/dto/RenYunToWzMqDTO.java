package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

/**
 * RenYunToWzMqDTO
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenYunToWzMqDTO {

    private String msgId;

    private String queueName;

    private String msg;

}

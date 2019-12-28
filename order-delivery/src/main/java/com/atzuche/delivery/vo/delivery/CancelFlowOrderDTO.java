package com.atzuche.delivery.vo.delivery;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 胡春林
 *
 */
@Data
@ToString
public class CancelFlowOrderDTO implements Serializable {

    private String ordernumber;
    private String servicetype;
    private String sign;
}

package com.atzuche.delivery.vo.handover;

import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 交接车结构体
 */
@Data
@ToString
public class HandoverCarVO {

    /**
     * 交接车信息
     */
    private HandoverCarInfoDTO handoverCarInfoDTO;
    /**
     * 交接车备注信息
     */
    private HandoverCarRemarkDTO handoverCarRemarkDTO;
}


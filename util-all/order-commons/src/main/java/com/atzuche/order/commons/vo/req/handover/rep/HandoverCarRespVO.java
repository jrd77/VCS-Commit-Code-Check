package com.atzuche.order.commons.vo.req.handover.rep;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * @author 胡春林
 * 返回给service的数据
 */
@Data
@ToString
public class HandoverCarRespVO {
    List<RenterHandoverCarInfoVO> renterHandoverCarInfoVOS;
    List<OwnerHandoverCarInfoVO> ownerHandoverCarInfoVOS;
}

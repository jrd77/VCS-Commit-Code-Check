package com.atzuche.order.delivery.service.handover.interfaces;

import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqDTO;

/**
 * @author 胡春林
 */
public interface IUpdateHandoverCarInfo {

    /**
     * 更新取还车信息
     * @param handoverCarInfoReqDTO
     */
    void updateHandoverCarOilMileageNum(HandoverCarInfoReqDTO handoverCarInfoReqDTO);
}

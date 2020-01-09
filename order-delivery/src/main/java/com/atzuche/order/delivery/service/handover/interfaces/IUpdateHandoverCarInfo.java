package com.atzuche.order.delivery.service.handover.interfaces;

import com.atzuche.order.delivery.vo.handover.req.HandoverCarInfoReqDTO;

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

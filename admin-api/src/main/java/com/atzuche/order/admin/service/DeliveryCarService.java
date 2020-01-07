package com.atzuche.order.admin.service;

import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoService;
import com.atzuche.order.delivery.service.handover.HandoverCarInfoService;
import com.atzuche.order.delivery.vo.delivery.rep.DeliveryCarVO;
import com.atzuche.order.delivery.vo.delivery.req.CarConditionPhotoUploadVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqVO;
import com.atzuche.order.delivery.vo.handover.req.HandoverCarInfoReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 胡春林
 * 配送信息服务
 */
@Service
public class DeliveryCarService {

    @Autowired
    DeliveryCarInfoService deliveryCarInfoService;
    @Autowired
    HandoverCarInfoService handoverCarInfoService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarVO findDeliveryListByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        logger.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
        return deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO);
    }

    /**
     * 上传交接车
     * @param photoUploadReqVo
     * @return
     */
    public Boolean uploadByOrderNo(CarConditionPhotoUploadVO photoUploadReqVo) throws Exception {
        logger.info("入参photoUploadReqVo：[{}]", photoUploadReqVo.toString());
        return handoverCarInfoService.uploadByOrderNo(photoUploadReqVo);
    }

    /**
     * 更新交接车信息
     *
     * @param handoverCarReqVO
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateHandoverCarInfo(HandoverCarInfoReqVO handoverCarReqVO) throws Exception {
        logger.info("入参handoverCarReqVO：[{}]", handoverCarReqVO.toString());
        handoverCarInfoService.updateHandoverCarInfo(handoverCarReqVO);
    }

    /**
     * 更新取还车信息 更新仁云接口
     *
     * @param deliveryReqVO
     * @throws Exception
     */
    public void updateDeliveryCarInfo(DeliveryReqVO deliveryReqVO) throws Exception {
        logger.info("入参deliveryReqVO：[{}]", deliveryReqVO.toString());
        handoverCarInfoService.updateDeliveryCarInfo(deliveryReqVO);
    }
}

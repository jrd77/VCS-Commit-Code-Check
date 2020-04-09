package com.atzuche.order.delivery.service.handover;

import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqDTO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.delivery.common.DeliveryCarTask;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OrderDeliveryFlowEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.DeliveryTypeEnum;
import com.atzuche.order.delivery.enums.UsedDeliveryTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.exception.HandoverCarOrderException;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryMapper;
import com.atzuche.order.delivery.service.OrderDeliveryFlowService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.utils.OSSUtils;
import com.atzuche.order.delivery.vo.delivery.*;
import com.atzuche.order.delivery.vo.delivery.req.CarConditionPhotoUploadVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqDTO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqVO;
import com.atzuche.order.renterorder.service.RenterOrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.serialNumber;

/**
 * @author 胡春林
 * 交接车信息
 */
@Service
public class HandoverCarInfoService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    HandoverCarService handoverCarService;
    @Autowired
    DeliveryCarService deliveryCarInfoService;
    @Resource
    RenterOrderDeliveryMapper renterOrderDeliveryMapper;
    @Autowired
    DeliveryCarTask deliveryCarTask;
    @Autowired
    OwnerHandoverCarService ownerHandoverCarService;
    @Autowired
    RenterHandoverCarService renterHandoverCarService;
    @Autowired
    OrderDeliveryFlowService deliveryFlowService;
    @Autowired
    RenterOrderService renterOrderService;

    /**
     * 上传交接车
     * @param photoUploadReqVo
     * @return
     */
    public Boolean uploadByOrderNo(CarConditionPhotoUploadVO photoUploadReqVo) throws Exception {
        logger.debug("上传图片参数：{}", ToStringBuilder.reflectionToString(photoUploadReqVo));
        String orderNo = photoUploadReqVo.getOrderNo();
        String basePath = CommonUtils.createTransBasePath(orderNo + "");
        String str = UUID.randomUUID().toString();
        String key = basePath + "delivery/" + str + ".jpg";
        int photoType = Integer.parseInt(photoUploadReqVo.getPhotoType());
        int userType = Integer.parseInt(photoUploadReqVo.getUserType());
        handoverCarService.validateOrderInfo(photoUploadReqVo.getMemNo(), orderNo, userType, photoType);
        logger.info("上传交接车。。。。。orderNo is {},photoType is{},userType is {},serialNumber is {} picKey={}", orderNo, photoType, userType, serialNumber, key);
        //生成原图片
        boolean result = OSSUtils.uploadAuth(key, photoUploadReqVo.getPhotoContent(), "");
        if (result) {
            handoverCarService.findUpdateHandoverCarInfo(orderNo, userType, photoType, key);
            logger.info("上传文件到阿里云成功。。。。。orderNo is {},photoType is{},userType is {},serialNumber is {}", orderNo, photoType, userType, serialNumber);
            return true;
        }
        return false;
    }

    /**
     * 更新交接车信息
     *
     * @param handoverCarReqVO
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateHandoverCarInfo(HandoverCarInfoReqVO handoverCarReqVO)  {
        logger.debug("参数：{}", ToStringBuilder.reflectionToString(handoverCarReqVO));
        if (Objects.isNull(handoverCarReqVO)) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "参数错误");
        }
        if (handoverCarReqVO.getRenterHandoverCarDTO() != null) {
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = handoverCarReqVO.getRenterHandoverCarDTO();
            //更新租客交接车相关信息
            renterHandoverCarService.updateHandoverCarOilMileageNum(handoverCarInfoReqDTO);
            //获取是否是取还车单子
            List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryMapper.findRenterOrderListByorderNo(handoverCarInfoReqDTO.getOrderNo());
            for(RenterOrderDeliveryEntity renterOrderDeliveryEntity : renterOrderDeliveryEntityList){
                if (Objects.nonNull(renterOrderDeliveryEntity) && renterOrderDeliveryEntity.getIsNotifyRenyun().intValue() == 0) {
                    if(StringUtils.isNotBlank(handoverCarInfoReqDTO.getOwnReturnKM()) && StringUtils.isNotBlank(handoverCarInfoReqDTO.getRenterReturnOil())){
                        if(renterOrderDeliveryEntity.getType() == 2)
                        {
                            HandoverCarInfoReqDTO renterHandoverCarInfoReqDTO = handoverCarInfoReqDTO;
                            //更新车主交接车相关信息
                            ownerHandoverCarService.updateHandoverCarOilMileageNum(renterHandoverCarInfoReqDTO);
                        }
                    }else if(StringUtils.isNotBlank(handoverCarInfoReqDTO.getRenterRetrunKM()) && StringUtils.isNotBlank(handoverCarInfoReqDTO.getOwnReturnOil()))
                    {
                        if(renterOrderDeliveryEntity.getType() == 1)
                        {
                            HandoverCarInfoReqDTO renterHandoverCarInfoReqDTO = handoverCarInfoReqDTO;
                            //更新车主交接车相关信息
                            ownerHandoverCarService.updateHandoverCarOilMileageNum(renterHandoverCarInfoReqDTO);
                        }

                    }
                }
            }
            return;

        }
        //车主取还车
        if (handoverCarReqVO.getOwnerHandoverCarDTO() != null) {
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = handoverCarReqVO.getOwnerHandoverCarDTO();
            //更新车主交接车相关信息
            ownerHandoverCarService.updateHandoverCarOilMileageNum(handoverCarInfoReqDTO);
            //获取是否是取还车单子
            List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryMapper.findRenterOrderListByorderNo(handoverCarInfoReqDTO.getOrderNo());
            for(RenterOrderDeliveryEntity renterOrderDeliveryEntity : renterOrderDeliveryEntityList){
                if (Objects.nonNull(renterOrderDeliveryEntity) && renterOrderDeliveryEntity.getIsNotifyRenyun().intValue() == 0) {
                    if(StringUtils.isNotBlank(handoverCarInfoReqDTO.getOwnReturnKM()) && StringUtils.isNotBlank(handoverCarInfoReqDTO.getOwnReturnOil())){
                    if(renterOrderDeliveryEntity.getType() == 2)
                    {
                        HandoverCarInfoReqDTO renterHandoverCarInfoReqDTO = handoverCarInfoReqDTO;
                        //更新租客交接车相关信息
                        renterHandoverCarService.updateHandoverCarOilMileageNum(renterHandoverCarInfoReqDTO);
                    }
                    }else if(StringUtils.isNotBlank(handoverCarInfoReqDTO.getRenterRetrunKM()) && StringUtils.isNotBlank(handoverCarInfoReqDTO.getRenterReturnOil()))
                    {
                        if(renterOrderDeliveryEntity.getType() == 1)
                        {
                            HandoverCarInfoReqDTO renterHandoverCarInfoReqDTO = handoverCarInfoReqDTO;
                            //更新租客交接车相关信息
                            renterHandoverCarService.updateHandoverCarOilMileageNum(renterHandoverCarInfoReqDTO);
                        }

                    }
                }
            }
            return;
        }
    }

    /**
     * 更新取还车信息 更新仁云接口
     *
     * @param deliveryReqVO
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDeliveryCarInfo(DeliveryReqVO deliveryReqVO) throws Exception {
        logger.debug("参数：{}", ToStringBuilder.reflectionToString(deliveryReqVO));
        if (Objects.isNull(deliveryReqVO)) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "参数错误");
        }
        DeliveryReqDTO deliveryReqDTO;
        if (deliveryReqVO.getGetDeliveryReqDTO() != null) {
            deliveryReqDTO = deliveryReqVO.getGetDeliveryReqDTO();
            updateDeliveryCarInfoByUsed(deliveryReqDTO, 1);
        }
        if (deliveryReqVO.getRenterDeliveryReqDTO() != null) {
            deliveryReqDTO = deliveryReqVO.getRenterDeliveryReqDTO();
            updateDeliveryCarInfoByUsed(deliveryReqDTO, 2);
        }
    }

    /**
     * 更新取还车备注信息
     * @param deliveryReqVO
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDeliveryCarRemarkInfo(DeliveryReqDTO deliveryReqVO,Integer type) throws Exception {
        if(Objects.nonNull(deliveryReqVO)){
            RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryMapper.findRenterOrderByrOrderNo(deliveryReqVO.getOrderNo(), type);
            if(Objects.isNull(renterOrderDeliveryEntity))
            {
                return;
            }
            //车主租客备注
            if(StringUtils.isBlank(deliveryReqVO.getOwnerRealGetAddrReamrk())){
                renterOrderDeliveryEntity.setOwnerRealGetReturnRemark(deliveryReqVO.getOwnerRealGetAddrReamrk());
            }
            if(StringUtils.isBlank(deliveryReqVO.getRenterRealGetAddrReamrk())){
                renterOrderDeliveryEntity.setRenterRealGetReturnRemark(deliveryReqVO.getRenterRealGetAddrReamrk());
            }
            renterOrderDeliveryMapper.updateByPrimaryKeySelective(renterOrderDeliveryEntity);
        }
    }

    /**
     * 更新配送订单相关信息
     *
     * @param deliveryReqDTO
     */
    public void updateDeliveryCarInfoByUsed(DeliveryReqDTO deliveryReqDTO, Integer type) {

            RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryMapper.findRenterOrderByrOrderNo(deliveryReqDTO.getOrderNo(), type);
        if (renterOrderDeliveryEntity != null && String.valueOf(UsedDeliveryTypeEnum.NO_USED.getValue()).equals(deliveryReqDTO.getIsUsedGetAndReturnCar())) {
            if (renterOrderDeliveryEntity.getStatus().intValue() != 3 && renterOrderDeliveryEntity.getIsNotifyRenyun() == 1) {
                deliveryCarInfoService.cancelRenYunFlowOrderInfo(new CancelOrderDeliveryVO().setCancelFlowOrderDTO(new CancelFlowOrderDTO().setServicetype(type == 1 ? "take" : "back").setOrdernumber(renterOrderDeliveryEntity.getOrderNo())).setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo()));
            }
        } else{
            //本身就是开启的 修改地址 发送修改记录(仁云记录由修改订单功能触发)
            UpdateOrderDeliveryVO updateOrderDeliveryVO = createDeliveryCarInfoParams(renterOrderDeliveryEntity, deliveryReqDTO, type);
            if(renterOrderDeliveryEntity.getIsNotifyRenyun() == 1) {
                deliveryCarInfoService.updateFlowOrderInfo(updateOrderDeliveryVO);
                deliveryCarInfoService.updateRenYunFlowOrderInfo(updateOrderDeliveryVO.getUpdateFlowOrderDTO());
            }else {
                //从关闭到开启 新增仁云记录数据 修改状态值
                //开始新增数据并发送仁云
                OrderDeliveryVO orderDeliveryVO = new OrderDeliveryVO();
                updateOrderDeliveryVO.getOrderDeliveryDTO().setIsNotifyRenyun(1);
                orderDeliveryVO.setOrderDeliveryDTO(updateOrderDeliveryVO.getOrderDeliveryDTO());
                orderDeliveryVO.setRenterDeliveryAddrDTO(updateOrderDeliveryVO.getRenterDeliveryAddrDTO());
                OrderDeliveryFlowEntity orderDeliveryFlow = deliveryFlowService.selectOrderDeliveryFlowByOrderNo(deliveryReqDTO.getOrderNo(),type == 1 ? "take" : "back");
                orderDeliveryFlow.setServiceTypeInfo(type,orderDeliveryVO.getOrderDeliveryDTO());
                orderDeliveryVO.setOrderDeliveryFlowEntity(orderDeliveryFlow);
                deliveryCarInfoService.insertRenterDeliveryInfoAndDeliveryAddressInfo(null,null, orderDeliveryVO, DeliveryTypeEnum.UPDATE_TYPE.getValue().intValue());
                RenYunFlowOrderDTO renYunFlowOrderDTO = deliveryCarInfoService.createRenYunDTO(orderDeliveryVO.getOrderDeliveryFlowEntity());
                deliveryCarTask.addRenYunFlowOrderInfo(renYunFlowOrderDTO);
            }
        }

        //同步租客订单中取还车标识
        if (type == OrderConstant.ONE) {
            renterOrderService.updateSrvGetAndReturnFlagByRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo(),
                    Integer.valueOf(deliveryReqDTO.getIsUsedGetAndReturnCar()), null);
        } else if (type == OrderConstant.TWO) {
            renterOrderService.updateSrvGetAndReturnFlagByRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo(),
                    null, Integer.valueOf(deliveryReqDTO.getIsUsedGetAndReturnCar()));
        }

    }

    /**
     * 构造更新参数
     *
     * @return
     */
    public UpdateOrderDeliveryVO createDeliveryCarInfoParams(RenterOrderDeliveryEntity renterOrderDeliveryEntity, DeliveryReqDTO deliveryReqDTO, Integer type) {
        OrderDeliveryDTO orderDeliveryDTO = new OrderDeliveryDTO();
        UpdateFlowOrderDTO updateFlowOrderDTO = new UpdateFlowOrderDTO();
        RenterDeliveryAddrDTO renterDeliveryAddrDTO = new RenterDeliveryAddrDTO();
        orderDeliveryDTO.setType(type);
        orderDeliveryDTO.setRenterGetReturnAddr(deliveryReqDTO.getRenterGetReturnAddr());
        orderDeliveryDTO.setRenterGetReturnAddrLat(deliveryReqDTO.getRenterGetReturnLat());
        orderDeliveryDTO.setRenterGetReturnAddrLon(deliveryReqDTO.getRenterGetReturnLng());
        orderDeliveryDTO.setOwnerGetReturnAddr(deliveryReqDTO.getOwnerGetReturnAddr());
        orderDeliveryDTO.setOwnerGetReturnAddrLon(deliveryReqDTO.getOwnerGetReturnLng());
        orderDeliveryDTO.setOwnerGetReturnAddrLat(deliveryReqDTO.getOwnerGetReturnLat());
        orderDeliveryDTO.setOrderNo(renterOrderDeliveryEntity.getOrderNo());
        orderDeliveryDTO.setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo());
        orderDeliveryDTO.setOwnerRealGetReturnRemark(deliveryReqDTO.getOwnerRealGetAddrReamrk());
        orderDeliveryDTO.setRenterRealGetReturnRemark(deliveryReqDTO.getRenterRealGetAddrReamrk());
        renterDeliveryAddrDTO.setExpGetCarAddr(deliveryReqDTO.getRenterGetReturnAddr());
        renterDeliveryAddrDTO.setOrderNo(renterOrderDeliveryEntity.getOrderNo());
        renterDeliveryAddrDTO.setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo());
        renterDeliveryAddrDTO.setExpReturnCarAddr(deliveryReqDTO.getOwnerGetReturnAddr());
        renterDeliveryAddrDTO.setActReturnCarAddr(deliveryReqDTO.getOwnerGetReturnAddr());
        renterDeliveryAddrDTO.setActReturnCarLat(deliveryReqDTO.getOwnerGetReturnLat());
        renterDeliveryAddrDTO.setActReturnCarLon(deliveryReqDTO.getOwnerGetReturnLng());
        renterDeliveryAddrDTO.setActGetCarAddr(deliveryReqDTO.getRenterGetReturnAddr());
        renterDeliveryAddrDTO.setActGetCarLat(deliveryReqDTO.getRenterGetReturnLat());
        renterDeliveryAddrDTO.setActGetCarLon(deliveryReqDTO.getRenterGetReturnLng());
        updateFlowOrderDTO.setOrdernumber(deliveryReqDTO.getOrderNo());
        updateFlowOrderDTO.setServicetype(type == 1 ? "take" : "back");
        updateFlowOrderDTO.setChangetype("ownerAddr");
        updateFlowOrderDTO.setNewpickupcaraddr(deliveryReqDTO.getRenterGetReturnAddr());
        updateFlowOrderDTO.setNewalsocaraddr(deliveryReqDTO.getOwnerGetReturnAddr());
        return UpdateOrderDeliveryVO.builder().orderDeliveryDTO(orderDeliveryDTO).renterDeliveryAddrDTO(renterDeliveryAddrDTO).updateFlowOrderDTO(updateFlowOrderDTO).build();
    }
}

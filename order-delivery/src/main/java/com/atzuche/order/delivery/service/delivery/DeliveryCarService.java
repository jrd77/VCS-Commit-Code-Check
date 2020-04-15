package com.atzuche.order.delivery.service.delivery;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.delivery.common.DeliveryCarTask;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OrderDeliveryFlowEntity;
import com.atzuche.order.delivery.entity.RenterDeliveryAddrEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.*;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.service.OrderDeliveryFlowService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.CodeUtils;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.utils.DateUtils;
import com.atzuche.order.delivery.vo.delivery.*;
import com.atzuche.order.delivery.vo.handover.HandoverCarInfoDTO;
import com.atzuche.order.delivery.vo.handover.HandoverCarVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @author 胡春林
 * 配送服务内部服务
 */
@Service
@Slf4j
public class DeliveryCarService {

    @Autowired
    DeliveryCarTask deliveryCarTask;
    @Autowired
    HandoverCarService handoverCarService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    CodeUtils codeUtils;
    @Autowired
    OrderDeliveryFlowService deliveryFlowService;

    /**
     * 添加配送相关信息(是否下单，是否推送仁云)
     */
    public void addFlowOrderInfo(Integer getMinutes, Integer returnMinutes, OrderReqContext orderReqContext) {
        if (null == orderReqContext || Objects.isNull(orderReqContext.getOrderReqVO())) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        int getMinute = getMinutes == null ? 0 : getMinutes;
        int returnMinute = returnMinutes == null ? 0 : returnMinutes;
        log.info("新增配送订单数据------>>>>> 租客地址信息 入参数：【{}】",
                orderReqContext.getOrderReqVO().getSrvGetAddr());
        addRenYunFlowOrderInfo(getMinute, returnMinute, orderReqContext, UserTypeEnum.OWNER_TYPE.getValue().intValue());
        addRenYunFlowOrderInfo(getMinute, returnMinute, orderReqContext, UserTypeEnum.RENTER_TYPE.getValue().intValue());
    }

    /**
     * 添加配送相关信息细节(是否下单，是否推送仁云)
     */
    public void addRenYunFlowOrderInfo(Integer getMinutes, Integer returnMinutes, OrderReqContext orderReqContext, Integer orderType) {
        OrderDeliveryVO orderDeliveryVO = createOrderDeliveryParams(orderReqContext, orderType);
        if (null == orderDeliveryVO) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        insertRenterDeliveryInfoAndDeliveryAddressInfo(getMinutes, returnMinutes, orderDeliveryVO, DeliveryTypeEnum.ADD_TYPE.getValue().intValue());
        log.info(orderDeliveryVO.getOrderDeliveryDTO().toString());
    }


    /**
     * 更新车辆数据到仁云
     * @param getMinutes
     * @param returnMinutes
     * @param orderReqContext
     * @param orderType
     */
    public void updateRenYunFlowOrderCarInfo(Integer getMinutes, Integer returnMinutes, OrderReqContext orderReqContext, Integer orderType)
    {
        OrderDeliveryVO orderDeliveryVO = createOrderDeliveryParams(orderReqContext, orderType);
        if (null == orderDeliveryVO) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        //开始取消仁云订单数据
        Future<Boolean> result = cancelRenYunFlowOrderInfo(new CancelOrderDeliveryVO().setRenterOrderNo(orderDeliveryVO.getOrderDeliveryDTO().getRenterOrderNo())
                .setCancelFlowOrderDTO(new CancelFlowOrderDTO().setOrdernumber(orderDeliveryVO.getOrderDeliveryDTO().getOrderNo()).setServicetype(orderDeliveryVO.getOrderDeliveryFlowEntity().getServiceType())));
        if (result.isDone()) {
            //开始新增数据并发送仁云
            //insertRenterDeliveryInfoAndDeliveryAddressInfo(getMinutes, returnMinutes, orderDeliveryVO, DeliveryTypeEnum.UPDATE_TYPE.getValue().intValue());
            RenYunFlowOrderDTO renYunFlowOrderDTO = createRenYunDTO(orderDeliveryVO.getOrderDeliveryFlowEntity());
            deliveryCarTask.addRenYunFlowOrderInfo(renYunFlowOrderDTO);
        }
    }


    /**
     * 发送配送订单到仁云 提供给外层回调
     * @param renterOrderNo
     */
    public void sendDataMessageToRenYun(String renterOrderNo) {
        List<RenterOrderDeliveryEntity> renterOrderDeliveryEntities = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
        if (CollectionUtils.isEmpty(renterOrderDeliveryEntities)) {
            //不抛异常，直接return
            log.info("没有找到当前子订单的仁云配送订单信息：renterOrderNo：{}", renterOrderNo);
            return;
        }
        for (RenterOrderDeliveryEntity renterOrderDeliveryEntity : renterOrderDeliveryEntities) {
            if (renterOrderDeliveryEntity.getIsNotifyRenyun().intValue() == 1) {
                OrderDeliveryFlowEntity orderDeliveryFlowEntity = deliveryFlowService.selectOrderDeliveryFlowByOrderNo(renterOrderDeliveryEntity.getOrderNo(), renterOrderDeliveryEntity.getType().intValue() == 1 ? "take" : "back");
                if (Objects.isNull(orderDeliveryFlowEntity)) {
                    continue;
                }
                log.info("开始组装仁云数据进行发送----->>>params:[{}]", orderDeliveryFlowEntity.toString());
                RenYunFlowOrderDTO renYunFlowOrderDTO = createRenYunDTO(orderDeliveryFlowEntity);
                if (Objects.isNull(renYunFlowOrderDTO)) {
                    continue;
                }
                deliveryCarTask.addRenYunFlowOrderInfo(renYunFlowOrderDTO);
            }

        }
    }

    /**
     * 更新配送订单
     */
    public void updateFlowOrderInfo(UpdateOrderDeliveryVO updateFlowOrderVO) {
        if (null == updateFlowOrderVO) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        OrderDeliveryVO orderDeliveryVO = new OrderDeliveryVO();
        orderDeliveryVO.setOrderDeliveryDTO(updateFlowOrderVO.getOrderDeliveryDTO());
        orderDeliveryVO.setRenterDeliveryAddrDTO(updateFlowOrderVO.getRenterDeliveryAddrDTO());
        insertRenterDeliveryInfoAndDeliveryAddressInfo(null, null, orderDeliveryVO, DeliveryTypeEnum.UPDATE_TYPE.getValue().intValue());
    }

    /**
     * 更新配送订单到仁云流程系统
     */
    public void updateRenYunFlowOrderInfo(UpdateFlowOrderDTO updateFlowOrderDTO) {
        if (null == updateFlowOrderDTO) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        deliveryCarTask.updateRenYunFlowOrderInfo(updateFlowOrderDTO);
    }

    /**
     * 取消配送订单到仁云流程系统
     */
    @Transactional(rollbackFor = Exception.class)
    public Future<Boolean> cancelRenYunFlowOrderInfo(CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        if (null == cancelOrderDeliveryVO || cancelOrderDeliveryVO.getCancelFlowOrderDTO() == null || StringUtils.isBlank(cancelOrderDeliveryVO.getRenterOrderNo())) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        int serviceType;
        if (cancelOrderDeliveryVO.getCancelFlowOrderDTO().getServicetype().equals("all")) {
            cancelOrderDeliveryVO.getCancelFlowOrderDTO().setServicetype(ServiceTypeEnum.TAKE_TYPE.getValue());
            addHandoverInfo(cancelOrderDeliveryVO.getRenterOrderNo(), 1,cancelOrderDeliveryVO);
            deliveryCarTask.cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo(), 1,cancelOrderDeliveryVO);
            cancelOrderDeliveryVO.getCancelFlowOrderDTO().setServicetype(ServiceTypeEnum.BACK_TYPE.getValue());
            addHandoverInfo(cancelOrderDeliveryVO.getRenterOrderNo(), 2,cancelOrderDeliveryVO);
            deliveryCarTask.cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo(), 2,cancelOrderDeliveryVO);
        } else {
            serviceType = cancelOrderDeliveryVO.getCancelFlowOrderDTO().getServicetype().equals(ServiceTypeEnum.TAKE_TYPE.getValue()) ? 1 : 2;
            addHandoverInfo(cancelOrderDeliveryVO.getRenterOrderNo(), serviceType,cancelOrderDeliveryVO);
           return deliveryCarTask.cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo(), serviceType,cancelOrderDeliveryVO);
        }
        return null;
    }

    /**
     * 插入配送地址/配送订单信息
     * @param orderDeliveryVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertRenterDeliveryInfoAndDeliveryAddressInfo(Integer getMinutes, Integer returnMinutes, OrderDeliveryVO orderDeliveryVO, Integer type) {

        if (Objects.nonNull(orderDeliveryVO.getOrderDeliveryDTO())) {
            RenterOrderDeliveryEntity orderDeliveryEntity = new RenterOrderDeliveryEntity();
            BeanUtils.copyProperties(orderDeliveryVO.getOrderDeliveryDTO(), orderDeliveryEntity);
            if (type == DeliveryTypeEnum.ADD_TYPE.getValue().intValue()) {
                RenterOrderDeliveryEntity lastOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(orderDeliveryEntity.getOrderNo(), orderDeliveryEntity.getType());
                if (Objects.nonNull(lastOrderDeliveryEntity)) {
                    lastOrderDeliveryEntity.setIsDelete(1);
                    renterOrderDeliveryService.updateDeliveryByPrimaryKey(lastOrderDeliveryEntity);
                }
                orderDeliveryEntity.setOrderNoDelivery(codeUtils.createDeliveryNumber());
                orderDeliveryEntity.setAheadOrDelayTimeInfo(getMinutes, returnMinutes, orderDeliveryVO);
                orderDeliveryEntity.setStatus(1);
                renterOrderDeliveryService.insert(orderDeliveryEntity);
                addHandoverCarInfo(orderDeliveryEntity, getMinutes, returnMinutes, UserTypeEnum.RENTER_TYPE.getValue().intValue());
                addHandoverCarInfo(orderDeliveryEntity, getMinutes, returnMinutes, UserTypeEnum.OWNER_TYPE.getValue().intValue());
            } else {
                RenterOrderDeliveryEntity lastOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(orderDeliveryEntity.getOrderNo(), orderDeliveryEntity.getType());
                if (null == lastOrderDeliveryEntity) {
                    throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_MOUDLE_ERROR.getValue(), "没有找到最近的一笔配送订单记录");
                }
                lastOrderDeliveryEntity.setIsDelete(1);
                renterOrderDeliveryService.updateDeliveryByPrimaryKey(lastOrderDeliveryEntity);
                //时间
                if (Objects.isNull(orderDeliveryEntity.getRentTime()) || Objects.isNull(orderDeliveryEntity.getRevertTime())) {
                    orderDeliveryEntity.setRentTime(lastOrderDeliveryEntity.getRentTime());
                    orderDeliveryEntity.setRevertTime(lastOrderDeliveryEntity.getRevertTime());
                }
                CommonUtil.copyPropertiesIgnoreNull(orderDeliveryEntity, lastOrderDeliveryEntity);
                lastOrderDeliveryEntity.setIsDelete(0);
                lastOrderDeliveryEntity.setStatus(2);
                renterOrderDeliveryService.insert(lastOrderDeliveryEntity);
                addHandoverCarInfo(lastOrderDeliveryEntity, 0, 0, UserTypeEnum.RENTER_TYPE.getValue().intValue());
                addHandoverCarInfo(lastOrderDeliveryEntity, 0, 0, UserTypeEnum.OWNER_TYPE.getValue().intValue());
            }
        }
        insertOrUpdateRenterDeliveryAddressInfo(orderDeliveryVO);
    }

    /**
     * 新增地址信息和仁云信息
     * @param orderDeliveryVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateRenterDeliveryAddressInfo(OrderDeliveryVO orderDeliveryVO) {

        if (orderDeliveryVO.getRenterDeliveryAddrDTO() != null) {
            RenterDeliveryAddrEntity deliveryAddrEntity = new RenterDeliveryAddrEntity();
            RenterDeliveryAddrEntity renterDeliveryAddrEntity = renterOrderDeliveryService.selectAddrByOrderNo(orderDeliveryVO.getRenterDeliveryAddrDTO().getOrderNo());
            if (null == renterDeliveryAddrEntity) {
                BeanUtils.copyProperties(orderDeliveryVO.getRenterDeliveryAddrDTO(), deliveryAddrEntity);
                renterOrderDeliveryService.insertDeliveryAddr(deliveryAddrEntity);
            } else {
                CommonUtil.copyPropertiesIgnoreNull(orderDeliveryVO.getRenterDeliveryAddrDTO(), renterDeliveryAddrEntity);
                renterOrderDeliveryService.updateDeliveryAddrByPrimaryKey(renterDeliveryAddrEntity);
            }
        }
        if (orderDeliveryVO.getOrderDeliveryFlowEntity() != null) {
            insertRenYunFlowOrderDTO(orderDeliveryVO.getOrderDeliveryFlowEntity());
        }
    }

    /**
     * 新增交接车信息
     * @param orderDeliveryEntity
     * @param getMinutes
     * @param returnMinutes
     * @param userType
     */
    public void addHandoverCarInfo(RenterOrderDeliveryEntity orderDeliveryEntity, Integer getMinutes, Integer returnMinutes, Integer userType) {

        //提前或延后时间(取车:提前时间, 还车：延后时间
        HandoverCarInfoDTO handoverCarInfoDTO = new HandoverCarInfoDTO();
        HandoverCarVO handoverCarVO = new HandoverCarVO();
        handoverCarInfoDTO.setCreateOp("");
        handoverCarInfoDTO.setOrderNo(orderDeliveryEntity.getOrderNo());
        handoverCarInfoDTO.setRenterOrderNo(orderDeliveryEntity.getRenterOrderNo());
        handoverCarInfoDTO.setAheadTimeAndType(getMinutes,returnMinutes,orderDeliveryEntity);
        handoverCarInfoDTO.setRealReturnAddr(orderDeliveryEntity.getRenterGetReturnAddr());
        handoverCarInfoDTO.setRealReturnAddrLat(orderDeliveryEntity.getRenterGetReturnAddrLat());
        handoverCarInfoDTO.setRealReturnAddrLon(orderDeliveryEntity.getRenterGetReturnAddrLon());
        handoverCarVO.setHandoverCarInfoDTO(handoverCarInfoDTO);
        handoverCarService.addHandoverCarInfo(handoverCarVO, userType);
    }

    /**
     * 构造配送订单数据
     * @param orderReqContext
     * @return
     */
    public OrderDeliveryVO createOrderDeliveryParams(OrderReqContext orderReqContext, Integer orderType) {
        if (null == orderReqContext) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        OrderDeliveryVO orderDeliveryVO = new OrderDeliveryVO();
        RenterDeliveryAddrDTO renterDeliveryAddrDTO = new RenterDeliveryAddrDTO();
        OrderReqVO orderReqVO = orderReqContext.getOrderReqVO();
        OrderDeliveryDTO orderDeliveryDTO = new OrderDeliveryDTO();
        RenterMemberDTO renterMemberDTO = orderReqContext.getRenterMemberDto();
        OwnerMemberDTO ownerMemberDTO = orderReqContext.getOwnerMemberDto();
        OrderDeliveryFlowEntity orderDeliveryFlowEntity = new OrderDeliveryFlowEntity();
        RenterGoodsDetailDTO renterGoodsDetailDTO = orderReqContext.getRenterGoodsDetailDto();
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = orderReqContext.getOwnerGoodsDetailDto();
        renterDeliveryAddrDTO.setParamsTypeValue(orderType, renterMemberDTO);
        boolean isGetCarUsed = orderType == 1 && orderReqVO.getSrvGetFlag().intValue() == UsedDeliveryTypeEnum.NO_USED.getValue().intValue();
        boolean isReturnUsed = orderType == 2 && orderReqVO.getSrvReturnFlag().intValue() == UsedDeliveryTypeEnum.NO_USED.getValue().intValue();
        if (isGetCarUsed || isReturnUsed) {
            String carShowAddr = renterGoodsDetailDTO.getCarShowAddr() == null ? renterGoodsDetailDTO.getCarRealAddr() : renterGoodsDetailDTO.getCarShowAddr();
            String carShowLat = renterGoodsDetailDTO.getCarShowLat() == null ? renterGoodsDetailDTO.getCarRealLat() : renterGoodsDetailDTO.getCarShowLat();
            String carShowLng = renterGoodsDetailDTO.getCarShowLon() == null ? renterGoodsDetailDTO.getCarRealLon() : renterGoodsDetailDTO.getCarShowLon();
            if (StringUtils.isNotBlank(renterGoodsDetailDTO.getCarShowAddr())) {
                renterDeliveryAddrDTO = RenterDeliveryAddrDTO.builder().actGetCarAddr(carShowAddr).actGetCarLat(carShowLat).actGetCarLon(carShowLng).actReturnCarAddr(carShowAddr)
                        .actReturnCarLat(carShowLat).actReturnCarLon(carShowLng).expGetCarAddr(carShowAddr).expGetCarLat(carShowLat).expGetCarLon(carShowLng).expReturnCarAddr(carShowAddr)
                        .expReturnCarLat(carShowLat).expReturnCarLon(carShowLng).orderNo(renterGoodsDetailDTO.getOrderNo()).renterOrderNo(renterGoodsDetailDTO.getRenterOrderNo()).createTime(LocalDateTime.now()).createOp("").build();
            }
        } else {
            renterDeliveryAddrDTO = RenterDeliveryAddrDTO.builder().actGetCarAddr(orderReqVO.getSrvGetAddr()).actGetCarLat(orderReqVO.getSrvGetLat()).actGetCarLon(orderReqVO.getSrvGetLon()).actReturnCarAddr(orderReqVO.getSrvReturnAddr())
                    .actReturnCarLat(orderReqVO.getSrvReturnLat()).actReturnCarLon(orderReqVO.getSrvReturnLon()).expGetCarAddr(orderReqVO.getSrvGetAddr()).expGetCarLat(orderReqVO.getSrvGetLat()).expGetCarLon(orderReqVO.getSrvGetLon()).expReturnCarAddr(orderReqVO.getSrvReturnAddr())
                    .expReturnCarLat(orderReqVO.getSrvReturnLat()).expReturnCarLon(orderReqVO.getSrvReturnLon()).orderNo(renterGoodsDetailDTO.getOrderNo()).renterOrderNo(renterGoodsDetailDTO.getRenterOrderNo()).createTime(LocalDateTime.now()).createOp("").build();
        }
        renterDeliveryAddrDTO.setIsDelete(0);
        /**组装配送订单信息**/
        orderDeliveryDTO.setCityCode(orderReqVO.getCityCode());
        orderDeliveryDTO.setCityName(orderReqVO.getCityName());
        orderDeliveryDTO.setCreateOp("");
        orderDeliveryDTO.setRenterName(renterMemberDTO.getRealName());
        orderDeliveryDTO.setRenterPhone(renterMemberDTO.getPhone());
        orderDeliveryDTO.setOrderNo(renterGoodsDetailDTO.getOrderNo());
        orderDeliveryDTO.setRenterDealCount(renterMemberDTO.getOrderSuccessCount());
        orderDeliveryDTO.setIsNotifyRenyun(UsedDeliveryTypeEnum.NO_USED.getValue().intValue());
        orderDeliveryDTO.setOwnerGetReturnAddr(ownerGoodsDetailDTO.getCarRealAddr());
        orderDeliveryDTO.setOwnerGetReturnAddrLat(ownerGoodsDetailDTO.getCarRealLat());
        orderDeliveryDTO.setOwnerGetReturnAddrLon(ownerGoodsDetailDTO.getCarRealLon());
        orderDeliveryDTO.setOwnerName(ownerMemberDTO.getRealName());
        orderDeliveryDTO.setOwnerPhone(ownerMemberDTO.getPhone());
        orderDeliveryDTO.setRenterPhone(renterMemberDTO.getPhone());
        orderDeliveryDTO.setRenterOrderNo(renterGoodsDetailDTO.getRenterOrderNo());
        orderDeliveryDTO.setRentTime(orderReqContext.getOrderReqVO().getRentTime());
        orderDeliveryDTO.setRevertTime(orderReqContext.getOrderReqVO().getRevertTime());
        orderDeliveryDTO.setType(orderType);
        orderDeliveryDTO.setParamsTypeValue(orderReqVO, orderType,renterGoodsDetailDTO);
        //如果没有使用取还车服务则不需要flow数据 暂加入
        orderDeliveryFlowEntity.setRenterOrderNo(renterGoodsDetailDTO.getRenterOrderNo());
        orderDeliveryFlowEntity.setOrderNo(renterGoodsDetailDTO.getOrderNo());
        orderDeliveryFlowEntity.setServiceTypeInfo(orderType, orderDeliveryDTO);
        orderDeliveryFlowEntity.setTermTime(orderDeliveryDTO.getRentTime());
        orderDeliveryFlowEntity.setReturnTime(orderDeliveryDTO.getRevertTime());
        orderDeliveryFlowEntity.setBeforeTime(DateUtils.formate(orderDeliveryDTO.getRentTime(), DateUtils.DATE_DEFAUTE_1));
        orderDeliveryFlowEntity.setAfterTime(DateUtils.formate(orderDeliveryDTO.getRevertTime(), DateUtils.DATE_DEFAUTE_1));
        orderDeliveryFlowEntity.setCarNo(String.valueOf(renterGoodsDetailDTO.getCarPlateNum()));
        orderDeliveryFlowEntity.setVehicleModel(renterGoodsDetailDTO.getCarBrandTxt());
        orderDeliveryFlowEntity.setVehicleType(renterGoodsDetailDTO.getCarTypeTxt());
        orderDeliveryFlowEntity.setDeliveryCarCity(orderReqVO.getCityName());
        orderDeliveryFlowEntity.setDefaultPickupCarAddr(orderDeliveryDTO.getRenterGetReturnAddr());
        orderDeliveryFlowEntity.setOwnerName(ownerMemberDTO.getRealName());
        orderDeliveryFlowEntity.setOwnerPhone(ownerMemberDTO.getPhone());
        orderDeliveryFlowEntity.setSuccessOrdeNumber(String.valueOf(ownerMemberDTO.getOrderSuccessCount()));
        orderDeliveryFlowEntity.setTenantName(renterMemberDTO.getRealName());
        orderDeliveryFlowEntity.setTenantPhone(renterMemberDTO.getPhone());
        orderDeliveryFlowEntity.setTenantTurnoverNo(String.valueOf(renterMemberDTO.getOrderSuccessCount()));
        orderDeliveryFlowEntity.setOwnerType(Integer.valueOf(ownerGoodsDetailDTO.getType()));
        orderDeliveryFlowEntity.setSceneName(orderReqVO.getSceneCode());
        orderDeliveryFlowEntity.setIsDelete(0);
        if (StringUtils.isNotBlank(orderReqVO.getOrderCategory()) && "1".equals(orderReqVO.getOrderCategory())) {
            orderDeliveryFlowEntity.setOrderType("0");
        }
        orderDeliveryFlowEntity.setCreateTime(LocalDateTime.now());
        orderDeliveryFlowEntity.setUpdateTime(LocalDateTime.now());
        orderDeliveryFlowEntity.setDisplacement(String.valueOf(ownerGoodsDetailDTO.getCarCylinderCapacity()));
        orderDeliveryFlowEntity.setSource(orderReqVO.getSource());
        orderDeliveryFlowEntity.setDayMileage(String.valueOf(renterGoodsDetailDTO.getCarDayMileage()));
        orderDeliveryFlowEntity.setTankCapacity(String.valueOf(renterGoodsDetailDTO.getCarOilVolume()));
        orderDeliveryFlowEntity.setOwnerGetAddr(renterGoodsDetailDTO.getCarRealAddr());
        orderDeliveryFlowEntity.setOwnerReturnAddr(renterGoodsDetailDTO.getCarRealAddr());
        orderDeliveryVO.setOrderDeliveryFlowEntity(orderDeliveryFlowEntity);
        orderDeliveryVO.setOrderDeliveryDTO(orderDeliveryDTO);
        orderDeliveryVO.setRenterDeliveryAddrDTO(renterDeliveryAddrDTO);
        return orderDeliveryVO;
    }

    /**
     * 新增仁云数据
     * @param orderDeliveryFlowEntity
     */
    public void insertRenYunFlowOrderDTO(OrderDeliveryFlowEntity orderDeliveryFlowEntity) {

        OrderDeliveryFlowEntity orderDeliveryFlow = deliveryFlowService.selectOrderDeliveryFlowByOrderNo(orderDeliveryFlowEntity.getOrderNo(),orderDeliveryFlowEntity.getServiceType());
        if(Objects.nonNull(orderDeliveryFlow))
        {
            BeanUtils.copyProperties(orderDeliveryFlowEntity,orderDeliveryFlow);
            deliveryFlowService.updateOrderDeliveryFlow(orderDeliveryFlow);
            return;
        }
        deliveryFlowService.insertOrderDeliveryFlow(orderDeliveryFlowEntity);
    }

    /**
     * 构造仁云数据
     * @param orderDeliveryFlowEntity
     */
    public RenYunFlowOrderDTO createRenYunDTO(OrderDeliveryFlowEntity orderDeliveryFlowEntity) {
        RenYunFlowOrderDTO renYunFlowOrderDTO = new RenYunFlowOrderDTO();
        renYunFlowOrderDTO.setOrderType(orderDeliveryFlowEntity.getOrderType());
        renYunFlowOrderDTO.setOrdernumber(orderDeliveryFlowEntity.getOrderNo());
        renYunFlowOrderDTO.setServiceTypeInfo(orderDeliveryFlowEntity);
        renYunFlowOrderDTO.setTermtime(DateUtils.formate(orderDeliveryFlowEntity.getTermTime(), DateUtils.DATE_DEFAUTE_4));
        renYunFlowOrderDTO.setReturntime(DateUtils.formate(orderDeliveryFlowEntity.getReturnTime(), DateUtils.DATE_DEFAUTE_4));
        renYunFlowOrderDTO.setBeforeTime(DateUtils.formate(orderDeliveryFlowEntity.getTermTime(), DateUtils.DATE_DEFAUTE_1));
        renYunFlowOrderDTO.setAfterTime(DateUtils.formate(orderDeliveryFlowEntity.getReturnTime(), DateUtils.DATE_DEFAUTE_1));
        renYunFlowOrderDTO.setVehicletype(orderDeliveryFlowEntity.getVehicleType());
        renYunFlowOrderDTO.setDeliverycarcity(orderDeliveryFlowEntity.getDeliveryCarCity());
        renYunFlowOrderDTO.setDefaultpickupcaraddr(orderDeliveryFlowEntity.getDefaultPickupCarAddr());
        renYunFlowOrderDTO.setOwnername(orderDeliveryFlowEntity.getOwnerName());
        renYunFlowOrderDTO.setOwnerphone(orderDeliveryFlowEntity.getOwnerPhone());
        renYunFlowOrderDTO.setSuccessordenumber(String.valueOf(orderDeliveryFlowEntity.getSuccessOrdeNumber()));
        renYunFlowOrderDTO.setTenantname(orderDeliveryFlowEntity.getTenantName());
        renYunFlowOrderDTO.setTenantphone(orderDeliveryFlowEntity.getTenantPhone());
        renYunFlowOrderDTO.setTenantturnoverno(String.valueOf(orderDeliveryFlowEntity.getTenantTurnoverNo()));
        renYunFlowOrderDTO.setOwnerType(String.valueOf(orderDeliveryFlowEntity.getOwnerType()));
        renYunFlowOrderDTO.setSceneName(orderDeliveryFlowEntity.getSceneName());
        renYunFlowOrderDTO.setDisplacement(String.valueOf(orderDeliveryFlowEntity.getDisplacement()));
        renYunFlowOrderDTO.setSource(orderDeliveryFlowEntity.getSource());
        renYunFlowOrderDTO.setCarno(orderDeliveryFlowEntity.getCarNo());
        renYunFlowOrderDTO.setVehicletype(orderDeliveryFlowEntity.getVehicleType());
        renYunFlowOrderDTO.setVehiclemodel(orderDeliveryFlowEntity.getVehicleModel());
        renYunFlowOrderDTO.setDayMileage(orderDeliveryFlowEntity.getDayMileage());
        renYunFlowOrderDTO.setTankCapacity(orderDeliveryFlowEntity.getTankCapacity());
        renYunFlowOrderDTO.setOwnerReturnAddr(orderDeliveryFlowEntity.getOwnerReturnAddr());
        renYunFlowOrderDTO.setOwnerGetAddr(orderDeliveryFlowEntity.getOwnerGetAddr());
        return renYunFlowOrderDTO;
    }

    /**
     * 新增更新handover
     * @param renterOrderNo
     * @param serviceType
     * @param cancelOrderDeliveryVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void addHandoverInfo(String renterOrderNo, Integer serviceType,CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        RenterOrderDeliveryEntity orderDelivery = renterOrderDeliveryService.findRenterOrderByrOrderNo(cancelOrderDeliveryVO.getCancelFlowOrderDTO().getOrdernumber(), serviceType);

        if (null == orderDelivery) {
            log.info("没有找到该配送订单信息，renterOrderNo：{}",renterOrderNo);
            return;
        }
        orderDelivery.setStatus(3);
        orderDelivery.setIsNotifyRenyun(0);
        orderDelivery.setRenterOrderNo(renterOrderNo);
        renterOrderDeliveryService.updateDeliveryByPrimaryKey(orderDelivery);
        addHandoverCarInfo(orderDelivery, 0, 0, UserTypeEnum.RENTER_TYPE.getValue().intValue());
        addHandoverCarInfo(orderDelivery, 0, 0, UserTypeEnum.OWNER_TYPE.getValue().intValue());
    }

}
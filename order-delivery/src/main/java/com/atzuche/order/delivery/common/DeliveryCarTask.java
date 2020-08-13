package com.atzuche.order.delivery.common;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.car.CarRentTimeRangeReqDTO;
import com.atzuche.order.car.CarRentalTimeProxyService;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.SectionDeliveryUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CarRentTimeRangeDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.SectionParamDTO;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.vo.res.SectionDeliveryResultVO;
import com.atzuche.order.commons.vo.res.SectionDeliveryVO;
import com.atzuche.order.delivery.config.DeliveryRenYunConfig;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryMode;
import com.atzuche.order.delivery.enums.OrderScenesSourceEnum;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.service.RenterOrderDeliveryModeService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.service.delivery.RenYunDeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.*;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.car.api.model.enums.EngineTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author 胡春林
 * 执行流程
 */
@Service
@Slf4j
public class DeliveryCarTask {

    @Autowired
    RenYunDeliveryCarService renyunDeliveryCarService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    DeliveryRenYunConfig deliveryRenYunConfig;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private RenterOrderDeliveryModeService renterOrderDeliveryModeService;
    @Autowired
    private RenterOrderCostService renterOrderCostService;
    @Autowired
    private DeliveryCarInfoPriceService deliveryCarInfoPriceService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private DeliveryAsyncProxy deliveryAsyncProxy;
    @Autowired
    private CarRentalTimeProxyService carRentalTimeProxyService;

    /**
     * 添加订单到仁云流程系统
     *
     * @param renYunFlowOrderDTO   通知仁云信息
     * @param noticeRenYunFlag     通知仁云标识:0-不通知 1-通知(取还车服务) 2-通知(自取自还并使用虚拟地址)
     * @param renterGoodsDetailDTO 租客商品信息
     */
    public void addRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderDTO, int noticeRenYunFlag, RenterGoodsDetailDTO renterGoodsDetailDTO) {
        log.info("DeliveryCarTask.addRenYunFlowOrderInfo. param is,renYunFlowOrderDTO:[{}], noticeRenYunFlag:[{}], " +
                "renterGoodsDetailDTO:[{}]", JSON.toJSONString(renYunFlowOrderDTO), noticeRenYunFlag, JSON.toJSONString(renterGoodsDetailDTO));
        // 获取有效的租客子订单
        OrderEntity orderEntity = orderService.getOrderEntity(renYunFlowOrderDTO.getOrdernumber());
        // 追加参数
        renYunFlowOrderDTO = appendRenYunFlowOrderDTO(renYunFlowOrderDTO, renterGoodsDetailDTO, orderEntity);
        // 针对自取自还订单特殊处理
        if (OrderConstant.TWO == noticeRenYunFlag) {
            noServiceSpecialHandle(renYunFlowOrderDTO, renterGoodsDetailDTO, orderEntity.getCityCode());
        }
        deliveryAsyncProxy.addRenYunFlowOrderInfoproxy(renYunFlowOrderDTO);
    }


    /**
     * 更新订单到仁云流程系统
     *
     * @param updateFlowOrderDTO 通知仁云数据
     */
    public void updateRenYunFlowOrderInfo(UpdateFlowOrderDTO updateFlowOrderDTO) {
        // 获取订单信息
        OrderEntity orderEntity = orderService.getOrderEntity(updateFlowOrderDTO.getOrdernumber());
        // 获取有效的租客子订单
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(updateFlowOrderDTO.getOrdernumber());
        // 获取租客商品信息
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo(), false);

        // 追加参数
        updateFlowOrderDTO = appendUpdateFlowOrderDTO(updateFlowOrderDTO, renterOrderEntity);
        //添加参数化
        appendRenyunUpdateFlowOrderParam(updateFlowOrderDTO, renterGoodsDetail, orderEntity);

        int noticeRenYunFlag = getNoticeRenYunFlag(null, Integer.valueOf(updateFlowOrderDTO.getPickUpCarSrvFlag()), renterGoodsDetail.getCarAddrIndex());
        if (noticeRenYunFlag > OrderConstant.ZERO) {
            // 自取自还并使用虚拟地址特殊处理
            if (noticeRenYunFlag == OrderConstant.TWO) {
                noServiceSpecialHandle(updateFlowOrderDTO, renterGoodsDetail, orderEntity.getCityCode());
            }
            deliveryAsyncProxy.updateRenYunFlowOrderInfoProxy(updateFlowOrderDTO);
        }
    }


    /**
     * 实时更新订单信息到流程系统
     */
    @Async
    public void changeRenYunFlowOrderInfo(ChangeOrderInfoDTO changeOrderInfoDTO) {
        renyunDeliveryCarService.changeRenYunFlowOrderInfo(changeOrderInfoDTO);
    }

    /**
     * 取消订单到仁云流程系统
     */
    @Async
    public Future<Boolean> cancelRenYunFlowOrderInfo(CancelFlowOrderDTO cancelFlowOrderDTO) {

        String result = renyunDeliveryCarService.cancelRenYunFlowOrderInfo(cancelFlowOrderDTO);
        if (StringUtils.isBlank(result)) {
            deliveryAsyncProxy.sendMailByType(cancelFlowOrderDTO.getServicetype(), DeliveryConstants.CANCEL_TYPE, deliveryRenYunConfig.CANCEL_FLOW_ORDER, cancelFlowOrderDTO.getOrdernumber());
        }
        return new AsyncResult(true);
    }

    /**
     * 取消配送订单
     *
     * @param renterOrderNo 租客订单号
     * @param serviceType   服务类型
     */
    public Future<Boolean> cancelOrderDelivery(String renterOrderNo, Integer serviceType, CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        cancelOtherDeliveryTypeInfo(renterOrderNo, serviceType, cancelOrderDeliveryVO);
        return cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
    }

    /**
     * 更新另一個配送信息子订单号
     */
    public void cancelOtherDeliveryTypeInfo(String renterOrderNo, Integer serviceType, CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        RenterOrderDeliveryEntity orderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(cancelOrderDeliveryVO.getCancelFlowOrderDTO().getOrdernumber(), serviceType == 1 ? 2 : 1);
        if (null == orderDeliveryEntity) {
            log.info("没有找到该配送订单信息，renterOrderNo：{}", renterOrderNo);
        } else {
            if (!renterOrderNo.equals(orderDeliveryEntity.getRenterOrderNo())) {
                orderDeliveryEntity.setRenterOrderNo(renterOrderNo);
                renterOrderDeliveryService.updateDeliveryByPrimaryKey(orderDeliveryEntity);
            }
        }
    }


    /**
     * 新增仁云追加参数
     *
     * @param renYunFlowOrderDTO   通知仁云数据
     * @param renterGoodsDetailDTO 租客订单商品信息
     * @return RenYunFlowOrderDTO
     */
    public RenYunFlowOrderDTO appendRenYunFlowOrderDTO(RenYunFlowOrderDTO renYunFlowOrderDTO,
                                                       RenterGoodsDetailDTO renterGoodsDetailDTO, OrderEntity orderEntity) {
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(renYunFlowOrderDTO.getOrdernumber());
        renYunFlowOrderDTO.setSsaRisks(renterOrderEntity.getIsAbatement() == null ? "0" : renterOrderEntity.getIsAbatement().toString());
        if ("8".equals(renYunFlowOrderDTO.getOrderType())) {
            // 线上长租订单
            renYunFlowOrderDTO.setBaseInsurFlag("0");
        } else {
            renYunFlowOrderDTO.setBaseInsurFlag("1");
        }
        Integer addDriver = renterOrderEntity.getAddDriver();
        renYunFlowOrderDTO.setExtraDriverFlag(addDriver == null || addDriver == 0 ? "0" : "1");
        renYunFlowOrderDTO.setTyreInsurFlag(renterOrderEntity.getTyreInsurFlag() == null ? "0" : renterOrderEntity.getTyreInsurFlag().toString());
        renYunFlowOrderDTO.setDriverInsurFlag(renterOrderEntity.getDriverInsurFlag() == null ? "0" : renterOrderEntity.getDriverInsurFlag().toString());
        renYunFlowOrderDTO.setCarRegNo(renterGoodsDetailDTO.getCarNo() == null ? "" : String.valueOf(renterGoodsDetailDTO.getCarNo()));
        renYunFlowOrderDTO.setDayUnitPrice(renterGoodsDetailDTO.getCarGuideDayPrice() == null ? "" : String.valueOf(renterGoodsDetailDTO.getCarGuideDayPrice()));
        if (StringUtils.isNotBlank(renYunFlowOrderDTO.getSceneName())) {
            renYunFlowOrderDTO.setSceneName(OrderScenesSourceEnum.getOrderScenesSource(renYunFlowOrderDTO.getSceneName()));
        }
        List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
        if (deliveryList != null && !deliveryList.isEmpty()) {
            deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> deliver));
        }
        if (deliveryMap != null) {
            RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
            RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
            if (srvGetDelivery != null) {
                renYunFlowOrderDTO.setOwnerGetLat(srvGetDelivery.getOwnerGetReturnAddrLat());
                renYunFlowOrderDTO.setOwnerGetLon(srvGetDelivery.getOwnerGetReturnAddrLon());
            }
            if (srvReturnDelivery != null) {
                renYunFlowOrderDTO.setOwnerReturnLat(srvReturnDelivery.getOwnerGetReturnAddrLat());
                renYunFlowOrderDTO.setOwnerReturnLon(srvReturnDelivery.getOwnerGetReturnAddrLon());
            }
        }
        // 获取区间配送信息
        SectionDeliveryResultVO deliveryResult = getSectionDeliveryResultVO(renterOrderEntity, deliveryList);
        renYunFlowOrderDTO = convertDataAdd(renYunFlowOrderDTO, deliveryResult);
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(renterOrderEntity.getOrderNo(), renterOrderEntity.getRenterOrderNo());
        renYunFlowOrderDTO.setRentAmt(renterOrderCostEntity.getRentCarAmount() == null ? "" : String.valueOf(Math.abs(renterOrderCostEntity.getRentCarAmount())));
        log.info("推送仁云子订单号renterOrderNo={}", renterOrderEntity.getRenterOrderNo());

        Double oilPriceByCityCodeAndType = deliveryCarInfoPriceService.getOilPriceByCityCodeAndType(Integer.valueOf(orderEntity.getCityCode()), renterGoodsDetailDTO.getCarEngineType());
        renYunFlowOrderDTO.setOilPrice(oilPriceByCityCodeAndType == null ? "0" : String.valueOf(oilPriceByCityCodeAndType));

        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(renYunFlowOrderDTO.getOrdernumber());
        renYunFlowOrderDTO.setIsPayDeposit(orderStatusEntity.getDepositPayStatus() == null ? "0" : String.valueOf(orderStatusEntity.getDepositPayStatus()));

        renYunFlowOrderDTO.setEngineType(EngineTypeEnum.getName(renterGoodsDetailDTO.getCarEngineType()));
        return renYunFlowOrderDTO;
    }


    public RenYunFlowOrderDTO convertDataAdd(RenYunFlowOrderDTO renYunFlowOrderDTO, SectionDeliveryResultVO deliveryResult) {
        if (renYunFlowOrderDTO == null) {
            return null;
        }
        if (deliveryResult == null) {
            return renYunFlowOrderDTO;
        }
        Integer distributionMode = deliveryResult.getDistributionMode();
        renYunFlowOrderDTO.setDistributionMode(distributionMode == null ? "0" : String.valueOf(distributionMode));
        if (distributionMode != null && distributionMode == OrderConstant.ONE) {
            return renYunFlowOrderDTO;
        }
        SectionDeliveryVO renter = deliveryResult.getRenterSectionDelivery();
        SectionDeliveryVO owner = deliveryResult.getOwnerSectionDelivery();
        if (renter != null) {
            renYunFlowOrderDTO.setRenterRentTimeStart(renter.getRentTimeStart());
            renYunFlowOrderDTO.setRenterRentTimeEnd(renter.getRentTimeEnd());
            renYunFlowOrderDTO.setRenterRevertTimeStart(renter.getRevertTimeStart());
            renYunFlowOrderDTO.setRenterRevertTimeEnd(renter.getRevertTimeEnd());
            renYunFlowOrderDTO.setRenterProposalGetTime(renter.getDefaultRentTime());
            renYunFlowOrderDTO.setRenterProposalReturnTime(renter.getDefaultRevertTime());
        }
        if (owner != null) {
            renYunFlowOrderDTO.setOwnerRentTimeStart(owner.getRentTimeStart());
            renYunFlowOrderDTO.setOwnerRentTimeEnd(owner.getRentTimeEnd());
            renYunFlowOrderDTO.setOwnerRevertTimeStart(owner.getRevertTimeStart());
            renYunFlowOrderDTO.setOwnerRevertTimeEnd(owner.getRevertTimeEnd());
            renYunFlowOrderDTO.setOwnerProposalGetTime(owner.getDefaultRentTime());
            renYunFlowOrderDTO.setOwnerProposalReturnTime(owner.getDefaultRevertTime());
        }
        return renYunFlowOrderDTO;
    }


    /**
     * 追加修改参数
     *
     * @param updFlow           通知仁云数据
     * @param renterOrderEntity 租客订单信息
     * @return UpdateFlowOrderDTO
     */
    public UpdateFlowOrderDTO appendUpdateFlowOrderDTO(UpdateFlowOrderDTO updFlow, RenterOrderEntity renterOrderEntity) {
        if (updFlow == null) {
            return null;
        }
        if (renterOrderEntity == null) {
            log.error("appendUpdateFlowOrderDTO renterOrderEntity is null orderNo={}", updFlow.getOrdernumber());
            return null;
        }
        List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        // 获取区间配送信息
        SectionDeliveryResultVO deliveryResult = getSectionDeliveryResultVO(renterOrderEntity, deliveryList);
        if (deliveryResult == null) {
            return updFlow;
        }
        Integer distributionMode = deliveryResult.getDistributionMode();
        updFlow.setDistributionMode(distributionMode == null ? "0" : String.valueOf(distributionMode));
        if (distributionMode != null && distributionMode == OrderConstant.ONE) {
            return updFlow;
        }
        SectionDeliveryVO renter = deliveryResult.getRenterSectionDelivery();
        SectionDeliveryVO owner = deliveryResult.getOwnerSectionDelivery();
        if (renter != null) {
            updFlow.setRenterRentTimeStart(renter.getRentTimeStart());
            updFlow.setRenterRentTimeEnd(renter.getRentTimeEnd());
            updFlow.setRenterRevertTimeStart(renter.getRevertTimeStart());
            updFlow.setRenterRevertTimeEnd(renter.getRevertTimeEnd());
            updFlow.setRenterProposalGetTime(renter.getDefaultRentTime());
            updFlow.setRenterProposalReturnTime(renter.getDefaultRevertTime());
        }
        if (owner != null) {
            updFlow.setOwnerRentTimeStart(owner.getRentTimeStart());
            updFlow.setOwnerRentTimeEnd(owner.getRentTimeEnd());
            updFlow.setOwnerRevertTimeStart(owner.getRevertTimeStart());
            updFlow.setOwnerRevertTimeEnd(owner.getRevertTimeEnd());
            updFlow.setOwnerProposalGetTime(owner.getDefaultRentTime());
            updFlow.setOwnerProposalReturnTime(owner.getDefaultRevertTime());
        }
        return updFlow;
    }


    /**
     * 获取区间配送信息
     *
     * @param renterOrderEntity 租客订单信息
     * @return SectionDeliveryResultVO
     */
    public SectionDeliveryResultVO getSectionDeliveryResultVO(RenterOrderEntity renterOrderEntity, List<RenterOrderDeliveryEntity> deliveryList) {
        if (renterOrderEntity == null) {
            log.info("获取区间配送信息getSectionDeliveryResultVO renterOrderEntity is null");
            return null;
        }
        RenterOrderDeliveryMode mode = renterOrderDeliveryModeService.getDeliveryModeByRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        if (mode == null) {
            log.info("获取区间配送信息getSectionDeliveryResultVO RenterOrderDeliveryMode is null");
            return null;
        }
        Integer getCarBeforeTime = 0;
        Integer returnCarAfterTime = 0;
        Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
        if (deliveryList != null && !deliveryList.isEmpty()) {
            deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> deliver));
        }
        if (deliveryMap != null) {
            RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
            RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
            if (srvGetDelivery != null) {
                getCarBeforeTime = srvGetDelivery.getAheadOrDelayTime();
            }
            if (srvReturnDelivery != null) {
                returnCarAfterTime = srvReturnDelivery.getAheadOrDelayTime();
            }
        }
        SectionParamDTO sectionParam = new SectionParamDTO();
        BeanUtils.copyProperties(mode, sectionParam);
        sectionParam.setRentTime(renterOrderEntity.getExpRentTime());
        sectionParam.setRevertTime(renterOrderEntity.getExpRevertTime());
        sectionParam.setGetCarBeforeTime(getCarBeforeTime);
        sectionParam.setReturnCarAfterTime(returnCarAfterTime);
        SectionDeliveryResultVO sectionDeliveryResultVO = SectionDeliveryUtils.getSectionDeliveryResultVO(sectionParam, DateUtils.FORMAT_STR_RENYUN);
        if (sectionDeliveryResultVO != null) {
            sectionDeliveryResultVO.setDistributionMode(mode.getDistributionMode());
            if (mode.getRenterProposalGetTime() != null && sectionDeliveryResultVO.getRenterSectionDelivery() != null) {
                sectionDeliveryResultVO.getRenterSectionDelivery().setDefaultRentTime(DateUtils.formate(mode.getRenterProposalGetTime(), DateUtils.FORMAT_STR_RENYUN));
            }
            if (mode.getRenterProposalReturnTime() != null && sectionDeliveryResultVO.getRenterSectionDelivery() != null) {
                sectionDeliveryResultVO.getRenterSectionDelivery().setDefaultRevertTime(DateUtils.formate(mode.getRenterProposalReturnTime(), DateUtils.FORMAT_STR_RENYUN));
            }
            if (mode.getOwnerProposalGetTime() != null && sectionDeliveryResultVO.getOwnerSectionDelivery() != null) {
                sectionDeliveryResultVO.getOwnerSectionDelivery().setDefaultRentTime(DateUtils.formate(mode.getOwnerProposalGetTime(), DateUtils.FORMAT_STR_RENYUN));
            }
            if (mode.getOwnerProposalReturnTime() != null && sectionDeliveryResultVO.getOwnerSectionDelivery() != null) {
                sectionDeliveryResultVO.getOwnerSectionDelivery().setDefaultRevertTime(DateUtils.formate(mode.getOwnerProposalReturnTime(), DateUtils.FORMAT_STR_RENYUN));
            }
        }
        return sectionDeliveryResultVO;
    }

    /**
     * 追加参数
     *
     * @param updateFlowOrderDTO 通知仁云数据
     * @param orderEntity        订单信息
     * @param renterGoodsDetail  租客商品信息
     **/
    public void appendRenyunUpdateFlowOrderParam(UpdateFlowOrderDTO updateFlowOrderDTO,
                                                 RenterGoodsDetailDTO renterGoodsDetail, OrderEntity orderEntity) {
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(updateFlowOrderDTO.getOrdernumber());
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(renterOrderEntity.getOrderNo(), renterOrderEntity.getRenterOrderNo());
        updateFlowOrderDTO.setRentAmt(renterOrderCostEntity.getRentCarAmount() == null ? "" : String.valueOf(Math.abs(renterOrderCostEntity.getRentCarAmount())));
        updateFlowOrderDTO.setDayUnitPrice(renterGoodsDetail.getCarGuideDayPrice() == null ? "" : String.valueOf(renterGoodsDetail.getCarGuideDayPrice()));
        Double oilPriceByCityCodeAndType = deliveryCarInfoPriceService.getOilPriceByCityCodeAndType(Integer.valueOf(orderEntity.getCityCode()), renterGoodsDetail.getCarEngineType());
        updateFlowOrderDTO.setOilPrice(oilPriceByCityCodeAndType == null ? "0" : String.valueOf(oilPriceByCityCodeAndType));


        updateFlowOrderDTO.setUseVirtualAddrFlag(Objects.nonNull(renterGoodsDetail.getCarAddrIndex()) && renterGoodsDetail.getCarAddrIndex() > OrderConstant.ZERO ?
                String.valueOf(OrderConstant.YES) : String.valueOf(OrderConstant.NO));
        if (ServiceTypeEnum.TAKE_TYPE.getValue().equals(updateFlowOrderDTO.getServicetype())) {
            updateFlowOrderDTO.setPickUpCarSrvFlag(Objects.nonNull(renterOrderEntity.getIsGetCar()) ?
                    renterOrderEntity.getIsGetCar().toString() : String.valueOf(OrderConstant.NO));
        } else if (ServiceTypeEnum.BACK_TYPE.getValue().equals(updateFlowOrderDTO.getServicetype())) {
            updateFlowOrderDTO.setPickUpCarSrvFlag(Objects.nonNull(renterOrderEntity.getIsReturnCar()) ?
                    renterOrderEntity.getIsReturnCar().toString() : String.valueOf(OrderConstant.NO));
        } else {
            updateFlowOrderDTO.setPickUpCarSrvFlag(String.valueOf(OrderConstant.YES));
        }
        log.info("推送仁云子订单号renterOrderNo={}", renterOrderEntity.getRenterOrderNo());
    }

    /**
     * 获取通知仁云标识
     *
     * @param isNotifyRenyun      是否通知仁云(配送订单)
     * @param getAndReturnSrvFlag 取还车服务标识
     * @return int 0-不通知 1-通知(取还车服务) 2-通知(自取自还并使用虚拟地址)
     */
    public int getNoticeRenYunFlag(Integer isNotifyRenyun, Integer getAndReturnSrvFlag, Integer carAddrIndex) {
        log.info("DeliveryCarService.getNoticeRenYunFlag. param is,isNotifyRenyun:[{}], getAndReturnSrvFlag:[{}], " +
                "carAddrIndex:[{}]", isNotifyRenyun, getAndReturnSrvFlag, carAddrIndex);
        // 配送订单明确推送
        if (Objects.nonNull(isNotifyRenyun) && isNotifyRenyun == OrderConstant.YES) {
            return OrderConstant.ONE;
        }

        // 使用取还车服务
        if (Objects.nonNull(getAndReturnSrvFlag) && getAndReturnSrvFlag == OrderConstant.YES) {
            return OrderConstant.ONE;
        }

        // 自取自还并使用了虚拟地址
        if (Objects.nonNull(carAddrIndex) && carAddrIndex > OrderConstant.ZERO) {
            return OrderConstant.TWO;
        }
        return OrderConstant.ZERO;
    }


    /**
     * 自取自还并使用虚拟地址特殊处理
     *
     * @param renYunFlowOrderDTO   童子仁云数据
     * @param renterGoodsDetailDTO 租客商品信息
     * @param cityCode             城市编码
     */
    private void noServiceSpecialHandle(RenYunFlowOrderDTO renYunFlowOrderDTO,
                                        RenterGoodsDetailDTO renterGoodsDetailDTO, String cityCode) {

        if (Objects.isNull(renYunFlowOrderDTO)) {
            return;
        }
        // 重置配送模式相关信息
        if (!StringUtils.equals(renYunFlowOrderDTO.getDistributionMode(), String.valueOf(OrderConstant.ONE))) {
            renYunFlowOrderDTO.setDistributionMode(String.valueOf(OrderConstant.ONE));
            renYunFlowOrderDTO.setRenterRentTimeStart(null);
            renYunFlowOrderDTO.setRenterRentTimeEnd(null);
            renYunFlowOrderDTO.setRenterRevertTimeStart(null);
            renYunFlowOrderDTO.setRenterRevertTimeEnd(null);
            renYunFlowOrderDTO.setRenterProposalGetTime(null);
            renYunFlowOrderDTO.setRenterProposalReturnTime(null);

            renYunFlowOrderDTO.setOwnerRentTimeStart(null);
            renYunFlowOrderDTO.setOwnerRentTimeEnd(null);
            renYunFlowOrderDTO.setOwnerRevertTimeStart(null);
            renYunFlowOrderDTO.setOwnerRevertTimeEnd(null);
            renYunFlowOrderDTO.setOwnerProposalGetTime(null);
            renYunFlowOrderDTO.setOwnerProposalReturnTime(null);
        }
        // 重置订单地址信息
        renYunFlowOrderDTO.setPickupcaraddr(renterGoodsDetailDTO.getCarShowAddr());
        renYunFlowOrderDTO.setRealGetCarLat(renterGoodsDetailDTO.getCarShowLat());
        renYunFlowOrderDTO.setRealGetCarLon(renterGoodsDetailDTO.getCarShowLon());
        renYunFlowOrderDTO.setAlsocaraddr(renterGoodsDetailDTO.getCarShowAddr());
        renYunFlowOrderDTO.setRealReturnCarLat(renterGoodsDetailDTO.getCarShowLat());
        renYunFlowOrderDTO.setRealReturnCarLon(renterGoodsDetailDTO.getCarShowLon());
        // 计算提前延后时间
        CarRentTimeRangeReqDTO reqDTO = new CarRentTimeRangeReqDTO();
        reqDTO.setCarNo(renterGoodsDetailDTO.getCarNo().toString());
        reqDTO.setCityCode(cityCode);
        reqDTO.setRentTime(LocalDateTimeUtils.parseStringToDateTime(renYunFlowOrderDTO.getTermtime()));
        reqDTO.setRevertTime(LocalDateTimeUtils.parseStringToDateTime(renYunFlowOrderDTO.getReturntime()));
        reqDTO.setSrvGetFlag(OrderConstant.YES);
        reqDTO.setSrvGetAddr(renYunFlowOrderDTO.getPickupcaraddr());
        reqDTO.setSrvGetLat(renYunFlowOrderDTO.getRealGetCarLat());
        reqDTO.setSrvGetLon(renYunFlowOrderDTO.getRealGetCarLon());
        reqDTO.setSrvReturnFlag(OrderConstant.YES);
        reqDTO.setSrvReturnAddr(renYunFlowOrderDTO.getAlsocaraddr());
        reqDTO.setSrvReturnLat(renYunFlowOrderDTO.getRealReturnCarLat());
        reqDTO.setSrvReturnLon(renYunFlowOrderDTO.getRealReturnCarLon());
        CarRentTimeRangeDTO dto = carRentalTimeProxyService.getCarRentTimeRange(reqDTO);
        if (Objects.nonNull(dto)) {
            renYunFlowOrderDTO.setBeforeTime(DateUtils.formate(dto.getAdvanceStartDate(), DateUtils.FORMAT_STR_RENYUN));
            renYunFlowOrderDTO.setAfterTime(DateUtils.formate(dto.getDelayEndDate(), DateUtils.FORMAT_STR_RENYUN));
        }
    }


    /**
     * 自取自还并使用虚拟地址特殊处理(修改订单)
     *
     * @param renYunFlowOrderDTO   童子仁云数据
     * @param renterGoodsDetailDTO 租客商品信息
     * @param cityCode             城市编码
     */
    private void noServiceSpecialHandle(UpdateFlowOrderDTO renYunFlowOrderDTO,
                                        RenterGoodsDetailDTO renterGoodsDetailDTO, String cityCode) {

        if (Objects.isNull(renYunFlowOrderDTO)) {
            return;
        }
        // 重置配送模式相关信息
        if (!StringUtils.equals(renYunFlowOrderDTO.getDistributionMode(), String.valueOf(OrderConstant.ONE))) {
            renYunFlowOrderDTO.setDistributionMode(String.valueOf(OrderConstant.ONE));
            renYunFlowOrderDTO.setRenterRentTimeStart(null);
            renYunFlowOrderDTO.setRenterRentTimeEnd(null);
            renYunFlowOrderDTO.setRenterRevertTimeStart(null);
            renYunFlowOrderDTO.setRenterRevertTimeEnd(null);
            renYunFlowOrderDTO.setRenterProposalGetTime(null);
            renYunFlowOrderDTO.setRenterProposalReturnTime(null);

            renYunFlowOrderDTO.setOwnerRentTimeStart(null);
            renYunFlowOrderDTO.setOwnerRentTimeEnd(null);
            renYunFlowOrderDTO.setOwnerRevertTimeStart(null);
            renYunFlowOrderDTO.setOwnerRevertTimeEnd(null);
            renYunFlowOrderDTO.setOwnerProposalGetTime(null);
            renYunFlowOrderDTO.setOwnerProposalReturnTime(null);
        }
        // 重置订单地址信息
        renYunFlowOrderDTO.setNewpickupcaraddr(renterGoodsDetailDTO.getCarShowAddr());
        renYunFlowOrderDTO.setRealGetCarLat(renterGoodsDetailDTO.getCarShowLat());
        renYunFlowOrderDTO.setRealGetCarLon(renterGoodsDetailDTO.getCarShowLon());
        renYunFlowOrderDTO.setNewalsocaraddr(renterGoodsDetailDTO.getCarShowAddr());
        renYunFlowOrderDTO.setRealReturnCarLat(renterGoodsDetailDTO.getCarShowLat());
        renYunFlowOrderDTO.setRealReturnCarLon(renterGoodsDetailDTO.getCarShowLon());
        // 计算提前延后时间
        CarRentTimeRangeReqDTO reqDTO = new CarRentTimeRangeReqDTO();
        reqDTO.setCarNo(renterGoodsDetailDTO.getCarNo().toString());
        reqDTO.setCityCode(cityCode);
        reqDTO.setRentTime(LocalDateTimeUtils.parseStringToDateTime(renYunFlowOrderDTO.getNewtermtime()));
        reqDTO.setRevertTime(LocalDateTimeUtils.parseStringToDateTime(renYunFlowOrderDTO.getNewreturntime()));
        reqDTO.setSrvGetFlag(OrderConstant.YES);
        reqDTO.setSrvGetAddr(renYunFlowOrderDTO.getNewpickupcaraddr());
        reqDTO.setSrvGetLat(renYunFlowOrderDTO.getRealGetCarLat());
        reqDTO.setSrvGetLon(renYunFlowOrderDTO.getRealGetCarLon());
        reqDTO.setSrvReturnFlag(OrderConstant.YES);
        reqDTO.setSrvReturnAddr(renYunFlowOrderDTO.getNewalsocaraddr());
        reqDTO.setSrvReturnLat(renYunFlowOrderDTO.getRealReturnCarLat());
        reqDTO.setSrvReturnLon(renYunFlowOrderDTO.getRealReturnCarLon());
        CarRentTimeRangeDTO dto = carRentalTimeProxyService.getCarRentTimeRange(reqDTO);
        if (Objects.nonNull(dto)) {
            renYunFlowOrderDTO.setNewbeforeTime(DateUtils.formate(dto.getAdvanceStartDate(),
                    DateUtils.FORMAT_STR_RENYUN));
            renYunFlowOrderDTO.setNewafterTime(DateUtils.formate(dto.getDelayEndDate(), DateUtils.FORMAT_STR_RENYUN));
        }
    }


}
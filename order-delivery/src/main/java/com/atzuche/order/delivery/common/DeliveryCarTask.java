package com.atzuche.order.delivery.common;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.SectionDeliveryUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.SectionParamDTO;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.vo.res.SectionDeliveryResultVO;
import com.atzuche.order.commons.vo.res.SectionDeliveryVO;
import com.atzuche.order.delivery.config.DeliveryRenYunConfig;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryMode;
import com.atzuche.order.delivery.enums.OrderScenesSourceEnum;
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
import org.springframework.transaction.annotation.Transactional;

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
        // 追加参数
        renYunFlowOrderDTO = appendRenYunFlowOrderDTO(renYunFlowOrderDTO, renterGoodsDetailDTO);
        // 针对自取自还订单特殊处理
        if (OrderConstant.TWO == noticeRenYunFlag) {
            noServiceSpecialHandle(renYunFlowOrderDTO, renterGoodsDetailDTO);
        }
        deliveryAsyncProxy.addRenYunFlowOrderInfoproxy(renYunFlowOrderDTO);
    }


    /**
     * 更新订单到仁云流程系统
     */
    public void updateRenYunFlowOrderInfo(UpdateFlowOrderDTO updateFlowOrderDTO) {
        // 追加参数
        updateFlowOrderDTO = appendUpdateFlowOrderDTO(updateFlowOrderDTO);
        //添加参数化
        appendRenyunUpdateFlowOrderParam(updateFlowOrderDTO);
        deliveryAsyncProxy.updateRenYunFlowOrderInfoProxy(updateFlowOrderDTO);
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
    @Transactional(rollbackFor = Exception.class)
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
     * @param renYunFlowOrderDTO 通知仁云数据
     * @param renterGoodsDetailDTO 租客订单商品信息
     * @return RenYunFlowOrderDTO
     */
    public RenYunFlowOrderDTO appendRenYunFlowOrderDTO(RenYunFlowOrderDTO renYunFlowOrderDTO, RenterGoodsDetailDTO renterGoodsDetailDTO) {
        // 获取有效的租客子订单
        OrderEntity orderEntity = orderService.getOrderEntity(renYunFlowOrderDTO.getOrdernumber());
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
     * @param updFlow 通知仁云数据
     * @return UpdateFlowOrderDTO
     */
    public UpdateFlowOrderDTO appendUpdateFlowOrderDTO(UpdateFlowOrderDTO updFlow) {
        if (updFlow == null) {
            return null;
        }
        // 获取有效的租客子订单
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(updFlow.getOrdernumber());
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
     **/
    public void appendRenyunUpdateFlowOrderParam(UpdateFlowOrderDTO updateFlowOrderDTO) {
        OrderEntity orderEntity = orderService.getOrderEntity(updateFlowOrderDTO.getOrdernumber());
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(updateFlowOrderDTO.getOrdernumber());
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(renterOrderEntity.getOrderNo(), renterOrderEntity.getRenterOrderNo());
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo(), false);
        updateFlowOrderDTO.setRentAmt(renterOrderCostEntity.getRentCarAmount() == null ? "" : String.valueOf(Math.abs(renterOrderCostEntity.getRentCarAmount())));
        updateFlowOrderDTO.setDayUnitPrice(renterGoodsDetail.getCarGuideDayPrice() == null ? "" : String.valueOf(renterGoodsDetail.getCarGuideDayPrice()));
        Double oilPriceByCityCodeAndType = deliveryCarInfoPriceService.getOilPriceByCityCodeAndType(Integer.valueOf(orderEntity.getCityCode()), renterGoodsDetail.getCarEngineType());
        updateFlowOrderDTO.setOilPrice(oilPriceByCityCodeAndType == null ? "0" : String.valueOf(oilPriceByCityCodeAndType));
        log.info("推送仁云子订单号renterOrderNo={}", renterOrderEntity.getRenterOrderNo());
    }



    private void noServiceSpecialHandle(RenYunFlowOrderDTO renYunFlowOrderDTO,
                                        RenterGoodsDetailDTO renterGoodsDetailDTO) {

        if(Objects.isNull(renYunFlowOrderDTO)) {
            return ;
        }
        //重置配送模式相关信息
        if(!StringUtils.equals(renYunFlowOrderDTO.getDistributionMode(), String.valueOf(OrderConstant.ONE))) {
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

        // todo 重置订单地址信息


    }
}
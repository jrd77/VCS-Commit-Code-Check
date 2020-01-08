package com.atzuche.order.delivery.service.delivery;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.delivery.common.DeliveryCarTask;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.enums.*;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.utils.DateUtils;
import com.atzuche.order.delivery.vo.delivery.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author 胡春林
 * 配送服务
 */
@Service
public class DeliveryCarService {

    @Autowired
    DeliveryCarTask deliveryCarTask;

    /**
     * 添加配送相关信息(是否下单，是否推送仁云)
     */
    public void addFlowOrderInfo(Integer getMinutes, Integer returnMinutes, OrderReqContext orderReqContext) {
        if (null == orderReqContext || Objects.isNull(orderReqContext.getOrderReqVO())) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        int getMinute = getMinutes == null ? 0 : getMinutes;
        int returnMinute = returnMinutes == null ? 0 : returnMinutes;
        if (orderReqContext.getOrderReqVO().getSrvReturnFlag().intValue() == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
            addRenYunFlowOrderInfo(getMinute, returnMinute, orderReqContext, UserTypeEnum.OWNER_TYPE.getValue());
        }
        if (orderReqContext.getOrderReqVO().getSrvGetFlag().intValue() == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
            addRenYunFlowOrderInfo(getMinute, returnMinute, orderReqContext, UserTypeEnum.RENTER_TYPE.getValue());
        }
    }

    /**
     * 添加配送相关信息细节(是否下单，是否推送仁云)
     */
    public void addRenYunFlowOrderInfo(Integer getMinutes, Integer returnMinutes, OrderReqContext orderReqContext, Integer orderType) {
        OrderDeliveryVO orderDeliveryVO = createOrderDeliveryParams(orderReqContext, orderType);
        if (null == orderDeliveryVO) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        deliveryCarTask.insertDeliveryAddress(getMinutes, returnMinutes, orderDeliveryVO, DeliveryTypeEnum.ADD_TYPE.getValue().intValue());
        if (orderDeliveryVO.getOrderDeliveryDTO() != null && orderDeliveryVO.getOrderDeliveryDTO().getIsNotifyRenyun().intValue() == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
            RenYunFlowOrderDTO renYunFlowOrder = orderDeliveryVO.getRenYunFlowOrderDTO();
            deliveryCarTask.addRenYunFlowOrderInfo(renYunFlowOrder);
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
        deliveryCarTask.insertDeliveryAddress(null, null, orderDeliveryVO, DeliveryTypeEnum.UPDATE_TYPE.getValue().intValue());
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
    public void cancelRenYunFlowOrderInfo(CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        if (null == cancelOrderDeliveryVO || cancelOrderDeliveryVO.getCancelFlowOrderDTO() == null || StringUtils.isBlank(cancelOrderDeliveryVO.getRenterOrderNo())) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        int serviceType;
        if (cancelOrderDeliveryVO.getCancelFlowOrderDTO().getServicetype().equals("all")) {
            deliveryCarTask.cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo(), 1);
            cancelOrderDeliveryVO.getCancelFlowOrderDTO().setServicetype(ServiceTypeEnum.TAKE_TYPE.getValue());
            deliveryCarTask.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
            deliveryCarTask.cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo(), 2);
            cancelOrderDeliveryVO.getCancelFlowOrderDTO().setServicetype(ServiceTypeEnum.BACK_TYPE.getValue());
            deliveryCarTask.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
        } else {
            serviceType = cancelOrderDeliveryVO.getCancelFlowOrderDTO().getServicetype().equals(ServiceTypeEnum.TAKE_TYPE.getValue()) ? 1 : 2;
            deliveryCarTask.cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo(), serviceType);
            deliveryCarTask.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
        }
    }

    /**
     * 构造配送订单数据
     *
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
        RenYunFlowOrderDTO renYunFlowOrderDTO = new RenYunFlowOrderDTO();
        RenterGoodsDetailDTO renterGoodsDetailDTO = orderReqContext.getRenterGoodsDetailDto();
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = orderReqContext.getOwnerGoodsDetailDto();
        renterDeliveryAddrDTO.setParamsTypeValue(orderType,renterMemberDTO);
        if (orderReqVO.getSrvReturnFlag().intValue() == UsedDeliveryTypeEnum.NO_USED.getValue().intValue()) {
            String carShowAddr = renterGoodsDetailDTO.getCarShowAddr() == null ? renterGoodsDetailDTO.getCarRealAddr() : renterGoodsDetailDTO.getCarShowAddr();
            String carShowLat = renterGoodsDetailDTO.getCarShowLat() == null ? renterGoodsDetailDTO.getCarRealLat() : renterGoodsDetailDTO.getCarShowLat();
            String carShowLng = renterGoodsDetailDTO.getCarShowLon() == null ? renterGoodsDetailDTO.getCarRealLon() : renterGoodsDetailDTO.getCarShowLon();
            if (StringUtils.isNotBlank(renterGoodsDetailDTO.getCarShowAddr())) {
                renterDeliveryAddrDTO = RenterDeliveryAddrDTO.builder().actGetCarAddr(carShowAddr).actGetCarLat(carShowLat).actGetCarLon(carShowLng).actReturnCarAddr(carShowAddr)
                        .actReturnCarLat(carShowLat).actReturnCarLon(carShowLng).expGetCarAddr(carShowAddr).expGetCarLat(carShowLat).expGetCarLon(carShowLng).expReturnCarAddr(carShowAddr)
                        .expReturnCarLat(carShowLat).expReturnCarLon(carShowLng).orderNo(renterGoodsDetailDTO.getOrderNo()).renterOrderNo(renterGoodsDetailDTO.getRenterOrderNo()).createTime(LocalDateTime.now()).createOp("").build();
            }
        } else {
            /**组装地址信息**/
            renterDeliveryAddrDTO = RenterDeliveryAddrDTO.builder().actGetCarAddr(orderReqVO.getSrvGetAddr()).actGetCarLat(orderReqVO.getSrvGetLat()).actGetCarLon(orderReqVO.getSrvGetLon()).actReturnCarAddr(orderReqVO.getSrvReturnAddr())
                    .actReturnCarLat(orderReqVO.getSrvReturnLat()).actReturnCarLon(orderReqVO.getSrvReturnLon()).expGetCarAddr(orderReqVO.getSrvReturnAddr()).expGetCarLat(orderReqVO.getSrvReturnLat()).expGetCarLon(orderReqVO.getSrvReturnLon()).expReturnCarAddr(orderReqVO.getSrvGetAddr())
                    .expReturnCarLat(orderReqVO.getSrvGetLat()).expReturnCarLon(orderReqVO.getSrvGetLon()).orderNo(renterGoodsDetailDTO.getOrderNo()).renterOrderNo(renterGoodsDetailDTO.getRenterOrderNo()).createTime(LocalDateTime.now()).createOp("").build();
            /**组装配送订单信息**/
            orderDeliveryDTO.setCityCode(orderReqVO.getCityCode());
            orderDeliveryDTO.setCityName(orderReqVO.getCityName());
            orderDeliveryDTO.setCreateOp("");
            orderDeliveryDTO.setRenterGetReturnAddr(orderReqVO.getSrvReturnAddr());
            orderDeliveryDTO.setRenterGetReturnAddrLat(orderReqVO.getSrvReturnLat());
            orderDeliveryDTO.setRenterGetReturnAddrLon(orderReqVO.getSrvReturnLon());
            orderDeliveryDTO.setRenterName(renterMemberDTO.getRealName());
            orderDeliveryDTO.setRenterPhone(renterMemberDTO.getPhone());
            orderDeliveryDTO.setOrderNo(renterGoodsDetailDTO.getOrderNo());
            orderDeliveryDTO.setRenterDealCount(renterMemberDTO.getOrderSuccessCount());
            orderDeliveryDTO.setIsNotifyRenyun(UsedDeliveryTypeEnum.USED.getValue().intValue());
            orderDeliveryDTO.setOwnerGetReturnAddr(orderReqVO.getSrvReturnAddr());
            orderDeliveryDTO.setOwnerGetReturnAddrLat(orderReqVO.getSrvReturnLat());
            orderDeliveryDTO.setOwnerGetReturnAddrLon(orderReqVO.getSrvReturnLon());
            orderDeliveryDTO.setOwnerName(ownerMemberDTO.getRealName());
            orderDeliveryDTO.setOwnerPhone(ownerMemberDTO.getPhone());
            orderDeliveryDTO.setRenterPhone(renterMemberDTO.getPhone());
            orderDeliveryDTO.setRenterOrderNo(renterGoodsDetailDTO.getRenterOrderNo());
            orderDeliveryDTO.setRentTime(renterGoodsDetailDTO.getRentTime());
            orderDeliveryDTO.setRevertTime(renterGoodsDetailDTO.getRevertTime());
            orderDeliveryDTO.setType(orderType);
            orderDeliveryDTO.setParamsTypeValue(orderType,ownerMemberDTO,renterMemberDTO);
            /**组装仁云信息**/
            renYunFlowOrderDTO.setOrdernumber(renterGoodsDetailDTO.getOrderNo());
            renYunFlowOrderDTO.setOrderType(orderReqVO.getOrderCategory());
            renYunFlowOrderDTO.setServiceTypeInfo(orderType,orderDeliveryDTO);
            renYunFlowOrderDTO.setTermtime(DateUtils.formate(renterGoodsDetailDTO.getRentTime(), DateUtils.DATE_DEFAUTE_4));
            renYunFlowOrderDTO.setReturntime(DateUtils.formate(renterGoodsDetailDTO.getRevertTime(), DateUtils.DATE_DEFAUTE_4));
            renYunFlowOrderDTO.setCarno(String.valueOf(renterGoodsDetailDTO.getCarNo()));
            renYunFlowOrderDTO.setVehiclemodel(renterGoodsDetailDTO.getCarBrandTxt());
            renYunFlowOrderDTO.setVehicletype(renterGoodsDetailDTO.getCarTypeTxt());
            renYunFlowOrderDTO.setDeliverycarcity(orderReqVO.getCityName());
            renYunFlowOrderDTO.setDefaultpickupcaraddr(orderDeliveryDTO.getRenterGetReturnAddr());
            renYunFlowOrderDTO.setAlsocaraddr(orderDeliveryDTO.getRenterGetReturnAddr());
            renYunFlowOrderDTO.setOwnername(ownerMemberDTO.getRealName());
            renYunFlowOrderDTO.setOwnerphone(ownerMemberDTO.getPhone());
            renYunFlowOrderDTO.setSuccessordenumber(String.valueOf(ownerMemberDTO.getOrderSuccessCount()));
            renYunFlowOrderDTO.setTenantname(renterMemberDTO.getRealName());
            renYunFlowOrderDTO.setTenantphone(renterMemberDTO.getPhone());
            renYunFlowOrderDTO.setTenantturnoverno(String.valueOf(renterMemberDTO.getOrderSuccessCount()));
            renYunFlowOrderDTO.setOwnerType(ownerGoodsDetailDTO.getType());
            renYunFlowOrderDTO.setSceneName(orderReqVO.getSceneCode());
            renYunFlowOrderDTO.setDisplacement(String.valueOf(ownerGoodsDetailDTO.getCarCylinderCapacity()));
            renYunFlowOrderDTO.setSource(orderReqVO.getSource());
        }
        orderDeliveryVO.setOrderDeliveryDTO(orderDeliveryDTO);
        orderDeliveryVO.setRenterDeliveryAddrDTO(renterDeliveryAddrDTO);
        orderDeliveryVO.setRenYunFlowOrderDTO(renYunFlowOrderDTO);
        return orderDeliveryVO;
    }

}

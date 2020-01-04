package com.atzuche.order.delivery.service.delivery;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.delivery.common.DeliveryCarTask;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.RenterDeliveryAddrEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.*;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.mapper.RenterDeliveryAddrMapper;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryMapper;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.CodeUtils;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.utils.DateUtils;
import com.atzuche.order.delivery.vo.delivery.*;
import com.atzuche.order.delivery.vo.handover.HandoverCarInfoDTO;
import com.atzuche.order.delivery.vo.handover.HandoverCarVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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
    public void addRenYunFlowOrderInfo(Integer getMinutes, Integer returnMinutes, OrderReqContext orderReqContext) {
        OrderDeliveryVO orderDeliveryVO = createOrderDeliveryParams(orderReqContext);
        if (null == orderDeliveryVO) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        deliveryCarTask.insertDeliveryAddress(getMinutes,returnMinutes,orderDeliveryVO, DeliveryTypeEnum.ADD_TYPE.getValue().intValue());
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
        deliveryCarTask.insertDeliveryAddress(null,null,orderDeliveryVO, DeliveryTypeEnum.UPDATE_TYPE.getValue().intValue());
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
    public void cancelRenYunFlowOrderInfo(CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        if (null == cancelOrderDeliveryVO || cancelOrderDeliveryVO.getCancelFlowOrderDTO() == null || StringUtils.isBlank(cancelOrderDeliveryVO.getRenterOrderNo())) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        int serviceType = cancelOrderDeliveryVO.getCancelFlowOrderDTO().getServicetype().equals(ServiceTypeEnum.TAKE_TYPE.getValue()) ? 1 : 2;
        deliveryCarTask.cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo(), serviceType);
        deliveryCarTask.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
    }

    /**
     * 构造配送订单数据
     *
     * @param orderReqContext
     * @return
     */
    public OrderDeliveryVO createOrderDeliveryParams(OrderReqContext orderReqContext) {
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
        //不使用还车服务（一定不使用取车服务）
        if (orderReqVO.getSrvReturnFlag().intValue() == UsedDeliveryTypeEnum.NO_USED.getValue().intValue()) {

            String carShowAddr = renterGoodsDetailDTO.getCarShowAddr() == null ? renterGoodsDetailDTO.getCarRealAddr() : renterGoodsDetailDTO.getCarShowAddr();
            String carShowLat = renterGoodsDetailDTO.getCarShowLat() == null ? renterGoodsDetailDTO.getCarRealLat() : renterGoodsDetailDTO.getCarShowLat();
            String carShowLng = renterGoodsDetailDTO.getCarShowLon() == null ? renterGoodsDetailDTO.getCarRealLon() : renterGoodsDetailDTO.getCarShowLon();
            //入库取送地址数据
            if (StringUtils.isNotBlank(renterGoodsDetailDTO.getCarShowAddr())) {
                renterDeliveryAddrDTO.setActGetCarAddr(carShowAddr);
                renterDeliveryAddrDTO.setActGetCarLat(carShowLat);
                renterDeliveryAddrDTO.setActGetCarLon(carShowLng);
                renterDeliveryAddrDTO.setActReturnCarAddr(carShowAddr);
                renterDeliveryAddrDTO.setActReturnCarLat(carShowLat);
                renterDeliveryAddrDTO.setActReturnCarLon(carShowLng);
                renterDeliveryAddrDTO.setExpGetCarAddr(carShowAddr);
                renterDeliveryAddrDTO.setExpGetCarLat(carShowLat);
                renterDeliveryAddrDTO.setExpGetCarLon(carShowLng);
                renterDeliveryAddrDTO.setCreateTime(LocalDateTime.now());
                renterDeliveryAddrDTO.setCreateOp("");
            }
        } else {
            /**组装地址信息**/
            renterDeliveryAddrDTO.setActGetCarAddr(orderReqVO.getSrvGetAddr());
            renterDeliveryAddrDTO.setActGetCarLat(orderReqVO.getSrvGetLat());
            renterDeliveryAddrDTO.setActGetCarLon(orderReqVO.getSrvGetLon());
            renterDeliveryAddrDTO.setActReturnCarAddr(orderReqVO.getSrvReturnAddr());
            renterDeliveryAddrDTO.setActReturnCarLat(orderReqVO.getSrvReturnLat());
            renterDeliveryAddrDTO.setActReturnCarLon(orderReqVO.getSrvReturnLon());
            renterDeliveryAddrDTO.setExpGetCarAddr(orderReqVO.getSrvReturnAddr());
            renterDeliveryAddrDTO.setExpGetCarLat(orderReqVO.getSrvReturnLat());
            renterDeliveryAddrDTO.setExpGetCarLon(orderReqVO.getSrvReturnLon());
            renterDeliveryAddrDTO.setCreateTime(LocalDateTime.now());
            renterDeliveryAddrDTO.setCreateOp("");
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
            orderDeliveryDTO.setRenterOrderNo(renterGoodsDetailDTO.getRenterOrderNo());
            orderDeliveryDTO.setRentTime(renterGoodsDetailDTO.getRentTime());
            orderDeliveryDTO.setRevertTime(renterGoodsDetailDTO.getRevertTime());
            if (orderReqVO.getSrvReturnFlag().intValue() == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
                orderDeliveryDTO.setType(2);
                renYunFlowOrderDTO.setAlsocaraddr(orderDeliveryDTO.getRenterGetReturnAddr());
            } else if (orderReqVO.getSrvGetFlag().intValue() == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
                orderDeliveryDTO.setType(1);
                renYunFlowOrderDTO.setPickupcaraddr(orderDeliveryDTO.getRenterGetReturnAddr());
            }
            /**组装仁云信息**/
            renYunFlowOrderDTO.setOrdernumber(renterGoodsDetailDTO.getOrderNo());
            renYunFlowOrderDTO.setOrderType(orderReqVO.getOrderCategory());
            if (orderReqVO.getSrvReturnFlag().intValue() == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
                renYunFlowOrderDTO.setServicetype(ServiceTypeEnum.BACK_TYPE.getValue());
            } else if (orderReqVO.getSrvGetFlag().intValue() == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
                renYunFlowOrderDTO.setServicetype(ServiceTypeEnum.TAKE_TYPE.getValue());
            }
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

package com.atzuche.order.admin.service;

import com.atzuche.order.admin.filter.CityLonLatFilter;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.ModifyOrderConsoleDTO;
import com.atzuche.order.commons.enums.DeliveryErrorCode;
import com.atzuche.order.commons.exceptions.DeliveryOrderException;
import com.atzuche.order.commons.vo.OwnerTransAddressReqVO;
import com.atzuche.order.commons.vo.delivery.*;
import com.atzuche.order.commons.vo.req.ModifyOrderReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.open.service.FeignOrderModifyService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author 胡春林
 * 配送信息服务
 */
@Service
public class AdminDeliveryCarService {

    @Autowired
    FeignOrderModifyService feignOrderModifyService;
    @Autowired
    CityLonLatFilter cityLonLatFilter;
    @Autowired
    private DeliveryRemoteService deliveryRemoteService;
    @Autowired
    private RemoteFeignService remoteFeignService;
    @Autowired
    private ModificationOrderService modificationOrderService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarVO findDeliveryListByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        logger.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
        return deliveryRemoteService.getDeliveryCarVO(deliveryCarDTO);
    }

    

    /**
     * 更新交接车信息
     * @param deliveryCarVO
     * @throws Exception
     */
    public void updateHandoverCarInfo(DeliveryCarVO deliveryCarVO) throws Exception {
        logger.info("入参handoverCarReqVO：[{}]", deliveryCarVO.toString());
        deliveryRemoteService.updateHandoverCarInfo(deliveryCarVO);
    }

    /**
     * 更新取还车信息
     * @param deliveryCarVO
     * @throws Exception
     */
    public void updateDeliveryCarInfo(DeliveryCarVO deliveryCarVO) throws Exception {
        logger.info("入参deliveryReqVO：[{}]", deliveryCarVO.toString());
        OrderReqContext orderReqContext = new OrderReqContext();
        OrderReqVO orderReqVo = new OrderReqVO();
        SimpleOrderInfoVO simp = deliveryRemoteService.getSimpleOrderInfoVO(deliveryCarVO.getOrderNo());
        if (Objects.isNull(simp)) {
            logger.info("没有找到对应的订单数据");
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        deliveryCarVO.setRenterMemNo(simp.getRenterMemNo());
        deliveryCarVO.setOwnerMemNo(simp.getOwnerMemNo());
        DeliveryReqVO deliveryReqVO = createParams(deliveryCarVO);
        orderReqVo.setCityCode(simp.getCityCode());
        orderReqVo.setSrvGetFlag(deliveryCarVO.getIsGetCar());
        orderReqVo.setSrvReturnFlag(deliveryCarVO.getIsReturnCar());
        orderReqVo.setSrvGetLat(deliveryReqVO.getGetDeliveryReqDTO().getRenterGetReturnLat());
        orderReqVo.setSrvGetLon(deliveryReqVO.getGetDeliveryReqDTO().getRenterGetReturnLng());
        orderReqVo.setSrvReturnLat(deliveryReqVO.getRenterDeliveryReqDTO().getRenterGetReturnLat());
        orderReqVo.setSrvReturnLon(deliveryReqVO.getRenterDeliveryReqDTO().getRenterGetReturnLng());
        orderReqVo.setSrvGetAddr(deliveryReqVO.getGetDeliveryReqDTO().getRenterGetReturnAddr());
        orderReqVo.setSrvReturnAddr(deliveryReqVO.getRenterDeliveryReqDTO().getRenterGetReturnAddr());
        orderReqContext.setOrderReqVO(orderReqVo);
        cityLonLatFilter.validate(orderReqContext);
        //ResponseData responseData = feignOrderModifyService.modifyOrderForConsole(createModifyOrderInfoParams(deliveryCarVO));
        // 获取修改前数据
 		ModifyOrderConsoleDTO modifyOrderConsoleDTO = remoteFeignService.getInitModifyOrderDTO(createModifyOrderInfoParams(deliveryCarVO));
        ResponseData responseData = remoteFeignService.modifyOrder(createModifyOrderInfoParams(deliveryCarVO));
        // 保存操作日志
        modificationOrderService.saveModifyOrderLog(createModifyOrderInfoParams(deliveryCarVO), modifyOrderConsoleDTO);
        if (!responseData.getResCode().equals(ErrorCode.SUCCESS.getCode()) && !responseData.getResCode().equals("400504")) {
            logger.info("修改配送订单租客失败，orderNo：[{}],cause:[{}]", deliveryCarVO.getOrderNo(), responseData.getResCode()+"--"+responseData.getResMsg());
            throw  new DeliveryOrderException(responseData.getResCode(),responseData.getResMsg());
        }
        OwnerTransAddressReqVO ownerTransAddressReqVO = createModifyOrderOwnerInfoParams(deliveryCarVO);
        if(Objects.nonNull(ownerTransAddressReqVO)) {
            //ResponseData ownerResponseData = feignModifyOwnerAddrService.updateOwnerAddrInfo(ownerTransAddressReqVO);
            ResponseData ownerResponseData = remoteFeignService.updateOwnerAddrInfoFromRemote(ownerTransAddressReqVO);
            if (!ownerResponseData.getResCode().equals(ErrorCode.SUCCESS.getCode()) && !ownerResponseData.getResCode().equals("510004")) {
                logger.info("修改配送订单车主失败，orderNo：[{}],cause:[{}]", deliveryCarVO.getOrderNo(), ownerResponseData.getResCode()+"--"+ownerResponseData.getResMsg());
                throw  new DeliveryOrderException(ownerResponseData.getResCode(),ownerResponseData.getResMsg());
            }
        }
        // 更新备注
        deliveryRemoteService.updateDeliveryRemark(deliveryReqVO);
    }

    /**
     * 更新配送订单租客参数
     * @param deliveryCarVO
     * @return
     */
    public ModifyOrderReqVO createModifyOrderInfoParams(DeliveryCarVO deliveryCarVO){
        ModifyOrderReqVO modifyOrderReqVO = new ModifyOrderReqVO();
        modifyOrderReqVO.setOrderNo(deliveryCarVO.getOrderNo());
        modifyOrderReqVO.setMemNo(deliveryCarVO.getRenterMemNo());
        if(Objects.nonNull(deliveryCarVO.getGetHandoverCarDTO())) {
            modifyOrderReqVO.setGetCarAddress(deliveryCarVO.getGetHandoverCarDTO().getRenterRealGetAddr());
            modifyOrderReqVO.setGetCarLat(deliveryCarVO.getGetHandoverCarDTO().getRenterRealGetLat());
            modifyOrderReqVO.setGetCarLon(deliveryCarVO.getGetHandoverCarDTO().getRenterRealGetLng());
        }
        if(Objects.nonNull(deliveryCarVO.getReturnHandoverCarDTO())) {
            modifyOrderReqVO.setRevertCarAddress(deliveryCarVO.getReturnHandoverCarDTO().getRenterRealReturnAddr());
            modifyOrderReqVO.setRevertCarLat(deliveryCarVO.getReturnHandoverCarDTO().getRenterRealReturnLat());
            modifyOrderReqVO.setRevertCarLon(deliveryCarVO.getReturnHandoverCarDTO().getRenterRealReturnLng());
        }
        modifyOrderReqVO.setSrvGetFlag(deliveryCarVO.getIsGetCar());
        modifyOrderReqVO.setSrvReturnFlag(deliveryCarVO.getIsReturnCar());
        return modifyOrderReqVO;
    }

    /**
     * 更新配送订单车主参数
     * @param deliveryCarVO
     * @return
     */
    public OwnerTransAddressReqVO createModifyOrderOwnerInfoParams(DeliveryCarVO deliveryCarVO){
        OwnerTransAddressReqVO ownerTransAddressReqVO = new OwnerTransAddressReqVO();
        ownerTransAddressReqVO.setOrderNo(deliveryCarVO.getOrderNo());
        ownerTransAddressReqVO.setMemNo(deliveryCarVO.getOwnerMemNo());
        if(Objects.nonNull(deliveryCarVO.getGetHandoverCarDTO())) {
            ownerTransAddressReqVO.setGetCarAddressText(deliveryCarVO.getGetHandoverCarDTO().getOwnRealReturnAddr());
            ownerTransAddressReqVO.setSrvGetLat(deliveryCarVO.getGetHandoverCarDTO().getOwnRealReturnLat());
            ownerTransAddressReqVO.setSrvGetLon(deliveryCarVO.getGetHandoverCarDTO().getOwnRealReturnLng());
        }
        if(Objects.nonNull(deliveryCarVO.getReturnHandoverCarDTO())) {
            ownerTransAddressReqVO.setReturnCarAddressText(deliveryCarVO.getReturnHandoverCarDTO().getOwnerRealGetAddr());
            ownerTransAddressReqVO.setSrvReturnLat(deliveryCarVO.getReturnHandoverCarDTO().getOwnerRealGetLat());
            ownerTransAddressReqVO.setSrvReturnLon(deliveryCarVO.getReturnHandoverCarDTO().getOwnerRealGetLng());
        }
        return ownerTransAddressReqVO;
    }


    public DeliveryReqVO createParams(DeliveryCarVO deliveryCarVO) {
        //取车服务信息
        DeliveryReqVO deliveryReqVO = new DeliveryReqVO();
        GetHandoverCarDTO getHandoverCarDTO = deliveryCarVO.getGetHandoverCarDTO();
        ReturnHandoverCarDTO returnHandoverCarDTO = deliveryCarVO.getReturnHandoverCarDTO();
        if (Objects.nonNull(getHandoverCarDTO)) {
            DeliveryReqDTO deliveryReqDTO = new DeliveryReqDTO();
            deliveryReqDTO.setIsUsedGetAndReturnCar(String.valueOf(deliveryCarVO.getIsGetCar()));
            deliveryReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            deliveryReqDTO.setOwnerRealGetAddrReamrk(getHandoverCarDTO.getOwnRealGetRemark());
            deliveryReqDTO.setRenterRealGetAddrReamrk(getHandoverCarDTO.getRenterRealGetAddrReamrk());
            deliveryReqDTO.setRenterGetReturnAddr(getHandoverCarDTO.getRenterRealGetAddr());
            deliveryReqDTO.setOwnerGetReturnAddr(getHandoverCarDTO.getOwnRealReturnAddr());
            deliveryReqDTO.setOwnerGetReturnLat(getHandoverCarDTO.getOwnRealReturnLat());
            deliveryReqDTO.setOwnerGetReturnLng(getHandoverCarDTO.getOwnRealReturnLng());
            deliveryReqDTO.setRenterGetReturnLat(getHandoverCarDTO.getRenterRealGetLat());
            deliveryReqDTO.setRenterGetReturnLng(getHandoverCarDTO.getRenterRealGetLng());
            deliveryReqDTO.setIsUsedGetAndReturnCar(String.valueOf(deliveryCarVO.getIsGetCar()));
            deliveryReqVO.setGetDeliveryReqDTO(deliveryReqDTO);
        }
        if (Objects.nonNull(returnHandoverCarDTO)) {
            DeliveryReqDTO renterDeliveryReqDTO = new DeliveryReqDTO();
            renterDeliveryReqDTO.setIsUsedGetAndReturnCar(String.valueOf(deliveryCarVO.getIsReturnCar()));
            renterDeliveryReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            renterDeliveryReqDTO.setRenterRealGetAddrReamrk(returnHandoverCarDTO.getRenterRealGetRemark());
            renterDeliveryReqDTO.setOwnerRealGetAddrReamrk(returnHandoverCarDTO.getOwnerRealGetAddrReamrk());
            renterDeliveryReqDTO.setRenterGetReturnAddr(returnHandoverCarDTO.getRenterRealReturnAddr());
            renterDeliveryReqDTO.setOwnerGetReturnAddr(returnHandoverCarDTO.getOwnerRealGetAddr());
            renterDeliveryReqDTO.setOwnerGetReturnLat(returnHandoverCarDTO.getOwnerRealGetLat());
            renterDeliveryReqDTO.setOwnerGetReturnLng(returnHandoverCarDTO.getOwnerRealGetLng());
            renterDeliveryReqDTO.setRenterGetReturnLat(returnHandoverCarDTO.getRenterRealReturnLat());
            renterDeliveryReqDTO.setRenterGetReturnLng(returnHandoverCarDTO.getRenterRealReturnLng());
            renterDeliveryReqDTO.setIsUsedGetAndReturnCar(String.valueOf(deliveryCarVO.getIsReturnCar()));
            deliveryReqVO.setRenterDeliveryReqDTO(renterDeliveryReqDTO);
        }
        return deliveryReqVO;
    }


    /**
     * 获取配送取还车信息
     * @param deliveryCarDTO
     * @return
     */
    public DistributionCostVO findDeliveryCostByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        logger.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
        
        return deliveryRemoteService.getDistributionCostVO(deliveryCarDTO);
    }

}

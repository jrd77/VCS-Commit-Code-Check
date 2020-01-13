package com.atzuche.order.coreapi.common.conver;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.ListUtil;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.order.CostItemVO;
import com.atzuche.order.commons.vo.res.order.TotalCostVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pengcheng.fu
 * @date 2020/1/13 14:35
 */

@Component
public class OrderCommonConver {

    private static Logger logger = LoggerFactory.getLogger(OrderCommonConver.class);

    /**
     * 租客订单请求参数封装
     *
     * @param orderNo               主订单号
     * @param renterOrderNo         租客子订单号
     * @param reqContext            下单请求参数
     * @param carRentTimeRangeResVO 提前延后信息
     * @return RenterOrderReqVO 租客订单请求参数
     */
    public RenterOrderReqVO buildRenterOrderReqVO(String orderNo, String renterOrderNo, OrderReqContext reqContext,
                                                  CarRentTimeRangeResVO carRentTimeRangeResVO) {

        RenterOrderReqVO renterOrderReqVO = new RenterOrderReqVO();
        renterOrderReqVO.setOrderNo(orderNo);
        renterOrderReqVO.setRenterOrderNo(renterOrderNo);

        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, RenterOrderReqVO.class, false);
        beanCopier.copy(reqContext.getOrderReqVO(), renterOrderReqVO, null);

        OrderReqVO orderReqVO = reqContext.getOrderReqVO();
        renterOrderReqVO.setEntryCode(orderReqVO.getSceneCode());
        renterOrderReqVO.setSource(orderReqVO.getSource());
        String driverIds = orderReqVO.getDriverIds();
        renterOrderReqVO.setDriverIds(ListUtil.parseString(driverIds, ","));
        renterOrderReqVO.setGetCarBeforeTime(null == carRentTimeRangeResVO || null == carRentTimeRangeResVO.getGetMinutes() ? 0 : carRentTimeRangeResVO.getGetMinutes());
        renterOrderReqVO.setReturnCarAfterTime(null == carRentTimeRangeResVO || null == carRentTimeRangeResVO.getReturnMinutes() ? 0 :
                carRentTimeRangeResVO.getReturnMinutes());

        RenterGoodsDetailDTO goodsDetail = reqContext.getRenterGoodsDetailDto();
        renterOrderReqVO.setGuidPrice(goodsDetail.getCarGuidePrice());
        renterOrderReqVO.setCarSurplusPrice(goodsDetail.getCarSurplusPrice());
        renterOrderReqVO.setInmsrp(goodsDetail.getCarInmsrp());
        renterOrderReqVO.setBrandId(goodsDetail.getBrand());
        renterOrderReqVO.setTypeId(goodsDetail.getType());
        renterOrderReqVO.setLicenseDay(goodsDetail.getLicenseDay());
        renterOrderReqVO.setLabelIds(goodsDetail.getLabelIds());
        renterOrderReqVO.setRenterGoodsPriceDetailDTOList(goodsDetail.getRenterGoodsPriceDetailDTOList());
        renterOrderReqVO.setPlateNum(goodsDetail.getCarPlateNum());
        renterOrderReqVO.setAbatement(orderReqVO.getAbatement());
        renterOrderReqVO.setCarShowLat(goodsDetail.getCarShowLat());
        renterOrderReqVO.setCarShowLon(goodsDetail.getCarShowLon());
        renterOrderReqVO.setCarRealLat(goodsDetail.getCarRealLat());
        renterOrderReqVO.setCarRealLon(goodsDetail.getCarRealLon());

        RenterMemberDTO renterMember = reqContext.getRenterMemberDto();
        renterOrderReqVO.setCertificationTime(renterMember.getCertificationTime());
        renterOrderReqVO.setIsNew(null == renterMember.getIsNew() || renterMember.getIsNew() == 0);
        renterOrderReqVO.setRenterMemberRightDTOList(renterMember.getRenterMemberRightDTOList());
        renterOrderReqVO.setCommUseDriverList(renterMember.getCommUseDriverList());
        renterOrderReqVO.setReplyFlag(goodsDetail.getReplyFlag());
        logger.info("Build renter order reqVO,result is ,renterOrderReqVO:[{}]",
                JSON.toJSONString(renterOrderReqVO));
        return renterOrderReqVO;
    }

    /**
     * 获取商品明细请求参数
     *
     * @param orderReqVO 下单请求参数
     * @return CarProxyService.CarDetailReqVO
     */
    public CarProxyService.CarDetailReqVO buildCarDetailReqVO(OrderReqVO orderReqVO) {
        CarProxyService.CarDetailReqVO carDetailReqVO = new CarProxyService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(StringUtils.isBlank(orderReqVO.getCarAddrIndex()) ? 0 : Integer.parseInt(orderReqVO.getCarAddrIndex()));
        carDetailReqVO.setCarNo(orderReqVO.getCarNo());
        carDetailReqVO.setRentTime(orderReqVO.getRentTime());
        carDetailReqVO.setRevertTime(orderReqVO.getRevertTime());
        carDetailReqVO.setUseSpecialPrice(StringUtils.equals("0",
                orderReqVO.getUseSpecialPrice()));
        return carDetailReqVO;
    }

    /**
     * 下单前计算页租车费用列表
     *
     * @param renterOrderCostRespDTO 订单租车费用信息
     * @return List<CostItemVO>
     */
    public List<CostItemVO> buildCostItemList(RenterOrderCostRespDTO renterOrderCostRespDTO) {
        logger.info("Build costItem list.param is,renterOrderCostRespDTO:[{}]",JSON.toJSONString(renterOrderCostRespDTO));
        if (null == renterOrderCostRespDTO || CollectionUtils.isEmpty(renterOrderCostRespDTO.getRenterOrderCostDetailDTOList())) {
            return null;
        }

        List<CostItemVO> costItemList = new ArrayList<>();
        renterOrderCostRespDTO.getRenterOrderCostDetailDTOList().stream().forEach(cost -> {
            CostItemVO vo = new CostItemVO();
            vo.setCostCode(cost.getCostCode());
            vo.setCostDesc(cost.getCostDesc());
            vo.setCount(cost.getCount());
            if(StringUtils.equals(RenterCashCodeEnum.SRV_GET_COST.getCashNo(),cost.getCostCode())) {
                vo.setUnitPrice(renterOrderCostRespDTO.getGetRealAmt());
                vo.setTotalAmount(renterOrderCostRespDTO.getGetRealAmt());
            } else if(StringUtils.equals(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo(),cost.getCostCode())) {
                vo.setUnitPrice(renterOrderCostRespDTO.getReturnRealAmt());
                vo.setTotalAmount(renterOrderCostRespDTO.getReturnRealAmt());
            } else {
                vo.setUnitPrice(cost.getUnitPrice());
                vo.setTotalAmount(cost.getTotalAmount());
            }
            costItemList.add(vo);
        });

        logger.info("Build costItem list.result is,costItemList:[{}]",JSON.toJSONString(costItemList));
        return costItemList;
    }

    /**
     * 租车费用总计
     *
     * @param costItems 租车费用列表
     * @return TotalCostVO 租车费用总计信息
     */
    public TotalCostVO buildTotalCostVO(List<CostItemVO> costItems) {

        if(CollectionUtils.isEmpty(costItems)) {
            return null;
        }
        int totalFee = costItems.stream().mapToInt(CostItemVO::getTotalAmount).sum();
        TotalCostVO totalCost = new TotalCostVO();
        totalCost.setTotalFee(totalFee);
        logger.info("Build TotalCostVO.result is,totalCost:[{}]",JSON.toJSONString(totalCost));
        return totalCost;
    }


}

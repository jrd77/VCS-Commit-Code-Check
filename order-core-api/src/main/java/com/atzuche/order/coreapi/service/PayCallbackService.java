package com.atzuche.order.coreapi.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.enums.OrderPayStatusEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.settle.service.AccountDebtProxyService;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.event.rabbit.neworder.NewOrderMQStatusEventEnum;

import lombok.extern.slf4j.Slf4j;


/**
 * 订单支付成功 回调子订单
 */
@Service
@Slf4j
public class PayCallbackService implements OrderPayCallBack {

    @Autowired ModifyOrderForRenterService modifyOrderForRenterService;
    @Autowired private DeliveryCarService deliveryCarService;
    @Autowired private OrderStatusService orderStatusService;
    @Autowired private OrderStatusMqService orderStatusMqService;
    
    @Autowired
    AccountDebtProxyService accountDebtProxyService;
    @Autowired
    OrderSupplementDetailService orderSupplementDetailService;
    
    /**
     * ModifyOrderForRenterService.supplementPayPostProcess（修改订单补付回掉）
     */
    @Override
    public void callBack(String menNo,String orderNo,String renterOrderNo,Integer isPayAgain,YesNoEnum isGetCar){
        log.info("PayCallbackService callBack start param menNo=[{}],orderNo=[{}],renterOrderNo=[{}],isPayAgain=[{}],isGetCar=[{}]",menNo,orderNo,renterOrderNo,isPayAgain,isGetCar);
        //APP修改订单补付
        if(YesNoEnum.YES.getCode().equals(isPayAgain) && !StringUtil.isBlank(renterOrderNo)){
            // 修改订单补付成功后回调
            modifyOrderForRenterService.supplementPayPostProcess(orderNo,renterOrderNo);
        }
        
        //发送MQ-仁云
        OrderStatusEntity entity = orderStatusService.getByOrderNo(orderNo);
        if(YesNoEnum.YES.equals(isGetCar)){
            log.info("PayCallbackService sendOrderStatusByOrderNo to_get_car orderStatus,orderNo=[{}]",orderNo);
            orderStatusMqService.sendOrderStatusByOrderNo(orderNo,OrderStatusEnum.TO_GET_CAR.getStatus(), NewOrderMQStatusEventEnum.ORDER_PREGETCAR);
        }
        log.info("PayCallbackService sendDataMessageToRenYun param [{}]", GsonUtils.toJson(entity));
        
        //租车费用已支付 并且非补付支付
        if(!YesNoEnum.YES.getCode().equals(isPayAgain) && Objects.nonNull(entity) && Objects.nonNull(entity.getRentCarPayStatus()) && OrderPayStatusEnum.PAYED.getStatus()==entity.getRentCarPayStatus()){
            log.info("PayCallbackService sendDataMessageToRenYun remote param [{}]", renterOrderNo);
            deliveryCarService.sendDataMessageToRenYun(renterOrderNo);
        }
        log.info("PayCallbackService callBack end param [{}]", GsonUtils.toJson(renterOrderNo));

    }

    /**
     * 结算订单 回调，租车押金结算无需调用修改订单的方法。200312
     * @param orderNo
     * @param renterOrderNo
     */
    @Override
    public void callBackSettle(String orderNo, String renterOrderNo) {
    	//注释掉
//        modifyOrderForRenterService.supplementPayPostProcess(orderNo,renterOrderNo);
    }

    //Integer isPayAgain, YesNoEnum isGetCar,  去掉参数
    //rentAmountAfterRenterOrderNo  管理后台修改增加的补付记录。
	@Override
	public void callBack(String orderNo, List<String> rentAmountAfterRenterOrderNo, List<NotifyDataVo> supplementIds, List<NotifyDataVo> debtIds) {
		//rentAmountAfterRenterOrderNo  无需处理
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>orderSupplementDetail补付回调通知处理:orderNo=[{}]",orderNo);
		if(supplementIds != null) {
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>orderSupplementDetail补付回调通知处理:orderNo=[{}],size=[{}]",orderNo,supplementIds.size());
		}
		//更新supplement
		for (NotifyDataVo vo : supplementIds) {
			String id = vo.getExtendParams();
			String settleAmt = vo.getSettleAmount();
			String memNo = vo.getMemNo();
			String qn = vo.getQn();
			int i = orderSupplementDetailService.updatePayFlagById(Integer.valueOf(id), 3, new Date(), (-Integer.valueOf(settleAmt)));  //order_supplement_detail正负号 取反，根据金额来修改。
			log.info("抵扣补付 result=[{}],params id=[{}],amt=[{}],memNo=[{}],qn=[{}]",i,id, (-Integer.valueOf(settleAmt)), memNo, qn);
			
		}
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>debt欠款回调通知处理:orderNo=[{}]",orderNo);
		if(debtIds != null) {
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>debt欠款回调通知处理:orderNo=[{}],size=[{}]",orderNo,debtIds.size());
		}
		//更新欠款
		for (NotifyDataVo vo : debtIds) {
			String id = vo.getExtendParams();
			String settleAmt = vo.getSettleAmount();
			String memNo = vo.getMemNo();
			String qn = vo.getQn();
			int realDebt = accountDebtProxyService.deductDebtByDebtId(id, Integer.valueOf(settleAmt), memNo, qn);
			log.info("抵扣欠款，真实金额=[{}],params id=[{}],payDebtAmt=[{}],memNo=[{}],qn=[{}]",realDebt,id, Integer.valueOf(settleAmt), memNo, qn);
		}
		
	}
}

/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.cashieraccount.entity.AccountVirtualPayDetailEntity;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.entity.OfflineRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.OfflineRefundApplyService;
import com.atzuche.order.cashieraccount.service.notservice.AccountVirtualPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.vo.res.PaymentRespVO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.vo.res.CashierResVO;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Service
@Slf4j
public class PaymentCashierService {
	@Autowired
	CashierService cashierService;
    @Autowired
    CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired
    OfflineRefundApplyService offlineRefundApplyService;
    @Autowired
    AccountVirtualPayService accountVirtualPayService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
	
	/**
	 * 根据订单号查询支付记录
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public PaymentRespVO queryPaymentList(String orderNo)  {
        PaymentRespVO paymentRespVO = new PaymentRespVO();
		List<CashierResVO> cashierResVOS = new ArrayList<CashierResVO>();
		List<CashierEntity> lst = cashierService.getCashierRentCostsByOrderNo(orderNo);
		for (CashierEntity cashierEntity : lst) {
			CashierResVO vo = new CashierResVO();
			//数据转换
			BeanUtils.copyProperties(cashierEntity,vo);
            cashierResVOS.add(vo);
		}
        //线下支付退款
        List<OfflineRefundApplyEntity> offlineRefundApplyEntityList = offlineRefundApplyService.queryByOrderNo(orderNo);
        List<OfflineRefundApplyDTO> offlineRefundApplyDTOS = new ArrayList<>();
        if(offlineRefundApplyEntityList !=null){
            Optional.ofNullable(offlineRefundApplyEntityList).orElseGet(ArrayList::new).stream().forEach(x->{
                OfflineRefundApplyDTO dto = new OfflineRefundApplyDTO();
                BeanUtils.copyProperties(x,dto);
                offlineRefundApplyDTOS.add(dto);
            });
        }
        //虚拟支付退款表
        List<AccountVirtualPayDetailEntity> accountVirtualPayDetailEntityList = accountVirtualPayService.queryVirtualPayByOrderNo(orderNo);
        List<AccountVirtualPayDetailDTO> accountVirtualPayDetailDTOS = new ArrayList<>();
        if(accountVirtualPayDetailEntityList !=null){
            Optional.ofNullable(accountVirtualPayDetailEntityList).orElseGet(ArrayList::new).stream().forEach(x->{
                AccountVirtualPayDetailDTO dto = new AccountVirtualPayDetailDTO();
                BeanUtils.copyProperties(x,dto);
                accountVirtualPayDetailDTOS.add(dto);
            });
        }
        //真实支付退款表
        List<CashierRefundApplyEntity> refundApplyList = cashierRefundApplyNoTService.getRefundApplyByOrderNo(orderNo);
        List<CashierRefundApplyDTO> cashierRefundApplyDTOS = new ArrayList<>();
        if(refundApplyList !=null){
            Optional.ofNullable(refundApplyList).orElseGet(ArrayList::new).stream().forEach(x->{
                CashierRefundApplyDTO dto = new CashierRefundApplyDTO();
                BeanUtils.copyProperties(x,dto);
                cashierRefundApplyDTOS.add(dto);
            });
        }
        //钱包退款
        List<AccountRenterCostDetailEntity> accountRenterCostDetailEntityList = accountRenterCostDetailNoTService.getAccountRenterCostDetailsByOrderNo(orderNo);
        List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOList = new ArrayList<>();
        if(accountRenterCostDetailEntityList !=null){
            Optional.ofNullable(accountRenterCostDetailEntityList).orElseGet(ArrayList::new).stream().forEach(x->{
                AccountRenterCostDetailDTO dto = new AccountRenterCostDetailDTO();
                BeanUtils.copyProperties(x,dto);
                accountRenterCostDetailDTOList.add(dto);
            });
        }
        //订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        OrderStatusDTO orderStatusDTO = null;
        if(orderStatusEntity != null){
            orderStatusDTO = new OrderStatusDTO();
            BeanUtils.copyProperties(orderStatusEntity,orderStatusDTO);
        }

        paymentRespVO.setAccountVirtualPayDetailDTOList(accountVirtualPayDetailDTOS);
        paymentRespVO.setCashierRefundApplyDTOList(cashierRefundApplyDTOS);
        paymentRespVO.setCashierResVOList(cashierResVOS);
        paymentRespVO.setOfflineRefundApplyDTOList(offlineRefundApplyDTOS);
        paymentRespVO.setOrderStatusDTO(orderStatusDTO);
        paymentRespVO.setAccountRenterCostDetailDTOList(accountRenterCostDetailDTOList);
		return paymentRespVO;
	}
	
}

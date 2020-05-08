package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.FreeDepositModeException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.settle.service.AccountDebtService;
import com.atzuche.order.wallet.api.DebtDetailVO;
import com.autoyol.commons.utils.GsonUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ Author        :  ZhangBin
 * @ CreateDate    :  2019/10/14 16:34
 * @ Description   :  免押方式校验
 *
 */

@Slf4j
@Service("freeDepositModeFilter")
public class FreeDepositModeFilter implements OrderFilter {
	@Autowired
	private AccountDebtService accountDebtService;
	@Autowired
    private OrderSupplementDetailService orderSupplementDetailService;
	
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        String freeDoubleTypeId = orderReqVO.getFreeDoubleTypeId() == null ? null: orderReqVO.getFreeDoubleTypeId();
        int appVersion = orderReqVO.getAppVersion() != null ? Double.valueOf(orderReqVO.getAppVersion()).intValue() : 0;
        String os =  orderReqVO.getOS() != null ? orderReqVO.getOS().toLowerCase() : "";
        if (("Android".equalsIgnoreCase(os) && appVersion >= 100) || ("IOS".equalsIgnoreCase(os) && appVersion >= 95)) {
            if (org.apache.commons.lang.StringUtils.isBlank(freeDoubleTypeId)) {
                throw new FreeDepositModeException();
            }
        }
        
        //如果存在欠款的方式：200506
        String renterNo = orderReqVO.getMemNo();
        DebtDetailVO debtDetailVO = accountDebtService.getTotalNewDebtAndOldDebtAmt(renterNo);
		debtDetailVO.setNoPaySupplementAmt(orderSupplementDetailService.getSumNoPaySupplementAmt(renterNo));
		if(checkDebt(debtDetailVO)) {
			freeDoubleTypeId = "3";//走普通的方式
			context.getOrderReqVO().setFreeDoubleTypeId(freeDoubleTypeId);
	    	log.info("当前会员号memNo=[{}]存在欠款，无法使用芝麻免押。params data=[{}]",renterNo,GsonUtils.toJson(context.getOrderReqVO()));
		}
        

        // 5.10以后需要校验免押方式,如果是绑卡免押，需要校验是否绑卡成功
        /*if ("1".equals(freeDoubleTypeId)) {
            // 是否绑定信用卡
            Integer countBind = freeDepositModeMapper.getTransCardLianlianCount(memRenter.getMemNo()+"");
            if (countBind == null || countBind < 1) {
                throw new FreeDepositModeException(ErrorCode.FREE_DOUBLE_NOT_BIND_ERROR);
            }
        }*/
    }

	private boolean checkDebt(DebtDetailVO vo) {
		if(vo != null){
			log.info("DebtDetailVO vo=[{}]",GsonUtils.toJson(vo));
			if( (vo.getOrderDebtAmt() != null && vo.getOrderDebtAmt().intValue() > 0) 
					|| (vo.getNoPaySupplementAmt() != null && vo.getNoPaySupplementAmt().intValue() > 0)
					|| (vo.getHistoryDebtAmt() != null && vo.getHistoryDebtAmt().intValue() > 0)
					){  //大于0 为欠款，Math.abs
				return true;
			}
		}
		return false;
	}


//    public static void main(String[] args) {
//        System.out.println(Integer.valueOf("108.0"));
//    }
}

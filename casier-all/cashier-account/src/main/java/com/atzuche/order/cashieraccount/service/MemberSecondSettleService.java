/**
 * 
 */
package com.atzuche.order.cashieraccount.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.entity.MemberSecondSettleEntity;
import com.atzuche.order.cashieraccount.mapper.MemberSecondSettleMapper;
import com.autoyol.autopay.gateway.api.AutoPayGatewaySecondaryService;
import com.autoyol.autopay.gateway.vo.BaseOutJB;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Service
@Slf4j
public class MemberSecondSettleService {
	@Autowired
	MemberSecondSettleMapper memberSecondSettleMapper;
	@Autowired
	AutoPayGatewaySecondaryService autoPayGatewaySecondaryService;
	
	public final static Integer DEPOSIT_SETTLE_TYPE = 1;
	public final static Integer DEPOSIT_WZ_SETTLE_TYPE = 2;
	
	
	public void initDepositMemberSecondSettle(String orderNo,Integer memNo) {
		if(memberSecondSettleMapper.getMemberSecondSettleEntityNumber(orderNo, DEPOSIT_SETTLE_TYPE) == 0) {
			MemberSecondSettleEntity record = new MemberSecondSettleEntity();
			record.setMemNo(memNo);
			record.setOrderNo(orderNo);
			record.setSettleType(DEPOSIT_SETTLE_TYPE);
			
			try {
				BaseOutJB out = autoPayGatewaySecondaryService.checkMemberTestConfig(String.valueOf(memNo));
				if(out != null) {
					record.setIsSecondFlow(Integer.valueOf(String.valueOf(out.getData())));
				}
			} catch (Exception e) {
				log.error("checkMemberTestConfig error:",e);
				record.setIsSecondFlow(0);
			}
			
			memberSecondSettleMapper.insertSelective(record);
			
		}
	}
	
	
	public void initDepositWzMemberSecondSettle(String orderNo,Integer memNo) {
		if(memberSecondSettleMapper.getMemberSecondSettleEntityNumber(orderNo, DEPOSIT_WZ_SETTLE_TYPE) == 0) {
			MemberSecondSettleEntity record = new MemberSecondSettleEntity();
			record.setMemNo(memNo);
			record.setOrderNo(orderNo);
			record.setSettleType(DEPOSIT_WZ_SETTLE_TYPE);
			
			try {
				BaseOutJB out = autoPayGatewaySecondaryService.checkMemberTestConfig(String.valueOf(memNo));
				if(out != null) {
					record.setIsSecondFlow(Integer.valueOf(String.valueOf(out.getData())));
				}
			} catch (Exception e) {
				log.error("wz checkMemberTestConfig error:",e);
				record.setIsSecondFlow(0);
			}
			
			memberSecondSettleMapper.insertSelective(record);
		}
	}
}

/**
 *
 */
package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.commons.constant.OrderConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.entity.MemberSecondSettleEntity;
import com.atzuche.order.cashieraccount.mapper.MemberSecondSettleMapper;
import com.autoyol.autopay.gateway.api.AutoPayGatewaySecondaryService;
import com.autoyol.autopay.gateway.vo.BaseOutJB;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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


    public void initDepositMemberSecondSettle(String orderNo, Integer memNo) {
        if (memberSecondSettleMapper.getMemberSecondSettleEntityNumber(orderNo, DEPOSIT_SETTLE_TYPE) == 0) {
            MemberSecondSettleEntity record = new MemberSecondSettleEntity();
            record.setMemNo(memNo);
            record.setOrderNo(orderNo);
            record.setSettleType(DEPOSIT_SETTLE_TYPE);

            try {
                BaseOutJB out = autoPayGatewaySecondaryService.checkMemberTestConfig(String.valueOf(memNo));
                if (out != null) {
                    record.setIsSecondFlow(Integer.valueOf(String.valueOf(out.getData())));
                } else {
                	record.setIsSecondFlow(0);
                }
            } catch (Exception e) {
                log.error("checkMemberTestConfig error:", e);
                record.setIsSecondFlow(0);
            }

            memberSecondSettleMapper.insertSelective(record);

        }
    }


    public void initDepositWzMemberSecondSettle(String orderNo, Integer memNo) {
        if (memberSecondSettleMapper.getMemberSecondSettleEntityNumber(orderNo, DEPOSIT_WZ_SETTLE_TYPE) == 0) {
            MemberSecondSettleEntity record = new MemberSecondSettleEntity();
            record.setMemNo(memNo);
            record.setOrderNo(orderNo);
            record.setSettleType(DEPOSIT_WZ_SETTLE_TYPE);

            try {
                BaseOutJB out = autoPayGatewaySecondaryService.checkMemberTestConfig(String.valueOf(memNo));
                if (out != null) {
                    record.setIsSecondFlow(Integer.valueOf(String.valueOf(out.getData())));
                } else {
                	record.setIsSecondFlow(0);
                }
            } catch (Exception e) {
                log.error("wz checkMemberTestConfig error:", e);
                record.setIsSecondFlow(0);
            }

            memberSecondSettleMapper.insertSelective(record);
        }
    }

    /**
     * 判断会员指定的订单是否走上海二清支付流程
     * <p>注:只判断租车费用+车辆押金</p>
     * <p><font color=red>注:使用于车主收益变更接口</font></p>
     *
     * @param memNo   会员号,必传
     * @param orderNo 订单号,非必传
     * @return boolean true-是 false-否
     */
    public boolean judgeIsSecond(Integer memNo, String orderNo) {
        log.info("Determine if the order is paid in two clear. param is,memNo:[{}], orderNo:[{}]", memNo, orderNo);
        if (StringUtils.isBlank(orderNo)) {
            return false;
        }
        Integer result = memberSecondSettleMapper.selectByCondition(memNo, orderNo, OrderConstant.ONE,
                OrderConstant.YES);
        return Objects.nonNull(result) && result > OrderConstant.ZERO;
    }
}

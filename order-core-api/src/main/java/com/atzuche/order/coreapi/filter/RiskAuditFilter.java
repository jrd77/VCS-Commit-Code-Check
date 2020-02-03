package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.entity.vo.req.SubmitOrderRiskCheckReqVO;
import com.atzuche.order.coreapi.service.SubmitOrderRiskAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 风控过滤检查
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 4:47 下午
 **/
@Service
public class RiskAuditFilter implements OrderFilter {
    @Autowired
    private SubmitOrderRiskAuditService submitOrderRiskAuditService;
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {

        String riskAuditId = submitOrderRiskAuditService.check(buildSubmitOrderRiskCheckReqVO(context.getOrderReqVO(), context.getOrderReqVO().getReqTime(),
                context.getRenterGoodsDetailDto().getWeekendPrice()));
        context.setRiskAuditId(riskAuditId);
    }

    private SubmitOrderRiskCheckReqVO buildSubmitOrderRiskCheckReqVO(OrderReqVO orderReqVO, LocalDateTime reqTime,
                                                                     Integer weekendPrice) {

        SubmitOrderRiskCheckReqVO submitOrderRiskCheckReqVO = new SubmitOrderRiskCheckReqVO();
        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, SubmitOrderRiskCheckReqVO.class, false);
        beanCopier.copy(orderReqVO, submitOrderRiskCheckReqVO, null);
        submitOrderRiskCheckReqVO.setReqTime(LocalDateTimeUtils.localDateTimeToDate(reqTime));
        submitOrderRiskCheckReqVO.setWeekendPrice(weekendPrice);
        return submitOrderRiskCheckReqVO;
    }
}

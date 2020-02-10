package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.NotRentSelfCarException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 2:33 下午
 **/
@Service("notRentSelfCarFilter")
public class NotRentSelfCarFilter implements OrderFilter {
    private final static Logger logger = LoggerFactory.getLogger(NotRentSelfCarFilter.class);
    
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
       OrderReqVO orderReqVO = context.getOrderReqVO();
       String renterNo = orderReqVO.getMemNo();
       String ownerNo = context.getOwnerMemberDto().getMemNo();
       if(renterNo.equalsIgnoreCase(ownerNo)){
           logger.error("“(renterNo={})”不能租用自己的车辆“carNo={}”", renterNo, context.getOrderReqVO().getCarNo());
           throw new NotRentSelfCarException();
       }
    }
}

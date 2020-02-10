package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 9:44 上午
 **/
@Service
public class OrderFilterChain implements OrderFilter, ApplicationContextAware {

    private List<OrderFilter> orderFilterList = new ArrayList<>();

    private ApplicationContext applicationContext;


    @PostConstruct
    private void init(){
       orderFilterList.add(applicationContext.getBean(ParamInputFilter.class));
       orderFilterList.add(applicationContext.getBean(NotRentSelfCarFilter.class));
       orderFilterList.add(applicationContext.getBean(StockFilter.class));
    }


    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
       if(orderFilterList.size()==0){
           throw new RuntimeException("cannot be init");
       }
       for(OrderFilter filter:orderFilterList){
           filter.validate(context);
       }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

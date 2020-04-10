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


   // @PostConstruct
    private void init() {
        /* 参数检测 */
        orderFilterList.add(applicationContext.getBean(ParamInputFilter.class));
        /*经纬度转换校验*/
        orderFilterList.add(applicationContext.getBean(ConvertLatOrLonFilter.class));
        /*免押方式校验*/
        orderFilterList.add(applicationContext.getBean(FreeDepositModeFilter.class));
        /* 不能自己租自己 的车的检查 */
        orderFilterList.add(applicationContext.getBean(NotRentSelfCarFilter.class));
        /*平台显示校验*/
        orderFilterList.add(applicationContext.getBean(PlatformShowFilter.class));
        /*取还车时间校验*/
        //orderFilterList.add(applicationContext.getBean(RentRevertTimeCheckFilter.class));
        /*取还车服务起租时间需大于4小时校验*/
        //orderFilterList.add(applicationContext.getBean(RenterTime4HourFilter.class));
        /*车辆设置校验*/
       // orderFilterList.add(applicationContext.getBean(CarSettingCheckFilter.class));
        /*城市凹凸服务点判断*/
        orderFilterList.add(applicationContext.getBean(CityLonLatFilter.class));
        /*机场服务点校验*/
        orderFilterList.add(applicationContext.getBean(AirportServiceFilter.class));
        /*15分钟内重复下单校验*/
        /* orderFilterList.add(applicationContext.getBean(WaitOwner15minuteReplyFilter.class));*/
        /*准驾车型校验*/
        orderFilterList.add(applicationContext.getBean(CheckSuitCarAndDriLicFilter.class));
        /* 库存检查 */
        // orderFilterList.add(applicationContext.getBean(StockFilter.class));
        /* 风控检查 */
       // orderFilterList.add(applicationContext.getBean(RiskAuditFilter.class));

    }


    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        if (orderFilterList.size() == 0) {
            throw new RuntimeException("cannot be init");
        }
        for (OrderFilter filter : orderFilterList) {
            filter.validate(context);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

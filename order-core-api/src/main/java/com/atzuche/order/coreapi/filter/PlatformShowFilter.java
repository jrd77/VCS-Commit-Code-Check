package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.PlatformShowException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import org.springframework.stereotype.Service;

/**
 * @ Author        :  ZhangBin
 * @ CreateDate    :  2019/10/14 16:34
 * @ Description   :  平台显示校验
 *
 */
@Service("platformShowFilter")
public class PlatformShowFilter implements OrderFilter {
    private static final int IS_PLATFORM_SHOW = 1;//是否在平台显示  1：是，2：不是
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        String carNo = orderReqVO.getCarNo();
        String sceneCode = orderReqVO.getSceneCode();  //根据场景来源来判断。
        //校验是否在平台显示
        if(!GlobalConstant.DISTRIBUTE_VIRTUAL_CARNO.equals(carNo)
                && !GlobalConstant.CONSOLE_SITE.equals(sceneCode)
                && !GlobalConstant.H5_CPIC_CAR.equals(sceneCode)){  //虚拟车无需验证
            //针对管理后台下单，需要放开该限制, 平台不可见不允许下单的限制。
            if(isNotFavorites(sceneCode)){
                Integer isPlatformShow = context.getRenterGoodsDetailDto().getIsPlatformShow();
                if(null==isPlatformShow)isPlatformShow = IS_PLATFORM_SHOW;//如果为空则默认为在平台显示 1
                //是否在平台显示  1：是，2：不是
                if(IS_PLATFORM_SHOW!=isPlatformShow){
                    throw new PlatformShowException();
                }
            }
        }
    }
    /**
     * 是否是非收藏列表进入
     * @param sceneCode
     * @return
     */
    public static boolean isNotFavorites(String sceneCode){
//		return !Objects.equals("EX001",sceneCode) && !Objects.equals("EX005",sceneCode);
        return Boolean.FALSE;
    }
}

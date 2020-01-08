package com.atzuche.order.coreapi.service;

import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.entity.vo.req.CarDispatchReqVO;
import com.atzuche.order.renterorder.service.OrderCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 车主取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 16:22
 */
@Service
public class OwnerCancelOrderService {

    @Autowired
    private CarRentalTimeApiService carRentalTimeApiService;

    @Autowired
    OrderCouponService orderCouponService;

    /**
     * 取消处理
     *
     * @return CancelOrderResDTO 返回信息
     */
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancel() {
        //校验
        //todo

        //调度判定
        //todo
        boolean isDispatch = carRentalTimeApiService.checkCarDispatch(null);
        //罚金计算(罚金和收益)
        //todo

        //订单状态更新
        //todo

        //去库存
        //todo

        //落库
        //todo


        return null;

    }



    public CarDispatchReqVO buildCarDispatchReqVO(){

        CarDispatchReqVO carDispatchReqVO = new CarDispatchReqVO();

        carDispatchReqVO.setType(2);




        return carDispatchReqVO;
    }


}

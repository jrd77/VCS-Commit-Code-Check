package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.coreapi.entity.vo.req.CarRentTimeRangeReqVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.service.RenterOrderCalCostService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

/**
 * 下单前费用计算
 *
 * @author pengcheng.fu
 * @date 2020/1/11 16:11
 */
@Service
public class SubmitOrderBeforeCostCalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderBeforeCostCalService.class);

    @Autowired
    private RenterOrderCalCostService renterOrderCalCostService;

    @Autowired
    private CarRentalTimeApiService carRentalTimeApiService;



    /**
     * 下单前费用计算
     *
     * @param orderReqVO 请求参数
     * @return NormalOrderCostCalculateResVO 返回信息
     */
    public NormalOrderCostCalculateResVO costCalculate(OrderReqVO orderReqVO) {

        //TODO:租车费用处理

        //提前延后时间计算
        CarRentTimeRangeResVO carRentTimeRangeResVO = carRentalTimeApiService.getCarRentTimeRange(buildCarRentTimeRangeReqVO(orderReqVO));




        RenterOrderCostRespDTO renterOrderCostRespDTO =
                renterOrderCalCostService.getOrderCostAndDeailList(new RenterOrderCostReqDTO());





        //TODO:抵扣费用处理



        //TODO:车辆押金处理

        //TODO:违章押金处理

        //TODO:租车费用小计处理

        //TODO:待支付信息处理

        return new NormalOrderCostCalculateResVO();

    }



    public RenterOrderReqVO buildRenterOrderReqVO() {


        return null;
    }


    /**
     * 提前延后时间计算请求参数封装
     *
     * @param orderReqVO 下单请求参数
     * @return CarRentTimeRangeReqVO 提前延后时间计算请求参数
     */
    private CarRentTimeRangeReqVO buildCarRentTimeRangeReqVO(OrderReqVO orderReqVO) {
        CarRentTimeRangeReqVO carRentTimeRangeReqVO = new CarRentTimeRangeReqVO();
        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, CarRentTimeRangeReqVO.class, false);
        beanCopier.copy(orderReqVO, carRentTimeRangeReqVO, null);

        LOGGER.info("Submit order before build CarRentTimeRangeReqVO,result is ,carRentTimeRangeReqVO:[{}]",
                JSON.toJSONString(carRentTimeRangeReqVO));
        return carRentTimeRangeReqVO;
    }


}

package com.atzuche.order.admin.service.remark;

import com.atzuche.order.admin.mapper.remark.OrderRemarkMapper;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkRequestVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkOverviewListResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkOverviewResponseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderRemarkService {


    @Autowired
    private OrderRemarkMapper orderRemarkMapper;

    /**
     * 获取备注总览
     * @param orderRemarkRequestVO
     * @return
     */
    public OrderRemarkOverviewListResponseVO getOrderRemarkOverview(OrderRemarkRequestVO orderRemarkRequestVO){
        List<OrderRemarkOverviewResponseVO> orderRemarkOverview = new ArrayList();
        BeanUtils.copyProperties(orderRemarkMapper.getOrderRemarkOverview(orderRemarkRequestVO),orderRemarkOverview);
        OrderRemarkOverviewListResponseVO orderRemarkOverviewListResponseVO = new OrderRemarkOverviewListResponseVO();
        orderRemarkOverviewListResponseVO.setRemarkOverviewList(orderRemarkOverview);
        return orderRemarkOverviewListResponseVO;

    }


}

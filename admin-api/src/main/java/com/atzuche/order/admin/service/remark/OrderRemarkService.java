package com.atzuche.order.admin.service.remark;

import com.atzuche.order.admin.dto.OrderRemarkAdditionRequestDTO;
import com.atzuche.order.admin.entity.OrderRemarkOverviewEntity;
import com.atzuche.order.admin.enums.RemarkTypeEnum;
import com.atzuche.order.admin.mapper.OrderRemarkMapper;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkAdditionRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkInformationRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkRequestVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkOverviewListResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkOverviewResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderRemarkService {

    private static final Logger logger = LoggerFactory.getLogger(OrderRemarkService.class);

    @Autowired
    private OrderRemarkMapper orderRemarkMapper;

    /**
     * 获取备注总览
     * @param orderRemarkRequestVO
     * @return
     */
    public OrderRemarkOverviewListResponseVO getOrderRemarkOverview(OrderRemarkRequestVO orderRemarkRequestVO) {
        List<OrderRemarkOverviewResponseVO> orderRemarkOverviewList = new ArrayList();
        List<OrderRemarkOverviewEntity> orderRemarkOverviewEntityList = orderRemarkMapper.getOrderRemarkOverview(orderRemarkRequestVO);
        OrderRemarkOverviewListResponseVO orderRemarkOverviewListResponseVO = new OrderRemarkOverviewListResponseVO();
        if (!CollectionUtils.isEmpty(orderRemarkOverviewEntityList)) {
            for (OrderRemarkOverviewEntity orderRemarkOverviewEntity : orderRemarkOverviewEntityList) {
                OrderRemarkOverviewResponseVO orderRemarkOverviewResponseVO = new OrderRemarkOverviewResponseVO();
                BeanUtils.copyProperties(orderRemarkOverviewEntity, orderRemarkOverviewResponseVO);
                orderRemarkOverviewResponseVO.setRemarkTypeText(RemarkTypeEnum.getDescriptionByType(orderRemarkOverviewResponseVO.getRemarkType()));
                orderRemarkOverviewList.add(orderRemarkOverviewResponseVO);
            }
            orderRemarkOverviewListResponseVO.setRemarkOverviewList(orderRemarkOverviewList);
        }
        return orderRemarkOverviewListResponseVO;
    }

    /**
     * 添加备注
     * @param orderRemarkAdditionRequestVO
     */
    public void addOrderRemark(OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO) {
        OrderRemarkAdditionRequestDTO orderRemarkAdditionRequestDTO = new OrderRemarkAdditionRequestDTO();
        BeanUtils.copyProperties(orderRemarkAdditionRequestVO,orderRemarkAdditionRequestDTO);
        orderRemarkMapper.addOrderRemark(orderRemarkAdditionRequestDTO);
    }

    public OrderRemarkResponseVO getOrderRemarkInformation(OrderRemarkInformationRequestVO orderRemarkInformationRequestVO){
        return orderRemarkMapper.getOrderRemarkInformation(orderRemarkInformationRequestVO);
    }

}

package com.atzuche.order.admin.mapper;

import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.entity.OrderRemarkLogEntity;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkListRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkLogListRequestVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface OrderRemarkLogMapper {


    /**
     * 添加备注操作日志
     * @param orderRemarkLogEntity
     */
    void addOrderRemarkLog(OrderRemarkLogEntity orderRemarkLogEntity);

    List<OrderRemarkLogEntity> selectRemarkLogList(OrderRemarkLogListRequestVO orderRemarkLogListRequestVO);

    long selectRemarkLogListCount(OrderRemarkLogListRequestVO orderRemarkLogListRequestVO);


}

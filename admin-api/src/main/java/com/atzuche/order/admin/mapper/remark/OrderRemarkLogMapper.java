package com.atzuche.order.admin.mapper.remark;

import com.atzuche.order.admin.dto.remark.OrderRemarkLogListRequestDTO;
import com.atzuche.order.admin.entity.OrderRemarkLogEntity;
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

    List<OrderRemarkLogEntity> selectRemarkLogList(OrderRemarkLogListRequestDTO orderRemarkLogListRequestDTO);

    long selectRemarkLogListCount(OrderRemarkLogListRequestVO orderRemarkLogListRequestVO);


}

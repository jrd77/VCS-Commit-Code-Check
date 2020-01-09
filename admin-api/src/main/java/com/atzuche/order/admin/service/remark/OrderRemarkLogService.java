package com.atzuche.order.admin.service.remark;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.entity.OrderRemarkLogEntity;
import com.atzuche.order.admin.enums.OperateTypeEnum;
import com.atzuche.order.admin.mapper.OrderRemarkLogMapper;
import com.atzuche.order.admin.mapper.OrderRemarkMapper;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkInformationRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderRemarkLogService {

    private static final Logger logger = LoggerFactory.getLogger(OrderRemarkLogService.class);

    @Autowired
    private OrderRemarkService orderRemarkService;
    @Autowired
    private OrderRemarkLogMapper orderRemarkLogMapper;


    /**
     * 保存操作日志
     * @param orderRemarkEntity
     * @param operateType
     */
    public void addOrderRemarkLog(OrderRemarkEntity orderRemarkEntity, String operateType){
        //保存操作日志
        OrderRemarkEntity oldOrderRemarkEntity = orderRemarkService.getOrderRemarkInformation(orderRemarkEntity.getRemarkId());
        OrderRemarkLogEntity orderRemarkLogEntity = new OrderRemarkLogEntity();
        BeanUtils.copyProperties(orderRemarkEntity, orderRemarkLogEntity);
        orderRemarkLogEntity.setOperateType(operateType);
        orderRemarkLogEntity.setRemarkHistory(oldOrderRemarkEntity.getRemarkContent());
        String userName = AdminUserUtil.getAdminUser().getAuthName();
        orderRemarkLogEntity.setOrderNo(oldOrderRemarkEntity.getOrderNo());
        orderRemarkLogEntity.setCreateOp(userName);
        orderRemarkLogEntity.setUpdateOp(userName);
        orderRemarkLogEntity.setRemarkType(oldOrderRemarkEntity.getRemarkType());
        orderRemarkLogEntity.setNumber(oldOrderRemarkEntity.getNumber());
        orderRemarkLogEntity.setDepartmentId(oldOrderRemarkEntity.getDepartmentId());
        String remarkContent = oldOrderRemarkEntity.getRemarkContent();
        orderRemarkLogEntity.setRemarkContent(remarkContent);
        orderRemarkLogMapper.addOrderRemarkLog(orderRemarkLogEntity);
    }

}

package com.atzuche.order.admin.service.remark;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.common.PageBean;
import com.atzuche.order.admin.dto.OrderRemarkLogListRequestDTO;
import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.entity.OrderRemarkLogEntity;
import com.atzuche.order.admin.enums.DepartmentEnum;
import com.atzuche.order.admin.enums.OperateTypeEnum;
import com.atzuche.order.admin.enums.RemarkTypeEnum;
import com.atzuche.order.admin.mapper.OrderRemarkLogMapper;
import com.atzuche.order.admin.mapper.OrderRemarkMapper;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkInformationRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkListRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkLogListRequestVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkListResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkLogListResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkLogPageListResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkPageListResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


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


    /**
     * 查询备注日志列表
     * @param orderRemarkLogListRequestVO
     * @return
     */
    public OrderRemarkLogPageListResponseVO selectRemarkLoglist(OrderRemarkLogListRequestVO orderRemarkLogListRequestVO){
        OrderRemarkLogPageListResponseVO orderRemarkLogPageListResponseVO = new OrderRemarkLogPageListResponseVO();
        List<OrderRemarkLogListResponseVO> orderRemarkPageList = new ArrayList<>();
        long count = orderRemarkLogMapper.selectRemarkLogListCount(orderRemarkLogListRequestVO);
        OrderRemarkLogListRequestDTO orderRemarkLogListRequestDTO = new OrderRemarkLogListRequestDTO(count);
        BeanUtils.copyProperties(orderRemarkLogListRequestVO, orderRemarkLogListRequestDTO);
        List<OrderRemarkLogEntity> remarkList = orderRemarkLogMapper.selectRemarkLogList(orderRemarkLogListRequestDTO);
        if(!CollectionUtils.isEmpty(remarkList)) {
            remarkList.forEach(remarkLogEntity -> {
                OrderRemarkLogListResponseVO orderRemarkLogListResponseVO = new OrderRemarkLogListResponseVO();
                orderRemarkLogListResponseVO.setNumber(remarkLogEntity.getNumber());
                orderRemarkLogListResponseVO.setRemarkType(RemarkTypeEnum.getDescriptionByType(remarkLogEntity.getRemarkType()));
                orderRemarkLogListResponseVO.setOperateType(OperateTypeEnum.getDescriptionByType(remarkLogEntity.getOperateType()));
                orderRemarkLogListResponseVO.setOperatorName(remarkLogEntity.getCreateOp());
                orderRemarkLogListResponseVO.setOperateTime(remarkLogEntity.getCreateTime());
                orderRemarkLogListResponseVO.setOldRemarkContent(remarkLogEntity.getRemarkHistory());
                orderRemarkLogListResponseVO.setNewRemarkContent(remarkLogEntity.getRemarkContent());
                orderRemarkLogListResponseVO.setDepartmentName(DepartmentEnum.getDescriptionByType(remarkLogEntity.getDepartmentId()));
                orderRemarkPageList.add(orderRemarkLogListResponseVO);
            });
        }

        orderRemarkLogPageListResponseVO.setPageNumber(String.valueOf(orderRemarkLogListRequestDTO.getPageNumber()));
        orderRemarkLogPageListResponseVO.setPages(String.valueOf(orderRemarkLogListRequestDTO.getPages()));
        orderRemarkLogPageListResponseVO.setPageSize(String.valueOf(orderRemarkLogListRequestDTO.getPageSize()));
        orderRemarkLogPageListResponseVO.setOrderRemarkLogList(orderRemarkPageList);
        return orderRemarkLogPageListResponseVO;
    }

}

package com.atzuche.order.admin.service.log;

import com.atzuche.order.admin.common.AdminUser;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.dto.log.AdminOperateLogDTO;
import com.atzuche.order.admin.entity.AdminOperateLogEntity;
import com.atzuche.order.admin.mapper.log.AdminOperateLogMapper;
import com.atzuche.order.admin.mapper.log.QueryVO;
import com.atzuche.order.admin.vo.req.log.LogQueryVO;
import com.atzuche.order.commons.DateUtils;
import com.autoyol.commons.web.ResponseData;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/14 3:53 下午
 **/
@Service
public class AdminLogService {


    @Autowired
    private AdminOperateLogMapper adminOperateLogMapper;

    /**
     * 插入日志
     *
     * @param type    操作类型
     * @param orderNo 订单号
     * @param desc    操作内容
     */
    public void insertLog(AdminOpTypeEnum type, String orderNo, String desc) {
        AdminOperateLogEntity entity = new AdminOperateLogEntity();
        entity.setOrderNo(orderNo);
        entity.setDesc(desc);
        entity.setOpTypeCode(type.getOpCode());
        entity.setOpTypeDesc(type.getOpType());
        entity.setOperatorName(AdminUserUtil.getAdminUser().getAuthName());
        entity.setOperatorId(AdminUserUtil.getAdminUser().getAuthId());
        adminOperateLogMapper.insertLog(entity);
    }
    /**
     * 插入日志
     *
     * @param type    操作类型
     * @param orderNo 订单号
     * @param desc    操作内容
     */
    public void insertLog(AdminOpTypeEnum type, String orderNo,String renterOrderNo,String ownerOrderNo, String desc) {
        AdminOperateLogEntity entity = new AdminOperateLogEntity();
        entity.setOrderNo(orderNo);
        entity.setRenterOrderNo(renterOrderNo);
        entity.setOwnerOrderNo(ownerOrderNo);
        entity.setDesc(desc);
        entity.setOpTypeCode(type.getOpCode());
        entity.setOpTypeDesc(type.getOpType());
        entity.setOperatorName(AdminUserUtil.getAdminUser().getAuthName());
        entity.setOperatorId(AdminUserUtil.getAdminUser().getAuthId());
        adminOperateLogMapper.insertLog(entity);
    }
    public List<AdminOperateLogEntity> findByOrderNo(String orderNo) {
        return adminOperateLogMapper.findAll(orderNo);
    }

    public List<AdminOperateLogDTO> findByQueryVO(QueryVO req) {
        List<AdminOperateLogEntity> list = adminOperateLogMapper.findByQuery(req);

        List<AdminOperateLogDTO> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(log -> {
                AdminOperateLogDTO dto = new AdminOperateLogDTO();
                BeanUtils.copyProperties(log, dto);
                dto.setOperateTime(DateUtils.formate(log.getCreateTime(), DateUtils.DATE_DEFAUTE1));
                dtos.add(dto);
            });
        }
        return dtos;
    }
}

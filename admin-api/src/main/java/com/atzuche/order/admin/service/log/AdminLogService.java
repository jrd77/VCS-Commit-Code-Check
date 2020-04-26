package com.atzuche.order.admin.service.log;

import com.atzuche.order.admin.common.AdminUser;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.entity.AdminOperateLogEntity;
import com.atzuche.order.admin.mapper.log.AdminOperateLogMapper;
import com.atzuche.order.admin.mapper.log.QueryVO;
import com.atzuche.order.admin.vo.req.log.LogQueryVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<AdminOperateLogEntity> findByOrderNo(String orderNo) {
        return adminOperateLogMapper.findAll(orderNo);
    }

    public List<AdminOperateLogEntity> findByQueryVO(QueryVO req) {
        return adminOperateLogMapper.findByQuery(req);


    }
}

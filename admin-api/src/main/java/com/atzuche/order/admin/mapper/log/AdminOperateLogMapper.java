package com.atzuche.order.admin.mapper.log;

import com.atzuche.order.admin.entity.AdminOperateLogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/14 3:47 下午
 **/
@Mapper
public interface AdminOperateLogMapper {
    /**
     * 查询该订单的所有相关操作日志
     * @param orderNo
     * @return
     */
    public List<AdminOperateLogEntity>  findAll(String orderNo);

    /**
     * 查询接口
     * @param queryVO
     * @return
     */
    public List<AdminOperateLogEntity> findAll(QueryVO queryVO);

    /**
     * 插入一条日志
     * @param entity
     */
    void insertLog(AdminOperateLogEntity entity);
}

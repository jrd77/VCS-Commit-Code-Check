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
     *
     * @param orderNo 订单号
     * @return List<AdminOperateLogEntity>
     */
    List<AdminOperateLogEntity> findAll(String orderNo);

    /**
     * 查询接口
     *
     * @param queryVO 查询条件
     * @return List<AdminOperateLogEntity>
     */
    List<AdminOperateLogEntity> findByQuery(QueryVO queryVO);

    /**
     * 插入一条日志
     *
     * @param entity 操作日志
     */
    void insertLog(AdminOperateLogEntity entity);
}

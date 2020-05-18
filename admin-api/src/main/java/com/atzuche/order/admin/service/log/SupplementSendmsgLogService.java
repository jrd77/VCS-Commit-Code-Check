package com.atzuche.order.admin.service.log;

import com.atzuche.order.admin.entity.SupplementSendmsgLog;

import java.util.List;

/**
 * 发送补付消息记录(SupplementSendmsgLog)表服务接口
 *
 * @author makejava
 * @since 2020-05-18 20:46:29
 */
public interface SupplementSendmsgLogService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SupplementSendmsgLog queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<SupplementSendmsgLog> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param supplementSendmsgLog 实例对象
     * @return 实例对象
     */
    SupplementSendmsgLog insert(SupplementSendmsgLog supplementSendmsgLog);

    /**
     * 修改数据
     *
     * @param supplementSendmsgLog 实例对象
     * @return 实例对象
     */
    SupplementSendmsgLog update(SupplementSendmsgLog supplementSendmsgLog);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

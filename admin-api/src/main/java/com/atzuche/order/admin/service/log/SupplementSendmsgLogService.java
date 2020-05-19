package com.atzuche.order.admin.service.log;


import com.atzuche.order.admin.entity.SupplementSendmsgLog;
import com.atzuche.order.admin.mapper.log.SupplementSendmsgLogDao;
import com.atzuche.order.admin.vo.req.supplement.BufuMessagePushRecordListReqVO;
import com.atzuche.order.admin.vo.req.supplement.MessagePushSendReqVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 发送补付消息记录(SupplementSendmsgLog)表服务实现类
 *
 * @author makejava
 * @since 2020-05-18 20:46:30
 */
@Service("supplementSendmsgLogService")
public class SupplementSendmsgLogService {
    @Resource
    private SupplementSendmsgLogDao supplementSendmsgLogDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */

    public SupplementSendmsgLog queryById(Long id) {
        return this.supplementSendmsgLogDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */

    public List<SupplementSendmsgLog> queryAllByLimit(int offset, int limit) {
        return this.supplementSendmsgLogDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param reqVO 实例对象
     * @return 实例对象
     */

    public Integer insert(MessagePushSendReqVO reqVO) {
        SupplementSendmsgLog supplementSendmsgLog = new SupplementSendmsgLog();
        BeanUtils.copyProperties(reqVO,supplementSendmsgLog);
        int insert = this.supplementSendmsgLogDao.insert(supplementSendmsgLog);
        return insert;
    }

    /**
     * 修改数据
     *
     * @param supplementSendmsgLog 实例对象
     * @return 实例对象
     */

    public SupplementSendmsgLog update(SupplementSendmsgLog supplementSendmsgLog) {
        this.supplementSendmsgLogDao.update(supplementSendmsgLog);
        return this.queryById(supplementSendmsgLog.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */

    public boolean deleteById(Long id) {
        return this.supplementSendmsgLogDao.deleteById(id) > 0;
    }

    public ResponseData<?> selectByPage(BufuMessagePushRecordListReqVO reqVO) {
        return null;
    }
}

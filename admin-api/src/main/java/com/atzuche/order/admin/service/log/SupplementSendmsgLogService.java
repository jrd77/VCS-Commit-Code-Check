package com.atzuche.order.admin.service.log;


import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.common.Page;
import com.atzuche.order.admin.entity.SupplementSendmsgLog;
import com.atzuche.order.admin.mapper.log.SupplementSendmsgLogMapper;
import com.atzuche.order.admin.vo.req.supplement.BufuMessagePushRecordListReqVO;
import com.atzuche.order.admin.vo.req.supplement.MessagePushSendReqVO;
import com.atzuche.order.admin.vo.resp.supplement.MessagePushRecordListResVO;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderSupplementPayMq;
import com.github.pagehelper.PageHelper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发送补付消息记录(SupplementSendmsgLog)表服务实现类
 *
 * @author makejava
 * @since 2020-05-18 20:46:30
 */
@Service("supplementSendmsgLogService")
public class SupplementSendmsgLogService {
    @Autowired
    private SupplementSendmsgLogMapper supplementSendmsgLogMapper;

    @Autowired
    private BaseProducer baseProducer;

    private static org.slf4j.Logger log = LoggerFactory.getLogger(SupplementSendmsgLogService.class);
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */

    public SupplementSendmsgLog queryById(Long id) {
        return this.supplementSendmsgLogMapper.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */

    public List<SupplementSendmsgLog> queryAllByLimit(int offset, int limit) {
        return this.supplementSendmsgLogMapper.queryAllByLimit(offset, limit);
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
        OrderMessage orderMessage = OrderMessage.builder().build();
        OrderSupplementPayMq orderSupplementPayMq= new OrderSupplementPayMq();
        orderSupplementPayMq.setAmount("100");
        orderSupplementPayMq.setItem("测试补付项目");
        orderSupplementPayMq.setOrderNo("81163141500299");
        orderMessage.setMessage(orderSupplementPayMq);
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.SUPPLEMENT_PAY_MASSAGE.exchange,
                NewOrderMQActionEventEnum.SUPPLEMENT_PAY_MASSAGE.routingKey, orderMessage);
        log.info("插入消息记录数据库入参"+ JSON.toJSONString(supplementSendmsgLog));
        int insert = this.supplementSendmsgLogMapper.insertSelective(supplementSendmsgLog);
        return insert;
    }

    /**
     * 修改数据
     *
     * @param supplementSendmsgLog 实例对象
     * @return 实例对象
     */

    public SupplementSendmsgLog update(SupplementSendmsgLog supplementSendmsgLog) {
        this.supplementSendmsgLogMapper.update(supplementSendmsgLog);
        return this.queryById(supplementSendmsgLog.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */

    public boolean deleteById(Long id) {
        return this.supplementSendmsgLogMapper.deleteById(id) > 0;
    }

    public Page<MessagePushRecordListResVO> selectByPage(BufuMessagePushRecordListReqVO reqVO) {
        //开启分页插件
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<MessagePushRecordListResVO> bufuRecordListByPage = supplementSendmsgLogMapper.selectByPage(reqVO);
        return new Page<>(bufuRecordListByPage);
    }

}

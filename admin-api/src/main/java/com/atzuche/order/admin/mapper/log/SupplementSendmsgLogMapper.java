package com.atzuche.order.admin.mapper.log;


import com.atzuche.order.admin.entity.SupplementSendmsgLog;
import com.atzuche.order.admin.vo.req.supplement.BufuMessagePushRecordListReqVO;
import com.atzuche.order.admin.vo.resp.supplement.MessagePushRecordListResVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 发送补付消息记录(SupplementSendmsgLog)表数据库访问层
 *
 * @author makejava
 * @since 2020-05-18 20:46:28
 */
@Mapper
public interface SupplementSendmsgLogMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SupplementSendmsgLog queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<SupplementSendmsgLog> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param supplementSendmsgLog 实例对象
     * @return 对象列表
     */
    List<SupplementSendmsgLog> queryAll(SupplementSendmsgLog supplementSendmsgLog);

    /**
     * 新增数据
     *
     * @param supplementSendmsgLog 实例对象
     * @return 影响行数
     */
    int insert(SupplementSendmsgLog supplementSendmsgLog);

    /**
     * 修改数据
     *
     * @param supplementSendmsgLog 实例对象
     * @return 影响行数
     */
    int update(SupplementSendmsgLog supplementSendmsgLog);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);
    /**
     * 新增数据
     *
     * @param supplementSendmsgLog 实例对象
     * @return 影响行数
     */
    int insertSelective(SupplementSendmsgLog supplementSendmsgLog);

    List<MessagePushRecordListResVO> selectByPage(BufuMessagePushRecordListReqVO reqVO);
}

package com.atzuche.order.admin.mapper.log;

import com.atzuche.order.admin.entity.WeizhangSendmsgLog;
import com.atzuche.order.admin.vo.req.renterWz.WzMessagePushRecordListReqVO;
import com.atzuche.order.admin.vo.resp.renterWz.WzMessagePushRecordListResVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WeizhangSendmsgLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WeizhangSendmsgLog record);

    int insertSelective(WeizhangSendmsgLog record);

    WeizhangSendmsgLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WeizhangSendmsgLog record);

    int updateByPrimaryKey(WeizhangSendmsgLog record);

    List<WzMessagePushRecordListResVO> selectByPage(WzMessagePushRecordListReqVO reqVO);
}
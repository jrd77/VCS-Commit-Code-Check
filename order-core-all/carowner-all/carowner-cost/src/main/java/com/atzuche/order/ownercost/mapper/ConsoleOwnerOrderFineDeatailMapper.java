package com.atzuche.order.ownercost.mapper;

import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 全局的车主订单罚金明细表
 *
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Mapper
public interface ConsoleOwnerOrderFineDeatailMapper {

    ConsoleOwnerOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    int insert(ConsoleOwnerOrderFineDeatailEntity record);

    int insertSelective(ConsoleOwnerOrderFineDeatailEntity record);

    int updateByPrimaryKey(ConsoleOwnerOrderFineDeatailEntity record);

    int updateByPrimaryKeySelective(ConsoleOwnerOrderFineDeatailEntity record);

//    List<ConsoleOwnerOrderFineDeatailEntity> selectByOrderNo(@Param("orderNo")String orderNo);

    List<ConsoleOwnerOrderFineDeatailEntity> selectByOrderNoMemNo(@Param("orderNo") String orderNo, @Param("memNo") String memNo);


    /**
     * 获取租客罚金记录
     * <p>适用于目前取消罚金种类单一情况，后续可以考虑使用费用编码处理
     *
     * @param orderNo               租客子订单号
     * @param fineType              罚金类型：1-修改订单取车违约金，2-修改订单还车违约金，3-修改订单提前还车违约金，4-取消订单违约金
     * @param fineSubsidyCode       罚金补贴方编码（车主/租客/平台）1-租客，2-车主，3-平台
     * @param fineSubsidySourceCode 罚金来源编码（车主/租客/平台）1-租客，2-车主，3-平台
     * @return ConsoleOwnerOrderFineDeatailEntity
     */
    ConsoleOwnerOrderFineDeatailEntity selectByCondition(@Param("orderNo") String orderNo,
                                                         @Param("fineType") Integer fineType,
                                                         @Param("fineSubsidyCode") String fineSubsidyCode,
                                                         @Param("fineSubsidySourceCode") String fineSubsidySourceCode);

}

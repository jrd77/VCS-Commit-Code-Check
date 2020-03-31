package com.atzuche.order.rentercost.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;

import java.util.List;

/**
 * 租客订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:35:56
 */
@Mapper
public interface RenterOrderFineDeatailMapper{

    RenterOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    int insert(RenterOrderFineDeatailEntity record);
    
    int saveRenterOrderFineDeatail(RenterOrderFineDeatailEntity record);

    int updateByPrimaryKey(RenterOrderFineDeatailEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderFineDeatailEntity record);
    
    List<RenterOrderFineDeatailEntity> listRenterOrderFineDeatail(@Param("orderNo") String orderNo, @Param("renterOrderNo") String renterOrderNo);

    Integer saveRenterOrderFineDeatailBatch(@Param("costList") List<RenterOrderFineDeatailEntity> costList);
    
    Integer deleteGetReturnFineAfterAgree(@Param("id") Integer id, @Param("remark") String remark);

    List<RenterOrderFineDeatailEntity> getRenterOrderFineDeatailByOwnerOrderNo(@Param("renterOrderNo")String renterOrderNo);

    /**
     * 获取租客罚金记录
     * <p>适用于目前取消罚金种类单一情况，后续可以考虑使用费用编码处理
     *
     * @param renterOrderNo         租客子订单号
     * @param fineType              罚金类型：1-修改订单取车违约金，2-修改订单还车违约金，3-修改订单提前还车违约金，4-取消订单违约金
     * @param fineSubsidyCode       罚金补贴方编码（车主/租客/平台）1-租客，2-车主，3-平台
     * @param fineSubsidySourceCode 罚金来源编码（车主/租客/平台）1-租客，2-车主，3-平台
     * @return RenterOrderFineDeatailEntity
     */
    RenterOrderFineDeatailEntity selectByCondition(@Param("renterOrderNo") String renterOrderNo,
                                                   @Param("fineType") Integer fineType,
                                                   @Param("fineSubsidyCode") String fineSubsidyCode,
                                                   @Param("fineSubsidySourceCode") String fineSubsidySourceCode);
}

package com.atzuche.order.ownercost.mapper;

import com.atzuche.order.ownercost.entity.OwnerOrderFineApplyEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 *  表操作
 * 
 * @author ZhangBin
 * @date 2020-01-14 19:39:44
 */
@Mapper
public interface OwnerOrderFineApplyMapper{

    /**
     * 依据主键获取罚金信息
     *
     * @param id 主键
     * @return OwnerOrderFineApplyEntity
     */
    OwnerOrderFineApplyEntity selectByPrimaryKey(Integer id);


    /**
     * 依据订单号获取罚金信息
     *
     * @param orderNo 主订单号
     * @return OwnerOrderFineApplyEntity
     */
    OwnerOrderFineApplyEntity selectByOrderNo(String orderNo);

    /**
     * 新增
     *
     * @param record 罚金信息
     * @return int
     */
    int insert(OwnerOrderFineApplyEntity record);

    /**
     * 新增
     *
     * @param record 罚金信息
     * @return int
     */
    int insertSelective(OwnerOrderFineApplyEntity record);

    /**
     * 修改
     *
     * @param record 罚金信息
     * @return int
     */
    int updateByPrimaryKey(OwnerOrderFineApplyEntity record);

    /**
     * 修改
     *
     * @param record 罚金信息
     * @return int
     */
    int updateByPrimaryKeySelective(OwnerOrderFineApplyEntity record);


    /**
     * 修改
     *
     * @param id 主键
     * @return int
     */
    int updateInvalidByPrimaryKey(Integer id);

}

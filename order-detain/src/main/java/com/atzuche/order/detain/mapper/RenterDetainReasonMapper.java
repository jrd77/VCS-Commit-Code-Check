package com.atzuche.order.detain.mapper;

import com.atzuche.order.detain.entity.RenterDetainReasonEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 租车押金暂扣原因表
 *
 * @author ZhangBin
 * @date 2020-03-23 15:20:17
 */
@Mapper
public interface RenterDetainReasonMapper {

    /**
     * 根据主键查询数据
     *
     * @param id 主键
     * @return RenterDetainReasonEntity
     */
    RenterDetainReasonEntity selectByPrimaryKey(Integer id);

    /**
     * 根据主键查询数据
     *
     * @param orderNo 订单号
     * @param detainTypeCode 暂扣类型编码
     * @return RenterDetainReasonEntity
     */
    RenterDetainReasonEntity selectByOrderNo(@Param("orderNo") String orderNo,
                                       @Param("detainTypeCode") String detainTypeCode);

    /**
     * 新增数据
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int insert(RenterDetainReasonEntity record);

    /**
     * 新增数据(过滤为空的数据)
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int insertSelective(RenterDetainReasonEntity record);

    /**
     * 依据主键修改数据
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int updateByPrimaryKey(RenterDetainReasonEntity record);


    /**
     * 依据主键修改数据(过滤为空的数据)
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int updateByPrimaryKeySelective(RenterDetainReasonEntity record);

}

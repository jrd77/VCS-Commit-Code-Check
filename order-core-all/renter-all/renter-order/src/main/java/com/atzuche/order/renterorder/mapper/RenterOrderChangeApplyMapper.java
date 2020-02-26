package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客订单变更申请表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Mapper
public interface RenterOrderChangeApplyMapper{

    RenterOrderChangeApplyEntity selectByPrimaryKey(Integer id);

    List<RenterOrderChangeApplyEntity> selectALL();
    
    List<RenterOrderChangeApplyEntity> selectALLByOrderNo(@Param("orderNo") String orderNo);
    
    int insert(RenterOrderChangeApplyEntity record);
    
    int saveRenterOrderChangeApply(RenterOrderChangeApplyEntity record);

    int updateByPrimaryKey(RenterOrderChangeApplyEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderChangeApplyEntity record);

    Integer updateRenterOrderChangeApplyStatus(@Param("id") Integer id, @Param("auditStatus") Integer auditStatus);
    
    RenterOrderChangeApplyEntity getRenterOrderChangeApplyByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);
    /**
     * 根据租客子订单号查询。申请记录
     * @param renterOrderNo
     * @return
     */
    RenterOrderChangeApplyEntity getRenterOrderApplyByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);

    Integer getRenterOrderChangeApplyCountByOrderNo(@Param("orderNo") String orderNo);
    
    Integer updateRenterOrderChangeApplyStatusByOrderNo(@Param("orderNo") String orderNo);
}

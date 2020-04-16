package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客订单子表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Mapper
public interface RenterOrderMapper{

    RenterOrderEntity selectByPrimaryKey(Integer id);

    List<RenterOrderEntity> selectALL();

    int insert(RenterOrderEntity record);
    
    int insertSelective(RenterOrderEntity record);

    int updateByPrimaryKey(RenterOrderEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderEntity record);

    List<RenterOrderEntity> listAgreeRenterOrderByOrderNo(@Param("orderNo") String orderNo);

    RenterOrderEntity getRenterOrderByOrderNoAndIsEffective(@Param("orderNo") String orderNo);
    List<RenterOrderEntity> queryRenterOrderByOrderNo(@Param("orderNo")String orderNo);
    
    RenterOrderEntity getRenterOrderByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);
    
    Integer updateRenterOrderEffective(@Param("id") Integer id, @Param("effectiveFlag") Integer effectiveFlag);
    
    RenterOrderEntity getRenterOrderByOrderNoAndWaitPay(@Param("orderNo") String orderNo);
    //
    RenterOrderEntity getRenterOrderByOrderNoAndWaitPayIncrement(@Param("orderNo") String orderNo);
    
    //列表
    List<RenterOrderEntity> getRenterOrderByMemNoAndWaitPay(@Param("memNo") String memNo);
    //
    List<RenterOrderEntity> getRenterOrderByMemNoOrderNosAndWaitPay(@Param("memNo") String memNo,@Param("orderNoList")List<String> orderNoList);
    
    
    Integer updateRenterOrderChildStatus(@Param("id") Integer id, @Param("childStatus") Integer childStatus);

    /**
     * 订单结束更新所有租客订单状态
     *
     * @param orderNo 主订单号
     * @param childStatus 子订单状态
     * @return Integer
     */
    Integer updateChildStatusByOrderNo(@Param("orderNo") String orderNo, @Param("childStatus") Integer childStatus);

    Integer updateRenterOrderAgreeFlag(@Param("id") Integer id, @Param("agreeFlag") Integer agreeFlag);

    RenterOrderEntity getChangeRenterOrderByOrderNo(@Param("orderNo") String orderNo);

    List<RenterOrderEntity> queryHostiryRenterOrderByOrderNo(@Param("orderNo") String orderNo);

    int updateChildStatusByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo, @Param("childStatus") Integer childStatus);

    int updateSrvGetAndReturnFlagByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo,
                                                 @Param("isGetCar") Integer isGetCar, @Param("isReturnCar") Integer isReturnCar);

    RenterOrderEntity getRenterOrderNoByOrderNoAndFinish(@Param("orderNo")String orderNo);
    
    RenterOrderEntity getRenterOrderByOrderNoAndChildStatus(@Param("orderNo") String orderNo);
}
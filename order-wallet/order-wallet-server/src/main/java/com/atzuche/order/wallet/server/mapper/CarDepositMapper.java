package com.atzuche.order.wallet.server.mapper;

import com.atzuche.order.wallet.server.entity.CarDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/9 10:25 上午
 **/
@Mapper
public interface CarDepositMapper {

    CarDepositEntity getCarDepositById(Integer id);

    CarDepositEntity getCarDepositByCarNo(String carNo);

    int updateCarDepositBill(@Param("carNo") String carNo, @Param("updateBill") Integer incrementBill);
}

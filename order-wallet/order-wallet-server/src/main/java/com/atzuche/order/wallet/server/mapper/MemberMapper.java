package com.atzuche.order.wallet.server.mapper;

import com.atzuche.order.wallet.server.entity.BalanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/5 2:21 下午
 **/
@Mapper
public interface MemberMapper {

    BalanceEntity getByMemNo(String memNo);

    int deductBalance(@Param("memNo")String memNo,@Param("deduct")Integer balance);

    int deductDebt(@Param("memNo")String memNo,@Param("debt")Integer debt);
}

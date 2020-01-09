package com.atzuche.order.wallet.server.mapper;

import com.atzuche.order.wallet.server.entity.WalletEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 11:13 上午
 **/
@Mapper
public interface WalletMapper {

    public WalletEntity getWalletByMemNo(String memNo);

    public int updateWallet(@Param("memNo") String memNo,@Param("expPay") int expensePay,@Param("expGive") int expenseGive);

}

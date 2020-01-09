package com.atzuche.order.wallet.server.mapper;

import com.atzuche.order.wallet.server.entity.WalletLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 11:13 上午
 **/
@Mapper
public interface WalletLogMapper {
    public void insertWalletLog(WalletLogEntity entity);

    public List<WalletLogEntity> findByMemNoAndOrderNo(@Param("memNo") String memNo,@Param("orderNo") String orderNo);
}

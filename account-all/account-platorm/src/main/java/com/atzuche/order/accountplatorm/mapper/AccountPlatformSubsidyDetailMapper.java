package com.atzuche.order.accountplatorm.mapper;

import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 平台结算补贴明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:45:24
 */
@Mapper
public interface AccountPlatformSubsidyDetailMapper{

    AccountPlatformSubsidyDetailEntity selectByPrimaryKey(Integer id);

    List<AccountPlatformSubsidyDetailEntity> selectALL();

    int insert(AccountPlatformSubsidyDetailEntity record);
    
    int insertSelective(AccountPlatformSubsidyDetailEntity record);

    int updateByPrimaryKey(AccountPlatformSubsidyDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountPlatformSubsidyDetailEntity record);

}

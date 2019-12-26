package com.atzuche.order.accountplatorm.service.notservice;

import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountplatorm.exception.AccountPlatormException;
import com.atzuche.order.accountplatorm.mapper.AccountPlatformSubsidyDetailMapper;
import com.atzuche.order.accountplatorm.vo.req.AccountPlatformSubsidyDetailReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 平台结算补贴明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:45:24
 */
@Service
public class AccountPlatformSubsidyDetailNoTService {
    @Autowired
    private AccountPlatformSubsidyDetailMapper accountPlatformSubsidyDetailMapper;


    /**
     * 补贴信息落库
     * @param accountPlatformSubsidyDetails
     */
    public void insertAccountPlatformSubsidyDetail(List<AccountPlatformSubsidyDetailReqVO> accountPlatformSubsidyDetails) {
        if(!CollectionUtils.isEmpty(accountPlatformSubsidyDetails)){
            for(int i =0;i<accountPlatformSubsidyDetails.size();i++){
                AccountPlatformSubsidyDetailReqVO vo = accountPlatformSubsidyDetails.get(i);
                AccountPlatformSubsidyDetailEntity accountPlatformSubsidyDetail = new AccountPlatformSubsidyDetailEntity();
                BeanUtils.copyProperties(vo,accountPlatformSubsidyDetail);
                accountPlatformSubsidyDetail.setSubsidyName(vo.getSubsidyName().getDesc());
                accountPlatformSubsidyDetail.setSourceCode(vo.getRenterCashCodeEnum().getCashNo());
                accountPlatformSubsidyDetail.setSourceDesc(vo.getRenterCashCodeEnum().getTxt());
                int result = accountPlatformSubsidyDetailMapper.insert(accountPlatformSubsidyDetail);
                if(result==0){
                    throw new AccountPlatormException();
                }
            }
        }
    }
}

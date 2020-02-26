package com.atzuche.order.accountplatorm.service.notservice;

import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.exception.AccountPlatormException;
import com.atzuche.order.accountplatorm.vo.req.AccountPlatformProfitDetailReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountplatorm.mapper.AccountPlatformProfitDetailMapper;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 平台结算收益明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:45:24
 */
@Service
public class AccountPlatformProfitDetailNotService {
    @Autowired
    private AccountPlatformProfitDetailMapper accountPlatformProfitDetailMapper;

    /**
     * 结算收益信息落库
     * @param accountPlatformProfitDetails
     */
    public void insertAccountPlatformProfitDetail(List<AccountPlatformProfitDetailReqVO> accountPlatformProfitDetails) {
        if(!CollectionUtils.isEmpty(accountPlatformProfitDetails)){
            for(int i =0;i<accountPlatformProfitDetails.size();i++){
                AccountPlatformProfitDetailReqVO vo = accountPlatformProfitDetails.get(i);
                AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
                BeanUtils.copyProperties(vo,accountPlatformProfitDetail);
                int result = accountPlatformProfitDetailMapper.insertSelective(accountPlatformProfitDetail);
                if(result==0){
                    throw new AccountPlatormException();
                }
            }
        }
    }

    /**
     * 平台收益明细 落库
     * @param accountPlatformProfitDetails
     */
    public void insertAccountPlatformProfitDetails(List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails) {
        if(!CollectionUtils.isEmpty(accountPlatformProfitDetails)){
            for(int i =0;i<accountPlatformProfitDetails.size();i++){
                AccountPlatformProfitDetailEntity entity = accountPlatformProfitDetails.get(i);
                int result = accountPlatformProfitDetailMapper.insertSelective(entity);
                if(result==0){
                    throw new AccountPlatormException();
                }
            }
        }
    }

    /**
     * 查询 平台结算收益明
     * @param orderNo
     * @return
     */
    public List<AccountPlatformProfitDetailEntity> getPlatformProfitDetails(String orderNo) {
        return accountPlatformProfitDetailMapper.getPlatformProfitDetails(orderNo);
    }
}

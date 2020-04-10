package com.atzuche.order.wallet.server.service;

import com.atzuche.order.wallet.api.AccountVO;
import com.atzuche.order.wallet.api.MemAccountStatVO;
import com.atzuche.order.wallet.server.entity.AccountEntity;
import com.atzuche.order.wallet.server.mapper.AccountMapper;
import com.atzuche.order.wallet.server.util.AESEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/4 3:25 下午
 **/
@Service
public class AccountService {
    private final static Logger logger = LoggerFactory.getLogger(AccountService.class);
    

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 根据id获取对应的account信息
     * @param id
     * @return
     */
    public AccountVO getById(Integer id){
        AccountEntity entity = accountMapper.getById(id);
        AccountVO accountVO = convert(entity);
        return accountVO;
    }


    public List<MemAccountStatVO> findByMemNo(List<String> memNoList){
        return accountMapper.statMemAccount(memNoList);
    }





    public List<AccountVO> findByMemNo(String memNo){
        List<AccountEntity> entities = accountMapper.findByMemNo(memNo);
        List<AccountVO> result = new ArrayList<>();
        for(AccountEntity entity:entities){
            AccountVO accountVO = convert(entity);
            result.add(accountVO);
        }

        return result;
    }

    public static AccountVO convert(AccountEntity entity){
        logger.info("entity is [{}]",entity);
        AccountVO accountVO = new AccountVO();
        BeanUtils.copyProperties(entity,accountVO);
        try {
            if(entity.getCardNo()!=null) {
                accountVO.setCardNoPlain(AESEncrypter.decrypt(entity.getCardNo()));
            }
            if(entity.getCardHolder()!=null) {
                accountVO.setCardHolderPlain(AESEncrypter.decrypt(entity.getCardHolder()));
            }
            if(entity.getCertNo()!=null) {
                accountVO.setCertNoPlain(AESEncrypter.decrypt(entity.getCertNo()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return accountVO;
    }
}

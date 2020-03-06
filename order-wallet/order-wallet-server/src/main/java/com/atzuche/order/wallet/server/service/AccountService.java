package com.atzuche.order.wallet.server.service;

import com.atzuche.order.wallet.api.AccountVO;
import com.atzuche.order.wallet.server.entity.AccountEntity;
import com.atzuche.order.wallet.server.mapper.AccountMapper;
import com.atzuche.order.wallet.server.util.AESEncrypter;
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
        AccountVO accountVO = new AccountVO();
        BeanUtils.copyProperties(entity,accountVO);
        try {
            accountVO.setCardNoPlain(AESEncrypter.decrypt(entity.getCardNo()));
            accountVO.setCardHolderPlain(AESEncrypter.decrypt(entity.getCardHolder()));
            accountVO.setCertNoPlain(AESEncrypter.decrypt(entity.getCertNo()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return accountVO;
    }
}

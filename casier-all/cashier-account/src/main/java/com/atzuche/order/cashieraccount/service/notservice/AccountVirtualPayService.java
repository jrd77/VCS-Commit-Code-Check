package com.atzuche.order.cashieraccount.service.notservice;

import com.atzuche.order.cashieraccount.entity.AccountVirtualPayDetailEntity;
import com.atzuche.order.cashieraccount.entity.AccountVirtualPayEntity;
import com.atzuche.order.cashieraccount.mapper.AccountVirtualPayDetailMapper;
import com.atzuche.order.cashieraccount.mapper.AccountVirtualPayMapper;
import com.atzuche.order.cashieraccount.vo.req.pay.VirtualPayDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 11:40 上午
 **/
@Service
public class AccountVirtualPayService {
    
    private final static Logger logger = LoggerFactory.getLogger(AccountVirtualPayService.class);
    
    @Autowired
    private AccountVirtualPayDetailMapper accountVirtualPayDetailMapper;

    @Autowired
    private AccountVirtualPayMapper accountVirtualPayMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    public void addVirtualPayRecord(VirtualPayDTO virtualPayVO){
        insertDetail(virtualPayVO);

        AccountVirtualPayEntity entity = accountVirtualPayMapper.selectByAccountNo(virtualPayVO.getAccountEnum().getAccountNo());
        if(entity==null){
            try {
                initVirtualAccount(virtualPayVO.getAccountEnum().getAccountNo(), virtualPayVO.getAccountEnum().getAccountName());
            }catch (Exception e){
                logger.warn("初始化失败，可能主键冲突造成",e);
            }
        }

        deductAmt(virtualPayVO);

    }

    private void insertDetail(VirtualPayDTO virtualPayVO){
        AccountVirtualPayDetailEntity detailEntity = new AccountVirtualPayDetailEntity();
        detailEntity.setAccountNo(virtualPayVO.getAccountEnum().getAccountNo());
        detailEntity.setAccountName(virtualPayVO.getAccountEnum().getAccountName());
        detailEntity.setAmt(virtualPayVO.getPayAmt());
        detailEntity.setOrderNo(virtualPayVO.getOrderNo());
        detailEntity.setPayType(virtualPayVO.getPayType().getValue());
        detailEntity.setPayCashType(virtualPayVO.getCashType().getValue());
        accountVirtualPayDetailMapper.insertSelective(detailEntity);
    }

    private void deductAmt(VirtualPayDTO virtualPayVO){
        int count = accountVirtualPayMapper.deductAmt(virtualPayVO.getAccountEnum().getAccountNo(),virtualPayVO.getPayAmt());
        if(count == 0){
            throw new RuntimeException("插入记录失败:"+virtualPayVO);
        }
    }



    /**
     * 初始化记录
     * @param accountNo
     * @param accountName
     */
    private void initVirtualAccount(String accountNo,String accountName){
        AccountVirtualPayEntity entity = new AccountVirtualPayEntity();
        entity.setAccountNo(accountNo);
        entity.setAccountName(accountName);
        entity.setTotalAmt(0);
        entity.setVersion(1);
        accountVirtualPayMapper.insertSelective(entity);
    }


    public List<AccountVirtualPayDetailEntity> queryVirtualPayByOrderNo(String orderNo){
        return accountVirtualPayDetailMapper.queryByOrderNo(orderNo);
    }
}

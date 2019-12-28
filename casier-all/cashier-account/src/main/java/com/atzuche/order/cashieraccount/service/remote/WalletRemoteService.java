package com.atzuche.order.cashieraccount.service.remote;

import com.atzuche.order.cashieraccount.exception.DeductWalletRemoteException;
import com.atzuche.order.cashieraccount.exception.GetWalletRemoteException;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.api.WalletFeignService;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.vo.req.WalletDeductionReqVO;
import com.autoyol.vo.res.WalletChangeResVO;
import com.autoyol.vo.res.WalletResVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class WalletRemoteService {
    @Autowired private WalletFeignService walletFeignService;

    /**
     * 查询钱包信息
     * @param memNo
     * @return
     */
    public WalletResVO getWalletByMemNo(String memNo){
        log.info("WalletRemoteService  getWalletAmtByMemNo start 开始获取钱包信息,memNo：[{}]",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "钱包信息详情服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"WalletRemoteService.getWalletByMemNo");
            String parameter = "memNo="+memNo;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            ResponseData<WalletResVO> responseData =  walletFeignService.getWalletByMemNo(Integer.valueOf(memNo));
            log.info("WalletRemoteService  getWalletAmtByMemNo end 获取会员钱包信息结束,responseData：[{}]", GsonUtils.toJson(responseData));
            Cat.logEvent(CatConstants.FEIGN_RESULT, GsonUtils.toJson(responseData));
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
               throw new GetWalletRemoteException();
            }
            WalletResVO vo = responseData.getData();
            if(Objects.isNull(vo) || Objects.isNull(vo.getMemNo())){
                throw new GetWalletRemoteException();
            }
            t.setStatus(Transaction.SUCCESS);
            return vo;
        }catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 获取租客钱包余额信息失败,e：[{}]",e);
            log.error("Feign 获取租客钱包余额,memNo：[{}],e：[{}]",memNo,e);
            throw new GetWalletRemoteException();
        }finally {
            t.complete();
        }
    }
    /**
     * 查询钱包余额
     * @param memNo
     * @return
     */
    public int getWalletPayBalanceByMemNo (String memNo){
        WalletResVO vo =  getWalletByMemNo(memNo);
        if(Objects.isNull(vo) || Objects.isNull(vo.getPayBalance())){
            return NumberUtils.INTEGER_ZERO;
        }
        return vo.getPayBalance();
    }

    /**
     * 扣减钱包余额
     * @param walletDeduction
     */
    public int updateWalletByDeduct(WalletDeductionReqVO walletDeduction) {
        log.info("WalletRemoteService  updateWalletByDeduct start 开始扣减会员钱包信息,walletDeduction：[{}]",GsonUtils.toJson(walletDeduction));
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "钱包扣减服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"WalletRemoteService.getWalletByMemNo");
            String parameter = "memNo="+GsonUtils.toJson(walletDeduction);
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            ResponseData<WalletChangeResVO> responseData =  walletFeignService.updateWalletByDeduct(walletDeduction);
            log.info("WalletRemoteService  updateWalletByDeduct end 扣减会员钱包信息结束,responseData：[{}]", GsonUtils.toJson(responseData));
            Cat.logEvent(CatConstants.FEIGN_RESULT, GsonUtils.toJson(responseData));
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                throw new DeductWalletRemoteException();
            }
            WalletChangeResVO vo = responseData.getData();
            if(Objects.isNull(vo) || Objects.isNull(vo.getAmt())){
                throw new DeductWalletRemoteException();
            }
            t.setStatus(Transaction.SUCCESS);
            return vo.getAmt();
        }catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 扣减租客钱包余额信息失败,e：[{}]",e);
            log.error("Feign 扣减租客钱包余额,walletDeduction：[{}],e：[{}]",GsonUtils.toJson(walletDeduction),e);
            throw new DeductWalletRemoteException();
        }finally {
            t.complete();
        }
    }


}

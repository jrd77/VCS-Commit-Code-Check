package com.atzuche.order.wallet.server.controller;

import com.atzuche.order.commons.StringUtil;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.wallet.server.service.WalletService;
import com.atzuche.order.wallet.api.DeductWalletVO;
import com.atzuche.order.wallet.api.TotalWalletVO;
import com.autoyol.commons.web.ResponseData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 12:01 下午
 **/
@RestController
public class OrderWalletController {

    @Autowired
    private WalletService walletService;

    @RequestMapping(value = "wallet/get",method = RequestMethod.GET)
    public ResponseData<TotalWalletVO> getWalletTotalByMemNo(@RequestParam("memNo") String memNo)throws Exception{
        int total = walletService.getTotalWallet(memNo);
        TotalWalletVO vo = new TotalWalletVO();
        vo.setMemNo(memNo);
        vo.setTotal(String.valueOf(total));
        return ResponseData.success(vo);
    }

    @RequestMapping(value = "wallet/deduct",method = RequestMethod.POST)
    public ResponseData<DeductWalletVO> deductWallet(@RequestBody DeductWalletVO deductWalletVO){
         Integer reqAmt = deductWalletVO.getAmt();
         if(reqAmt<=0){
             throw new InputErrorException("amt 不能小于0");
         }
         String expDesc= deductWalletVO.getExpDesc();
         if(StringUtils.isBlank(expDesc)){
             expDesc="租车消费";
         }
         int realAmt = walletService.deductWallet(deductWalletVO.getMemNo(),deductWalletVO.getOrderNo(),deductWalletVO.getAmt(),expDesc);
         deductWalletVO.setAmt(realAmt);
         return ResponseData.success(deductWalletVO);

    }

    @RequestMapping(value = "wallet/ret",method = RequestMethod.POST)
    public ResponseData returnOrChangeWallet(@RequestBody DeductWalletVO deductWalletVO){
        Integer reqAmt = deductWalletVO.getAmt();
        if(reqAmt<=0){
            throw new InputErrorException("amt 不能小于0");
        }
        String expDesc= deductWalletVO.getExpDesc();
        if(StringUtils.isBlank(expDesc)){
            expDesc="订单返还";
        }
        walletService.returnOrCharge(deductWalletVO.getMemNo(),deductWalletVO.getOrderNo(),deductWalletVO.getAmt(),expDesc);
        return ResponseData.success();

    }


}

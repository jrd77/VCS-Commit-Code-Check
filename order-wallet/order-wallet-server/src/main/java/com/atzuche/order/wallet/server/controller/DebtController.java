package com.atzuche.order.wallet.server.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.commons.vo.DebtDetailVO;
import com.atzuche.order.wallet.api.DeductBalanceVO;
import com.atzuche.order.wallet.api.DeductDebtVO;
import com.atzuche.order.wallet.api.MemBalanceVO;
import com.atzuche.order.wallet.api.MemDebtVO;
import com.atzuche.order.wallet.server.service.DebtService;
import com.autoyol.commons.web.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/16 4:53 下午
 **/
@RestController
public class DebtController {

    @Autowired
    private DebtService debtService;
    private final static Logger logger = LoggerFactory.getLogger(DebtController.class);
    

    /**
     * 返回用户的欠款，为正值
     * @param memNo
     * @return
     */
    @RequestMapping(value = "debt/get",method = RequestMethod.GET)
    public ResponseData<MemDebtVO> getMemBalance(@RequestParam("memNo")String memNo){
        int total = debtService.getMemDebtTotal(memNo);
        MemDebtVO debtVO = new MemDebtVO();
        debtVO.setMemNo(memNo);
        debtVO.setDebt(total);
        return ResponseData.success(debtVO);
    }

    @RequestMapping(value = "debt/deduct",method = RequestMethod.POST)
    public ResponseData deductBalance(@Valid @RequestBody DeductDebtVO deductDebtVO, BindingResult result){
        logger.info("deduct debt param is [{}]",deductDebtVO);
        BindingResultUtil.checkBindingResult(result);
        if(deductDebtVO.getDeduct()<=0){
            throw new InputErrorException("扣减金额不能为负数:"+deductDebtVO.getDeduct());
        }
        debtService.deductDebt(deductDebtVO.getMemNo(),deductDebtVO.getDeduct());
        return ResponseData.success();
    }
    
    /**
     * 返回用户名下的欠款(区分历史欠款和订单欠款)
     * @param memNo
     * @return ResponseData<DebtDetailVO>
     */
    @RequestMapping(value = "debt/detail",method = RequestMethod.GET)
    public ResponseData<DebtDetailVO> getDebtDetailVO(@RequestParam("memNo")String memNo){
    	DebtDetailVO debtDetailVO = debtService.getDebtDetailVO(memNo);
        return ResponseData.success(debtDetailVO);
    }
}

package com.atzuche.order.wallet.server.controller;

import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.wallet.api.CarDepositVO;
import com.atzuche.order.wallet.api.UpdateCarDepositBillVO;
import com.atzuche.order.wallet.server.service.CarDepositService;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/9 2:18 下午
 **/
@RestController
public class CarDepositController {

    @Autowired
    private CarDepositService depositService;


    @RequestMapping(value = "carDeposit/getByCarNo",method = RequestMethod.GET)
    public ResponseData<CarDepositVO> getCarDepositByCarNo(@RequestParam("carNo") String carNo){
        CarDepositVO depositVO = depositService.getCarDepositByCarNo(carNo);
        return ResponseData.success(depositVO);
    }


    @RequestMapping(value = "carDeposit/getById",method = RequestMethod.GET)
    public ResponseData<CarDepositVO> getCarDepositById(@RequestParam("id") Integer id){
        CarDepositVO depositVO = depositService.getCarDepositById(id);
        return ResponseData.success(depositVO);
    }

    @RequestMapping(value = "carDeposit/update",method = RequestMethod.POST)
    public ResponseData updateCarDeposit(@Valid @RequestBody UpdateCarDepositBillVO updateCarDepositBillVO){
        if(updateCarDepositBillVO.getUpdateBill()<=0){
            throw new InputErrorException("updateBill<0:"+updateCarDepositBillVO.getUpdateBill());
        }
        depositService.updateCarDeposit(updateCarDepositBillVO.getCarNo(),updateCarDepositBillVO.getUpdateBill());
        return ResponseData.success();
    }


}

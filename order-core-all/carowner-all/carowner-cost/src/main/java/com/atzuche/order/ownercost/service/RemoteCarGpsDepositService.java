package com.atzuche.order.ownercost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.wallet.api.CarDepositFeignService;
import com.atzuche.order.wallet.api.CarDepositVO;
import com.atzuche.order.wallet.api.UpdateCarDepositBillVO;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RemoteCarGpsDepositService {

	@Autowired
	private CarDepositFeignService carDepositFeignService;
	
	/**
     * 获取车辆gps押金信息
     * @param carNo
     * @return CarDepositVO
     */
    public CarDepositVO getCarDepositByCarNo(String carNo)  {
        ResponseData<CarDepositVO> responseData = null;
        log.info("Feign 开始获取车辆gps押金信息,carNo={}",carNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "gps押金服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteCarGpsDepositService.getCarDepositByCarNo");
            String parameter = "carNo="+carNo;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = carDepositFeignService.getCarDepositByCarNo(carNo);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取车辆gps押金信息失败,ResponseData={},carNo={}",responseData,carNo,e);
            Cat.logError("Feign 获取车辆gps押金信息失败",e);
            t.setStatus(e);
        }finally {
            t.complete();
        }
        return responseData.getData();
    }
    
    
    /**
     * 更新车辆gps押金
     * @param orderNo
     * @param carNo
     * @param updateBill
     */
    public void updateCarDeposit(String orderNo, String carNo, Integer updateBill)  {
    	UpdateCarDepositBillVO updateCarDepositBillVO = new UpdateCarDepositBillVO();
    	updateCarDepositBillVO.setCarNo(carNo);
    	updateCarDepositBillVO.setOrderNo(orderNo);
    	updateCarDepositBillVO.setUpdateBill(updateBill);
        ResponseData<CarDepositVO> responseData = null;
        log.info("Feign 开始更新车辆gps押金,orderNo={},carNo={},updateBill={}",orderNo, carNo, updateBill);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "gps押金服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteCarGpsDepositService.updateCarDeposit");
            String parameter = "orderNo="+orderNo+"&carNo="+carNo+"&updateBill="+updateBill;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = carDepositFeignService.updateCarDeposit(updateCarDepositBillVO);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 更新车辆gps押金失败,ResponseData={},carNo={}",responseData,carNo,e);
            Cat.logError("Feign 更新车辆gps押金失败",e);
            t.setStatus(e);
        }finally {
            t.complete();
        }
    }
}

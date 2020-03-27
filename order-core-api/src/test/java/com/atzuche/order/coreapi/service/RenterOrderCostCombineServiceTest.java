package com.atzuche.order.coreapi.service;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class RenterOrderCostCombineServiceTest {
	@Autowired
	RenterOrderCostCombineService renterOrderCostCombineService;
	@Autowired
	private OrderSupplementDetailService orderSupplementDetailService;
	@Autowired
	private CashierNoTService cashierNoTService;
	
	String memNo = "";
	
	private void handleRentIncrementDebtAmt(String memNo, List<OrderSupplementDetailEntity> lsEntity, List<PayableVO> payableVOs) {
		 //应付租车费用（已经求和）
       int rentIncrementDebtAmt = cashierNoTService.sumRentOrderCost(payableVOs);
		if(!CollectionUtils.isEmpty(payableVOs) && rentIncrementDebtAmt < 0){
		    for(int i=0;i<payableVOs.size();i++){
		        PayableVO payableVO = payableVOs.get(i);
		        //判断是租车费用、还是补付 租车费用 并记录 详情
		        RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN;	                    
		        //数据封装
		        OrderSupplementDetailEntity entity = orderSupplementDetailService.handleDebtData(payableVO.getAmt(), type, memNo, payableVO.getOrderNo());
		        if(entity != null) {
		    		lsEntity.add(entity);
		    	}
		    }
		}
	}
	
	@Test
	public void testListPayableDebtPayVOByMemNo() {
		List<OrderSupplementDetailEntity> lsEntity = new ArrayList<OrderSupplementDetailEntity>();
		//支付欠款,跟子订单号无关。根据会员号查询。区别:数据源
		List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableDebtPayVOByMemNo(memNo);
        //已付租车费用(shifu  租车费用的实付)
        handleRentIncrementDebtAmt(memNo, lsEntity, payableVOs);
        for (OrderSupplementDetailEntity entity : lsEntity) {
			log.info("entity="+entity.toString());
		}
    	log.info("===========================333补付记录OK");
	}
	
	
	@Test
	public void testListRentAmtEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetServiceChargeFeeEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInsurAmtEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testListAbatementAmtEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetExtraDriverInsureAmtEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMileageAmtEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOilAmtEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCarDepositAmtVO() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCarSpecialCoefficientNew() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNewCarCoefficient() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIllegalDepositAmt() {
		fail("Not yet implemented");
	}

	@Test
	public void testListPayableVO() {
		fail("Not yet implemented");
	}

	@Test
	public void testListPayableGlobalVO() {
		fail("Not yet implemented");
	}

	@Test
	public void testListPayableIncrementVO() {
		fail("Not yet implemented");
	}

	@Test
	public void testListPayableSupplementVO() {
		fail("Not yet implemented");
	}

	@Test
	public void testListPayableDebtPayVO() {
		fail("Not yet implemented");
	}



	@Test
	public void testListPayableDebtPayVOByMemNoAndOrderNos() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPayable() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPayableIncrement() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRenterNormalCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRenterGlobalCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testCostBaseConvert() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetReturnCarCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGetReturnOverCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckResponse() {
		fail("Not yet implemented");
	}

}

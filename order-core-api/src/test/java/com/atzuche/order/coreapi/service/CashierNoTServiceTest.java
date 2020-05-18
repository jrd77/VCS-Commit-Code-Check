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

import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.OrderSupplementDetailVO;
import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.autoyol.autopay.gateway.vo.req.PayVo;
import com.autoyol.commons.utils.GsonUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class CashierNoTServiceTest {
	@Autowired
	private OrderSupplementDetailService orderSupplementDetailService;
	@Autowired
	private CashierNoTService cashierNoTService;
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;
	@Autowired
	private AccountRenterCostSettleService accountRenterCostSettleService;
	@Autowired
	private SupplementService supplementService;
	
	String memNo = "985856422";
	
	private void handleListRenterOrderEntity(String memNo, List<OrderSupplementDetailEntity> lsEntity,List<RenterOrderEntity> listRenterOrderEntity) {
		for (RenterOrderEntity renterOrderEntity : listRenterOrderEntity) {
			String orderNo = renterOrderEntity.getOrderNo();
			//管理后台修改补付
		    if(renterOrderEntity != null) {
//		        List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableGlobalVO(orderNo,renterOrderEntity.getRenterOrderNo(),memNo);
		        List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableIncrementVO(orderNo,renterOrderEntity.getRenterOrderNo(),memNo);
		        
		        //应付租车费用（已经求和）
		        int rentAmtAfter = cashierNoTService.sumRentOrderCost(payableVOs);
		        log.info("rentAmtAfter=应付="+rentAmtAfter);
		        
		        //已付租车费用(shifu  租车费用的实付)
		        int rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderNo,memNo);
		        log.info("rentAmtPayed=实付="+rentAmtPayed);
		        
		        if(!CollectionUtils.isEmpty(payableVOs) && rentAmtAfter+rentAmtPayed < 0){   // 
		            for(int i=0;i<payableVOs.size();i++){
		                PayableVO payableVO = payableVOs.get(i);
		                //判断是租车费用、还是补付 租车费用 并记录 详情
		                RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER;	                    
		                //数据封装
		                OrderSupplementDetailEntity entity = orderSupplementDetailService.handleConsoleData(-payableVO.getAmt(), type, memNo, orderNo);
		                if(entity != null) {
		            		lsEntity.add(entity);
		            	}
		            }
		        }else {
		        	log.info("无需支付>>>>>>>>>>");
		        }
		    }
		}
	}
	
	@Test
	public void testGetRenterOrderNoByMemNo() {
		//1 查询子单号 ,根据会员号查询。区别:数据源
		List<OrderSupplementDetailEntity> lsEntity = new ArrayList<OrderSupplementDetailEntity>();
        List<RenterOrderEntity> listRenterOrderEntity = cashierNoTService.getRenterOrderNoByMemNo(memNo);
        //管理后台修改订单
        handleListRenterOrderEntity(memNo, lsEntity, listRenterOrderEntity);
        for (OrderSupplementDetailEntity entity : lsEntity) {
			log.info("entity="+entity.toString());
		}
    	log.info("===========================222补付记录OK");
	}
	
	
	@Test
	public void testGetRenterOrderNoByMemNoAndOrderNos() {
		List<String> orderNoList = new ArrayList();
		orderNoList.add("64231262300299");
		orderNoList.add("43267162300299");
		
		//1 查询子单号 ,根据会员号查询。区别:数据源
		List<OrderSupplementDetailEntity> lsEntity = new ArrayList<OrderSupplementDetailEntity>();
        List<RenterOrderEntity> listRenterOrderEntity = cashierNoTService.getRenterOrderNoByMemNoAndOrderNos(memNo, orderNoList);
        //管理后台修改订单
        handleListRenterOrderEntity(memNo, lsEntity, listRenterOrderEntity);
        for (OrderSupplementDetailEntity entity : lsEntity) {
			log.info("entity="+entity.toString());
		}
    	log.info("===========================222补付记录OK");
	}
	
	
	@Test
	public void testMinigramChangeReqOs() {
		String orderNo = "70010280500299";
		String reqOs = "miniprogram-alipay";
		PayVo vo = new PayVo();
		vo.setReqOs(reqOs);
		cashierNoTService.minigramChangeReqOs(orderNo, vo, reqOs);
		log.info("vo="+GsonUtils.toJson(vo));
		
	}
	
	@Test
	public void testGetRenterOrderNoByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRenterOrderNoByOrderNoAndFinish() {
		fail("Not yet implemented");
	}



	

	@Test
	public void testGetCashierEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertRenterWZDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertRenterDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertRenterCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPayedOrderRenterDepositReq() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdataCashier() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPayedOrderRenterWZDepositReq() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAccountRenterCostReq() {
		fail("Not yet implemented");
	}

	@Test
	public void testSumRentOrderCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPayVO() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdataCashierAndRenterDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdataCashierAndRenterWzDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdataCashierAndRenterCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCashierRentCostPaySn() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPaySignByPayVos() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertRenterCostByWallet() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRefundVo() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCashierEntitys() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendOrderRefundSuccessMq() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendOrderRefundFailMq() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testListOrderSupplementDetailVOByOrderNo() {
		List<OrderSupplementDetailVO> list = supplementService.listOrderSupplementDetailVOByOrderNo("43267162300299");
		log.info("supplementlist=",list);
	}

}

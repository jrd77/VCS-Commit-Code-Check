/**
 * 
 */
package com.atzuche.order.settle.service.notservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.OilAmtDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.account.CostTypeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.vo.delivery.DeliveryOilCostVO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.handover.HandoverCarRepVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarReqVO;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderCostCombineService;
import com.atzuche.order.ownercost.service.OwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderIncrementDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderPurchaseDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.OrderConsoleCostDetailService;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.service.OrderOwnerSettleNewService;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.platformcost.CommonUtils;
import com.autoyol.platformcost.model.FeeResult;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jhuang
 *
 */
@Service
@Slf4j
public class OrderOwnerSettleNoTService {
	@Autowired
	private OrderSettleProxyService orderSettleProxyService;
	@Autowired
	private OrderOwnerSettleNewService orderOwnerSettleNewService;
	
	@Autowired 
	private OwnerOrderService ownerOrderService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OwnerOrderCostCombineService ownerOrderCostCombineService;
	@Autowired
	private RenterGoodsService renterGoodsService;
	@Autowired
	private HandoverCarService handoverCarService;
	@Autowired
	private DeliveryCarInfoPriceService deliveryCarInfoPriceService;
	@Autowired
	private OrderConsoleCostDetailService orderConsoleCostDetailService;
	@Autowired
	private OwnerOrderFineDeatailService ownerOrderFineDeatailService;
	@Autowired
	private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
	@Autowired
	private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
	@Autowired
	private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
	@Autowired
	private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
	@Autowired
	private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
	@Autowired
	private OwnerGoodsService ownerGoodsService; 
	@Autowired
	private CashierSettleService cashierSettleService;
	
	
	public SettleOrders initSettleOrdersSeparateOwner(String orderNo,SettleOrders settleOrders) {
        //1 校验参数
        if(StringUtil.isBlank(orderNo)){
            throw new OrderSettleFlatAccountException();
        }
        OwnerOrderEntity ownerOrder = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(ownerOrder) || Objects.isNull(ownerOrder.getOwnerOrderNo())){
            throw new OrderSettleFlatAccountException();
        }

        //3.2获取车主子订单 和 车主会员号
        String ownerOrderNo = ownerOrder.getOwnerOrderNo();
        String ownerMemNo = ownerOrder.getMemNo();

        settleOrders.setOrderNo(orderNo);
        settleOrders.setOwnerOrderNo(ownerOrderNo);
        settleOrders.setOwnerMemNo(ownerMemNo);
        settleOrders.setOwnerOrder(ownerOrder);
        // 2 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
//        this.check(renterOrder,settleOrders);
        return settleOrders;
    }
	
	
	public void settleOrderFirstSeparateOwner(SettleOrders settleOrders, SettleOrdersDefinition settleOrdersDefinition){
        //3 查询所有车主费用明细 TODO 暂不支持 多个车主
        this.getOwnerCostSettleDetail(settleOrders);
        Cat.logEvent("getOwnerCostSettleDetail",GsonUtils.toJson(settleOrders));
        log.info("OrderSettleService getOwnerCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));

        //4 计算费用统计  资金统计
        this.settleOrdersDefinitionSeparateOwner(settleOrders,settleOrdersDefinition);
        log.info("OrderSettleService settleOrdersDefinition settleOrdersDefinition [{}]", GsonUtils.toJson(settleOrdersDefinition));
        Cat.logEvent("SettleOrdersDefinition",GsonUtils.toJson(settleOrdersDefinition));

        //5 费用明细先落库
        this.insertSettleOrdersSeparateOwner(settleOrdersDefinition);


//        return settleOrdersDefinition;
    }
	
	@Transactional(rollbackFor=Exception.class)
    public void settleOrderAfterSeparateOwner(SettleOrders settleOrders, SettleOrdersDefinition settleOrdersDefinition,OrderPayCallBack callBack) {
        //7.2 车主 费用 落库表
        cashierSettleService.insertAccountOwnerCostSettle(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo(),settleOrders.getOwnerMemNo(),settleOrdersDefinition.getAccountOwnerCostSettleDetails());

        SettleOrdersAccount settleOrdersAccount = new SettleOrdersAccount();
        BeanUtils.copyProperties(settleOrders,settleOrdersAccount);

        settleOrdersAccount.setOwnerCostAmtFinal(settleOrdersDefinition.getOwnerCostAmtFinal());
        settleOrdersAccount.setOwnerCostSurplusAmt(settleOrdersDefinition.getOwnerCostAmtFinal());

        log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount one [{}]", GsonUtils.toJson(settleOrdersAccount));
        Cat.logEvent("settleOrdersAccount",GsonUtils.toJson(settleOrdersAccount));
        

        // 13.1车主收益 结余处理 历史欠款
        orderOwnerSettleNewService.repayHistoryDebtOwner(settleOrdersAccount);
        // 13.2车主收益 结余处理 历史欠款
        int oldTotalRealDebtAmt = orderOwnerSettleNewService.oldRepayHistoryDebtOwner(settleOrdersAccount);
        settleOrders.setOwnerTotalOldRealDebtAmt(oldTotalRealDebtAmt);
        //14 车主待审核收益落库
        orderOwnerSettleNewService.insertOwnerIncomeExamine(settleOrdersAccount);
    }
	
	//---------------------------------------------------------------------------------------------------------------------
	
	/**
     * 查询车主费用明细
     * @param settleOrders
     */
    public void getOwnerCostSettleDetail(SettleOrders settleOrders) {
        OwnerCosts ownerCosts = new OwnerCosts();
        // 车主收益
        int ownerIncomeAmt = 0;
        //1  初始化
        //1.1 油费、超里程费用 配送模块需要的参数
//        HandoverCarReqVO handoverCarReq = new HandoverCarReqVO();
//        handoverCarReq.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
//        HandoverCarRepVO handoverCarRep = handoverCarService.getRenterHandover(handoverCarReq);
        // 1.2 油费、超里程费用 订单商品需要的参数
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(settleOrders.getOwnerOrderNo(),Boolean.TRUE);
        
        //4 获取车主费用列表
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        int rentAmt = 0;
        if(!CollectionUtils.isEmpty(ownerOrderPurchaseDetail)){
        	ownerIncomeAmt += ownerOrderPurchaseDetail.stream().mapToInt(OwnerOrderPurchaseDetailEntity::getTotalAmount).sum();
            rentAmt = ownerOrderPurchaseDetail.stream().filter(obj ->{
                return OwnerCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getCostCode());
            }).mapToInt(OwnerOrderPurchaseDetailEntity::getTotalAmount).sum();
        }
        
        //1 车主端代管车服务费
        CostBaseDTO costBaseDTO= getCostBaseOwner(settleOrders.getOwnerOrder());
        //计算服务费按照租金来计算。需要提前来计算。
        // 应该从车主那边获取，而不是从租客端获取。  200306
//        int rentAmt=settleOrders.getRenterOrderCost();
        
        log.info("计算车主服务费，基于租客租金费用rentAmt=[{}]",rentAmt);
        
        //代管车服务费比例 商品
        Double proxyProportionDou= ownerGoodsDetail.getServiceProxyRate();
        if(proxyProportionDou==null){
            proxyProportionDou = Double.valueOf(0.0);
        }
        
        int proxyProportion = proxyProportionDou.intValue();
        OwnerOrderPurchaseDetailEntity proxyExpense = ownerOrderCostCombineService.getProxyExpense(costBaseDTO,rentAmt,proxyProportion);
        if (proxyExpense != null && proxyExpense.getTotalAmount() != null) {
        	ownerIncomeAmt += -proxyExpense.getTotalAmount();
        }
        
        //2 车主端平台服务费
        //服务费比例 商品
        Double serviceRate = ownerGoodsDetail.getServiceRate();
        if(serviceRate==null){
            serviceRate = Double.valueOf(0.0);
        }
        int serviceProportion = serviceRate.intValue();
        OwnerOrderPurchaseDetailEntity serviceExpense = ownerOrderCostCombineService.getServiceExpense(costBaseDTO,rentAmt,serviceProportion);
        if (serviceExpense != null && serviceExpense.getTotalAmount() != null) {
        	ownerIncomeAmt += -serviceExpense.getTotalAmount();
        }
        
        
        //3 获取车主补贴明细列表
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        if (ownerOrderSubsidyDetail != null) {
        	ownerIncomeAmt += ownerOrderSubsidyDetail.stream().mapToInt(OwnerOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        //5 获取车主增值服务费用列表
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        if (ownerOrderIncrementDetail != null) {
        	ownerIncomeAmt += ownerOrderIncrementDetail.stream().filter(incr -> {return !OwnerCashCodeEnum.GPS_SERVICE_AMT.getCashNo().equals(incr.getCostCode()) &&
                    ! OwnerCashCodeEnum.SERVICE_CHARGE.getCashNo().equals(incr.getCostCode()) && 
                    ! OwnerCashCodeEnum.PROXY_CHARGE.getCashNo().equals(incr.getCostCode());}).mapToInt(OwnerOrderIncrementDetailEntity::getTotalAmount).sum();
        }
        // 6 获取gps服务费
        //车辆安装gps序列号列表 商品系统
        
        List<Integer> lsGpsSerialNumber = getLsGpsSerialNumber(ownerGoodsDetail.getGpsSerialNumber());
        List<OwnerOrderPurchaseDetailEntity> gpsCost =  ownerOrderCostCombineService.getGpsServiceAmtEntity(costBaseDTO,lsGpsSerialNumber);
        if (gpsCost != null) {
        	ownerIncomeAmt += -gpsCost.stream().mapToInt(OwnerOrderPurchaseDetailEntity::getTotalAmount).sum();
        }
        //7 获取车主油费 //（不含超里程）
        DeliveryOilCostVO deliveryOilCostVO = deliveryCarInfoPriceService.getOilCostByRenterOrderNo(settleOrders.getOrderNo(),ownerGoodsDetail.getCarEngineType());
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = Objects.isNull(deliveryOilCostVO)?null:deliveryOilCostVO.getOwnerGetAndReturnCarDTO();
        if (ownerGetAndReturnCarDTO != null) {
        	int oilDifferenceCrash = StringUtils.isBlank(ownerGetAndReturnCarDTO.getOilDifferenceCrash()) ? 0:Integer.valueOf(ownerGetAndReturnCarDTO.getOilDifferenceCrash());
        	ownerIncomeAmt += oilDifferenceCrash;
        }
        //8 管理后台补贴 （租客车主共用表 ，会员号区分车主/租客）
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(settleOrders.getOrderNo(),settleOrders.getOwnerMemNo());
        if (orderConsoleSubsidyDetails != null) {
        	ownerIncomeAmt += orderConsoleSubsidyDetails.stream().mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        //9 获取全局的车主订单罚金明细（租客车主共用表 ，会员号区分车主/租客）
        //调整为 主订单号+会员号 200305
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailEntitys = consoleOwnerOrderFineDeatailService.selectByOrderNo(settleOrders.getOrderNo(),settleOrders.getOwnerMemNo());
        if (consoleOwnerOrderFineDeatailEntitys != null) {
        	ownerIncomeAmt += consoleOwnerOrderFineDeatailEntitys.stream().mapToInt(ConsoleOwnerOrderFineDeatailEntity::getFineAmount).sum();
        }
        //10 车主罚金  调整为 主订单号 + 车主子订单号
        //调整为 主订单号+子订单号 200305
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOrderNo(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        if (ownerOrderFineDeatails != null) {
        	ownerIncomeAmt += ownerOrderFineDeatails.stream().mapToInt(OwnerOrderFineDeatailEntity::getFineAmount).sum();
        }
        //11后台管理操作费用表（无条件补贴）
        List<OrderConsoleCostDetailEntity> orderConsoleCostDetailEntity = orderConsoleCostDetailService.selectByOrderNoAndMemNo(settleOrders.getOrderNo(),settleOrders.getOwnerMemNo());
        if (orderConsoleCostDetailEntity != null) {
        	ownerIncomeAmt += orderConsoleCostDetailEntity.stream().mapToInt(OrderConsoleCostDetailEntity::getSubsidyAmount).sum();
        }
        //12 平台加油服务费
        int ownerPlatFormOilService = deliveryCarInfoPriceService.getOwnerPlatFormOilServiceChargeByOrderNo(settleOrders.getOrderNo());
        ownerIncomeAmt += -ownerPlatFormOilService;
        //13 补充 租客端的超里程，计算给车主。 add 200306 huangjing
        //跟租客有关联的地方（徐开心，之前是根据该方法上部的车主的参数来计算。目前不支持）
    	HandoverCarReqVO handoverCarReq = new HandoverCarReqVO();
    	handoverCarReq.setRenterOrderNo(settleOrders.getRenterOrderNo());
        HandoverCarRepVO handoverCarRep = handoverCarService.getRenterHandover(handoverCarReq);

		RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(settleOrders.getRenterOrderNo(),Boolean.TRUE);
		
		//同车主端油费计算，把租客的油费也计算出来。   去掉，逻辑上不能分开，结算的时候需要平账检测。只能从结构上分离。
		//2 租客 交接车-油费
//        DeliveryOilCostVO deliveryRenterOilCostVO = deliveryCarInfoPriceService.getOilCostByRenterOrderNo(settleOrders.getOrderNo(),renterGoodsDetail.getCarEngineType());
//        RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = Objects.isNull(deliveryRenterOilCostVO)?null:deliveryRenterOilCostVO.getRenterGetAndReturnCarDTO();
		
		
        //13.2 交接车-获取超里程费用 代码回退
//        MileageAmtDTO mileageAmtDTO = getMileageAmtDTO(settleOrders,settleOrders.getOwnerOrder(),handoverCarRep,ownerGoodsDetail);
		MileageAmtDTO mileageAmtDTO = orderSettleProxyService.getMileageAmtDTO(settleOrders,settleOrders.getRenterOrder(),handoverCarRep,renterGoodsDetail);
        FeeResult mileageAmt = deliveryCarInfoPriceService.getMileageAmtEntity(mileageAmtDTO);
        if (mileageAmt != null) {
        	if (!CommonUtils.isEscrowCar(ownerGoodsDetail.getCarOwnerType())) {
        		int mileageTotalAmt = mileageAmt.getTotalFee() == null ? 0:-mileageAmt.getTotalFee();
        		ownerIncomeAmt += mileageTotalAmt;
        	}
        }
        // 14 计算gps押金
        // 车辆注册号
        String carNo = ownerGoodsDetail.getCarNo() == null ? null:String.valueOf(ownerGoodsDetail.getCarNo());
        settleOrders.setCarNo(carNo);
        OwnerOrderIncrementDetailEntity gpsDepositInc = ownerOrderCostCombineService.getGpsDepositIncrement(costBaseDTO, carNo, ownerIncomeAmt);
		//代码重构，不应该根据租客来计算。
//		OwnerOrderPurchaseDetailEntity mileageAmt = ownerOrderCostCombineService.getMileageAmtEntity(mileageAmtDTO);
        //车辆类型封装，代管车的归平台，否则归车主。
		Integer carOwnerType = ownerGoodsDetail.getCarOwnerType();
        
        ownerCosts.setProxyExpense(proxyExpense);
        ownerCosts.setServiceExpense(serviceExpense);
        ownerCosts.setGpsCost(gpsCost);
        //平台加油服务费
        ownerCosts.setOwnerPlatFormOilService(ownerPlatFormOilService);
        //油费，（不含超里程费用）。
        ownerCosts.setOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDTO);
        //需要平账，去掉。
//        ownerCosts.setRenterGetAndReturnCarDTO(renterGetAndReturnCarDTO); ///add 200308 计算平台油费差价补贴
        //超里程
        ownerCosts.setMileageAmt(mileageAmt);
        ownerCosts.setCarOwnerType(carOwnerType);
        ////////////////////////////////////////////////
        
        ownerCosts.setOwnerOrderPurchaseDetail(ownerOrderPurchaseDetail);
        ownerCosts.setOwnerOrderIncrementDetail(ownerOrderIncrementDetail);
        ownerCosts.setOrderConsoleCostDetailEntity(orderConsoleCostDetailEntity);
        
        ownerCosts.setOwnerOrderFineDeatails(ownerOrderFineDeatails);
        ownerCosts.setConsoleOwnerOrderFineDeatailEntitys(consoleOwnerOrderFineDeatailEntitys);
        
        ownerCosts.setOwnerOrderSubsidyDetail(ownerOrderSubsidyDetail);
        ownerCosts.setOrderConsoleSubsidyDetails(orderConsoleSubsidyDetails);
        ownerCosts.setGpsDepositDetail(gpsDepositInc);
        settleOrders.setOwnerCosts(ownerCosts);
    }
    
    
    public void settleOrdersDefinitionSeparateOwner(SettleOrders settleOrders, SettleOrdersDefinition settleOrdersDefinition) {
        //2统计 车主结算费用明细， 补贴，费用总额
        handleOwnerAndPlatform(settleOrdersDefinition,settleOrders);
        //3统计 计算总费用
        countCostSeparateOwner(settleOrdersDefinition,settleOrders);        
    }
    
    /**
     * 统计 车主结算费用明细， 补贴，费用总额
     * @param settleOrdersDefinition
     * @param settleOrders
     */
    public void handleOwnerAndPlatform(SettleOrdersDefinition settleOrdersDefinition, SettleOrders settleOrders) {
        OwnerCosts ownerCosts = settleOrders.getOwnerCosts();
        //1车主费用明细
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = new ArrayList<>();
        if(Objects.nonNull(ownerCosts)){
           // 1.1 车主端代管车服务费
            OwnerOrderPurchaseDetailEntity proxyExpense = ownerCosts.getProxyExpense();
            if(Objects.nonNull(proxyExpense) && Objects.nonNull(proxyExpense.getTotalAmount()) && proxyExpense.getTotalAmount()!=0){
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(proxyExpense,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.PROXY_CHARGE.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.PROXY_CHARGE.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(proxyExpense.getId()));
                int amt = Objects.isNull(proxyExpense.getTotalAmount())?0:proxyExpense.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(-Math.abs(amt));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
                // 车主端代管车服务费车主端代管车服务费 费用平台端冲账
                orderOwnerSettleNewService.addProxyExpenseAmtToPlatform(proxyExpense,settleOrdersDefinition);
            }
        }
        // 1.2 车主端平台服务费
        OwnerOrderPurchaseDetailEntity serviceExpense = ownerCosts.getServiceExpense();
        if(Objects.nonNull(serviceExpense) && Objects.nonNull(serviceExpense.getTotalAmount()) && serviceExpense.getTotalAmount()!=0){
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
            BeanUtils.copyProperties(serviceExpense,accountOwnerCostSettleDetail);
            accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.SERVICE_CHARGE.getCashNo());
            accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.SERVICE_CHARGE.getTxt());
            accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(serviceExpense.getId()));
            int amt = Objects.isNull(serviceExpense.getTotalAmount())?0:serviceExpense.getTotalAmount();
            accountOwnerCostSettleDetail.setAmt(-Math.abs(amt));
            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            // 车主端平台服务费 费用平台端冲账
            orderOwnerSettleNewService.addServiceExpenseAmtToPlatform(serviceExpense,settleOrdersDefinition);
        }
        
        //1.3 获取车主费用列表
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = ownerCosts.getOwnerOrderPurchaseDetail();
        if(!CollectionUtils.isEmpty(ownerOrderPurchaseDetail)){
            for(int i=0; i<ownerOrderPurchaseDetail.size();i++){
                OwnerOrderPurchaseDetailEntity renterOrderCostDetail = ownerOrderPurchaseDetail.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(renterOrderCostDetail.getCostCode());
                accountOwnerCostSettleDetail.setSourceDetail(renterOrderCostDetail.getCostCodeDesc());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            }
        }
        //1.4获取车主增值服务费用列表
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = ownerCosts.getOwnerOrderIncrementDetail();
        if(!CollectionUtils.isEmpty(ownerOrderIncrementDetail)){
            for(int i=0; i<ownerOrderIncrementDetail.size();i++){
                OwnerOrderIncrementDetailEntity renterOrderCostDetail = ownerOrderIncrementDetail.get(i);
                // GPS 和 平台服务费  下单已落库  结算 实时算 故排除这两种费用
                // TODO 待这两种费用存值确认后 再处理
                if(
                		//排除200306 加上代管车服务费。
                        !OwnerCashCodeEnum.GPS_SERVICE_AMT.getCashNo().equals(renterOrderCostDetail.getCostCode()) &&
                        ! OwnerCashCodeEnum.SERVICE_CHARGE.getCashNo().equals(renterOrderCostDetail.getCostCode()) && 
                        ! OwnerCashCodeEnum.PROXY_CHARGE.getCashNo().equals(renterOrderCostDetail.getCostCode())
                ){
                    AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                    accountOwnerCostSettleDetail.setSourceCode(renterOrderCostDetail.getCostCode());
                    accountOwnerCostSettleDetail.setSourceDetail(renterOrderCostDetail.getCostDesc());
                    accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                    accountOwnerCostSettleDetail.setAmt(amt); //amt是负数
                    accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
                    // 获取车主增值服务费用列表 费用平台端冲账
                    orderOwnerSettleNewService.addOwnerOrderIncrementAmtToPlatform(renterOrderCostDetail,settleOrdersDefinition);
                }

            }
        }
        //1.5 获取gps服务费
        List<OwnerOrderPurchaseDetailEntity> gpsCost = ownerCosts.getGpsCost();
        if(!CollectionUtils.isEmpty(gpsCost)){
            for(int i=0; i<gpsCost.size();i++){
                OwnerOrderPurchaseDetailEntity renterOrderCostDetail = gpsCost.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(renterOrderCostDetail.getCostCode());
                accountOwnerCostSettleDetail.setSourceDetail(renterOrderCostDetail.getCostCodeDesc());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                //gps返回正数  车主取负
                int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(-Math.abs(amt));
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
                // 获取gps服务费 费用平台端冲账
                orderOwnerSettleNewService.addGpsCostAmtToPlatform(renterOrderCostDetail,settleOrdersDefinition);
            }
        }
        //1.6 获取车主油费
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = ownerCosts.getOwnerGetAndReturnCarDTO();
        if(Objects.nonNull(ownerGetAndReturnCarDTO) && !StringUtil.isBlank(ownerGetAndReturnCarDTO.getOilDifferenceCrash())){
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
            BeanUtils.copyProperties(ownerGetAndReturnCarDTO,accountOwnerCostSettleDetail);
            accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getCashNo());
            accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getTxt());
            //油费
            String carOilDifferenceCrash = ownerGetAndReturnCarDTO.getOilDifferenceCrash();
            accountOwnerCostSettleDetail.setAmt(Integer.valueOf(carOilDifferenceCrash));
            accountOwnerCostSettleDetail.setMemNo(settleOrders.getOwnerMemNo());
            accountOwnerCostSettleDetail.setOrderNo(settleOrders.getOrderNo());
            accountOwnerCostSettleDetail.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            //超里程  -->1.13
            
            //平台加油服务费  -->1.12平台加油服务费
            
        }
        //1.7平台加油服务费
        int ownerPlatFormOilService = ownerCosts.getOwnerPlatFormOilService();
        {
            //记录车主结算费用明细
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
            accountOwnerCostSettleDetail.setOrderNo(settleOrders.getOrderNo());
            accountOwnerCostSettleDetail.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
            accountOwnerCostSettleDetail.setMemNo(settleOrders.getOwnerMemNo());
            accountOwnerCostSettleDetail.setAmt(-ownerPlatFormOilService);
            accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE.getCashNo());
            accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE.getTxt());
            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            //记录平台收益
            AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
            entity.setOrderNo(settleOrders.getOrderNo());
            entity.setSourceCode(OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE.getCashNo());
            entity.setSourceDesc(OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE.getTxt());
            entity.setAmt(ownerPlatFormOilService);
            entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
            settleOrdersDefinition.addPlatformSubsidy(entity);
        }
        
        //1.8超里程  -->1.13  参考//1.7 获取车主油费  200306 huangjing
        FeeResult mileageAmt = ownerCosts.getMileageAmt();
        if(Objects.nonNull(mileageAmt)){  //&& !StringUtil.isBlank(mileageAmt.getTotalFee())
        	if(com.autoyol.platformcost.CommonUtils.isEscrowCar(ownerCosts.getCarOwnerType())) {
	            //记录平台收益
	            AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
//	            BeanUtils.copyProperties(mileageAmt,entity);
	            //主要是订单号
	            entity.setOrderNo(settleOrders.getOrderNo());
	            entity.setSourceCode(OwnerCashCodeEnum.MILEAGE_COST_OWNER_PROXY.getCashNo());
	            entity.setSourceDesc(OwnerCashCodeEnum.MILEAGE_COST_OWNER_PROXY.getTxt());
	            entity.setUniqueNo(String.valueOf(0)); //默认0
	            entity.setAmt(mileageAmt.getTotalFee()); //已经是正数
	            settleOrdersDefinition.addPlatformProfit(entity);
	            
        	}else {
	            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
	            //超里程
	            BeanUtils.copyProperties(mileageAmt,accountOwnerCostSettleDetail);
	            accountOwnerCostSettleDetail.setOrderNo(settleOrders.getOrderNo());
	            accountOwnerCostSettleDetail.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
	            accountOwnerCostSettleDetail.setMemNo(settleOrders.getOwnerMemNo());
	            accountOwnerCostSettleDetail.setAmt(mileageAmt.getTotalFee());  //金额的字段不一致。
	            accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.MILEAGE_COST_OWNER.getCashNo());
	            accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.MILEAGE_COST_OWNER.getTxt());
	            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
        	}
        }

        //1.9 查询车主罚金
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails = ownerCosts.getOwnerOrderFineDeatails();
        if(!CollectionUtils.isEmpty(ownerOrderFineDeatails)){
            for(int i=0; i<ownerOrderFineDeatails.size();i++){
                OwnerOrderFineDeatailEntity ownerOrderFineDetailEntity = ownerOrderFineDeatails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(ownerOrderFineDetailEntity,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(String.valueOf(ownerOrderFineDetailEntity.getFineType()));
                accountOwnerCostSettleDetail.setSourceDetail(ownerOrderFineDetailEntity.getFineTypeDesc());
                int fineAmount = Objects.isNull(ownerOrderFineDetailEntity.getFineAmount())?0:ownerOrderFineDetailEntity.getFineAmount();
                CostTypeEnum costTypeEnum = orderSettleProxyService.getCostTypeEnumByFine(ownerOrderFineDetailEntity.getFineSubsidySourceCode());
                accountOwnerCostSettleDetail.setCostType(costTypeEnum.getCode());
                accountOwnerCostSettleDetail.setAmt(fineAmount);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(ownerOrderFineDetailEntity.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

//                //罚金来源方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(ownerOrderFineDetailEntity.getFineSubsidySourceCode())){
//                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
//                    BeanUtils.copyProperties(ownerOrderFineDetailEntity,entity);
//                    entity.setSourceCode(String.valueOf(ownerOrderFineDetailEntity.getFineType()));
//                    entity.setSourceDesc(ownerOrderFineDetailEntity.getFineTypeDesc());
//                    entity.setUniqueNo(String.valueOf(ownerOrderFineDetailEntity.getId()));
//                    entity.setAmt(-fineAmount);
//                    entity.setUniqueNo(String.valueOf(ownerOrderFineDetailEntity.getId()));
//                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
//                    settleOrdersDefinition.addPlatformSubsidy(entity);
                	
                	//算平台收益
                	AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(ownerOrderFineDetailEntity,entity);
                    entity.setSourceCode(String.valueOf(ownerOrderFineDetailEntity.getFineType()));
                    entity.setSourceDesc(ownerOrderFineDetailEntity.getFineTypeDesc());
                    entity.setUniqueNo(String.valueOf(ownerOrderFineDetailEntity.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
                //罚金补贴方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(ownerOrderFineDetailEntity.getFineSubsidyCode())){
                    AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(ownerOrderFineDetailEntity,entity);
                    entity.setSourceCode(String.valueOf(ownerOrderFineDetailEntity.getFineType()));
                    entity.setSourceDesc(ownerOrderFineDetailEntity.getFineTypeDesc());
                    entity.setUniqueNo(String.valueOf(ownerOrderFineDetailEntity.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
            }
        }
        
        //1.10 全局的车主订单罚金明细
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailEntitys = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys();
        if(!CollectionUtils.isEmpty(consoleOwnerOrderFineDeatailEntitys)){
            for(int i=0; i<consoleOwnerOrderFineDeatailEntitys.size();i++){
                ConsoleOwnerOrderFineDeatailEntity consoleOwnerOrderFineDeatailEntity = consoleOwnerOrderFineDeatailEntitys.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(consoleOwnerOrderFineDeatailEntity,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(String.valueOf(consoleOwnerOrderFineDeatailEntity.getFineType()));
                accountOwnerCostSettleDetail.setSourceDetail(consoleOwnerOrderFineDeatailEntity.getFineTypeDesc());
                int fineAmount = Objects.isNull(consoleOwnerOrderFineDeatailEntity.getFineAmount())?0:consoleOwnerOrderFineDeatailEntity.getFineAmount();
                accountOwnerCostSettleDetail.setAmt(fineAmount);
                CostTypeEnum costTypeEnum = orderSettleProxyService.getCostTypeEnumByFine(consoleOwnerOrderFineDeatailEntity.getFineSubsidySourceCode());
                accountOwnerCostSettleDetail.setCostType(costTypeEnum.getCode());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(consoleOwnerOrderFineDeatailEntity.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
                
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(consoleOwnerOrderFineDeatailEntity.getFineSubsidySourceCode())){
                    AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(consoleOwnerOrderFineDeatailEntity,entity);
                    entity.setSourceCode(String.valueOf(consoleOwnerOrderFineDeatailEntity.getFineType()));
                    entity.setSourceDesc(consoleOwnerOrderFineDeatailEntity.getFineTypeDesc());
                    entity.setUniqueNo(String.valueOf(consoleOwnerOrderFineDeatailEntity.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
                //罚金补贴方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(consoleOwnerOrderFineDeatailEntity.getFineSubsidyCode())){
                    AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(consoleOwnerOrderFineDeatailEntity,entity);
                    entity.setSourceCode(String.valueOf(consoleOwnerOrderFineDeatailEntity.getFineType()));
                    entity.setSourceDesc(consoleOwnerOrderFineDeatailEntity.getFineTypeDesc());
                    entity.setUniqueNo(String.valueOf(consoleOwnerOrderFineDeatailEntity.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
                
            }
        }
        
        //1.11 平台无脑费用
        List<OrderConsoleCostDetailEntity> orderConsoleCostDetails = ownerCosts.getOrderConsoleCostDetailEntity();
        if(!CollectionUtils.isEmpty(orderConsoleCostDetails)){
            for(int i=0; i<orderConsoleCostDetails.size();i++){
                OrderConsoleCostDetailEntity orderConsoleCostDetail = orderConsoleCostDetails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(orderConsoleCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(orderConsoleCostDetail.getSubsidyTypeCode());
                accountOwnerCostSettleDetail.setSourceDetail(orderConsoleCostDetail.getSubsidTypeName());
                int fineAmount = Objects.isNull(orderConsoleCostDetail.getSubsidyAmount())?0:orderConsoleCostDetail.getSubsidyAmount();
                CostTypeEnum costTypeEnum = orderSettleProxyService.getCostTypeEnumByConsoleCost(orderConsoleCostDetail.getSubsidyTargetCode());
                accountOwnerCostSettleDetail.setCostType(costTypeEnum.getCode());
                accountOwnerCostSettleDetail.setAmt(fineAmount);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(orderConsoleCostDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

//                //罚金来源方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleCostDetail.getSubsidySourceCode())){
//                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
//                    BeanUtils.copyProperties(orderConsoleCostDetail,entity);
//                    entity.setSourceCode(orderConsoleCostDetail.getSubsidyTypeCode());
//                    entity.setSourceDesc(orderConsoleCostDetail.getSubsidTypeName());
//                    entity.setUniqueNo(String.valueOf(orderConsoleCostDetail.getId()));
//                    entity.setAmt(-fineAmount);
//                    entity.setUniqueNo(String.valueOf(orderConsoleCostDetail.getId()));
//                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
//                    settleOrdersDefinition.addPlatformSubsidy(entity);
                	
                	//算平台收益
                	AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(orderConsoleCostDetail,entity);
                    entity.setSourceCode(orderConsoleCostDetail.getSubsidyTypeCode());
                    entity.setSourceDesc(orderConsoleCostDetail.getSubsidTypeName());
                    entity.setUniqueNo(String.valueOf(orderConsoleCostDetail.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
                //罚金补贴方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleCostDetail.getSubsidyTargetCode())){
                    AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(orderConsoleCostDetail,entity);
                    entity.setSourceCode(orderConsoleCostDetail.getSubsidyTypeCode());
                    entity.setSourceDesc(orderConsoleCostDetail.getSubsidTypeName());
                    entity.setUniqueNo(String.valueOf(orderConsoleCostDetail.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
            }
        }
        
        // -----------------------------------------PlatformSubsidy  以上是PlatformProfit
        // 1.12 获取车主补贴明细列表
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetails = ownerCosts.getOwnerOrderSubsidyDetail();
        if(!CollectionUtils.isEmpty(ownerOrderSubsidyDetails)){
            for(int i=0; i<ownerOrderSubsidyDetails.size();i++){
                OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity = ownerOrderSubsidyDetails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(ownerOrderSubsidyDetailEntity,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(ownerOrderSubsidyDetailEntity.getSubsidyCostCode());
                accountOwnerCostSettleDetail.setSourceDetail(ownerOrderSubsidyDetailEntity.getSubsidyCostName());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(ownerOrderSubsidyDetailEntity.getId()));
                CostTypeEnum costTypeEnum = orderSettleProxyService.getCostTypeEnumBySubsidy(ownerOrderSubsidyDetailEntity.getSubsidySourceCode());
                accountOwnerCostSettleDetail.setCostType(costTypeEnum.getCode());
                int subsidyAmount = Objects.isNull(ownerOrderSubsidyDetailEntity.getSubsidyAmount())?0:ownerOrderSubsidyDetailEntity.getSubsidyAmount();
                accountOwnerCostSettleDetail.setAmt(subsidyAmount);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(ownerOrderSubsidyDetailEntity.getSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(ownerOrderSubsidyDetailEntity,entity);
                    entity.setSourceCode(ownerOrderSubsidyDetailEntity.getSubsidyCostCode());
                    entity.setSourceDesc(ownerOrderSubsidyDetailEntity.getSubsidyCostName());
                    entity.setUniqueNo(String.valueOf(ownerOrderSubsidyDetailEntity.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
                ///add 200309
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(ownerOrderSubsidyDetailEntity.getSubsidyTargetCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(ownerOrderSubsidyDetailEntity,entity);
                    entity.setSourceCode(ownerOrderSubsidyDetailEntity.getSubsidyCostCode());
                    entity.setSourceDesc(ownerOrderSubsidyDetailEntity.getSubsidyCostName());
                    entity.setUniqueNo(String.valueOf(ownerOrderSubsidyDetailEntity.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
            }
        }
        
        //1.13 管理后台补贴
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = ownerCosts.getOrderConsoleSubsidyDetails();
        if(!CollectionUtils.isEmpty(orderConsoleSubsidyDetails)){
            for(int i=0; i<orderConsoleSubsidyDetails.size();i++){
                OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity = orderConsoleSubsidyDetails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(orderConsoleSubsidyDetailEntity,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(orderConsoleSubsidyDetailEntity.getSubsidyCostCode());
                accountOwnerCostSettleDetail.setSourceDetail(orderConsoleSubsidyDetailEntity.getSubsidyCostName());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(orderConsoleSubsidyDetailEntity.getId()));
                CostTypeEnum costTypeEnum = orderSettleProxyService.getCostTypeEnumBySubsidy(orderConsoleSubsidyDetailEntity.getSubsidySourceCode());
                accountOwnerCostSettleDetail.setCostType(costTypeEnum.getCode());
                int subsidyAmount = Objects.isNull(orderConsoleSubsidyDetailEntity.getSubsidyAmount())?0:orderConsoleSubsidyDetailEntity.getSubsidyAmount();
                accountOwnerCostSettleDetail.setAmt(subsidyAmount);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(orderConsoleSubsidyDetailEntity.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(orderConsoleSubsidyDetailEntity,entity);
                    entity.setSourceCode(orderConsoleSubsidyDetailEntity.getSubsidyCostCode());
                    entity.setSourceDesc(orderConsoleSubsidyDetailEntity.getSubsidyCostName());
                    entity.setUniqueNo(String.valueOf(orderConsoleSubsidyDetailEntity.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(orderConsoleSubsidyDetailEntity,entity);
                    entity.setSourceCode(orderConsoleSubsidyDetailEntity.getSubsidyCostCode());
                    entity.setSourceDesc(orderConsoleSubsidyDetailEntity.getSubsidyCostName());
                    entity.setUniqueNo(String.valueOf(orderConsoleSubsidyDetailEntity.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
            }
        }
        
        // 1.14 gsp押金
        OwnerOrderIncrementDetailEntity gpsDeposit = ownerCosts.getGpsDepositDetail();
        if (gpsDeposit != null) {
        	AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
            BeanUtils.copyProperties(gpsDeposit,accountOwnerCostSettleDetail);
            accountOwnerCostSettleDetail.setSourceCode(gpsDeposit.getCostCode());
            accountOwnerCostSettleDetail.setSourceDetail(gpsDeposit.getCostDesc());
            //accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(gpsDeposit.getId()));
            int amt = Objects.isNull(gpsDeposit.getTotalAmount())?0:gpsDeposit.getTotalAmount();
            accountOwnerCostSettleDetail.setAmt(amt); // amt是负数
            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            // 获取车主gps押金用平台端冲账
            orderOwnerSettleNewService.addGpsDepositIncrementAmtToPlatform(gpsDeposit,settleOrdersDefinition);
        }
        
        settleOrdersDefinition.setAccountOwnerCostSettleDetails(accountOwnerCostSettleDetails);
    }
    
    private void countCostSeparateOwner(SettleOrdersDefinition settleOrdersDefinition,SettleOrders settleOrders) {
//      List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = settleOrdersDefinition.getAccountRenterCostSettleDetails();
      //1租客总账
//      if(!CollectionUtils.isEmpty(accountRenterCostSettleDetails)){
//          int rentCostAmt = accountRenterCostSettleDetails.stream().filter(obj ->{return obj.getAmt()<0;}).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
//          settleOrdersDefinition.setRentCostAmt(rentCostAmt);
//          int rentSubsidyAmt = accountRenterCostSettleDetails.stream().filter(obj ->{return obj.getAmt()>0;}).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
//          settleOrdersDefinition.setRentSubsidyAmt(rentSubsidyAmt);
//      }
      //2车主总账
      List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = settleOrdersDefinition.getAccountOwnerCostSettleDetails();
      if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
    	  //车主结算的总费用
          int ownerCostAmtFinal = accountOwnerCostSettleDetails.stream().mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
          settleOrdersDefinition.setOwnerCostAmtFinal(ownerCostAmtFinal);
          
          int ownerCostAmt = accountOwnerCostSettleDetails.stream().filter(obj ->{return obj.getAmt()>0;}).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
          settleOrdersDefinition.setOwnerCostAmt(ownerCostAmt);
          int ownerSubsidyAmt = accountOwnerCostSettleDetails.stream().filter(obj ->{return obj.getAmt()<0;}).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
          settleOrdersDefinition.setOwnerSubsidyAmt(ownerSubsidyAmt);
      }
      // 3 计算车主、租客交接车油费差
      orderOwnerSettleNewService.addPlatFormAmtSeparateOwner(settleOrdersDefinition,settleOrders);
      //4 平台收益总账
      List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails = settleOrdersDefinition.getAccountPlatformProfitDetails();
      if(!CollectionUtils.isEmpty(accountPlatformProfitDetails)){
          int platformProfitAmt = accountPlatformProfitDetails.stream().mapToInt(AccountPlatformProfitDetailEntity::getAmt).sum();
          settleOrdersDefinition.setPlatformProfitAmt(platformProfitAmt);
      }
      //5 平台补贴总额
      List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetails = settleOrdersDefinition.getAccountPlatformSubsidyDetails();
      if(!CollectionUtils.isEmpty(accountPlatformSubsidyDetails)){
          int platformSubsidyAmt = accountPlatformSubsidyDetails.stream().mapToInt(AccountPlatformSubsidyDetailEntity::getAmt).sum();
          settleOrdersDefinition.setPlatformSubsidyAmt(platformSubsidyAmt);
      }
    }
    
    public void insertSettleOrdersSeparateOwner(SettleOrdersDefinition settleOrdersDefinition) {
        //2 明细落库
        //2.1 租客端 明细落库
//        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = settleOrdersDefinition.getAccountRenterCostSettleDetails();
//        cashierSettleService.insertAccountRenterCostSettleDetails(accountRenterCostSettleDetails);
    	
        //2.1 车主端 明细落库
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = settleOrdersDefinition.getAccountOwnerCostSettleDetails();
        cashierSettleService.insertAccountOwnerCostSettleDetails(accountOwnerCostSettleDetails);
        //2.2 平台补贴明细 落库
        List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetails = settleOrdersDefinition.getAccountPlatformSubsidyDetails();
        cashierSettleService.insertAccountPlatformSubsidyDetails(accountPlatformSubsidyDetails);
        //2.2 平台收益明细 落库
        List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails = settleOrdersDefinition.getAccountPlatformProfitDetails();
        cashierSettleService.insertAccountPlatformProfitDetails(accountPlatformProfitDetails);
    }
    
  //---------------------------------------------------------------------------------------------------------------------
	
    
    /**
     *  车主返回基本信息
     * @return
     */
    private CostBaseDTO getCostBaseOwner(OwnerOrderEntity ownerOrder){
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(ownerOrder.getOrderNo());
        costBaseDTO.setMemNo(ownerOrder.getMemNo());
        costBaseDTO.setOwnerOrderNo(ownerOrder.getOwnerOrderNo());
        costBaseDTO.setStartTime(ownerOrder.getExpRentTime());
        costBaseDTO.setEndTime(ownerOrder.getExpRevertTime());
        return costBaseDTO;
    }
    
    /**
     * 车主交接车-油费 参数构建
     * @param ownerOrder
     * @return
     */
    private OilAmtDTO getOilAmtOwner(OwnerOrderEntity ownerOrder,HandoverCarRepVO handoverCarRep,OwnerGoodsDetailDTO ownerGoodsDetail) {
        OilAmtDTO oilAmtDTO = new OilAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseOwner(ownerOrder);
        oilAmtDTO.setCostBaseDTO(costBaseDTO);

        oilAmtDTO.setCarOwnerType(ownerGoodsDetail.getCarOwnerType());
        //
        OrderEntity orderEntity = orderService.getOrderEntity(ownerOrder.getOrderNo());
        String cityCodeStr = "";
        if(Objects.nonNull(orderEntity) && Objects.nonNull(orderEntity.getCityCode())){
            cityCodeStr = orderEntity.getCityCode();
        }
        if(StringUtil.isBlank(cityCodeStr)){
            throw new RuntimeException("结算下单城市不存在");
        }
        oilAmtDTO.setCityCode(Integer.parseInt(cityCodeStr));
        oilAmtDTO.setEngineType(ownerGoodsDetail.getCarEngineType());
        oilAmtDTO.setOilVolume(ownerGoodsDetail.getCarOilVolume());
        //
        oilAmtDTO.setOilScaleDenominator(ownerGoodsDetail.getOilTotalCalibration());

        //默认值0  取/还 车油表刻度
        oilAmtDTO.setGetOilScale(0);
        oilAmtDTO.setReturnOilScale(0);
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities = handoverCarRep.getOwnerHandoverCarInfoEntities();
        if(!CollectionUtils.isEmpty(ownerHandoverCarInfoEntities)){
            for(int i=0;i<ownerHandoverCarInfoEntities.size();i++){
                OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = ownerHandoverCarInfoEntities.get(i);
                if(RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue().equals(ownerHandoverCarInfoEntity.getType())
                        ||  RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().equals(ownerHandoverCarInfoEntity.getType())
                ){
                    oilAmtDTO.setReturnOilScale(Objects.isNull(ownerHandoverCarInfoEntity.getOilNum())?0:ownerHandoverCarInfoEntity.getOilNum());
                }

                if(RenterHandoverCarTypeEnum.RENTER_TO_OWNER.getValue().equals(ownerHandoverCarInfoEntity.getType())
                        ||  RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().equals(ownerHandoverCarInfoEntity.getType())
                ){
                    oilAmtDTO.setGetOilScale(Objects.isNull(ownerHandoverCarInfoEntity.getOilNum())?0:ownerHandoverCarInfoEntity.getOilNum());
                }
            }
        }
        return oilAmtDTO;
    }
    
    /**
     * 交接车-获取超里程费用   车主
     */
    private MileageAmtDTO getMileageAmtDTO(SettleOrders settleOrders, OwnerOrderEntity ownerOrder,HandoverCarRepVO handoverCarRep,OwnerGoodsDetailDTO ownerGoodsDetail) {
        MileageAmtDTO mileageAmtDTO = new MileageAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseRent(settleOrders,ownerOrder);
        mileageAmtDTO.setCostBaseDTO(costBaseDTO);

        mileageAmtDTO.setCarOwnerType(ownerGoodsDetail.getCarOwnerType());
        mileageAmtDTO.setGuideDayPrice(ownerGoodsDetail.getCarGuideDayPrice());
        mileageAmtDTO.setDayMileage(ownerGoodsDetail.getCarDayMileage());
        
        //默认值0  取/还 车里程数
        mileageAmtDTO.setGetmileage(0);
        mileageAmtDTO.setReturnMileage(0);
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfos = handoverCarRep.getOwnerHandoverCarInfoEntities();
        if(!CollectionUtils.isEmpty(ownerHandoverCarInfos)){
            for(int i=0;i<ownerHandoverCarInfos.size();i++){
                OwnerHandoverCarInfoEntity ownerHandoverCarInfo = ownerHandoverCarInfos.get(i);
                if(RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue().equals(ownerHandoverCarInfo.getType())
                        ||  RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().equals(ownerHandoverCarInfo.getType())
                ){
                    mileageAmtDTO.setGetmileage(Objects.isNull(ownerHandoverCarInfo.getMileageNum())?0:ownerHandoverCarInfo.getMileageNum());
                }

                if(RenterHandoverCarTypeEnum.RENTER_TO_OWNER.getValue().equals(ownerHandoverCarInfo.getType())
                        ||  RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().equals(ownerHandoverCarInfo.getType())
                ){
                    mileageAmtDTO.setReturnMileage(Objects.isNull(ownerHandoverCarInfo.getMileageNum())?0:ownerHandoverCarInfo.getMileageNum());

                }
            }
        }
        return mileageAmtDTO;
    }
    
    /**
     * 车主返回基本信息
     * @param settleOrders
     * @param ownerOrder
     * @return
     */
    private CostBaseDTO getCostBaseRent(SettleOrders settleOrders,OwnerOrderEntity ownerOrder){
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(ownerOrder.getOrderNo());
        costBaseDTO.setMemNo(ownerOrder.getMemNo());
        costBaseDTO.setOwnerOrderNo(ownerOrder.getOwnerOrderNo());
        costBaseDTO.setStartTime(ownerOrder.getExpRentTime());
        costBaseDTO.setEndTime(ownerOrder.getExpRevertTime());
        return costBaseDTO;
    }
    
    /**
     * gps 列表
     * @param gpsSerialNumber
     * @return
     */
    private List<Integer> getLsGpsSerialNumber(String gpsSerialNumber) {
        if(StringUtil.isBlank(gpsSerialNumber)){
            return Collections.emptyList();
        }
        String[] gpsSerialNumberArr = gpsSerialNumber.split(",");
        List<Integer> list = new ArrayList<>();
        	for(int i=0;i<gpsSerialNumberArr.length;i++){
                if(!StringUtil.isBlank(gpsSerialNumberArr[i])){
                	try {
                		list.add(Integer.valueOf(gpsSerialNumberArr[i]));
                	} catch (Exception e) {
            			log.error("封装gps序列号异常，跳过:",e);
            		}
                }
            }

        return list;
    }
    
    
    /**
	 * 更新车辆gps押金
	 * @param orderNo
	 * @param carNo
	 * @param updateBill
	 */
    public void updateCarDeposit(SettleOrders settleOrders) {
    	if (settleOrders == null) {
    		return;
    	}
    	String orderNo = settleOrders.getOrderNo();
    	String carNo = settleOrders.getCarNo();
    	OwnerCosts ownerCosts = settleOrders.getOwnerCosts();
    	if (ownerCosts == null) {
    		return;
    	}
    	OwnerOrderIncrementDetailEntity gpsDepositDetail = ownerCosts.getGpsDepositDetail();
    	if (gpsDepositDetail == null) {
    		return;
    	}
    	Integer updateBill = gpsDepositDetail.getTotalAmount() == null ? null:Math.abs(gpsDepositDetail.getTotalAmount());
    	ownerOrderCostCombineService.updateCarDeposit(orderNo, carNo, updateBill);
    }
    
}

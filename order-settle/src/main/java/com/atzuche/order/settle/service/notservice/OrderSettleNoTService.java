package com.atzuche.order.settle.service.notservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitEntity;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.vo.delivery.DeliveryOilCostVO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.ownercost.entity.*;
import com.atzuche.order.ownercost.service.*;
import com.autoyol.platformcost.model.FeeResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountrenterdeposit.vo.req.OrderCancelRenterDepositReqVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostToFineReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.RenterCancelWZDepositCostReqVO;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.req.DeductDepositToRentCostReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.coin.service.AccountRenterCostCoinService;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.OilAmtDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SysOrHandEnum;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.vo.handover.HandoverCarRepVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarReqVO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.req.SettleCancelOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.doc.util.StringUtil;

import lombok.extern.slf4j.Slf4j;
//
///**
// * 订单结算
// */
@Service
@Slf4j
public class OrderSettleNoTService {
    @Autowired CashierNoTService cashierNoTService;
    @Autowired private CashierService cashierService;
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
    @Autowired private RenterOrderFineDeatailService renterOrderFineDeatailService;
    @Autowired private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
    @Autowired private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
    @Autowired private OwnerOrderCostCombineService ownerOrderCostCombineService;
    @Autowired private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
    @Autowired private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
    @Autowired private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired private HandoverCarService handoverCarService;
    @Autowired private OwnerGoodsService ownerGoodsService;
    @Autowired private RenterOrderService renterOrderService;
    @Autowired private OwnerOrderService ownerOrderService;
    @Autowired private OrderStatusService orderStatusService;
    @Autowired private RenterGoodsService renterGoodsService;
    @Autowired private AccountRenterCostCoinService accountRenterCostCoinService;
    @Autowired private WalletProxyService walletProxyService;
    @Autowired private OrderFlowService orderFlowService;
    @Autowired private OrderService orderService;
    @Autowired private OwnerOrderFineDeatailService ownerOrderFineDeatailService;
    @Autowired private OrderCouponService orderCouponService;
    @Autowired private OrderSettleNewService orderSettleNewService;
    @Autowired private DeliveryCarInfoPriceService deliveryCarInfoPriceService;

    /**
     * 车辆结算
     * @param orderNo
     * @return
     */
    public List<AccountRenterCostDetailEntity> getAccountRenterCostDetailsByOrderNo(String orderNo){
        return cashierSettleService.getAccountRenterCostDetailsByOrderNo(orderNo);
    }


    public SettleOrders preInitSettleOrders(String orderNo,String renterOrderNo,String ownerOrderNo) {
        SettleOrders settleOrders = new SettleOrders();
        settleOrders.setOrderNo(orderNo);

        //1 校验参数
        if(StringUtil.isBlank(orderNo)){
            throw new OrderSettleFlatAccountException();
        }
        //查询租客子单
        if(org.apache.commons.lang.StringUtils.isNotBlank(renterOrderNo)) {
	        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
	        if(Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())){
	            throw new OrderSettleFlatAccountException();
	        }
	        String renterMemNo = renterOrder.getRenterMemNo();
	        settleOrders.setRenterMemNo(renterMemNo);
	        settleOrders.setRenterOrderNo(renterOrderNo);
	        settleOrders.setRenterOrder(renterOrder);
        }


        if(org.apache.commons.lang.StringUtils.isNotBlank(ownerOrderNo)) {
	        OwnerOrderEntity ownerOrder = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
	        if(Objects.isNull(ownerOrder) || Objects.isNull(ownerOrder.getOwnerOrderNo())){
	            throw new OrderSettleFlatAccountException();
	        }

	        String ownerMemNo = ownerOrder.getMemNo();
	        settleOrders.setOwnerOrderNo(ownerOrderNo);
	        settleOrders.setOwnerMemNo(ownerMemNo);
	        settleOrders.setOwnerOrder(ownerOrder);

        }

        // 2 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
//        this.check(renterOrder);
        // 3 初始化数据

        // 3.1获取租客子订单 和 租客会员号
//        String renterOrderNo = renterOrder.getRenterOrderNo();
//        String renterMemNo = renterOrder.getRenterMemNo();
        //3.2获取车主子订单 和 车主会员号
//        String ownerOrderNo = ownerOrder.getOwnerOrderNo();

        return settleOrders;
    }



    /**
     * 初始化结算对象
     * @param orderNo
     */
    public SettleOrders initSettleOrders(String orderNo) {
        //1 校验参数
        if(StringUtil.isBlank(orderNo)){
            throw new OrderSettleFlatAccountException();
        }
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())){
            throw new OrderSettleFlatAccountException();
        }
        OwnerOrderEntity ownerOrder = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(ownerOrder) || Objects.isNull(ownerOrder.getOwnerOrderNo())){
            throw new OrderSettleFlatAccountException();
        }

        // 2 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
        this.check(renterOrder);
        // 3 初始化数据

        // 3.1获取租客子订单 和 租客会员号
        String renterOrderNo = renterOrder.getRenterOrderNo();
        String renterMemNo = renterOrder.getRenterMemNo();
        //3.2获取车主子订单 和 车主会员号
        String ownerOrderNo = ownerOrder.getOwnerOrderNo();
        String ownerMemNo = ownerOrder.getMemNo();

        SettleOrders settleOrders = new SettleOrders();
        settleOrders.setOrderNo(orderNo);
        settleOrders.setRenterOrderNo(renterOrderNo);
        settleOrders.setOwnerOrderNo(ownerOrderNo);
        settleOrders.setRenterMemNo(renterMemNo);
        settleOrders.setOwnerMemNo(ownerMemNo);
        settleOrders.setRenterOrder(renterOrder);
        settleOrders.setOwnerOrder(ownerOrder);
        return settleOrders;
    }
    /**
     * 校验是否可以结算 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
     * @param renterOrder
     */
    public void check(RenterOrderEntity renterOrder) {
        // 1 订单校验是否可以结算
        OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(renterOrder.getOrderNo());
        if(OrderStatusEnum.TO_SETTLE.getStatus() == orderStatus.getStatus()){
            throw new RuntimeException("租客订单状态不是待结算，不能结算");
        }
        //2校验租客是否还车
        boolean isReturn = handoverCarService.isReturnCar(renterOrder.getOrderNo());
        if(!isReturn){
            throw new RuntimeException("租客未还车不能结算");
        }
        //3 校验是否存在 理赔  存在不结算
        boolean isClaim = cashierSettleService.getOrderClaim(renterOrder.getOrderNo());
        if(isClaim){
            throw new RuntimeException("租客存在理赔信息不能结算");
        }
        //3 是否存在 暂扣存在不结算
        boolean isDetain = cashierSettleService.getOrderDetain(renterOrder.getOrderNo());
        if(isDetain){
            throw new RuntimeException("租客存在暂扣信息不能结算");
        }
        //4 先查询  发现 有结算数据停止结算 手动处理
        orderSettleNewService.checkIsSettle(renterOrder.getOrderNo());
    }

    /**
     *  租客返回基本信息
     * @return
     */
    private CostBaseDTO getCostBaseRent(SettleOrders settleOrders,RenterOrderEntity renterOrder){
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(renterOrder.getOrderNo());
        costBaseDTO.setMemNo(renterOrder.getRenterMemNo());
        costBaseDTO.setRenterOrderNo(renterOrder.getRenterOrderNo());
        costBaseDTO.setStartTime(renterOrder.getExpRentTime());
        costBaseDTO.setEndTime(renterOrder.getExpRevertTime());
        return costBaseDTO;
    }
    /**
     * 租客交接车-油费 参数构建
     * @param settleOrders
     * @param renterOrder
     * @return
     */
    private OilAmtDTO getOilAmtDTO(SettleOrders settleOrders,RenterOrderEntity renterOrder,HandoverCarRepVO handoverCarRep,RenterGoodsDetailDTO renterGoodsDetail) {
        OilAmtDTO oilAmtDTO = new OilAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseRent(settleOrders,renterOrder);
        oilAmtDTO.setCostBaseDTO(costBaseDTO);
        oilAmtDTO.setCarOwnerType(renterGoodsDetail.getCarOwnerType());
        OrderEntity orderEntity = orderService.getOrderEntity(settleOrders.getOrderNo());
        String cityCodeStr = "";
        if(Objects.nonNull(orderEntity) && Objects.nonNull(orderEntity.getCityCode())){
            cityCodeStr = orderEntity.getCityCode();
        }
        if(StringUtil.isBlank(cityCodeStr)){
            throw new RuntimeException("结算下单城市不存在");
        }
        oilAmtDTO.setCityCode(Integer.parseInt(cityCodeStr));
        oilAmtDTO.setEngineType(renterGoodsDetail.getCarEngineType());
        oilAmtDTO.setOilVolume(renterGoodsDetail.getCarOilVolume());
        //
        oilAmtDTO.setOilScaleDenominator(renterGoodsDetail.getCarOilVolume());

        //默认值0  取/还 车油表刻度
        oilAmtDTO.setGetOilScale(0);
        oilAmtDTO.setReturnOilScale(0);
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfos = handoverCarRep.getRenterHandoverCarInfoEntities();
        if(!CollectionUtils.isEmpty(renterHandoverCarInfos)){
            for(int i=0;i<renterHandoverCarInfos.size();i++){
                RenterHandoverCarInfoEntity renterHandoverCarInfo = renterHandoverCarInfos.get(i);
                if(RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                ||  RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                ){
                    oilAmtDTO.setReturnOilScale(Objects.isNull(renterHandoverCarInfo.getOilNum())?0:renterHandoverCarInfo.getOilNum());
                }

                if(RenterHandoverCarTypeEnum.RENTER_TO_OWNER.getValue().equals(renterHandoverCarInfo.getType())
                        ||  RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().equals(renterHandoverCarInfo.getType())
                ){
                    oilAmtDTO.setGetOilScale(Objects.isNull(renterHandoverCarInfo.getOilNum())?0:renterHandoverCarInfo.getOilNum());
                }
            }
        }

        return oilAmtDTO;
    }

    /**
     * 交接车-获取超里程费用
     */
    private MileageAmtDTO getMileageAmtDTO(SettleOrders settleOrders, RenterOrderEntity renterOrder,HandoverCarRepVO handoverCarRep,RenterGoodsDetailDTO renterGoodsDetail) {
        MileageAmtDTO mileageAmtDTO = new MileageAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseRent(settleOrders,renterOrder);
        mileageAmtDTO.setCostBaseDTO(costBaseDTO);

        mileageAmtDTO.setCarOwnerType(renterGoodsDetail.getCarOwnerType());
        mileageAmtDTO.setGuideDayPrice(renterGoodsDetail.getCarGuidePrice());
        mileageAmtDTO.setDayMileage(renterGoodsDetail.getCarDayMileage());

        //默认值0  取/还 车里程数
        mileageAmtDTO.setGetmileage(0);
        mileageAmtDTO.setReturnMileage(0);
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfos = handoverCarRep.getRenterHandoverCarInfoEntities();
        if(!CollectionUtils.isEmpty(renterHandoverCarInfos)){
            for(int i=0;i<renterHandoverCarInfos.size();i++){
                RenterHandoverCarInfoEntity renterHandoverCarInfo = renterHandoverCarInfos.get(i);
                if(RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                        ||  RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                ){
                    mileageAmtDTO.setReturnMileage(Objects.isNull(renterHandoverCarInfo.getMileageNum())?0:renterHandoverCarInfo.getOilNum());
                }

                if(RenterHandoverCarTypeEnum.RENTER_TO_OWNER.getValue().equals(renterHandoverCarInfo.getType())
                        ||  RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().equals(renterHandoverCarInfo.getType())
                ){
                    mileageAmtDTO.setGetmileage(Objects.isNull(renterHandoverCarInfo.getMileageNum())?0:renterHandoverCarInfo.getOilNum());

                }
            }
        }
        return mileageAmtDTO;
    }

    /**
     * 查询租客费用明细
     * @param settleOrders
     */
    public void getRenterCostSettleDetail(SettleOrders settleOrders) {
    	RentCosts rentCosts = new RentCosts();
    	//1  初始化
    	//1.1 油费、超里程费用 配送模块需要的参数
    	HandoverCarReqVO handoverCarReq = new HandoverCarReqVO();
    	handoverCarReq.setRenterOrderNo(settleOrders.getRenterOrderNo());
        HandoverCarRepVO handoverCarRep = handoverCarService.getRenterHandover(handoverCarReq);
        // 1.2 油费、超里程费用 订单商品需要的参数
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(settleOrders.getRenterOrderNo(),Boolean.TRUE);

        //1 查询租车费用
        List<RenterOrderCostDetailEntity> renterOrderCostDetails = renterOrderCostDetailService.listRenterOrderCostDetail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        //2 交接车-油费
        DeliveryOilCostVO deliveryOilCostVO = deliveryCarInfoPriceService.getOilCostByRenterOrderNo(settleOrders.getOrderNo(),renterGoodsDetail.getCarEngineType());
        RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = Objects.isNull(deliveryOilCostVO)?null:deliveryOilCostVO.getRenterGetAndReturnCarDTO();

        //3 交接车-获取超里程费用
        MileageAmtDTO mileageAmtDTO = getMileageAmtDTO(settleOrders,settleOrders.getRenterOrder(),handoverCarRep,renterGoodsDetail);
        FeeResult feeResult = deliveryCarInfoPriceService.getMileageAmtEntity(mileageAmtDTO);

        //4 补贴
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        //5 租客罚金
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatails = renterOrderFineDeatailService.listRenterOrderFineDeatail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        //6 管理后台补贴 （租客车主共用表 ，会员号区分车主/租客）
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        //7 获取全局的租客订单罚金明细（租客车主共用表 ，会员号区分车主/租客）
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());

        //租车费用之和 等于 租车费用列表 + 补贴 + 管理后台补贴 过滤 RenterCashCodeEnum.RENT_AMT = getCostCode
        int renterOrderCost = 0;
        if(!CollectionUtils.isEmpty(renterOrderCostDetails)){
            renterOrderCost = renterOrderCost + renterOrderCostDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getCostCode());
            }).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
        }
        if(!CollectionUtils.isEmpty(renterOrderSubsidyDetails)){
            renterOrderCost = renterOrderCost + renterOrderSubsidyDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getSubsidyCostCode());
            }).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        if(!CollectionUtils.isEmpty(orderConsoleSubsidyDetails)){
            renterOrderCost = renterOrderCost + orderConsoleSubsidyDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getSubsidyCostCode());
            }).mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
        }

        rentCosts.setRenterOrderCostDetails(renterOrderCostDetails);
        rentCosts.setOilAmt(renterGetAndReturnCarDTO);
        rentCosts.setMileageAmt(feeResult);
        rentCosts.setRenterOrderSubsidyDetails(renterOrderSubsidyDetails);
        rentCosts.setRenterOrderFineDeatails(renterOrderFineDeatails);
        rentCosts.setOrderConsoleSubsidyDetails(orderConsoleSubsidyDetails);
        rentCosts.setConsoleRenterOrderFineDeatails(consoleRenterOrderFineDeatails);

        settleOrders.setRenterOrderCost(renterOrderCost);
        settleOrders.setRentCosts(rentCosts);
    }

    /**
     * 查询车主费用明细
     * @param settleOrders
     */
    public void getOwnerCostSettleDetail(SettleOrders settleOrders) {
        OwnerCosts ownerCosts = new OwnerCosts();
        //1  初始化
        //1.1 油费、超里程费用 配送模块需要的参数
        HandoverCarReqVO handoverCarReq = new HandoverCarReqVO();
        handoverCarReq.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
        HandoverCarRepVO handoverCarRep = handoverCarService.getRenterHandover(handoverCarReq);
        // 1.2 油费、超里程费用 订单商品需要的参数
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(settleOrders.getOwnerOrderNo(),Boolean.TRUE);

        //1 车主端代管车服务费
        CostBaseDTO costBaseDTO= getCostBaseOwner(settleOrders.getOwnerOrder());
        Integer rentAmt=settleOrders.getRenterOrderCost();
        //代管车服务费比例 商品
        Double proxyProportionDou= ownerGoodsDetail.getServiceRate();
        if(proxyProportionDou==null){
            proxyProportionDou = Double.valueOf(0.0);
        }
        Integer proxyProportion = proxyProportionDou.intValue();
        OwnerOrderPurchaseDetailEntity proxyExpense = ownerOrderCostCombineService.getProxyExpense(costBaseDTO,rentAmt,proxyProportion);
        //2 车主端平台服务费
        //服务费比例 商品
        Double serviceRate = ownerGoodsDetail.getServiceRate();
        if(serviceRate==null){
            serviceRate = Double.valueOf(0.0);
        }
        Integer serviceProportion = serviceRate.intValue();
        OwnerOrderPurchaseDetailEntity serviceExpense = ownerOrderCostCombineService.getServiceExpense(costBaseDTO,rentAmt,serviceProportion);
        //3 获取车主补贴明细列表
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        //4 获取车主费用列表
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        //5 获取车主增值服务费用列表
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        // 6 获取gps服务费
        //车辆安装gps序列号列表 商品系统
        List<Integer> lsGpsSerialNumber = getLsGpsSerialNumber(ownerGoodsDetail.getGpsSerialNumber());
        List<OwnerOrderPurchaseDetailEntity> gpsCost =  ownerOrderCostCombineService.getGpsServiceAmtEntity(costBaseDTO,lsGpsSerialNumber);
        //7 获取车主油费 //超里程
        DeliveryOilCostVO deliveryOilCostVO = deliveryCarInfoPriceService.getOilCostByRenterOrderNo(settleOrders.getOrderNo(),ownerGoodsDetail.getCarEngineType());
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = Objects.isNull(deliveryOilCostVO)?null:deliveryOilCostVO.getOwnerGetAndReturnCarDTO();

        //8 管理后台补贴 （租客车主共用表 ，会员号区分车主/租客）
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(settleOrders.getOrderNo(),settleOrders.getOwnerMemNo());
        //9 获取全局的车主订单罚金明细（租客车主共用表 ，会员号区分车主/租客）
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailEntitys = consoleOwnerOrderFineDeatailService.selectByOrderNo(settleOrders.getOrderNo());
        //10 车主罚金
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOrderNo(settleOrders.getOrderNo());

        ownerCosts.setProxyExpense(proxyExpense);
        ownerCosts.setServiceExpense(serviceExpense);
        ownerCosts.setOwnerOrderSubsidyDetail(ownerOrderSubsidyDetail);
        ownerCosts.setOwnerOrderPurchaseDetail(ownerOrderPurchaseDetail);
        ownerCosts.setOwnerOrderIncrementDetail(ownerOrderIncrementDetail);
        ownerCosts.setGpsCost(gpsCost);
        ownerCosts.setOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDTO);
        ownerCosts.setOrderConsoleSubsidyDetails(orderConsoleSubsidyDetails);
        ownerCosts.setConsoleOwnerOrderFineDeatailEntitys(consoleOwnerOrderFineDeatailEntitys);
        ownerCosts.setOwnerOrderFineDeatails(ownerOrderFineDeatails);
        settleOrders.setOwnerCosts(ownerCosts);
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
            if(!StringUtil.isBlank(gpsSerialNumber)){
                list.add(Integer.valueOf(gpsSerialNumber));
            }
        }
        return list;
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
     *  租客返回基本信息
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
     * 计算费用统计
     * @param settleOrders
     * @return
     */
    public SettleOrdersDefinition settleOrdersDefinition(SettleOrders settleOrders) {
        SettleOrdersDefinition settleOrdersDefinition = new SettleOrdersDefinition();
        //1统计 租客结算费用明细， 补贴，费用总额
        handleRentAndPlatform(settleOrdersDefinition,settleOrders);
        //2统计 车主结算费用明细， 补贴，费用总额
        handleOwnerAndPlatform(settleOrdersDefinition,settleOrders);
        //3统计 计算总费用
        countCost(settleOrdersDefinition);
        return settleOrdersDefinition;
    }

    /**
     * 根据流水记录总账
     * @param settleOrdersDefinition
     */
    private void countCost(SettleOrdersDefinition settleOrdersDefinition) {
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = settleOrdersDefinition.getAccountRenterCostSettleDetails();
        //1租客总账
        if(!CollectionUtils.isEmpty(accountRenterCostSettleDetails)){
            int rentCostAmt = accountRenterCostSettleDetails.stream().filter(obj ->{return obj.getAmt()<0;}).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            settleOrdersDefinition.setRentCostAmt(rentCostAmt);
            int rentSubsidyAmt = accountRenterCostSettleDetails.stream().filter(obj ->{return obj.getAmt()>0;}).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            settleOrdersDefinition.setRentSubsidyAmt(rentSubsidyAmt);
        }
        //2车主总账
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = settleOrdersDefinition.getAccountOwnerCostSettleDetails();
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
            int ownerCostAmtFinal = accountOwnerCostSettleDetails.stream().mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            settleOrdersDefinition.setOwnerCostAmtFinal(ownerCostAmtFinal);
            int ownerCostAmt = accountOwnerCostSettleDetails.stream().filter(obj ->{return obj.getAmt()>0;}).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            settleOrdersDefinition.setOwnerCostAmt(ownerCostAmt);
            int ownerSubsidyAmt = accountOwnerCostSettleDetails.stream().filter(obj ->{return obj.getAmt()<0;}).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            settleOrdersDefinition.setOwnerSubsidyAmt(ownerSubsidyAmt);
        }
        //3 平台收益总账
        List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails = settleOrdersDefinition.getAccountPlatformProfitDetails();
        if(!CollectionUtils.isEmpty(accountPlatformProfitDetails)){
            int platformProfitAmt = accountPlatformProfitDetails.stream().mapToInt(AccountPlatformProfitDetailEntity::getAmt).sum();
            settleOrdersDefinition.setPlatformProfitAmt(platformProfitAmt);
        }
        //4 平台补贴总额
        List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetails = settleOrdersDefinition.getAccountPlatformSubsidyDetails();
        if(!CollectionUtils.isEmpty(accountPlatformSubsidyDetails)){
            int platformSubsidyAmt = accountPlatformSubsidyDetails.stream().mapToInt(AccountPlatformSubsidyDetailEntity::getAmt).sum();
            settleOrdersDefinition.setPlatformSubsidyAmt(platformSubsidyAmt);
        }
    }

    /**
     * 统计 车主结算费用明细， 补贴，费用总额
     * @param settleOrdersDefinition
     * @param settleOrders
     */
    private void handleOwnerAndPlatform(SettleOrdersDefinition settleOrdersDefinition, SettleOrders settleOrders) {
        OwnerCosts ownerCosts = settleOrders.getOwnerCosts();
        //1车主费用明细
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = new ArrayList<>();
        if(Objects.nonNull(ownerCosts)){
           // 1.1 车主端代管车服务费
            OwnerOrderPurchaseDetailEntity proxyExpense = ownerCosts.getProxyExpense();
            if(Objects.nonNull(proxyExpense) && Objects.nonNull(proxyExpense.getTotalAmount()) && proxyExpense.getTotalAmount()!=0){
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(proxyExpense,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_PROXY_EXPENSE_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_PROXY_EXPENSE_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(proxyExpense.getId()));
                int amt = Objects.isNull(proxyExpense.getTotalAmount())?0:proxyExpense.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
                // 车主端代管车服务费车主端代管车服务费 费用平台端冲账
                orderSettleNewService.addProxyExpenseAmtToPlatform(proxyExpense,settleOrdersDefinition);
            }
        }
        // 1.2 车主端平台服务费
        OwnerOrderPurchaseDetailEntity serviceExpense = ownerCosts.getServiceExpense();
        if(Objects.nonNull(serviceExpense) && Objects.nonNull(serviceExpense.getTotalAmount()) && serviceExpense.getTotalAmount()!=0){
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
            BeanUtils.copyProperties(serviceExpense,accountOwnerCostSettleDetail);
            accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_SERVICE_EXPENSE_COST.getCashNo());
            accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_SERVICE_EXPENSE_COST.getTxt());
            accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(serviceExpense.getId()));
            int amt = Objects.isNull(serviceExpense.getTotalAmount())?0:serviceExpense.getTotalAmount();
            accountOwnerCostSettleDetail.setAmt(amt);
            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            // 车主端平台服务费 费用平台端冲账
            orderSettleNewService.addServiceExpenseAmtToPlatform(serviceExpense,settleOrdersDefinition);
        }
        // 1.3 获取车主补贴明细列表
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetails = ownerCosts.getOwnerOrderSubsidyDetail();
        if(!CollectionUtils.isEmpty(ownerOrderSubsidyDetails)){
            for(int i=0; i<ownerOrderSubsidyDetails.size();i++){
                OwnerOrderSubsidyDetailEntity renterOrderCostDetail = ownerOrderSubsidyDetails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int subsidyAmount = Objects.isNull(renterOrderCostDetail.getSubsidyAmount())?0:renterOrderCostDetail.getSubsidyAmount();
                accountOwnerCostSettleDetail.setAmt(subsidyAmount);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,entity);
                    entity.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getCashNo());
                    entity.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
            }
        }
        //1.4 获取车主费用列表
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = ownerCosts.getOwnerOrderPurchaseDetail();
        if(!CollectionUtils.isEmpty(ownerOrderPurchaseDetail)){
            for(int i=0; i<ownerOrderPurchaseDetail.size();i++){
                OwnerOrderPurchaseDetailEntity renterOrderCostDetail = ownerOrderPurchaseDetail.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_DEBT.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_DEBT.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            }
        }
        //1.5获取车主增值服务费用列表
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = ownerCosts.getOwnerOrderIncrementDetail();
        if(!CollectionUtils.isEmpty(ownerOrderIncrementDetail)){
            for(int i=0; i<ownerOrderIncrementDetail.size();i++){
                OwnerOrderIncrementDetailEntity renterOrderCostDetail = ownerOrderIncrementDetail.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
                // 获取车主增值服务费用列表 费用平台端冲账
                orderSettleNewService.addOwnerOrderIncrementAmtToPlatform(renterOrderCostDetail,settleOrdersDefinition);
            }
        }
        //1.6 获取gps服务费
        List<OwnerOrderPurchaseDetailEntity> gpsCost = ownerCosts.getGpsCost();
        if(!CollectionUtils.isEmpty(gpsCost)){
            for(int i=0; i<gpsCost.size();i++){
                OwnerOrderPurchaseDetailEntity renterOrderCostDetail = gpsCost.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
                // 获取gps服务费 费用平台端冲账
                orderSettleNewService.addGpsCostAmtToPlatform(renterOrderCostDetail,settleOrdersDefinition);
            }
        }
        //1.7 获取车主油费
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = ownerCosts.getOwnerGetAndReturnCarDTO();
        if(Objects.nonNull(ownerGetAndReturnCarDTO) && !StringUtil.isBlank(ownerGetAndReturnCarDTO.getCarOilDifferenceCrash())){
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
            BeanUtils.copyProperties(ownerGetAndReturnCarDTO,accountOwnerCostSettleDetail);
            accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getCashNo());
            accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getTxt());
            String carOilDifferenceCrash = ownerGetAndReturnCarDTO.getCarOilDifferenceCrash();
            accountOwnerCostSettleDetail.setAmt(Integer.valueOf(carOilDifferenceCrash));
            accountOwnerCostSettleDetail.setMemNo(settleOrders.getOwnerMemNo());
            accountOwnerCostSettleDetail.setOrderNo(settleOrders.getOrderNo());
            accountOwnerCostSettleDetail.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
        }
        //1.8 管理后台补贴
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = ownerCosts.getOrderConsoleSubsidyDetails();
        if(!CollectionUtils.isEmpty(orderConsoleSubsidyDetails)){
            for(int i=0; i<orderConsoleSubsidyDetails.size();i++){
                OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetail = orderConsoleSubsidyDetails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(orderConsoleSubsidyDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                int subsidyAmount = Objects.isNull(orderConsoleSubsidyDetail.getSubsidyAmount())?0:orderConsoleSubsidyDetail.getSubsidyAmount();
                accountOwnerCostSettleDetail.setAmt(subsidyAmount);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetail.getSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(orderConsoleSubsidyDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    entity.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetail.getSubsidyTargetCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(orderConsoleSubsidyDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    entity.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
            }
        }
        //1.9 全局的车主订单罚金明细
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailEntitys = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys();
        if(!CollectionUtils.isEmpty(consoleOwnerOrderFineDeatailEntitys)){
            for(int i=0; i<consoleOwnerOrderFineDeatailEntitys.size();i++){
                ConsoleOwnerOrderFineDeatailEntity consoleOwnerOrderFineDeatailEntity = consoleOwnerOrderFineDeatailEntitys.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(consoleOwnerOrderFineDeatailEntity,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                int fineAmount = Objects.isNull(consoleOwnerOrderFineDeatailEntity.getFineAmount())?0:consoleOwnerOrderFineDeatailEntity.getFineAmount();
                accountOwnerCostSettleDetail.setAmt(fineAmount);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(consoleOwnerOrderFineDeatailEntity.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                //罚金补贴方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(consoleOwnerOrderFineDeatailEntity.getFineSubsidyCode())){
                    AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(consoleOwnerOrderFineDeatailEntity,entity);
                    entity.setSourceCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    entity.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(consoleOwnerOrderFineDeatailEntity.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(consoleOwnerOrderFineDeatailEntity.getFineSubsidySourceCode())){
                    AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(consoleOwnerOrderFineDeatailEntity,entity);
                    entity.setSourceCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    entity.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(consoleOwnerOrderFineDeatailEntity.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
            }
        }
        //1.10 查询车主罚金
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails = ownerCosts.getOwnerOrderFineDeatails();
        if(CollectionUtils.isEmpty(ownerOrderFineDeatails)){
            for(int i=0; i<ownerOrderFineDeatails.size();i++){
                OwnerOrderFineDeatailEntity ownerOrderFineDeatail = ownerOrderFineDeatails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(ownerOrderFineDeatail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                int fineAmount = Objects.isNull(ownerOrderFineDeatail.getFineAmount())?0:ownerOrderFineDeatail.getFineAmount();

                accountOwnerCostSettleDetail.setAmt(fineAmount);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(ownerOrderFineDeatail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

//                //罚金来源方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(ownerOrderFineDeatail.getFineSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(ownerOrderFineDeatail,entity);
                    entity.setSourceCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    entity.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(ownerOrderFineDeatail.getId()));
                    entity.setAmt(-fineAmount);
                    entity.setUniqueNo(String.valueOf(ownerOrderFineDeatail.getId()));
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
                //罚金补贴方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(ownerOrderFineDeatail.getFineSubsidyCode())){
                    AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(ownerOrderFineDeatail,entity);
                    entity.setSourceCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    entity.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(ownerOrderFineDeatail.getId()));
                    entity.setAmt(-fineAmount);
                    settleOrdersDefinition.addPlatformProfit(entity);
                }
            }
        }

        settleOrdersDefinition.setAccountOwnerCostSettleDetails(accountOwnerCostSettleDetails);
    }

    /**
     *  1统计 租客结算费用明细， 补贴，费用总额
     * @param settleOrdersDefinition
     * @param settleOrders
     */
    private void handleRentAndPlatform(SettleOrdersDefinition settleOrdersDefinition, SettleOrders settleOrders) {
        //1 租客费用明细 整理
        RentCosts rentCosts = settleOrders.getRentCosts();
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = settleOrdersDefinition.getAccountRenterCostSettleDetails();
        accountRenterCostSettleDetails = CollectionUtils.isEmpty(accountRenterCostSettleDetails)?new ArrayList<>():accountRenterCostSettleDetails;

        if(Objects.nonNull(rentCosts)){
            //1.1 查询租车费用
            List<RenterOrderCostDetailEntity> renterOrderCostDetails = rentCosts.getRenterOrderCostDetails();
            if(!CollectionUtils.isEmpty(renterOrderCostDetails)){
                for(int i=0; i<renterOrderCostDetails.size();i++){
                    RenterOrderCostDetailEntity renterOrderCostDetail = renterOrderCostDetails.get(i);
                    AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,accountRenterCostSettleDetail);
                    accountRenterCostSettleDetail.setCostCode(renterOrderCostDetail.getCostCode());
                    accountRenterCostSettleDetail.setCostDetail(renterOrderCostDetail.getCostDesc());
                    accountRenterCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    accountRenterCostSettleDetail.setAmt(renterOrderCostDetail.getTotalAmount());
                    accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
                    // 租车费用
                    orderSettleNewService.addRentCostToPlatformAndOwner(renterOrderCostDetail,settleOrdersDefinition);
                }
            }
            //1.2 交接车-油费
            RenterGetAndReturnCarDTO oilAmt = rentCosts.getOilAmt();
            if(Objects.nonNull(oilAmt) && !StringUtil.isBlank(oilAmt.getOilDifferenceCrash())){
                AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                BeanUtils.copyProperties(oilAmt,accountRenterCostSettleDetail);
                accountRenterCostSettleDetail.setCostCode(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_OIL_COST.getCashNo());
                accountRenterCostSettleDetail.setCostDetail(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_OIL_COST.getTxt());
                String oilDifferenceCrash = oilAmt.getOilDifferenceCrash();
                oilDifferenceCrash = StringUtil.isBlank(oilDifferenceCrash)?"0":oilDifferenceCrash;
                accountRenterCostSettleDetail.setAmt(Integer.valueOf(oilDifferenceCrash));
                accountRenterCostSettleDetail.setMemNo(settleOrders.getRenterMemNo());
                accountRenterCostSettleDetail.setOrderNo(settleOrders.getOrderNo());
                accountRenterCostSettleDetail.setRenterOrderNo(settleOrders.getRenterOrderNo());
                accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
            }
            //1.3 交接车-获取超里程费用
            FeeResult mileageAmt = rentCosts.getMileageAmt();
            if(Objects.nonNull(mileageAmt) && Objects.nonNull(mileageAmt.getTotalFee())  && mileageAmt.getTotalFee()!=0){
                AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                BeanUtils.copyProperties(mileageAmt,accountRenterCostSettleDetail);
                accountRenterCostSettleDetail.setCostCode(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getCashNo());
                accountRenterCostSettleDetail.setCostDetail(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getTxt());
                accountRenterCostSettleDetail.setAmt(mileageAmt.getTotalFee());
                accountRenterCostSettleDetail.setOrderNo(settleOrders.getOrderNo());
                accountRenterCostSettleDetail.setRenterOrderNo(settleOrders.getRenterOrderNo());
                accountRenterCostSettleDetail.setMemNo(settleOrders.getRenterMemNo());
                accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
                //算平台收益
                orderSettleNewService.addRenterGetAndReturnCarAmtToPlatform(accountRenterCostSettleDetail,settleOrdersDefinition);
            }
            //1.4 补贴
            List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails = rentCosts.getRenterOrderSubsidyDetails();
            if(!CollectionUtils.isEmpty(renterOrderSubsidyDetails)) {
                for (int i = 0; i < renterOrderSubsidyDetails.size(); i++) {
                    RenterOrderSubsidyDetailEntity renterOrderCostDetail = renterOrderSubsidyDetails.get(i);
                    AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,accountRenterCostSettleDetail);
                    accountRenterCostSettleDetail.setCostCode(RenterCashCodeEnum.ACCOUNT_RENTER_SUBSIDY_COST.getCashNo());
                    accountRenterCostSettleDetail.setCostDetail(RenterCashCodeEnum.ACCOUNT_RENTER_SUBSIDY_COST.getTxt());
                    accountRenterCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    accountRenterCostSettleDetail.setAmt(renterOrderCostDetail.getSubsidyAmount());
                    accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
                    // 平台补贴 记录补贴
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getSubsidyTargetCode())){
                        AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_SUBSIDY_COST.getCashNo());
                        entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_RENTER_SUBSIDY_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-renterOrderCostDetail.getSubsidyAmount());
                        entity.setSubsidyName(SubsidySourceCodeEnum.RENTER.getDesc());
                        settleOrdersDefinition.addPlatformSubsidy(entity);
                    }
                    // 平台补贴 记录补贴
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getSubsidySourceCode())){
                        AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_SUBSIDY_COST.getCashNo());
                        entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_RENTER_SUBSIDY_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-renterOrderCostDetail.getSubsidyAmount());
                        entity.setSubsidyName(SubsidySourceCodeEnum.RENTER.getDesc());
                        settleOrdersDefinition.addPlatformSubsidy(entity);
                    }
                }
            }
            //1.5租客罚金
            List<RenterOrderFineDeatailEntity> renterOrderFineDeatails = rentCosts.getRenterOrderFineDeatails();
            if(!CollectionUtils.isEmpty(renterOrderFineDeatails)) {
                for (int i = 0; i < renterOrderFineDeatails.size(); i++) {
                    RenterOrderFineDeatailEntity renterOrderCostDetail = renterOrderFineDeatails.get(i);
                    AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,accountRenterCostSettleDetail);
                    accountRenterCostSettleDetail.setCostCode(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getCashNo());
                    accountRenterCostSettleDetail.setCostDetail(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getTxt());
                    accountRenterCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    accountRenterCostSettleDetail.setAmt(renterOrderCostDetail.getFineAmount());
                    accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
                    //罚金来源方 是平台
                    int fineAmount = renterOrderCostDetail.getFineAmount();
                    //罚金补贴方 是平台
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getFineSubsidyCode())){
                        AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getCashNo());
                        entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-fineAmount);
                        settleOrdersDefinition.addPlatformProfit(entity);
                    }

                    //罚金补贴方 是平台
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getFineSubsidySourceCode())){
                        AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getCashNo());
                        entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-fineAmount);
                        settleOrdersDefinition.addPlatformProfit(entity);
                    }
                }
            }
            //1.6 管理后台补贴
            List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = rentCosts.getOrderConsoleSubsidyDetails();
            if(!CollectionUtils.isEmpty(orderConsoleSubsidyDetails)) {
                for (int i = 0; i < orderConsoleSubsidyDetails.size(); i++) {
                    OrderConsoleSubsidyDetailEntity renterOrderCostDetail = orderConsoleSubsidyDetails.get(i);
                    AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,accountRenterCostSettleDetail);
                    accountRenterCostSettleDetail.setCostCode(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo());
                    accountRenterCostSettleDetail.setCostDetail(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getTxt());
                    accountRenterCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    accountRenterCostSettleDetail.setAmt(renterOrderCostDetail.getSubsidyAmount());
                    accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
                    // 平台补贴 记录补贴
                    int subsidyAmount = renterOrderCostDetail.getSubsidyAmount();
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getSubsidySourceCode())){
                        AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo());
                        entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-subsidyAmount);
                        entity.setSubsidyName(SubsidySourceCodeEnum.RENTER.getDesc());
                        settleOrdersDefinition.addPlatformSubsidy(entity);
                    }
                }
            }
            //1.7 获取全局的租客订单罚金明细
            List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails = rentCosts.getConsoleRenterOrderFineDeatails();
            if(!CollectionUtils.isEmpty(consoleRenterOrderFineDeatails)) {
                for (int i = 0; i < consoleRenterOrderFineDeatails.size(); i++) {
                    ConsoleRenterOrderFineDeatailEntity renterOrderCostDetail = consoleRenterOrderFineDeatails.get(i);
                    AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,accountRenterCostSettleDetail);
                    accountRenterCostSettleDetail.setCostCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    accountRenterCostSettleDetail.setCostDetail(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    accountRenterCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    accountRenterCostSettleDetail.setAmt(renterOrderCostDetail.getFineAmount());
                    accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);

                    //罚金来源方 是平台
                    int fineAmount = renterOrderCostDetail.getFineAmount();
                    //罚金补贴方 是平台
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getFineSubsidyCode())){
                        AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                        entity.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-fineAmount);
                        settleOrdersDefinition.addPlatformProfit(entity);
                    }
                    //罚金补贴方 是平台
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getFineSubsidySourceCode())){
                        AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                        entity.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-fineAmount);
                        settleOrdersDefinition.addPlatformProfit(entity);
                    }
                }
            }
        }
        settleOrdersDefinition.setAccountRenterCostSettleDetails(accountRenterCostSettleDetails);
    }

    /**
     * 结算明细记录落库
     * @param settleOrdersDefinition
     */
    public void insertSettleOrders(SettleOrdersDefinition settleOrdersDefinition) {
        //2 明细落库
        //2.1 租客端 明细落库
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = settleOrdersDefinition.getAccountRenterCostSettleDetails();
        cashierSettleService.insertAccountRenterCostSettleDetails(accountRenterCostSettleDetails);
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

    /**
     * 租客费用结余 处理 （如果应付 大于实付，这个订单存在未支付信息，优先 押金抵扣，未支付信息）
     * @param settleOrdersAccount
     */
    public void rentCostSettle(SettleOrders settleOrders , SettleOrdersAccount settleOrdersAccount) {
        //1 如果租车费用计算应付总额大于 实际支付 车辆押金抵扣租车费用欠款
        if(settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmt()<0){
            //1.1押金 抵扣 租车费用欠款
            if(settleOrdersAccount.getDepositAmt()>0){
                DeductDepositToRentCostReqVO vo = new DeductDepositToRentCostReqVO();
                BeanUtils.copyProperties(settleOrders,vo);
                int debtAmt = settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmt();
                //真实抵扣金额
                int amt = debtAmt+settleOrdersAccount.getDepositSurplusAmt()>=0?debtAmt:-settleOrdersAccount.getDepositSurplusAmt();
                vo.setAmt(amt);
                //车俩押金抵扣 租车费用金额 返回 已抵扣部分
                cashierSettleService.deductDepositToRentCost(vo);
                //计算剩余车俩押金
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() + amt);
                // 实付费用加上 押金已抵扣部分
                settleOrdersAccount.setRentCostPayAmt(settleOrdersAccount.getRentCostPayAmt() + Math.abs(amt));

            }
        }
        //2如果 步骤1 结算 应付还是大于实付  此订单产生历史欠款
        if(settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmt()<0){
            //2.1 记录历史欠款
            int amt = settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmt();
            AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
            BeanUtils.copyProperties(settleOrders,accountInsertDebt);
            accountInsertDebt.setType(DebtTypeEnum.SETTLE.getCode());
            accountInsertDebt.setAmt(amt);
            accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            int debtDetailId = cashierService.createDebt(accountInsertDebt);
            //2.2记录结算费用状态
            AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrders,entity);
            entity.setAmt(-amt);
            entity.setCostCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            entity.setCostDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(debtDetailId));
            cashierSettleService.insertAccountRenterCostSettleDetail(entity);
            // 实付费用加上 历史欠款转移部分，存在欠款 1走历史欠款，2当前订单 账户拉平
            settleOrdersAccount.setRentCostPayAmt(settleOrdersAccount.getRentCostPayAmt() + Math.abs(amt));
        }
    }

    /**
     * 结算租客 还历史欠款
     * @param settleOrdersAccount
     */
    public void repayHistoryDebtRent(SettleOrdersAccount settleOrdersAccount) {
        // 1 存在 实付大于应付 先抵扣 历史欠款
        if(settleOrdersAccount.getRentCostSurplusAmt()>0){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierDeductDebtReq);
            cashierDeductDebtReq.setAmt(settleOrdersAccount.getRentCostSurplusAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_COST_TO_HISTORY_AMT);
            cashierDeductDebtReq.setMemNo(settleOrdersAccount.getRenterMemNo());
            CashierDeductDebtResVO result = cashierService.deductDebtByRentCost(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余租车费用
                settleOrdersAccount.setRentCostSurplusAmt(settleOrdersAccount.getRentCostSurplusAmt() - deductAmt);
            }
        }
        //车辆押金存在 且 租车费用没有抵扣完 ，使用车辆押金抵扣 历史欠款
        if(settleOrdersAccount.getDepositSurplusAmt()>0){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierDeductDebtReq);
            cashierDeductDebtReq.setAmt(settleOrdersAccount.getDepositSurplusAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT);
            CashierDeductDebtResVO result = cashierService.deductDebt(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余车俩押金
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() - deductAmt);
            }
        }
    }

    /**
     * 退还多余费用
     * 退还优先级 凹凸币<钱包<消费
     *
     * @param settleOrdersAccount
     */
    public void refundRentCost(SettleOrdersAccount settleOrdersAccount,List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails,OrderStatusDTO orderStatusDTO) {
        List<AccountRenterCostSettleDetailEntity> renterCostSettleDetails = new ArrayList<>();
        //应退结余 租车费用
        int rentCostSurplusAmt = settleOrdersAccount.getRentCostSurplusAmt();
        if(rentCostSurplusAmt>0){
            //退还租车费用
            if(rentCostSurplusAmt>0){
                // 实付
                int rentCostPayAmtFinal = settleOrdersAccount.getRentCostPayAmt();
                int returnAmt = rentCostSurplusAmt>rentCostPayAmtFinal?rentCostPayAmtFinal:-rentCostSurplusAmt;
                //退还剩余 租车费用
                CashierRefundApplyReqVO cashierRefundApplyReq = getCashierRefundApplyReqVO(settleOrdersAccount,-returnAmt);
                int id = cashierService.refundRentCost(cashierRefundApplyReq);
                AccountRenterCostSettleDetailEntity entity = getAccountRenterCostSettleDetailEntityForRentCost(settleOrdersAccount,-returnAmt,id);
                renterCostSettleDetails.add(entity);
                rentCostSurplusAmt = rentCostSurplusAmt-returnAmt;
                orderStatusDTO.setRentCarRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());
            }
            //2 查询钱包 比较
            if(rentCostSurplusAmt>0){
                int walletAmt = cashierSettleService.getRentCostPayByWallet(settleOrdersAccount.getOrderNo(),settleOrdersAccount.getRenterMemNo());
                //计算应退钱包金额 并退还
                int returnWalletAmt = rentCostSurplusAmt>walletAmt?walletAmt:rentCostSurplusAmt;
                walletProxyService.returnOrChargeWallet(settleOrdersAccount.getRenterMemNo(),settleOrdersAccount.getOrderNo(),Math.abs(returnWalletAmt));
                AccountRenterCostSettleDetailEntity entity = getAccountRenterCostSettleDetailEntityForWallet(settleOrdersAccount,-returnWalletAmt);
                renterCostSettleDetails.add(entity);

            }
            //记录 凹凸币/钱包退还 流水
            cashierSettleService.insertAccountRenterCostSettleDetails(renterCostSettleDetails);
        }
        //1 退还凹凸币 coinAmt为订单真实使用的凹凸币
        int coinAmt = accountRenterCostSettleDetails.stream().filter(obj ->{
            return RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(obj.getCostCode());
        }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
        if(coinAmt>0){
            //退还多余凹凸币
            accountRenterCostCoinService.settleAutoCoin(settleOrdersAccount.getRenterMemNo(),settleOrdersAccount.getOrderNo(),coinAmt);
        }

    }
    /**
     * 结算退还租车费用金额 费用明细
     * @param settleOrdersAccount
     * @param rentCostSurplusAmt
     * @return
     */
    private AccountRenterCostSettleDetailEntity getAccountRenterCostSettleDetailEntityForRentCost(SettleOrdersAccount settleOrdersAccount, int rentCostSurplusAmt, int id) {
        AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(settleOrdersAccount,entity);
        entity.setAmt(rentCostSurplusAmt);
        entity.setCostCode(RenterCashCodeEnum.SETTLE_RENT_COST_TO_RETURN_AMT.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.SETTLE_RENT_COST_TO_RETURN_AMT.getTxt());
        entity.setUniqueNo(String.valueOf(id));
        return entity;
    }

    /**
     * 租车费用退还
     * @param settleOrdersAccount
     * @param rentCostSurplusAmt
     * @return
     */
    private CashierRefundApplyReqVO getCashierRefundApplyReqVO(SettleOrdersAccount settleOrdersAccount, int rentCostSurplusAmt) {
        CashierRefundApplyReqVO vo = new CashierRefundApplyReqVO();
        BeanUtils.copyProperties(settleOrdersAccount,vo);
        vo.setAmt(rentCostSurplusAmt);
        vo.setRemake("结算退还");
        vo.setMemNo(settleOrdersAccount.getRenterMemNo());
        vo.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_COST_TO_RETURN_AMT);
        return vo;
    }

    /**
     * 结算退还钱包金额 费用明细
     * @param settleOrdersAccount
     * @param returnWalletAmt
     * @return
     */
    private AccountRenterCostSettleDetailEntity getAccountRenterCostSettleDetailEntityForWallet(SettleOrdersAccount settleOrdersAccount, int returnWalletAmt) {
        AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(settleOrdersAccount,entity);
        entity.setAmt(returnWalletAmt);
        entity.setCostCode(RenterCashCodeEnum.WALLET_DEDUCT.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.WALLET_DEDUCT.getTxt());
        return entity;
    }


    /**
     * 退还剩余的 车辆押金
     * @param settleOrdersAccount
     */
    public void refundDepositAmt(SettleOrdersAccount settleOrdersAccount,OrderStatusDTO orderStatusDTO) {
        if(settleOrdersAccount.getDepositSurplusAmt()>0){
            //1退还租车押金
            CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierRefundApply);
            cashierRefundApply.setAmt(-settleOrdersAccount.getDepositSurplusAmt());
            cashierRefundApply.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT);
            cashierRefundApply.setRemake(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT.getTxt());
            cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getCashNo());
            cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
            int id =cashierService.refundDeposit(cashierRefundApply);
            // 2记录退还 租车押金 结算费用明细
            AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrdersAccount,entity);
            entity.setAmt(-settleOrdersAccount.getDepositSurplusAmt());
            entity.setCostCode(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT.getCashNo());
            entity.setCostDetail(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(id));
            cashierSettleService.insertAccountRenterCostSettleDetail(entity);
            orderStatusDTO.setDepositRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());

        }

    }

    /**
     * 车主收益 结余处理 历史欠款
     * @param settleOrdersAccount
     */
    public void repayHistoryDebtOwner(SettleOrdersAccount settleOrdersAccount) {
        //车主收益大于0 先还历史欠款
        if(settleOrdersAccount.getOwnerCostSurplusAmt()>0){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierDeductDebtReq);
            cashierDeductDebtReq.setAmt(settleOrdersAccount.getOwnerCostSurplusAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_OWNER_INCOME_TO_HISTORY_AMT);
            CashierDeductDebtResVO result = cashierService.deductDebtByOwnerIncome(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 最终车主收益总金额
                settleOrdersAccount.setOwnerCostSurplusAmt(settleOrdersAccount.getOwnerCostSurplusAmt() - deductAmt);
            }
        }
    }

    /**
     * 结算 产生 车主待审核记录
     * @param settleOrdersAccount
     */
    public void insertOwnerIncomeExamine(SettleOrdersAccount settleOrdersAccount) {
        if(settleOrdersAccount.getOwnerCostSurplusAmt()>0){
            AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamine = new AccountOwnerIncomeExamineReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,accountOwnerIncomeExamine);
            accountOwnerIncomeExamine.setAmt(settleOrdersAccount.getOwnerCostSurplusAmt());
            accountOwnerIncomeExamine.setMemNo(settleOrdersAccount.getOwnerMemNo());
            cashierService.insertOwnerIncomeExamine(accountOwnerIncomeExamine);
        }

    }

    /**
     * 车辆结算成功 更新订单状态
     * @param settleOrdersAccount
     */
    public void saveOrderStatusInfo(SettleOrdersAccount settleOrdersAccount) {
        //1更新 订单流转状态
        orderStatusService.saveOrderStatusInfo(settleOrdersAccount.getOrderStatusDTO());
        //2记录订单流传信息
        orderFlowService.inserOrderStatusChangeProcessInfo(settleOrdersAccount.getOrderNo(), OrderStatusEnum.TO_WZ_SETTLE);
    }


    /**
     * 取消订单结算 数据初始化
     * @param orderNo
     * @return
     */
    public SettleOrders initCancelSettleOrders(String orderNo) {
        //1 校验参数
        if(StringUtil.isBlank(orderNo)){
            throw new OrderSettleFlatAccountException();
        }
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())){
            throw new OrderSettleFlatAccountException();
        }
        OwnerOrderEntity ownerOrder = ownerOrderService.queryCancelOwnerOrderByOrderNoIsEffective(orderNo);
        // 3 初始化数据
        // 3.1获取租客子订单 和 租客会员号
        String renterOrderNo = renterOrder.getRenterOrderNo();
        String renterMemNo = renterOrder.getRenterMemNo();
        //3.2获取车主子订单 和 车主会员号
        String ownerOrderNo = Objects.nonNull(ownerOrder)?ownerOrder.getOwnerOrderNo():"";
        String ownerMemNo = Objects.nonNull(ownerOrder)?ownerOrder.getMemNo():"";

        SettleOrders settleOrders = new SettleOrders();
        settleOrders.setOrderNo(orderNo);
        settleOrders.setRenterOrderNo(renterOrderNo);
        settleOrders.setOwnerOrderNo(ownerOrderNo);
        settleOrders.setRenterMemNo(renterMemNo);
        settleOrders.setOwnerMemNo(ownerMemNo);
        settleOrders.setRenterOrder(renterOrder);
        settleOrders.setOwnerOrder(ownerOrder);
        return settleOrders;
    }

    /**
     * 取消订单查询 获取租客罚金信息
     * @param settleOrders
     */
    public void getCancelRenterCostSettleDetail(SettleOrders settleOrders) {
        //1 租客罚金
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatails = renterOrderFineDeatailService.listRenterOrderFineDeatail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        //2 获取全局的租客订单罚金明细（租客车主共用表 ，会员号区分车主/租客）
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        //3 补贴包含凹凸币信息
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        RentCosts rentCosts = new RentCosts();
        rentCosts.setRenterOrderSubsidyDetails(renterOrderSubsidyDetails);
        rentCosts.setRenterOrderFineDeatails(renterOrderFineDeatails);
        rentCosts.setConsoleRenterOrderFineDeatails(consoleRenterOrderFineDeatails);
        settleOrders.setRentCosts(rentCosts);
    }

    /**
     * 查询所有车主罚金明细
     * @param settleOrders
     */
    public void getCancelOwnerCostSettleDetail(SettleOrders settleOrders) {
        OwnerCosts ownerCosts = new OwnerCosts();
        //1 获取全局的车主订单罚金明细（租客车主共用表 ，会员号区分车主/租客）
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailEntitys = consoleOwnerOrderFineDeatailService.selectByOrderNo(settleOrders.getOrderNo());

        //2 车主罚金
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOrderNo(settleOrders.getOrderNo());
        ownerCosts.setOwnerOrderFineDeatails(ownerOrderFineDeatails);
        ownerCosts.setConsoleOwnerOrderFineDeatailEntitys(consoleOwnerOrderFineDeatailEntitys);
        settleOrders.setOwnerCosts(ownerCosts);
    }

    /**
     * 初始化 取消订单结算信息
     * @param settleOrders
     * @return
     */
    public SettleCancelOrdersAccount initSettleCancelOrdersAccount(SettleOrders settleOrders) {
        SettleCancelOrdersAccount settleCancelOrdersAccount = new SettleCancelOrdersAccount();
        // 实付车俩押金金额
        int rentDepositAmt = cashierSettleService.getRentDeposit(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        // 实付钱包金额
        int rentWalletAmt = cashierSettleService.getRentCostPayByWallet(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        // 实付违章押金金额
        int rentWzDepositAmt = cashierSettleService.getSurplusWZDepositCostAmt(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        // 查询实付租车费用金额
        int rentCostAmt = cashierSettleService.getCostPaidRentRefundAmt(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        // 计算租客罚金
        int rentFineAmt =0;
        RentCosts rentCosts = settleOrders.getRentCosts();
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderFineDeatails())){
            int amt = rentCosts.getRenterOrderFineDeatails().stream().filter(obj ->{
                return SubsidySourceCodeEnum.RENTER.getCode().equals(obj.getFineSubsidySourceCode());
            }).mapToInt(RenterOrderFineDeatailEntity::getFineAmount).sum();
            rentFineAmt = rentFineAmt +amt;
        }
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getConsoleRenterOrderFineDeatails())){
            int amt = rentCosts.getConsoleRenterOrderFineDeatails().stream().filter(obj ->{
                return SubsidySourceCodeEnum.RENTER.getCode().equals(obj.getFineSubsidySourceCode());
            }).mapToInt(ConsoleRenterOrderFineDeatailEntity::getFineAmount).sum();
            rentFineAmt = rentFineAmt +amt;
        }

        // 计算凹凸币使用金额
        int renCoinAmt =0;
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderSubsidyDetails())){
            renCoinAmt = rentCosts.getRenterOrderSubsidyDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(obj.getSubsidyCostCode());
            }).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        //计算 车主罚金
        int ownerFineAmt = 0;
        OwnerCosts ownerCosts = settleOrders.getOwnerCosts();
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getOwnerOrderFineDeatails())){
            int amt = ownerCosts.getOwnerOrderFineDeatails().stream().filter(obj ->{
                return SubsidySourceCodeEnum.OWNER.getCode().equals(obj.getFineSubsidySourceCode());
            }).mapToInt(OwnerOrderFineDeatailEntity::getFineAmount).sum();
            ownerFineAmt = ownerFineAmt +amt;
        }
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getConsoleOwnerOrderFineDeatailEntitys())){
            int amt = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys().stream().filter(obj ->{
                return SubsidySourceCodeEnum.OWNER.getCode().equals(obj.getFineSubsidySourceCode());
            }).mapToInt(ConsoleOwnerOrderFineDeatailEntity::getFineAmount).sum();
            ownerFineAmt = ownerFineAmt +amt;
        }


        //租客收入
        int rentFineIncomeAmt = 0;
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderFineDeatails())){
            int amt = rentCosts.getRenterOrderFineDeatails().stream().filter(obj ->{
                return SubsidySourceCodeEnum.RENTER.getCode().equals(obj.getFineSubsidyCode());
            }).mapToInt(RenterOrderFineDeatailEntity::getFineAmount).sum();
            rentFineIncomeAmt = rentFineIncomeAmt +amt;
        }
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getConsoleRenterOrderFineDeatails())){
            int amt = rentCosts.getConsoleRenterOrderFineDeatails().stream().filter(obj ->{
                return SubsidySourceCodeEnum.RENTER.getCode().equals(obj.getFineSubsidyCode());
            }).mapToInt(ConsoleRenterOrderFineDeatailEntity::getFineAmount).sum();
            rentFineIncomeAmt = rentFineIncomeAmt +amt;
        }
        //车主收入罚金
        int ownerFineIncomeAmt = 0;
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getOwnerOrderFineDeatails())){
            int amt = ownerCosts.getOwnerOrderFineDeatails().stream().filter(obj ->{
                return SubsidySourceCodeEnum.OWNER.getCode().equals(obj.getFineSubsidyCode());
            }).mapToInt(OwnerOrderFineDeatailEntity::getFineAmount).sum();
            ownerFineIncomeAmt = ownerFineIncomeAmt +amt;
        }
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getConsoleOwnerOrderFineDeatailEntitys())){
            int amt = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys().stream().filter(obj ->{
                return SubsidySourceCodeEnum.OWNER.getCode().equals(obj.getFineSubsidyCode());
            }).mapToInt(ConsoleOwnerOrderFineDeatailEntity::getFineAmount).sum();
            ownerFineIncomeAmt = ownerFineIncomeAmt +amt;
        }
        //平台罚金收入
        int platformFineImconeAmt=0;
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getConsoleOwnerOrderFineDeatailEntitys())){
            int amt = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys().stream().filter(obj ->{
                return SubsidySourceCodeEnum.PLATFORM.getCode().equals(obj.getFineSubsidyCode());
            }).mapToInt(ConsoleOwnerOrderFineDeatailEntity::getFineAmount).sum();
            platformFineImconeAmt = platformFineImconeAmt +amt;
        }
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(rentCosts.getConsoleRenterOrderFineDeatails())){
            int amt = rentCosts.getConsoleRenterOrderFineDeatails().stream().filter(obj ->{
                return SubsidySourceCodeEnum.PLATFORM.getCode().equals(obj.getFineSubsidyCode());
            }).mapToInt(ConsoleRenterOrderFineDeatailEntity::getFineAmount).sum();
            platformFineImconeAmt = platformFineImconeAmt +amt;
        }

        settleCancelOrdersAccount.setOwnerFineAmt(ownerFineAmt);
        settleCancelOrdersAccount.setRentFineAmt(rentFineAmt);
        settleCancelOrdersAccount.setRentCostAmt(rentCostAmt);
        settleCancelOrdersAccount.setRentSurplusCostAmt(rentCostAmt);
        settleCancelOrdersAccount.setRentDepositAmt(rentDepositAmt);
        settleCancelOrdersAccount.setRentSurplusDepositAmt(rentDepositAmt);
        settleCancelOrdersAccount.setRentWzDepositAmt(rentWzDepositAmt);
        settleCancelOrdersAccount.setRentSurplusWzDepositAmt(rentWzDepositAmt);
        settleCancelOrdersAccount.setRenWalletAmt(rentWalletAmt);
        settleCancelOrdersAccount.setRentSurplusWalletAmt(rentWalletAmt);
        settleCancelOrdersAccount.setRenCoinAmt(renCoinAmt);
        settleCancelOrdersAccount.setOwnerFineIncomeAmt(ownerFineIncomeAmt);
        settleCancelOrdersAccount.setRentFineIncomeAmt(rentFineIncomeAmt);
        settleCancelOrdersAccount.setPlatformFineImconeAmt(platformFineImconeAmt);
        return settleCancelOrdersAccount;
    }

    /**
     * 车主存在 罚金 走历史欠款
     * @param settleCancelOrdersAccount
     */
    public void handleOwnerFine(SettleOrders settleOrders,SettleCancelOrdersAccount settleCancelOrdersAccount) {
        //1 车主存在 罚金走历史欠款
        if(settleCancelOrdersAccount.getOwnerFineAmt()<0){
            //2 记录历史欠款
            AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
            BeanUtils.copyProperties(settleOrders,accountInsertDebt);
            accountInsertDebt.setType(DebtTypeEnum.CANCEL.getCode());
            accountInsertDebt.setMemNo(settleOrders.getOwnerMemNo());
            accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            accountInsertDebt.setAmt(settleCancelOrdersAccount.getOwnerFineAmt());
            cashierService.createDebt(accountInsertDebt);
        }
    }

    /**
     租客存在罚金 （抵扣优先级 钱包》租车费用》车辆押金》违章押金）
     * @param settleOrders
     * @param settleCancelOrdersAccount
     */
    public void handleRentFine(SettleOrders settleOrders, SettleCancelOrdersAccount settleCancelOrdersAccount) {
        int rentCostAmt = settleCancelOrdersAccount.getRentCostAmt();
        int rentDepositAmt =  settleCancelOrdersAccount.getRentDepositAmt();
        int rentWzDepositAmt = settleCancelOrdersAccount.getRentWzDepositAmt();
        int renWalletAmt =  settleCancelOrdersAccount.getRenWalletAmt();

        //2 罚金处理
        int rentFineAmt = settleCancelOrdersAccount.getRentFineAmt();
        //2.1 钱包抵罚金
        if(renWalletAmt>0 && rentFineAmt<0){
            AccountRenterCostToFineReqVO vo = new AccountRenterCostToFineReqVO();
            BeanUtils.copyProperties(settleOrders,vo);
            vo.setMemNo(settleOrders.getRenterMemNo());
            int debtAmt = renWalletAmt + rentFineAmt;
            //计算抵扣金额
            int amt = debtAmt>=0?rentFineAmt:-renWalletAmt;
            vo.setAmt(amt);
            //钱包支付金额抵扣 罚金
            cashierSettleService.deductWalletCostToRentFine(vo);
            rentFineAmt = renWalletAmt + rentFineAmt;
            settleCancelOrdersAccount.setRentSurplusWalletAmt(settleCancelOrdersAccount.getRentSurplusWalletAmt()+amt);
        }
        //2.2 租车费用抵罚金
        if(rentCostAmt>0 && rentFineAmt<0){
            AccountRenterCostToFineReqVO vo = new AccountRenterCostToFineReqVO();
            BeanUtils.copyProperties(settleOrders,vo);
            vo.setMemNo(settleOrders.getRenterMemNo());
            int debtAmt = rentCostAmt + rentFineAmt;
            //计算抵扣金额
            int amt = debtAmt>=0?rentFineAmt:-rentCostAmt;
            vo.setAmt(amt);
            //租车费用抵扣 罚金
            cashierSettleService.deductRentCostToRentFine(vo);
            rentFineAmt = rentCostAmt + rentFineAmt;
            settleCancelOrdersAccount.setRentSurplusCostAmt(settleCancelOrdersAccount.getRentSurplusCostAmt()+amt);
        }
        //2.2 押金抵扣
        if(rentDepositAmt>0 && rentFineAmt<0){
            OrderCancelRenterDepositReqVO vo = new OrderCancelRenterDepositReqVO();
            BeanUtils.copyProperties(settleOrders,vo);
            vo.setMemNo(settleOrders.getRenterMemNo());
            int debtAmt = rentDepositAmt + rentFineAmt;
            //计算抵扣金额
            int amt = debtAmt>=0?rentFineAmt:-rentDepositAmt;
            vo.setAmt(amt);
            //押金抵扣抵扣 罚金
            cashierSettleService.deductRentDepositToRentFine(vo);
            rentFineAmt = rentDepositAmt + rentFineAmt;
            settleCancelOrdersAccount.setRentSurplusDepositAmt(settleCancelOrdersAccount.getRentSurplusDepositAmt()+amt);
        }
        //2.2 违章押金抵扣
        if(rentWzDepositAmt>0 && rentFineAmt<0){
            RenterCancelWZDepositCostReqVO vo = new RenterCancelWZDepositCostReqVO();
            BeanUtils.copyProperties(settleOrders,vo);
            vo.setMemNo(settleOrders.getRenterMemNo());
            int debtAmt = rentWzDepositAmt + rentFineAmt;
            //计算抵扣金额
            int amt = debtAmt>=0?rentFineAmt:-rentWzDepositAmt;
            vo.setAmt(amt);
            //押金抵扣抵扣 罚金
            cashierSettleService.deductRentWzDepositToRentFine(vo);
            rentFineAmt = rentWzDepositAmt + rentFineAmt;
            settleCancelOrdersAccount.setRentSurplusWzDepositAmt(settleCancelOrdersAccount.getRentSurplusWzDepositAmt()+amt);
        }
        //3 租客存在 罚金走历史欠款
        if(rentFineAmt<0){
            //2 记录历史欠款
            AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
            BeanUtils.copyProperties(settleOrders,accountInsertDebt);
            accountInsertDebt.setType(DebtTypeEnum.CANCEL.getCode());
            accountInsertDebt.setMemNo(settleOrders.getRenterMemNo());
            accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            accountInsertDebt.setAmt(rentFineAmt);
            cashierService.createDebt(accountInsertDebt);
        }
    }

    /**
     * 租客金额 退还 包含 凹凸币，钱包 租车费用 押金 违章押金 退还 （优惠卷退还 TODO）
     * @param settleOrders
     * @param settleCancelOrdersAccount
     */
    public void refundCancelCost(SettleOrders settleOrders, SettleCancelOrdersAccount settleCancelOrdersAccount,OrderStatusDTO orderStatusDTO) {
        //1退还凹凸币
//        if(settleCancelOrdersAccount.getRenCoinAmt()>0){
//            accountRenterCostCoinService.settleAutoCoin(settleOrders.getRenterMemNo(),settleOrders.getOrderNo(),settleCancelOrdersAccount.getRenCoinAmt());
//        }
        //2 退还 钱包金额
        if(settleCancelOrdersAccount.getRentSurplusWalletAmt()>0){
            walletProxyService.returnOrChargeWallet(settleOrders.getRenterMemNo(),settleOrders.getOrderNo(),settleCancelOrdersAccount.getRentSurplusWalletAmt());
            //记录退还
            AccountRenterCostDetailReqVO accountRenterCostDetail = new AccountRenterCostDetailReqVO ();
            accountRenterCostDetail.setMemNo(settleOrders.getRenterMemNo());
            accountRenterCostDetail.setOrderNo(settleOrders.getOrderNo());
            accountRenterCostDetail.setPaySource(com.atzuche.order.commons.enums.cashier.PaySourceEnum.WALLET_PAY.getText());
            accountRenterCostDetail.setPaySourceCode(com.atzuche.order.commons.enums.cashier.PaySourceEnum.WALLET_PAY.getCode());
            accountRenterCostDetail.setRenterCashCodeEnum(RenterCashCodeEnum.CANCEL_RENT_COST_TO_RETURN_AMT);
            accountRenterCostDetail.setAmt(-settleCancelOrdersAccount.getRentSurplusWalletAmt());
            accountRenterCostDetail.setPayType(PayTypeEnum.PAY_PUR.getCode());
            cashierService.refundRentCostWallet(accountRenterCostDetail);
        }
        //3 租车费用 退还
        if(settleCancelOrdersAccount.getRentSurplusCostAmt()>0){
            //退还剩余 租车费用
            List<CashierRefundApplyReqVO> cashierRefundApplyReqs = getCancelCashierRefundApply(settleOrders,-settleCancelOrdersAccount.getRentSurplusCostAmt());
            if(!CollectionUtils.isEmpty(cashierRefundApplyReqs)){
                for(int i=0;i<cashierRefundApplyReqs.size();i++){
                    cashierService.refundRentCost(cashierRefundApplyReqs.get(i));
                }
            }
            orderStatusDTO.setRentCarRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());
        }
        //4 车辆押金退还
        if(settleCancelOrdersAccount.getRentSurplusDepositAmt()>0){
            //1退还租车押金
            CashierEntity cashierEntity = cashierNoTService.getCashierEntity(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), DataPayKindConstant.RENT);

            CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
            BeanUtils.copyProperties(cashierEntity,cashierRefundApply);
            cashierRefundApply.setAmt(-settleCancelOrdersAccount.getRentSurplusDepositAmt());
            cashierRefundApply.setRenterCashCodeEnum(RenterCashCodeEnum.CANCEL_RENT_DEPOSIT_TO_RETURN_AMT);
            cashierRefundApply.setRemake(RenterCashCodeEnum.CANCEL_RENT_DEPOSIT_TO_RETURN_AMT.getTxt());
            cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getCashNo());
            cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
            cashierRefundApply.setQn(cashierEntity.getQn());
            cashierService.refundDeposit(cashierRefundApply);
            orderStatusDTO.setDepositRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());
        }
        // 5 违章押金退还
        if(settleCancelOrdersAccount.getRentSurplusWzDepositAmt()>0){
            CashierEntity cashierEntity = cashierNoTService.getCashierEntity(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), DataPayKindConstant.RENT);
            CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
            BeanUtils.copyProperties(cashierEntity,cashierRefundApply);

            cashierRefundApply.setAmt(-settleCancelOrdersAccount.getRentSurplusWzDepositAmt());
            cashierRefundApply.setRenterCashCodeEnum(RenterCashCodeEnum.CANCEL_RENT_WZ_DEPOSIT_TO_RETURN_AMT);
            cashierRefundApply.setRemake(RenterCashCodeEnum.CANCEL_RENT_WZ_DEPOSIT_TO_RETURN_AMT.getTxt());
            cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
            cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
            cashierRefundApply.setQn(cashierEntity.getQn());
            cashierRefundApply.setPayKind(DataPayKindConstant.DEPOSIT);

            cashierService.refundWZDeposit(cashierRefundApply);
            orderStatusDTO.setWzRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());
        }
    }

    /**
     *   返回可退还租车费用
     * @param settleOrders
     * @param refundAmt
     * @return
     */
    private List<CashierRefundApplyReqVO> getCancelCashierRefundApply(SettleOrders settleOrders, int refundAmt) {
        List<CashierRefundApplyReqVO> cashierRefundApplys = new ArrayList<>();
        //1 租车费用
        CashierEntity cashierEntity = cashierNoTService.getCashierEntity(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), DataPayKindConstant.RENT_AMOUNT);
        if(Objects.nonNull(cashierEntity) && Objects.nonNull(cashierEntity.getId()) && refundAmt<0){
            CashierRefundApplyReqVO vo = new CashierRefundApplyReqVO();
            BeanUtils.copyProperties(cashierEntity,vo);
            vo.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getCashNo());
            vo.setRenterCashCodeEnum(RenterCashCodeEnum.CANCEL_RENT_COST_TO_RETURN_AMT);
            vo.setPaySource(PaySourceEnum.getFlagText(cashierEntity.getPaySource()));
            vo.setPayType(PayTypeEnum.getFlagText(cashierEntity.getPayType()));
            vo.setRemake("取消订单退还");
            int amt = refundAmt + cashierEntity.getPayAmt();
            vo.setAmt(amt>=0?refundAmt:-cashierEntity.getPayAmt());
            cashierRefundApplys.add(vo);
            refundAmt = refundAmt + cashierEntity.getPayAmt();
        }
        if(refundAmt<0){
            List<CashierEntity> cashierEntitys = cashierNoTService.getCashierEntitys(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), DataPayKindConstant.RENT_INCREMENT);
            if(CollectionUtils.isEmpty(cashierEntitys)){
                for(int i=0;i<cashierEntitys.size();i++){
                    if(refundAmt<0){
                        CashierRefundApplyReqVO vo = new CashierRefundApplyReqVO();
                        BeanUtils.copyProperties(cashierEntity,vo);
                        vo.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getCashNo());
                        vo.setRenterCashCodeEnum(RenterCashCodeEnum.CANCEL_RENT_COST_TO_RETURN_AMT);
                        vo.setPaySource(PaySourceEnum.getFlagText(cashierEntity.getPaySource()));
                        vo.setRemake("取消订单退还");
                        int amt = refundAmt + cashierEntity.getPayAmt();
                        vo.setAmt(amt>=0?refundAmt:-cashierEntity.getPayAmt());
                        cashierRefundApplys.add(vo);
                        refundAmt = refundAmt + cashierEntity.getPayAmt();
                    }

                }
            }
        }

        return cashierRefundApplys;
    }

    /**
     * 租客还历史欠款
     */
    public void repayHistoryDebtRentCancel(SettleOrders settleOrders, SettleCancelOrdersAccount settleCancelOrdersAccount) {
        //钱包抵扣历史欠款
        if(settleCancelOrdersAccount.getRentSurplusWalletAmt()>0){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrders,cashierDeductDebtReq);
            cashierDeductDebtReq.setAmt(settleCancelOrdersAccount.getRentSurplusWalletAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_WALLET_TO_HISTORY_AMT);
            cashierDeductDebtReq.setMemNo(settleOrders.getRenterMemNo());
            cashierDeductDebtReq.setPaySource(PaySourceEnum.WALLET_PAY.getText());
            cashierDeductDebtReq.setPaySourceCode(PaySourceEnum.WALLET_PAY.getCode());
            CashierDeductDebtResVO result = cashierService.deductDebtByRentCost(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余租车费用
                settleCancelOrdersAccount.setRentSurplusWalletAmt(settleCancelOrdersAccount.getRentSurplusWalletAmt() - deductAmt);
            }
        }
       //租车费用
        if(settleCancelOrdersAccount.getRentSurplusCostAmt()>0){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrders,cashierDeductDebtReq);
            cashierDeductDebtReq.setAmt(settleCancelOrdersAccount.getRentSurplusCostAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_COST_TO_HISTORY_AMT);
            cashierDeductDebtReq.setMemNo(settleOrders.getRenterMemNo());
            CashierDeductDebtResVO result = cashierService.deductDebtByRentCost(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余租车费用
                settleCancelOrdersAccount.setRentSurplusCostAmt(settleCancelOrdersAccount.getRentSurplusCostAmt() - deductAmt);
            }
        }
        //车辆押金
        if(settleCancelOrdersAccount.getRentSurplusDepositAmt()>0){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrders,cashierDeductDebtReq);
            cashierDeductDebtReq.setAmt(settleCancelOrdersAccount.getRentSurplusDepositAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT);
            cashierDeductDebtReq.setMemNo(settleOrders.getRenterMemNo());
            CashierDeductDebtResVO result = cashierService.deductDebt(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余租车费用
                settleCancelOrdersAccount.setRentSurplusDepositAmt(settleCancelOrdersAccount.getRentSurplusDepositAmt() - deductAmt);
            }
        }

        //违章押金
        if(settleCancelOrdersAccount.getRentSurplusWzDepositAmt()>0){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrders,cashierDeductDebtReq);
            cashierDeductDebtReq.setAmt(settleCancelOrdersAccount.getRentSurplusWzDepositAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.CANCEL_WZ_DEPOSIT_TO_HISTORY_AMT);
            cashierDeductDebtReq.setMemNo(settleOrders.getRenterMemNo());
            CashierDeductDebtResVO result = cashierService.deductWZDebt(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余租车费用
                settleCancelOrdersAccount.setRentSurplusWzDepositAmt(settleCancelOrdersAccount.getRentSurplusWzDepositAmt() - deductAmt);
            }
        }
    }

    /**
     * 车俩结算 优惠卷 退还
     * @param renterOrderSubsidyDetails 租客补贴 列表
     */
    public void settleUndoCoupon(String orderNo,List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails) {
//        isUndoCoupon          是否撤销平台优惠券
//        isUndoGetCarFeeCoupon 是否撤销送取服务券
//        isUndoOwnerCoupon     是否撤销车主券
        boolean isUndoCoupon=false;
        boolean isUndoGetCarFeeCoupon=false;
        boolean isUndoOwnerCoupon=false;
        List<RenterOrderSubsidyDetailEntity> isUndoCouponDetails = renterOrderSubsidyDetails.stream().filter(obj ->{
            return RenterCashCodeEnum.REAL_COUPON_OFFSET.equals(obj.getSubsidyCostCode());
        }).collect(Collectors.toList());
        List<RenterOrderSubsidyDetailEntity> isUndoGetCarFeeCouponDetails = renterOrderSubsidyDetails.stream().filter(obj ->{
            return RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.equals(obj.getSubsidyCostCode());
        }).collect(Collectors.toList());
        List<RenterOrderSubsidyDetailEntity> isUndoOwnerCouponDetails = renterOrderSubsidyDetails.stream().filter(obj ->{
            return RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.equals(obj.getSubsidyCostCode());
        }).collect(Collectors.toList());
        //判断是否要退
        isUndoCoupon = CollectionUtils.isEmpty(isUndoCouponDetails);
        isUndoGetCarFeeCoupon = CollectionUtils.isEmpty(isUndoGetCarFeeCouponDetails);
        isUndoOwnerCoupon = CollectionUtils.isEmpty(isUndoOwnerCouponDetails);
        // 结算退回优惠卷
        try {
            orderCouponService.settleUndoCoupon(orderNo,isUndoCoupon,isUndoGetCarFeeCoupon,isUndoOwnerCoupon);
        } catch (Exception e) {
            log.error("OrderSettleNoTService settleUndoCoupon error [{}]",e);
        }

    }

    /**
     * 处理
     * @param settleOrders
     * @param settleCancelOrdersAccount
     */
    public void handleIncomeFine(SettleOrders settleOrders, SettleCancelOrdersAccount settleCancelOrdersAccount) {
        AccountPlatformProfitEntity accountPlatformProfitEntity = new AccountPlatformProfitEntity();
        accountPlatformProfitEntity.setOrderNo(settleOrders.getOrderNo());
        accountPlatformProfitEntity.setStatus(3);

        // 租客罚金收益
        if(settleCancelOrdersAccount.getRentFineIncomeAmt()!=0){
            AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
            entity.setOrderNo(settleOrders.getOrderNo());
            entity.setMemNo(settleOrders.getRenterMemNo());
            entity.setRenterOrderNo(settleOrders.getRenterOrderNo());
            entity.setAmt(settleCancelOrdersAccount.getRentFineIncomeAmt());
            entity.setType(1);
            cashierSettleService.insertAccountRenterCostSettleDetail(entity);
            walletProxyService.returnOrChargeWallet(settleOrders.getRenterMemNo(),settleOrders.getOrderNo(),settleCancelOrdersAccount.getRentFineIncomeAmt());
        }
        // 车主罚金收入
        if(settleCancelOrdersAccount.getOwnerFineIncomeAmt()!=0){
            AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamine = new AccountOwnerIncomeExamineReqVO();
            accountOwnerIncomeExamine.setAmt(settleCancelOrdersAccount.getOwnerFineIncomeAmt());
            accountOwnerIncomeExamine.setMemNo(settleOrders.getOwnerMemNo());
            accountOwnerIncomeExamine.setOrderNo(settleOrders.getOrderNo());
            accountOwnerIncomeExamine.setRemark("罚金收入");
            cashierService.insertOwnerIncomeExamine(accountOwnerIncomeExamine);
            accountPlatformProfitEntity.setOwnerIncomeAmt(-settleCancelOrdersAccount.getOwnerFineIncomeAmt());
        }
        // 平台罚金收入
        if(settleCancelOrdersAccount.getPlatformFineImconeAmt()!=0){
            List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails = new ArrayList<>();
            AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
            entity.setOrderNo(settleOrders.getOrderNo());
            entity.setAmt(settleCancelOrdersAccount.getPlatformFineImconeAmt());
            entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getTxt());
            entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getCashNo());
            accountPlatformProfitDetails.add(entity);
            cashierSettleService.insertAccountPlatformProfitDetails(accountPlatformProfitDetails);
            accountPlatformProfitEntity.setPlatformReceivableAmt(settleCancelOrdersAccount.getPlatformFineImconeAmt());
            accountPlatformProfitEntity.setPlatformReceivedAmt(settleCancelOrdersAccount.getPlatformFineImconeAmt());
        }
        cashierSettleService.insertAccountPlatformProfit(accountPlatformProfitEntity);
    }
}

package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.req.DeductDepositToRentCostReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.coin.service.AccountRenterCostCoinService;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.vo.handover.HandoverCarRepVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarReqVO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.service.*;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.rentercost.service.*;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.vo.req.*;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.doc.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 订单结算
 */
@Service
public class OrderSettleNoTService {
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
    @Autowired private RenterOrderCostCombineService renterOrderCostCombineService;
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


    /**
     * 车辆结算
     * @param orderNo
     * @return
     */
    public List<AccountRenterCostDetailEntity> getAccountRenterCostDetailsByOrderNo(String orderNo){
        return cashierSettleService.getAccountRenterCostDetailsByOrderNo(orderNo);
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
        OilAmtDTO oilAmtDTO = getOilAmtDTO(settleOrders,settleOrders.getRenterOrder(),handoverCarRep,renterGoodsDetail);
        RenterOrderCostDetailEntity oilAmt = renterOrderCostCombineService.getOilAmtEntity(oilAmtDTO);
        //3 交接车-获取超里程费用
        MileageAmtDTO mileageAmtDTO = getMileageAmtDTO(settleOrders,settleOrders.getRenterOrder(),handoverCarRep,renterGoodsDetail);
        RenterOrderCostDetailEntity mileageAmt = renterOrderCostCombineService.getMileageAmtEntity(mileageAmtDTO);
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
        rentCosts.setOilAmt(oilAmt);
        rentCosts.setMileageAmt(mileageAmt);
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
        OilAmtDTO oilAmtDTO = getOilAmtOwner(settleOrders.getOwnerOrder(),handoverCarRep,ownerGoodsDetail);
        OwnerOrderPurchaseDetailEntity renterOrderCostDetail = ownerOrderCostCombineService.getOilAmtEntity(oilAmtDTO);

        //8 管理后台补贴 （租客车主共用表 ，会员号区分车主/租客）
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(settleOrders.getOrderNo(),settleOrders.getOwnerMemNo());
        //9 获取全局的车主订单罚金明细（租客车主共用表 ，会员号区分车主/租客）
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        //10 TODO 车主罚金

        ownerCosts.setProxyExpense(proxyExpense);
        ownerCosts.setServiceExpense(serviceExpense);
        ownerCosts.setOwnerOrderSubsidyDetail(ownerOrderSubsidyDetail);
        ownerCosts.setOwnerOrderPurchaseDetail(ownerOrderPurchaseDetail);
        ownerCosts.setOwnerOrderIncrementDetail(ownerOrderIncrementDetail);
        ownerCosts.setGpsCost(gpsCost);
        ownerCosts.setRenterOrderCostDetail(renterOrderCostDetail);
        ownerCosts.setOrderConsoleSubsidyDetails(orderConsoleSubsidyDetails);
        ownerCosts.setConsoleRenterOrderFineDeatails(consoleRenterOrderFineDeatails);
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
        // TODO
        oilAmtDTO.setCityCode(null);
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
            int ownerCostAmt = accountOwnerCostSettleDetails.stream().filter(obj ->{return obj.getAmt()<0;}).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            settleOrdersDefinition.setOwnerCostAmt(ownerCostAmt);
            int ownerSubsidyAmt = accountOwnerCostSettleDetails.stream().filter(obj ->{return obj.getAmt()>0;}).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
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
            if(Objects.nonNull(proxyExpense) && Objects.nonNull(proxyExpense.getTotalAmount())){
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(proxyExpense,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_PROXY_EXPENSE_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_OWNER_PROXY_EXPENSE_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(proxyExpense.getId()));
                int amt = Objects.isNull(proxyExpense.getTotalAmount())?0:proxyExpense.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            }
        }
        // 1.2 车主端平台服务费
        OwnerOrderPurchaseDetailEntity serviceExpense = ownerCosts.getServiceExpense();
        if(Objects.nonNull(serviceExpense) && Objects.nonNull(serviceExpense.getTotalAmount())){
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
            BeanUtils.copyProperties(serviceExpense,accountOwnerCostSettleDetail);
            accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_SERVICE_EXPENSE_COST.getCashNo());
            accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_OWNER_SERVICE_EXPENSE_COST.getTxt());
            accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(serviceExpense.getId()));
            int amt = Objects.isNull(serviceExpense.getTotalAmount())?0:serviceExpense.getTotalAmount();
            accountOwnerCostSettleDetail.setAmt(amt);
            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
        }
        // 1.3 获取车主补贴明细列表
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetails = ownerCosts.getOwnerOrderSubsidyDetail();
        if(CollectionUtils.isEmpty(ownerOrderSubsidyDetails)){
            for(int i=0; i<ownerOrderSubsidyDetails.size();i++){
                OwnerOrderSubsidyDetailEntity renterOrderCostDetail = ownerOrderSubsidyDetails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int subsidyAmount = Objects.isNull(renterOrderCostDetail.getSubsidyAmount())?0:renterOrderCostDetail.getSubsidyAmount();
                accountOwnerCostSettleDetail.setAmt(subsidyAmount);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
            }
        }
        //1.4 获取车主费用列表
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = ownerCosts.getOwnerOrderPurchaseDetail();
        if(CollectionUtils.isEmpty(ownerOrderPurchaseDetail)){
            for(int i=0; i<ownerOrderPurchaseDetail.size();i++){
                OwnerOrderPurchaseDetailEntity renterOrderCostDetail = ownerOrderPurchaseDetail.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_DEBT.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_OWNER_DEBT.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            }
        }
        //1.5获取车主增值服务费用列表
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = ownerCosts.getOwnerOrderIncrementDetail();
        if(CollectionUtils.isEmpty(ownerOrderIncrementDetail)){
            for(int i=0; i<ownerOrderIncrementDetail.size();i++){
                OwnerOrderIncrementDetailEntity renterOrderCostDetail = ownerOrderIncrementDetail.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            }
        }
        //1.6 获取gps服务费
        List<OwnerOrderPurchaseDetailEntity> gpsCost = ownerCosts.getGpsCost();
        if(CollectionUtils.isEmpty(gpsCost)){
            for(int i=0; i<gpsCost.size();i++){
                OwnerOrderPurchaseDetailEntity renterOrderCostDetail = gpsCost.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
                accountOwnerCostSettleDetail.setAmt(amt);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
            }
        }
        //1.7 获取车主油费
        OwnerOrderPurchaseDetailEntity renterOrderCostDetail = ownerCosts.getRenterOrderCostDetail();
        if(Objects.nonNull(renterOrderCostDetail) && Objects.nonNull(renterOrderCostDetail.getTotalAmount())){
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
            BeanUtils.copyProperties(renterOrderCostDetail,accountOwnerCostSettleDetail);
            accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getCashNo());
            accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getTxt());
            accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            int amt = Objects.isNull(renterOrderCostDetail.getTotalAmount())?0:renterOrderCostDetail.getTotalAmount();
            accountOwnerCostSettleDetail.setAmt(amt);
            accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);
        }
        //1.8 管理后台补贴
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = ownerCosts.getOrderConsoleSubsidyDetails();
        if(CollectionUtils.isEmpty(orderConsoleSubsidyDetails)){
            for(int i=0; i<orderConsoleSubsidyDetails.size();i++){
                OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetail = orderConsoleSubsidyDetails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(orderConsoleSubsidyDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getTxt());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                int subsidyAmount = Objects.isNull(orderConsoleSubsidyDetail.getSubsidyAmount())?0:orderConsoleSubsidyDetail.getSubsidyAmount();
                accountOwnerCostSettleDetail.setAmt(subsidyAmount);
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetail.getSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    entity.setAmt(-subsidyAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
            }
        }
        //1.9 全局的车主订单罚金明细
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails = ownerCosts.getConsoleRenterOrderFineDeatails();
        if(CollectionUtils.isEmpty(consoleRenterOrderFineDeatails)){
            for(int i=0; i<consoleRenterOrderFineDeatails.size();i++){
                ConsoleRenterOrderFineDeatailEntity orderConsoleSubsidyDetail = consoleRenterOrderFineDeatails.get(i);
                AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetail = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(orderConsoleSubsidyDetail,accountOwnerCostSettleDetail);
                accountOwnerCostSettleDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                accountOwnerCostSettleDetail.setSourceDetail(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                int fineAmount = Objects.isNull(orderConsoleSubsidyDetail.getFineAmount())?0:orderConsoleSubsidyDetail.getFineAmount();

                accountOwnerCostSettleDetail.setAmt(fineAmount);
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                //罚金来源方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetail.getFineSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    entity.setAmt(-fineAmount);
                    entity.setSubsidyName(SubsidySourceCodeEnum.OWNER.getDesc());
                    settleOrdersDefinition.addPlatformSubsidy(entity);
                }
                //罚金补贴方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetail.getFineSubsidyCode())){
                    AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
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
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = new ArrayList<>();

        if(Objects.nonNull(rentCosts)){
            //1.1 查询租车费用
            List<RenterOrderCostDetailEntity> renterOrderCostDetails = rentCosts.getRenterOrderCostDetails();
            if(CollectionUtils.isEmpty(renterOrderCostDetails)){
                for(int i=0; i<renterOrderCostDetails.size();i++){
                    RenterOrderCostDetailEntity renterOrderCostDetail = renterOrderCostDetails.get(i);
                    AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,accountRenterCostSettleDetail);
                    accountRenterCostSettleDetail.setCostCode(renterOrderCostDetail.getCostCode());
                    accountRenterCostSettleDetail.setCostDetail(renterOrderCostDetail.getCostDesc());
                    accountRenterCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
                }
            }
            //1.2 交接车-油费
            RenterOrderCostDetailEntity oilAmt = rentCosts.getOilAmt();
            if(Objects.nonNull(oilAmt) && Objects.nonNull(oilAmt.getId())){
                AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                BeanUtils.copyProperties(oilAmt,accountRenterCostSettleDetail);
                accountRenterCostSettleDetail.setCostCode(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_OIL_COST.getCashNo());
                accountRenterCostSettleDetail.setCostDetail(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_OIL_COST.getTxt());
                accountRenterCostSettleDetail.setUniqueNo(String.valueOf(oilAmt.getId()));
                accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
            }
            //1.3 交接车-获取超里程费用
            RenterOrderCostDetailEntity mileageAmt = rentCosts.getMileageAmt();
            if(Objects.nonNull(mileageAmt) && Objects.nonNull(mileageAmt.getId())){
                AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                BeanUtils.copyProperties(mileageAmt,accountRenterCostSettleDetail);
                accountRenterCostSettleDetail.setCostCode(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getCashNo());
                accountRenterCostSettleDetail.setCostDetail(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getTxt());
                accountRenterCostSettleDetail.setUniqueNo(String.valueOf(mileageAmt.getId()));
                accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);
            }
            //1.4 补贴
            List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails = rentCosts.getRenterOrderSubsidyDetails();
            if(CollectionUtils.isEmpty(renterOrderSubsidyDetails)) {
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
            if(CollectionUtils.isEmpty(renterOrderFineDeatails)) {
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
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getFineSubsidySourceCode())&& fineAmount<0){
                        AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getCashNo());
                        entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-fineAmount);
                        entity.setSubsidyName(SubsidySourceCodeEnum.RENTER.getDesc());
                        settleOrdersDefinition.addPlatformSubsidy(entity);
                    }
                    //罚金补贴方 是平台
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getFineSubsidyCode())&& fineAmount>0){
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
            if(CollectionUtils.isEmpty(orderConsoleSubsidyDetails)) {
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
            if(CollectionUtils.isEmpty(consoleRenterOrderFineDeatails)) {
                for (int i = 0; i < consoleRenterOrderFineDeatails.size(); i++) {
                    ConsoleRenterOrderFineDeatailEntity renterOrderCostDetail = consoleRenterOrderFineDeatails.get(i);
                    AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,accountRenterCostSettleDetail);
                    accountRenterCostSettleDetail.setCostCode(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    accountRenterCostSettleDetail.setCostDetail(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    accountRenterCostSettleDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    accountRenterCostSettleDetail.setAmt(renterOrderCostDetail.getFineAmount());
                    accountRenterCostSettleDetails.add(accountRenterCostSettleDetail);

                    //罚金来源方 是平台
                    int fineAmount = renterOrderCostDetail.getFineAmount();
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getFineSubsidySourceCode()) && fineAmount<0){
                        AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                        entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                        entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                        entity.setAmt(-fineAmount);
                        entity.setSubsidyName(SubsidySourceCodeEnum.RENTER.getDesc());
                        settleOrdersDefinition.addPlatformSubsidy(entity);
                    }
                    //罚金补贴方 是平台
                    if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getFineSubsidyCode()) && fineAmount>0){
                        AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
                        BeanUtils.copyProperties(renterOrderCostDetail,entity);
                        entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                        entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
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
        //1 先删除 之前的的 结算记录 逻辑删除
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
            //1 退还凹凸币 coinAmt为订单真实使用的凹凸币
            int coinAmt = accountRenterCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(obj.getCostCode());
            }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            if(coinAmt>0){
                //退还多余凹凸币
                accountRenterCostCoinService.settleAutoCoin(settleOrdersAccount.getRenterMemNo(),settleOrdersAccount.getOrderNo(),coinAmt);
            }
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
}

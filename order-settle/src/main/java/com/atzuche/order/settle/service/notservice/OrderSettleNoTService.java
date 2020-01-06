package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.DeductDepositToRentCostReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.OilAmtDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.enums.HandoverCarTypeEnum;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.vo.handover.HandoverCarRepVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarReqVO;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderCostCombineService;
import com.atzuche.order.ownercost.service.OwnerOrderIncrementDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderPurchaseDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.rentercost.service.*;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.settle.vo.req.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * @param renterOrderNo
     * @param ownerOrderNo
     * @param renterMemNo
     * @param ownerMemNo
     * @return
     */
    public SettleOrders initSettleOrders(String orderNo, String renterOrderNo, String ownerOrderNo, String renterMemNo, String ownerMemNo) {
        SettleOrders settleOrders = new SettleOrders();
        settleOrders.setOrderNo(orderNo);
        settleOrders.setRenterOrderNo(renterOrderNo);
        settleOrders.setOwnerOrderNo(ownerOrderNo);
        settleOrders.setRenterMemNo(renterMemNo);
        settleOrders.setOwnerMemNo(ownerMemNo);
        return settleOrders;
    }
    /**
     * 校验是否可以结算
     * @param renterOrder
     */
    public void check(RenterOrderEntity renterOrder) {
    }

    /**
     *  租客返回基本信息 TODO
     * @return
     */
    private CostBaseDTO getCostBaseRent(SettleOrders settleOrders,RenterOrderEntity renterOrder){
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(renterOrder.getOrderNo());
        costBaseDTO.setMemNo("");
        costBaseDTO.setRenterOrderNo(renterOrder.getRenterOrderNo());
        costBaseDTO.setStartTime(renterOrder.getExpRentTime());
        costBaseDTO.setEndTime(renterOrder.getExpRevertTime());
        return costBaseDTO;
    }
    /**
     * 租客交接车-油费 参数构建 TODO
     * @param settleOrders
     * @param renterOrder
     * @return
     */
    private OilAmtDTO getOilAmtDTO(SettleOrders settleOrders,RenterOrderEntity renterOrder,HandoverCarRepVO handoverCarRep,OwnerGoodsDetailDTO ownerGoodsDetail) {
        OilAmtDTO oilAmtDTO = new OilAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseRent(settleOrders,renterOrder);
        oilAmtDTO.setCostBaseDTO(costBaseDTO);
        oilAmtDTO.setCarOwnerType(ownerGoodsDetail.getCarOwnerType());
        // TODO
        oilAmtDTO.setCityCode(null);
        oilAmtDTO.setEngineType(ownerGoodsDetail.getCarEngineType());
        oilAmtDTO.setOilVolume(ownerGoodsDetail.getCarOilVolume());
        // TODO
        oilAmtDTO.setOilScaleDenominator(null);

        //默认值0  取/还 车油表刻度
        oilAmtDTO.setGetOilScale(0);
        oilAmtDTO.setReturnOilScale(0);
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfos = handoverCarRep.getRenterHandoverCarInfoEntities();
        if(!CollectionUtils.isEmpty(renterHandoverCarInfos)){
            for(int i=0;i<renterHandoverCarInfos.size();i++){
                RenterHandoverCarInfoEntity renterHandoverCarInfo = renterHandoverCarInfos.get(i);
                if(HandoverCarTypeEnum.OWNER_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                ||  HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                ){
                    oilAmtDTO.setReturnOilScale(Objects.isNull(renterHandoverCarInfo.getOilNum())?0:renterHandoverCarInfo.getOilNum());
                }

                if(HandoverCarTypeEnum.RENTER_TO_OWNER.getValue().equals(renterHandoverCarInfo.getType())
                        ||  HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().equals(renterHandoverCarInfo.getType())
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
    private MileageAmtDTO getMileageAmtDTO(SettleOrders settleOrders, RenterOrderEntity renterOrder,HandoverCarRepVO handoverCarRep,OwnerGoodsDetailDTO goodsDetail) {
        MileageAmtDTO mileageAmtDTO = new MileageAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseRent(settleOrders,renterOrder);
        mileageAmtDTO.setCostBaseDTO(costBaseDTO);

        mileageAmtDTO.setCarOwnerType(goodsDetail.getCarOwnerType());
        mileageAmtDTO.setGuideDayPrice(goodsDetail.getCarGuidePrice());
        mileageAmtDTO.setDayMileage(goodsDetail.getCarDayMileage());

        //默认值0  取/还 车里程数
        mileageAmtDTO.setGetmileage(0);
        mileageAmtDTO.setReturnMileage(0);
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfos = handoverCarRep.getRenterHandoverCarInfoEntities();
        if(!CollectionUtils.isEmpty(renterHandoverCarInfos)){
            for(int i=0;i<renterHandoverCarInfos.size();i++){
                RenterHandoverCarInfoEntity renterHandoverCarInfo = renterHandoverCarInfos.get(i);
                if(HandoverCarTypeEnum.OWNER_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                        ||  HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                ){
                    mileageAmtDTO.setReturnMileage(Objects.isNull(renterHandoverCarInfo.getMileageNum())?0:renterHandoverCarInfo.getOilNum());
                }

                if(HandoverCarTypeEnum.RENTER_TO_OWNER.getValue().equals(renterHandoverCarInfo.getType())
                        ||  HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().equals(renterHandoverCarInfo.getType())
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
    public void getRenterCostSettleDetail(SettleOrders settleOrders,RenterOrderEntity renterOrder) {
    	RentCosts rentCosts = new RentCosts();
    	//1  初始化 
    	//1.1 油费、超里程费用 配送模块需要的参数
    	HandoverCarReqVO handoverCarReq = new HandoverCarReqVO();
    	handoverCarReq.setRenterOrderNo(settleOrders.getRenterOrderNo());
        HandoverCarRepVO handoverCarRep = handoverCarService.getRenterHandover(handoverCarReq);
        // 1.2 油费、超里程费用 订单商品需要的参数
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(settleOrders.getRenterOrderNo(),Boolean.TRUE);
        settleOrders.setRenterMemNo(ownerGoodsDetail.getMemNo());

        //1 查询租车费用
        List<RenterOrderCostDetailEntity> renterOrderCostDetails = renterOrderCostDetailService.listRenterOrderCostDetail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        //2 交接车-油费
        OilAmtDTO oilAmtDTO = getOilAmtDTO(settleOrders,renterOrder,handoverCarRep,ownerGoodsDetail);
        RenterOrderCostDetailEntity oilAmt = renterOrderCostCombineService.getOilAmtEntity(oilAmtDTO);
        //3 交接车-获取超里程费用
        MileageAmtDTO mileageAmtDTO = getMileageAmtDTO(settleOrders,renterOrder,handoverCarRep,ownerGoodsDetail);
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
    public void getOwnerCostSettleDetail(SettleOrders settleOrders, OwnerOrderEntity ownerOrder) {
        OwnerCosts ownerCosts = new OwnerCosts();
        //1  初始化
        //1.1 油费、超里程费用 配送模块需要的参数
        HandoverCarReqVO handoverCarReq = new HandoverCarReqVO();
        handoverCarReq.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
        HandoverCarRepVO handoverCarRep = handoverCarService.getRenterHandover(handoverCarReq);
        // 1.2 油费、超里程费用 订单商品需要的参数
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(settleOrders.getOwnerMemNo(),Boolean.TRUE);
        settleOrders.setRenterMemNo(ownerGoodsDetail.getMemNo());

        //1 车主端代管车服务费
        CostBaseDTO costBaseDTO= getCostBaseOwner(ownerOrder);
        Integer rentAmt=settleOrders.getRenterOrderCost();
        //代管车服务费比例 商品 TODO
        Integer proxyProportion=0;
        OwnerOrderPurchaseDetailEntity proxyExpense = ownerOrderCostCombineService.getProxyExpense(costBaseDTO,rentAmt,proxyProportion);
        //2 车主端平台服务费
        //服务费比例 商品 TODO
        Integer serviceProportion =0;
        OwnerOrderPurchaseDetailEntity serviceExpense = ownerOrderCostCombineService.getServiceExpense(costBaseDTO,rentAmt,serviceProportion);
        //3 获取车主补贴明细列表
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        //4 获取车主费用列表
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        //5 获取车主增值服务费用列表
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        // 6 获取gps服务费
        //车辆安装gps序列号列表 商品系统 TODO
        List<Integer> lsGpsSerialNumber = null;
        List<OwnerOrderPurchaseDetailEntity> gpsCost =  ownerOrderCostCombineService.getGpsServiceAmtEntity(costBaseDTO,lsGpsSerialNumber);
        //7 获取车主油费 //超里程
        OilAmtDTO oilAmtDTO = getOilAmtOwner(ownerOrder,handoverCarRep,ownerGoodsDetail);
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
     * 车主交接车-油费 参数构建 TODO
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
        // TODO
        oilAmtDTO.setOilScaleDenominator(null);

        //默认值0  取/还 车油表刻度
        oilAmtDTO.setGetOilScale(0);
        oilAmtDTO.setReturnOilScale(0);
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities = handoverCarRep.getOwnerHandoverCarInfoEntities();
        if(!CollectionUtils.isEmpty(ownerHandoverCarInfoEntities)){
            for(int i=0;i<ownerHandoverCarInfoEntities.size();i++){
                OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = ownerHandoverCarInfoEntities.get(i);
                if(HandoverCarTypeEnum.OWNER_TO_RENTER.getValue().equals(ownerHandoverCarInfoEntity.getType())
                        ||  HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().equals(ownerHandoverCarInfoEntity.getType())
                ){
                    oilAmtDTO.setReturnOilScale(Objects.isNull(ownerHandoverCarInfoEntity.getOilNum())?0:ownerHandoverCarInfoEntity.getOilNum());
                }

                if(HandoverCarTypeEnum.RENTER_TO_OWNER.getValue().equals(ownerHandoverCarInfoEntity.getType())
                        ||  HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().equals(ownerHandoverCarInfoEntity.getType())
                ){
                    oilAmtDTO.setGetOilScale(Objects.isNull(ownerHandoverCarInfoEntity.getOilNum())?0:ownerHandoverCarInfoEntity.getOilNum());
                }
            }
        }
        return oilAmtDTO;
    }
    /**
     *  租客返回基本信息 TODO
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
                accountOwnerCostSettleDetail.setAmt(proxyExpense.getTotalAmount());
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
            accountOwnerCostSettleDetail.setAmt(serviceExpense.getTotalAmount());
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
                accountOwnerCostSettleDetail.setAmt(renterOrderCostDetail.getSubsidyAmount());
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(renterOrderCostDetail.getSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    entity.setAmt(-renterOrderCostDetail.getSubsidyAmount());
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
                accountOwnerCostSettleDetail.setAmt(renterOrderCostDetail.getTotalAmount());
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
                accountOwnerCostSettleDetail.setAmt(renterOrderCostDetail.getTotalAmount());
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
                accountOwnerCostSettleDetail.setAmt(renterOrderCostDetail.getTotalAmount());
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
            accountOwnerCostSettleDetail.setAmt(renterOrderCostDetail.getTotalAmount());
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
                accountOwnerCostSettleDetail.setAmt(orderConsoleSubsidyDetail.getSubsidyAmount());
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                // 平台补贴 记录补贴
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetail.getSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    entity.setAmt(-orderConsoleSubsidyDetail.getSubsidyAmount());
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
                accountOwnerCostSettleDetail.setAmt(orderConsoleSubsidyDetail.getFineAmount());
                accountOwnerCostSettleDetail.setUniqueNo(String.valueOf(orderConsoleSubsidyDetail.getId()));
                accountOwnerCostSettleDetails.add(accountOwnerCostSettleDetail);

                //罚金来源方 是平台
                if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(orderConsoleSubsidyDetail.getFineSubsidySourceCode())){
                    AccountPlatformSubsidyDetailEntity entity = new AccountPlatformSubsidyDetailEntity();
                    BeanUtils.copyProperties(renterOrderCostDetail,entity);
                    entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo());
                    entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getTxt());
                    entity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
                    entity.setAmt(-orderConsoleSubsidyDetail.getFineAmount());
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
                    entity.setAmt(-orderConsoleSubsidyDetail.getFineAmount());
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
     * 租客费用结余 处理
     * @param settleOrdersAccount
     */
    public void rentCostSettle(SettleOrders settleOrders , SettleOrdersAccount settleOrdersAccount) {
        //1 如果租车费用计算应付总额大于 实际支付
        if(settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmtFinal()<0){
            //1.1押金 抵扣 租车费用欠款
            if(settleOrdersAccount.getDepositAmt()>0){
                DeductDepositToRentCostReqVO vo = new DeductDepositToRentCostReqVO();
                BeanUtils.copyProperties(settleOrders,vo);
                //车俩押金抵扣 租车费用金额 返回 已抵扣部分
                int amt= cashierSettleService.deductDepositToRentCost(vo);
                //计算剩余车俩押金
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() + amt);
                // 实付费用加上 押金已抵扣部分
                settleOrdersAccount.setRentCostPayAmtFinal(settleOrdersAccount.getRentCostPayAmtFinal() + Math.abs(amt));
            }
        }
//        // 1计算 押金转费用金额
//        if(settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmtFinal()<0 && settleOrdersAccount.getDepositAmt()>0){
//            //计算真实抵扣金额 和剩余 押金
//            int amt = settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmtFinal();
//            int depositToRentAmt = amt+settleOrdersAccount.getDepositAmt()>=0?amt:-settleOrdersAccount.getDepositAmt();
//            settleOrdersAccount.setDepositToRentAmt(depositToRentAmt);
//            settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt()+depositToRentAmt);
//        }

    }

    /**
     * 结算租客还历史欠款
     * @param settleOrdersAccount
     */
    public void repayHistoryDebtRent(SettleOrdersAccount settleOrdersAccount) {
        // 1 存在 实付大于应付 先走历史欠款
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
        //车辆押金存在 且 租车费用没有抵扣完 ，使用车辆押金抵扣
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
     * 退还优先级 凹凸币>钱包>消费
     *
     * @param settleOrdersAccount
     */
    public void refundRentCost(SettleOrdersAccount settleOrdersAccount,List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails) {
        int rentCostSurplusAmt = settleOrdersAccount.getRentCostSurplusAmt();
        if(rentCostSurplusAmt>0){
            //1 优先退还凹凸币
            int coinAmt = accountRenterCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(obj.getCostCode());
            }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            rentCostSurplusAmt = rentCostSurplusAmt-coinAmt;
            if(rentCostSurplusAmt>0){
                // 退还凹凸币 coinAmt
            }
            //2 查询钱包 比较
            //3 退还租车费用
            if(rentCostSurplusAmt>0){

            }
        }
    }

    /**
     * 退还剩余的 车辆押金
     * @param settleOrdersAccount
     */
    public void refundDepositAmt(SettleOrdersAccount settleOrdersAccount) {
    }
}

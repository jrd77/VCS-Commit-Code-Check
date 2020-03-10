package com.atzuche.order.coreapi.service;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterrentcost.utils.AccountSettleUtils;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.commons.entity.rentCost.*;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.DepostiDetailVO;
import com.atzuche.order.commons.vo.OrderSupplementDetailVO;
import com.atzuche.order.commons.vo.WzDepositDetailVO;
import com.atzuche.order.commons.vo.res.*;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.rentercost.service.*;
import com.atzuche.order.rentercost.utils.FineDetailUtils;
import com.atzuche.order.rentercost.utils.OrderSubsidyDetailUtils;
import com.atzuche.order.rentercost.utils.RenterOrderCostDetailUtils;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.service.RenterDepositDetailService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.res.RenterCostVO;
import com.autoyol.doc.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 提供租客费用的对外接口服务
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 10:22 上午
 **/
@Service
public class RenterCostFacadeService {
    @Autowired
    private RenterOrderCostDetailService orderCostDetailService;

    @Autowired
    private RenterOrderSubsidyDetailService subsidyDetailService;
    @Autowired
    private RenterOrderFineDeatailService fineDeatailService;
    @Autowired
    private OrderConsoleCostDetailService consoleCostDetailService;
    @Autowired
    private OrderConsoleSubsidyDetailService consoleSubsidyDetailService;
    @Autowired
    private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;

    @Autowired
    private WzCostFacadeService wzCostFacadeService;

    @Autowired
    private CashierQueryService cashierQueryService;

    @Autowired
    private SupplementService supplementService;
    @Autowired
    private OrderSettleService orderSettleService;

    @Autowired
    private RenterOrderCostService renterOrderCostService;
    @Autowired
    private CashierSettleService cashierSettleService;
    @Autowired
    private RenterDepositDetailService renterDepositDetailService;
    @Autowired
    private AccountRenterCostSettleDetailNoTService accountRenterCostSettleService;
    @Autowired
    private CashierPayService cashierPayService;
    @Autowired
    private AccountRenterDepositNoTService accountRenterDepositNoTService;
    @Autowired
    private AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    private final static Logger logger = LoggerFactory.getLogger(RenterCostFacadeService.class);

    /**
     * 返回租车的总费用
     * @param orderNo
     * @param renterOrderNo
     * @param memNo
     * @return
     */
    public int getTotalRenterCost(String orderNo,String renterOrderNo,String memNo){
        int totalBsicRentCostAmt = orderCostDetailService.getTotalOrderCostAmt(orderNo,renterOrderNo);
        int totalSubsidyAmt = subsidyDetailService.getTotalRenterOrderSubsidyAmt(orderNo,renterOrderNo);
        int totalRenterOrderFineAmt = fineDeatailService.getTotalRenterOrderFineAmt(orderNo,renterOrderNo);
        int totalConsoleFineAmt = consoleRenterOrderFineDeatailService.getTotalConsoleFineAmt(orderNo,memNo);
        int totalConsoleSubsidyAmt = consoleSubsidyDetailService.getTotalRenterOrderConsoleSubsidy(orderNo,memNo);
        int totalConsoleCostAmt = consoleCostDetailService.getTotalOrderConsoleCostAmt(orderNo,memNo);

        int totalCost = totalBsicRentCostAmt +totalSubsidyAmt+totalRenterOrderFineAmt+totalConsoleFineAmt+totalConsoleSubsidyAmt+totalConsoleCostAmt;
        logger.info("getTotalRenterCost[orderNo={},renterOrderNo={},memNo={}]==[totalBasicRentCostAmt={},totalSubsidyAmt={},totalRenterOrderFineAmt={},totalConsoleFineAmt={},totalConsoleSubsidyAmt={},totalConsoleCostAmt={}]",
                orderNo,renterOrderNo,memNo,totalBsicRentCostAmt,totalSubsidyAmt,totalRenterOrderFineAmt,totalConsoleFineAmt,totalConsoleSubsidyAmt,totalConsoleCostAmt);
        return totalCost;
    }

    /**
     * 获得租车的总费用（不包括罚金）
     * @param orderNo
     * @param renterOrderNo
     * @param memNo
     * @return
     */
    public int getTotalRenterCostWithoutFine(String orderNo,String renterOrderNo,String memNo){
        int totalBsicRentCostAmt = orderCostDetailService.getTotalOrderCostAmt(orderNo,renterOrderNo);
        int totalSubsidyAmt = subsidyDetailService.getTotalRenterOrderSubsidyAmt(orderNo,renterOrderNo);
        int totalConsoleSubsidyAmt = consoleSubsidyDetailService.getTotalRenterOrderConsoleSubsidy(orderNo,memNo);
        int totalConsoleCostAmt = consoleCostDetailService.getTotalOrderConsoleCostAmt(orderNo,memNo);

        int totalCost = totalBsicRentCostAmt +totalSubsidyAmt+totalConsoleSubsidyAmt+totalConsoleCostAmt;
        logger.info("getTotalRenterCost[orderNo={},renterOrderNo={},memNo={}]==[totalBasicRentCostAmt={},totalSubsidyAmt={},totalConsoleSubsidyAmt={},totalConsoleCostAmt={}]",
                orderNo,renterOrderNo,memNo,totalBsicRentCostAmt,totalSubsidyAmt,totalConsoleSubsidyAmt,totalConsoleCostAmt);
        return totalCost;
    }
    
    
    ////全费用对象full
    public RenterCostDetailVO getRenterCostFullDetail(String orderNo, String renterOrderNo, String memNo){
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = orderCostDetailService.getRenterOrderCostDetailList(orderNo,renterOrderNo);
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatailEntityList=fineDeatailService.listRenterOrderFineDeatail(orderNo,renterOrderNo);
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatailEntityList = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(orderNo,memNo);

        RenterCostDetailVO basicCostDetailVO = new RenterCostDetailVO();

        basicCostDetailVO.setMemNo(memNo);
        basicCostDetailVO.setOrderNo(orderNo);
        basicCostDetailVO.setRentAmt(-RenterOrderCostDetailUtils.getRentAmt(renterOrderCostDetailEntityList));
        basicCostDetailVO.setInsuranceAmt(-RenterOrderCostDetailUtils.getInsuranceAmt(renterOrderCostDetailEntityList));
        basicCostDetailVO.setFee(-RenterOrderCostDetailUtils.getFeeAmt(renterOrderCostDetailEntityList));
        basicCostDetailVO.setAbatementInsuranceAmt(-RenterOrderCostDetailUtils.getAbatementInsuranceAmt(renterOrderCostDetailEntityList));
        basicCostDetailVO.setExtraDriverInsuranceAmt(-RenterOrderCostDetailUtils.getExtraDriverInsureAmt(renterOrderCostDetailEntityList));

        //取送车费用
        RenterDeliveryFeeDetailVO deliveryFeeDetailVO = getRenterDeliveryFeeDetail(renterOrderCostDetailEntityList);
        basicCostDetailVO.setDeliveryFeeDetail(deliveryFeeDetailVO);

        //罚金费用
        RenterFineVO renterFineVO = getRenterFineDetail(orderNo,renterOrderNo,memNo);
        basicCostDetailVO.setFineDetail(renterFineVO);

        //补贴费用
        RenterSubsidyDetailVO subsidyDetail = getRenterSubsidyDetail(orderNo,renterOrderNo,memNo);
        basicCostDetailVO.setSubsidyDetail(subsidyDetail);

        //违章费用
        RenterWzCostVO wzCostVO = wzCostFacadeService.getRenterWzCostDetail(orderNo);
        basicCostDetailVO.setWzCostDetail(wzCostVO);
        //违章押金
        WzDepositDetailVO wzDepositDetailVO = wzCostFacadeService.getWzDepostDetail(orderNo);
        basicCostDetailVO.setWzDepositDetailVO(wzDepositDetailVO);
        //押金
        DepostiDetailVO depostiDetailVO = cashierQueryService.getRenterDepositVO(orderNo,memNo);
        basicCostDetailVO.setDepostiDetailVO(depostiDetailVO);

        List<OrderSupplementDetailVO> orderSupplementDetailVOS = supplementService.listOrderSupplementDetailVOByOrderNo(orderNo);
        basicCostDetailVO.setSupplementDetailVOList(orderSupplementDetailVOS);
        //TODO: 超里程费用和油费没有计算
        return basicCostDetailVO;
    }

    public RenterSubsidyDetailVO getRenterSubsidyDetail(String orderNo, String renterOrderNo, String memNo){
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntityList = subsidyDetailService.listRenterOrderSubsidyDetail(orderNo,renterOrderNo);
        List<OrderConsoleSubsidyDetailEntity> consoleSubsidyDetailEntityList = consoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(orderNo,memNo);

        RenterSubsidyDetailVO detail = new RenterSubsidyDetailVO();

        detail.setPlatformCouponSubsidyAmt(-OrderSubsidyDetailUtils.getRenterPlatCouponSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setOwnerCouponSubsidyAmt(-OrderSubsidyDetailUtils.getRenterOwnerCouponSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setAutoCoinSubsidyAmt(-OrderSubsidyDetailUtils.getRenterAutoCoinSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setGetCarCouponSubsidyAmt(-OrderSubsidyDetailUtils.getRenterGetCarFeeSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setLimitTimeSubsidyAmt(-OrderSubsidyDetailUtils.getRenterRealLimitDeductSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setUpdateSubsidyAmt(-OrderSubsidyDetailUtils.getRenterUpateSubsidyAmt(renterOrderSubsidyDetailEntityList));

        detail.setRenter2OwnerSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST)));
        detail.setOwner2RenterSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST)));
        detail.setOwner2RenterRentSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT)));
        detail.setAbatementSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_ABATEMENT)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_ABATEMENT)));
        detail.setCleanCarSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_CLEANCAR)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_CLEANCAR)));
        detail.setDelaySubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_DELAY)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_DELAY)));
        detail.setFeeSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_FEE)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_FEE)));
        detail.setInsureSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_INSURE)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_INSURE)));
        detail.setOilSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OIL)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OIL)));
        detail.setOtherSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OTHER)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OTHER)));

        detail.setRentSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_RENTAMT)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_RENTAMT)));
        detail.setGetReturnDelaySubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_GETRETURNDELAY)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_GETRETURNDELAY)));
        detail.setTrafficSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_TRAFFIC)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_TRAFFIC)));
        detail.setRenter2PlatformAmt(OrderSubsidyDetailUtils.getRenterSubsidyByCode(renterOrderSubsidyDetailEntityList,SubsidySourceCodeEnum.RENTER,SubsidySourceCodeEnum.PLATFORM)+
                OrderSubsidyDetailUtils.getConsoleRenterSubsidyByCode(consoleSubsidyDetailEntityList,SubsidySourceCodeEnum.RENTER,SubsidySourceCodeEnum.PLATFORM));

        return detail;
    }

    public RenterFineVO getRenterFineDetail(String orderNo, String renterOrderNo, String memNo){
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatailEntityList=fineDeatailService.listRenterOrderFineDeatail(orderNo,renterOrderNo);
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatailEntityList = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(orderNo,memNo);

        RenterFineVO renterFineVO = new RenterFineVO();
        renterFineVO.setCancelFine(-(FineDetailUtils.getRenterOrderCancelFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleCancelFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setModifyGetFine(-(FineDetailUtils.getRenterOrderModifyGetFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleModifyGetFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setModifyReturnFine(-(FineDetailUtils.getRenterOrderModifyReturnFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleModifyReturnFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setModifyAdvanceFine(-(FineDetailUtils.getRenterOrderModifyAdvanceFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleModifyAdvanceFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setDelayFine(-(FineDetailUtils.getRenterOrderDelayFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleDelayFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setGetReturnCarFine(-(FineDetailUtils.getRenterOrderGetReturnCarFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleGetReturnCarFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setRenterAdvanceReturnFine(-(FineDetailUtils.getRenterOrderAdvanceReturnFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleAdvanceReturnFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setRenterDelayReturnFine(-(FineDetailUtils.getRenterOrderDelayReturnFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleDelayFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setTotalFine(renterFineVO.getTotalFine());
        return renterFineVO;
    }

    private RenterDeliveryFeeDetailVO getRenterDeliveryFeeDetail(List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList) {
        RenterDeliveryFeeDetailVO deliveryFeeDetailVO = new RenterDeliveryFeeDetailVO();
        deliveryFeeDetailVO.setSrvGetCostAmt(-RenterOrderCostDetailUtils.getSrvGetCostAmt(renterOrderCostDetailEntityList));
        deliveryFeeDetailVO.setSrvReturnCostAmt(-RenterOrderCostDetailUtils.getSrvReturnCostAmt(renterOrderCostDetailEntityList));
        deliveryFeeDetailVO.setGetBlockedRaiseAmt(-RenterOrderCostDetailUtils.getGetBlockedRaiseAmt(renterOrderCostDetailEntityList));
        deliveryFeeDetailVO.setReturnBlockedRaiseAmt(-RenterOrderCostDetailUtils.getReturnBlockedRaiseAmt(renterOrderCostDetailEntityList));
        return deliveryFeeDetailVO;
    }


    /**
     * 获取订单罚金总额
     * @param orderNo
     * @param renterOrderNo
     * @param memNo
     * @return
     */
    public int getTotalFine(String orderNo,String renterOrderNo,String memNo){
        int totalRenterOrderFineAmt = fineDeatailService.getTotalRenterOrderFineAmt(orderNo,renterOrderNo);
        int totalConsoleFineAmt = consoleRenterOrderFineDeatailService.getTotalConsoleFineAmt(orderNo,memNo);
        return totalRenterOrderFineAmt+totalConsoleFineAmt;
    }


    public RenterCostDetailDTO renterCostDetail(String orderNo,String renterOrderNo,String memNo) {
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = orderCostDetailService.getRenterOrderCostDetailList(orderNo,renterOrderNo);
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo, renterOrderNo);
        RenterFineVO renterFineVO = getRenterFineDetail(orderNo,renterOrderNo,memNo);
        RenterSubsidyDetailVO renterSubsidyDetail = getRenterSubsidyDetail(orderNo, renterOrderNo, memNo);
        int rentWalletAmt = cashierSettleService.getRentCostPayByWallet(orderNo, memNo);
        RenterCostVO renterCostVO = orderSettleService.getRenterCostByOrderNo(orderNo);
        RentCosts rentCost = orderSettleService.preRenterSettleOrder(orderNo,renterOrderNo);
        String oilDifferenceCrash = rentCost.getOilAmt().getOilDifferenceCrash();
        oilDifferenceCrash = StringUtil.isBlank(oilDifferenceCrash)?"0":oilDifferenceCrash;
        int extraMileageFee = (rentCost == null || rentCost.getMileageAmt() == null)?0:rentCost.getMileageAmt().getTotalFee();
        int oilFee = (int)Float.parseFloat(oilDifferenceCrash);
        //1、租车费用
        RentCarCostDTO rentCarCostDTO = new RentCarCostDTO();
        //1.1、基础费用
        BaseCostDTO baseCostDTO = new BaseCostDTO();
        baseCostDTO.renterAmt = abs(renterOrderCostEntity.getRentCarAmount());
        baseCostDTO.serviceFee = abs(RenterOrderCostDetailUtils.getFeeAmt(renterOrderCostDetailEntityList));
        baseCostDTO.basicGuaranteeFee = abs(renterOrderCostEntity.getBasicEnsureAmount());
        baseCostDTO.allGuaranteeFee = abs(renterOrderCostEntity.getComprehensiveEnsureAmount());
        baseCostDTO.driverInsurance = abs(renterOrderCostEntity.getAdditionalDrivingEnsureAmount());
        baseCostDTO.distributionCost = abs(RenterOrderCostDetailUtils.getDistributionCost(renterOrderCostDetailEntityList));
        baseCostDTO.penaltyBreachContract = renterFineVO.getTotalFine();
        baseCostDTO.extraMileageFee = abs(extraMileageFee);
        baseCostDTO.oilFee = abs(oilFee);
        baseCostDTO.payToPlatFormFee = abs(renterSubsidyDetail.getRenter2PlatformAmt());
        baseCostDTO.renterOWnerAdjustmentFee = abs(renterSubsidyDetail.getRenter2OwnerSubsidyAmt() + renterSubsidyDetail.getOwner2RenterSubsidyAmt());
        rentCarCostDTO.baseCostDTO = baseCostDTO;
        //1.2、优惠券抵扣
        CouponDeductionDTO couponDeductionDTO = new CouponDeductionDTO();
        couponDeductionDTO.ownerCouponRealDeduction = renterSubsidyDetail.getOwnerCouponSubsidyAmt();
        couponDeductionDTO.platFormCouponRealDeduction = renterSubsidyDetail.getPlatformCouponSubsidyAmt();
        couponDeductionDTO.deliveryCouponRealDeduction = renterSubsidyDetail.getGetCarCouponSubsidyAmt();
        couponDeductionDTO.walletBalanceRealDeduction = rentWalletAmt;
        couponDeductionDTO.autoCoinSubsidyAmt = renterSubsidyDetail.getAutoCoinSubsidyAmt();
        rentCarCostDTO.couponDeductionDTO = couponDeductionDTO;
        //1.3、平台补贴
        PlatformSubsidyDTO platformSubsidyDTO = new PlatformSubsidyDTO();
        platformSubsidyDTO.updateCarSubsidy = renterSubsidyDetail.getUpdateSubsidyAmt();
        platformSubsidyDTO.oilSubsidySubsidy = renterSubsidyDetail.getOilSubsidyAmt();
        platformSubsidyDTO.carWashSubsidy = renterSubsidyDetail.getCleanCarSubsidyAmt();
        platformSubsidyDTO.deliveryLateSubsidy = renterSubsidyDetail.getGetReturnDelaySubsidyAmt();
        platformSubsidyDTO.delaySubsidy = renterSubsidyDetail.getDelaySubsidyAmt();
        platformSubsidyDTO.carFeeSubsidy = renterSubsidyDetail.getTrafficSubsidyAmt();
        platformSubsidyDTO.premiumSubsidy = renterSubsidyDetail.getInsureSubsidyAmt();
        platformSubsidyDTO.comprehensiveEnsureSubsidy = renterSubsidyDetail.getAbatementSubsidyAmt();
        platformSubsidyDTO.rentSubsidy = renterSubsidyDetail.getRentSubsidyAmt();
        platformSubsidyDTO.ServiceSubsidy = renterSubsidyDetail.getFeeSubsidyAmt();
        platformSubsidyDTO.otherSubsidy = renterSubsidyDetail.getOtherSubsidyAmt();
        rentCarCostDTO.platformSubsidyDTO = platformSubsidyDTO;
        //1.4、车主给租客的补贴
        rentCarCostDTO.rentAmtSubsidy =  renterSubsidyDetail.getOwner2RenterRentSubsidyAmt();
        //1.5、统计
        CostStatisticsDTO rentCarCostStatics = new CostStatisticsDTO();
        rentCarCostStatics.shouldReceiveAmt = renterCostVO.getRenterCostYingshou();
        rentCarCostStatics.realReceiveAmt = renterCostVO.getRenterCostShishou();
        rentCarCostStatics.shouldRetreatAmt = renterCostVO.getRenterCost();
        rentCarCostStatics.shouldDeductionAmt = 0;//TODO 待计算
        rentCarCostStatics.realRetreatAmt = renterCostVO.getRenterCostReal();
        rentCarCostStatics.realDeductionAmt = 0;//TODO 待计算
        rentCarCostDTO.costStatisticsDTO = rentCarCostStatics;

        //2、车辆押金
        CarDepositDTO carDepositDTO = new CarDepositDTO();
        RenterDepositDetailEntity renterDepositDetailEntity = renterDepositDetailService.queryByOrderNo(orderNo);
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetailList = accountRenterCostSettleService.getAccountRenterCostSettleDetail(orderNo);
        //2.1、基础费用
        carDepositDTO.carDeposit = abs(renterDepositDetailEntity.getOriginalDepositAmt());
        carDepositDTO.platformTaskRelief = renterDepositDetailEntity.getReductionDepositAmt();
        carDepositDTO.expDeductionRentAmt = 0;
        carDepositDTO.DeductionRentHistoricalAmt = AccountSettleUtils.getDepositSettleDeductionDebtAmt(accountRenterCostSettleDetailList,RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT);
        //2.2、统计
        CostStatisticsDTO carDepositStatisticsDTO = new CostStatisticsDTO();
        carDepositStatisticsDTO.shouldReceiveAmt = renterCostVO.getDepositCostYingfu();
        carDepositStatisticsDTO.realReceiveAmt = renterCostVO.getDepositCostShifu();
        carDepositStatisticsDTO.freeAmt = getCarDepositFreeAmt(orderNo);
        carDepositStatisticsDTO.shouldRetreatAmt = renterCostVO.getDepositCost();
        carDepositStatisticsDTO.shouldDeductionAmt = 0;//TODO 待计算
        carDepositStatisticsDTO.realRetreatAmt = renterCostVO.getDepositCostReal();
        carDepositStatisticsDTO.realDeductionAmt = 0;//TODO 待计算
        carDepositDTO.costStatisticsDTO = carDepositStatisticsDTO;

        //3、违章押金
        WzDepositDetailVO wzDepositDetailVO = wzCostFacadeService.getWzDepostDetail(orderNo);
        RenterWzCostVO wzCostVO = wzCostFacadeService.getRenterWzCostDetail(orderNo);
        WzDepositDTO wzDepositDTO = new WzDepositDTO();
        //3.1、基础费用
        wzDepositDTO.wzDepositAmt = abs(wzDepositDetailVO.getYingshouDeposit());
        wzDepositDTO.wzFineAmt = wzCostVO.getWzFineAmt();
        wzDepositDTO.wzServiceCostAmt = wzCostVO.getWzServiceCostAmt();
        wzDepositDTO.wzDysFineAmt = wzCostVO.getWzDysFineAmt();
        wzDepositDTO.wzStopCostAmt = wzCostVO.getWzStopCostAmt();
        wzDepositDTO.wzOtherAmt = wzCostVO.getWzOtherAmt();
        wzDepositDTO.wzInsuranceAmt = wzCostVO.getWzInsuranceAmt();
        wzDepositDTO.expDeductionRentCarAmt = 0;
        wzDepositDTO.deductionRentHistoricalAmt = AccountSettleUtils.getDepositSettleDeductionDebtAmt(accountRenterCostSettleDetailList,RenterCashCodeEnum.SETTLE_WZ_TO_HISTORY_AMT);
        //3.2、统计
        CostStatisticsDTO wzCostStatisticsDTO = new CostStatisticsDTO();
        wzCostStatisticsDTO.shouldReceiveAmt = wzDepositDetailVO.getYingshouDeposit();
        wzCostStatisticsDTO.freeAmt = getWzDepositFreeAmt(orderNo);
        wzCostStatisticsDTO.realReceiveAmt = wzDepositDetailVO.getShishouDeposit();
        wzCostStatisticsDTO.shouldDeductionAmt = 0;//TODO 待计算
        wzCostStatisticsDTO.shouldRetreatAmt = wzDepositDetailVO.getShouldReturnDeposit();
        wzCostStatisticsDTO.realRetreatAmt = wzDepositDetailVO.getRealReturnDeposit();
        wzCostStatisticsDTO.realDeductionAmt = 0;//TODO 待计算
        wzDepositDTO.costStatisticsDTO = wzCostStatisticsDTO;

        //4、租车费用结算后补付
        int needIncrementAmt = cashierPayService.getRentCostBufu(orderNo, renterOrderNo);
        SettleMakeUpDTO settleMakeUpDTO = new SettleMakeUpDTO();
        settleMakeUpDTO.shouldReveiveAmt = Math.abs(needIncrementAmt);
        settleMakeUpDTO.realReveiveAmt = renterCostVO.getRenterCostBufuShifu();

        //预计抵扣的租车费用
        DeductionRentAmtDTO deductionRentAmtDTO = deductionCarDdeposit(rentCarCostStatics, carDepositStatisticsDTO.realReceiveAmt, wzCostStatisticsDTO.realReceiveAmt);
        carDepositDTO.expDeductionRentAmt = deductionRentAmtDTO.getCarDepositDeductionAmt();
        wzDepositDTO.expDeductionRentCarAmt = deductionRentAmtDTO.getWzDepositDeductionAmt();

        //5、统计费用
        CostStatisticsDTO costStatisticsDTO = getCostStatistics(rentCarCostStatics, carDepositStatisticsDTO, wzCostStatisticsDTO,settleMakeUpDTO);
        RenterCostDetailDTO renterCostDetailDTO = new RenterCostDetailDTO();
        renterCostDetailDTO.costStatisticsDTO = costStatisticsDTO;
        renterCostDetailDTO.rentCarCostDTO = rentCarCostDTO;
        renterCostDetailDTO.carDepositDTO = carDepositDTO;
        renterCostDetailDTO.wzDepositDTO = wzDepositDTO;
        renterCostDetailDTO.settleMakeUpDTO = settleMakeUpDTO;
        return renterCostDetailDTO;
    }

    public CostStatisticsDTO getCostStatistics(CostStatisticsDTO rentCar,CostStatisticsDTO carDeposit,CostStatisticsDTO wz,SettleMakeUpDTO settleMakeUpDTO){
        CostStatisticsDTO costStatisticsDTO = new CostStatisticsDTO();
        costStatisticsDTO.shouldReceiveAmt = rentCar.shouldReceiveAmt + carDeposit.shouldReceiveAmt + wz.shouldReceiveAmt + settleMakeUpDTO.shouldReveiveAmt;
        costStatisticsDTO.realReceiveAmt = rentCar.realReceiveAmt + carDeposit.realReceiveAmt + wz.realReceiveAmt + settleMakeUpDTO.realReveiveAmt;
        costStatisticsDTO.freeAmt = carDeposit.freeAmt + wz.freeAmt;
        costStatisticsDTO.shouldDeductionAmt = rentCar.shouldDeductionAmt + carDeposit.shouldDeductionAmt + wz.shouldDeductionAmt;
        costStatisticsDTO.shouldRetreatAmt = rentCar.shouldRetreatAmt + carDeposit.shouldRetreatAmt + wz.shouldRetreatAmt;
        costStatisticsDTO.realRetreatAmt = rentCar.realRetreatAmt + carDeposit.realRetreatAmt + wz.realRetreatAmt;
        costStatisticsDTO.realDeductionAmt = rentCar.realDeductionAmt + carDeposit.realDeductionAmt + wz.realDeductionAmt;
        return costStatisticsDTO;
    }

    /**
     * 获取车辆芝麻/信用卡免押金额
     * @return
     */
    private int getCarDepositFreeAmt(String orderNo){
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositNoTService.queryDeposit(orderNo);
        if(accountRenterDepositEntity == null){
            return 0;
        }
        if(FreeDepositTypeEnum.SESAME_FREE.getCode().equals(accountRenterDepositEntity.getFreeDepositType()) || FreeDepositTypeEnum.BIND_CARD_FREE.getCode().equals(accountRenterDepositEntity.getFreeDepositType())){
            return accountRenterDepositEntity.getReductionAmt();
        }
        return 0;
    }

    /**
     * 获取违章芝麻/信用卡免押金额
     * @return
     */
    private int getWzDepositFreeAmt(String orderNo){
        AccountRenterWzDepositEntity accountRenterWZDepositByOrder = accountRenterWzDepositNoTService.getAccountRenterWZDepositByOrder(orderNo);
        if(accountRenterWZDepositByOrder == null){
            return 0;
        }
        if(FreeDepositTypeEnum.SESAME_FREE.getCode().equals(accountRenterWZDepositByOrder.getFreeDepositType()) || FreeDepositTypeEnum.BIND_CARD_FREE.getCode().equals(accountRenterWZDepositByOrder.getFreeDepositType())){
            return 0;
        }
        return 0;
    }

    /**
     * 取绝对值
     * @param amt
     * @return
     */
    private static int abs(Integer amt){
        if(amt == null){
            return 0;
        }
        if(amt>=0){
            return amt;
        }
        return -amt;
    }

    /**
     * 车辆押金抵预计抵扣的租车费用
     * 违章押金预计抵扣租车费用
     * @return
     */
    private DeductionRentAmtDTO deductionCarDdeposit(CostStatisticsDTO rentCarCostStatics,int depositCostShifu,int realReceiveAmt){
        DeductionRentAmtDTO deductionRentAmtDTO = new DeductionRentAmtDTO();
        int difference = rentCarCostStatics.shouldReceiveAmt - rentCarCostStatics.realReceiveAmt;
        if(difference < 0){
            if(depositCostShifu >= abs(difference)){
                deductionRentAmtDTO.setCarDepositDeductionAmt(difference);
            }else{
                deductionRentAmtDTO.setCarDepositDeductionAmt(depositCostShifu);
                int wzDiff = abs(difference) - abs(depositCostShifu);
                if(realReceiveAmt >= wzDiff){
                    deductionRentAmtDTO.setWzDepositDeductionAmt(wzDiff);
                }else{
                    deductionRentAmtDTO.setWzDepositDeductionAmt(realReceiveAmt);
                }
            }
        }
        return deductionRentAmtDTO;
    }
}

package com.atzuche.order.coreapi.service;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositDetailNoTService;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositNoTService;
import com.atzuche.order.accountrenterdeposit.utils.AccountRenterDepositUtils;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.accountrenterwzdepost.utils.AccountRenterWzDepositUtils;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.commons.NumberUtils;
import com.atzuche.order.commons.entity.rentCost.*;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.DepostiDetailVO;
import com.atzuche.order.commons.vo.OrderSupplementDetailVO;
import com.atzuche.order.commons.vo.WzDepositDetailVO;
import com.atzuche.order.commons.vo.res.*;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.rentercost.service.*;
import com.atzuche.order.rentercost.utils.FineDetailUtils;
import com.atzuche.order.rentercost.utils.OrderSubsidyDetailUtils;
import com.atzuche.order.rentercost.utils.RenterOrderCostDetailUtils;
import com.atzuche.order.renterorder.entity.OwnerCouponLongEntity;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.service.OwnerCouponLongService;
import com.atzuche.order.renterorder.service.OwnerCouponLongService;
import com.atzuche.order.renterorder.service.RenterDepositDetailService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.res.RenterCostVO;
import com.autoyol.doc.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private AccountRenterDepositDetailNoTService accountRenterDepositDetailNoTService;
    @Autowired
    private AccountRenterWzDepositDetailNoTService accountRenterWzDepositDetailNoTService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderConsoleCostDetailService orderConsoleCostDetailService;
    @Autowired
    private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
    @Autowired
    private OwnerCouponLongService ownerCouponLongService;

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
        //List<RenterOrderFineDeatailEntity> renterOrderFineDeatailEntityList=fineDeatailService.listRenterOrderFineDeatail(orderNo,renterOrderNo);
        //List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatailEntityList = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(orderNo,memNo);

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
        detail.setUpdateSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterUpateSubsidyAmt(renterOrderSubsidyDetailEntityList) + OrderSubsidyDetailUtils.getConsoleRenterUpateSubsidyAmt(consoleSubsidyDetailEntityList)));

        detail.setRenter2OwnerSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.OWNER,RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST)));
        detail.setOwner2RenterSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST)));
        detail.setOwner2RenterRentSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT)));
        detail.setAbatementSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.ABATEMENT_INSURE)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_ABATEMENT)));
        detail.setCleanCarSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_CLEANCAR)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_CLEANCAR)));
        detail.setDelaySubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_DELAY)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_DELAY)));
        detail.setFeeSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_FEE)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_FEE)));
        detail.setInsureSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.INSURE_TOTAL_PRICES)+
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

        List<OrderConsoleCostDetailEntity> orderConsoleCostDetailEntityList = orderConsoleCostDetailService.selectByOrderNoAndMemNo(orderNo, memNo);
        detail.setRenter2PlatformAmt(OrderSubsidyDetailUtils.getOrderConsoleCostDetail(orderConsoleCostDetailEntityList,SubsidySourceCodeEnum.RENTER,SubsidySourceCodeEnum.PLATFORM));

        detail.setLongRentDecutAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.RENT_AMT)));
        detail.setLongGetReturnCarCostSubsidy(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SRV_GET_COST) +
                OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SRV_RETURN_COST) +
                OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT) +
                OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT)));

        detail.setSrvGetCostAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SRV_GET_COST)+OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT)));
        detail.setSrvGetCostAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SRV_RETURN_COST)+OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT)));
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
        //renterFineVO.setRenterDelayReturnFine(-(FineDetailUtils.getRenterOrderDelayReturnFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleDelayFineAmt(consoleRenterOrderFineDeatailEntityList)));
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


    /**
     * mock
     * @param orderNo
     * @param renterOrderNo
     * @param memNo
     * @return
     */
    public RenterCostVO renterCostShishouDetail(String orderNo,String renterOrderNo,String memNo) {
//    	RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo, renterOrderNo);
    	RentCosts rentCost = orderSettleService.preRenterSettleOrder(orderNo,renterOrderNo);
    	RenterCostVO renterCostVO = orderSettleService.getRenterCostByOrderNo(orderNo,renterOrderNo,memNo,rentCost.getRenterCostAmtFinal());
    	return renterCostVO;
    }

    public RenterCostDetailDTO renterCostDetail(String orderNo,String renterOrderNo,String memNo) {
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if(orderStatusEntity == null){
            logger.error("订单状态信息不存在orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = orderCostDetailService.getRenterOrderCostDetailList(orderNo,renterOrderNo);
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo, renterOrderNo);
        RenterFineVO renterFineVO = getRenterFineDetail(orderNo,renterOrderNo,memNo);
        RenterSubsidyDetailVO renterSubsidyDetail = getRenterSubsidyDetail(orderNo, renterOrderNo, memNo);
        int rentWalletAmt = cashierSettleService.getRentCostPayByWallet(orderNo, memNo);
        RentCosts rentCost = orderSettleService.preRenterSettleOrder(orderNo,renterOrderNo);
        RenterCostVO renterCostVO = orderSettleService.getRenterCostByOrderNo(orderNo,renterOrderNo,renterOrderCostEntity.getMemNo(),rentCost.getRenterCostAmtFinal());
        RenterWzCostVO wzCostVO = wzCostFacadeService.getRenterWzCostDetail(orderNo);
        RenterGetAndReturnCarDTO oilAmt = rentCost.getOilAmt();
        String oilDifferenceCrash = (oilAmt==null||rentCost.getOilAmt()==null||rentCost.getOilAmt().getOilDifferenceCrash()==null)?"0":rentCost.getOilAmt().getOilDifferenceCrash();
        int oilFee = (int)Float.parseFloat(oilDifferenceCrash);
        int extraMileageFee = (rentCost == null || rentCost.getMileageAmt() == null)?0:rentCost.getMileageAmt().getTotalFee();

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
        baseCostDTO.oilFee = -oilFee;
        baseCostDTO.payToPlatFormFee = abs(renterSubsidyDetail.getRenter2PlatformAmt());
        baseCostDTO.renterOWnerAdjustmentFee = renterSubsidyDetail.getRenter2OwnerSubsidyAmt() + renterSubsidyDetail.getOwner2RenterSubsidyAmt();
        rentCarCostDTO.baseCostDTO = baseCostDTO;
        //1.2、优惠券抵扣
        CouponDeductionDTO couponDeductionDTO = new CouponDeductionDTO();
        couponDeductionDTO.ownerCouponRealDeduction = renterSubsidyDetail.getOwnerCouponSubsidyAmt();
        couponDeductionDTO.platFormCouponRealDeduction = renterSubsidyDetail.getPlatformCouponSubsidyAmt();
        couponDeductionDTO.deliveryCouponRealDeduction = renterSubsidyDetail.getGetCarCouponSubsidyAmt();
        couponDeductionDTO.walletBalanceRealDeduction = rentWalletAmt;
        couponDeductionDTO.autoCoinSubsidyAmt = renterSubsidyDetail.getAutoCoinSubsidyAmt();
        //长租折扣字段
        longRentDeduct(couponDeductionDTO,renterOrderNo,renterSubsidyDetail);

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
        platformSubsidyDTO.longGetReturnCarCostSubsidy = renterSubsidyDetail.getLongGetReturnCarCostSubsidy();
        rentCarCostDTO.platformSubsidyDTO = platformSubsidyDTO;
        //1.4、车主给租客的补贴
        rentCarCostDTO.rentAmtSubsidy =  renterSubsidyDetail.getOwner2RenterRentSubsidyAmt();
        //1.5、统计
        CostStatisticsDTO rentCarCostStatics = new CostStatisticsDTO();
        rentCarCostStatics.shouldReceiveAmt = renterCostVO.getRenterCostFeeYingshou();
        rentCarCostStatics.realReceiveAmt = renterCostVO.getRenterCostFeeShishou();
        rentCarCostStatics.shouldRetreatAmt = renterCostVO.getRenterCostFeeYingtui();
        rentCarCostStatics.shouldDeductionAmt = renterCostVO.getRenterCostFeeYingkou();
        rentCarCostStatics.realRetreatAmt = renterCostVO.getRenterCostFeeShitui();
        rentCarCostStatics.realDeductionAmt = renterCostVO.getRenterCostFeeShikou();
        rentCarCostDTO.costStatisticsDTO = rentCarCostStatics;

        //2、车辆押金
        CarDepositDTO carDepositDTO = new CarDepositDTO();
        RenterDepositDetailEntity renterDepositDetailEntity = renterDepositDetailService.queryByOrderNo(orderNo);

        //2.1、基础费用
        int carExpAndActFlg = 1;
        int carDeductionRentAmt = 0;//车辆抵扣的租车费用
        int carDeductionRentHistoricalAmt = 0;
        if(orderStatusEntity.getSettleStatus() == SettleStatusEnum.SETTLED.getCode()){//车辆押金已经结算
            carExpAndActFlg = 2;
            //车辆押金
            List<AccountRenterDepositDetailEntity> accountRenterDepositDetailEntities = accountRenterDepositDetailNoTService.findByOrderNo(orderNo);
            carDeductionRentAmt = AccountRenterDepositUtils.getDepositSettleDeductionDebtAmt(accountRenterDepositDetailEntities, RenterCashCodeEnum.SETTLE_DEPOSIT_TO_RENT_COST);
            carDeductionRentHistoricalAmt = AccountRenterDepositUtils.getDepositSettleDeductionDebtAmt(accountRenterDepositDetailEntities,RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT);
        }else{//未结算
            carDeductionRentAmt = renterCostVO.getDepositCostYingkou();
        }
        carDepositDTO.carDeposit = abs(renterDepositDetailEntity.getOriginalDepositAmt());
        carDepositDTO.platformTaskRelief = renterDepositDetailEntity.getReductionDepositAmt();
        carDepositDTO.DeductionRentHistoricalAmt = Math.abs(carDeductionRentHistoricalAmt);
        carDepositDTO.expDeductionRentAmt = Math.abs(carDeductionRentAmt);
        carDepositDTO.expAndActFlg = carExpAndActFlg;
        carDepositDTO.isDetain = orderStatusEntity.getIsDetain();

        //2.2、统计
        CostStatisticsDTO carDepositStatisticsDTO = new CostStatisticsDTO();
        carDepositStatisticsDTO.shouldReceiveAmt = renterCostVO.getDepositCostYingshou();
        carDepositStatisticsDTO.realReceiveAmt = renterCostVO.getDepositCostShishou();
        carDepositStatisticsDTO.freeAmt = renterCostVO.getDepositCostShishouAuth();
        carDepositStatisticsDTO.shouldRetreatAmt = renterCostVO.getDepositCostYingtui();
        carDepositStatisticsDTO.shouldDeductionAmt = renterCostVO.getDepositCostYingkou();
        carDepositStatisticsDTO.realRetreatAmt = renterCostVO.getDepositCostShitui();
        carDepositStatisticsDTO.realDeductionAmt = renterCostVO.getDepositCostShikou();
        carDepositDTO.costStatisticsDTO = carDepositStatisticsDTO;


        //3、违章押金
        //3.1、基础费用
        WzDepositDTO wzDepositDTO = new WzDepositDTO();
        wzDepositDTO.wzDepositAmt = renterCostVO.getDepositWzCostYingshou();
        wzDepositDTO.wzFineAmt = wzCostVO.getWzFineAmt();
        wzDepositDTO.wzServiceCostAmt = wzCostVO.getWzServiceCostAmt();
        wzDepositDTO.wzDysFineAmt = wzCostVO.getWzDysFineAmt();
        wzDepositDTO.wzStopCostAmt = wzCostVO.getWzStopCostAmt();
        wzDepositDTO.wzOtherAmt = wzCostVO.getWzOtherAmt();
        wzDepositDTO.wzInsuranceAmt = wzCostVO.getWzInsuranceAmt();
        int wzDeductionRentAmt = 0;//违章抵扣的租车费用
        int wzDeductionRentHistoricalAmt = 0;
        int wzExpAndActFlg = 1;
        if(orderStatusEntity.getWzSettleStatus() == 1){//违章押金已经结算
            wzExpAndActFlg = 2;
            List<AccountRenterWzDepositDetailEntity> byOrderNo = accountRenterWzDepositDetailNoTService.findByOrderNo(orderNo);
            wzDeductionRentHistoricalAmt = AccountRenterWzDepositUtils.getWzDepositSettleDeductionDebtAmt(byOrderNo,RenterCashCodeEnum.SETTLE_WZ_TO_HISTORY_AMT);
            wzDepositDTO.deductionRentHistoricalAmt = Math.abs(wzDeductionRentHistoricalAmt);
            wzDeductionRentAmt = Math.abs(renterCostVO.getDepositWzCostShikou()) <= 0 ? 0:Math.abs(renterCostVO.getDepositWzCostShikou()) - getOther(wzDepositDTO);
        }else{
            wzDepositDTO.deductionRentHistoricalAmt = wzDeductionRentHistoricalAmt;
            wzDeductionRentAmt = renterCostVO.getDepositWzCostYingkou() <= 0 ? 0 : renterCostVO.getDepositWzCostYingkou()- getOther(wzDepositDTO);
        }
        wzDepositDTO.expAndActFlg = wzExpAndActFlg;

        wzDepositDTO.expDeductionRentCarAmt = wzDeductionRentAmt;
        //3.2、统计
        CostStatisticsDTO wzCostStatisticsDTO = new CostStatisticsDTO();
        wzCostStatisticsDTO.shouldReceiveAmt = renterCostVO.getDepositWzCostYingshou();
        wzCostStatisticsDTO.freeAmt = renterCostVO.getDepositWzCostShishouAuth();
        wzCostStatisticsDTO.realReceiveAmt = renterCostVO.getDepositWzCostShishou();
        wzCostStatisticsDTO.shouldRetreatAmt = renterCostVO.getDepositWzCostYingtui();
        wzCostStatisticsDTO.realRetreatAmt = renterCostVO.getDepositWzCostShitui();
        wzCostStatisticsDTO.shouldDeductionAmt = renterCostVO.getDepositWzCostYingkou();
        wzCostStatisticsDTO.realDeductionAmt = renterCostVO.getDepositWzCostShikou();
        wzDepositDTO.costStatisticsDTO = wzCostStatisticsDTO;
        wzDepositDTO.isWzDetain = orderStatusEntity.getIsDetainWz();

        //4、租车费用结算后补付
        SettleMakeUpDTO settleMakeUpDTO = new SettleMakeUpDTO();
        settleMakeUpDTO.shouldReveiveAmt = renterCostVO.getRenterCostBufuYingshou()>=0?0:NumberUtils.convertNumberToZhengshu(renterCostVO.getRenterCostBufuYingshou());
        settleMakeUpDTO.realReveiveAmt = Math.abs(renterCostVO.getRenterCostBufuShishou());


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
    private void longRentDeduct( CouponDeductionDTO couponDeductionDTO,String renterOrderNo,RenterSubsidyDetailVO renterSubsidyDetail) {
        OwnerCouponLongEntity ownerCouponLongEntity = ownerCouponLongService.getByRenterOrderNo(renterOrderNo);

        if(ownerCouponLongEntity != null){
            couponDeductionDTO.setOwnerLongRentDeduct(ownerCouponLongEntity.getDiscountDesc());
            couponDeductionDTO.setOwnerLongRentDeductAmt(renterSubsidyDetail.getLongRentDecutAmt());
        }
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

    public Integer getOther(WzDepositDTO wzDepositDTO){
        return
                nullToSero(wzDepositDTO.wzFineAmt)+
                        nullToSero(wzDepositDTO.wzServiceCostAmt)+
                        nullToSero(wzDepositDTO.wzDysFineAmt)+
                        nullToSero(wzDepositDTO.wzStopCostAmt)+
                        nullToSero(wzDepositDTO.wzOtherAmt)+
                        nullToSero(wzDepositDTO.wzInsuranceAmt)+
                        nullToSero(wzDepositDTO.deductionRentHistoricalAmt);
    }

    private int nullToSero(Integer amt){
        if(amt == null){
            return 0;
        }
        return amt;
    }
}

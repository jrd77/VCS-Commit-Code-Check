package com.atzuche.order.admin.service;

import com.atzuche.order.accountrenterdeposit.exception.AccountRenterDepositDBException;
import com.atzuche.order.admin.common.AdminUser;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.vo.req.renterWz.CarDepositTemporaryRefundReqVO;
import com.atzuche.order.admin.vo.req.renterWz.RenterWzCostDetailReqVO;
import com.atzuche.order.admin.vo.req.renterWz.TemporaryRefundReqVO;
import com.atzuche.order.admin.vo.resp.renterWz.*;
import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.cashieraccount.vo.res.WzDepositMsgResVO;
import com.atzuche.order.commons.CompareHelper;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.NumberUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.enums.detain.DetailSourceEnum;
import com.atzuche.order.detain.dto.CarDepositTemporaryRefundReqDTO;
import com.atzuche.order.detain.service.RenterDetain;
import com.atzuche.order.detain.vo.RenterDetainVO;
import com.atzuche.order.detain.vo.UnfreezeRenterDetainVO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.entity.WzCostLogEntity;
import com.atzuche.order.renterwz.entity.WzTemporaryRefundLogEntity;
import com.atzuche.order.renterwz.enums.WzCostEnums;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.renterwz.service.WzCostLogService;
import com.atzuche.order.renterwz.service.WzTemporaryRefundLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * RenterWzService
 *
 * @author shisong
 * @date 2020/1/6
 */
@Service
@Slf4j
public class RenterWzService {

    @Resource
    private RenterOrderWzCostDetailService renterOrderWzCostDetailService;

    @Resource
    private WzCostLogService wzCostLogService;

    @Resource
    private RenterGoodsService renterGoodsService;

    @Resource
    private RenterMemberService renterMemberService;

    @Resource
    private WzTemporaryRefundLogService wzTemporaryRefundLogService;

    @Resource
    private CashierQueryService cashierQueryService;

    @Resource
    private OrderStatusService orderStatusService;

    @Resource
    private RenterOrderService renterOrderService;

    @Resource
    private RenterDetain renterDetain;

    private static final String WZ_OTHER_FINE_REMARK = "其他扣款备注";
    private static final String WZ_OTHER_FINE = "其他扣款";
    private static final String WZ_OTHER_FINE_CODE = "11240044";

    private static final String INSURANCE_CLAIM_REMARK = "保险理赔备注";
    private static final String INSURANCE_CLAIM = "保险理赔";
    private static final String INSURANCE_CLAIM_CODE = "11240045";

    private static final String REMARK = "remark";
    private static final String AMOUNT = "amount";

    private static final String SOURCE_TYPE_CONSOLE = "2";

    private static final String RADIX_POINT = ".";

    private static final List<String> COST_CODE_LIST = Arrays.asList("11240040","11240041","11240042","11240043","11240044","11240045");


    public void updateWzCost(String orderNo, List<RenterWzCostDetailReqVO> costDetails) {
        OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(orderNo);
        if(orderStatus != null && orderStatus.getWzSettleStatus() != null && orderStatus.getWzSettleStatus().equals(1)){
            throw new AccountRenterDepositDBException(ErrorCode.RENTER_WZ_SETTLED.getCode(),ErrorCode.RENTER_WZ_SETTLED.getText());
        }
        //只会处理其他扣款 和 保险理赔
        for (RenterWzCostDetailReqVO costDetail : costDetails) {
            if(!WZ_OTHER_FINE_CODE.equals(costDetail.getCostCode()) && !INSURANCE_CLAIM_CODE.equals(costDetail.getCostCode())){
                continue;
            }
            RenterOrderWzCostDetailEntity fromDb = renterOrderWzCostDetailService.queryInfoByOrderAndCode(orderNo, costDetail.getCostCode());
            try {
                RenterOrderWzCostDetailEntity fromApp = new RenterOrderWzCostDetailEntity();
                BeanUtils.copyProperties(costDetail,fromApp);
                if(StringUtils.isNotBlank(costDetail.getAmount())){
                    fromApp.setAmount(Integer.parseInt(costDetail.getAmount()));
                }
                Map<String,String> paramNames = this.getParamNamesByCode(costDetail.getCostCode());
                CompareHelper<RenterOrderWzCostDetailEntity> compareHelper = new CompareHelper<>(fromDb,fromApp,paramNames);
                String content = compareHelper.compare();
                if(StringUtils.isNotBlank(content)){
                    //记录日志 并且做修改费用处理
                    updateCostStatus(orderNo, costDetail, fromDb);
                    saveWzCostLog(orderNo, costDetail, content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCostStatus(String orderNo, RenterWzCostDetailReqVO costDetail, RenterOrderWzCostDetailEntity fromDb) {
        String carNum;
        Integer memNo = null;
        if(fromDb != null){
            carNum = fromDb.getCarPlateNum();
            memNo = fromDb.getMemNo();
        }else{
            carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
            String renterNoByOrderNo = renterMemberService.getRenterNoByOrderNo(orderNo);
            if(StringUtils.isNotBlank(renterNoByOrderNo)){
                memNo = Integer.parseInt(renterNoByOrderNo);
            }
        }
        //先将之前的置为无效
        renterOrderWzCostDetailService.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,1,costDetail.getCostCode());
        //再新添加
        RenterOrderWzCostDetailEntity entityByType = getEntityByType(costDetail.getCostCode(), orderNo, costDetail.getAmount(), carNum, memNo,costDetail.getRemark());
        renterOrderWzCostDetailService.saveRenterOrderWzCostDetail(entityByType);
    }

    private RenterOrderWzCostDetailEntity getEntityByType(String code, String orderNo, String amount, String carNum, Integer memNo, String remark){
        String authName = AdminUserUtil.getAdminUser().getAuthName();
        String authId = AdminUserUtil.getAdminUser().getAuthId();
        RenterOrderWzCostDetailEntity entity = new RenterOrderWzCostDetailEntity();
        entity.setOrderNo(orderNo);
        entity.setCarPlateNum(carNum);
        if(StringUtils.isNotBlank(amount)){
            entity.setAmount(Integer.parseInt(amount));
        }else{
            entity.setAmount(0);
        }
        entity.setMemNo(memNo);
        entity.setCostCode(code);
        entity.setCostDesc(WzCostEnums.getDesc(code));
        entity.setCreateTime(new Date());
        entity.setSourceType(SOURCE_TYPE_CONSOLE);
        entity.setOperatorName(authName);
        entity.setOperatorId(authId);
        entity.setCreateOp(authName);
        entity.setRemark(remark);
        return entity;
    }

    private void saveWzCostLog(String orderNo, RenterWzCostDetailReqVO costDetail, String content) {
        WzCostLogEntity wzCostLogEntity = new WzCostLogEntity();
        wzCostLogEntity.setContent(content);
        wzCostLogEntity.setCreateTime(new Date());
        wzCostLogEntity.setOperator(AdminUserUtil.getAdminUser().getAuthName());
        wzCostLogEntity.setOrderNo(orderNo);
        wzCostLogEntity.setCostCode(costDetail.getCostCode());
        wzCostLogService.save(wzCostLogEntity);
    }

    private Map<String, String> getParamNamesByCode(String costCode) {
        if(StringUtils.isBlank(costCode)){
            return null;
        }
        Map<String, String> map = new LinkedHashMap<>();
        if(INSURANCE_CLAIM_CODE.equals(costCode)){
            map.put(AMOUNT,INSURANCE_CLAIM);
            map.put(REMARK,INSURANCE_CLAIM_REMARK);
        }
        if(WZ_OTHER_FINE_CODE.equals(costCode)){
            map.put(AMOUNT,WZ_OTHER_FINE);
            map.put(REMARK,WZ_OTHER_FINE_REMARK);
        }
        return map;
    }

    public WzCostLogsResVO queryWzCostLogsByOrderNo(String orderNo) {
        List<WzCostLogEntity> wzCostLogEntities = wzCostLogService.queryWzCostLogsByOrderNo(orderNo);
        List<WzCostLogResVO> wzCostLogs = new ArrayList<>();
        WzCostLogResVO vo;
        for (WzCostLogEntity wzCostLog : wzCostLogEntities) {
            vo = new WzCostLogResVO();
            BeanUtils.copyProperties(wzCostLog,vo);
            vo.setCostItem(WzCostEnums.getDesc(wzCostLog.getCostCode()));
            vo.setCreateTimeStr(DateUtils.formate(wzCostLog.getCreateTime(),DateUtils.DATE_DEFAUTE1));
            vo.setOperateContent(wzCostLog.getContent());
            wzCostLogs.add(vo);
        }
        WzCostLogsResVO wzCostLogsResVO = new WzCostLogsResVO();
        wzCostLogsResVO.setWzCostLogs(wzCostLogs);
        return wzCostLogsResVO;
    }

    private List<TemporaryRefundLogResVO> queryTemporaryRefundLogsByOrderNo(String orderNo) {
        List<WzTemporaryRefundLogEntity> wzTemporaryRefundLogEntities = wzTemporaryRefundLogService.queryTemporaryRefundLogsByOrderNo(orderNo);
        List<TemporaryRefundLogResVO> wzCostLogs = new ArrayList<>();
        TemporaryRefundLogResVO vo;
        for (WzTemporaryRefundLogEntity wzCostLog : wzTemporaryRefundLogEntities) {
            vo = new TemporaryRefundLogResVO();
            BeanUtils.copyProperties(wzCostLog,vo);
            vo.setCreateTimeStr(DateUtils.formate(wzCostLog.getCreateTime(),DateUtils.DATE_DEFAUTE1));
            vo.setAmount(String.valueOf(wzCostLog.getAmount()));
            wzCostLogs.add(vo);
        }
        return wzCostLogs;
    }

    private static final Integer DETAIN = 1;
    private static final Integer UN_DETAIN = 2;

    public void addTemporaryRefund(TemporaryRefundReqVO req) {
        //违章押金暂扣状态 1：暂扣 2：取消暂扣
        Integer detainStatus = req.getDetainStatus();
        OrderStatusEntity orderStatusEntity = null;
        if(DETAIN.equals(detainStatus)){
            RenterDetainVO renterDetainVO = new RenterDetainVO();
            renterDetainVO.setOrderNo(req.getOrderNo());
            renterDetainVO.setEventType(DetailSourceEnum.WZ_DEPOSIT);
            renterDetain.insertRenterDetain(renterDetainVO);
            //暂扣状态，暂扣
            orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setIsDetainWz(OrderConstant.ONE);
        }else if(UN_DETAIN.equals(detainStatus)){
            UnfreezeRenterDetainVO unfreezeRenterDetainVO = new UnfreezeRenterDetainVO();
            unfreezeRenterDetainVO.setOrderNo(req.getOrderNo());
            unfreezeRenterDetainVO.setEventType(DetailSourceEnum.WZ_DEPOSIT);
            renterDetain.unfreezeRenterDetain(unfreezeRenterDetainVO);
            //暂扣状态，撤销
            orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setIsDetainWz(OrderConstant.TWO);
        }

        //更新订单暂扣状态
        if(null != orderStatusEntity) {
            orderStatusEntity.setOrderNo(req.getOrderNo());
            orderStatusService.updateRenterOrderByOrderNo(orderStatusEntity);
        }
    }

    /**
     * 租车押金暂扣
     *
     * @param req 请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveCarDepositTemporaryRefund(CarDepositTemporaryRefundReqVO req) {
        if (null != AdminUserUtil.getAdminUser()) {
            req.setOperator(AdminUserUtil.getAdminUser().getAuthName());
        }
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(req.getOrderNo());
        OrderStatusEntity record = null;
        if (getIsDetain(req) == OrderConstant.ONE) {
            if (orderStatusEntity.getIsDetain() != OrderConstant.ONE) {
                RenterDetainVO renterDetainVO = new RenterDetainVO();
                renterDetainVO.setOrderNo(req.getOrderNo());
                renterDetainVO.setEventType(DetailSourceEnum.RENT_DEPOSIT);
                renterDetain.insertRenterDetain(renterDetainVO);
                //暂扣状态，暂扣
                record = new OrderStatusEntity();
                record.setIsDetain(OrderConstant.ONE);
            }
        } else if (getIsDetain(req) == OrderConstant.TWO) {
            if (orderStatusEntity.getIsDetain() == OrderConstant.ONE) {
                UnfreezeRenterDetainVO unfreezeRenterDetainVO = new UnfreezeRenterDetainVO();
                unfreezeRenterDetainVO.setOrderNo(req.getOrderNo());
                unfreezeRenterDetainVO.setEventType(DetailSourceEnum.RENT_DEPOSIT);
                renterDetain.unfreezeRenterDetain(unfreezeRenterDetainVO);
                //暂扣状态,撤销
                record = new OrderStatusEntity();
                record.setIsDetain(OrderConstant.TWO);
            }
        } else {
            log.warn("Car deposit temporary refund is empty.");
        }

        CarDepositTemporaryRefundReqDTO carDepositTemporaryRefund = new CarDepositTemporaryRefundReqDTO();
        BeanUtils.copyProperties(req, carDepositTemporaryRefund);
        renterDetain.saveRenterDetainReason(carDepositTemporaryRefund);

        //更新订单暂扣状态
        if(null != record) {
            record.setOrderNo(req.getOrderNo());
            orderStatusService.updateRenterOrderByOrderNo(record);
        }
    }

    /**
     * 获取是否暂扣租车押金表示
     *
     * @param req 请求参数
     * @return Integer 0-否 1-是 2-撤销暂扣
     */
    private int getIsDetain(CarDepositTemporaryRefundReqVO req) {
        if (null == req) {
            return OrderConstant.ZERO;
        }

        if (Integer.parseInt(req.getFkDetainFlag()) == OrderConstant.YES ||
                Integer.parseInt(req.getJyDetainFlag()) == OrderConstant.YES ||
                Integer.parseInt(req.getLpDetainFlag()) == OrderConstant.YES) {
            return OrderConstant.ONE;
        }
        return OrderConstant.TWO;
    }


    private int convertIntString(String intStr){
        if(org.apache.commons.lang.StringUtils.isBlank(intStr)){
            return 0;
        }
        //判断是否有小数点
        if(intStr.contains(RADIX_POINT)){
            String subStr= intStr.substring(0,(intStr.indexOf(RADIX_POINT)));
            return Integer.parseInt(subStr);
        }
        return Integer.parseInt(intStr);
    }

    public RenterWzDetailResVO queryWzDetailByOrderNo(String orderNo) {
        RenterWzDetailResVO rs = new RenterWzDetailResVO();
        rs.setOrderNo(orderNo);
        //违章结算 状态
        OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(orderNo);
        if(orderStatus == null || orderStatus.getWzSettleStatus() == null){
            rs.setSettleStatus("0");
        }else {
            rs.setSettleStatus(String.valueOf(orderStatus.getWzSettleStatus()));
        }

        //费用详情
        List<RenterWzCostDetailResVO> costDetails = getRenterWzCostDetailRes(orderNo);
        rs.setCostDetails(costDetails);

        //获取预计/实际的暂扣金额 = 等于以下6项费用之和，协助违章处理费、凹凸代办服务费、不良用车处罚金、停运费、其他扣款、保险理赔
        int zanKouAmount = this.getZanKouAmount(costDetails);

        //暂扣日志
        /*List<TemporaryRefundLogResVO> temporaryRefundLogResVos = this.queryTemporaryRefundLogsByOrderNo(orderNo);
        rs.setTemporaryRefundLogs(temporaryRefundLogResVos);*/

        WzDepositMsgResVO wzDepositMsg = cashierQueryService.queryWzDepositMsg(orderNo);

        //违章押金暂扣处理
        RenterWzWithholdResVO withhold = this.queryRenterWzWithhold(orderNo,rs.getSettleStatus(),orderStatus,wzDepositMsg,zanKouAmount);
        rs.setWithhold(withhold);

        //违章支付信息
        RenterWzInfoResVO renterWzInfo = this.queryRenterWzInfoByOrderNo(wzDepositMsg);
        rs.setInfo(renterWzInfo);

        return rs;
    }

    private int getZanKouAmount(List<RenterWzCostDetailResVO> costDetails) {
        if(CollectionUtils.isEmpty(costDetails)){
            return 0;
        }
        return costDetails
                .stream()
                .filter(Objects::nonNull)
                .filter(dto -> StringUtils.isNotBlank(dto.getAmount()))
                .map(RenterWzCostDetailResVO::getAmount)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private static final String UN_SETTLE = "0";
    private RenterWzWithholdResVO queryRenterWzWithhold(String orderNo, String settleStatus, OrderStatusEntity orderStatus, WzDepositMsgResVO wzDepositMsg, int zanKouAmount) {
        RenterWzWithholdResVO result = new RenterWzWithholdResVO();
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(renterOrder != null && renterOrder.getActRevertTime() != null){
            result.setExpectSettleTimeStr(DateUtils.formate(renterOrder.getActRevertTime().plusDays(18L),DateUtils.DATE_DEFAUTE1));
        }
        int wzDepositAmt = wzDepositMsg.getWzDepositAmt();

        result.setDetainStatus(orderStatus.getIsDetainWz().toString());
        result.setActuallyProvisionalDeduction(orderStatus.getIsDetainWz() == OrderConstant.YES ?
                String.valueOf(wzDepositAmt) : "0");
        if(UN_SETTLE.equals(settleStatus)){
            //未结算
            result.setShouldReturnDeposit(String.valueOf(wzDepositAmt - zanKouAmount));
            result.setProvisionalDeduction(String.valueOf(zanKouAmount));
            result.setYuJiDiKouZuCheFee(String.valueOf(wzDepositMsg.getDetainCostAmt()));
        }else{
            //已结算
            result.setShiJiZanKouJinE(String.valueOf(zanKouAmount));
            result.setShiJiYiTuiWeiZhangYaJin(String.valueOf(wzDepositAmt - zanKouAmount));
            result.setShiJiDiKouZuCheFee(String.valueOf(wzDepositMsg.getDetainCostAmt()));
            result.setJieSuanShiDiKouLiShiQianKuan(String.valueOf(wzDepositMsg.getDebtAmt()));
            if(orderStatus.getWzSettleTime() != null){
                result.setRealSettleTimeStr(DateUtils.formate(orderStatus.getWzSettleTime(),DateUtils.DATE_DEFAUTE1));
            }
        }

        if(StringUtils.isNotBlank(wzDepositMsg.getDeductionTime())){
            result.setDeductionTimeStr(wzDepositMsg.getDeductionTime());
            result.setDeductionStatusStr(wzDepositMsg.getDebtStatus());
        }
        return result;
    }

    private RenterWzInfoResVO queryRenterWzInfoByOrderNo(WzDepositMsgResVO wzDepositMsg) {
        RenterWzInfoResVO result = new RenterWzInfoResVO();
        if(wzDepositMsg == null){
            return result;
        }
        result.setYingshouDeposit(String.valueOf(NumberUtils.convertNumberToZhengshu(wzDepositMsg.getYingshouWzDepositAmt())));
        result.setWzDeposit(String.valueOf(wzDepositMsg.getWzDepositAmt()));
        result.setWaiverAmount(String.valueOf(wzDepositMsg.getReductionAmt()));
        result.setTransStatusStr(wzDepositMsg.getPayStatus());
        result.setPayTimeStr(wzDepositMsg.getPayTime());
        result.setPaymentStr(wzDepositMsg.getPaySource());
        result.setFreeDepositTypeStr(wzDepositMsg.getPayType());
        return result;
    }

    private List<RenterWzCostDetailResVO> getRenterWzCostDetailRes(String orderNo) {
        List<RenterOrderWzCostDetailEntity> results = new ArrayList<>();
        for (String costCode : COST_CODE_LIST) {
            RenterOrderWzCostDetailEntity dto = renterOrderWzCostDetailService.queryInfoWithSumAmountByOrderAndCode(orderNo,costCode);
            if(dto == null){
                dto = new RenterOrderWzCostDetailEntity();
                dto.setAmount(0);
                dto.setCostCode(costCode);
                dto.setCostDesc(WzCostEnums.getDesc(costCode));
                dto.setOrderNo(orderNo);
            }
            results.add(dto);
        }
        List<RenterWzCostDetailResVO> costDetails = new ArrayList<>();
        RenterWzCostDetailResVO dto;
        for (RenterOrderWzCostDetailEntity costDetail : results) {
            dto = new RenterWzCostDetailResVO();
            BeanUtils.copyProperties(costDetail,dto);
            dto.setAmount(String.valueOf(costDetail.getAmount()));
            dto.setCostType(WzCostEnums.getType(costDetail.getCostCode()));
            dto.setRemarkName(WzCostEnums.getRemark(costDetail.getCostCode()));
            costDetails.add(dto);
        }
        return costDetails;
    }
}
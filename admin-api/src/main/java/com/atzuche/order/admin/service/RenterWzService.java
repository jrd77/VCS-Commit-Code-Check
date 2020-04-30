package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.exception.ConsoleBusinessException;
import com.atzuche.order.admin.vo.req.renterWz.CarDepositTemporaryRefundReqVO;
import com.atzuche.order.admin.vo.req.renterWz.RenterWzCostDetailReqVO;
import com.atzuche.order.admin.vo.req.renterWz.TemporaryRefundReqVO;
import com.atzuche.order.admin.vo.resp.renterWz.*;
import com.atzuche.order.commons.*;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.WzCostLogDTO;
import com.atzuche.order.commons.entity.dto.WzDepositMsgDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderWzCostDetailDTO;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.enums.wz.WzCostEnums;
import com.atzuche.order.commons.vo.detain.CarDepositDetainReqVO;
import com.atzuche.order.commons.vo.detain.IllegalDepositDetainReqVO;
import com.atzuche.order.commons.vo.res.console.ConsoleOrderWzDetailQueryResVO;
import com.atzuche.order.open.service.FeignGoodsService;
import com.atzuche.order.open.service.FeignMemberService;
import com.atzuche.order.open.service.FeignOrderDepositService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    private FeignMemberService feignMemberService;

    @Resource
    private FeignGoodsService feignGoodsService;

    @Resource
    private FeignOrderDepositService feignOrderDepositService;

    private static final String WZ_OTHER_FINE_REMARK = "其他扣款备注";
    private static final String WZ_OTHER_FINE = "其他扣款";
    private static final String WZ_OTHER_FINE_CODE = "11240044";

    private static final String INSURANCE_CLAIM_REMARK = "保险理赔备注";
    private static final String INSURANCE_CLAIM = "保险理赔";
    private static final String INSURANCE_CLAIM_CODE = "11240045";

    private static final String REMARK = "remark";
    private static final String AMOUNT = "amount";
    private static final String SOURCE_TYPE_CONSOLE = "2";
    private static final List<String> COST_CODE_LIST = Arrays.asList("11240040", "11240041", "11240042", "11240043", "11240044", "11240045");


    public void updateWzCost(String orderNo, List<RenterWzCostDetailReqVO> costDetails) {
        //远程调用
        ConsoleOrderWzDetailQueryResVO vo = queryWzDetailRemoteByOrderNo(orderNo);
        OrderStatusDTO orderStatus = vo.getOrderStatus();
        if (orderStatus != null && orderStatus.getWzSettleStatus() != null && orderStatus.getWzSettleStatus() == OrderConstant.YES) {
            throw new ConsoleBusinessException(ErrorCode.RENTER_WZ_SETTLED.getCode(), ErrorCode.RENTER_WZ_SETTLED.getText());
        }
        //只会处理其他扣款 和 保险理赔
        List<RenterOrderWzCostDetailDTO> dtos = vo.getDtos();
        Map<String, RenterOrderWzCostDetailDTO> dataMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(dtos)) {
            dataMap =
                    dtos.stream().collect(Collectors.toMap(RenterOrderWzCostDetailDTO::getCostCode, costDetail -> costDetail));
        }
        for (RenterWzCostDetailReqVO costDetail : costDetails) {
            if (!WZ_OTHER_FINE_CODE.equals(costDetail.getCostCode()) && !INSURANCE_CLAIM_CODE.equals(costDetail.getCostCode())) {
                continue;
            }
            RenterOrderWzCostDetailDTO fromDb = dataMap.get(costDetail.getCostCode());
            try {
                RenterOrderWzCostDetailDTO fromApp = new RenterOrderWzCostDetailDTO();
                BeanUtils.copyProperties(costDetail, fromApp);
                if (StringUtils.isNotBlank(costDetail.getAmount())) {
                    fromApp.setAmount(Integer.parseInt(costDetail.getAmount()));
                }
                Map<String, String> paramNames = this.getParamNamesByCode(costDetail.getCostCode());
                CompareHelper<RenterOrderWzCostDetailDTO> compareHelper = new CompareHelper<>(fromDb, fromApp, paramNames);
                String content = compareHelper.compare();
                if (StringUtils.isNotBlank(content)) {
                    //记录日志 并且做修改费用处理
                    updateCostStatus(orderNo, costDetail, fromDb);
                    saveWzCostLog(orderNo, costDetail, content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public WzCostLogsResVO queryWzCostLogsByOrderNo(String orderNo) {
        //远程调用
        ResponseData<List<WzCostLogDTO>> responseObject = null;
        List<WzCostLogDTO> dtos;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDepositService.queryWzCostOptLogByOrderNo");
            log.info("Feign 获取订单违章费用变更日志接口,orderNo:[{}]", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM, orderNo);
            responseObject = feignOrderDepositService.queryWzCostOptLogByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            dtos = responseObject.getData();
        } catch (Exception e) {
            log.error("Feign 获取订单违章费用变更日志接口异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject), orderNo, e);
            Cat.logError("Feign 获取订单违章费用变更日志接口异常", e);
            throw e;
        } finally {
            t.complete();
        }

        //数据处理
        List<WzCostLogResVO> wzCostLogs = new ArrayList<>();
        WzCostLogResVO vo;
        for (WzCostLogDTO dto : dtos) {
            vo = new WzCostLogResVO();
            BeanUtils.copyProperties(dto, vo);
            vo.setCostItem(WzCostEnums.getDesc(dto.getCostCode()));
            vo.setCreateTimeStr(DateUtils.formate(dto.getCreateTime(), DateUtils.DATE_DEFAUTE1));
            vo.setOperateContent(dto.getContent());
            wzCostLogs.add(vo);
        }
        WzCostLogsResVO wzCostLogsResVO = new WzCostLogsResVO();
        wzCostLogsResVO.setWzCostLogs(wzCostLogs);
        return wzCostLogsResVO;
    }

    public void addTemporaryRefund(TemporaryRefundReqVO req) {
        IllegalDepositDetainReqVO reqVO = new IllegalDepositDetainReqVO();
        BeanUtils.copyProperties(req, reqVO);
        reqVO.setOperatorName(AdminUserUtil.getAdminUser().getAuthName());

        log.info("Feign 订单违章押金暂扣/撤销暂扣,reqVO:[{}]", JSON.toJSONString(reqVO));
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDepositService.illegalDepositDetain");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            responseObject = feignOrderDepositService.illegalDepositDetain(reqVO);
            log.info("Feign 订单违章押金暂扣/撤销暂扣,responseObject:[{}]", JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("Feign 订单违章押金暂扣/撤销暂扣异常,responseObject:[{}],reqVO:[{}]", JSON.toJSONString(responseObject),
                    JSON.toJSONString(reqVO), e);
            Cat.logError("Feign 订单违章押金暂扣/撤销暂扣异常", e);
            throw e;
        } finally {
            t.complete();
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
        CarDepositDetainReqVO reqVO = new CarDepositDetainReqVO();
        BeanUtils.copyProperties(req, reqVO);
        reqVO.setDetainStatus(getIsDetain(req));
        log.info("Feign 订单车辆押金暂扣/撤销暂扣,reqVO:[{}]", JSON.toJSONString(reqVO));
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDepositService.carDepositDetain");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            responseObject = feignOrderDepositService.carDepositDetain(reqVO);
            log.info("Feign 订单车辆押金暂扣/撤销暂扣,responseObject:[{}]", JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("Feign 订单车辆押金暂扣/撤销暂扣异常,responseObject:[{}],reqVO:[{}]", JSON.toJSONString(responseObject),
                    JSON.toJSONString(reqVO), e);
            Cat.logError("Feign 订单车辆押金暂扣/撤销暂扣异常", e);
            throw e;
        } finally {
            t.complete();
        }
    }

    private void updateCostStatus(String orderNo, RenterWzCostDetailReqVO costDetail,
                                  RenterOrderWzCostDetailDTO fromDb) {
        String carNum;
        Integer memNo = null;
        if (fromDb != null) {
            carNum = fromDb.getCarPlateNum();
            memNo = fromDb.getMemNo();
        } else {
            carNum = getCarNumFromRemot(orderNo);
            String renterNoByOrderNo = getRenterMemberFromRemote(orderNo);
            if (StringUtils.isNotBlank(renterNoByOrderNo)) {
                memNo = Integer.parseInt(renterNoByOrderNo);
            }
        }

        //调用远程api 维护订单违章费用
        RenterOrderWzCostDetailDTO entityByType = getEntityByType(costDetail.getCostCode(), orderNo,
                costDetail.getAmount(), carNum, memNo, costDetail.getRemark());
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDepositService.saveWzCostDetail");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(entityByType));
            responseObject = feignOrderDepositService.saveWzCostDetail(entityByType);
            log.info("Feign 保存订单违章费用明细接口,responseObject:[{}]", JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("Feign 保存订单违章费用明细接口异常,responseObject:[{}],reqVO:[{}]", JSON.toJSONString(responseObject),
                    JSON.toJSONString(entityByType), e);
            Cat.logError("Feign 保存订单违章费用明细接口异常", e);
            throw e;
        } finally {
            t.complete();
        }
    }

    private RenterOrderWzCostDetailDTO getEntityByType(String code, String orderNo, String amount, String carNum,
                                                       Integer memNo, String remark) {
        String authName = AdminUserUtil.getAdminUser().getAuthName();
        String authId = AdminUserUtil.getAdminUser().getAuthId();
        RenterOrderWzCostDetailDTO entity = new RenterOrderWzCostDetailDTO();
        entity.setOrderNo(orderNo);
        entity.setCarPlateNum(carNum);
        if (StringUtils.isNotBlank(amount)) {
            entity.setAmount(Integer.parseInt(amount));
        } else {
            entity.setAmount(OrderConstant.ZERO);
        }
        entity.setMemNo(memNo);
        entity.setCostCode(code);
        entity.setCostDesc(WzCostEnums.getDesc(code));
        entity.setSourceType(SOURCE_TYPE_CONSOLE);
        entity.setOperatorName(authName);
        entity.setOperatorId(authId);
        entity.setRemark(remark);
        return entity;
    }

    private void saveWzCostLog(String orderNo, RenterWzCostDetailReqVO costDetail, String content) {
        WzCostLogDTO wzCostLogEntity = new WzCostLogDTO();
        wzCostLogEntity.setContent(content);
        wzCostLogEntity.setCreateTime(new Date());
        wzCostLogEntity.setOperator(AdminUserUtil.getAdminUser().getAuthName());
        wzCostLogEntity.setOrderNo(orderNo);
        wzCostLogEntity.setCostCode(costDetail.getCostCode());

        //远程调用
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDepositService.saveWzCostOptLog");
            log.info("Feign 保存订单违章费用变更日志接口,wzCostLogEntity:[{}]", JSON.toJSONString(wzCostLogEntity));
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(wzCostLogEntity));
            responseObject = feignOrderDepositService.saveWzCostOptLog(wzCostLogEntity);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("Feign 保存订单违章费用变更日志接口异常,responseObject={},wzCostLogEntity={}",
                    JSON.toJSONString(responseObject), JSON.toJSONString(wzCostLogEntity), e);
            Cat.logError("Feign 保存订单违章费用变更日志接口异常", e);
            throw e;
        } finally {
            t.complete();
        }
    }

    private Map<String, String> getParamNamesByCode(String costCode) {
        if (StringUtils.isBlank(costCode)) {
            return null;
        }
        Map<String, String> map = new LinkedHashMap<>();
        if (INSURANCE_CLAIM_CODE.equals(costCode)) {
            map.put(AMOUNT, INSURANCE_CLAIM);
            map.put(REMARK, INSURANCE_CLAIM_REMARK);
        }
        if (WZ_OTHER_FINE_CODE.equals(costCode)) {
            map.put(AMOUNT, WZ_OTHER_FINE);
            map.put(REMARK, WZ_OTHER_FINE_REMARK);
        }
        return map;
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

    public RenterWzDetailResVO queryWzDetailByOrderNo(String orderNo) {
        //远程调用
        ConsoleOrderWzDetailQueryResVO vo = queryWzDetailRemoteByOrderNo(orderNo);
        //数据处理
        RenterWzDetailResVO rs = new RenterWzDetailResVO();
        rs.setOrderNo(orderNo);
        //违章结算 状态
        OrderStatusDTO orderStatus = vo.getOrderStatus();
        if (orderStatus == null || orderStatus.getWzSettleStatus() == null) {
            rs.setSettleStatus(String.valueOf(OrderConstant.ZERO));
        } else {
            rs.setSettleStatus(String.valueOf(orderStatus.getWzSettleStatus()));
        }
        //费用详情
        List<RenterWzCostDetailResVO> costDetails = getRenterWzCostDetailRes(orderNo, vo.getDtos());
        rs.setCostDetails(costDetails);

        //获取预计/实际的暂扣金额 = 等于以下6项费用之和，协助违章处理费、凹凸代办服务费、不良用车处罚金、停运费、其他扣款、保险理赔
        int zanKouAmount = this.getZanKouAmount(costDetails);
        //违章押金信息
        WzDepositMsgDTO wzDepositMsg = vo.getWzDepositMsg();
        //违章押金暂扣处理
        RenterWzWithholdResVO withhold = this.queryRenterWzWithhold(rs.getSettleStatus(), zanKouAmount, vo);
        rs.setWithhold(withhold);

        //违章支付信息
        RenterWzInfoResVO renterWzInfo = this.queryRenterWzInfoByOrderNo(wzDepositMsg);
        rs.setInfo(renterWzInfo);

        return rs;
    }

    /**
     * 获取订单违章费用信息列表
     *
     * @param orderNo 订单号
     * @return ConsoleOrderWzDetailQueryResVO
     */
    private ConsoleOrderWzDetailQueryResVO queryWzDetailRemoteByOrderNo(String orderNo) {

        ResponseData<ConsoleOrderWzDetailQueryResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDepositService.queryWzDetailByOrderNo");
            log.info("Feign 获取订单违章明细接口,orderNo:[{}]", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM, orderNo);
            responseObject = feignOrderDepositService.queryWzDetailByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        } catch (Exception e) {
            log.error("Feign 获取订单违章明细接口异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject), orderNo, e);
            Cat.logError("Feign 获取订单违章明细接口异常", e);
            throw e;
        } finally {
            t.complete();
        }
    }


    private int getZanKouAmount(List<RenterWzCostDetailResVO> costDetails) {
        if (CollectionUtils.isEmpty(costDetails)) {
            return OrderConstant.ZERO;
        }
        return costDetails
                .stream()
                .filter(Objects::nonNull)
                .filter(dto -> StringUtils.isNotBlank(dto.getAmount()))
                .map(RenterWzCostDetailResVO::getAmount)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private RenterWzWithholdResVO queryRenterWzWithhold(String settleStatus, int zanKouAmount, ConsoleOrderWzDetailQueryResVO vo) {
        RenterWzWithholdResVO result = new RenterWzWithholdResVO();
        RenterOrderDTO renterOrder = vo.getRenterOrderDTO();
        if (renterOrder != null && renterOrder.getActRevertTime() != null) {
            result.setExpectSettleTimeStr(DateUtils.formate(renterOrder.getActRevertTime().plusDays(18L), DateUtils.DATE_DEFAUTE1));
        }
        OrderStatusDTO orderStatus = vo.getOrderStatus();
        WzDepositMsgDTO wzDepositMsg = vo.getWzDepositMsg();
        int wzDepositAmt = wzDepositMsg.getWzDepositAmt();
        result.setDetainStatus(orderStatus.getIsDetainWz().toString());
        result.setActuallyProvisionalDeduction(orderStatus.getIsDetainWz() == OrderConstant.YES ?
                String.valueOf(wzDepositAmt) : String.valueOf(OrderConstant.ZERO));
        if (String.valueOf(OrderConstant.ZERO).equals(settleStatus)) {
            //未结算
            result.setShouldReturnDeposit(String.valueOf(wzDepositAmt - zanKouAmount));
            result.setProvisionalDeduction(String.valueOf(zanKouAmount));
            result.setYuJiDiKouZuCheFee(String.valueOf(wzDepositMsg.getDetainCostAmt()));
        } else {
            //已结算
            result.setShiJiZanKouJinE(String.valueOf(zanKouAmount));
            result.setShiJiYiTuiWeiZhangYaJin(String.valueOf(wzDepositAmt - zanKouAmount));
            result.setShiJiDiKouZuCheFee(String.valueOf(wzDepositMsg.getDetainCostAmt()));
            result.setJieSuanShiDiKouLiShiQianKuan(String.valueOf(wzDepositMsg.getDebtAmt()));
            if (orderStatus.getWzSettleTime() != null) {
                result.setRealSettleTimeStr(DateUtils.formate(orderStatus.getWzSettleTime(), DateUtils.DATE_DEFAUTE1));
            }
        }

        if (StringUtils.isNotBlank(wzDepositMsg.getDeductionTime())) {
            result.setDeductionTimeStr(wzDepositMsg.getDeductionTime());
            result.setDeductionStatusStr(wzDepositMsg.getDebtStatus());
        }
        return result;
    }

    private RenterWzInfoResVO queryRenterWzInfoByOrderNo(WzDepositMsgDTO wzDepositMsg) {
        RenterWzInfoResVO result = new RenterWzInfoResVO();
        if (wzDepositMsg == null) {
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

    private List<RenterWzCostDetailResVO> getRenterWzCostDetailRes(String orderNo, List<RenterOrderWzCostDetailDTO> dtos) {
        List<RenterWzCostDetailResVO> costDetails = new ArrayList<>();
        Map<String, RenterOrderWzCostDetailDTO> dataMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(dtos)) {
            dataMap =
                    dtos.stream().collect(Collectors.toMap(RenterOrderWzCostDetailDTO::getCostCode, costDetail -> costDetail));
        }
        for (String costCode : COST_CODE_LIST) {
            RenterOrderWzCostDetailDTO dto = dataMap.get(costCode);
            RenterWzCostDetailResVO vo = new RenterWzCostDetailResVO();
            vo.setOrderNo(orderNo);
            vo.setCostCode(costCode);
            vo.setCostDesc(WzCostEnums.getDesc(costCode));
            vo.setCostType(WzCostEnums.getType(costCode));
            vo.setRemarkName(WzCostEnums.getRemark(costCode));
            if (Objects.nonNull(dto)) {
                vo.setAmount(String.valueOf(dto.getAmount()));
                vo.setRemark(dto.getRemark());
            } else {
                vo.setAmount(String.valueOf(OrderConstant.ZERO));
            }
            costDetails.add(vo);
        }
        return costDetails;
    }

    private String getCarNumFromRemot(String orderNo) {
        ResponseData<String> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车辆号");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取车辆号,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM, orderNo);
            responseObject = feignGoodsService.queryCarNumByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT, orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        } catch (Exception e) {
            log.error("Feign 获取车辆号异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject), orderNo, e);
            Cat.logError("Feign 获取车辆号异常", e);
            throw e;
        } finally {
            t.complete();
        }
    }


    private String getRenterMemberFromRemote(String orderNo) {
        ResponseData<String> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取会员号");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取会员号,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM, orderNo);
            responseObject = feignMemberService.getRenterMemberByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT, orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        } catch (Exception e) {
            log.error("Feign 获取获取会员号异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject), orderNo, e);
            Cat.logError("Feign 获取获取会员号异常", e);
            throw e;
        } finally {
            t.complete();
        }
    }
}

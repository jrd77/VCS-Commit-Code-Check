package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.vo.req.consolecost.SaveTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.res.consolecost.GetTempCarDepositInfoResVO;
import com.atzuche.order.commons.vo.res.consolecost.TempCarDepoistInfoResVO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.service.OrderConsoleCostDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台维护订单相关费用(管理后台添加的费用)业务处理
 *
 * @author pengcheng.fu
 * @date 2020/4/23 17:10
 */

@Service
@Slf4j
public class OrderConsoleCostHandleService {

    @Autowired
    private OrderConsoleCostDetailService orderConsoleCostDetailService;
    @Autowired
    private OrderStatusService orderStatusService;


    /**
     * 获取订单车辆押金暂扣扣款信息
     *
     * @param orderNo 订单号
     * @param memNo   租客会员号
     * @param cashNos 费用项列表
     * @return GetTempCarDepositInfoResVO
     */
    public GetTempCarDepositInfoResVO getTempCarDepoistInfos(String orderNo, String memNo, List<String> cashNos) {
        log.info("获取订单车辆押金暂扣扣款信息.param is,orderNo:[{}],memNo:[{}],cashNos:[{}]", orderNo, memNo, cashNos);
        List<OrderConsoleCostDetailEntity> costList =
                orderConsoleCostDetailService.getOrderConsoleCostByCondition(orderNo, memNo, cashNos);
        List<TempCarDepoistInfoResVO> tempCarDepoists = new ArrayList<>();
        if (CollectionUtils.isEmpty(costList)) {
            tempCarDepoists = initTempCarDepoistList(cashNos, OrderConstant.ZERO);
        } else {
            List<String> cashNoList = costList.stream().map(OrderConsoleCostDetailEntity::getSubsidyTypeCode).collect(Collectors.toList());
            Map<String, OrderConsoleCostDetailEntity> dataMap =
                    costList.stream().collect(Collectors.toMap(OrderConsoleCostDetailEntity::getSubsidyTypeCode,
                            cost -> cost));

            List<TempCarDepoistInfoResVO> list = new ArrayList<>(costList.size());
            // 兼容后续添加新的费用项
            cashNos.forEach(c -> {
                TempCarDepoistInfoResVO res;
                if (cashNoList.contains(c)) {
                    res = buildTempCarDepoistInfoResVO(dataMap.get(c).getId(), dataMap.get(c).getSubsidyTypeCode(),
                            Math.abs(dataMap.get(c).getSubsidyAmount()));
                } else {
                    res = buildTempCarDepoistInfoResVO(OrderConstant.ZERO, c, OrderConstant.ZERO);
                }
                list.add(res);
            });
            tempCarDepoists.addAll(list);
        }

        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        GetTempCarDepositInfoResVO resVO = new GetTempCarDepositInfoResVO();
        resVO.setOrderNo(orderNo);
        resVO.setMemNo(memNo);
        int isEdit =
                orderStatusEntity.getStatus() > OrderStatusEnum.TO_SETTLE.getStatus()
                        || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()
                        || orderStatusEntity.getIsDetain() != OrderConstant.ONE ? OrderConstant.NO : OrderConstant.YES;
        resVO.setIsEdit(String.valueOf(isEdit));
        resVO.setTempCarDepoists(tempCarDepoists);

        log.info("获取订单车辆押金暂扣扣款信息.result is,resVO:[{}]", JSON.toJSONString(resVO));
        return resVO;

    }

    /**
     * 保存订单车辆押金暂扣扣款信息
     *
     * @param reqVO 请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveTempCarDeposit(SaveTempCarDepositInfoReqVO reqVO) {
        int total = orderConsoleCostDetailService.saveOrderConsoleCostDetai(buildOrderConsoleCostDetailEntity(reqVO));
        log.info("Save temp car depoist. result is, orderNo:[{}], memNo:[{}], total:[{}]", reqVO.getOrderNo(),
                reqVO.getMemNo(), total);
    }


    /**
     * 初始化费用列表
     *
     * @param cashNos 费用项集合
     * @param amt     费用项金额
     * @return List<TempCarDepoistInfoResVO>
     */
    private List<TempCarDepoistInfoResVO> initTempCarDepoistList(List<String> cashNos, int amt) {
        List<TempCarDepoistInfoResVO> list = new ArrayList<>();
        cashNos.forEach(c -> {
            TempCarDepoistInfoResVO res = buildTempCarDepoistInfoResVO(OrderConstant.ZERO, c, amt);
            list.add(res);
        });
        log.info("Init temp carDepoist list. result is,list:[{}]", JSON.toJSONString(list));
        return list;
    }


    /**
     * 构建TempCarDepoistInfoResVO
     *
     * @param id     主键
     * @param cashNo 费用编码
     * @param amt    费用金额
     * @return TempCarDepoistInfoResVO
     */
    private TempCarDepoistInfoResVO buildTempCarDepoistInfoResVO(Integer id, String cashNo, int amt) {
        TempCarDepoistInfoResVO res = new TempCarDepoistInfoResVO();
        res.setId(id);
        res.setCashAmt(amt);
        ConsoleCashCodeEnum cashCodeEnum = ConsoleCashCodeEnum.from(cashNo);
        res.setCashNo(cashCodeEnum.getCashNo());
        res.setCashTitle(cashCodeEnum.getTxt());
        return res;
    }

    /**
     * 构建OrderConsoleCostDetailEntity
     *
     * @param reqVO 请求参数
     * @return List<OrderConsoleCostDetailEntity>
     */
    private List<OrderConsoleCostDetailEntity> buildOrderConsoleCostDetailEntity(SaveTempCarDepositInfoReqVO reqVO) {

        List<OrderConsoleCostDetailEntity> list = new ArrayList<>();
        List<TempCarDepoistInfoResVO> tempCarDepoists = reqVO.getTempCarDepoists();

        if (CollectionUtils.isNotEmpty(tempCarDepoists)) {
            tempCarDepoists.forEach(t -> {
                OrderConsoleCostDetailEntity entity = new OrderConsoleCostDetailEntity();
                if (t.getId() != OrderConstant.ZERO) {
                    //更新
                    entity.setId(t.getId());
                    entity.setSubsidyAmount(-Math.abs(t.getCashAmt()));
                    entity.setUpdateOp(reqVO.getOperatorName());
                } else {
                    //新增
                    entity.setOrderNo(reqVO.getOrderNo());
                    entity.setMemNo(reqVO.getMemNo());
                    entity.setSubsidyTypeCode(t.getCashNo());
                    entity.setSubsidTypeName(t.getCashTitle());
                    entity.setSubsidyCostCode(t.getCashNo());
                    entity.setSubsidyCostName(t.getCashTitle());
                    entity.setSubsidySourceCode(SubsidySourceCodeEnum.RENTER.getCode());
                    entity.setSubsidySourceName(SubsidySourceCodeEnum.RENTER.getDesc());
                    entity.setSubsidyTargetCode(SubsidySourceCodeEnum.PLATFORM.getCode());
                    entity.setSubsidyTargetName(SubsidySourceCodeEnum.PLATFORM.getDesc());
                    entity.setSubsidyAmount(-Math.abs(t.getCashAmt()));
                    entity.setCreateOp(reqVO.getOperatorName());
                    entity.setUpdateOp(reqVO.getOperatorName());
                }
                entity.setOperatorId(reqVO.getOperatorName());
                entity.setRemark("管理后台维护车辆押金暂扣扣款信息");
                list.add(entity);
            });

        }
        log.info("Build orderConsoleCostDetailEntity. result is, list:[{}]", JSON.toJSONString(list));
        return list;
    }
}

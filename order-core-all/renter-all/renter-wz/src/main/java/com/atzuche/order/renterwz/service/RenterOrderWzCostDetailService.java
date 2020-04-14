package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzSettleFlagEntity;
import com.atzuche.order.renterwz.enums.WzCostEnums;
import com.atzuche.order.renterwz.mapper.RenterOrderWzCostDetailMapper;
import com.atzuche.order.renterwz.vo.WzSettleVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RenterOrderWzCostDetailService
 *
 * @author shisong
 * @date 2019/12/31
 */
@Service("renterOrderWzCostDetailService")
public class RenterOrderWzCostDetailService {

    @Resource
    private RenterOrderWzCostDetailMapper renterOrderWzCostDetailMapper;

    @Resource
    private RenterOrderWzSettleFlagService renterOrderWzSettleFlagService;

    private static final String SOURCE_TYPE_REN_YUN = "1";
    private static final String REN_YUN_NAME = "任云";

    private static final Integer LOSE_EFFECTIVENESS = 1;

    public int updateTransFeeByOrderNo(String orderNo, Integer wzFine, Integer wzDysFine, Integer wzServiceCost, String carNum, Integer memNo) {
        int count = 0;
        //将之前同类的设为无效
        renterOrderWzCostDetailMapper.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,LOSE_EFFECTIVENESS,WzCostEnums.getCode(1));
        if(wzFine != null && wzFine > 0){
            RenterOrderWzCostDetailEntity entity = getEntityByType(1, orderNo, wzFine, carNum, memNo);
            Integer i = renterOrderWzCostDetailMapper.saveRenterOrderWzCostDetail(entity);
            if(i != null){
                count+=i;
            }
        }
        //将之前同类的设为无效
        renterOrderWzCostDetailMapper.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,LOSE_EFFECTIVENESS,WzCostEnums.getCode(2));
        if(wzDysFine != null && wzDysFine > 0){
            RenterOrderWzCostDetailEntity entity = getEntityByType(2, orderNo, wzDysFine, carNum, memNo);
            Integer i = renterOrderWzCostDetailMapper.saveRenterOrderWzCostDetail(entity);
            if(i != null){
                count+=i;
            }
        }
        //将之前同类的设为无效
        renterOrderWzCostDetailMapper.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,LOSE_EFFECTIVENESS,WzCostEnums.getCode(3));
        if(wzServiceCost != null && wzServiceCost > 0){
            RenterOrderWzCostDetailEntity entity = getEntityByType(3, orderNo, wzServiceCost, carNum, memNo);
            Integer i = renterOrderWzCostDetailMapper.saveRenterOrderWzCostDetail(entity);
            if(i != null){
                count+=i;
            }
        }
        return count;
    }

    private RenterOrderWzCostDetailEntity getEntityByType(Integer type,String orderNo,Integer amount,String carNum, Integer memNo){
        RenterOrderWzCostDetailEntity entity = new RenterOrderWzCostDetailEntity();
        entity.setAmount(amount);
        entity.setOrderNo(orderNo);
        entity.setCarPlateNum(carNum);
        entity.setMemNo(memNo);
        entity.setCostCode(WzCostEnums.getCode(type));
        entity.setCostDesc(WzCostEnums.getDesc(type));
        entity.setCreateTime(new Date());
        entity.setSourceType(SOURCE_TYPE_REN_YUN);
        entity.setOperatorName(REN_YUN_NAME);
        entity.setCreateOp(REN_YUN_NAME);
        return entity;
    }

    public RenterOrderWzCostDetailEntity queryInfoByOrderAndCode(String orderNo, String costCode) {
        return renterOrderWzCostDetailMapper.queryInfoByOrderAndCode(orderNo,costCode);
    }

    public void updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(String orderNo, String carNum, Integer memNo, Integer costStatus, String code){
        renterOrderWzCostDetailMapper.updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(orderNo,carNum,memNo,costStatus,code);
    }

    public void saveRenterOrderWzCostDetail(RenterOrderWzCostDetailEntity renterOrderWzCostDetail){
        renterOrderWzCostDetailMapper.saveRenterOrderWzCostDetail(renterOrderWzCostDetail);
    }

    public List<RenterOrderWzCostDetailEntity> queryInfosByOrderNo(String orderNo) {
        return renterOrderWzCostDetailMapper.queryInfosByOrderNo(orderNo);
    }
    
    /**
     * 单个，不求和
     * @param orderNo
     * @return
     */
    public List<RenterOrderWzCostDetailEntity> queryInfosUnitByOrderNo(String orderNo) {
        return renterOrderWzCostDetailMapper.queryInfosUnitByOrderNo(orderNo);
    }

    /**
     * 查询能否违章能否结算
     * 规则：
     *  1.查询结算表
     *   1.1如果不存在数据 则不能结算
     *   1.2如果存在 则过滤未结算的和结算失败的
     *      1.2.1 再进一步过滤是否有未通知的违章
     *          1.2.1.1 如果存在 则不能结算
     *      以下都能结算
     *      1.2.2 过滤是否有违章的数据
     *          1.2.2.1 有则 设为 有违章 ,并且赋予总金额
     *          1.2.2.2 没有 则设为 没有违章
     *
     * @param orderNo 订单号
     * @return 结算DTO
     */
    public WzSettleVO querySettleInfoByOrder(String orderNo){
        WzSettleVO rs = new WzSettleVO();
        //默认可以结算
        rs.setCanSettle(true);
        Integer totalAmount = 0;
        List<RenterOrderWzCostDetailEntity> costDetails = queryInfosByOrderNo(orderNo);
        if(!CollectionUtils.isEmpty(costDetails)){
            totalAmount = costDetails.stream().filter(Objects::nonNull).filter(dto -> dto.getAmount() != null).mapToInt(RenterOrderWzCostDetailEntity::getAmount).sum();
        }
        List<RenterOrderWzSettleFlagEntity> settleInfos = renterOrderWzSettleFlagService.getIllegalSettleInfosByOrderNo(orderNo);
        //过滤未结算和结算失败的
        if(!CollectionUtils.isEmpty(settleInfos)){
            rs.setAmount(String.valueOf(totalAmount));
        }else{
            rs.setCanSettle(false);
            return rs;
        }
        return rs;
    }
    
    /**
     * 统计费用
     * @param orderNo
     * @return
     */
    public Integer sumQuerySettleInfoByOrder(String orderNo) {
    	Integer totalAmount = 0;
        List<RenterOrderWzCostDetailEntity> costDetails = queryInfosByOrderNo(orderNo);
        if(!CollectionUtils.isEmpty(costDetails)){
            totalAmount = costDetails.stream().filter(Objects::nonNull).filter(dto -> dto.getAmount() != null).mapToInt(RenterOrderWzCostDetailEntity::getAmount).sum();
        }
        return totalAmount;
    }
    

    public RenterOrderWzCostDetailEntity queryInfoWithSumAmountByOrderAndCode(String orderNo, String costCode) {
        return renterOrderWzCostDetailMapper.queryInfoWithSumAmountByOrderAndCode(orderNo,costCode);
    }
}

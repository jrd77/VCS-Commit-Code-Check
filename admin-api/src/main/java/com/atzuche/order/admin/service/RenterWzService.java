package com.atzuche.order.admin.service;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.vo.req.renterWz.RenterWzCostDetailReqVO;
import com.atzuche.order.admin.vo.resp.renterWz.WzCostLogResVO;
import com.atzuche.order.admin.vo.resp.renterWz.WzCostLogsResVO;
import com.atzuche.order.commons.CompareHelper;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.entity.WzCostLogEntity;
import com.atzuche.order.renterwz.enums.WzCostEnums;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.renterwz.service.WzCostLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * RenterWzService
 *
 * @author shisong
 * @date 2020/1/6
 */
@Service
public class RenterWzService {

    @Resource
    private RenterOrderWzCostDetailService renterOrderWzCostDetailService;

    @Resource
    private WzCostLogService wzCostLogService;

    @Resource
    private RenterGoodsService renterGoodsService;

    @Resource
    private RenterMemberService renterMemberService;

    private static final String WZ_OTHER_FINE_REMARK = "其他扣款备注";
    private static final String WZ_OTHER_FINE = "其他扣款";
    private static final String WZ_OTHER_FINE_CODE = "5";

    private static final String INSURANCE_CLAIM_REMARK = "保险理赔备注";
    private static final String INSURANCE_CLAIM = "保险理赔";
    private static final String INSURANCE_CLAIM_CODE = "6";

    private static final String REMARK = "remark";
    private static final String AMOUNT = "amount";

    private static final String SOURCE_TYPE_CONSOLE = "2";


    public void updateWzCost(String orderNo, List<RenterWzCostDetailReqVO> costDetails) {
        //只会处理其他扣款 和 保险理赔
        for (RenterWzCostDetailReqVO costDetail : costDetails) {
            if(!WZ_OTHER_FINE_CODE.equals(costDetail.getCostCode()) && !INSURANCE_CLAIM_CODE.equals(costDetail.getCostCode())){
                continue;
            }
            RenterOrderWzCostDetailEntity fromDb = new RenterOrderWzCostDetailEntity();
            if(WZ_OTHER_FINE_CODE.equals(costDetail.getCostCode())){
                fromDb = renterOrderWzCostDetailService.queryInfoByOrderAndCode(orderNo, costDetail.getCostCode());
            }
            if(INSURANCE_CLAIM_CODE.equals(costDetail.getCostCode())){
                //TODO 调用其他接口
            }
            try {
                RenterOrderWzCostDetailEntity fromApp = new RenterOrderWzCostDetailEntity();
                BeanUtils.copyProperties(costDetail,fromApp);
                Map<String,String> paramNames = this.getParamNamesByCode(costDetail.getCostCode());
                CompareHelper<RenterOrderWzCostDetailEntity> compareHelper = new CompareHelper<>(fromDb,fromApp,paramNames);
                String content = compareHelper.compare();
                if(StringUtils.isNotBlank(content)){
                    //记录日志 并且做修改费用处理
                    if(WZ_OTHER_FINE_CODE.equals(costDetail.getCostCode())){
                        updateCostStatus(orderNo, costDetail, fromDb);
                    }
                    if(INSURANCE_CLAIM_CODE.equals(costDetail.getCostCode())){
                        //TODO 调用其他接口
                    }
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
        RenterOrderWzCostDetailEntity entityByType = getEntityByType(costDetail.getCostCode(), orderNo, costDetail.getAmount(), carNum, memNo);
        renterOrderWzCostDetailService.saveRenterOrderWzCostDetail(entityByType);
    }

    private RenterOrderWzCostDetailEntity getEntityByType(String code,String orderNo,String amount,String carNum, Integer memNo){
        String authName = AdminUserUtil.getAdminUser().getAuthName();
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
        entity.setCreateOp(authName);
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
        WzCostLogResVO vo = null;
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
}

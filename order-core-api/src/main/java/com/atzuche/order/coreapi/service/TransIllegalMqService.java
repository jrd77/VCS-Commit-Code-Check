package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterwz.entity.RenterOrderWzDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzIllegalPhotoEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzStatusEntity;
import com.atzuche.order.renterwz.entity.RenyunSendIllegalInfoLogEntity;
import com.atzuche.order.renterwz.service.*;
import com.atzuche.order.renterwz.vo.IllegalHandleMqVO;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TransIllegalMqService
 *
 * @author shisong
 * @date 2019/12/28
 */
@Service
public class TransIllegalMqService {

    private static final Logger logger = LoggerFactory.getLogger(TransIllegalMqService.class);

    @Resource
    private RenyunSendIllegalInfoLogService renyunSendIllegalInfoLogService;

    @Resource
    private RenterOrderWzDetailService renterOrderWzDetailService;

    @Resource
    private RenterOrderWzSettleFlagService renterOrderWzSettleFlagService;

    @Resource
    private RenterOrderWzStatusService renterOrderWzStatusService;

    @Resource
    private RenterOrderWzIllegalPhotoService renterOrderWzIllegalPhotoService;

    @Resource
    private RenterOrderWzCostDetailService renterOrderWzCostDetailService;

    @Resource
    private RenterGoodsService renterGoodsService;

    @Resource
    private RenterMemberService renterMemberService;

    private static Gson gson = new Gson();

    private static final String REN_YUN_MQ_INFO_ORDER_NO = "orderno";
    private static final String REN_YUN_MQ_INFO_WZ_INFO = "wzinfo";
    private static final String REN_YUN_MQ_INFO_WZ_ADDR = "wziaddr";
    private static final String REN_YUN_MQ_INFO_WZ_CODE = "wzicode";
    private static final String REN_YUN_MQ_INFO_WZ_CONTENT = "wzicontent";
    private static final String REN_YUN_MQ_INFO_WZ_FINES = "wzifines";
    private static final String REN_YUN_MQ_INFO_WZ_POINTS = "wzipoints";
    private static final String REN_YUN_MQ_INFO_WZ_TIME = "wzitime";

    /**
     * 是否有违章：1-未通知，2-有违章，3-无违章
     */
    private static final int HAS_ILLEGAL_YES=2;
    private static final int HAS_ILLEGAL_NO=3;
    /**
     * 是否有违章扣款金额：0-无，1-有
     */
    private static final int ILLEGAL_COST_YES=1;
    private static final String ILLEGAL_MQ="处理仁云推送违章MQ";
    private static final String ILLEGAL_COST_MQ="处理仁云推送违章扣款金额MQ";

    public void renYunIllegalInfo(String messageBody) {
        try {
            JSONObject jsonObject = JSONObject.fromObject(messageBody);
            if(jsonObject == null){
                return;
            }
            String orderNo = jsonObject.getString(REN_YUN_MQ_INFO_ORDER_NO);
            //根据订单号 查询最后一个有效子订单的车牌
            String carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
            //就算之前存在 ，仍需要记录 这是日志表
            isNeedHandle(messageBody,"01",carNum);

            List<RenterOrderWzDetailEntity> illegals=new ArrayList<>();
            logger.info("处理仁云违章MQ：order_no={}",orderNo);
            if(!"".equals(jsonObject.get(REN_YUN_MQ_INFO_WZ_INFO))){
                JSONArray jsonArray = JSONArray.fromObject(jsonObject.get(REN_YUN_MQ_INFO_WZ_INFO).toString());
                for(int j=0;j<jsonArray.size();j++){
                    JSONObject job = jsonArray.getJSONObject(j);
                    RenterOrderWzDetailEntity illegal=new RenterOrderWzDetailEntity();
                    illegal.setIllegalAddr(job.getString(REN_YUN_MQ_INFO_WZ_ADDR));
                    illegal.setOrderNo(orderNo);
                    illegal.setCarPlateNum(carNum);
                    illegal.setRyWzCode(job.getString(REN_YUN_MQ_INFO_WZ_CODE));
                    illegal.setIllegalReason(job.getString(REN_YUN_MQ_INFO_WZ_CONTENT));
                    illegal.setIllegalAmt(job.getString(REN_YUN_MQ_INFO_WZ_FINES));
                    illegal.setIllegalDeduct(job.getString(REN_YUN_MQ_INFO_WZ_POINTS));
                    illegal.setIllegalTime(DateUtils.parseDate(job.getString(REN_YUN_MQ_INFO_WZ_TIME), "yyyy-MM-dd HH:mm:ss"));
                    illegal.setCreateTime(new Date());
                    illegal.setOrderFlag(1);
                    illegals.add(illegal);
                }
                logger.info("处理仁云违章MQ：order_no={},carNum={},有违章",orderNo,carNum);
                //记录有违章
                renterOrderWzSettleFlagService.updateIsIllegal(orderNo,carNum,HAS_ILLEGAL_YES,ILLEGAL_MQ);
                logger.info("处理仁云违章MQ：order_no={},carNum={},有违章,设置transIllegalSettleFlag-has_illegal状态为2",orderNo,carNum);
            }else{
                logger.info("处理仁云违章MQ：order_no={},carNum={},无违章",orderNo,carNum);
                //记录无违章
                renterOrderWzSettleFlagService.updateIsIllegal(orderNo,carNum,HAS_ILLEGAL_NO,ILLEGAL_MQ);
                logger.info("处理仁云违章MQ：order_no={},carNum={},无违章,设置transIllegalSettleFlag-has_illegal状态为3",orderNo,carNum);
            }
            if(CollectionUtils.isEmpty(illegals)) {
                renterOrderWzStatusService.updateTransIllegalQuery(3,orderNo,carNum);
            }else{
                //不为空，仁云给出违章查询结果后，需将违章表中之前的数据置为无效
                renterOrderWzDetailService.updateIsValid(orderNo,carNum);
                for (RenterOrderWzDetailEntity illegal : illegals) {
                    renterOrderWzDetailService.addIllegalDetailFromRenyun(illegal);
                }
                //修改订单违章查询状态为，有违章
                renterOrderWzStatusService.updateTransIllegalQuery(4,orderNo,carNum);
                renterOrderWzStatusService.updateTransIllegalStatus(25,orderNo,carNum);

            }
        }catch (Exception e){
            logger.error("处理：仁云流程系统同步订单违章信息MQ,报错:[{}]",e);
            throw e;
        }
    }

    private boolean isNeedHandle(String messageBody,String dataType,String carNum) {
        try {
            RenyunSendIllegalInfoLogEntity log = gson.fromJson(messageBody, RenyunSendIllegalInfoLogEntity.class);
            String wzCode = log.getWzcode();
            String orderNo = log.getOrderno();
            if (renyunSendIllegalInfoLogService.count(wzCode, dataType,carNum) > 0) {
                logger.error("MQ在日志表中已存在，违章唯一标示：{}，MQ类型：{},carNum: {}", wzCode, dataType,carNum);
            }
            log.setDataType(dataType);
            log.setJsonData(messageBody);
            log.setOrderno(orderNo);
            log.setCarPlateNum(carNum);
            log.setCreateTime(new Date());
            renyunSendIllegalInfoLogService.saveRenyunSendIllegalInfoLog(log);
            return true;
        }catch (Exception e){
            logger.error("MQ在插入日志表失败，MQ类型：{},messageBody:{},报错：{}",  dataType,messageBody,e);
            throw e;
        }
    }

    public void renYunIllegalQuotedPrice(String messageBody) {
        try {
            JSONObject jsonObject = JSONObject.fromObject(messageBody);
            if(jsonObject == null){
                return;
            }
            String orderNo = jsonObject.getString(REN_YUN_MQ_INFO_ORDER_NO);
            //根据订单号 查询最后一个有效子订单的车牌
            String carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
            String memNoStr = renterMemberService.getRenterNoByOrderNo(orderNo);
            Integer memNo = null;
            if(StringUtils.isNotBlank(memNoStr)){
                memNo = Integer.parseInt(memNoStr);
            }
            //就算之前存在 ，仍需要记录 这是日志表
            isNeedHandle(messageBody,"02",carNum);

            String wzCode=jsonObject.getString(REN_YUN_MQ_INFO_WZ_CODE);
            //校验订单是否已结算
            if(renterOrderWzSettleFlagService.getIllegalSettleFlag(orderNo,carNum)>0){
                throw new RuntimeException("订单号："+orderNo+"，违章编号："+wzCode+"，订单违章已结算，不做数据处理，请流程系统检查数据");
            }

            Integer wzslCostInt = 0;
            //违章处理费
            String wzTotalCost = jsonObject.get("wztotalcost") == null ? null : jsonObject.get("wztotalcost").toString();
            //不良用车处罚金
            String wzTotalFines = jsonObject.getString("wztotalfines");
            //凹凸代办服务费
            String wzTotalsFee = jsonObject.getString("wztotalsfee");
            /*
             * 1.校验违章处理费、不良用车处罚金、凹凸代办服务费 是否有金额为0
             * 2.针对不良用车罚金的变更
             * 3.针对凹凸代办服务费的变更
             */
            if(convertIntString(wzTotalCost)<=0||convertIntString(wzTotalFines)!=0||convertIntString(wzTotalsFee)!=0){
                throw new RuntimeException("订单号："+orderNo+"，违章编号："+wzCode+"，违章处理费、不良用车处罚金、凹凸代办服务费，其中一项或多项为空，请流程系统检查数据");
            }
            //协助违章处理费
            Integer wzTotalCostInt = 0;
            //违章超证费
            String wzslCost = jsonObject.get("wzslcost") == null ? null : jsonObject.get("wzslcost").toString();
            if(StringUtils.isNotBlank(wzTotalCost)) {
                wzTotalCostInt = Double.valueOf(wzTotalCost).intValue();
            }
            if(StringUtils.isNotBlank(wzslCost)) {
                wzslCostInt = Double.valueOf(wzslCost).intValue();
                if(wzslCostInt != null && wzslCostInt > 0) {
                    wzTotalCostInt += wzslCostInt;
                }
            }
            List<RenterOrderWzDetailEntity> illegals=new ArrayList<>();
            if(jsonObject.get("wzbinfo") != null && !"".equals(jsonObject.get("wzbinfo").toString())){
                JSONArray jsonArray = JSONArray.fromObject(jsonObject.get("wzbinfo").toString());
                for(int j=0;j<jsonArray.size();j++) {
                    JSONObject job = jsonArray.getJSONObject(j);
                    RenterOrderWzDetailEntity bo = new RenterOrderWzDetailEntity();
                    bo.setRyWzCode(job.getString("wzbcode"));
                    bo.setIllegalFine(convertIntString(job.getString("wzbcost")));
                    bo.setIllegalDysFine(convertIntString(job.getString("wzbfines")));
                    bo.setIllegalServiceCost(convertIntString(job.getString("wzbsfee")));
                    illegals.add(bo);
                }
            }

            logger.info("仁云流程系统同步违章报价信息反馈MQ,操作renter_order_wz_cost_detail表~~~~~strart~~,orderNo=[{}],wzFine=[{}],wzDysFine=[{}],wzServiceCost=[{}]",orderNo,wzTotalCostInt,wzTotalFines,wzTotalsFee);
            int upCount=renterOrderWzCostDetailService.updateTransFeeByOrderNo(orderNo,wzTotalCostInt,convertIntString(wzTotalFines),convertIntString(wzTotalsFee),carNum,memNo);
            if(upCount>0){
                //修改违章扣款金额成功，记录变更违章扣款记录状态
                renterOrderWzSettleFlagService.updateIsIllegalCost(orderNo,ILLEGAL_COST_YES,ILLEGAL_COST_MQ,carNum);
            }
            logger.info("仁云流程系统同步违章报价信息反馈MQ,更新trans表~~~~~end~~,orderNo={},",orderNo);
            if(!CollectionUtils.isEmpty(illegals)) {
                int itemCount = 0;
                for (RenterOrderWzDetailEntity illegal : illegals) {
                    itemCount++;
                    if(itemCount == 1 && wzslCostInt > 0) {
                        //只给第一个违章赋值
                        illegal.setIllegalSupercerCost(wzslCostInt);
                    }
                    logger.info("仁云流程系统同步违章报价信息反馈MQ,更新TransIllegalDetail表~~~~~strart~~,orderNo={},illegal={}",orderNo,illegal);
                    renterOrderWzDetailService.updateFeeByWzCode(illegal);
                    logger.info("仁云流程系统同步违章报价信息反馈MQ,更新TransIllegalDetail表~~~~~end~~,orderNo={}",orderNo);
                }
            }
        }catch (Exception e){
            logger.error("处理：仁云流程系统同步违章报价信息反馈MQ,报错:{}",e);
            throw e;
        }
    }

    private int convertIntString(String intStr){
        if(StringUtils.isBlank(intStr)){
            return 0;
        }
        //判断是否有小数点
        if(intStr.contains(".")){
            String subStr= intStr.substring(0,(intStr.indexOf(".")));
            return Integer.parseInt(subStr);
        }
        return Integer.parseInt(intStr);
    }

    public void renYunFeedbackVoucher(String messageBody) {
        try {
            JSONObject jsonObject = JSONObject.fromObject(messageBody);
            if(jsonObject == null){
                return;
            }
            String orderNo = jsonObject.getString(REN_YUN_MQ_INFO_ORDER_NO);
            //根据订单号 查询最后一个有效子订单的车牌
            String carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
            //就算之前存在 ，仍需要记录 这是日志表
            isNeedHandle(messageBody,"03",carNum);

            List<RenterOrderWzIllegalPhotoEntity> photos=new ArrayList<>();
            if(!"".equals(jsonObject.get("imagePath"))){
                JSONArray jsonArray = JSONArray.fromObject(jsonObject.get("imagePath").toString());
                for(int j=0;j<jsonArray.size();j++) {
                    JSONObject job = jsonArray.getJSONObject(j);
                    RenterOrderWzIllegalPhotoEntity photo=new RenterOrderWzIllegalPhotoEntity();
                    photo.setPath(job.getString("img"));
                    photo.setUserType(convertIntString(job.getString("userType")));
                    photo.setCreateTime(new Date());
                    photo.setOrderNo(orderNo);
                    photo.setCarPlateNum(carNum);
                    photo.setCreateOp("renyunMq");
                    photos.add(photo);
                }
            }
            if(!CollectionUtils.isEmpty(photos)){
                for (RenterOrderWzIllegalPhotoEntity photo : photos) {
                    Integer maxSerialNum=renterOrderWzIllegalPhotoService.getMaxSerialNum(orderNo,photo.getUserType(),photo.getCarPlateNum());
                    String img=photo.getPath()!=null ? photo.getPath() : "";
                    //处理图片地址，改绝对路径为相对路径
                    if(StringUtils.isNotBlank(img)&&img.indexOf(".com/")>0){
                        img=img.substring(img.indexOf(".com/")+5);
                    }else{
                        img="";
                    }
                    //校验图片是否已存
                    Integer count=renterOrderWzIllegalPhotoService.countPhoto(orderNo,img,photo.getCarPlateNum());
                    if(count != null && count>0){
                        //图片存在不保存
                        continue;
                    }
                    photo.setPath(img);
                    photo.setSerialNumber(maxSerialNum==null?0:maxSerialNum);
                    renterOrderWzIllegalPhotoService.insertPhotoRenYunMq(photo);
                }
            }
        }catch (Exception e){
            logger.error("处理：仁云流程系统同步违章凭证信息反馈MQ,报错:{}",e);
            throw e;
        }
    }

    public void renYunFeedbackIllegalHandle(String messageBody) {
        try {
            IllegalHandleMqVO illegalHandleBo = gson.fromJson(messageBody, IllegalHandleMqVO.class);
            if (illegalHandleBo == null) {
                logger.error("处理：仁云流程系统同步单违章处理方MQ,数据转换错误，illegalQuotedPriceBo==null");
                return;
            }
            //根据订单号 查询最后一个有效子订单的车牌
            String orderNo=illegalHandleBo.getOrderno();
            String carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
            //就算之前存在 ，仍需要记录 这是日志表
            isNeedHandle(messageBody,"04",carNum);

            String wzclParty=illegalHandleBo.getWzclparty();
            int i;
            switch(wzclParty){
                case "租客办理":i=1;break;
                case "凹凸办理":i=2;break;
                case "车主办理":i=3;break;
                default:i=0;break;
            }
            if(i == 0){
                logger.error("处理方不正确，无法解析，处理方：{}",wzclParty);
                return;
            }
            renterOrderWzStatusService.updateIllegalHandle(i,orderNo,carNum);
            //查询是否有违章处理结束时间，有不修改违章处理状态，无修改违章处理状态
            RenterOrderWzStatusEntity dto = renterOrderWzStatusService.selectByOrderNo(orderNo,carNum);
            if(dto == null ){
                return;
            }
            Date wzHandleCompleteTime = dto.getWzHandleCompleteTime();
            if(wzHandleCompleteTime==null){
                //修改违章处理状态
                int status = 0;
                switch(i){
                    case 1:status=25;break;
                    case 2:status=40;break;
                    case 3:status=26;break;
                    default:status=0;break;
                }
                renterOrderWzStatusService.updateStatusByOrderNoAndCarNum(orderNo,status,carNum);
            }
        }catch (Exception e){
            logger.error("处理：仁云流程系统同步单违章处理方MQ,报错:{}",e);
            throw e;
        }
    }
}

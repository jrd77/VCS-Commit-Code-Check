package com.atzuche.order.coreapi.service;

import com.atzuche.order.cashieraccount.service.CashierRefundApplyService;
import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.coreapi.entity.request.IllegalAppealReqVO;
import com.atzuche.order.coreapi.entity.vo.res.IllegalOrderInfoResVO;
import com.atzuche.order.coreapi.entity.vo.res.TransIllegalDetailResVO;
import com.atzuche.order.coreapi.enums.OrderStatusEnums;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterwz.entity.*;
import com.atzuche.order.renterwz.service.*;
import com.atzuche.order.renterwz.vo.PhotoUploadVO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * RenterOrderWzService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class RenterOrderWzService {

    private static final Logger logger = LoggerFactory.getLogger(RenterOrderWzService.class);

    @Resource
    private RenterOrderWzIllegalPhotoService renterOrderWzIllegalPhotoService;

    @Resource
    private TransIllegalSendAliYunMq transIllegalSendAliYunMq;

    @Resource
    private OssService ossService;

    @Resource
    private OwnerMemberService ownerMemberService;

    @Resource
    private RenterMemberService renterMemberService;

    @Resource
    private RenterGoodsService renterGoodsService;

    @Resource
    private RenterOrderWzStatusService renterOrderWzStatusService;

    @Resource
    private RenterOrderWzCostDetailService renterOrderWzCostDetailService;

    @Resource
    private OrderService orderService;

    @Resource
    private RenterOrderService renterOrderService;

    @Resource
    private OrderStatusService orderStatusService;

    @Resource
    private CashierRefundApplyService cashierRefundApplyService;

    @Resource
    private RenterOrderWzDetailService renterOrderWzDetailService;

    @Resource
    private IllegalAppealService illegalAppealService;

    private static final Integer SUCCESS_STATUS = 200;
    private static final Integer FAILED_STATUS = 500;

    /**图片上传张数上限**/
    public static final int IMAGE_UPLOAD_LIMIT = 35;


    /**
     *  500 系统内部异常 200 成功 -1  阿里云上传失败  -2 上传数量大于35张 -3订单不存在 -4您只能上传自己的违章照片
     * @param photoUpload 照片实体
     * @return 状态码
     * @throws Exception 异常
     */
    public Integer upload(PhotoUploadVO photoUpload) throws Exception{
        String orderNo = photoUpload.getOrderNo();
        String basePath = CommonUtils.createTransBasePath(orderNo);
        String str = UUID.randomUUID().toString();
        String key = basePath+"illegal/"+str+".jpg";
        String serialNumber = photoUpload.getSerialNumber();
        String userType = photoUpload.getUserType();
        String carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
        logger.info("上传文件。。orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);

        Integer result  = validateOrderInfo(photoUpload.getMemNo(),orderNo,Integer.parseInt(userType));
        if (!SUCCESS_STATUS.equals(result)) {
            return result;
        }
        //生成原图片
        result = ossService.upload(key, photoUpload.getPhotoContent());
        if (SUCCESS_STATUS.equals(result)) {
            logger.info("上传文件到阿里云成功。。。。。orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);
            RenterOrderWzIllegalPhotoEntity photo = new RenterOrderWzIllegalPhotoEntity();
            Date date = new Date();
            photo.setOrderNo(orderNo);
            photo.setPath(key);
            photo.setUserType(Integer.valueOf(photoUpload.getUserType()));
            photo.setCarPlateNum(carNum);
            photo.setCreateTime(date);
            photo.setUpdateTime(date);
            photo.setUpdateOp(String.valueOf(photoUpload.getMemNo()));
            photo.setCreateOp(String.valueOf(photoUpload.getMemNo()));

            int i = 0;
            //如果没有传入serialNumber则是新增图片，如果传入了替换图片
            if (StringUtils.isNotEmpty(serialNumber) ) {
                logger.info("替换图片：{},orderNo:{},userType:{}",serialNumber,orderNo,userType);
                RenterOrderWzIllegalPhotoEntity illegalPhoto  =  renterOrderWzIllegalPhotoService.getIllegalPhotoBy(orderNo,Integer.parseInt(userType),Integer.parseInt(serialNumber),carNum);
                if (illegalPhoto!=null) {
                    //保证图片存储于数据库一致
                    ossService.deleteOSSObject(illegalPhoto.getPath());
                    photo.setSerialNumber(Integer.parseInt(serialNumber));
                    i = renterOrderWzIllegalPhotoService.update(photo);
                }else{
                    if (Integer.parseInt(photoUpload.getUserType()) != 3) {
                        if (renterOrderWzIllegalPhotoService.countIllegalPhoto(orderNo,Integer.parseInt(photoUpload.getUserType()),carNum) >= IMAGE_UPLOAD_LIMIT) {
                            return -2;
                        }
                    }
                    photo.setSerialNumber(Integer.parseInt(serialNumber));
                    i = renterOrderWzIllegalPhotoService.insert(photo);
                }
            }else{
                if (Integer.parseInt(photoUpload.getUserType()) != 3) {
                    if (renterOrderWzIllegalPhotoService.countIllegalPhoto(orderNo,Integer.parseInt(photoUpload.getUserType()),carNum) >= IMAGE_UPLOAD_LIMIT) {
                        return -2;
                    }
                }
                Integer num = renterOrderWzIllegalPhotoService.getMaxSerialNum(orderNo,Integer.parseInt(userType),carNum);
                photo.setSerialNumber((num!=null?num:0)+1);
                i = renterOrderWzIllegalPhotoService.insert(photo);
            }
            if (i>0) {
                result = SUCCESS_STATUS;
                logger.info("记录文件到数据成功。。orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);
                //保存成功，发送上传图片给仁云
                transIllegalSendAliYunMq.transIllegalPhotoToRenYun(photo);
            }else {
                //保证图片存储于数据库一致
                ossService.deleteOSSObject(key);
                result = FAILED_STATUS;
            }
        }else{
            logger.warn("上传文件失败orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);
        }
        return result;

    }

    private Integer validateOrderInfo(Integer memNo, String orderNo, int userType) {
        String renterNo = renterMemberService.getRenterNoByOrderNo(orderNo);
        String ownerNo = ownerMemberService.getOwnerNoByOrderNo(orderNo);
        if (StringUtils.isBlank(renterNo) && StringUtils.isBlank(ownerNo)) {
            return -3;
        }else {
            String memNoStr = String.valueOf(memNo);
            if (!memNoStr.equals(renterNo) && !memNoStr.equals(ownerNo)) {
                return -4;
            }
            if (userType == 1 && !memNoStr.equals(renterNo)) {
                //租客
                return -4;
            }
            if (userType == 2 && !memNoStr.equals(ownerNo)) {
                //车主
                return -4;
            }
        }
        return SUCCESS_STATUS;
    }

    public List<IllegalOrderInfoResVO> getIllegalOrderListByMemNo(String memNo) {
        List<RenterOrderWzStatusEntity> wzStatusEntities = renterOrderWzStatusService.queryIllegalOrderListByMemNo(memNo);
        if(CollectionUtils.isEmpty(wzStatusEntities)){
            return new ArrayList<>();
        }
        List<IllegalOrderInfoResVO> results = new ArrayList<>();
        IllegalOrderInfoResVO result;
        for (RenterOrderWzStatusEntity wzStatusEntity : wzStatusEntities) {
            result = getIllegalOrderInfoResVO(wzStatusEntity);
            results.add(result);
        }
        return results;
    }

    private IllegalOrderInfoResVO getIllegalOrderInfoResVO(RenterOrderWzStatusEntity wzStatusEntity) {
        IllegalOrderInfoResVO result;
        String orderNo = wzStatusEntity.getOrderNo();
        String carNo = wzStatusEntity.getCarNo();
        String ownerNo = wzStatusEntity.getOwnerNo();
        String renterNo = wzStatusEntity.getRenterNo();
        //查询 车主信息
        OwnerMemberEntity owner = ownerMemberService.queryOwnerInfoByOrderNoAndOwnerNo(orderNo,ownerNo);
        //查询 租客信息
        RenterMemberEntity renter = renterMemberService.queryRenterInfoByOrderNoAndRenterNo(orderNo,renterNo);
        //查询 车辆信息
        RenterGoodsEntity car = renterGoodsService.queryCarInfoByOrderNoAndCarNo(orderNo,carNo);
        //查询 订单信息
        OrderEntity order = orderService.getOrderEntity(orderNo);
        //查询 费用
        List<RenterOrderWzCostDetailEntity> wzCosts = renterOrderWzCostDetailService.queryInfosByOrderNo(orderNo);
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        result = convertTo(wzStatusEntity,owner,renter,car,order,wzCosts,renterOrder);
        //订单状态
        Integer status = orderStatusService.getStatusByOrderNo(orderNo);
        result.setStatus(OrderStatusEnums.getOldStatus(status));
        Date wzAmtReturnTime = cashierRefundApplyService.queryRefundTimeByOrderNo(orderNo, DataPayKindConstant.DEPOSIT);
        result.setWzAmtReturnTime(wzAmtReturnTime);
        result.setOrderNo(orderNo);
        return result;
    }

    private static final String WZ_FINE = "100040";
    private static final String WZ_DYS_FINE = "100042";
    private static final String WZ_SERVICE_COST = "100041";
    private static final String WZ_STOP_COST = "100043";
    private static final String WZ_OTHER_FINE = "100044";
    private static final String INSURANCE_CLAIM = "100045";

    private IllegalOrderInfoResVO convertTo(RenterOrderWzStatusEntity wzStatusEntity, OwnerMemberEntity owner, RenterMemberEntity renter, RenterGoodsEntity car, OrderEntity order, List<RenterOrderWzCostDetailEntity> wzCosts, RenterOrderEntity renterOrder){
        IllegalOrderInfoResVO result = new IllegalOrderInfoResVO();
        if(wzStatusEntity != null){
            result.setWzHandleCompleteTime(wzStatusEntity.getWzHandleCompleteTime());
            result.setIllegalQuery(String.valueOf(wzStatusEntity.getIllegalQuery()));
            result.setWzDisposeStatus(String.valueOf(wzStatusEntity.getStatus()));
        }
        if(!CollectionUtils.isEmpty(wzCosts)){
            for (RenterOrderWzCostDetailEntity wzCost : wzCosts) {
                if(wzCost == null){
                    continue;
                }
                if(WZ_FINE.equalsIgnoreCase(wzCost.getCostCode())&& wzCost.getAmount() != null){
                    result.setWzFine(String.valueOf(wzCost.getAmount()));
                }else if(WZ_DYS_FINE.equalsIgnoreCase(wzCost.getCostCode()) && wzCost.getAmount() != null){
                    result.setWzDysFine(String.valueOf(wzCost.getAmount()));
                }else if(WZ_SERVICE_COST.equalsIgnoreCase(wzCost.getCostCode()) && wzCost.getAmount() != null){
                    result.setWzServiceCost(String.valueOf(wzCost.getAmount()));
                }else if(WZ_STOP_COST.equalsIgnoreCase(wzCost.getCostCode()) && wzCost.getAmount() != null){
                    result.setWzOffStreamCost(String.valueOf(wzCost.getAmount()));
                }else if(WZ_OTHER_FINE.equalsIgnoreCase(wzCost.getCostCode()) && wzCost.getAmount() != null){
                    result.setOtherDeductionAmt(String.valueOf(wzCost.getAmount()));
                }else if(INSURANCE_CLAIM.equalsIgnoreCase(wzCost.getCostCode()) && wzCost.getAmount() != null){
                    result.setInsuranceClaimAmt(String.valueOf(wzCost.getAmount()));
                }
            }
        }
        if(renter != null){
            result.setRenterName(renter.getRealName());
            result.setRenterNo(renter.getMemNo());
            result.setRenterPhone(renter.getPhone());
        }
        if(owner != null){
            result.setOwnerNo(owner.getMemNo());
            result.setOwnerPhone(owner.getPhone());
        }
        if(order != null){
            if(order.getExpRentTime() != null){
                result.setRentTime(DateUtils.formate(order.getExpRentTime(),DateUtils.DATE_DEFAUTE));
            }
            if(order.getExpRevertTime() != null){
                result.setRevertTime(DateUtils.formate(order.getExpRevertTime(),DateUtils.DATE_DEFAUTE));
            }
            result.setSource(order.getSource());
        }
        if(renterOrder != null){
            if(renterOrder.getActRentTime() != null){
                result.setRealRentTime(DateUtils.formate(renterOrder.getActRentTime(),DateUtils.DATE_DEFAUTE));
            }
            if(renterOrder.getActRevertTime() !=null){
                result.setRealRevertTime(DateUtils.formate(renterOrder.getActRevertTime(),DateUtils.DATE_DEFAUTE));
            }
        }
        if(car != null){
            result.setCarBrand(car.getCarBrandTxt());
            result.setCarNo(String.valueOf(car.getCarNo()));
            result.setCarType(car.getCarTypeTxt());
            result.setCarPlateNum(car.getCarPlateNum());
        }
        return result;
    }

    public List<TransIllegalDetailResVO> findTransIllegalDetailByOrderNo(String orderNo) {
        List<RenterOrderWzDetailEntity> wzDetailEntities = renterOrderWzDetailService.findTransIllegalDetailByOrderNo(orderNo);
        if(CollectionUtils.isEmpty(wzDetailEntities)){
            return new ArrayList<>();
        }
        List<TransIllegalDetailResVO> results = new ArrayList<>();
        for (RenterOrderWzDetailEntity wzDetailEntity : wzDetailEntities) {
            TransIllegalDetailResVO dto = new TransIllegalDetailResVO();
            BeanUtils.copyProperties(wzDetailEntity,dto);
            if(StringUtils.isNotBlank(wzDetailEntity.getIllegalAmt())){
                dto.setIllegalAmt(convertIntString(wzDetailEntity.getIllegalAmt()));
            }
            if(StringUtils.isNotBlank(wzDetailEntity.getIllegalDeduct())){
                dto.setIllegalDeduct(convertIntString(wzDetailEntity.getIllegalDeduct()));
            }
            results.add(dto);
        }
        return results;
    }

    private int convertIntString(String intStr){
        if(org.apache.commons.lang.StringUtils.isBlank(intStr)){
            return 0;
        }
        //判断是否有小数点
        if(intStr.contains(".")){
            String subStr= intStr.substring(0,(intStr.indexOf(".")));
            return Integer.parseInt(subStr);
        }
        return Integer.parseInt(intStr);
    }

    public IllegalOrderInfoResVO getOrderInfoByOrderNo(String orderNo) {
        RenterOrderWzStatusEntity wzStatusEntity = renterOrderWzStatusService.getOrderInfoByOrderNo(orderNo);
        return getIllegalOrderInfoResVO(wzStatusEntity);
    }

    /**
     *  500 系统内部异常 200 成功 -1  阿里云上传失败  -2 上传数量大于35张 -3订单不存在 -4您只能上传自己的违章照片 -5上传交接车,picKey为空!
     * @param photoUpload 照片实体
     * @return 状态码
     * @throws Exception 异常
     */
    public Integer uploadV47(PhotoUploadVO photoUpload) {
        String orderNo = photoUpload.getOrderNo();
        // 上传图片key，前端传过来的图片相对路径
        String picKey = photoUpload.getPicKey();
        String carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
        if(org.apache.commons.lang.StringUtils.isBlank(picKey)){
            logger.error("上传交接车,picKey为空!");
            return -5;
        }
        String serialNumber = photoUpload.getSerialNumber();
        String userType = photoUpload.getUserType();
        logger.info("上传违章照片。。orderNo : {},serialNumber:{},userType:{},picKey={}",orderNo,serialNumber,userType,picKey);
        Integer result  = validateOrderInfo(photoUpload.getMemNo(),orderNo,Integer.parseInt(userType));
        if (!SUCCESS_STATUS.equals(result)) {
            return result;
        }
        RenterOrderWzIllegalPhotoEntity photo = new RenterOrderWzIllegalPhotoEntity();
        Date date = new Date();
        photo.setOrderNo(orderNo);
        photo.setPath(picKey);
        photo.setUserType(Integer.valueOf(photoUpload.getUserType()));
        photo.setCarPlateNum(carNum);
        photo.setCreateTime(date);
        photo.setUpdateTime(date);;
        photo.setUpdateOp(String.valueOf(photoUpload.getMemNo()));
        photo.setCreateOp(String.valueOf(photoUpload.getMemNo()));

        int i = 0;
        //如果没有传入serialNumber则是新增图片，如果传入了替换图片
        if (StringUtils.isNotEmpty(serialNumber) ) {
            logger.info("替换图片：{},orderNo:{},userType:{}",serialNumber,orderNo,userType);
            RenterOrderWzIllegalPhotoEntity illegalPhoto  =  renterOrderWzIllegalPhotoService.getIllegalPhotoBy(orderNo,Integer.parseInt(userType),Integer.parseInt(serialNumber),carNum);
            if (illegalPhoto!=null) {
                //保证图片存储于数据库一致
                ossService.deleteOSSObject(illegalPhoto.getPath());
                photo.setSerialNumber(Integer.parseInt(serialNumber));
                i = renterOrderWzIllegalPhotoService.update(photo);
            }else{
                if (Integer.parseInt(photoUpload.getUserType()) != 3) {
                    if (renterOrderWzIllegalPhotoService.countIllegalPhoto(orderNo,Integer.parseInt(photoUpload.getUserType()),carNum) >= IMAGE_UPLOAD_LIMIT) {
                        return -2;
                    }
                }
                photo.setSerialNumber(Integer.parseInt(serialNumber));
                i = renterOrderWzIllegalPhotoService.insert(photo);
            }

        }else{
            if (Integer.parseInt(photoUpload.getUserType()) != 3) {
                if (renterOrderWzIllegalPhotoService.countIllegalPhoto(orderNo,Integer.parseInt(photoUpload.getUserType()),carNum) >= IMAGE_UPLOAD_LIMIT) {
                    return -2;
                }
            }
            Integer num = renterOrderWzIllegalPhotoService.getMaxSerialNum(orderNo,Integer.parseInt(userType),carNum);
            photo.setSerialNumber((num!=null?num:0)+1);
            i = renterOrderWzIllegalPhotoService.insert(photo);
        }
        if (i>0) {
            result = SUCCESS_STATUS;
            logger.info("记录文件到数据成功。。orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);
            //保存成功，发送上传图片给仁云
            transIllegalSendAliYunMq.transIllegalPhotoToRenYun(photo);
        }
        return result;
    }

    public Integer getIllegalDetailCount(String orderNo, String illegalNum) {
        return renterOrderWzDetailService.getIllegalDetailCount(orderNo,illegalNum);
    }

    public Integer getIllegalAppealCount(String orderNo, String illegalNum) {
        return illegalAppealService.getIllegalAppealCount(orderNo,illegalNum);
    }

    public void insertIllegalAppeal(IllegalAppealReqVO illegalAppeal) {
        IllegalAppealEntity entity = new IllegalAppealEntity();
        BeanUtils.copyProperties(illegalAppeal,entity);
        illegalAppealService.insertIllegalAppeal(entity);
    }
}

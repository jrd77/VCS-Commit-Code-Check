package com.atzuche.violation.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.enums.WzLogOperateTypeEnums;
import com.atzuche.order.commons.vo.req.ViolationReqVO;
import com.atzuche.order.commons.vo.res.ViolationResVO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.mapper.OrderStatusMapper;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzStatusEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzCostDetailMapper;
import com.atzuche.order.renterwz.mapper.RenterOrderWzDetailMapper;
import com.atzuche.order.renterwz.mapper.RenterOrderWzStatusMapper;
import com.atzuche.order.renterwz.service.RenterOrderWzDetailService;
import com.atzuche.violation.common.AdminUserUtil;
import com.atzuche.violation.common.AnnotationHandler;
import com.atzuche.violation.common.CommonUtil;
import com.atzuche.violation.common.FileUtil;
import com.atzuche.violation.common.xlsx.ExportExcelUtil;
import com.atzuche.violation.common.xlsx.ExportExcelWrapper;
import com.atzuche.violation.common.xlsx.ImportExcel;
import com.atzuche.order.commons.entity.wz.RenterOrderWzDetailLogEntity;
import com.atzuche.violation.enums.OilCostTypeEnum;
import com.atzuche.violation.enums.WzCostEnums;
import com.atzuche.violation.enums.WzInfoStatusEnum;
import com.atzuche.violation.enums.WzStatusEnums;
import com.atzuche.violation.exception.ViolationManageException;
import com.atzuche.violation.vo.req.RenterWzCostDetailReqVO;
import com.atzuche.violation.vo.req.ViolationDetailReqVO;
import com.atzuche.violation.vo.resp.RenterOrderWzDetailResVO;
import com.atzuche.violation.vo.resp.ViolationExportResVO;
import com.atzuche.violation.vo.resp.ViolationResDesVO;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.commons.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author 胡春林
 */
@Service
@Slf4j
public class ViolationInfoService {

    @Resource
    RenterOrderWzDetailMapper renterOrderWzDetailMapper;
    @Resource
    RenterOrderWzStatusMapper renterOrderWzStatusMapper;
    @Resource
    OrderStatusMapper orderStatusMapper;
    @Resource
    RenterOrderWzCostDetailMapper renterOrderWzCostDetailMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    WzService renterWzService;
    @Autowired
    RenterOrderWzDetailLogService renterOrderWzDetailLogService;
    @Autowired
    RenterOrderWzDetailService renterOrderWzDetailService;

    private static final String ORDER_CENTER_WZ_WITHHOLD_EXCHANGE = "auto-order-center-wz";
    private static final String ORDER_CENTER_WZ_WITHHOLD_ROUTING_KEY = "order.center.wz.with.hold.feedback";

    /**
     * 明细列表
     * @param violationDetailReqVO
     * @return
     */
    public List<RenterOrderWzDetailResVO> detailList(ViolationDetailReqVO violationDetailReqVO){
        List<RenterOrderWzDetailEntity> renterOrderWzDetailEntities = renterOrderWzDetailMapper.queryAllList(violationDetailReqVO.getOrderNo());
        List<RenterOrderWzDetailResVO> renterOrderWzDetailResVOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(renterOrderWzDetailEntities)) {
            log.info("没有违章明细数据，violationDetailReqVO--->>>>[{}]", violationDetailReqVO.getOrderNo());
            return renterOrderWzDetailResVOS;
        }
        renterOrderWzDetailEntities.stream().forEach(r -> {
            RenterOrderWzDetailResVO renterOrderWzDetailRes = new RenterOrderWzDetailResVO();
            CommonUtil.copyPropertiesIgnoreNull(r, renterOrderWzDetailRes);
            try {
                renterOrderWzDetailRes.setIllegalTime(DateUtils.formate(r.getIllegalTime(), DateUtils.DATE_DEFAUTE1));
            } catch (Exception e) {
                log.info("違章時間爲空");
            }
            List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetailEntities = renterOrderWzCostDetailMapper.queryInfosByOrderNo(r.getOrderNo());
            if (!CollectionUtils.isEmpty(renterOrderWzCostDetailEntities)) {
                renterOrderWzCostDetailEntities.stream().filter(s -> s.getCostCode().equals(WzCostEnums.WZ_FINE.getCode())).findFirst().ifPresent(getCrashVO -> {
                    renterOrderWzDetailRes.setIllegalFine(getCrashVO.getAmount());
                });
                renterOrderWzCostDetailEntities.stream().filter(s -> s.getCostCode().equals(WzCostEnums.WZ_SERVICE_COST.getCode())).findFirst().ifPresent(getCrashVO -> {
                    renterOrderWzDetailRes.setIllegalServiceCost(getCrashVO.getAmount());
                });
                renterOrderWzCostDetailEntities.stream().filter(s -> s.getCostCode().equals(WzCostEnums.WZ_DYS_FINE.getCode())).findFirst().ifPresent(getCrashVO -> {
                    renterOrderWzDetailRes.setIllegalDysFine(getCrashVO.getAmount());
                });
            }
            renterOrderWzDetailResVOS.add(renterOrderWzDetailRes);
        });
        return renterOrderWzDetailResVOS;
    }

    /**
     * 列表
     * @param violationReqVO
     * @return
     */
    public List<ViolationResVO> list(ViolationReqVO violationReqVO) {
        violationReqVO.setCarTypeArr(StringUtils.isBlank(violationReqVO.getCarType())?null:violationReqVO.getCarType().split(","));
        violationReqVO.setUserCarCityArr(StringUtils.isBlank(violationReqVO.getUseCarCity())?null:violationReqVO.getUseCarCity().split(","));
        PageHelper.startPage(violationReqVO.getPageNum(),violationReqVO.getPageSize());
        List<ViolationResVO> violationResDesVOList = renterOrderWzStatusMapper.queryIllegalOrderList(violationReqVO);
        for (ViolationResVO violationResVO: violationResDesVOList) {
            violationResVO.setOrderType("普通订单");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(violationResVO.getWzInfo())) {
                violationResVO.setWzInfo(WzInfoStatusEnum.getStatusDesc(Integer.valueOf(violationResVO.getWzInfo())));
            }else {
                violationResVO.setWzInfo(WzInfoStatusEnum.getStatusDesc(3));
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(violationResVO.getWzStatus())) {
                violationResVO.setWzStatus(WzStatusEnums.getStatusDesc(Integer.valueOf(violationResVO.getWzStatus())));
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(violationResVO.getPowerType())) {
                violationResVO.setPowerType(OilCostTypeEnum.getOilCostType(Integer.valueOf(violationResVO.getPowerType())));
            }
            OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByOrderNo(violationResVO.getOrderNo());
            String orderDetain = "未暂扣";
            if (orderStatusEntity.getIsDetainWz() == OrderConstant.ONE) {
                orderDetain = "已暂扣";
            } else if(orderStatusEntity.getIsDetainWz() == OrderConstant.TWO) {
                orderDetain = "撤销暂扣";
            }
            violationResVO.setWzDepositStatus(orderDetain);
            violationResVO.setCarType(CarOwnerTypeEnum.getNameByCode(Integer.valueOf(violationResVO.getCarType())));
            violationResVO.setWzProcessedProof("0".equals(violationResVO.getWzProcessedProof()) ? "无" : "有");
        }
        return  violationResDesVOList;
    }

    /**
     * 导出数据
     * @param violationReqVO
     */
    public void export(ViolationReqVO violationReqVO, HttpServletResponse response){
        long startTime = System.currentTimeMillis();
        log.info("查询出的数据,开始时间：{}",startTime);
        violationReqVO.setPageSize(1000);
        violationReqVO.setPageNum(1);
        List<ViolationResVO> violationResDesVOList = list(violationReqVO);
        if(CollectionUtils.isEmpty(violationResDesVOList))
        {
            log.info("数据源为空,incomeAuditResponseVO:[{}]",violationResDesVOList.toString());
        }
        List<ViolationExportResVO> violationExportResVOS = Lists.newArrayList();
        for (ViolationResVO violationResVO : violationResDesVOList) {
            ViolationExportResVO violationExportResVO = new ViolationExportResVO();
            CommonUtil.copyPropertiesIgnoreNull(violationResVO, violationExportResVO);
            List<RenterOrderWzDetailEntity> renterOrderWzDetailEntities = renterOrderWzDetailMapper.findDetailByOrderNo(violationExportResVO.getOrderNo(), violationReqVO.getCarNo());
            if (!CollectionUtils.isEmpty(renterOrderWzDetailEntities)) {
                renterOrderWzDetailEntities.stream().forEach(r -> {
                    violationExportResVO.setIllegalAddr(r.getIllegalAddr());
                    violationExportResVO.setIllegalAmt(r.getIllegalAmt());
                    violationExportResVO.setIllegalDeduct(r.getIllegalDeduct());
                    violationExportResVO.setIllegalDysFine(String.valueOf(r.getIllegalDysFine()));
                    violationExportResVO.setIllegalFine(String.valueOf(r.getIllegalFine()));
                    OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByOrderNo(r.getOrderNo());
                    String orderDetain = "未暂扣";
                    if (orderStatusEntity.getIsDetain().intValue() == 0) {
                        orderDetain = "未暂扣";
                    } else {
                        orderDetain = orderStatusEntity.getIsDetain().intValue() == 1 ? "已暂扣" : "撤销暂扣";
                    }
                    violationExportResVO.setIllegalPauseCost(orderDetain);
                    violationExportResVO.setIllegalReason(r.getIllegalReason());
                    violationExportResVO.setIllegalServiceCost(String.valueOf(r.getIllegalServiceCost()));
                    violationExportResVO.setIllegalSupercerCost(String.valueOf(r.getIllegalSupercerCost()));
                    violationExportResVO.setIllegalTime(String.valueOf(r.getIllegalTime()));
                    violationExportResVO.setId(String.valueOf(r.getId()));
                    violationExportResVOS.add(violationExportResVO);
                });
            } else {
                OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByOrderNo(violationExportResVO.getOrderNo());
                String orderDetain = "未暂扣";
                if (orderStatusEntity.getIsDetain().intValue() == 0) {
                    orderDetain = "未暂扣";
                } else {
                    orderDetain = orderStatusEntity.getIsDetain().intValue() == 1 ? "已暂扣" : "撤销暂扣";
                }
                violationExportResVO.setIllegalPauseCost(orderDetain);
                violationExportResVOS.add(violationExportResVO);
            }
            for (ViolationExportResVO violationExportRes : violationExportResVOS) {
                List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetailEntities = renterOrderWzCostDetailMapper.queryInfosByOrderNo(violationExportRes.getOrderNo());
                if (!CollectionUtils.isEmpty(renterOrderWzCostDetailEntities)) {
                    renterOrderWzCostDetailEntities.stream().filter(s -> s.getCostCode().equals(WzCostEnums.WZ_FINE.getCode())).findFirst().ifPresent(getCrashVO -> {
                        violationExportRes.setIllegalFine(String.valueOf(getCrashVO.getAmount()));
                    });
                    renterOrderWzCostDetailEntities.stream().filter(s -> s.getCostCode().equals(WzCostEnums.WZ_SERVICE_COST.getCode())).findFirst().ifPresent(getCrashVO -> {
                        violationExportRes.setIllegalServiceCost(String.valueOf(getCrashVO.getAmount()));
                    });
                    renterOrderWzCostDetailEntities.stream().filter(s -> s.getCostCode().equals(WzCostEnums.WZ_DYS_FINE.getCode())).findFirst().ifPresent(getCrashVO -> {
                        violationExportRes.setIllegalDysFine(String.valueOf(getCrashVO.getAmount()));
                    });
                }
            }
        }
        log.info("查询出的数据源总长：{},耗时：{}",violationExportResVOS.size() ,System.currentTimeMillis() - startTime);
        ExportExcelWrapper exportExcelWrapper = new ExportExcelWrapper();
        String[] fieldDescription = AnnotationHandler.getFeildDescription(ViolationResDesVO.class);
        log.info("开始进行导出操作，导出的字段名：{}", JSONObject.toJSONString(fieldDescription));
        exportExcelWrapper.exportExcel("违章管理列表数据"+ DateUtil.formatDate(new Date(),DateUtil.BASIC_DATE_TIME_FORMAT),"违章管理列表数据",fieldDescription,violationExportResVOS,response, ExportExcelUtil.EXCEl_FILE_2007);
        log.info("导出成功：文件名：{}","违章管理列表数据"+ DateUtil.formatDate(new Date(),DateUtil.BASIC_DATE_TIME_FORMAT));
    }

    /**
     * 导入数据
     * @param file
     * @param request
     * @return
     */
    public String importExcel(MultipartFile file, HttpServletRequest request) {
        String messageInfo = "";
        try {
            String uploadPath = FileUtil.uploadFile(file, request.getServletContext());
            if (StringUtils.isBlank(uploadPath)) {
                log.info("文件写入失败,fileName-->>", file.getOriginalFilename());
            }
            String path = request.getServletContext().getRealPath("/");
            ImportExcel poi = new ImportExcel();
            List<List<String>> list = poi.read(path + uploadPath);
            if (list == null || list.size() == 0) {
                messageInfo = "您的excel没有数据";
            }
            messageInfo = "数据导入成功\n";
            List<RenterOrderWzDetailEntity> renterOrderWzDetailEntities = Lists.newArrayList();
            for (int i = 0; i < list.size(); i++) {
                try {
                    RenterOrderWzDetailEntity renterOrderWzDetailEntity = new RenterOrderWzDetailEntity();
                    List<String> cellList = list.get(i);
                    renterOrderWzDetailEntity.setOrderNo(cellList.get(2));
                    renterOrderWzDetailEntity.setCarPlateNum(cellList.get(5));
                    renterOrderWzDetailEntity.setOrderFlag(1);
                    renterOrderWzDetailEntity.setIsValid(1);
                    renterOrderWzDetailEntity.setIsSms(0);
                    renterOrderWzDetailEntity.setIsDelete(0);
                    renterOrderWzDetailEntity.setUpdateTime(new Date());
                    renterOrderWzDetailEntity.setId(Long.valueOf(cellList.get(0)));
                    renterOrderWzDetailEntity.setIllegalTime(DateUtil.parseDate(cellList.get(9), "yyyy-MM-dd HH:mm:ss"));
                    renterOrderWzDetailEntity.setIllegalAmt(cellList.get(12));
                    renterOrderWzDetailEntity.setIllegalAddr(cellList.get(10));
                    renterOrderWzDetailEntity.setIllegalReason(cellList.get(11));
                    renterOrderWzDetailEntity.setIllegalDeduct(cellList.get(13));
                    if (Objects.isNull(renterOrderWzDetailEntity.getOrderNo())) {
                        log.info("导入的数据没有订单号{}", JSONObject.toJSONString(cellList));
                        throw new ViolationManageException(ErrorCode.ORDER_QUERY_FAIL.getCode(), "找不到导入数据的订单");
                    }
                    renterOrderWzDetailEntities.add(renterOrderWzDetailEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                    messageInfo += "第" + (i+1) + "条数据不对\n";
                }
            }
            messageInfo +="\n\n" + updateRenterOrderWzDetailInfo(renterOrderWzDetailEntities);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件发生异常，e--->>>>",e);
        }
        return messageInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized String updateRenterOrderWzDetailInfo(List<RenterOrderWzDetailEntity> renterOrderWzDetailEntities){
        String authName = AdminUserUtil.getAdminUser().getAuthName();
        String importMsgInfo = "";
        int a = 0;
        int b = 0;
        if(CollectionUtils.isEmpty(renterOrderWzDetailEntities)){
            return importMsgInfo;
        }
        Iterator<RenterOrderWzDetailEntity> iterator = renterOrderWzDetailEntities.iterator();
        while (iterator.hasNext()){
            RenterOrderWzDetailEntity renterOrderWzDetail = iterator.next();
            Date illegalTime = renterOrderWzDetail.getIllegalTime();
            if(illegalTime != null){
                String IllegalTime = DateUtils.formate(illegalTime, DateUtils.DATE_DEFAUTE1);
                List<RenterOrderWzDetailEntity> repeatData = renterOrderWzDetailService.getRepeatData(renterOrderWzDetail.getOrderNo(),IllegalTime);
                if(repeatData != null && repeatData.size()>=1){
                    log.error("当前违章数据重复,跳过保存data={}",JSON.toJSONString(renterOrderWzDetail));
                    RenterOrderWzDetailLogEntity entity = new RenterOrderWzDetailLogEntity();
                    try{
                        String wzContent = RenterOrderWzDetailLogEntity.getWzContent(DateUtils.formate(renterOrderWzDetail.getIllegalTime(), DateUtils.DATE_DEFAUTE1),
                                renterOrderWzDetail.getIllegalAddr(),
                                renterOrderWzDetail.getIllegalReason(),
                                renterOrderWzDetail.getIllegalAmt(),
                                renterOrderWzDetail.getIllegalDeduct(),
                                renterOrderWzDetail.getIllegalStatus());
                        entity.setOrderNo(renterOrderWzDetail.getOrderNo());
                        entity.setWzDetailId(renterOrderWzDetail.getId());
                        entity.setOperateType(WzLogOperateTypeEnums.SYSTEM_DISTINCT.getCode());
                        entity.setContent(wzContent);
                        entity.setCreateOp(authName);
                        entity.setUpdateOp(authName);
                        log.info("批量导入违章信息记录日志entity={}", JSON.toJSONString(entity));
                        int insert = renterOrderWzDetailLogService.insert(entity);
                        log.info("批量导入违章信息记录日志insert={},entity={}",insert,JSON.toJSONString(entity));
                    }catch (Exception e){
                        log.error("批量导入数据记录日志失败entity={},e",JSON.toJSONString(entity),e);
                    }
                    continue;
                }
            }


            RenterOrderWzDetailEntity renterOrderWzDetailEntity = renterOrderWzDetailMapper.queryRenterOrderWzDetailById(renterOrderWzDetail.getId());
            if (Objects.nonNull(renterOrderWzDetailEntity)) {

                renterOrderWzDetailEntity.setIllegalTime(illegalTime);
                renterOrderWzDetailEntity.setIllegalAmt(renterOrderWzDetail.getIllegalAmt());
                renterOrderWzDetailEntity.setIllegalAddr(renterOrderWzDetail.getIllegalAddr());
                renterOrderWzDetailEntity.setIllegalReason(renterOrderWzDetail.getIllegalReason());
                renterOrderWzDetailEntity.setIllegalDeduct(renterOrderWzDetail.getIllegalDeduct());
                int result = renterOrderWzDetailMapper.updateRenterOrderWzDetailById(renterOrderWzDetailEntity);
                if (result > 0) {
                    a++;
                    sendWeiZhangMessageWithIllegalDeduct(renterOrderWzDetailEntity);
                } else {
                    b++;
                }
            } else {
                int result = renterOrderWzDetailMapper.saveRenterOrderWzDetail(renterOrderWzDetail);
                if (result > 0) {
                    a++;
                    sendWeiZhangMessageWithIllegalDeduct(renterOrderWzDetail);
                } else {
                    b++;
                }
                RenterOrderWzDetailLogEntity entity = new RenterOrderWzDetailLogEntity();
                try{
                    String wzContent = RenterOrderWzDetailLogEntity.getWzContent(DateUtils.formate(renterOrderWzDetailEntity.getIllegalTime(), DateUtils.DATE_DEFAUTE1),
                            renterOrderWzDetailEntity.getIllegalAddr(),
                            renterOrderWzDetailEntity.getIllegalReason(),
                            renterOrderWzDetailEntity.getIllegalAmt(),
                            renterOrderWzDetailEntity.getIllegalDeduct(),
                            renterOrderWzDetailEntity.getIllegalStatus());
                    entity.setOrderNo(renterOrderWzDetailEntity.getOrderNo());
                    entity.setWzDetailId(renterOrderWzDetailEntity.getId());
                    entity.setOperateType(WzLogOperateTypeEnums.ANUAL_BATCH_IMPORT.getCode());
                    entity.setContent(wzContent);
                    entity.setCreateOp(authName);
                    entity.setUpdateOp(authName);
                    log.info("批量导入违章信息记录日志entity={}", JSON.toJSONString(entity));
                    int insert = renterOrderWzDetailLogService.insert(entity);
                    log.info("批量导入违章信息记录日志insert={},entity={}",insert,JSON.toJSONString(entity));
                }catch (Exception e){
                    log.error("批量导入数据记录日志失败entity={},e",JSON.toJSONString(entity),e);
                }
            }
        }
        importMsgInfo = "提示：" + a + "条导入成功。" + b + "条导入失败。";
        return importMsgInfo;
    }

    /**
     * 发送违章大于6分的数据
     * @param renterOrderWzDetail
     */
    public void sendWeiZhangMessageWithIllegalDeduct(RenterOrderWzDetailEntity renterOrderWzDetail){
        if(Integer.valueOf(renterOrderWzDetail.getIllegalDeduct()) >= 6) {
            rabbitTemplate.convertAndSend(ORDER_CENTER_WZ_WITHHOLD_EXCHANGE, ORDER_CENTER_WZ_WITHHOLD_ROUTING_KEY, renterOrderWzDetail.getOrderNo());
        }
        updateTransWzDisposeStatus(renterOrderWzDetail.getOrderNo(),renterOrderWzDetail.getCarPlateNum());
        updateViolationCostHandle(renterOrderWzDetail);
    }

    /**
     * 更新违章处理信息
     * @param renterOrderWzDetail
     */
    public void updateViolationCostHandle(RenterOrderWzDetailEntity renterOrderWzDetail) {
        List<RenterWzCostDetailReqVO> costDetails = new ArrayList<>();
        String orderNo = renterOrderWzDetail.getOrderNo();
        RenterWzCostDetailReqVO wzOtherFine = new RenterWzCostDetailReqVO();
        wzOtherFine.setAmount(renterOrderWzDetail.getIllegalAmt());
        wzOtherFine.setCostCode(WzCostEnums.WZ_COST.getCode());
        wzOtherFine.setCostDesc(WzCostEnums.WZ_COST.getDesc());
        wzOtherFine.setCostType(WzCostEnums.WZ_COST.getType());
        wzOtherFine.setOrderNo(orderNo);
        wzOtherFine.setRemark(WzCostEnums.WZ_COST.getRemark());
        costDetails.add(wzOtherFine);
        renterWzService.updateWzCost(orderNo, costDetails);
    }

    /**
     * 更新违章状态为有违章
     * @param orderNo
     * @param carNumber
     */
    public void updateTransWzDisposeStatus(String orderNo, String carNumber) {
        RenterOrderWzStatusEntity renterOrderWzStatusEntity = new RenterOrderWzStatusEntity();
        renterOrderWzStatusEntity.setOrderNo(orderNo);
        renterOrderWzStatusEntity.setCarPlateNum(carNumber);
        renterOrderWzStatusEntity.setStatus(5);
        renterOrderWzStatusEntity.setIllegalQuery(4);
        renterOrderWzStatusEntity.setStatusDesc(WzStatusEnums.getStatusDesc(5));
        renterOrderWzStatusEntity.setUpdateOp(AdminUserUtil.getAdminUser().getAuthName());
        renterOrderWzStatusMapper.updateOrderWzStatus(renterOrderWzStatusEntity);
    }

}

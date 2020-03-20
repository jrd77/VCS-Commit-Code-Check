package com.atzuche.violation.service;

import com.atzuche.order.commons.vo.req.ViolationReqVO;
import com.atzuche.order.commons.vo.res.ViolationResVO;
import com.atzuche.order.renterwz.entity.RenterOrderWzDetailEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzDetailMapper;
import com.atzuche.order.renterwz.mapper.RenterOrderWzStatusMapper;
import com.atzuche.violation.common.AnnotationHandler;
import com.atzuche.violation.common.CommonUtil;
import com.atzuche.violation.common.FileUtil;
import com.atzuche.violation.common.xlsx.ExportExcelUtil;
import com.atzuche.violation.common.xlsx.ExportExcelWrapper;
import com.atzuche.violation.common.xlsx.ImportExcel;
import com.atzuche.violation.vo.req.ViolationDetailReqVO;
import com.atzuche.violation.vo.resp.RenterOrderWzDetailResVO;
import com.atzuche.violation.vo.resp.ViolationExportResVO;
import com.atzuche.violation.vo.resp.ViolationResDesVO;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.commons.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
    /**
     * 明细列表
     * @param violationDetailReqVO
     * @return
     */
    public List<RenterOrderWzDetailResVO> detailList(ViolationDetailReqVO violationDetailReqVO){
        List<RenterOrderWzDetailEntity> renterOrderWzDetailEntities = renterOrderWzDetailMapper.queryAllList();
        List<RenterOrderWzDetailResVO> renterOrderWzDetailResVOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(renterOrderWzDetailEntities)) {
            log.info("没有违章明细数据，violationDetailReqVO--->>>>[{}]", violationDetailReqVO.getOrderNo());
            return renterOrderWzDetailResVOS;
        }
        renterOrderWzDetailResVOS.stream().forEach(r -> {
            RenterOrderWzDetailResVO renterOrderWzDetailRes = new RenterOrderWzDetailResVO();
            CommonUtil.copyPropertiesIgnoreNull(r, renterOrderWzDetailRes);
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
        PageHelper.startPage(violationReqVO.getPageNum(),violationReqVO.getPageSize());
        List<ViolationResVO> violationResDesVOList = renterOrderWzStatusMapper.queryIllegalOrderList(violationReqVO);
        for (ViolationResVO violationResVO: violationResDesVOList) {
            violationResVO.setOrderType("普通订单");
            violationResVO.setWzProcessedProof(violationResVO.getWzProcessedProof().equals("0") ? "无违章" : "有违章");
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
        List<ViolationResVO> violationResDesVOList = renterOrderWzStatusMapper.queryIllegalOrderList(violationReqVO);
        if(CollectionUtils.isEmpty(violationResDesVOList))
        {
            log.info("数据源为空,incomeAuditResponseVO:[{}]",violationResDesVOList.toString());
        }
        List<ViolationExportResVO> violationExportResVOS = Lists.newArrayList();
        for (ViolationResVO violationResVO : violationResDesVOList) {
            ViolationExportResVO violationExportResVO = new ViolationExportResVO();
            CommonUtil.copyPropertiesIgnoreNull(violationResVO, violationExportResVO);
            List<RenterOrderWzDetailEntity> renterOrderWzDetailEntities = renterOrderWzDetailMapper.findDetailByOrderNo(violationReqVO.getOrderNo(), violationReqVO.getCarNo());
            if (!CollectionUtils.isEmpty(renterOrderWzDetailEntities)) {
                renterOrderWzDetailEntities.stream().forEach(r -> {
                    violationExportResVO.setIllegalAddr(r.getIllegalAddr());
                    violationExportResVO.setIllegalAmt(r.getIllegalAmt());
                    violationExportResVO.setIllegalDeduct(r.getIllegalDeduct());
                    violationExportResVO.setIllegalDysFine(String.valueOf(r.getIllegalDysFine()));
                    violationExportResVO.setIllegalFine(String.valueOf(r.getIllegalFine()));
                    violationExportResVO.setIllegalPauseCost("0");
                    violationExportResVO.setIllegalReason(r.getIllegalReason());
                    violationExportResVO.setIllegalServiceCost(String.valueOf(r.getIllegalServiceCost()));
                    violationExportResVO.setIllegalSupercerCost(String.valueOf(r.getIllegalSupercerCost()));
                    violationExportResVO.setIllegalTime(String.valueOf(r.getIllegalTime()));
                    violationExportResVO.setId(String.valueOf(r.getId()));
                    violationExportResVOS.add(violationExportResVO);
                });
            }
        }
        log.info("查询出的数据源总长：{},耗时：{}",violationExportResVOS.size() ,System.currentTimeMillis() - startTime);
        ExportExcelWrapper exportExcelWrapper = new ExportExcelWrapper();
        String[] fieldDescription = AnnotationHandler.getFeildDescription(ViolationResDesVO.class);
        log.info("开始进行导出操作，导出的字段名：{}",fieldDescription.toString());
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
            String UploadPath = FileUtil.uploadFile(file, request.getServletContext());
            if (StringUtils.isBlank(UploadPath)) {
                log.info("文件写入失败,fileName-->>", file.getOriginalFilename());
            }
            String path = request.getServletContext().getRealPath("/");
            ImportExcel poi = new ImportExcel();
            List<List<String>> list = poi.read(path + UploadPath);
            if (list == null || list.size() == 0) {
                messageInfo = "您的excel没有数据";
            }
            messageInfo = "数据导入成功\n";
            List<RenterOrderWzDetailEntity> renterOrderWzDetailEntities = Lists.newArrayList();
            RenterOrderWzDetailEntity renterOrderWzDetailEntity = new RenterOrderWzDetailEntity();
            for (int i = 0; i < list.size(); i++) {
                try {
                    List<String> cellList = list.get(i);
                    renterOrderWzDetailEntity.setId(Long.valueOf(cellList.get(0)));
                    renterOrderWzDetailEntity.setIllegalTime(DateUtil.parseDate(cellList.get(9), "yyyy-MM-dd HH:mm:ss"));
                    renterOrderWzDetailEntity.setIllegalAmt(cellList.get(12));
                    renterOrderWzDetailEntity.setIllegalAddr(cellList.get(10));
                    renterOrderWzDetailEntity.setIllegalReason(cellList.get(11));
                    renterOrderWzDetailEntity.setIllegalDeduct(cellList.get(13));
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
        String importMsgInfo = "";
        int a = 0;
        int b = 0;
        if(CollectionUtils.isEmpty(renterOrderWzDetailEntities)){
            return importMsgInfo;
        }
        Iterator<RenterOrderWzDetailEntity> iterator = renterOrderWzDetailEntities.iterator();
        while (iterator.hasNext()){
            RenterOrderWzDetailEntity renterOrderWzDetailEntity = renterOrderWzDetailMapper.queryRenterOrderWzDetailById(iterator.next().getId());
            if (Objects.nonNull(renterOrderWzDetailEntity)) {
                int result = renterOrderWzDetailMapper.updateRenterOrderWzDetail(renterOrderWzDetailEntity);
                if (result > 0) {
                    a++;
                } else {
                    b++;
                }
            } else {
                b++;
            }
        }
        importMsgInfo = "提示：" + a + "条导入成功。" + b + "条导入失败。";
        return importMsgInfo;
    }

}

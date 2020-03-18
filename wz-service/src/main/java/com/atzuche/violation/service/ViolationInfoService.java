package com.atzuche.violation.service;

import com.atzuche.violation.common.AnnotationHandler;
import com.atzuche.violation.common.xlsx.ExportExcelUtil;
import com.atzuche.violation.common.xlsx.ExportExcelWrapper;
import com.atzuche.violation.vo.req.ViolationReqVO;
import com.atzuche.violation.vo.resp.ViolationResDesVO;
import com.autoyol.commons.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author 胡春林
 */
@Service
@Slf4j
public class ViolationInfoService {

    /**
     * 导出数据
     * @param violationReqVO
     */
    public void export(ViolationReqVO violationReqVO, HttpServletResponse response){
        long startTime = System.currentTimeMillis();
        log.info("查询出的数据,开始时间：{}",startTime);
        violationReqVO.setPageSize(1000);
        violationReqVO.setPageNum(1);
        log.info("查询出的数据源总长：{},耗时：{}",0 ,System.currentTimeMillis() - startTime);
        ExportExcelWrapper exportExcelWrapper = new ExportExcelWrapper();
        String[] fieldDescription = AnnotationHandler.getFeildDescription(ViolationResDesVO.class);
        log.info("开始进行导出操作，导出的字段名：{}",fieldDescription.toString());
        exportExcelWrapper.exportExcel("违章管理列表数据"+ DateUtil.formatDate(new Date(),DateUtil.BASIC_DATE_TIME_FORMAT),"违章管理列表数据",fieldDescription,null,response, ExportExcelUtil.EXCEl_FILE_2007);
        log.info("导出成功：文件名：{}","违章管理列表数据"+ DateUtil.formatDate(new Date(),DateUtil.BASIC_DATE_TIME_FORMAT));
    }


}

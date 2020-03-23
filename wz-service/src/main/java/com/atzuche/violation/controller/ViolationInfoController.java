package com.atzuche.violation.controller;

import com.atzuche.order.commons.vo.req.ViolationReqVO;
import com.atzuche.order.commons.vo.res.ViolationResVO;
import com.atzuche.violation.cat.CatLogRecord;
import com.atzuche.violation.common.PageModel;
import com.atzuche.violation.exception.ViolationManageException;
import com.atzuche.violation.service.ViolationInfoService;
import com.atzuche.violation.vo.req.ViolationDetailReqVO;
import com.atzuche.violation.vo.resp.RenterOrderWzDetailResVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @author 胡春林
 */
@RequestMapping("/console/order/violation")
@RestController
@AutoDocVersion(version = "违章管理接口文档")
public class ViolationInfoController {
    private static final Logger logger = LoggerFactory.getLogger(ViolationInfoController.class);
    @Autowired
    ViolationInfoService violationInfoService;

    /**
     * 违章管理列表
     * @param violationReqVO
     * @param bindingResult
     * @return
     */
    @AutoDocMethod(description = "违章管理列表", value = "违章管理列表", response = ViolationResVO.class)
    @PostMapping("/list")
    public ResponseData list(@Valid @RequestBody ViolationReqVO violationReqVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try {
            logger.info("违章管理列表入参:{}", violationReqVO.toString());
            List<ViolationResVO> violationResDesVOList = violationInfoService.list(violationReqVO);
            if (CollectionUtils.isEmpty(violationResDesVOList)) {
                logger.info("没有查到违章数据，violationDetailReqVO--->>>>[{}]", violationReqVO.getOrderNo());
                return ResponseData.success();
            }
            PageModel pageModel = new PageModel(violationResDesVOList);
            CatLogRecord.successLog("违章管理列表成功", "console/order/violation/list", violationReqVO);
            return ResponseData.success(pageModel);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("违章管理列表异常",e);
            CatLogRecord.failLog("违章管理列表异常", "console/order/violation/list", violationReqVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(), ErrorCode.SYS_ERROR.getText());
        }
    }

    /**
     * 违章明细管理列表
     * @param violationDetailReqVO
     * @param bindingResult
     * @return
     */
    @AutoDocMethod(description = "违章明细管理列表", value = "违章明细管理列表", response = RenterOrderWzDetailResVO.class)
    @PostMapping("/detailList")
    public ResponseData detailList(@Valid @RequestBody ViolationDetailReqVO violationDetailReqVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try {
            logger.info("违章明细管理列表入参:{}", violationDetailReqVO.toString());
            List<RenterOrderWzDetailResVO> renterOrderWzDetailResVOS = violationInfoService.detailList(violationDetailReqVO);
            if (CollectionUtils.isEmpty(renterOrderWzDetailResVOS)) {
                logger.info("没有查到违章明细数据，violationDetailReqVO--->>>>[{}]", violationDetailReqVO.getOrderNo());
                return ResponseData.success();
            }
            CatLogRecord.successLog("违章明细管理列表成功", "console/order/violation/detailList", violationDetailReqVO);
            return ResponseData.success(renterOrderWzDetailResVOS);
        } catch (Exception e) {
            logger.error("违章明细管理列表异常", e);
            CatLogRecord.failLog("违章明细管理列表异常", "console/order/violation/detailList", violationDetailReqVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(), ErrorCode.SYS_ERROR.getText());
        }
    }

    /**
     * 导出违章管理列表
     * @param violationReqVO
     * @param response
     */
    @AutoDocMethod(description = "导出违章管理列表", value = "导出违章管理列表")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void export(ViolationReqVO violationReqVO, HttpServletResponse response) {
        try {
            violationInfoService.export(violationReqVO, response);
        } catch (Exception e) {
            logger.error("收益审核列表导出excel异常", e);
        }
    }

    /**
     * 导入违章管理列表
     * @param file
     * @return
     */
    @AutoDocMethod(description = "导入违章管理列表数据excel", value = "导入违章管理列表数据excel", response = ResponseData.class)
    @PostMapping("/import")
    public ResponseData importExcel(@RequestParam("xlsfile") MultipartFile file, HttpServletRequest request) {
        try {
            logger.info("导入导入违章管理列表数据excel开始");
            if (null == file || file.isEmpty()) {
                logger.info("没有导入文件");
                return ResponseData.success();
            }
            String messageInfo = violationInfoService.importExcel(file, request);
            ResponseData.createErrorCodeResponse(ErrorCode.SUCCESS.getCode(), messageInfo);
        } catch (Exception e) {
            logger.info("导入批量修改打款状态excel异常", e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(), ErrorCode.SYS_ERROR.getText());
        }
        return ResponseData.success();
    }


    /**
     * 验证参数
     *
     * @param bindingResult
     */
    private void validateParameter(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ViolationManageException(ErrorCode.PARAMETER_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
    }
}

package com.atzuche.order.coreapi.controller;

import com.atzuche.order.coreapi.entity.request.IllegalAppealReqVO;
import com.atzuche.order.coreapi.entity.request.PhotoUploadReqVO;
import com.atzuche.order.coreapi.entity.vo.res.IllegalOrderInfoResVO;
import com.atzuche.order.coreapi.entity.vo.res.TransIllegalDetailResVO;
import com.atzuche.order.coreapi.listener.HandoverCarListener;
import com.atzuche.order.coreapi.service.RenterOrderWzService;
import com.atzuche.order.renterwz.service.RenterOrderWzIllegalPhotoService;
import com.atzuche.order.renterwz.vo.PhotoUploadVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * RenterWzPhotoController
 *
 * @author shisong
 * @date 2019/12/31
 */
@Controller
public class RenterWzPhotoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandoverCarListener.class);

    @Resource
    private RenterOrderWzService renterOrderWzService;

    @ResponseBody
    @RequestMapping(value = "photo/upload",method = RequestMethod.POST)
    @AutoDocMethod(description = "上传凭证", value = "上传凭证", response = Integer.class)
    public ResponseData<Integer> upload(@Valid @RequestBody PhotoUploadReqVO photoUploadReqVo, BindingResult result){
        if (result.hasErrors()) {
            return ResponseData.success(400);
        }
        try {
            PhotoUploadVO photoUploadVO = new PhotoUploadVO();
            BeanUtils.copyProperties(photoUploadReqVo, photoUploadVO,PhotoUploadVO.class);
            //500 系统内部异常 400 参数异常 200 成功 -1  阿里云上传失败  -2 上传数量大于35张 -3订单不存在 -4您只能上传自己的违章照片
            Integer status = renterOrderWzService.upload(photoUploadVO);
            return ResponseData.success(status);
        } catch (Exception e) {
            LOGGER.error("上传凭证 异常 e :",e);
            Cat.logError("上传凭证 异常",e);
            return ResponseData.success(500);
        }
    }

    @ResponseBody
    @RequestMapping(value = "v47/photo/upload",method = RequestMethod.POST)
    @AutoDocMethod(description = "上传凭证", value = "上传凭证", response = Integer.class)
    public ResponseData<Integer> uploadV47(@Valid @RequestBody PhotoUploadReqVO photoUploadReqVo, BindingResult result){
        if (result.hasErrors()) {
            return ResponseData.success(400);
        }
        try {
            PhotoUploadVO photoUploadVO = new PhotoUploadVO();
            BeanUtils.copyProperties(photoUploadReqVo, photoUploadVO,PhotoUploadVO.class);
            //500 系统内部异常 400 参数异常 200 成功 -1  阿里云上传失败  -2 上传数量大于35张 -3订单不存在 -4您只能上传自己的违章照片
            Integer status = renterOrderWzService.uploadV47(photoUploadVO);
            return ResponseData.success(status);
        } catch (Exception e) {
            LOGGER.error("上传凭证 异常 e :",e);
            Cat.logError("上传凭证 异常",e);
            return ResponseData.success(500);
        }
    }

    @ResponseBody
    @RequestMapping(value = "get/illegalOrderList",method = RequestMethod.GET)
    @AutoDocMethod(description = "查询有违章的订单", value = "查询有违章的订单", response = ResponseData.class)
    public ResponseData<List<IllegalOrderInfoResVO>> getIllegalOrderListByMemNo(@RequestParam("memNo") String memNo){
        try {
            List<IllegalOrderInfoResVO> results= renterOrderWzService.getIllegalOrderListByMemNo(memNo);
            return ResponseData.success(results);
        } catch (Exception e) {
            LOGGER.error("查询有违章的订单 异常 e :",e);
            Cat.logError("查询有违章的订单 异常",e);
            return ResponseData.error();
        }
    }

    @ResponseBody
    @RequestMapping(value = "get/illegalDetailList",method = RequestMethod.GET)
    @AutoDocMethod(description = "查询当前订单内的违章", value = "查询当前订单内的违章", response = ResponseData.class)
    public ResponseData<List<TransIllegalDetailResVO>> findTransIllegalDetailByOrderNo(@RequestParam("orderNo") String orderNo){
        try {
            List<TransIllegalDetailResVO> results= renterOrderWzService.findTransIllegalDetailByOrderNo(orderNo);
            return ResponseData.success(results);
        } catch (Exception e) {
            LOGGER.error("查询当前订单内的违章 异常 e :",e);
            Cat.logError("查询当前订单内的违章 异常",e);
            return ResponseData.error();
        }
    }

    @ResponseBody
    @RequestMapping(value = "get/illegalDetail",method = RequestMethod.GET)
    @AutoDocMethod(description = "查询当前订单的违章详情", value = "查询当前订单的违章详情", response = ResponseData.class)
    public ResponseData<IllegalOrderInfoResVO> getOrderInfoByOrderNo(@RequestParam("orderNo") String orderNo){
        try {
            IllegalOrderInfoResVO result= renterOrderWzService.getOrderInfoByOrderNo(orderNo);
            return ResponseData.success(result);
        } catch (Exception e) {
            LOGGER.error("查询当前订单的违章详情 异常 e :",e);
            Cat.logError("查询当前订单的违章详情 异常",e);
            return ResponseData.error();
        }
    }

    @ResponseBody
    @RequestMapping(value = "get/illegalDetailCount",method = RequestMethod.GET)
    @AutoDocMethod(description = "获取是否存在违章记录", value = "获取是否存在违章记录", response = ResponseData.class)
    public ResponseData<Integer> getIllegalDetailCount(@RequestParam("orderNo") String orderNo,@RequestParam("illegalNum") String illegalNum){
        try {
            Integer result= renterOrderWzService.getIllegalDetailCount(orderNo,illegalNum);
            return ResponseData.success(result);
        } catch (Exception e) {
            LOGGER.error("获取是否存在违章记录 异常 e :",e);
            Cat.logError("获取是否存在违章记录 异常",e);
            return ResponseData.error();
        }
    }

    @ResponseBody
    @RequestMapping(value = "get/illegalAppealCount",method = RequestMethod.GET)
    @AutoDocMethod(description = "获取当天已存在的申诉记录数", value = "获取当天已存在的申诉记录数", response = ResponseData.class)
    public ResponseData<Integer> getIllegalAppealCount(@RequestParam("orderNo") String orderNo,@RequestParam("illegalNum") String illegalNum){
        try {
            Integer result= renterOrderWzService.getIllegalAppealCount(orderNo,illegalNum);
            return ResponseData.success(result);
        } catch (Exception e) {
            LOGGER.error("获取当天已存在的申诉记录数 异常 e :",e);
            Cat.logError("获取当天已存在的申诉记录数 异常",e);
            return ResponseData.error();
        }
    }

    @ResponseBody
    @RequestMapping(value = "insert/illegalAppeal",method = RequestMethod.POST)
    @AutoDocMethod(description = "添加申诉记录", value = "添加申诉记录", response = ResponseData.class)
    public ResponseData insertIllegalAppeal(@Valid @RequestBody IllegalAppealReqVO illegalAppeal, BindingResult result){
        if (result.hasErrors()) {
            Optional<FieldError> error = result.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        try {
            renterOrderWzService.insertIllegalAppeal(illegalAppeal);
            return ResponseData.success();
        } catch (Exception e) {
            LOGGER.error("添加申诉记录 异常 e :",e);
            Cat.logError("添加申诉记录 异常",e);
            return ResponseData.error();
        }
    }

}

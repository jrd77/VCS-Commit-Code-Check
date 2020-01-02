package com.atzuche.order.coreapi.controller;

import com.atzuche.order.coreapi.entity.request.PhotoUploadReqVO;
import com.atzuche.order.coreapi.service.RenterOrderWzService;
import com.atzuche.order.renterwz.service.RenterOrderWzIllegalPhotoService;
import com.atzuche.order.renterwz.vo.PhotoUploadVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * RenterWzPhotoController
 *
 * @author shisong
 * @date 2019/12/31
 */
@Controller
public class RenterWzPhotoController {

    @Resource
    private RenterOrderWzService renterOrderWzService;

    @ResponseBody
    @RequestMapping(value = "photo/upload",method = RequestMethod.POST)
    @AutoDocMethod(description = "上传凭证", value = "上传凭证", response = Integer.class)
    public ResponseData upload(@Valid @RequestBody PhotoUploadReqVO photoUploadReqVo, BindingResult result){
        if (result.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        PhotoUploadVO photoUploadVO = new PhotoUploadVO();
        BeanUtils.copyProperties(photoUploadReqVo, photoUploadVO,PhotoUploadVO.class);
        renterOrderWzService.upload(photoUploadVO);


        return new ResponseData(ErrorCode.SUCCESS.getCode(),ErrorCode.SUCCESS.getText());
    }



}

package com.atzuche.order.photo.controller;


import com.atzuche.order.photo.common.AdminUserUtil;
import com.atzuche.order.photo.dto.OrderPhotoDTO;
import com.atzuche.order.photo.dto.TransIllegalPhotoDTO;
import com.atzuche.order.photo.enums.UserTypeEnum;
import com.atzuche.order.photo.exception.OrderPhotoException;
import com.atzuche.order.photo.service.OrderPhotoService;
import com.atzuche.order.photo.util.oss.PicUtils;
import com.atzuche.order.photo.vo.req.*;
import com.atzuche.order.photo.vo.resp.OrderPhotoResponseVO;
import com.atzuche.order.photo.vo.resp.OrderViolationPhotoResponseVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
* @ClassName: OrderPhotoController 
* @Description: (订单照片) 
* @author shixs
* @date 2016年6月23日 下午3:13:12 
*
 */
@Controller
@AutoDocVersion(version = "订单照片接口文档")
@RestController
@RequestMapping("console/order/photo/")
public class OrderPhotoController{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OrderPhotoService orderPhotoService;
	/**
	 * 
	* @Title: queryOrderPhoto 
	* @Description: (查询订单照片) 
	* @param @param orderNo
	* @param @param request
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	@AutoDocMethod(description = "订单照片列表", value = "订单照片列表", response = OrderPhotoResponseVO.class)
	@GetMapping("/list")
	public ResponseData list(@Valid OrderRequestVO orderRequestVO, BindingResult bindingResult){
        validateParameter(bindingResult);
		String orderNo = orderRequestVO.getOrderNo();
	    List<OrderPhotoDTO> getCarList =orderPhotoService.queryGetSrvCarList(orderNo, UserTypeEnum.RENTER.getType());
	    List<OrderPhotoDTO> srvCarList =orderPhotoService.queryGetSrvCarList(orderNo, UserTypeEnum.OWNER.getType());
		OrderViolationPhotoResponseVO orderViolationPhotoResponseVO =orderPhotoService.queryViolationPhotoList(orderNo);
		OrderPhotoResponseVO orderPhotoResponseVO = new OrderPhotoResponseVO();
		orderPhotoResponseVO.setGetCarPhotoList(getCarList);
		orderPhotoResponseVO.setReturnCarPhotoList(srvCarList);
		orderPhotoResponseVO.setOrderViolationPhotoResponseVO(orderViolationPhotoResponseVO);
		return ResponseData.success(orderPhotoResponseVO);
	}
	
	/**
	 * 
	* @Title: uploadOrderPhoto 
	* @Description: (上传订单照片) 
	* @param @param orderNo
	* @param @param uploadType
	* @param @param files
	* @param @param request
	* @param @param redirectAttributes
	* @param @return
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	@PostMapping("upload")
	@AutoDocMethod(description = "上传订单照片", value = "上传订单照片", response = ResponseData.class)
    public ResponseData uploadOrderPhoto(@RequestParam("picFiles") MultipartFile[] files, @Valid OrderUploadRequestVO orderUploadRequestVO, BindingResult bindingResult) throws Exception {
        validateParameter(bindingResult);
	    String operator =  AdminUserUtil.getAdminUser().getAuthName();
			if(files !=null && files.length >0){
				List<MultipartFile> data = new ArrayList<MultipartFile>();
				if(files.length>10 ){
                    return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "一次上传图片最多不能超过10张,请重新选择！");
				}
				for(MultipartFile multifile :files){
					if(multifile !=null && multifile.getSize() != 0){
						try {
							InputStream inputTmp = multifile.getInputStream();
							int fileSize = inputTmp.available();
							logger.info("Upload File size:{}",fileSize);
							if(fileSize > PicUtils.FILE_LIMIT_SIZE){
                                return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "上传文件不能大于3M");
							}
							
							String picType = PicUtils.getPicType((InputStream)inputTmp);
							if(!PicUtils.TYPE_JPG.equals(picType)&&!PicUtils.TYPE_PNG.equals(picType)){
								logger.error("不支持该图片格式！");
                                return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "上传失败,请上传 jpg 或  png 格式的图片");
							}
							data.add(multifile);
						}catch (IOException e) {
							logger.error("上传违章处理图片报IO流异常："+e.getMessage());
                            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "上传违章处理图片报IO流异常");
						}
					}
					
				}
				if(data != null && data.size()>0){
					orderPhotoService.uploadOrderPhoto(data,orderUploadRequestVO.getPhotoType(),orderUploadRequestVO.getOrderNo(), operator);
				}
			}

		return ResponseData.success();
    } 
	/**
	 * 
	* @Title: delOrderPhoto 
	* @Description: (删除订单图片) 
	* @param @param id
	* @param @param phtotoType
	* @param @param request
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	@DeleteMapping("/delete")
	@AutoDocMethod(description = "删除订单照片", value = "删除订单照片", response = ResponseData.class)
	public ResponseData delOrderPhoto(@Valid @RequestBody OrderDeleteRequestVO orderDeleteRequestVO, BindingResult bindingResult){
        validateParameter(bindingResult);
		orderPhotoService.delOrderPhoto(orderDeleteRequestVO.getPhotoType(),orderDeleteRequestVO.getPhotoId());
		return ResponseData.success();
	}


	/**
	 *
	 * @Title: uploadOrderPhoto
	 * @Description: (上传订单照片)
	 * @param @param orderNo
	 * @param @param uploadType
	 * @param @param files
	 * @param @param request
	 * @param @param redirectAttributes
	 * @param @return
	 * @param @throws Exception    设定文件
	 * @return String    返回类型
	 * @throws
	 */
	@PostMapping("update")
	@AutoDocMethod(description = "变更订单照片", value = "变更订单照片", response = ResponseData.class)
	public ResponseData updateOrderPhoto(@RequestParam("picFile") MultipartFile multipartFile, @Valid OrderUpdateRequestVO orderUpdateRequestVO, BindingResult bindingResult) throws Exception {
        validateParameter(bindingResult);
	    String operator =  AdminUserUtil.getAdminUser().getAuthName();

		if(multipartFile !=null){
			try {
				InputStream inputTmp = multipartFile.getInputStream();
				int fileSize = inputTmp.available();
				logger.info("Upload File size:{}",fileSize);
				if(fileSize > PicUtils.FILE_LIMIT_SIZE){
                    return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "上传文件不能大于3M");
				}

				String picType = PicUtils.getPicType((InputStream)inputTmp);
				if(!PicUtils.TYPE_JPG.equals(picType)&&!PicUtils.TYPE_PNG.equals(picType)){
					logger.error("不支持该图片格式！");
                    return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "上传失败,请上传 jpg 或  png 格式的图片");
				}
				orderPhotoService.uploadOrderPhoto(multipartFile, orderUpdateRequestVO, operator);
			}catch (IOException e) {
				logger.error("上传违章处理图片报IO流异常："+e.getMessage());
                return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "上传违章处理图片报IO流异常");

			}
		}
		return ResponseData.success();
	}

    /**
     * 验证参数
     * @param bindingResult
     */
    private void validateParameter(BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new OrderPhotoException(ErrorCode.PARAMETER_ERROR.getCode(),bindingResult.getFieldError().getDefaultMessage());
        }
    }
    @Autowired
	RabbitTemplate rabbitTemplate;

	@GetMapping("/get")
	@AutoDocMethod(description = "变更订单照片", value = "变更订单照片", response = ResponseData.class)
	public void update()
	{

	    //"path":"20/02/73544351200299/condition/b383761c-c10b-47b7-9c17-36e3f15853db.jpg","userType":2,"serialNumber":2,"orderNo":73544351200299,"photoType":2,"id":0,"createTime":"Feb 15, 2020 8:15:24 PM","updateTime":"Feb 15, 2020 8:15:24 PM"
		DeliveryCarConditionPhotoVO transIllegalPhotoDTO = new DeliveryCarConditionPhotoVO();
		transIllegalPhotoDTO.setUserType(1);
		transIllegalPhotoDTO.setOrderNo(42139241200299L);
		transIllegalPhotoDTO.setPath("42139241200299/1581668490412.jpg");
		transIllegalPhotoDTO.setCreateTime(new Date());
		transIllegalPhotoDTO.setPhotoType(1L);
		transIllegalPhotoDTO.setSerialNumber(1);
		transIllegalPhotoDTO.setUpdateTime(new Date());
		rabbitTemplate.convertAndSend("ren_yun_delivery_car_photo_queue1",GsonUtils.toJson(transIllegalPhotoDTO));
	}

}

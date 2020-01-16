package com.atzuche.order.photo.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.JsonUtil;
import com.atzuche.order.photo.dto.OrderPhotoDTO;
import com.atzuche.order.photo.dto.TransIllegalPhotoDTO;
import com.atzuche.order.photo.entity.OrderPhotoEntity;
import com.atzuche.order.photo.dto.PhotoPathDTO;
import com.atzuche.order.photo.enums.UserTypeEnum;
import com.atzuche.order.photo.mapper.OrderPhotoMapper;
import com.atzuche.order.photo.mq.AliyunMnsService;
import com.atzuche.order.photo.util.SysConf;
import com.atzuche.order.photo.util.oss.OSSUtils;
import com.atzuche.order.photo.vo.req.OrderUpdateRequestVO;
import com.atzuche.order.photo.vo.resp.OrderViolationPhotoResponseVO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderPhotoService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OrderPhotoMapper orderPhotoMapper;
	@Autowired
	private AliyunMnsService aliyunMnsService;


	public List<OrderPhotoDTO> queryGetSrvCarList(String orderNo, String type) {
		List<OrderPhotoEntity> photoList = orderPhotoMapper.queryGetSrvCarList(orderNo,type);
		List<OrderPhotoDTO> carPhotoList = new ArrayList();
		if(CollectionUtils.isNotEmpty(photoList)) {
			photoList.forEach(photoEntity -> {
				OrderPhotoDTO orderPhotoDTO = new OrderPhotoDTO();
				BeanUtils.copyProperties(photoEntity, orderPhotoDTO);
				orderPhotoDTO.setUserTypeText(UserTypeEnum.getDescriptionByType(photoEntity.getUserType()));
				orderPhotoDTO.setCreateTime(DateUtils.formate(photoEntity.getUpdateTime(), DateUtils.DATE_DEFAUTE1));
				carPhotoList.add(orderPhotoDTO);
			});
		}
		return carPhotoList;
	}

	public void uploadOrderPhoto(List<MultipartFile> data, String uploadType,
                                   String orderNo, String operator) throws Exception {
		//平台上传
		String userType = UserTypeEnum.PLATFORM.getType();
        for(MultipartFile multifile :data){
        		InputStream input = null;
        		try {
        			String relativePath = orderNo + "/";// 图片相对路径
        			String path = relativePath + System.currentTimeMillis() + ".jpg";// 重命名图片
					input = multifile.getInputStream();
					BufferedImage  bufImg = ImageIO.read(input);//把图片读入到内存中
					//上传小图
					OSSUtils.uploadBufferedImage1(1, path, bufImg);
					//上传中图
					OSSUtils.uploadBufferedImage1(2, path, bufImg);
					//上传大图（原图）
					OSSUtils.uploadBufferedImage1(3, path, bufImg);
					orderPhotoMapper.addUploadOrderPhoto(orderNo, path, uploadType, userType,operator);
				} catch (Exception e) {
					logger.error("",e);
					throw e;
				} finally{
					if(input != null){
						input.close();
					}
				}
        }
        if(uploadType.equals("4")){
			//上传结束，触发发送违章凭证到仁云违章处理系统
			transIllegalPhotoToRenyun(orderNo);
		}
	}


	public OrderViolationPhotoResponseVO queryViolationPhotoList(String orderNo) {
		List<OrderPhotoEntity> violationPhotoList = orderPhotoMapper.queryViolationPhotoList(orderNo);
		OrderViolationPhotoResponseVO orderViolationPhotoResponseVO = new OrderViolationPhotoResponseVO();

		List<OrderPhotoDTO> renterPhotoList = new ArrayList();
		List<OrderPhotoDTO> ownerPhotoList = new ArrayList();
		List<OrderPhotoDTO> platformPhotoList = new ArrayList();
		if(CollectionUtils.isNotEmpty(violationPhotoList)) {
			violationPhotoList.forEach(violationPhoto -> {
				OrderPhotoDTO orderPhotoDTO = new OrderPhotoDTO();
				BeanUtils.copyProperties(violationPhoto, orderPhotoDTO);
				orderPhotoDTO.setCreateTime(DateUtils.formate(violationPhoto.getUpdateTime(),DateUtils.DATE_DEFAUTE1));
				orderPhotoDTO.setUserTypeText(UserTypeEnum.getDescriptionByType(violationPhoto.getUserType()));

				//租客上传
				if(violationPhoto.getUserType().equals(UserTypeEnum.RENTER.getType())){
					renterPhotoList.add(orderPhotoDTO);
				} else if (violationPhoto.getUserType().equals(UserTypeEnum.OWNER.getType())) {
					ownerPhotoList.add(orderPhotoDTO);
				} else {
					platformPhotoList.add(orderPhotoDTO);
				}
			});
		}
		orderViolationPhotoResponseVO.setRenterPhotoList(renterPhotoList);
		orderViolationPhotoResponseVO.setOwnerPhotoList(ownerPhotoList);
		orderViolationPhotoResponseVO.setPlatformPhotoList(platformPhotoList);
		return orderViolationPhotoResponseVO;
	}

	public int delOrderPhoto(String photoType, String id) {
		return orderPhotoMapper.delOrderPhoto(photoType,id);
	}

	public void transIllegalPhotoToRenyun(String orderNo){
		try{
			String wzcode=orderPhotoMapper.queryWzcodeByOrderNo(orderNo+"");
			TransIllegalPhotoDTO transIllegalPhotoBo=new TransIllegalPhotoDTO();
			transIllegalPhotoBo.setWzcode(wzcode);
			transIllegalPhotoBo.setOrderno(orderNo+"");

			List<PhotoPathDTO> photoPathList=orderPhotoMapper.queryIllegalPhotoByOrderNo(orderNo);
			if(photoPathList!=null&&!photoPathList.isEmpty()){
				for(PhotoPathDTO p:photoPathList) {
					p.setImg(OSSUtils.BUCKET_URL+p.getImg());
				}
				transIllegalPhotoBo.setImagePath(photoPathList);
				//发送缴纳凭证MQ
				aliyunMnsService.asyncSend̨MessageToQueue(JsonUtil.toJson(transIllegalPhotoBo), SysConf.MQ_AUTO_RENTER_VOUCHER_QUEUE,true);
			}
			logger.info("发送违章凭证到仁云流程系统-结束");
		}catch (Exception e){
			logger.info("发送违章凭证到仁云流程系统，报错：{}",e);
		}

	}



	public void uploadOrderPhoto(MultipartFile multipartFile,
								 OrderUpdateRequestVO orderUpdateRequestVO, String operator) throws Exception {
		//平台上传
		String userType = UserTypeEnum.PLATFORM.getType();
		InputStream input = null;
		OrderPhotoEntity orderPhotoEntity = orderPhotoMapper.queryPhotoInfo(orderUpdateRequestVO.getPhotoId(), orderUpdateRequestVO.getPhotoType());
		String orderNo = orderPhotoEntity.getOrderNo();
		try {
			String relativePath = orderNo + "/";// 图片相对路径
			String path = relativePath + System.currentTimeMillis() + ".jpg";// 重命名图片
			input = multipartFile.getInputStream();
			BufferedImage  bufImg = ImageIO.read(input);//把图片读入到内存中
			//上传小图
			OSSUtils.uploadBufferedImage1(1, path, bufImg);
			//上传中图
			OSSUtils.uploadBufferedImage1(2, path, bufImg);
			//上传大图（原图）
			OSSUtils.uploadBufferedImage1(3, path, bufImg);
			orderPhotoMapper.updateUploadOrderPhoto(orderUpdateRequestVO.getPhotoId(), path,operator, userType, orderUpdateRequestVO.getPhotoType());
		} catch (Exception e) {
			logger.error("",e);
			throw e;
		} finally{
			if(input != null){
				input.close();
			}
		}

		if(orderPhotoEntity.getPhotoType().equals("4")){
			//上传结束，触发发送违章凭证到仁云违章处理系统
			transIllegalPhotoToRenyun(orderNo);
		}
	}
}

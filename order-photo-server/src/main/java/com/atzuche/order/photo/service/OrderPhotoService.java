package com.atzuche.order.photo.service;

import com.atzuche.order.commons.JsonUtil;
import com.atzuche.order.photo.dto.TransIllegalPhotoDTO;
import com.atzuche.order.photo.entity.OrderPhotoDTO;
import com.atzuche.order.photo.entity.PhotoPathDTO;
import com.atzuche.order.photo.mapper.OrderPhotoMapper;
import com.atzuche.order.photo.mq.AliyunMnsService;
import com.atzuche.order.photo.util.SysConf;
import com.atzuche.order.photo.util.oss.OSSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

@Service
public class OrderPhotoService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OrderPhotoMapper orderPhotoMapper;
	@Autowired
	private AliyunMnsService aliyunMnsService;


	public List<OrderPhotoDTO> queryGetSrvCarList(String orderNo, int type) {
		return orderPhotoMapper.queryGetSrvCarList(orderNo,type);
	}

	public String uploadOrderPhoto(List<MultipartFile> data, String uploadType,
                                   String orderNo, String operator) throws Exception {
		String msg = null;
		//平台上传
		int userType=3;
        for(MultipartFile multifile :data){
        		InputStream input = null;
        		try {
        			String photoType=uploadType;
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
					orderPhotoMapper.addUploadOrderPhoto(orderNo, path, photoType, userType,operator);
					msg="图片上传成功！";
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
		return msg;
	}


	public List<OrderPhotoDTO> queryViolationPhotoList(String orderNo) {
		return orderPhotoMapper.queryViolationPhotoList(orderNo);
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
}

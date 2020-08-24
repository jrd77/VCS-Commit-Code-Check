package com.atzuche.order.photo.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.JsonUtil;
import com.atzuche.order.photo.dto.OrderPhotoDTO;
import com.atzuche.order.photo.dto.TransIllegalPhotoDTO;
import com.atzuche.order.photo.entity.OrderPhotoEntity;
import com.atzuche.order.photo.dto.PhotoPathDTO;
import com.atzuche.order.photo.enums.PhotoTypeEnum;
import com.atzuche.order.photo.enums.UserTypeEnum;
import com.atzuche.order.photo.mapper.OrderPhotoMapper;
import com.atzuche.order.photo.mq.AliyunMnsService;
import com.atzuche.order.photo.util.SysConfig;
import com.atzuche.order.photo.util.oss.OSSUtils;
import com.atzuche.order.photo.vo.req.OrderUpdateRequestVO;
import com.atzuche.order.photo.vo.resp.OrderViolationPhotoResponseVO;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class OrderPhotoService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OrderPhotoMapper orderPhotoMapper;
	@Autowired
	private AliyunMnsService aliyunMnsService;

	@Autowired
	private RenterGoodsService renterGoodsService;
	@Value("${aliyun.oss.url}")
	public String aliyunOssUrl;


	public List<OrderPhotoDTO> queryGetSrvCarList(String orderNo, List<Integer> type) {
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
		String carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
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
					orderPhotoMapper.addUploadOrderPhoto(orderNo, path, uploadType, userType,operator, carNum);
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
				aliyunMnsService.asyncSend̨MessageToQueue(JsonUtil.toJson(transIllegalPhotoBo), SysConfig.voucherQueue,true);
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

		if(orderUpdateRequestVO.getPhotoType().equals("4")){
			//上传结束，触发发送违章凭证到仁云违章处理系统
			transIllegalPhotoToRenyun(orderNo);
		}
	}

	/**
	 * 接受仁云上传的交接车 取还车照片数据
	 */
	public void recevieRenYunDeliveryCarPhoto(OrderPhotoEntity orderPhotoEntity){
	    orderPhotoMapper.addRenYunUploadOrderPhoto(orderPhotoEntity);
	}

    /**
     * 查询符合条件的交接车 取还车数据
     * @param orderPhotoEntity
     * @return
     */
	public OrderPhotoEntity selectObjectByParams(OrderPhotoEntity orderPhotoEntity){
	    return orderPhotoMapper.selectObjectByParams(orderPhotoEntity);
    }

    /**
     * 更新仁云 交接车 取还车数据
     * @return
     */
    public void updateDeliveryCarPhotoInfo(String photoId,String path,String operator,String userType,String photoType,Integer serialNumber){
        orderPhotoMapper.updateUploadRenYunOrderPhoto(photoId,path,operator,userType,photoType,serialNumber);
    }

    public void downLoadImgs(String orderNo,Integer photoType,HttpServletResponse response) throws IOException {
        List<OrderPhotoDTO> getCarList =queryGetSrvCarList(orderNo, Arrays.asList(photoType));
        String zipName = orderNo;
        if(PhotoTypeEnum.GET_CAR_SERVICE_VOUCHER.getType() == photoType){
            zipName += "取车服务凭证";
        }else if(PhotoTypeEnum.RETURN_CAR_SERVICE_VOUCHER.getType() == photoType){
            zipName += "还车服务凭证";
        }
        List<String> urls = Optional
                .ofNullable(getCarList)
                .orElseGet(ArrayList::new)
                .stream().map(x -> aliyunOssUrl + x.getPath())
                .collect(Collectors.toList());
        compressToZip(urls,zipName,orderNo,response);
    }

    /*
     * @Author ZhangBin
     * @Date 2020/8/11 11:28 
     * @Description:
     * filesUrl：需要压缩的图片地址集合
     * zipName: 压缩文件的名称
     * prefixName：单个文件的文件名
     **/
    public void compressToZip(List<String> filesUrl ,String zipName,String prefixName,HttpServletResponse response) throws IOException {
        String[] files = new String[filesUrl.size()];
        filesUrl.toArray(files);
        //2.开始批量下载功能
        try {
            String downloadFilename = zipName+".zip";//文件的名称
            downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            for (int i = 0; i < files.length; i++) {
                try{
                    String suffixType = files[i].substring(files[i].lastIndexOf("."));
                    URL url = new URL(files[i]);

                    zos.putNextEntry(new ZipEntry(prefixName+"-"+i+suffixType));
                    InputStream fis = url.openConnection().getInputStream();
                    byte[] buffer = new byte[1024];
                    int r = 0;
                    while ((r = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, r);
                    }
                    fis.close();
                }catch (Exception e){
                    logger.error("图片异常,跳过文件-files={}",files,e);
                }
            }
            zos.flush();
            zos.close();
        } catch (Exception e) {
            logger.error("图片批量下载异常",e);
            throw e;
        }
    }
}

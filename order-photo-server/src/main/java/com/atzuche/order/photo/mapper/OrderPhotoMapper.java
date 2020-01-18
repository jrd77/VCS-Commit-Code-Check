package com.atzuche.order.photo.mapper;

import com.atzuche.order.photo.entity.OrderPhotoEntity;
import com.atzuche.order.photo.dto.PhotoPathDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderPhotoMapper {

	List<OrderPhotoEntity> queryGetSrvCarList(@Param("orderNo") String orderNo, @Param("type") String type);

	void addUploadOrderPhoto(@Param("orderNo") String orderNo, @Param("path") String path, @Param("photoType") String photoType, @Param("userType") String userType, @Param("operator") String operator, @Param("carPlateNum") String carPlateNum);

	void updateUploadOrderPhoto(@Param("photoId") String photoId, @Param("path") String path, @Param("operator") String operator, @Param("userType") String userType, @Param("photoType") String photoType);


	List<OrderPhotoEntity> queryInsuranceClaimPhotoList(String orderNo);

	List<OrderPhotoEntity> queryViolationPhotoList(String orderNo);

	int delOrderPhoto(@Param("photoType") String photoType, @Param("id") String id);

	/**
	 * 获取仁云违章编码
	 * @param orderNo
	 * @return
	 */
	String queryWzcodeByOrderNo(@Param("orderNo") String orderNo);

	/**
	 * 获取订单违章凭证
	 * @param orderNo
	 * @return
	 */
	 List<PhotoPathDTO> queryIllegalPhotoByOrderNo(@Param("orderNo") String orderNo);


	/**
	 * 获取照片信息
	 * @param photoId
	 * @return
	 */
	OrderPhotoEntity queryPhotoInfo(@Param("photoId") String photoId, @Param("photoType") String photoType);

	int addRenYunUploadOrderPhoto(OrderPhotoEntity orderPhotoEntity);

	OrderPhotoEntity selectObjectByParams(OrderPhotoEntity orderPhotoEntity);





}

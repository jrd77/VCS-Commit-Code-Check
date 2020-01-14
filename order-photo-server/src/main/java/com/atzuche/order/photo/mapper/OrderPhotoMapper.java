package com.atzuche.order.photo.mapper;

import com.atzuche.order.photo.entity.OrderPhotoDTO;
import com.atzuche.order.photo.entity.PhotoPathDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderPhotoMapper {

	List<OrderPhotoDTO> queryGetSrvCarList(@Param("orderNo") String orderNo, @Param("type") int type);

	void addUploadOrderPhoto(@Param("orderNo") String orderNo, @Param("path") String path, @Param("photoType") String photoType, @Param("userType") int userType, @Param("operator") String operator);

	List<OrderPhotoDTO> queryInsuranceClaimPhotoList(String orderNo);

	List<OrderPhotoDTO> queryViolationPhotoList(String orderNo);

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

}

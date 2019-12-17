package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主端交车车信息表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
 * @Description:
 */
@Data
public class OwnerHandoverCarInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 交车类型（车主向租客交车、租客向车主交车、车管家向租客交车，租客向车管家交车）
	 */
	private Integer type;
	/**
	 * 交车时油耗刻度
	 */
	private Integer oilNum;
	/**
	 * 交车时里程数
	 */
	private Integer mileageNum;
	/**
	 * 实际交车地址
	 */
	private String realReturnAddr;
	/**
	 * 实际交车经度
	 */
	private String realReturnAddrLon;
	/**
	 * 实际交车纬度
	 */
	private String realReturnAddrLat;
	/**
	 * 交车时间
	 */
	private LocalDateTime realReturnTime;
	/**
	 * 交车人姓名
	 */
	private String realReturnUserName;
	/**
	 * 交车人电话号码
	 */
	private String realReturnUserPhone;
	/**
	 * 交车人会员号
	 */
	private Integer realReturnMemNo;
	/**
	 * 取车人姓名
	 */
	private String realGetUserName;
	/**
	 * 取车人电话号码
	 */
	private String realGetUserPhone;
	/**
	 * 取车人会员号
	 */
	private Integer realGetMemNo;
	/**
	 * 图片地址
	 */
	private String imageUrl;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}

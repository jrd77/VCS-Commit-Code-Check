package com.atzuche.order.delivery.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主端交车车信息表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 * @Description:
 */
@Data
public class OwnerHandoverCarInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
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
	 * 交车类型 1-车主向租客交车、2-租客向车主交车、3-车管家向车主交车、4-车主向车管家交车
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
	 * 延后时间（分钟）
	 */
	private Integer delayTime;
	/**
	 * 提前时间（分钟）
	 */
	private Integer aheadTime;
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
	 * 图片文件夹路径
	 */
	private String imageUrl;
	/**
	 * 版本号
	 */
	private Integer version;
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
	/**
	 * 消息ID
	 */
	private String msgId;

}

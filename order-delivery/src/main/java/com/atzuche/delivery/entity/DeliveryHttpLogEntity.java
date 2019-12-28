package com.atzuche.delivery.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author 胡春林
 * @date 2019-12-27 16:25:53
 * @Description:
 */
@Data
@ToString
public class DeliveryHttpLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 请求URL
	 */
	private String requestUrl;
	/**
	 * 请求方式
	 */
	private Integer requestMethodType;
	/**
	 * 请求参数
	 */
	private String requestParams;
	/**
	 * 请求接口类别（1：新增 订单，2：更新订单，3：删除订单）
	 */
	private Integer requestCode;
	/**
	 * 返回code码(000000 ：成功）
	 */
	private String returnStatusCode;
	/**
	 * 返回状态说明
	 */
	private String returnStatusType;
	/**
	 * 返回内容
	 */
	private String returnContext;
	/**
	 * 请求发出时间
	 */
	private LocalDateTime sendTime;
	/**
	 * 请求返回时间
	 */
	private LocalDateTime returnTime;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 逻辑删除 0：否 1：是
	 */
	private Integer isDelete;

}

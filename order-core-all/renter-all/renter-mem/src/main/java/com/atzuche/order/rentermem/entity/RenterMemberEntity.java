package com.atzuche.order.rentermem.entity;
import java.util.List;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端会员概览表
 * 
 * @author ZhangBin
 * @date 2019-12-18 16:15:16
 * @Description:
 */
@Data
public class RenterMemberEntity implements Serializable {

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
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 手机号
	 */
	private String phone;
	/**
	 * 头像
	 */
	private String headerUrl;
	/**
	 * 真实姓名
	 */
	private String realName;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 驾驶证初次领证日期
	 */
	private LocalDate certificationTime;
	/**
	 * 成功下单次数
	 */
	private Integer orderSuccessCount;
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

    @org.springframework.beans.factory.annotation.Autowired
    private com.atzuche.order.rentermem.mapper.RenterMemberMapper renterMemberMapper;
}

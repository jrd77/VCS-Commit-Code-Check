package com.atzuche.order.coreapi.entity.dto;

import java.time.LocalDateTime;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderOwnerDTO {

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 车主子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 城市编号
	 */
	private String cityCode;
	/**
	 * 取车标志
	 */
	private Integer srvGetFlag;
	/**
	 * 还车标志
	 */
	private Integer srvReturnFlag;
	/**
	 * 起租时间
	 */
	private LocalDateTime rentTime;
	/**
	 * 还车时间
	 */
	private LocalDateTime revertTime;
	/**
	 * 取车地址
	 */
	private String getCarAddress;
	/**
	 * 取车地址纬度
	 */
    private String getCarLat;
	/**
	 * 取车地址经度
	 */
    private String getCarLon;
	/**
	 * 还车地址
	 */
	private String revertCarAddress;
	/**
	 * 还车地址纬度
	 */
    private String revertCarLat;
	/**
	 * 还车地址经度
	 */
    private String revertCarLon; 
    /**
     * 是否使用特供价（换车用）1-使用，0-不使用
     */
    private Integer useSpecialPriceFlag;
    /**
     * 车辆注册号（换车用）
     */
    private String carNo;
    /**
     * 是否换车操作 （换车用）
     */
    private Boolean transferFlag;
    /**
     * 车主商品信息
     */
    private OwnerGoodsDetailDTO ownerGoodsDetailDTO; 
    /**
     * 有效车主订单信息
     */
    private OwnerOrderEntity ownerOrderEffective;
    /**
     * 车主会员信息
     */
    private OwnerMemberDTO ownerMemberDTO;
    
}

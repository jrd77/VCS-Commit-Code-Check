package com.atzuche.order.transport.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 
 * @comments
 * @author xuyi
 * @version 创建时间：2014年7月22日
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CityDTO {
    
	private int id;
	@ApiModelProperty("城市编码")
	private String code;
	@ApiModelProperty("城市名称")
	private String name;
	@ApiModelProperty("城市备注")
	private String detail;
	@ApiModelProperty("城市中心店纬度")
	private String lat;
	@ApiModelProperty("城市中心店经度")
	private String lon;
	@ApiModelProperty("城市电话区号")
	private String telPrefix;
	@ApiModelProperty("车牌号前缀")
	private String platePrefix;
	private String type;
	@ApiModelProperty("是否支持取送车服务,0:不支持,1:支持")
	private String isSupport;
	private String isOpen;
	private Date createDate;
	private int calculateFlag;
    @ApiModelProperty("城市非托管车取送车服务配置小时数")
	private int trusteeshipNoHour;
    @ApiModelProperty("城市托管车取送车服务配置小时数")
	private int trusteeshipHour;
    @ApiModelProperty("取送车服务的取车费用")
	private int srvGetCost;
    @ApiModelProperty("取送车服务的还车费用")
	private int srvReturnCost;
    @ApiModelProperty("下单时间限制设置（格式：HH:mm:ss）")
	private String orderTimeSet;
    @ApiModelProperty("租车时间限制设置（格式：HH:mm:ss）")
	private String rentTimeSet;


    @ApiModelProperty("还车服务的间隔时间")
	private Integer revertstInterval;
    @ApiModelProperty("还车服务间隔时间内最大服务数量")
	private Integer revertServiceMax ;
    @ApiModelProperty("取车服务的间隔时间")
	private Integer rentstInterval;
    @ApiModelProperty("取车服务的最大服务数")
	private Integer rentServiceMax;
    @ApiModelProperty("城市非托管车取送车服务配置小时数")
	private Integer allowTransaction;
    @ApiModelProperty("提前多少小时下单")
	private Integer beforeTime;
	@ApiModelProperty("是否开放太保代步车服务,0关闭，1开启")
	private Integer isCpicCoupon;
    @ApiModelProperty("工作开始时间")
	private Integer workBeginTime;
    @ApiModelProperty("工作结束时间")
	private Integer workEndTime;
    @ApiModelProperty("区分城市，不同城市提前下单时间要求 —— 下单时间距离取车时间 >= A小时   工作时段")
	private Integer cpicBeforeTimeWork;
    @ApiModelProperty("区分城市，不同城市提前下单时间要求 —— 下单时间距离取车时间 >= A小时  非工作时段")
	private Integer cpicBeforeTimeFree;
	@ApiModelProperty("城市范围")
	private String addressRange;
	@ApiModelProperty("免费取还车范围")
	private String freeAddressRange;
    @ApiModelProperty("城市拼音")
	private String fullLetter;
    private boolean limit;

	/**
	 * 城市的高峰时段，如：09:00-11:30;14:30-17:00
	 */
	private String peakTimeSlot;

	/**
	 * X牌开头非本地,例沪C
	 */
	private String peakStartsWith;

	/**
	 * 非X牌开头非本地,例沪
	 */
	private String peakNotStartsWith;
	
	//提前下单时间，单位小时
	private Integer beforeTransTimeSpan;

	//是否热门城市
	private Integer isHotCity;
	/**无法提供服务的时间起始时间，只显示时间，不显示天,24小时,例如220000*/
	private  Integer unServiceBeginTime;
	/**无法提供服务的时间结束时间,80000*/
	private Integer unServiceEndTime;

}

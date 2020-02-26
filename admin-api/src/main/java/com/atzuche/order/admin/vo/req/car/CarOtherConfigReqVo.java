package com.atzuche.order.admin.vo.req.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CarOtherConfigReqVo {
	@AutoDocProperty(value="车辆注册号,必填")
	@NotNull(message="车辆注册号不能为空")
	private Integer carNo;
	@AutoDocProperty(value="倒车雷达，必填")
	@NotNull(message="倒车雷达不能为空")
	private Integer radar;
	@AutoDocProperty(value="座椅类型，必填")
	@NotNull(message="座椅类型不能为空")
	private Integer seatType;
	@AutoDocProperty(value="座位数，必填")
	@NotNull(message="座位数不能为空")
	private Integer seatNum;
	@AutoDocProperty(value="天窗配置，必填")
	@NotNull(message="天窗配置不能为空")
	private Integer skylightsType;
//	@AutoDocProperty(value="驱动方式，必填")
//	@NotNull(message="驱动方式不能为空")
//	private Integer driveType;
	@AutoDocProperty(value = "新驱动方式(力洋初始化)，3: 前轮驱动，4: 后轮驱动，5: 全时四驱，6: 分时四驱，7: 适时四驱，8四轮驱动，必填")
	@NotNull(message = "驱动方式不能为空")
	private Integer newDriveType;
	@AutoDocProperty(value="（是否有）备用钥匙，必填")
	@NotNull(message="备用钥匙不能为空")
	private Integer spareKey;
	@AutoDocProperty(value="（是否有）导航仪，必填")
	@NotNull(message="导航仪不能为空")
	private Integer avigraphType;
	@AutoDocProperty(value="（是否有）行车记录仪，必填")
	@NotNull(message="行车记录仪不能为空")
	private Integer tachographType;
	@AutoDocProperty(value="有无外接音源接口，必填")
	@NotNull(message="外接音源接口不能为空")
	private Integer audioInterface;
	@AutoDocProperty(value = "行李厢容积")
	private String luggageVolumeText;

	@AutoDocProperty(value = "【AUT-3785 新增】油表总刻度")
	@NotNull(message="油表总刻度不能为空")
	private Integer oilTotalCalibration;
}

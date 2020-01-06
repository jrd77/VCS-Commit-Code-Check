package com.atzuche.order.admin.vo.resp.car;


import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(value= JsonInclude.Include.NON_EMPTY)
@Data
public class CarOtherConfigResVo {
	@AutoDocProperty(value="倒车雷达")
	private String radar;
	@AutoDocProperty(value="倒车雷达名称")
	private String radarNm;
	@AutoDocProperty(value="座椅类型")
	private String seatType;
	@AutoDocProperty(value="座椅类型名称")
	private String seatTypeNm;
	@AutoDocProperty(value="座位数")
	private String seatNum;
	@AutoDocProperty(value="座位数名称")
	private String seatNumNm;
	@AutoDocProperty(value="天窗配置")
	private String skylightsType;
	@AutoDocProperty(value="天窗配置名称")
	private String skylightsTypeNm;
	@AutoDocProperty(value="驱动方式")
	private String driveType;
	@AutoDocProperty(value="驱动方式名称")
	private String driveTypeNm;
	@AutoDocProperty(value = "新驱动方式")
	private String newDriveType;
	@AutoDocProperty(value = "新驱动方式名称")
	private String newDriveTypeNm;
	@AutoDocProperty(value="（是否有）备用钥匙")
	private String spareKey;
	@AutoDocProperty(value="（是否有）备用钥匙名称")
	private String spareKeyNm;
	@AutoDocProperty(value="（是否有）导航仪")
	private String avigraphType;
	@AutoDocProperty(value="（是否有）导航仪名称")
	private String avigraphTypeNm;
	@AutoDocProperty(value="（是否有）行车记录仪")
	private String tachographType;
	@AutoDocProperty(value="（是否有）行车记录仪名称")
	private String tachographTypeNm;
	@AutoDocProperty(value="有无外接音源接口")
	private String audioInterface;
	@AutoDocProperty(value="有无外接音源接口名称")
	private String audioInterfaceNm;
	@AutoDocProperty(value = "行李厢最小容量,单位L")
	private String minLuggageVolume;
	@AutoDocProperty(value = "行李厢最大容量,单位L")
	private String maxLuggageVolume;
	@AutoDocProperty(value = "行李厢容积,单位L")
	private String luggageVolumeText;
	@AutoDocProperty(value = "【AUT-3785 新增】油表总刻度")
	private Integer oilTotalCalibration;
}

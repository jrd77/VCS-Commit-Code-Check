package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;

@JsonInclude(value= JsonInclude.Include.NON_EMPTY)
@Data
public class CarBaseInfoResVo implements java.io.Serializable{
	@AutoDocProperty(value="品牌")
	private String brand;
	@AutoDocProperty(value="品牌名称")
	private String brandNm;
	@AutoDocProperty(value="车型")
	private String carType;
	@AutoDocProperty(value="车型名称")
	private String carTypeNm;
	@AutoDocProperty(value="年款")
	private Integer year;
	@AutoDocProperty(value="月份")
	private Integer month;
	@AutoDocProperty(value="型号")
	private String modelTxt;
	@AutoDocProperty(value="排量")
	private String cylinderCapacity;
	@AutoDocProperty(value="单位")
	private String ccUnit;
	@AutoDocProperty(value="变速箱")
	private String gearboxType;
	@AutoDocProperty(value="变速箱名称")
	private String gearboxTypeNm;
	@AutoDocProperty(value="生产性质")
	private String lyCarParamSource;
	@AutoDocProperty(value="生产性质")
	private String lyCarParamSourceNm;
	@AutoDocProperty(value="车牌号")
	private String plateNum;
	@AutoDocProperty(value="功能类型")
	private String useType;
	@AutoDocProperty(value="功能类型名称")
	private String useTypeNm;
	@AutoDocProperty(value = "车辆等级")
	private String carLevel;
	@AutoDocProperty(value = "车辆等级名称")
	private String carLevelNm;
	@AutoDocProperty(value="车龄")
	private String carAge;
	@AutoDocProperty(value="动力源")
	private String engineSource;
	@AutoDocProperty(value="动力源名称")
	private String engineSourceNm;
	@AutoDocProperty(value="燃料")
	private String engineType;
	@AutoDocProperty(value="燃料名称")
	private String engineTypeNm;
	@AutoDocProperty(value="邮箱容量")
	private String oilVolume;
	@AutoDocProperty(value="颜色")
	private String color;
	@AutoDocProperty(value="颜色")
	private String colorNm;
	@AutoDocProperty(value="发动机号")
	private String engineNum;
	@AutoDocProperty(value="车辆VIN码(车架号)")
	private String vin;
	@AutoDocProperty(value="行驶里程")
	private String mileage;
	@AutoDocProperty(value="行驶里程名称")
	private String mileageNm;
	@AutoDocProperty(value="车辆购置价")
	private String purchasePrice;
	@AutoDocProperty(value="车辆残值")
	private String residualValue;
	private Integer licenseYear;
	@AutoDocProperty(value = "最新里程数")
	private String lastMileage;
	@AutoDocProperty(value = "保费计算用购置价")
	private String inmsrp;


	@AutoDocProperty(value = "平台服务费")
	private String serviceRate;

	private Integer city;

	private String serviceProportion;

	private Integer ownerType;

	private Integer hasUpdateflag;

	@AutoDocProperty(value="是否本地")
	private Integer isLocal;

//	private String carTypeTxt;
//
//	private String brandTxt;

	@NotNull(message="油表总刻度不能为空")
	private Integer oilTotalCalibration;

}

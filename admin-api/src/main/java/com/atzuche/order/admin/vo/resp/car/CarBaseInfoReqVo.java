package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CarBaseInfoReqVo implements java.io.Serializable{
	@AutoDocProperty(value="车辆注册号,必填")
	@NotNull(message="车辆注册号不能为空")
	private Integer carNo;
	@AutoDocProperty(value="品牌,必填")
	private Integer brand;

	@AutoDocProperty(value="[AUT-3787 新增]品牌文本")
	private String brandTxt;

	@AutoDocProperty(value="车型,必填")
	private Integer carType;

	@AutoDocProperty(value="[AUT-3787 新增] 车型文本")
	private String carTypeTxt;

	@AutoDocProperty(value="年款,必填")
	private Integer year;
	@AutoDocProperty(value="月份,必填")
	private Integer month;
	@AutoDocProperty(value="型号,必填")
	//@NotBlank(message="型号不能为空")
	private String modelTxt;
	@AutoDocProperty(value="排量,必填")
	private Double cylinderCapacity;
	@AutoDocProperty(value="单位")
	private String ccUnit;
	@AutoDocProperty(value="变速箱,必填")
	//@NotNull(message="变速箱不能为空")
	private Integer gearboxType;
	@AutoDocProperty(value="生产性质,必填")
	//@NotNull(message="生产性质不能为空")
	private Integer lyCarParamSource;
	//@AutoDocProperty(value="车牌号,必填")
	private String plateNum;
	@AutoDocProperty(value="功能类型,必填")
	//@NotNull(message="功能类型不能为空")
	private Integer useType;
	@AutoDocProperty(value="车辆等级")
	private Integer carLevel;
	@AutoDocProperty(value="动力源,必填")
	//@NotNull(message="动力源不能为空")
	private Integer engineSource;
	@AutoDocProperty(value="燃料,必填")
	//@NotNull(message="燃料不能为空")
	private Integer engineType;
	@AutoDocProperty(value="邮箱容量,必填")
	//@NotNull(message="邮箱容量不能为空")
	private Integer oilVolume;
	//@AutoDocProperty(value="颜色,必填")
	private String color;
	@AutoDocProperty(value="发动机号,必填")
	//@NotBlank(message="发动机号不能为空")
	private String engineNum;
	@AutoDocProperty(value="车辆VIN码(车架号),必填")
	//@NotBlank(message="车辆VIN码不能为空")
	private String vin;
	@AutoDocProperty(value="行驶里程,必填")
	//@NotNull(message="行驶里程不能为空")
	private Integer mileage;
	@AutoDocProperty(value="车辆购置价,必填")
	private Integer purchasePrice;

	@AutoDocProperty(value="是否本地")
	private Integer isLocal;

	@AutoDocProperty(value="[AUT-3787 新增] 功能记录ID 2：车辆详情品牌车型")
	private Integer modelId;

}




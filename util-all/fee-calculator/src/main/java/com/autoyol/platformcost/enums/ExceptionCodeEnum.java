package com.autoyol.platformcost.enums;

public enum ExceptionCodeEnum {
	SUCCESS("000000","success"), 
	SYS_ERROR("999999","系统异常"),
	CONNECTION_ERROR("999998","网络连接错误"),
	FAILED("900000","操作失败！"),
	INPUT_ERROR("400001","输入错误"),
	PARAM_ERROR("400002","参数错误"),
	RENT_COST_ERROR("400400","租金计算错误"),
	INSUR_COST_ERROR("400401","平台保障费计算错误"),
	ABATEMENT_COST_ERROR("400402","全面保障费计算错误"),
	SRV_GET_RETURN_COST_ERROR("400403","取还车费用计算错误"),
	SRV_GET_RETURN_OVER_COST_ERROR("400404","取还车超运能附加费计算错误"),
	EXTRA_DRIVER_INSURE_COST_ERROR("400405","附加驾驶人费用计算错误"),
	CASH_NO_NOT_EXIT("400406","费用项不存在"),
	INSUR_CONFIG_NOT_EXIT("400407","平台保障费单价配置不存在"),
	GUID_PRICE_IS_NULL("400408","车辆指导价不能为空"),
	DAY_PRICE_IS_NULL("400409","车辆平日价不能为空或0"),
	HOLIDAY_PRICE_IS_NULL("400410","车辆节假日价不能为空或0"),
	WEEKEND_PRICE_IS_NULL("400411","车辆周末价不能为空或0"),
	OVER_ORDER_TYPE_IS_NULL("400412","订单类型不能为空"),
	ISPACKAGEORDER_IS_NULL("400413","是否是套餐订单不能为空"),
	CAR_LON_IS_NULL("400414","车辆经度不能为空"),
	CAR_LAT_IS_NULL("400415","车辆纬度不能为空"),
	RENT_TIME_IS_NULL("400416","取车时间不能为空"),
	REVERT_TIME_IS_NULL("400417","还车时间不能为空"),
	CAR_DAY_PRICE_LIST_IS_EMPTY("400418","车辆日期价格列表不能为空"),
	COUNT_RENT_DAY_EXCEPTION("400419","计算租期异常，结果为空"),
	CAL_HOLIDAY_AVERAGE_PRICE_EXCEPTION("400420","计算日均价异常，结果为空"),
	INSURE_CONFIG_LIST_IS_EMPTY("400421","平台保障费配置列表不能为空"),
	INSURE_UNIT_PRICE_EXCEPTION("400422","计算平台保障费单价异常，结果为空"),
	DEPOSIT_CONFIG_LIST_IS_EMPTY("400423","车辆押金配置列表不能为空")
	;
	
	private String code;
	private String text;
	
	ExceptionCodeEnum(String code, String text) {
        this.code = code;   
        this.text = text;   
    }

	public String getCode() {
		return code;
	}

	public String getText() {
		return text;
	}
	
}

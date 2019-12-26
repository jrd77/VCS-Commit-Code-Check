package com.atzuche.order.commons.enums;

public enum ChannelNameTypeEnum {
	DEFAULT("default","默认渠道"),
	APP("app","app渠道"),
	OTA("ota","OTA类渠道"),
	SCOOTER("scooter","代步车渠道");
	
	public final String typeCode;
	public final String typeDesc;
	
	private ChannelNameTypeEnum(String typeCode, String typeDesc) {
		this.typeCode=typeCode;
		this.typeDesc=typeDesc;
	}
	
	public String getTypeCode() {
		return typeCode;
	}
	public String getTypeDesc() {
		return typeDesc;
	}

	public static boolean containsType(String typeCode) {
		for(ChannelNameTypeEnum one : values()) {
			if(one.typeCode.equals(typeCode)) {
				return true;
			}
		}
		return false;
	}
	
	public static ChannelNameTypeEnum getChannelNameType(String typeCode){
		if(typeCode.equals("app")){
			return ChannelNameTypeEnum.APP;
		}else if(typeCode.equals("ota")){
			return ChannelNameTypeEnum.OTA;
		}else if(typeCode.equals("scooter")){
			return ChannelNameTypeEnum.SCOOTER;
		}else{
			return ChannelNameTypeEnum.DEFAULT;
		}
	}
	
}

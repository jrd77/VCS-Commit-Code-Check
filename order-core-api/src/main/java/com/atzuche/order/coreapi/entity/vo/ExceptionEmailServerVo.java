package com.atzuche.order.coreapi.entity.vo;

public class ExceptionEmailServerVo {
	private  String hostName;
	private  String fromAddr;
	private  String fromName;
	private  String fromPwd;
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getFromAddr() {
		return fromAddr;
	}
	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getFromPwd() {
		return fromPwd;
	}
	public void setFromPwd(String fromPwd) {
		this.fromPwd = fromPwd;
	}
	
	
}

package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;

public class AccurateGetReturnSrvVO {

	@AutoDocProperty(value = "精准取车服务费")
    private Integer accurateGetSrvAmt;
    
    @AutoDocProperty(value = "精准还车服务费")
    private Integer accurateReturnSrvAmt;

	public Integer getAccurateGetSrvAmt() {
		return accurateGetSrvAmt;
	}

	public void setAccurateGetSrvAmt(Integer accurateGetSrvAmt) {
		this.accurateGetSrvAmt = accurateGetSrvAmt;
	}

	public Integer getAccurateReturnSrvAmt() {
		return accurateReturnSrvAmt;
	}

	public void setAccurateReturnSrvAmt(Integer accurateReturnSrvAmt) {
		this.accurateReturnSrvAmt = accurateReturnSrvAmt;
	}
    
    

}

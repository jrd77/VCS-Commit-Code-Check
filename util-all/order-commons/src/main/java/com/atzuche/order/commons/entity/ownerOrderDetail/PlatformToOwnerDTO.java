package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class PlatformToOwnerDTO {
    @AutoDocProperty("油费")
    private String oliAmt;
    @AutoDocProperty("超时费用")
    private String timeOut;
    @AutoDocProperty("临时修改订单的时间和地址")
    private String modifyOrderTimeAndAddrAmt;
    @AutoDocProperty("车辆清洗费")
    private String carWash;
    @AutoDocProperty("延误等待费")
    private String dlayWait;
    @AutoDocProperty("停车费")
    private String stopCar;
    @AutoDocProperty("超里程费用")
    private String extraMileage;

    public static void setDefaultValue(PlatformToOwnerDTO oldData){
        oldData.setOliAmt(oldData.getOliAmt()==null?"0":oldData.getOliAmt());
        oldData.setTimeOut(oldData.getTimeOut()==null?"0":oldData.getTimeOut());
        oldData.setModifyOrderTimeAndAddrAmt(oldData.getModifyOrderTimeAndAddrAmt()==null?"0":oldData.getModifyOrderTimeAndAddrAmt());
        oldData.setCarWash(oldData.getCarWash()==null?"0":oldData.getCarWash());
        oldData.setDlayWait(oldData.getDlayWait()==null?"0":oldData.getDlayWait());
        oldData.setStopCar(oldData.getStopCar()==null?"0":oldData.getStopCar());
        oldData.setExtraMileage(oldData.getExtraMileage()==null?"0":oldData.getExtraMileage());
    }
}

package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台下单参数
 *
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 11:13 上午
 **/
@Data
@EqualsAndHashCode(callSuper=true)
public class AdminOrderReqVO extends NormalOrderReqVO {

    private static final long serialVersionUID = -8153539553994615179L;

    @AutoDocProperty(value = "是否使用特供价", required = true)
    private String useSpecialPrice;

    @AutoDocProperty(value = "操作人", required = true)
    private String operator;

    @AutoDocProperty(value = "管理后台下单特殊处理标识", required = true)
    private String specialConsole;

    @AutoDocProperty(value = "线下订单类型", required = true)
    private String offlineOrderStatus;

    @AutoDocProperty(value = "限时红包面额(管理后台)")
    private Integer reductiAmt;



    public String getUseSpecialPrice() {
        return useSpecialPrice;
    }

    public void setUseSpecialPrice(String useSpecialPrice) {
        this.useSpecialPrice = useSpecialPrice;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getSpecialConsole() {
        return specialConsole;
    }

    public void setSpecialConsole(String specialConsole) {
        this.specialConsole = specialConsole;
    }

    public String getOfflineOrderStatus() {
        return offlineOrderStatus;
    }

    public void setOfflineOrderStatus(String offlineOrderStatus) {
        this.offlineOrderStatus = offlineOrderStatus;
    }

    @Override
    public String toString() {
        return "AdminOrderReqVO{" +
                "useSpecialPrice='" + useSpecialPrice + '\'' +
                ", operator='" + operator + '\'' +
                ", specialConsole='" + specialConsole + '\'' +
                ", offlineOrderStatus='" + offlineOrderStatus + '\'' +
                '}';
    }
}

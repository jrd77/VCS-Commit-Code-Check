package com.atzuche.order.admin.vo.request;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Created by qincai.lin on 2019/12/30.
 */
@Data
@ToString
public class CarDisCouponRequestVO implements Serializable {
    @AutoDocProperty(value = "用户注册号")
    private String memNo;

    @AutoDocProperty(value = "车辆注册号")
    private String carNo;

    @AutoDocProperty(value = "起租时间")
    @Pattern(regexp="^[1-9]\\d{3}(0[0-9]|1[0-2])(0[0-9]|[12]\\d|3[01])(0[0-9]|1\\d|2[0-3])(0[0-9]|[1-5]\\d){2}$",message="起租时间格式不正确")
    private String rentTime;//		string	租车起始时间(yyyyMMddHHmmss)
    @AutoDocProperty(value = "结束时间")
    @Pattern(regexp="^[1-9]\\d{3}(0[0-9]|1[0-2])(0[0-9]|[12]\\d|3[01])(0[0-9]|1\\d|2[0-3])(0[0-9]|[1-5]\\d){2}$",message="还车时间格式不正确")
    private String revertTime;//		string	租车结束时间(yyyyMMddHHmmss)


}

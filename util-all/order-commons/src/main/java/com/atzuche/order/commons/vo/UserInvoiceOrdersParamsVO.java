package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @ClassName UserOrdersParams
 * @Author yusong.miao
 * @Date 2019/8/12 10:20
 * @Version 1.0
 */
@Data
public class UserInvoiceOrdersParamsVO {
    @AutoDocProperty(value = "会员号")
    @Pattern(regexp = "^[0-9]*$", message = "用户不存在")
    private String memberNo;

    @AutoDocProperty(value = "根据传入的订单集合,查询这些订单信息")
    private List<String> in;
    private List<String> inOrders;//参数转化 in关键字冲突

    @AutoDocProperty(value = "查询信息时排除的订单")
    private List<String> notIn;

    @AutoDocProperty(value = "开始时间yyyyMMddHHmmss(预留字段,可不传)")
    private String beginTime;

    @AutoDocProperty(value = "页码")
    @Min(value = 1, message = "pageNum最小值1")
    private Integer pageNum = 1;

    @AutoDocProperty(value = "每页数量")
    @Min(value = 1, message = "pageSize最小值1")
    @Max(value = 100, message = "pageSize最大值100")
    private Integer pageSize = 3;

    @AutoDocProperty(value = "结束时间yyyyMMddHHmmss")
    private String finishTimeBegin;

    @AutoDocProperty(value = "开始时间yyyyMMddHHmmss")
    private String finishTimeEnd;

}

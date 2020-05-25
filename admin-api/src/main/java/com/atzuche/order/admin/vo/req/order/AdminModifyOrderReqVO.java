package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 封装管理后台对订单的修改
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/17 4:44 下午
 **/
@Data
@ToString
public class AdminModifyOrderReqVO {
    @NotBlank(message="订单编号不能为空")
    @AutoDocProperty(value="订单编号,必填，",required=true)
    private String orderNo;

    @AutoDocProperty(value="补充全险是否开启，0：否，1：是")
    private Integer abatementFlag;

    @AutoDocProperty(value="取车时间,格式 yyyyMMddHHmmss")
    private String rentTime;

    @AutoDocProperty(value="还车时间,格式 yyyyMMddHHmmss")
    private String revertTime;

    @AutoDocProperty(value="取车地址")
    private String getCarAddress;

    @AutoDocProperty(value="取车地址纬度")
    private String getCarLat;

    @AutoDocProperty(value="取车地址经度")
    private String getCarLon;

    @AutoDocProperty(value="还车地址")
    private String revertCarAddress;

    @AutoDocProperty(value="还车地址纬度")
    private String revertCarLat;

    @AutoDocProperty(value="还车地址经度")
    private String revertCarLon;

    @AutoDocProperty(value="【增加附加驾驶员】附加驾驶员ID列表")
    private List<String> driverIds;
    
    @AutoDocProperty(value="是否购买轮胎保障服务 0-不购买，1-购买")
	private Integer tyreInsurFlag;
	@AutoDocProperty(value="是否购买驾乘无忧保障服务 0-不购买，1-购买")
	private Integer driverInsurFlag;
}

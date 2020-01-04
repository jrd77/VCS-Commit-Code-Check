package com.atzuche.order.admin.vo.rep.delivery;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 胡春林
 * 交接车图片信息
 */
@Data
@ToString
public class CarConditionPhotoUploadVO {

    @ApiModelProperty(value="订单号",required=true)
    @NotBlank(message="orderNo不能为空")
    private String orderNo;
    @ApiModelProperty(value="userType userType只能为1:租客，2:车主",required=true)
    @NotBlank(message="userType不能为空 photoType只能为1：交车2： 取车")
    private String userType;
    @ApiModelProperty(value="photoType photoType只能为1：交车2： 取车",required=true)
    @NotBlank(message="photoType不能为空,photoType只能为1：交车2： 取车")
    private String photoType;
    @ApiModelProperty(value="会员号",required=true)
    @NotBlank
    private Integer memNo;
    @NotBlank(message="photoContent不能为空")
    private String photoContent;
}

package com.atzuche.order.commons.vo.delivery;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author 胡春林
 * 托管车数据
 */
@Data
@ToString
public class OrderCarTrusteeshipVO {

    @ApiModelProperty(value="订单号",required=true)
    @NotBlank(message="orderNo不能为空")
    private String orderNo;
    @ApiModelProperty(value="车牌号",required=true)
    @NotBlank(message="车牌号不能为空")
    private String carNo;
    private String trusteeshipName;
    private String trusteeshipTelephone;
    @ApiModelProperty(value="出库时间",required=true)
    @NotNull(message="出库时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date outDepotTime;
    @ApiModelProperty(value="入库时间",required=true)
    @NotNull(message="入库时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date inDepotTime;
    @ApiModelProperty(value="出库里程数",required=true)
    @NotBlank(message="出库里程数不能为空")
    private String outDepotMileage;
    @ApiModelProperty(value="入库里程数",required=true)
    @NotBlank(message="入库里程数不能为空")
    private String inDepotMileage;
    @ApiModelProperty(value="出库油量: 1:1/16,2:2/16,3:3/16,4:4/16,....",required=true)
    @NotNull(message="出库油量不能为空")
    private Integer outDepotOimass;
    @ApiModelProperty(value="入库油量:1:1/16,2:2/16,3:3/16,4:4/16,....",required=true)
    @NotNull(message="入库油量不能为空")
    private Integer inDepotOimass;
    @ApiModelProperty(value="入库是否损伤 1:是,2:否",required=true)
    @NotNull(message="入库是否损伤不能为空")
    private Integer inDepotDamage;
    @ApiModelProperty(value="行驶证是否正常交接: 1:是，2:否",required=true)
    @NotNull(message="行驶证是否正常交接不能为空")
    private Integer drivingLicenseJoin;
    @ApiModelProperty(value="车钥匙是否正常交接: 1:是，2:否",required=true)
    @NotNull(message="车钥匙是否正常交接不能为空")
    private Integer carKeyJoin;
}

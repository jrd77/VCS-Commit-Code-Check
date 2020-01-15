package com.atzuche.order.commons.entity.trusteeship;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 托管车信息表
 * @author 胡春林
 */
@Data
@ToString
public class OrderCarTrusteeshipRepVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键
	 */
	@AutoDocProperty(value="主键")
	private Integer id;
	/**
	 * 主订单号
	 */
    @AutoDocProperty(value="主订单号")
	private String orderNo;
	/**
	 * 车牌号
	 */
    @AutoDocProperty(value="车牌号")
	private String carNo;
	/**
	 * 车管家姓名
	 */
    @AutoDocProperty(value="车管家姓名")
	private String trusteeshipName;
	/**
	 * 车管家手机号
	 */
    @AutoDocProperty(value="车管家手机号")
	private String trusteeshipTelephone;
	/**
	 * 出库时间
	 */
    @AutoDocProperty(value="出库时间")
	private LocalDateTime outDepotTime;
	/**
	 * 入库时间
	 */
    @AutoDocProperty(value="入库时间")
	private LocalDateTime inDepotTime;
	/**
	 * 出库里程数
	 */
    @AutoDocProperty(value="出库里程数")
	private String outDepotMileage;
	/**
	 * 入库里程数
	 */
    @AutoDocProperty(value="入库里程数")
	private String inDepotMileage;
	/**
	 * 出库油量: 1:1/16,2:2/16,3:3/16,4:4/16,5:5/16,6:6/16,7:7/16,8:8/16,9:9/16,10:10/16,11:11/16,12:12/16,13:13/16,14:14/16,15:15/16,16:16/16
	 */
    @AutoDocProperty(value="出库油量1:1/16,2:2/16,3:3/16,4:4/16,5:5/16..")
	private Integer outDepotoimass;
	/**
	 * 入库油量: 1:1/16,2:2/16,3:3/16,4:4/16,5:5/16,6:6/16,7:7/16,8:8/16,9:9/16,10:10/16,11:11/16,12:12/16,13:13/16,14:14/16,15:15/16,16:16/16
	 */
    @AutoDocProperty(value="入库油量1:1/16,2:2/16,3:3/16,4:4/16,5:5/16..")
	private Integer inDepotOimass;
	/**
	 * 入库是否损伤:1:是,2:否
	 */
    @AutoDocProperty(value="入库是否损伤:1:是,2:否")
	private Integer inDepotDamage;
	/**
	 * 行驶证是否正常交接: 1:是，2:否
	 */
    @AutoDocProperty(value="行驶证是否正常交接: 1:是，2:否")
	private Integer drivingLicenseJoin;
	/**
	 * 车钥匙是否正常交接: 1:是，2:否
	 */
    @AutoDocProperty(value="车钥匙是否正常交接: 1:是，2:否")
	private Integer carKeyJoin;
	/**
	 * 创建人
	 */
    @AutoDocProperty(value="创建人")
	private String createOp;
	/**
	 * 修改人
	 */
    @AutoDocProperty(value="修改人")
	private String updateOp;
	/**
	 * 创建时间
	 */
    @AutoDocProperty(value="创建时间")
	private LocalDateTime createTime;
	/**
	 * 修改时间
	 */
    @AutoDocProperty(value="修改时间")
	private LocalDateTime updateTime;

    @AutoDocProperty(value="油耗刻度")
    private String oilScale;

	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}

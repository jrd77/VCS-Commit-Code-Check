package vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CarBO {
    /**日租金*/
    private int dayPrice;
    /**是否为自动挡*/
    private int gearBoxType;
    /**是否为沪牌*/
    private int isLocal;
    /**功能类型*/
    private int useType;
    /**维度*/
    private double lat;
    /**经度*/
    private double lon;
    /**城市编号*/
    private int city;
    /**车辆类型是否为托管等*/
    private int ownerType;
    /**车辆状态*/
    private int status;
    /**车辆品牌*/
    private String brandTxt;
    private Integer isServiceRange;
    private Integer isAcceptGetReturn;
    private String coverPic;
    private String plateNum;
    private String typeTxt;
    private Double cylinderCapacity;
    private String ccUnit;
}

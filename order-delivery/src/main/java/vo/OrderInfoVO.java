package vo;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 胡春林
 * 订单部分数据
 */
@Data
@ToString
public class OrderInfoVO {

    private Long orderNo;
    private Integer carNo;
    private Integer ownerNo;
    private String ownerName;
    private String ownerPhone;
    private Integer renterNo;
    private String renterName;
    private String source;
    private String renterOrderNo;

    public boolean isGreaterThanZero()
    {
        if(StringUtils.isBlank(renterOrderNo)) return false;
        return Integer.valueOf(renterOrderNo) > 0;
    }
}

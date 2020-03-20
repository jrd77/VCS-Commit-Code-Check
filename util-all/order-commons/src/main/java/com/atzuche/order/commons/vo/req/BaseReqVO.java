package com.atzuche.order.commons.vo.req;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author 胡春林
 */
@Data
@ToString
public class BaseReqVO implements Serializable{

    @NotNull(message = "頁碼不能为空")
    private Integer pageNum;
    @NotNull(message = "頁大小不能为空")
    private Integer pageSize;

    private String  orderBy;

    private String searchName;

    private String searchValue;
}

package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@ToString
public class ModificationOrderListResponseVO implements Serializable{
	@AutoDocProperty(value="修改订单列表")
	private List<ModificationOrderResponseVO> modificationOrderList;


}

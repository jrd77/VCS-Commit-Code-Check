package com.atzuche.order.flow.dto.resp;

import com.atzuche.order.flow.entity.OrderFlowEntity;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;



@Data
@ToString
public class OrderFlowListResponseDTO implements Serializable {

	@AutoDocProperty(value = "订单状态流转列表")
	List<OrderFlowDTO> orderFlowList;

}

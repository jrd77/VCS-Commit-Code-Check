package com.atzuche.order.photo.vo.resp;

import com.atzuche.order.photo.dto.OrderPhotoDTO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OrderPhotoTypeResponseVO {
    @AutoDocProperty(value = "照片列表(根据photoType来)")
    private List<OrderPhotoDTO> getPhotoList;
}

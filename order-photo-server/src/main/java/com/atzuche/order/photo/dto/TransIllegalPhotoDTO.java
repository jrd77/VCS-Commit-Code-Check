package com.atzuche.order.photo.dto;

import com.atzuche.order.photo.entity.PhotoPathDTO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by tao.sun on 2018/2/1.
 */
@Data
@ToString
public class TransIllegalPhotoDTO {
    private String wzcode;//违章唯一编码
    private String orderno;//订单编号
    private List<PhotoPathDTO> imagePath; //违章凭证图片
}


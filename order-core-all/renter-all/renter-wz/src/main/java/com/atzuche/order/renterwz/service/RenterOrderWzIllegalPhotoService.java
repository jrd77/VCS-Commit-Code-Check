package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzIllegalPhotoEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzIllegalPhotoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RenterOrderWzIllegalPhotoService
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class RenterOrderWzIllegalPhotoService {

    @Resource
    private RenterOrderWzIllegalPhotoMapper renterOrderWzIllegalPhotoMapper;

    Integer getMaxSerialNum(String orderNo, Integer userType, String carPlateNum) {
        return renterOrderWzIllegalPhotoMapper.getMaxSerialNum(orderNo,userType,carPlateNum);
    }

    public Integer countPhoto(String orderNo, String img, String carPlateNum) {
        return renterOrderWzIllegalPhotoMapper.countPhoto(orderNo,img,carPlateNum);
    }

    public void insertPhotoRenYunMq(RenterOrderWzIllegalPhotoEntity photo) {
        renterOrderWzIllegalPhotoMapper.saveRenterOrderWzIllegalPhoto(photo);
    }
}

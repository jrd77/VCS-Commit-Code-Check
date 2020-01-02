package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzIllegalPhotoEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzIllegalPhotoMapper;
import com.atzuche.order.renterwz.vo.PhotoPath;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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


    public Integer getMaxSerialNum(String orderNo, Integer userType, String carPlateNum) {
        return renterOrderWzIllegalPhotoMapper.getMaxSerialNum(orderNo,userType,carPlateNum);
    }

    public Integer countPhoto(String orderNo, String img, String carPlateNum) {
        return renterOrderWzIllegalPhotoMapper.countPhoto(orderNo,img,carPlateNum);
    }

    public void insertPhotoRenYunMq(RenterOrderWzIllegalPhotoEntity photo) {
        renterOrderWzIllegalPhotoMapper.saveRenterOrderWzIllegalPhoto(photo);
    }

    public int update(RenterOrderWzIllegalPhotoEntity photo) {
        return renterOrderWzIllegalPhotoMapper.update(photo);
    }

    public RenterOrderWzIllegalPhotoEntity getIllegalPhotoBy(String orderNo, int userType, int serialNumber, String carNum) {
        return renterOrderWzIllegalPhotoMapper.getIllegalPhotoBy(orderNo,userType,serialNumber,carNum);
    }

    public int countIllegalPhoto(String orderNo, int userType, String carNum) {
        Integer count = renterOrderWzIllegalPhotoMapper.countIllegalPhoto(orderNo, userType, carNum);
        return count == null ? 0 : count;
    }

    public Integer insert(RenterOrderWzIllegalPhotoEntity photo) {
        return renterOrderWzIllegalPhotoMapper.saveRenterOrderWzIllegalPhoto(photo);
    }

    public List<PhotoPath> queryIllegalPhotoByOrderNo(String orderNo, String carPlateNum) {
        return renterOrderWzIllegalPhotoMapper.queryIllegalPhotoByOrderNo(orderNo,carPlateNum);
    }
}

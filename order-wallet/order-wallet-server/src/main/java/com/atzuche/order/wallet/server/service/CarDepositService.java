package com.atzuche.order.wallet.server.service;

import com.atzuche.order.commons.exceptions.CarNotFoundException;
import com.atzuche.order.wallet.api.CarDepositVO;
import com.atzuche.order.wallet.server.entity.CarDepositEntity;
import com.atzuche.order.wallet.server.mapper.CarDepositMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/9 10:40 上午
 **/
@Service
public class CarDepositService {
    @Autowired
    private CarDepositMapper carDepositMapper;


    public CarDepositVO getCarDepositById(Integer id){
        CarDepositEntity carDepositEntity = carDepositMapper.getCarDepositById(id);
        if(carDepositEntity!=null){
            CarDepositVO carDepositVO = new CarDepositVO();
            BeanUtils.copyProperties(carDepositEntity,carDepositVO);
            return carDepositVO;
        }else{
            throw new CarNotFoundException("id="+id);
        }
    }


    public CarDepositVO getCarDepositByCarNo(String carNo){
        CarDepositEntity carDepositEntity = carDepositMapper.getCarDepositByCarNo(carNo);
        if(carDepositEntity!=null){
            CarDepositVO carDepositVO = new CarDepositVO();
            BeanUtils.copyProperties(carDepositEntity,carDepositVO);
            return carDepositVO;
        }else{
            throw new CarNotFoundException("regNo="+carNo);
        }
    }

    public void updateCarDeposit(String carNo,Integer updateBill){
        CarDepositEntity carDepositEntity = carDepositMapper.getCarDepositByCarNo(carNo);
        if(carDepositEntity==null){
            throw new CarNotFoundException("regNo="+carNo);
        }
        carDepositMapper.updateCarDepositBill(carNo,updateBill);
    }
}

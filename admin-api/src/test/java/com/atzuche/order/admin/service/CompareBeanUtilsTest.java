package com.atzuche.order.admin.service;

import com.atzuche.order.admin.AdminSpringBoot;
import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.util.CompareBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes= AdminSpringBoot.class)
@WebAppConfiguration
public class CompareBeanUtilsTest {
    @Autowired
    private AdminLogService adminLogService;

    @Test
    public  void test(String[] args) {
        OrderRemarkEntity oldData = new OrderRemarkEntity();
        oldData.setNumber("111");
        oldData.setRemarkContent("这是一个测试数据");


        OrderRemarkEntity newData = new OrderRemarkEntity();
        newData.setNumber("222");
        newData.setRemarkContent("这是一个真实数据");


        String desc = CompareBeanUtils.newInstance(oldData, newData).compare();
        adminLogService.insertLog(AdminOpTypeEnum.RENTER_PRICE_ADJUSTMENT,"000",
                "000456","000789",desc);

    }

    public static void main(String[] args) {
        OrderRemarkEntity oldData = new OrderRemarkEntity();
        oldData.setNumber("111");
        oldData.setRemarkContent("这是一个测试数据");


        OrderRemarkEntity newData = new OrderRemarkEntity();
        newData.setNumber("222");
        newData.setRemarkContent("这是一个真实数据");

        String desc = CompareBeanUtils.newInstance(oldData, newData).compare();
        System.out.println(desc);
    }
}

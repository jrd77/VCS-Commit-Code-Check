package com.atzuche.order.coreapi.service;

import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.rentermem.service.RenterMemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= TemplateApplication.class)
@WebAppConfiguration
public class RenterMemberServiceTest {
    @Autowired
    private RenterMemberService renterMemberService;

    @Test
    public void isEnterpriseUserOrder(){

        boolean enterpriseUserOrder = renterMemberService.isEnterpriseUserOrder("9293927110029910010");
        log.info("是否企业用户："+enterpriseUserOrder);

        boolean enterpriseUserOrder1 = renterMemberService.isEnterpriseUserOrder("7175537140029910010");
        log.info("是否企业用户："+enterpriseUserOrder1);
    }
}

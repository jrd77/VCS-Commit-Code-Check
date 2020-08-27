/**
 * 
 */
package com.atzuche.order.coreapi.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.cashieraccount.service.AutoSecondOpenRemoteServiceTest;
import com.atzuche.order.coreapi.TemplateApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class SecondaryEmailTaskTest {
	@Autowired
	SecondaryEmailTask secondaryEmailTask;
	
	@Test
	public void testEmail() {
		try {
			secondaryEmailTask.execute("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

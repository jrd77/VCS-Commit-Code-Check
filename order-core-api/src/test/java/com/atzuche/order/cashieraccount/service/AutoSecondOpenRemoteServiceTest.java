package com.atzuche.order.cashieraccount.service;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.atzuche.order.cashieraccount.service.remote.AutoSecondOpenRemoteService;
import com.atzuche.order.cashieraccount.vo.req.OwnerOpenReqVO;
import com.atzuche.order.cashieraccount.vo.res.OpenInfoStatusVO;
import com.atzuche.order.coreapi.TemplateApplication;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;

import java.lang.reflect.Type;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class AutoSecondOpenRemoteServiceTest {
	@Autowired
	AutoSecondOpenRemoteService  autoSecondOpenRemoteService;
	
    @Autowired
    private RestTemplate restTemplate;
    
	@Test
	public void testSendWithdrawalRequest() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testCheckOwnerIsOpenVir() {
		String url = "http://47.99.67.156:1444/";
		String memNo = "480540813";
		OwnerOpenReqVO reqVo = new OwnerOpenReqVO();
        reqVo.setMemNo(memNo);
        String json = restTemplate.postForObject(url + "second/open/person/owner/getOpenInfo", reqVo, String.class);
        log.info("string = " + json);
        
        //java.lang.ClassCastException: com.google.gson.internal.LinkedTreeMap cannot be cast to xxx
//        ResponseData<OpenInfoStatusVO> responseEntity = GsonUtils.convertObj(json, ResponseData.class);
        Type type = new TypeToken<ResponseData<OpenInfoStatusVO>>() {}.getType();
        
        ResponseData<OpenInfoStatusVO> responseEntity = new Gson().fromJson(json, type);
        
   
        if (responseEntity == null || responseEntity.getData() == null) {
        	log.info("postForEntity null,params memNo=[{}]",memNo);
        	log.info("11111111");
        }else {
        	OpenInfoStatusVO statusVo = (OpenInfoStatusVO) responseEntity.getData();
        	log.info("postForEntity ok,result=[{}],params memNo=[{}]",GsonUtils.toJson(statusVo),memNo);
        	//开户状态 0：未完成 1：完成
        	if(statusVo.getSecondOpenOwner() != null && statusVo.getSecondOpenOwner().getOpenStatus().intValue() == 1) {
        		log.info("2222222");
        	}else {
        		log.info("11111111");
        	}
        }
	}
	
	@Test
	public void testCheckOwnerIsOpenVirNew() {
		String url = "http://47.99.67.156:1444/";
		String memNo = "480540813";
		OwnerOpenReqVO reqVo = new OwnerOpenReqVO();
        reqVo.setMemNo(memNo);
//        ResponseEntity<ResponseData> responseEntity = restTemplate.postForEntity(url + "second/open/person/owner/getOpenInfo", reqVo, ResponseData.class);
        ResponseEntity<String> response = restTemplate.postForEntity(url + "second/open/person/owner/getOpenInfo", reqVo, String.class);
        log.info("string = " + GsonUtils.toJson(response));
        
        //java.lang.ClassCastException: com.google.gson.internal.LinkedTreeMap cannot be cast to xxx
//        ResponseData<OpenInfoStatusVO> responseEntity = GsonUtils.convertObj(json, ResponseData.class);
        
        
        Type type = new TypeToken<ResponseData<OpenInfoStatusVO>>() {}.getType();
        ResponseData<OpenInfoStatusVO> responseEntity = new Gson().fromJson(response.getBody(), type);
        
//        ResponseData<OpenInfoStatusVO> responseEntity = new Gson().fromJson(response.getBody(), ResponseData.class);
        
   
        if (responseEntity == null || responseEntity.getData()== null) {
        	log.info("postForEntity null,params memNo=[{}]",memNo);
        	log.info("11111111");
        }else {
        	OpenInfoStatusVO statusVo = (OpenInfoStatusVO) responseEntity.getData();
        	log.info("postForEntity ok,result=[{}],params memNo=[{}]",GsonUtils.toJson(statusVo),memNo);
        	//开户状态 0：未完成 1：完成
        	if(statusVo.getSecondOpenOwner() != null && statusVo.getSecondOpenOwner().getOpenStatus().intValue() == 1) {
        		log.info("2222222");
        	}else {
        		log.info("11111111");
        	}
        }
	}
	

}

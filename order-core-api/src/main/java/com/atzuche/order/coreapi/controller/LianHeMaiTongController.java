package com.atzuche.order.coreapi.controller;

import com.atzuche.order.coreapi.service.LianHeMaiTongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/lianHeMaiTong")
public class LianHeMaiTongController {
    @Autowired
    private LianHeMaiTongService lianHeMaiTongService;




}

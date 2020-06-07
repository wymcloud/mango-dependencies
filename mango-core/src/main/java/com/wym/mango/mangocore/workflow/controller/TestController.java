package com.wym.mango.mangocore.workflow.controller;

import com.wym.mango.mangocore.workflow.model.TestRequest;
import com.wym.mango.mangocore.workflow.service.MangoTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TestController {

    @Resource
    private MangoTestService mangoTestService;

    @GetMapping("/test")
    public void test(){
        TestRequest testRequest = new TestRequest();
        System.out.println(mangoTestService.testHandle(testRequest));

    }
}

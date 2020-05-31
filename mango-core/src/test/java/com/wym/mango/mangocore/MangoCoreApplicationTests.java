package com.wym.mango.mangocore;

import com.wym.mango.mangocore.workflow.model.TestRequest;
import com.wym.mango.mangocore.workflow.service.MangoTestService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = MangoCoreApplication.class)
class MangoCoreApplicationTests {

    @Resource
    private MangoTestService mangoTestService;

    @Test
    void contextLoads() {

    }

    @Test
    void testHandle(){
        TestRequest request = new TestRequest();
        System.out.println(mangoTestService.testHandle(request));
    }

}

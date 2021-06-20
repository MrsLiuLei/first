package com.tanhua.manage.test;

import com.tanhua.commons.templates.HuaWeiUGCTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test1 {
    @Autowired
    private HuaWeiUGCTemplate template;

    @Test
    public void testToken() {
        System.out.println(template.getToken());
    }

    @Test
    public void test1(){
        boolean check = template.textContentCheck("草你妈");
        System.out.println(check);
    }
}

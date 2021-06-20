package com.tanhua.server.test;

import com.tanhua.commons.templates.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OssTest {
    @Autowired
    private OssTemplate ossTemplate;
    @Test
    public void test0ss() throws FileNotFoundException {
        FileInputStream in = new FileInputStream("D:\\图片\\a.jpg");
        ossTemplate.upload("time.jpg",in);
    }
}

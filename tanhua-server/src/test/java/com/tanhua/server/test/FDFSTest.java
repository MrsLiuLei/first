package com.tanhua.server.test;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FDFSTest {
    @Autowired
    private FastFileStorageClient client;
    @Autowired
    private FdfsWebServer fdfsWebServer;
    @Test
    public void testUpLoadFile() throws IOException {
        File file = new File("D:\\图片\\d.jpg");
        StorePath path = client.uploadFile(FileUtils.openInputStream(file), file.length(), "jpg", null);
        System.out.println(path.getFullPath());
        System.out.println(path.getPath());
        String url = fdfsWebServer.getWebServerUrl() + path.getFullPath();
        System.out.println(url);
    }
}

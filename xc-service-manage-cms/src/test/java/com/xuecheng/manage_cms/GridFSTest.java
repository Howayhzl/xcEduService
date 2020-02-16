package com.xuecheng.manage_cms;


import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTest {

    @Autowired
    GridFsTemplate gridFsTemplate;

    //存文件
    @Test
    public void testGridFsStore() throws FileNotFoundException {
        //要存储的文件
        File file = new File("G:\\XCZXProjects\\xcEduService\\test‐freemarker\\src\\main\\resources\\templates\\index_banner.ftl");
        // 定义输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        //向GridFs存储文件
        ObjectId objectId = gridFsTemplate.store(fileInputStream, "index_banner");
        //获取存储文件id
        String fileId = objectId.toString();
        System.out.println(fileId);

    }


}

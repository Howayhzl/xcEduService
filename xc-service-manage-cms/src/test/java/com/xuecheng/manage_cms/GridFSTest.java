package com.xuecheng.manage_cms;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTest {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;



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

    //取文件
    @Test
    public void queryFile() throws IOException {
        String fileId="5e48ecf8f4b78d08442cf57a";
        //根据文件id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开一个下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        // 创建GridFSResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        //从流中获取数据
        String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(content);
    }

}

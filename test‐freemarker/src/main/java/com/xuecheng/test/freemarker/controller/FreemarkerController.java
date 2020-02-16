package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/freemarker")
public class FreemarkerController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/banner")
    public String index_banner(Map<String,Object> map){
        //使用RestTemplate请求轮播图的数据模型
        String dataUrl = "http://localhost:31001//cms/config/getModel/5a791725dd573c3574ee333f";
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = entity.getBody();
        //设置数据模型
        map.putAll(body);
        return "index_banner";
    }

    @RequestMapping("/test1")
    public String freemarker(Map<String,Object> map){
        //向数据模型放数据
        map.put("name","黑马程序员");

        //设置stu1学生信息
        Student stu1=new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        //设置stu2学生信息
        Student stu2=new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());

        // 朋友列表
        List<Student> friends=new ArrayList<>();
       friends.add(stu1);

       // 给stu2学生设置最好的朋友
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);

        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);

        //向数据模型放数据
        map.put("stus",stus);

        //准备map数据
        HashMap<String,Student>  stuMap  = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);

        //向数据模型放数据
        map.put("stu1",stu1);
        //向数据模型放数据
        map.put("stuMap",stuMap);
        //返回模板文件名称

        map.put("point", 102920122);

        return "test1";
    }
}

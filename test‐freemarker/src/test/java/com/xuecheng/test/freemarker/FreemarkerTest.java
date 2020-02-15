package com.xuecheng.test.freemarker;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.*;
import java.util.*;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-06-13 10:07
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    //基于模板生成静态化文件
    @Test
    public void testGenerateHtml() throws IOException, TemplateException {
        //创建配置类
        Configuration configuration=new Configuration(Configuration.getVersion());
        //得到classpath的路径
        /*String classpath = FreemarkerTest.class.getResource("/").getPath();
        //设置模板路径
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));*/
        configuration.setDirectoryForTemplateLoading(new File("G:\\XCZXProjects\\xcEduService\\test‐freemarker\\src\\test\\resources\\templates"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //获取模板中的内容
        Template template = configuration.getTemplate("test1.ftl");
        //定义数据模型
        Map map = getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //静态化内容
        System.out.println(content);
        InputStream inputStream = IOUtils.toInputStream(content);
        //输出文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/test1.html"));
        IOUtils.copy(inputStream,fileOutputStream);
        inputStream.close();
        fileOutputStream.close();
    }



    //数据模型
    private Map getMap(){
        Map map = new HashMap();

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

        return map;
    }
}

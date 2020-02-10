package com.xuecheng.manage_cms.dao;


import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    //分页查询
    @Test
    public void testFindPage(){
        int page=0;
        int size=2;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> cmsPages = cmsPageRepository.findAll(pageable);
        System.out.println(cmsPages);
    }

    //添加
    @Test
    public void testInsert(){
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageAliase("测试页面尝试");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> pageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        pageParams.add(cmsPageParam);
        cmsPage.setPageParams(pageParams);
       cmsPageRepository.save(cmsPage);
    }

    //删除
    @Test
    public void testDelete(){
        cmsPageRepository.deleteById("5e3d3143f4b78d47b8a1c3b111");
    }

    //修改
    @Test
    public void testUpdate(){
        Optional<CmsPage> optional = cmsPageRepository.findById("5e3d3137f4b78d370ce0b26d");
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            cmsPage.setTemplateId("q01");
            cmsPageRepository.save(cmsPage);
        }
    }

    ////自定义条件查询测试
    @Test
    public void testExample(){
        int page=0;
        int size=10;
        Pageable pageable = PageRequest.of(page,size);
        CmsPage cmsPage = new CmsPage();
        // 要查询5a751fab6abb5044e0d19ea111站点的页面
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        cmsPage.setTemplateId("5a925be7b00ffc4b3c1578b5");
        cmsPage.setPageAliase("页面");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }
}

package com.xuecheng.manage_cms.dao;


import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        cmsPageRepository.deleteById("5e3d3143f4b78d47b8a1c3b1");
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

}

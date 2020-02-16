package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    PageService pageService;

    // 页面预览
    @RequestMapping(value="/cms/preview/{pageId}",method = RequestMethod.GET)
   public void preview(@PathVariable("pageId")String pageId){
        //执行页面静态化
       String html = pageService.getPageHTml(pageId);
        ServletOutputStream outputStream = null;
        if (StringUtils.isNotEmpty(html)){
            try {
                // 通过response对象将内容输出
                outputStream = response.getOutputStream();
                outputStream.write(html.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
   }
}

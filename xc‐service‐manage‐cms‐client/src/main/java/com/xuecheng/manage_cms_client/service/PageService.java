package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class PageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    GridFsTemplate gridFsTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PageService.class);
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
   GridFSBucket gridFSBucket;

    @Autowired
    RestTemplate restTemplate;

    //将页面html保存到页面物理路径
    public void savePageToServerPath(String pageId){
        //根据pageId获取CmsPage
        CmsPage cmsPage = this.findCmsPageByPageId(pageId);
        //获取html的fileId，从CmsPage中获取
        String htmlFileId = cmsPage.getHtmlFileId();
        //从gridFS中查询html,根据htmlFileId从GridDF中查询文件内容
        InputStream inputStream = this.getFileById(htmlFileId);
        if (inputStream==null){
            logger.error("getFileById InputStream is null,htmlFileId:{}",htmlFileId);
            return;
        }
        //将页面html保存到页面物理路径

        // 获取站点信息
        CmsSite cmsSite = this.findCmsSiteByPageId(cmsPage.getSiteId());
        //获取站点物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //获取页面的物理路径
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();

        //将html文件保存到服务器的物理路径上
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //根据pageId获取CmsPage
    private CmsPage  findCmsPageByPageId(String siteId){
        Optional<CmsPage> optional = cmsPageRepository.findById(siteId);
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }

    //获取cmsSite信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (optional.isPresent()){
            CmsSite cmsSite = optional.get();
            return cmsSite;
        }
        return null;
    }

    // 通过文件Id获取文件的输入流
    public InputStream getFileById(String htmlFileId){
        //文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义GridFSResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取cmsPage信息
    public CmsPage findCmsPageById(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()){
            CmsSite cmsSite = optional.get();
            return cmsSite;
        }
        return null;
    }

    //根据htmlFileId从GridDF中查询文件内容
    private InputStream getFileById(String htmlFileId){
        //根据htmlFileId获取文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义GridFSResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            InputStream inputStream = gridFsResource.getInputStream();
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

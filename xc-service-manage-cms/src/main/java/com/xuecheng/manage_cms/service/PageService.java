package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 页面查询方法
     * @param page 页码，从1开始记数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult<CourseBase> findList(int page, int size, QueryPageRequest queryPageRequest) {

        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }

        // 自定义条件查询
        // 自定义条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        // 条件值对象
        CmsPage cmsPage = new CmsPage();
        // 设置条件值(站点id)
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }

        // 设置条件值(模板id)
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }

        // 设置条件值(别名)
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        // 设置条件值(页面名称)
        if (StringUtils.isNotEmpty(queryPageRequest.getPageName())) {
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //分页参数
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        QueryResult<CmsPage> queryResult = new QueryResult();
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult<CourseBase> queryResponseResult = new QueryResponseResult<CourseBase>(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    //新增页面
    /*public CmsPageResult addCms(CmsPage cmsPage){
        // 校验页面是否存在，根据页面名称、站点Id、页面webpath的唯一性
         // 根据页面名称、站点Id、页面webpath查询cmspage，如果查询到，说明页面已经存在.如果查询不到，在继续添加
        CmsPage cmspageCheck = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmspageCheck == null){
            // 调用dao新增页面
            cmsPage.setPageId(null);
            CmsPage save = cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,save);
        }
        // 添加失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }
*/
    //新增页面
    public CmsPageResult addCms(CmsPage cmsPage) {
        if (cmsPage == null) {
            //抛出异常，非法参数异常,指定异常信息的内容

        }

        // 校验页面是否存在，根据页面名称、站点Id、页面webpath的唯一性
        // 根据页面名称、站点Id、页面webpath查询cmspage，如果查询到，说明页面已经存在.如果查询不到，在继续添加
        CmsPage cmspageCheck = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmspageCheck != null) {
            // 页面已经存在，抛出的异常就是页面已经存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        // 调用dao新增页面
        cmsPage.setPageId(null);
        CmsPage save = cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, save);
    }

    //根据id查询页面
    public CmsPage findById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }


    //更新页面信息
    public CmsPageResult update(String id, CmsPage cmsPage) {
        CmsPage one = findById(id);
        if (one != null) {
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //更新成功
                return new CmsPageResult(CommonCode.SUCCESS, save);
            }
        }
        //返回失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    //删除页面
    public ResponseResult deleteCmspage(String id) {
        CmsPage cmsPage = findById(id);
        if (cmsPage != null) {
            cmsPageRepository.delete(cmsPage);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //页面静态化方法
    public String getPageHTml(String pageId){
        //1.静态化程序远程请求DataUrl获取数据模型
        Map map = getModelByPageId(pageId);
        if (CollectionUtils.isEmpty(map)){
            //获取页面数据模型为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        // 2.静态化程序获取页面的模板信息
        String template = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)){
            //获取模板数据为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //3.执行页面静态化
        String html = generateHtml(template, map);
        if (StringUtils.isEmpty(html)){
            //生成的静态html为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    //3.执行页面静态化
    public String generateHtml(String template,Map map){
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //使用一个模板加载器变为模板
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",template);
        //在配置中设置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template temp = configuration.getTemplate("template", "utf-8");
            //执行静态化
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(temp, map);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTemplateByPageId(String pageId){
        //获取页面信息
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage==null){
            //页面不存存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取模板id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)){
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (!optional.isPresent()){
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        CmsTemplate cmsTemplate = optional.get();
        //获取文件模板id
        String templateFileId = cmsTemplate.getTemplateFileId();
        //根据文件模板id取出文件模板中的内容
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //从下载流中过去数据
        try {
            String content = IOUtils.toString(gridFSDownloadStream, "utf-8");
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //1.静态化程序远程请求DataUrl获取数据模型方法
    public Map getModelByPageId(String pageId){
        //获取页面信息
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage==null){
            //页面不存存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取页面DataUrl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)){
            //页面的dataUrl为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //使用restTemplate远程调取数据
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = entity.getBody();
        return body;
    }

    //页面发布
    public ResponseResult post(String pageId){
        // 执行页面静态化
        String pageHTml = getPageHTml(pageId);
        //将静态化页面存储到GridFS上
        CmsPage cmsPage = saveHtml(pageId, pageHTml);
        //向MQ发送消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

       //向MQ发送消息
      private void sendPostPage(String pageId){
        //获取站点Id
          CmsPage cmsPage = findById(pageId);
          if (cmsPage==null){
              ExceptionCast.cast(CommonCode.INVALID_PARAM);
          }
          String siteId = cmsPage.getSiteId();

          //创建消息对象
          Map<String,String> msg = new HashMap<>();
          msg.put("pageId",pageId);
          //将消息转换为JsonString
          String msgString = JSON.toJSONString(msg);
          //发送给MQ
          rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msgString);

      }



        //保存html到GridFS
       private CmsPage saveHtml(String pageId,String htmlContent){

           CmsPage cmsPage = this.findById(pageId);
           if (cmsPage==null){
               ExceptionCast.cast(CommonCode.INVALID_PARAM);
           }
           //将htmlContent转化为输入流
           InputStream inputStream = null;
           ObjectId objectId = null;
           try {
               inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
               //将Html保存到GridFS上
               objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
           } catch (IOException e) {
               e.printStackTrace();
           }

        //将html文件id更新到CmsPage中
           cmsPage.setHtmlFileId(objectId.toHexString());
           CmsPage save = cmsPageRepository.save(cmsPage);
           return save;
       }
}

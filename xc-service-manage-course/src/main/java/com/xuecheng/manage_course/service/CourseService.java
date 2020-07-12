package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachPlanNodeMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {

    @Autowired
    TeachPlanNodeMapper teachPlanNodeMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId){
       return teachPlanNodeMapper.selectList(courseId);
    }

    //添加课程计划

    public ResponseResult addTeachplanNode(Teachplan teachplan) {
        if (teachplan==null
                || StringUtils.isEmpty(teachplan.getCourseid())
                || StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程计划
        String courseid = teachplan.getCourseid();
        //parentId
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)){
            //取出该课程的根节点
            parentid= getTeachplanRoot(courseid);
        }
        //取出父结点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);
        if(!teachplanOptional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //父结点
        Teachplan teachplanFather= teachplanOptional.get();
        //父结点级别
        String parentGrade = teachplanFather.getGrade();
        // 设置父节点
        teachplan.setGrade(parentGrade);
        teachplan.setStatus("0");


        if (parentGrade.equals("1")){
            teachplan.setGrade("2");// 级别，根据父节点的级别来设置
        }else if (parentGrade.equals("2")){
            teachplan.setGrade("3");
        }
        teachplan.setCourseid(teachplanFather.getCourseid());
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据课程Id查询根节点，如果查询不到自动添加根节点
    private String getTeachplanRoot(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()){
           return null;
        }
        CourseBase courseBase = optional.get();
        //查询课程根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (CollectionUtils.isEmpty(teachplanList)){
            //查询课程id，如果查询不到则自动添加
           Teachplan teachplan = new Teachplan();
           teachplan.setParentid("0");
           teachplan.setGrade("1");
           teachplan.setPname(courseBase.getName());
           teachplan.setCourseid(courseId);
          teachplan.setStatus("0");
          teachplanRepository.save(teachplan);
          return teachplan.getId();
        }
        return teachplanList.get(0).getId();
    }
}

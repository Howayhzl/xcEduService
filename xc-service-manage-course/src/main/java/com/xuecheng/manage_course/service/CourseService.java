package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.manage_course.dao.TeachPlanNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    TeachPlanNodeMapper teachPlanNodeMapper;

    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId){
       return teachPlanNodeMapper.selectList(courseId);
    }
}

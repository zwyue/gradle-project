package com.zhu.gradleproject.service.impl;

import com.zhu.gradleproject.entity.ProjectInfo;
import com.zhu.gradleproject.mapper.ProjectInfoDao;
import com.zhu.gradleproject.service.ProjectInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zwy
 * @since 2020-12-04
 */
@Service
public class ProjectInfoServiceImpl extends ServiceImpl<ProjectInfoDao, ProjectInfo> implements ProjectInfoService {

}

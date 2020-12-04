package com.zhu.gradleproject.service.impl;

import com.zhu.gradleproject.entity.CompanyPerson;
import com.zhu.gradleproject.mapper.CompanyPersonDao;
import com.zhu.gradleproject.service.CompanyPersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zwy
 * @since 2020-12-03
 */
@Service
public class CompanyPersonServiceImpl extends ServiceImpl<CompanyPersonDao, CompanyPerson> implements CompanyPersonService {

}

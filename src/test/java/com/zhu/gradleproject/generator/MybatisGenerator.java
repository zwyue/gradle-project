package com.zhu.gradleproject.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.HashMap;
import java.util.Map;

public class MybatisGenerator {


  public static void main(String[] args) {

    final String outputDir = "E:\\project\\own\\gradle-project\\src\\main\\java";

    AutoGenerator mpg = new AutoGenerator();

    // 全局配置
    GlobalConfig gc = new GlobalConfig();

    gc.setOutputDir(outputDir);
    gc.setFileOverride(true);
    gc.setActiveRecord(false);
    gc.setEnableCache(false);
    gc.setBaseResultMap(true);
    gc.setBaseColumnList(true);
    gc.setSwagger2(true) ;
    gc.setAuthor("zwy");
    gc.setMapperName("%sDao");
    gc.setXmlName("%sDao");
    gc.setServiceName("%sService");

    mpg.setGlobalConfig(gc);

    // 数据源配置
    DataSourceConfig dsc = new DataSourceConfig();
    dsc.setDbType(DbType.MYSQL);
    dsc.setDriverName("com.mysql.cj.jdbc.Driver");
    dsc.setUsername("root");
    dsc.setPassword("123456");
    dsc.setUrl("jdbc:mysql://localhost:3306/color?characterEncoding=utf8&serverTimezone=GMT");
    mpg.setDataSource(dsc);

    // 策略配置
    StrategyConfig strategy = new StrategyConfig();

    strategy.setCapitalMode(true);
    strategy.setNaming(NamingStrategy.underline_to_camel);
    strategy.setEntityLombokModel(true);
    strategy.setInclude("project_info","tender_info","builder_licence");
    strategy.setEntitySerialVersionUID(true) ;
    strategy.setEntityTableFieldAnnotationEnable(true);
    strategy.setRestControllerStyle(true);

    mpg.setStrategy(strategy);

    //包相关的配置项
    PackageConfig pc = new PackageConfig();
    pc.setParent("com.zhu");
    pc.setModuleName("gradleproject");
//    pc.setMapper("dao");
//    pc.setController("controller");
//    pc.setXml("mappers");

    Map<String,String> pathInfo = new HashMap<>();
    pathInfo.put(ConstVal.CONTROLLER_PATH   , "E:\\project\\own\\gradle-project\\src\\main\\java\\com\\zhu\\gradleproject\\controller");
    pathInfo.put(ConstVal.SERVICE_PATH      , "E:\\project\\own\\gradle-project\\src\\main\\java\\com\\zhu\\gradleproject\\service");
    pathInfo.put(ConstVal.SERVICE_IMPL_PATH , "E:\\project\\own\\gradle-project\\src\\main\\java\\com\\zhu\\gradleproject\\service\\impl");
    pathInfo.put(ConstVal.ENTITY_PATH       , "E:\\project\\own\\gradle-project\\src\\main\\java\\com\\zhu\\gradleproject\\entity");
    pathInfo.put(ConstVal.MAPPER_PATH       , "E:\\project\\own\\gradle-project\\src\\main\\java\\com\\zhu\\gradleproject\\mapper");
    pathInfo.put(ConstVal.XML_PATH          , "E:\\project\\own\\gradle-project\\src\\main\\resources\\mapper");
    pc.setPathInfo(pathInfo) ;

    mpg.setPackageInfo(pc);

    mpg.execute();
  }
}

package com.jeeplus.modules.base.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.modules.meiguotong.entity.comlanguage.ComLanguage;
import com.jeeplus.modules.meiguotong.entity.comnavigation.ComNavigation;
import com.jeeplus.modules.meiguotong.entity.module.ModuleContent;
import com.jeeplus.modules.meiguotong.entity.module.ModuleHtml;
import com.jeeplus.modules.meiguotong.mapper.comlanguage.ComLanguageMapper;
import com.jeeplus.modules.meiguotong.mapper.module.ModuleContentMapper;
import com.jeeplus.modules.meiguotong.mapper.module.ModuleHtmlMapper;
import com.jeeplus.modules.meiguotong.service.comnavigation.ComNavigationService;
import com.jeeplus.modules.sys.service.SystemService;



@Component
@Lazy(value = false)
public class QuartzJob{
	@Autowired
	private ComLanguageMapper comLanguageMapper;
	@Autowired
	private ComNavigationService comNavigationService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private ModuleHtmlMapper moduleHtmlMapper;
	@Autowired
	private ModuleContentMapper moduleContentMapper;
	
	private static final Logger log =  LoggerFactory.getLogger(QuartzJob.class);
	
	/**
	 * 首页缓存数据更新
	 */
	@Scheduled(cron = "0 0/20 * * * ?")
    public void updateIndex() {
        try {
        	//获取所有语言
            List<ComLanguage> languageList = comLanguageMapper.findLanguageList(new ComLanguage());
            if(languageList!=null&&languageList.size()>0){
            	for(ComLanguage a:languageList){
            		 //获取首页导航
    	              ComNavigation comNavigation = new ComNavigation();
    	              comNavigation.setLanguageId(Integer.parseInt(a.getId()));
    	              List<ComNavigation> list = comNavigationService.getComNavigation(comNavigation);
    	              CacheUtils.put("appHome", "navList_"+a.getId(), list);
    	              //首页热门目的地
    	              ComNavigation comNavigation1 = new ComNavigation();
    	              comNavigation1.setLanguageId(Integer.parseInt(a.getId()));
    	              List<ComNavigation> list2 = comNavigationService.getHotCity(comNavigation);
    	              CacheUtils.put("appHome", "hotCityList_"+a.getId(), list2);
    	              //模块数据
    	              //获取所有网站页面
    	              //判断语言有没有数据
    	              Integer count = moduleContentMapper.getCount(a.getId());
    	              if(count>0){
    	            	  List<ModuleHtml> list1 = moduleHtmlMapper.findList(new ModuleHtml());
        	              if(list1!=null&&list1.size()>0){
        	            	  for(ModuleHtml b:list1){
        	            		 if("内容模块".equals(b.gethtmlSeal())){
    		            			  List<ModuleContent> moduleContentList = systemService.getHomeContent( b.getId(), a.getId());
    			            		  CacheUtils.put("appHome", "contentList_"+b.getHtmlName()+"_"+b.gethtmlSeal()+"_"+a.getId(), moduleContentList);
    		            		  }else if("广告模块".equals(b.gethtmlSeal())){
    		            			  List<ModuleContent> moduleContentList = systemService.getAdvertContent( b.getId(), a.getId());
    			            		  CacheUtils.put("appHome", "advertList_"+b.getHtmlName()+"_"+b.gethtmlSeal()+"_"+a.getId(), moduleContentList);
    		            		  }
        	            	  }
        	              }
    	              }
    	              
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}

/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web.member;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.sys.entity.member.Member;
import com.jeeplus.modules.sys.service.member.MemberService;

/**
 * 会员列表Controller
 * @author psz
 * @version 2018-08-07
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/member/member")
public class MemberController extends BaseController {

	@Autowired
	private MemberService memberService;
	
	@ModelAttribute
	public Member get(@RequestParam(required=false) String id) {
		Member entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = memberService.get(id);
		}
		if (entity == null){
			entity = new Member();
		}
		return entity;
	}
	
	/**
	 * 会员列表列表页面
	 */
	@RequiresPermissions("sys:member:member:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/sys/member/memberList";
	}
	
		/**
	 * 会员列表列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:member:member:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Member member, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Member> page = memberService.findPage(new Page<Member>(request, response), member); 
		return getBootstrapData(page);
	}

	/**
	 * 查看，增加，编辑会员列表表单页面
	 */
	@RequiresPermissions(value={"sys:member:member:view","sys:member:member:add","sys:member:member:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Member member, Model model) {
		model.addAttribute("member", member);
		if(StringUtils.isBlank(member.getId())){//如果ID是空为添加
			model.addAttribute("isAdd", true);
		}
		return "modules/sys/member/memberForm";
	}

	/**
	 * 保存会员列表
	 */
	@RequiresPermissions(value={"sys:member:member:add","sys:member:member:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(Member member, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, member)){
			return form(member, model);
		}
		//新增或编辑表单保存
		memberService.save(member);//保存
		addMessage(redirectAttributes, "保存会员列表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/member/member/?repage";
	}
	
	/**
	 * 删除会员列表
	 */
	@ResponseBody
	@RequiresPermissions("sys:member:member:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Member member, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		memberService.delete(member);
		j.setMsg("删除会员列表成功");
		return j;
	}
	
	/**
	 * 批量删除会员列表
	 */
	@ResponseBody
	@RequiresPermissions("sys:member:member:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			memberService.delete(memberService.get(id));
		}
		j.setMsg("删除会员列表成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:member:member:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Member member, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "会员列表"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Member> page = memberService.findPage(new Page<Member>(request, response, -1), member);
    		new ExportExcel("会员列表", Member.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出会员列表记录失败！失败信息："+e.getMessage());
		}
			return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:member:member:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Member> list = ei.getDataList(Member.class);
			for (Member member : list){
				try{
					memberService.save(member);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条会员列表记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条会员列表记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入会员列表失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/member/member/?repage";
    }
	
	/**
	 * 下载导入会员列表数据模板
	 */
	@RequiresPermissions("sys:member:member:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "会员列表数据导入模板.xlsx";
    		List<Member> list = Lists.newArrayList(); 
    		new ExportExcel("会员列表数据", Member.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/member/member/?repage";
    }

}
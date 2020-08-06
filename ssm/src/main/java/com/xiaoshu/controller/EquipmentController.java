package com.xiaoshu.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Equipment;
import com.xiaoshu.entity.Modle;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.EquipmentService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("equipment")
public class EquipmentController extends LogController{
	static Logger logger = Logger.getLogger(EquipmentController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	@Autowired
	private EquipmentService es;
	
	@RequestMapping("equipmentIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Modle> mList = es.findModle();
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		request.setAttribute("mList", mList);
		return "equipment";
	}
	
	
	@RequestMapping(value="eList",method=RequestMethod.POST)
	public void eList(Equipment equip,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			System.out.println(equip);
			PageInfo<Equipment> eList= es.findEquipPage(equip,pageNum,pageSize);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",eList.getTotal() );
			jsonObj.put("rows", eList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveEquipment")
	public void reserveUser(HttpServletRequest request,Equipment equip,HttpServletResponse response){
		Integer eId = equip.getEid();
		JSONObject result=new JSONObject();
		try {
			if (eId != null) {   // userId不为空 说明是修改
				Equipment equipment = es.existfindByName(equip.getEname());
				if(equipment == null){
					String fomatTime = TimeUtil.formatTime(new Date(), "yyyy-MM-dd");
					Date parseTime = TimeUtil.ParseTime(fomatTime, "yyyy-MM-dd");
					equip.setCreatetime(parseTime);
					es.updateEquipment(equip);
					result.put("success", true);
				}
				else if(equipment != null && equipment.getEid().compareTo(eId)==0){
					String fomatTime = TimeUtil.formatTime(new Date(), "yyyy-MM-dd");
					Date parseTime = TimeUtil.ParseTime(fomatTime, "yyyy-MM-dd");
					equip.setCreatetime(parseTime);
					es.updateEquipment(equip);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
			}else {   // 添加
				if(es.existfindByName(equip.getEname())==null){  // 没有重复可以添加
					String fomatTime = TimeUtil.formatTime(new Date(), "yyyy-MM-dd");
					Date parseTime = TimeUtil.ParseTime(fomatTime, "yyyy-MM-dd");
					equip.setCreatetime(parseTime);
					es.addEquip(equip);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteEquipment")
	public void delEquipment(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				es.deleteEquipment(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser"); 
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}

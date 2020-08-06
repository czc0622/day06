package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.EquipmentMapper;
import com.xiaoshu.dao.ModleMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Equipment;
import com.xiaoshu.entity.Modle;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;

@Service
public class EquipmentService {

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	private EquipmentMapper em;
	
	@Autowired
	private ModleMapper mm;
	
	// 查询所有
	public List<User> findUser(User t) throws Exception {
		return userMapper.select(t);
	};

	// 数量
	public int countUser(User t) throws Exception {
		return userMapper.selectCount(t);
	};

	// 通过ID查询
	public User findOneUser(Integer id) throws Exception {
		return userMapper.selectByPrimaryKey(id);
	};

	// 新增
	public void addEquip(Equipment equip) throws Exception {
		em.insert(equip);
	};

	// 修改
	public void updateEquipment(Equipment equip) throws Exception {
		em.updateByPrimaryKey(equip);
	};

	// 删除
	public void deleteEquipment(Integer id) throws Exception {
		em.deleteByPrimaryKey(id);
	};

	// 登录
	public User loginUser(User user) throws Exception {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andPasswordEqualTo(user.getPassword()).andUsernameEqualTo(user.getUsername());
		List<User> userList = userMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};

	// 通过用户名判断是否存在，（新增时不能重名）
	public Equipment existfindByName(String ename) throws Exception {
		List<Equipment> eList = em.findByName(ename);
		return eList.isEmpty()?null:eList.get(0);
	};

	// 通过角色判断是否存在
	public User existUserWithRoleId(Integer roleId) throws Exception {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andRoleidEqualTo(roleId);
		List<User> userList = userMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	}

	public PageInfo<Equipment> findEquipPage(Equipment equip, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Equipment> eList = em.findAll(equip);
		PageInfo<Equipment> pageInfo = new PageInfo<Equipment>(eList);
		return pageInfo;
	}

	public List<Modle> findModle() {
		// TODO Auto-generated method stub
		return mm.selectAll();
	}


}

package com.nms.db.dao.equipment.port;

import java.util.List;


import com.nms.db.bean.equipment.port.PortAttr;


public interface PortAttrMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(PortAttr record);

    PortAttr selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortAttr record);

    int updateByPrimaryKey(PortAttr record);

	public List<PortAttr> queryByCondition(PortAttr portAttr);

	public void update(PortAttr portAttr);
	
	public int insert(PortAttr portAttr);
}
package com.nms.db.dao.equipment.port;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.equipment.port.PortStm;


public interface PortStmMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(PortStm record);

    PortStm selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortStm record);

    int updateByPrimaryKey(PortStm record);
    
    public int insert(@Param("portStm")PortStm portStm);

	List<PortStm> quertyBySite(int siteId);
}
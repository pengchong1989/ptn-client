package com.nms.db.dao.ptn.path.eth;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.ptn.path.eth.DualInfo;

public interface DualInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DualInfo record);

    int insertSelective(DualInfo record);

    DualInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DualInfo record);

    int updateByPrimaryKey(DualInfo record);

	List<DualInfo> queryByCondition(DualInfo dualInfo);

	List<DualInfo> queryAll(@Param("label")int label, @Param("siteId")int siteId);

	int query_name(@Param("afterName")String afterName, @Param("beforeName")String beforeName);
	
	List<DualInfo> queryBySiteIdAndBusinessId(int siteId, int businessId);
	
	Integer selectMaxServiceId();
	
	public DualInfo queryById(int id);

	public List<DualInfo> queryNameByPwId(int pwId);
}
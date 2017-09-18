package com.nms.db.dao.ptn.path.eth;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.ptn.path.eth.ElanInfo;

public interface ElanInfoMapper {
	public List<ElanInfo> filterSelect(ElanInfo elanInfo);

	public List<ElanInfo> selectById(@Param("serviceId")int serviceId);

	public List<ElanInfo> queryNodeBySite(ElanInfo condition);

	public List<ElanInfo> queryByPwId(@Param("pwIdList")List<Integer> pwIdList);

	public Integer selectMaxServiceId();

	public void delete(int serviceId);

	public int insert(ElanInfo elanInfo);

	public int queryByName(String elanName);

	public List<ElanInfo> selectBySiteId(int siteId);
	
	/**
	 * 通过条件查询
	 * @param elaninfo
	 */
	public List<ElanInfo> queryElan(ElanInfo elanInfo);
	
	public int query_name(@Param("afterName")String afterName, @Param("beforeName")String beforeName);

	public int query_nameBySingle(@Param("afterName")String afterName, @Param("beforeName")String beforeName, @Param("siteId")int siteId);
	
	public List<ElanInfo> queryAll();

	public List<ElanInfo> selectBySiteAndisSingle(int siteId, int i);
}
package com.nms.db.dao.ptn;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.ptn.CccInfo;



public interface CccInfoMapper {
     
    /**
	 * 列表过滤查询
	 * 
	 * @param etreeInfo
	 *            查询条件
	 * @param connection
	 *            数据库连接
	 * @return
	 * @throws Exception
	 */
	public List<CccInfo> filterSelect(CccInfo cccInfo);
	/**
	 * 查询单网元下的所有cccInfo
	 * 
	 * @param siteId
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public List<CccInfo> queryNodeBySite(@Param("siteId")int siteId);
	
	/**
	 * 查询名称是否重复
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public int query_nameBySingle(@Param("afterName")String afterName,@Param("beforeName")String beforeName,@Param("siteId")int siteId);

	/**
	 * 通过serviceId查询一组etree业务
	 * 
	 * @param serviceId
	 * @param connection
	 * @return
	 */
	public List<CccInfo> queryByServiceId(@Param("serviceId")int serviceId);
}
package com.nms.db.dao.path;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.path.Segment;

public interface SegmentMapper {
	/**
	 * 根据siteId和portId去查询
	 * 获取参数时取segmentCondition的aSiteId和aPortId
	 */
	public List<Segment> queryBySiteIdAndPortId(Segment segmentCondition);
	
	public List<Segment> query_site(Integer siteId);
	
	
	public List<Segment> queryByCondition(@Param("segment")Segment segment);
	
	/**
	 * 查询名字
	 * @param afterName
	 * @param beforeName
	 * @return
	 */
	public int query_name(@Param("afterName")String afterName,@Param("beforeName")String beforeName);

	public List<Segment> query_SegmentPortId(@Param("portId")int portId);

	public List<Segment> queryBySiteId(@Param("siteInstID")Integer siteInstID);

	public List<Segment> query_search(int portidOne, int portidTwo); 
	
	public int deletById(int id);

	public int insert(Segment segment);

	public void updateByPrimaryKey(Segment segment);
}

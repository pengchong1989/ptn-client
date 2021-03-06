package com.nms.db.dao.alarm;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.alarm.HisAlarmInfo;


public interface HisAlarmInfoMapper {
	public List<HisAlarmInfo> queryByCondition(Map<String, Object> map);

	public int update(HisAlarmInfo hisInfo);

	/**
	 * 搜索时，更新告警
	 * @param newTunnelId
	 * @param tid
	 * @param i
	 */
	public void updateObjectid(Map<String, Object> map);

	public int insert(HisAlarmInfo hisInfo);

	public Integer queryMaxId();

	public List<Integer> selectAllIdList(Map<String, Object> map);

	public List<HisAlarmInfo> selectByPage(Map<String, Object> map);

	public List<HisAlarmInfo> queryHisBySites(@Param("siteIdList")List<Integer> siteIdList);

	public List<HisAlarmInfo> queryHisBySlots(Map<String, Object> map);
	
	/**
	 * 根据主键集合，批量删除历史告警数据 
	 * @param idList 主键集合
	 * @return 删除记录数
	 */
	public int deleteByIds(@Param("idList")List<Integer> idList);

	public int selectAlarmCount(Map<String, Object> map);
}
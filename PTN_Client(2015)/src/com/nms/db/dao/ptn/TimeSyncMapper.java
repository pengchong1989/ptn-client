package com.nms.db.dao.ptn;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.ptn.clock.TimeSyncInfo;



public interface TimeSyncMapper {
    int deleteByPrimaryKey(Integer timeid);

    int insert(TimeSyncInfo record);

    int insertSelective(TimeSyncInfo record);

    TimeSyncInfo selectByPrimaryKey(Integer timeid);

    int updateByPrimaryKeySelective(TimeSyncInfo record);

    int updateByPrimaryKey(TimeSyncInfo record);
    
    public List<TimeSyncInfo> queryBySiteId(@Param("siteId")Integer siteId);
}
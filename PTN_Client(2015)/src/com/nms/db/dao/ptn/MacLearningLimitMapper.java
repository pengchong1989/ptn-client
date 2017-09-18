package com.nms.db.dao.ptn;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.ptn.MacLearningInfo;


public interface MacLearningLimitMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MacLearningInfo record);

    int insertSelective(MacLearningInfo record);

    MacLearningInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MacLearningInfo record);

    int updateByPrimaryKey(MacLearningInfo record);
    
    public List<Integer> selectAllPortId(@Param("siteId")Integer siteId);
    
    public List<MacLearningInfo> selectBySiteId(@Param("siteId")Integer siteId);
    
    public MacLearningInfo selectById(@Param("id")Integer id);
}
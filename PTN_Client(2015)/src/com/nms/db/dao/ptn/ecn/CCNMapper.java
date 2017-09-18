package com.nms.db.dao.ptn.ecn;

import java.util.List;

import com.nms.db.bean.ptn.ecn.CCN;


public interface CCNMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CCN record);

    int insertSelective(CCN record);

    CCN selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CCN record);

    int updateByPrimaryKey(CCN record);

	public List<CCN> queryByNeID(String neID);
}
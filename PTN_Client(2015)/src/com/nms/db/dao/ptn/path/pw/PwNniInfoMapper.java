package com.nms.db.dao.ptn.path.pw;

import java.util.List;

import com.nms.db.bean.ptn.path.pw.PwNniInfo;

public interface PwNniInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PwNniInfo record);

    int insertSelective(PwNniInfo record);

    PwNniInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PwNniInfo record);

    int updateByPrimaryKey(PwNniInfo record);

	public List<PwNniInfo> queryByCondition(PwNniInfo pwNniInfo);

	public int delete(int id);

	int update(PwNniInfo pwinfo);
}
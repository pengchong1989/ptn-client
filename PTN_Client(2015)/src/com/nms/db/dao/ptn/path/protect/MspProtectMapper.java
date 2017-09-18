package com.nms.db.dao.ptn.path.protect;

import java.util.List;

import com.nms.db.bean.ptn.path.protect.MspProtect;

public interface MspProtectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MspProtect record);

    int insertSelective(MspProtect record);

    MspProtect selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MspProtect record);

    int updateByPrimaryKey(MspProtect record);

	public List<MspProtect> query(MspProtect mspProtect);
}
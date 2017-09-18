package com.nms.db.dao.ptn.ecn;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.ptn.ecn.OSPFAREAInfo;


public interface OSPFAREAInfoMapper {
    int deleteByPrimaryKey(Integer id);

    public int insert(@Param("osp")OSPFAREAInfo osp);

    int insertSelective(OSPFAREAInfo record);

    OSPFAREAInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OSPFAREAInfo record);

    int updateByPrimaryKey(OSPFAREAInfo record);

	public List<OSPFAREAInfo> queryByNeID(String neId);
}
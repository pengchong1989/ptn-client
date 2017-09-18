package com.nms.db.dao.ptn.oam;

import java.util.List;

import com.nms.db.bean.ptn.oam.OamEthernetInfo;

public interface OamEthernetInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OamEthernetInfo record);

    int insertSelective(OamEthernetInfo record);

    OamEthernetInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OamEthernetInfo record);

    int updateByPrimaryKey(OamEthernetInfo record);

	public List<OamEthernetInfo> queryOamLinkInfoByConditionSide(OamEthernetInfo oamEthernetInfo);
	public int update(OamEthernetInfo oamEthernetInfo);
	public List<OamEthernetInfo> queryOamLinkInfoByCondition(OamEthernetInfo oamEthernetInfo);
}
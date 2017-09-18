package com.nms.db.dao.ptn;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.ptn.EthLoopInfo;



public interface EthLoopMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EthLoopInfo record);

    int insertSelective(EthLoopInfo record);

    EthLoopInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EthLoopInfo record);

    int updateByPrimaryKey(EthLoopInfo record);
    
    /**
	 * 查询全部
	 * @param condition	查询条件
	 * @param connection  数据库连接
	 * @return	List<EthLoopInfo>
	 * @throws Exception
	 */
	public List<EthLoopInfo> queryBySiteId(@Param("siteId")Integer siteId);
	
}
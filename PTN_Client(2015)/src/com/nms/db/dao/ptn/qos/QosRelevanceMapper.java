package com.nms.db.dao.ptn.qos;

import java.util.List;
import java.util.Map;

import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.ptn.qos.QosRelevance;

public interface QosRelevanceMapper {
	public int insert(QosRelevance qosRelevance);

    /**
	 * @param qosRelevance 查询条件 当为null的时候 查询全部
	 */
    public List<QosRelevance> queryByCondition(QosRelevance qosRelevance);

    /**
     * 根据objtype、objid、siteid修改groupid
     * @param qosRelevance
     */
    public void update(QosRelevance qosRelevance);
    
    
    /**
     * 条件查询qosinfo
     * @param qosInfo
     * @return
     */
    public List<QosInfo> queryByCondition_qos(QosInfo qosInfo);

	public void delete(QosRelevance qosRelevance);

	public void updateObjId(Map<String, Object> map);

	public void deleteByCondition(QosRelevance qosRelevance);
    
}
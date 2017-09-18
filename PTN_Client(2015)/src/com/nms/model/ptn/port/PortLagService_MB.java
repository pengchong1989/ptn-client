package com.nms.model.ptn.port;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.dao.equipment.port.PortInstMapper;
import com.nms.db.dao.ptn.port.PortLagInfoMapper;
import com.nms.db.dao.ptn.qos.QosQueueMapper;
import com.nms.db.enums.EActionType;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;

public class PortLagService_MB extends ObjectService_Mybatis{
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	private PortLagInfoMapper mapper;

	public PortLagInfoMapper getPortLagInfoMapper() {
		return mapper;
	}

	public void setPortLagInfoMapper(PortLagInfoMapper portLagInfoMapper) {
		this.mapper = portLagInfoMapper;
	}

	public List<PortLagInfo> selectPortByCondition(PortLagInfo portLagInfo) {
		List<PortLagInfo> portLagInfoList = new ArrayList<PortLagInfo>();
		try {
			portLagInfoList = this.mapper.queryByCondition(portLagInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return portLagInfoList;
	}

	public void updateLag(PortLagInfo portLagInfo) {
		boolean flag_qos_insert = false;
		try {
			SiteService_MB siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE, this.sqlSession);
			PortInstMapper portInstMapper = this.sqlSession.getMapper(PortInstMapper.class);
			QosQueueMapper qosQueueMapper = this.sqlSession.getMapper(QosQueueMapper.class);
			// 查询所有此lag修改之前的数据 修改端口用
			PortLagInfo portLagInfo_select = new PortLagInfo();
			portLagInfo_select.setId(portLagInfo.getId());
			List<PortLagInfo> portLagInfoList = this.selectByCondition(portLagInfo_select);
			if (null != portLagInfoList && portLagInfoList.size() == 1) {
				portLagInfo_select = portLagInfoList.get(0);
				for (PortInst portInst : portLagInfo_select.getPortList()) {
					portInst.setLagId(0);
					if (siteService.getManufacturer(portInst.getSiteId()) == EManufacturer.CHENXIAO.getValue()) {
						portInst.setPortType("NONE");
					}
					portInstMapper.update(portInst);
				}
			}
			// 修改端口状态
			for (PortInst portinst : portLagInfo.getPortList()) {
				portinst.setPortType(portLagInfo.getRole());
				portinst.setLagId(portLagInfo.getId());
				portInstMapper.update(portinst);
			}
			// 修改QOS 先查询此lag的qos是否存在
			QosQueue qosQueue_select = new QosQueue();
			qosQueue_select.setObjId(portLagInfo.getId());
			qosQueue_select.setObjType("LAG");
			List<QosQueue> qosQueueList = qosQueueMapper.queryByCondition(qosQueue_select);
			if (null == qosQueueList || qosQueueList.size() == 0) {
				flag_qos_insert = true;
			}
			// 遍历此lag的qos 如果不存在就新建 如果存在就修改
			for (QosQueue qosQueue : portLagInfo.getQosQueueList()) {
				qosQueue.setObjId(portLagInfo.getId());
				if (flag_qos_insert) {
					qosQueueMapper.insert(qosQueue);
				} else {
					for (QosQueue qosQueueSelect : qosQueueList) {
						if (qosQueueSelect.getCos() == qosQueue.getCos()) {
							qosQueue.setId(qosQueueSelect.getId());
							break;
						}
					}
					qosQueueMapper.update(qosQueue);
				}
			}
			// 修改LAG
			this.mapper.update(portLagInfo);
			// 离线网元数据下载
			super.dateDownLoad(portLagInfo.getSiteId(), portLagInfo.getId(), EServiceType.LAG.getValue(), EActionType.UPDATE.getValue(), portLagInfo.getLagID() + "", null, 0, 0, null);
			this.sqlSession.commit();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 查找端口高级配置
	 * @param portLagInfo
	 * @return
	 * @throws Exception
	 */
	public List<PortLagInfo> selectByCondition(PortLagInfo portLagInfo) throws Exception {
		List<PortLagInfo> portLagInfoList = new ArrayList<PortLagInfo>();
		try {
			PortInstMapper portInstMapper = this.sqlSession.getMapper(PortInstMapper.class);
			QosQueueMapper qosQueueMapper = this.sqlSession.getMapper(QosQueueMapper.class);
			portLagInfoList = this.mapper.queryByCondition(portLagInfo);
			for (PortLagInfo portLagInfoSelect : portLagInfoList) {
				//修改时，需要用到这些值
				if(portLagInfoSelect.getPortLagMember() != ""){
					portLagInfoSelect.setPortLagMember1Log(portLagInfoSelect.getPortLagMember().split(",")[0]);
					portLagInfoSelect.setPortLagMember2Log(portLagInfoSelect.getPortLagMember().split(",")[1]);
					portLagInfoSelect.setPortLagMember3Log(portLagInfoSelect.getPortLagMember().split(",")[2]);
					portLagInfoSelect.setPortLagMember4Log(portLagInfoSelect.getPortLagMember().split(",")[3]);
				}
				// 设置此lag的所有端口
				PortInst portInst = new PortInst();
				portInst.setLagId(portLagInfoSelect.getId());
				portLagInfoSelect.setPortList(portInstMapper.queryByCondition(portInst));
				// 设置此lag下的qos
				QosQueue qosQueue = new QosQueue();
				qosQueue.setObjId(portLagInfoSelect.getId());
				qosQueue.setObjType("LAG");
				portLagInfoSelect.setQosQueueList(qosQueueMapper.queryByCondition(qosQueue));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return portLagInfoList;
	}

	public List<PortLagInfo> selectLAGByCondition(PortLagInfo portLagInfo) {
		List<PortLagInfo> portLagInfoList = new ArrayList<PortLagInfo>();
		try {
			portLagInfoList = this.mapper.queryByCondition(portLagInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return portLagInfoList;
	}

	public int updateStatus(PortLagInfo portLagInfo) {
		return this.mapper.update(portLagInfo);
	}
	
	/**
	 * 根据lag主键查询lag名称。 显示用
	 * 
	 * @author kk
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public String getLagName(int lagId) throws Exception {
		PortLagInfo portLagInfo = null;
		List<PortLagInfo> portlagInfoList = null;
		try {

			portLagInfo = new PortLagInfo();
			portLagInfo.setId(lagId);
			portlagInfoList = this.selectByCondition(portLagInfo);

			if (null != portlagInfoList && portlagInfoList.size() == 1) {
				return "lag/" + portlagInfoList.get(0).getLagID();
			} else {
				throw new Exception("查询lag出错");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int selectLagCountByNeId(int NeId) throws Exception {
		return mapper.selectCountByNeId(NeId);
	}
	
}

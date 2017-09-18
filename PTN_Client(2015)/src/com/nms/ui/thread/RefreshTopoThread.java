package com.nms.ui.thread;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twaver.AlarmModel;
import twaver.Element;
import twaver.Node;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.system.Field;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.Ptnf;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.util.TopologyUtil;
import com.nms.ui.ptn.basicinfo.NetWorkInfoPanel;
import com.nms.ui.topology.NetworkElementPanel;
import com.nms.ui.topology.util.CreateElementUtil;

/**
 * 刷新拓扑图线程，包括 拓扑上的告警，网元连接状态，告警灯显示
 * 
 * @author kk
 * 
 */
public class RefreshTopoThread implements Runnable {

	private List<SiteInst> siteInstList = null; // 上一次的网元集合
	private List<SiteInst> siteInstListChange = new ArrayList<SiteInst>(); // 有变化的网元集合
	// map的key
	private final String KEY_SITE = "site";
	private final String KEY_PARENT = "parent";

	public RefreshTopoThread() {
		Thread.currentThread().setName("RefreshTopoThread");
	}

	@Override
	public void run() {
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			while (true) {
				try {
					//当前面板是否为toop
					if(Ptnf.getPtnf().isToop())
					{
						// 验证网元是否有变化
						if (this.siteIsChange(siteService))
						{
							this.refreshSite();
						}
					}
					if(Ptnf.getPtnf().getjTabbedPane1().getSelectedComponent() instanceof NetWorkInfoPanel)
					{
						this.refreshCurrSite(siteService);
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, getClass());
				}
				Thread.sleep(5*1000);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally
		{
            UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 离线网元集合是否有变化
	 * 
	 * @return true 有变化。刷新拓扑。 false 无变化
	 * @throws Exception
	 */
	private boolean siteIsChange(SiteService_MB siteService) throws Exception {
		boolean flag = false;
		List<SiteInst> siteInstList_select = null;
		try {
			// 查询所有网元
			siteInstList_select = siteService.select();

			// 如果上次查询的网元集合为null 直接赋值
			if (null == this.siteInstList) {
				this.siteInstList = new ArrayList<SiteInst>();
				this.siteInstList.addAll(siteInstList_select);
			} else {
				// 如果这次查询的网元比前一次少，说明有移除的网元
				if (siteInstList_select.size() < this.siteInstList.size()) {
					this.removeSite(siteInstList_select);
				}

				// 比较新查出网元集合是否与上次的网元集合有变化
				for (SiteInst siteInst : siteInstList_select) {
					this.compare(siteInst);
				}
				// 把最新的集合放入上次查询集合中
				this.siteInstList = new ArrayList<SiteInst>();
				this.siteInstList.addAll(siteInstList_select);
			}

			// 如果变化的集合有元素。返回true
			if (this.siteInstListChange.size() > 0) {
				flag = true;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			siteInstList_select = null;
		}

		return flag;
	}

	/**
	 * 比较siteInst是否在上一次的siteInstList集合中是否有改变。如果有改变，把此网元对象加入到siteInstListChange集合中
	 * 
	 * @param siteInst
	 *            要比较的网元对象
	 * @throws Exception
	 */
	private void compare(SiteInst siteInst) throws Exception {

		boolean flag = true; // 是否改变
		try {

			for (SiteInst siteInst_last : this.siteInstList) {

				if (siteInst.getSite_Inst_Id() == siteInst_last.getSite_Inst_Id()) {
					// 比较登陆状态、父节点ID是否改变（field表）、网元显示名称
					if(siteInst.getFieldID()>0){
					if (siteInst.getLoginstatus() == siteInst_last.getLoginstatus() && siteInst.getFieldID() == siteInst_last.getFieldID() && siteInst.getCellId().equals(siteInst_last.getCellId())) {
						flag = false;
					}
					break;
				}
					
				}
			}

			// 如果改变了。 就添加到改变集合中
			if (flag) {
				this.siteInstListChange.add(siteInst);
			}

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 刷新网元,根据siteInstListChange刷新。 如果此集合有元素。 才去刷新网元
	 * 
	 * @throws Exception
	 */
	private void refreshSite() throws Exception {
		Map<String, Element> map = null;
		Element element_parent = null;
		Node node = null;
		AlarmModel alarmModel = null;
		CreateElementUtil createElementUtil = null;
		TopologyUtil topologyUtil = new TopologyUtil();
		try {
			createElementUtil = new CreateElementUtil();
			if (this.siteInstListChange.size() > 0) {

				for (SiteInst siteInst : siteInstListChange) {
					map = this.selectElement(siteInst);

					// 为删除网元
					if (siteInst.getIsDeleteTopo() == 1) {
						// 从拓扑中移除网元
						if (null != map.get(this.KEY_SITE)) {
							node = (Node) map.get(this.KEY_SITE);
							NetworkElementPanel.getNetworkElementPanel().getBox().removeElement(node);
						}

					} else {
						// 如果存在父节点 去比较子节点
						if (null != map.get(this.KEY_PARENT)) {
							element_parent = map.get(this.KEY_PARENT);
							// 如果找到网元节点、说明此网元修改了登陆状态等信息。 直接在拓扑上进行修改
							if (null != map.get(this.KEY_SITE)) {
								node = (Node) map.get(this.KEY_SITE);
								topologyUtil.setNodeImage(node, siteInst);
								node.setName(siteInst.getCellId());
								node.setParent(element_parent);

								// 如果修改后的网元是离线网元，就清除此网元上的告警
								if (siteInst.getLoginstatus() == 0) {
									alarmModel = NetworkElementPanel.getNetworkElementPanel().getBox().getAlarmModel();
									alarmModel.removeAlarmsByElement(node);
								}
							} else {
								// 否则新创建一个网元
								node = createElementUtil.createNode(siteInst, element_parent);
								NetworkElementPanel.getNetworkElementPanel().getBox().addElement(node);
							}
						}
					}
					map = null;
					element_parent = null;
					node = null;
					alarmModel = null;
				}
				// 清空集合
				this.siteInstListChange = new ArrayList<SiteInst>();
			}

		} catch (Exception e) {
			throw e;
		} finally {
			createElementUtil = null;
			topologyUtil = null;
		}

	}

	/**
	 * 从拓扑中查询元素
	 * 
	 * @return map key为类型， site、parent。 site代表网元节点。如果为NULL 说明需要创建一个网元节点。 parent代表父节点。为此网元节点的父节点
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Element> selectElement(SiteInst siteInst) throws Exception {

		List<Element> elementList = null;
		Map<String, Element> map = new HashMap<String, Element>();
		try {
			// 获取拓扑上的所有元素
			elementList = NetworkElementPanel.getNetworkElementPanel().getBox().getAllElements();
			// 遍历元素 找出此网元对应的节点和父节点
			for (Element element : elementList) {
				// 如果是field对象。就找出父节点
				if (element.getUserObject() instanceof Field) {
					int id = ((Field) element.getUserObject()).getId();
					if (id == siteInst.getFieldID()) {
						map.put(this.KEY_PARENT, element);
					}
				} else if (element.getUserObject() instanceof SiteInst) {
					// 如果是网元 找出网元节点
					int id = ((SiteInst) element.getUserObject()).getSite_Inst_Id();
					if (id == siteInst.getSite_Inst_Id()) {
						map.put(this.KEY_SITE, element);
					}
				}
				// 如果网元和域都找到了。结束循环
				if (null != map.get(this.KEY_PARENT) && null != map.get(this.KEY_SITE)) {
					break;
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			elementList = null;
		}
		return map;
	}

	/**
	 * 移除网元,比较上次网元是否都再此次集合中， 如果有不在的。 代表删除操作
	 */
	private void removeSite(List<SiteInst> siteInstList_db) {

		for (SiteInst siteInst : this.siteInstList) {
			boolean flag = false;
			for (SiteInst siteInst_db : siteInstList_db) {
				if (siteInst_db.getSite_Inst_Id() == siteInst.getSite_Inst_Id()) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				siteInst.setIsDeleteTopo(1);
				this.siteInstListChange.add(siteInst);
			}
		}
	}

	/**
	 * 刷新当前网元的状态
	 */
	private void refreshCurrSite(SiteService_MB siteService){
		SiteInst site = null;
		try {
			site = siteService.select(ConstantUtil.siteId);
			if(site.getLoginstatus() == 0){
				Ptnf.getPtnf().isOnLine(false);
			}else{
				Ptnf.getPtnf().isOnLine(true);
			}
		} catch (Exception e2) {
			ExceptionManage.dispose(e2, this.getClass());
		} finally {
			site = null;
		}
	}
}

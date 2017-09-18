package com.nms.ui.topology.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import twaver.Dummy;
import twaver.Element;
import twaver.Group;
import twaver.Node;
import twaver.SubNetwork;
import twaver.TDataBox;
import twaver.network.TNetwork;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.NetWork;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.system.FieldService_MB;
import com.nms.model.system.NetService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.topology.NetworkElementPanel;

public class SavePlaceAction implements Action {

	private TNetwork network;

	public SavePlaceAction(TNetwork network) {
		this.network = network;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	public Object getValue(String key) {
		return null;
	}

	public boolean isEnabled() {
		return false;
	}

	public void putValue(String key, Object value) {

	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

	public void setEnabled(boolean b) {

	}

	public void actionPerformed(ActionEvent e) {

		List<Element> elements = null;
		TDataBox tDataBox = null;
		List<NetWork> netWorks = null;
		try {
			tDataBox = network.getDataBox();
			elements = tDataBox.getAllElements();
			if (network.getCurrentSubNetwork() == null) {
				if (NetworkElementPanel.getNetworkElementPanel().getShowTopoType().equals("SEGMENT")) {
					netWorks = new ArrayList<NetWork>();
					for (Element element : elements) {
						if (element instanceof SubNetwork) {
							netWorks.add(this.convertNetWork(element));
						}
					}
					this.saveDBNet(netWorks);
				} else {
					this.saveSiteXY(elements);
				}
			} else {
				List<Dummy> dummys = (List<Dummy>)network.getCurrentSubNetwork().getChildren();
				for(Dummy dummy :dummys){
					elements = dummy.getChildren();
					this.saveSiteXY(elements);
				}
			}
			DialogBoxUtil.succeedDialog(network, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
		} catch (Exception e2) {
			ExceptionManage.dispose(e2,this.getClass());
		} finally {
			elements = null;
			tDataBox = null;
			netWorks = null;
		}

	}

	private void saveSiteXY(List<Element> elements) throws Exception {
		SiteInst siteInst = null;
		List<SiteInst> siteInstList = null;
		SiteService_MB siteService = null;
		List<Field> fieldList=new ArrayList<Field>();
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInstList = new ArrayList<SiteInst>();
			for (Element element : elements) {
				if (element instanceof Group) {
					fieldList.add(this.convertField(element));

				}else if(element instanceof Node){
					siteInst = (SiteInst) element.getUserObject();
					siteInst.setSiteX(element.getLocation().x);
					siteInst.setSiteY(element.getLocation().y);
					siteInstList.add(siteInst);
				}
			}
			this.saveDBField(fieldList);
			siteService.updateBatch(siteInstList);
//			NetworkElementPanel.getNetworkElementPanel().showTopo(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally{
			UiUtil.closeService_MB(siteService);
		}

	}
	
	/**
	 * 修改域
	 * @param fieldList 域集合
	 * @throws Exception 
	 */
	private void saveDBField(List<Field> fields) throws Exception{
		FieldService_MB fieldService = null;
		try {
			if(fields.size()>0){
				fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
				fieldService.updateBatch(fields);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(fieldService);
		}
	}
	
	/**
	 * 把element对象转换成要修改的Field对象
	 * @param element 拓扑元素
	 * @return 域DB对象
	 * @throws Exception 
	 */
	private Field convertField(Element element) throws Exception{
		Field field = null;
		try {
			field = (Field) element.getUserObject();
			field.setFieldX(element.getLocation().x);
			field.setFieldY(element.getLocation().y);
		} catch (Exception e) {
			throw e;
		}
		return field;
	}
	
	/**
	 * 修改域
	 * @param fieldList 域集合
	 * @throws Exception 
	 */
	private void saveDBNet(List<NetWork> netWorks) throws Exception{
		NetService_MB netService = null;
		try {
			if(netWorks.size()>0){
				netService = (NetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NETWORKSERVICE);
				netService.updateBatch(netWorks);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(netService);
		}
	}
	
	/**
	 * 把element对象转换成要修改的NetWork对象
	 * @param element 拓扑元素
	 * @return 域DB对象
	 * @throws Exception 
	 */
	private NetWork convertNetWork(Element element) throws Exception{
		NetWork netWork = null;
		try {
			netWork = (NetWork) element.getUserObject();
			netWork.setNetX(element.getLocation().x);
			netWork.setNetY(element.getLocation().y);
		} catch (Exception e) {
			throw e;
		}
		return netWork;
	}
}

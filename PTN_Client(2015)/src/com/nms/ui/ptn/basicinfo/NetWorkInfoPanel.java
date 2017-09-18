﻿/*
 * NetWorkInfoPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.basicinfo;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import twaver.TWaverUtil;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EManufacturer;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ReadTableXml;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.IconNode;
import com.nms.ui.manager.IconNodeRenderer;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.ptn.safety.roleManage.RoleRoot;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.ShelfTopology;

/**
 * 
 * @author __USER__
 */
@SuppressWarnings("serial")
public class NetWorkInfoPanel extends javax.swing.JPanel {

	/** Creates new form NetWorkInfoPanel */
	public NetWorkInfoPanel() {
		initComponents();
		try {
			this.splitPaneSize();
			this.treeData();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 设计split的大小
	 */
	private void splitPaneSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		jTree1.setPreferredSize(new Dimension(width / 12 * 2, height - 200));
		jTree1.setMinimumSize(new Dimension(width / 12 * 2, height - 200));
		jSplitPane1.setDividerLocation(width / 12 * 2);
		jScrollPane1.setMinimumSize(new Dimension(width / 12 * 2, height - 400));
		// jSplitPane1.disable();
	}

	// private List<UdaGroup> getUdaGroup(String codeName,String codeGroupIdentity) throws Exception{
	// List<UdaGroup> udaGroupList=null;
	// List<Code> codeList=null;
	// String codeId=null;
	// UdaGroup udaGroup=null;
	// UdaGroupService udaGroupService=null;
	// try {
	// codeList=UiUtil.getCodeGroupByIdentity(codeGroupIdentity).getCodeList();
	// for(Code code:codeList){
	// if(code.getCodeName().equals(codeName)){
	// codeId=code.getId()+"";
	// }
	// }
	// udaGroupService=(UdaGroupService) ConstantUtil.serviceFactory.newService(Services.UDAGROUP);
	// udaGroup=new UdaGroup();
	// udaGroup.setGroupType(codeId);
	// udaGroup.setParentId(-1);
	// udaGroupList=udaGroupService.select(udaGroup);
	//			
	// } catch (Exception e) {
	// throw e;
	// }
	// return udaGroupList;
	// }

	private String getRealPath(SiteInst siteInst)
	{
		String path="";
		path = "config/treeNode/tree_node.xml";
		if(siteInst.getCellType().contains("710")){
			path = "config/treeNode/710_tree_node.xml";
		}
		
		if ("en_US".equals(ResourceUtil.language)) {
			path = "config/treeNode/tree_node_en.xml";
		}
		
		return path;
	}
	
	private void treeData() throws Exception {

		IconNode rootNode = null;
		IconNode parentNode = null;
		IconNode childNode = null;
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;
		Element root = null;
		NodeList nodeList = null;
		Element parent = null;
		NodeList childList = null;
		Element child = null;
		// List<UdaGroup> udaGroupList=null;
		String parameter = null;
		String clickClass = null;
		String clickClagg_parent = null;
		SiteService_MB siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
		SiteInst siteInst = null;
		try {

			rootNode = new IconNode(new ImageIcon(getClass().getResource("/com/nms/ui/images/tree/tree_main.png")), ResourceUtil.srcStr(StringKeysObj.TREE_FUNCTION), new ControlKeyValue("", ResourceUtil.srcStr(StringKeysObj.TREE_FUNCTION), null));
			siteInst = siteService.selectById(ConstantUtil.siteId);
			// 获得一个新的DocumentBuilderFactory实例
			factory = DocumentBuilderFactory.newInstance();
			// 使用DocumentBuilderFactory构建DocumentBulider
			builder = factory.newDocumentBuilder();

			//根据不同语言，区分不同路径
			String path = getRealPath(siteInst);
			// 使用DocumentBuilder的parse()方法解析文件
			doc = builder.parse(ReadTableXml.class.getClassLoader().getResource(path).toString());
			root = doc.getDocumentElement();
			nodeList = root.getElementsByTagName("node");

			for (int i = 0; i < nodeList.getLength(); i++) {
				parent = (Element) nodeList.item(i);
				//不同厂商设备，调用不同的列表类
				
				if (siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()) {
					clickClagg_parent = parent.getAttribute("whClickNodeClass");
				} else {
					clickClagg_parent = parent.getAttribute("clickNodeClass");
				}
				
				parentNode = new IconNode(new ImageIcon(getClass().getResource("/com/nms/ui/images/tree/" + parent.getAttribute("iconName"))), parent.getAttribute("nodename"), new ControlKeyValue("", parent.getAttribute("nodename"), clickClagg_parent));
				childList = parent.getElementsByTagName("childNode");
				if (childList.getLength() > 0) {
					for (int j = 0; j < childList.getLength(); j++) {
						child = (Element) childList.item(j);

//						if (null != child.getAttribute("parameter") && !"".equals(child.getAttribute("parameter"))) {
//							parameter = child.getAttribute("parameter");
//						}

						//不同厂商设备，调用不同的列表类
						if (siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()) {
							if ("".equals(child.getAttribute("whClickNodeClass"))) {
								continue;
							} else {
								clickClass = child.getAttribute("whClickNodeClass");
								//根据厂商设备型号来判断是否需要该项功能
								if(!hasFunctionItem(clickClass)){
									continue;
								}
							}
						} else {
							if ("".equals(child.getAttribute("clickNodeClass"))) {
								continue;
							} else {
								clickClass = child.getAttribute("clickNodeClass");
							}
						}
						childNode = new IconNode(new ImageIcon(getClass().getResource("/com/nms/ui/images/tree/" + child.getAttribute("iconName"))), child.getAttribute("nodename"), new ControlKeyValue(parameter, child.getAttribute("nodename"), clickClass));
						parentNode.add(childNode);
					}
				}
				//如果没有子节点，并且这个父节点的 “是否存在子节点” 属性=true  就不添加这个父节点
				if(parentNode.getChildCount() == 0 && "true".equals(parent.getAttribute("isExistChild"))){
					continue;
				}else{
					rootNode.add(parentNode);
				}
				
			}
			this.jTree1.setModel(new DefaultTreeModel(rootNode));
			this.jTree1.setCellRenderer(new IconNodeRenderer());
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
		}

	}

	/**
	 * 根据厂商设备型号来判断是否需要该项功能
	 * @throws Exception 
	 */
	private boolean hasFunctionItem(String clickClass) throws Exception {
		//武汉设备只有710系列有风扇
		SiteService_MB siteService = null;
		try {
			if(("com.nms.ui.ptn.ne.smartFan.view.SmartFanConfigNodePanel").equals(clickClass)){
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				SiteInst site = siteService.select(ConstantUtil.siteId);
				if(!site.getCellType().contains("710")){
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
		}
		return true;
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jSplitPane1 = new javax.swing.JSplitPane();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTree1 = new javax.swing.JTree();
		jPanel1 = new javax.swing.JPanel();
		RoleRoot roleRoot=new RoleRoot();//权限验证
		jTree1.setPreferredSize(new java.awt.Dimension(100, 64));
		jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			@Override
			public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
				jTree1ValueChanged(evt);
			}
		});
		jScrollPane1.setViewportView(jTree1);

		jSplitPane1.setLeftComponent(jScrollPane1);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 743, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 503, Short.MAX_VALUE));

		jSplitPane1.setRightComponent(jPanel1);
		//单击网元默认右界面显示网元机架图
		try {
			String classAddress = "com.nms.ui.topology.ShelfTopology";
			Class cls = Class.forName(classAddress);
			JPanel jPanel = (JPanel) cls.newInstance();
			this.jSplitPane1.setRightComponent(jPanel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE));
		
		if(roleRoot.root(RootFactory.CORE_SELECT) || roleRoot.root(RootFactory.CORE_MANAGE)){
			this.jTree1.setEnabled(true);
		}else{
			this.jTree1.setEnabled(false);
		}
	}// </editor-fold>

	// GEN-END:initComponents

	private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {
		Class cls = null;
		JPanel jPanel = null;
		ControlKeyValue controlKeyValue =null;
		JPanel clickBeforePanel=null;
		try {
			controlKeyValue = ((IconNode) evt.getPath().getLastPathComponent()).getControlKeyValue();
			if (null != controlKeyValue.getObject()) {
				if (!"".equals(controlKeyValue.getObject())) {
					ConstantUtil.portType = controlKeyValue.getId();
					if (!controlKeyValue.getObject().toString().equals("")) {
						clickBeforePanel=(JPanel) this.jSplitPane1.getRightComponent();
						//com.nms.ui.topology.ShelfTopology
						cls = Class.forName(controlKeyValue.getObject().toString());
						jPanel = (JPanel) cls.newInstance();
						this.jSplitPane1.setRightComponent(jPanel);
						
						//如果点击之前是设备面板图界面。那么清楚twaver的图片缓存，并且执行java垃圾回收
						if(clickBeforePanel instanceof ShelfTopology){
							TWaverUtil.clearImageIconCache();
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			cls = null;
			jPanel = null;
			controlKeyValue=null;
		}
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JTree jTree1;
	// End of variables declaration//GEN-END:variables
	public javax.swing.JPanel getjPanel1() {
		return jPanel1;
	}

	public void setjPanel1(javax.swing.JPanel jPanel1) {
		this.jPanel1 = jPanel1;
	}

}
/*
 * PortPropertyDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.ne.eth.view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;

import com.nms.db.bean.equipment.port.PortAttr;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EManufacturer;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTitle;

/**
 * 端口参数
 * @author __USER__
 */
public class PortPropertyDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7810935676210248875L;
	private PortInst portInst;
	private String port_type = "";
	private PortConfigPanel portConfigPanel ;
	/** Creates new form PortPropertyDialog */
	public PortPropertyDialog(java.awt.Frame parent, boolean modal) {
		initComponents();
	}

	/**
	 * 创建一个新的实例
	 * @param portinst
	 * 				端口
	 * @param type
	 * 			类型
	 * @param map
	 */
	public PortPropertyDialog(PortInst portinst, String type, Map<Integer, PortAttr> map) {
		this.setModal(true);
		initComponents();
		addListener();
		SiteService_MB siteService = null;
		try {
			
			if(null!=portinst){
				if(portinst.getLagId()>0){
					this.portTypeCBox.setEnabled(false);
				}
			}
			
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_ETH_PORT));
//			UiUtil.tabPanel(jTabbedPane1, UiUtil.getUdaGroup("port"), ConstantUtil.INT_WIDTH_ONE, true, map, false);
			this.comboBoxDataDate();
			JScrollPane jScrollPane = new JScrollPane();
			portConfigPanel = new PortConfigPanel(portinst);
			jScrollPane.setViewportView(portConfigPanel);
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
				jTabbedPane1.add("高级配置", jScrollPane);
			}
			if (null != type && !"".equals(type)) {
				this.port_type = type;

				for (int i = 0; i < this.jTabbedPane1.getTabCount(); i++) {
					if (jTabbedPane1.getTitleAt(i).equals(port_type)) {
						int index = i;
						if (port_type.equals("NNI")) {
							index = i + 1;
						} else {
							index = i - 1;
						}
						jTabbedPane1.setEnabledAt(index, false);
					}
				}
			} else {

				for (int i = 0; i < this.jTabbedPane1.getTabCount(); i++) {
					if (jTabbedPane1.getTitleAt(i).equals("NNI")) {
						jTabbedPane1.setEnabledAt(i, false);
					}
					if (jTabbedPane1.getTitleAt(i).equals("UNI")) {
						jTabbedPane1.setEnabledAt(i, false);
					}
				}

			}
			if (null != portinst) {
				this.portInst = portinst;
				this.txtMac.setText(portinst.getMacAddress());
				this.jTextField1.setText(portinst.getPortName());
				super.getComboBoxDataUtil().comboBoxSelect(enableComBox, String.valueOf(portinst.getIsEnabled_code()));
				if ("UNI".equals(portinst.getPortType())) {
					this.portTypeCBox.setSelectedIndex(1);
				} else if ("NNI".equals(portinst.getPortType())) {
					this.portTypeCBox.setSelectedIndex(2);
				} else if ("".equals(portinst.getPortType())) {
					this.portTypeCBox.setSelectedItem("");
				}

				// 判断下此端口归属设备的厂商。如果是武汉，把端口类型的下拉列表锁住，不让修改
//				if (UiUtil.getManufacturer(ConstantUtil.siteId)==EManufacturer.WUHAN.getValue()) {
//					this.portTypeCBox.setEnabled(false);
//				}
				// 如果被段占用了，不能修改端口的类型
				if (this.portInst.getIsOccupy() != 0) {
					this.portTypeCBox.setEnabled(false);
				}
				// 如果被ac用过的端口，不能修改端口类型
				List<AcPortInfo> acPortList = getAcPortInfoByPort();
				if (acPortList != null && !acPortList.isEmpty()) {
					this.portTypeCBox.setEnabled(false);
				}
				//单网元没有段，如果被tunnel使用，不能修改端口的类型
				List<Tunnel> tunnels = getTunnelByPort();
				if(tunnels != null && tunnels.size()>0){
					this.portTypeCBox.setEnabled(false);
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 根据siteId和portId查询tunnel
	 * @return
	 */
	private List<Tunnel> getTunnelByPort() {
		TunnelService_MB tunnelService = null;
		List<Tunnel> tunnels = null;
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			tunnels = tunnelService.selectByPortIdAndSiteId(ConstantUtil.siteId, portInst.getPortId());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(tunnelService);
		}
		return tunnels;
	}

	/**
	 * 通过端口号查找ac
	 * @return
	 */
	private List<AcPortInfo> getAcPortInfoByPort() {
		AcPortInfoService_MB acInfoService = null;
		AcPortInfo acportInfo = null;
		try {
			acportInfo = new AcPortInfo();
			acportInfo.setPortId(this.portInst.getPortId());
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			return acInfoService.queryByAcPortInfo(acportInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(acInfoService);
		}
		return null;
	}

	/**
	 * 监听事件
	 */
	private void addListener() {
		portTypeCBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
					if (((ControlKeyValue) portTypeCBox.getSelectedItem()).getName().equals("NNI")) {
						if (jTabbedPane1.getTitleAt(i).equals("NNI")) {
							jTabbedPane1.setEnabledAt(i, true);
						}
						if (jTabbedPane1.getTitleAt(i).equals("UNI")) {
							jTabbedPane1.setEnabledAt(i, false);
						}
					}
					if (((ControlKeyValue) portTypeCBox.getSelectedItem()).getName().equals("UNI")) {
						if (jTabbedPane1.getTitleAt(i).equals("NNI")) {
							jTabbedPane1.setEnabledAt(i, false);
						}
						if (jTabbedPane1.getTitleAt(i).equals("UNI")) {
							jTabbedPane1.setEnabledAt(i, true);
						}
					}
					if (((ControlKeyValue) portTypeCBox.getSelectedItem()).getName().equals("NONE")) {
						if (jTabbedPane1.getTitleAt(i).equals("NNI")) {
							jTabbedPane1.setEnabledAt(i, false);
						}
						if (jTabbedPane1.getTitleAt(i).equals("UNI")) {
							jTabbedPane1.setEnabledAt(i, false);
						}
					}
				}
			}
		});
	}

	/**
	 * 创建一个新的实例
	 * @param portinst
	 * 				端口
	 * @param type
	 * 			类型
	 */
	public PortPropertyDialog(PortInst portinst, String type) {
		this.setModal(true);
		initComponents();
		try {
			this.comboBoxDataDate();
			if (null != type && !"".equals(type)) {
				this.port_type = type;
				for (int i = 0; i < this.jTabbedPane1.getTabCount(); i++) {
					if (jTabbedPane1.getTitleAt(i).equals(port_type)) {
						int index = i;
						if (port_type.equals("NNI")) {
							index = i + 1;
						} else {
							index = i - 1;
						}
						jTabbedPane1.setEnabledAt(index, false);
					}
				}
			}
			if (null != portinst) {
				this.portInst = portinst;
				this.jTextField1.setText(portinst.getPortName());
				super.getComboBoxDataUtil().comboBoxSelect(enableComBox, String.valueOf(portinst.getIsEnabled_code()));
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 初始化下拉菜单
	 * @throws Exception
	 */
	private void comboBoxDataDate() throws Exception {

		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			defaultComboBoxModel = (DefaultComboBoxModel) enableComBox.getModel();
			defaultComboBoxModel.addElement(new ControlKeyValue("0", ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED_NO), null));
			defaultComboBoxModel.addElement(new ControlKeyValue("1", ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED), null));
			enableComBox.setModel(defaultComboBoxModel);
			// 端口类型
			super.getComboBoxDataUtil().comboBoxData(this.portTypeCBox, "PORTTYPE");

		} catch (Exception e) {
			throw e;
		} finally {
			defaultComboBoxModel = null;
		}
	}

	public JButton getJButton() {
		return this.jButton1;
	}

	/**
	 * 拿到端口数据
	 * @return
	 * @throws Exception
	 */
	public PortInst getPortInst() throws Exception {
		this.getPortConfig();
		this.portInst.setPortType(((ControlKeyValue) this.portTypeCBox.getSelectedItem()).getName());
		this.portInst.setMacAddress(this.txtMac.getText());
		if (this.enableComBox.getSelectedIndex() >= 0) {
			ControlKeyValue endabled = (ControlKeyValue) this.enableComBox.getSelectedItem();
			this.portInst.setIsEnabled_code(Integer.parseInt(endabled.getId()));
		}
		return this.portInst;
	}
	
	/**
	 * 获得高级配置面板值
	 */
	public void getPortConfig(){
		
		if(portInst.getLagInfo() == null){
			PortLagInfo portLagInfo = new PortLagInfo();
			portInst.setLagInfo(portLagInfo);
		}
		portInst.getLagInfo().setPortEnable(((ControlKeyValue) portConfigPanel.getPortStatusComboBox().getSelectedItem()).equals(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED_NO)) ? 0 : 1);
		portInst.getLagInfo().setFlowControl(((ControlKeyValue) portConfigPanel.getBufferComboBox().getSelectedItem()).equals(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED_NO)) ? 0 : 1);
		portInst.getLagInfo().setMtu(Integer.parseInt(portConfigPanel.getMtuText().getText()));
		portInst.getLagInfo().setPortLimitation(Integer.parseInt(portConfigPanel.getPortLimitationText().getText()));
		portInst.getLagInfo().setBroadcastBate(((ControlKeyValue) portConfigPanel.getBroadcastBateComboBox().getSelectedItem()).equals(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_CLOSE)) ? 0 : 1);
		portInst.getLagInfo().setBroadcastFlux(Integer.parseInt(portConfigPanel.getBroadcastFluxText().getText()));
		portInst.getLagInfo().setGroupBroadcastBate(((ControlKeyValue) portConfigPanel.getGroupBroadcastBateComboBox().getSelectedItem()).equals(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_CLOSE)) ? 0 : 1);
		portInst.getLagInfo().setGroupBroadcastFlux(Integer.parseInt(portConfigPanel.getGroupBroadcastFluxText().getText()));
		portInst.getLagInfo().setFloodBate(((ControlKeyValue) portConfigPanel.getFloodBateComboBox().getSelectedItem()).equals(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_CLOSE)) ? 0 : 1);
		portInst.getLagInfo().setFloodFlux(Integer.parseInt(portConfigPanel.getFloodFluxText().getText()));
		portInst.getLagInfo().setType(0);
	}
	public void closeDailog() {
		this.dispose();
	}

	public void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		panel.add(component, gridBagConstraints);
	}

	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	/**
	 * 初始化控件
	 */
	private void initComponents() {

		mainPanel = new JPanel();
		jTabbedPane1 = new JTabbedPane();
		jPanel3 = new JPanel();
		jLabel2 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		jTextField1 = new JTextField();
		jLabel3 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ENABLED_STATUS));
		enableComBox = new JComboBox();
		jLabel4 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_TYPE));
		portTypeCBox = new JComboBox();
		jPanel2 = new JPanel();
		jButton1 = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		lblMac=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_ADDRESS));
		txtMac=new JTextField();

		jTextField1.setEditable(false);
		enableComBox.setModel(new DefaultComboBoxModel(new String[] {}));
		portTypeCBox.setModel(new DefaultComboBoxModel(new String[] {}));

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		mainPanel.setLayout(new GridBagLayout());
		addComponent(mainPanel, jTabbedPane1, 0, 0, 1.0, 1.0, 1, 1, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), GridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(mainPanel, jPanel2, 0, 1, 1.0, 0.5, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.NORTHWEST, gridBagConstraints);

		jTabbedPane1.addTab(ResourceUtil.srcStr(StringKeysTab.TAB_BASIC_ATTRIBUTE), jPanel3);

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 100,200,50,100,200 };
		layout.columnWeights = new double[] { 0,0,0,0,0 };
		layout.rowHeights = new int[] { 15,40,40,40 };
		layout.rowWeights = new double[] { 0,0,0, 0.2 };
		jPanel3.setLayout(layout);
		addComponent(jPanel3, jLabel2, 0, 1, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(jPanel3, jTextField1, 1, 1, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(jPanel3, jLabel3, 3, 1, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(jPanel3, enableComBox, 4, 1, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(jPanel3, jLabel4, 0, 2, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(jPanel3, portTypeCBox, 1, 2, 0,0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(jPanel3, this.lblMac, 3, 2, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(jPanel3, this.txtMac, 4, 2, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, gridBagConstraints);

		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		jPanel2.setLayout(flowLayout);
		jPanel2.add(jButton1);
		add(mainPanel);


	}// </editor-fold>

	// GEN-END:initComponents

	/**
	 * 保存时获取界面上已经填写的值
	 * 
	 * @return portattr对象
	 * @throws Exception
	 */
	private List<PortAttr> getPortAttrList() throws Exception {
		List<PortAttr> portAttrList = null;
		JScrollPane jScrollPane = null;
		JViewport jViewport = null;
		JPanel jPanel = null;
		String className = null;
		JTextField jTextField = null;
		JComboBox jComboBox = null;
		JCheckBox jCheckBox = null;
		JTextArea jTextArea = null;
		PortAttr portAttr = null;
		String checkValue = null;
		Component component = null;
		JTextField jTextFieldCheck = null;

		try {
			portAttrList = new ArrayList<PortAttr>();

			for (int i = 1; i < jTabbedPane1.getComponentCount(); i++) {
				jScrollPane = (JScrollPane) jTabbedPane1.getComponents()[i];
				jViewport = (JViewport) jScrollPane.getComponents()[0];
				jPanel = (JPanel) jViewport.getComponents()[0];

				for (int j = 0; j < jPanel.getComponentCount(); j++) {
					portAttr = null;
					className = jPanel.getComponents()[j].getClass().getSimpleName();
					if (className.equals("JTextField")) {
						jTextField = (JTextField) jPanel.getComponents()[j];

						portAttr = this.getPortAttr(jTextField.getName(), jTextField.getText(), null, null, null);

					} else if (className.equals("JComboBox")) {
						jComboBox = (JComboBox) jPanel.getComponents()[j];

						ControlKeyValue controlKeyValue = (ControlKeyValue) jComboBox.getSelectedItem();
						Code code = (Code) controlKeyValue.getObject();
						portAttr = this.getPortAttr(jComboBox.getName(), controlKeyValue.getId(), "true", null, code.getCodeValue());

					} else if (className.equals("JCheckBox")) {
						jCheckBox = (JCheckBox) jPanel.getComponents()[j];
						checkValue = "";
						if (j != jPanel.getComponentCount()) {
							component = jPanel.getComponents()[j + 1];
							if (component.getClass().getSimpleName().equals("JTextField")) {
								jTextFieldCheck = (JTextField) component;
								if (jTextFieldCheck.getName() == null) {
									checkValue = jTextFieldCheck.getText();
								}
							}
						}
						portAttr = this.getPortAttr(jCheckBox.getName(), checkValue, null, jCheckBox.isSelected() + "", null);

					} else if (className.equals("JScrollPane")) {
						jScrollPane = (JScrollPane) jPanel.getComponents()[j];
						jViewport = (JViewport) jScrollPane.getComponents()[0];
						jTextArea = (JTextArea) jViewport.getComponents()[0];

						portAttr = this.getPortAttr(jTextArea.getName(), jTextArea.getText(), null, null, null);
					}
					if (null != portAttr) {
						portAttrList.add(portAttr);
					}
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			jScrollPane = null;
			jViewport = null;
			jPanel = null;
			className = null;
			portAttr = null;
			checkValue = null;
			component = null;
			jTextFieldCheck = null;
		}
		return portAttrList;
	}

	private PortAttr getPortAttr(String attrId, String value, String iscode, String isCheck, String codeIdentity) {
		PortAttr portAttr = new PortAttr();
//		portAttr.setAttrId(Integer.parseInt(attrId));
//		portAttr.setValue(value);
//		portAttr.setIscode(iscode);
//		portAttr.setIsCheck(isCheck);
//		portAttr.setObjectId(this.portInst.getPortId());
//		portAttr.setCodeIdentity(codeIdentity);
		return portAttr;
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private JComboBox enableComBox;
	private JButton jButton1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JPanel jPanel2;
	private JPanel jPanel3;
	private JTabbedPane jTabbedPane1;
	private JTextField jTextField1;
	private JComboBox portTypeCBox;
	private JPanel mainPanel;
	private JLabel lblMac; 	//本地MAC地址
	private JTextField txtMac;
	// End of variables declaration//GEN-END:variables

}
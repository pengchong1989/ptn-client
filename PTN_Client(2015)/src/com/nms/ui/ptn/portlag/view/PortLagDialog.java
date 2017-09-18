package com.nms.ui.ptn.portlag.view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EActiveStatus;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.portlag.AbstractLagDialog;

public class PortLagDialog extends AbstractLagDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8747763750088698069L;
	private JLabel lblTitle;
	private JPanel titlePanel;
	private JButton portMember;// 端口成员按钮
	private JLabel lagTypeLabel;// 聚合模式
	private JComboBox lagtypeCB;
	private JButton relevanceInstall;// 关联设置按钮
	private JButton exportQueue;// 出口队列策略按钮
	
	private JLabel broadcastFluxLabel;// 广播包流量
	private JTextField broadcastFluxText;
	private JLabel groupBroadcastBateLabel;// 组播包抑制
	private JComboBox groupBroadcastBateComboBox;
	private JLabel groupBroadcastFluxLabel;// 组播包流量
	private JTextField groupBroadcastFluxText;
	
	private JLabel floodFluxLabel;// 洪泛包流量
	private JTextField floodFluxText;
	private JLabel portLimitationLabel;// 
	private JTextField portLimitationText;
	
	private PtnButton okButton;// 确定按钮
	private JButton cancelButton;// 取消按钮
	private JPanel buttonPanel;// 按钮面板
	private JPanel contentPanel;
	private String portMemberValue = "10-1,10-2,10-3,10-4";
	
	private JLabel portStatus;//非负载分担返回模式
	private JCheckBox portStatusComboBox;//
	private JLabel buffer;// 主聚合成员端口
	private JComboBox bufferComboBox;//
	private JLabel mtuLabel;// 等待恢复时间
	private PtnSpinner mtuText;//
	private JLabel floodBateLabel;// 从聚合成员端口
	private JComboBox floodBateComboBox;
	private JLabel broadcastBateLabel;// 聚合组工作模式
	private JComboBox broadcastBateComboBox;
	private PortLagInfo portLagInfo;
	private RelevanceInstallDialog relevanceInstallDialog;
	private PortMemberDialog portMemberDialog;
	private ExportQueueDialog exportQueueDialog;
	private boolean hasPortnumber = false;
	
	public PortLagDialog(JPanel panel) {
		this.setModal(true);
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PORT_AGGREGATION_CONFIGURATION));
		portLagInfo = new PortLagInfo();
		init(portLagInfo);
		initDialog(portLagInfo);
		addActionListener();
	}

	private void addActionListener() {
		portStatusComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(portStatusComboBox.isSelected()){
					bufferComboBox.setEnabled(true);
					floodBateComboBox.setEnabled(true);
					lagtypeCB.setEnabled(false);
				}else {
					bufferComboBox.setEnabled(false);
					floodBateComboBox.setEnabled(false);
					lagtypeCB.setEnabled(true);
				}
			}
		});
	}

	public PortLagDialog(PortLagPanel panel, PortLagInfo info) {
		hasPortnumber = true;
		portLagInfo = info;
		init(info);
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PORT_AGGREGATION_CONFIGURATION));
		initdata(info);
		initDialog(portLagInfo);
		addActionListener();
	}
	private void initDialog(PortLagInfo info){
		relevanceInstallDialog = new RelevanceInstallDialog(portLagInfo);
		portMemberDialog = new PortMemberDialog(portLagInfo);
		portMemberDialog.setSize(350, 300);
		portMemberDialog.setLocation(UiUtil.getWindowWidth(portMemberDialog.getWidth()), UiUtil.getWindowHeight(portMemberDialog.getHeight()));
		
		exportQueueDialog = new ExportQueueDialog("");
	}

	private void initdata(PortLagInfo info) {
//		super.getComboBoxDataUtil().comboBoxSelect(this.lagtypeCB, info.getLagMode() + "");
		this.comboBoxSelect(this.lagtypeCB, info.getLagMode());
		this.portStatusComboBox.setSelected(info.getPortEnable()==0?true:false);
		super.getComboBoxDataUtil().comboBoxSelect(this.bufferComboBox, info.getFlowControl() + "");
		this.mtuText.getTxt().setText(info.getMtu()+"");
		this.portLimitationText.setText(info.getPortLimitation() + "");
		super.getComboBoxDataUtil().comboBoxSelect(this.broadcastBateComboBox, info.getBroadcastBate() + "");
		this.broadcastFluxText.setText(info.getBroadcastFlux() + "");
		super.getComboBoxDataUtil().comboBoxSelect(this.groupBroadcastBateComboBox, info.getGroupBroadcastBate() + "");
		this.groupBroadcastFluxText.setText(info.getGroupBroadcastFlux() + "");
		super.getComboBoxDataUtil().comboBoxSelect(this.floodBateComboBox, info.getFloodBate() + "");
		this.floodFluxText.setText(info.getFloodFlux() + "");
		if(portStatusComboBox.isSelected()){
			bufferComboBox.setEnabled(true);
			floodBateComboBox.setEnabled(true);
			lagtypeCB.setEnabled(false);
		}else {
			bufferComboBox.setEnabled(false);
			floodBateComboBox.setEnabled(false);
			lagtypeCB.setEnabled(true);
		}
	}

	private void initPortData(JComboBox portSelcet,PortLagInfo lagInfo)  {
		PortService_MB portService = null;
		List<PortInst> portInsetList = null;
		PortInst portInst = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		AcPortInfoService_MB acInfoService = null;
		AcPortInfo acPortInfo = null;
		List<AcPortInfo> acPortInfoList;
		try {
			defaultComboBoxModel = new DefaultComboBoxModel();
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			portInst.setLagId(0);
			portInst.setPortType("UNI");
			portInsetList = portService.select(portInst);
			defaultComboBoxModel.addElement(new ControlKeyValue(portInst.getPortId() + "", portInst.getPortName(), portInst));
			if(lagInfo.getId()>0){
				String[] numbers = lagInfo.getPortLagMember().split(",");
				for (String number : numbers) {
					if(Integer.parseInt(number)>0){
						portInst = new PortInst();
						portInst.setSiteId(ConstantUtil.siteId);
						portInst.setNumber(Integer.parseInt(number));
						portInst.setLagId(lagInfo.getId());
						portInst = portService.select(portInst).get(0);
						defaultComboBoxModel.addElement(new ControlKeyValue(portInst.getPortId() + "", portInst.getPortName(), portInst));
					}
				}
			}
			for (int i = 0; i < portInsetList.size(); i++) {
				portInst = portInsetList.get(i);
				if(portInst.getIsEnabled_code() ==1 && portInst.getLagId()==0){
					acPortInfo = new AcPortInfo();
					acPortInfo.setSiteId(portInst.getSiteId());
					acPortInfo.setPortId(portInst.getPortId());
					acPortInfoList = acInfoService.selectByCondition(acPortInfo);
					if(acPortInfoList == null || acPortInfoList.size()==0 ){
						defaultComboBoxModel.addElement(new ControlKeyValue(portInst.getPortId() + "", portInst.getPortName(), portInst));
					}
					
				}			
			}
			portSelcet.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(acInfoService);
		}

	}
	
	public boolean checkIsUser(PortLagInfo info){
		AcPortInfoService_MB acInfoService = null;
		try {
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			AcPortInfo acPortInfo = new AcPortInfo();
			acPortInfo.setLagId(info.getId());
			acPortInfo.setSiteId(info.getSiteId());
			List<AcPortInfo> acPortInfos = acInfoService.selectByCondition(acPortInfo);
			if( acPortInfos != null && acPortInfos.size()>0){
				return true;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(acInfoService);
		}
		
		return false;
		
	}
	private void init(PortLagInfo info) {
		initComponents(info);
		setLayout();

	}

	/**
	 * 初始化界面控件
	 */
	private void initComponents(PortLagInfo info) {
		try {
			lblTitle = new JLabel(ResourceUtil.srcStr(StringKeysTitle.TIT_PORT_AGGREGATION_CONFIGURATION));
			titlePanel = new JPanel();
			titlePanel.setBorder(BorderFactory.createEtchedBorder());
			titlePanel.setSize(60, ConstantUtil.INT_WIDTH_THREE);
			lagTypeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_AGGREGATION_SCHEME));
			lagtypeCB = new JComboBox();
			Map<Integer, String> map = new LinkedHashMap<Integer, String>();
			map.put(0, ResourceUtil.srcStr(StringKeysObj.LSP_TYPE_NO));
			map.put(1, ResourceUtil.srcStr(StringKeysLbl.LBL_ACCORDING_SOURCE_MACADDRESS));
			map.put(2, ResourceUtil.srcStr(StringKeysLbl.LBL_ACCORDING_TARGET_MACADDRESS));
			map.put(3, ResourceUtil.srcStr(StringKeysLbl.LBL_ACCORDING_SOURCE_TARGET_MACADDRESS));
			map.put(4, ResourceUtil.srcStr(StringKeysLbl.LBL_ACCORDING_SOURCE_IP));
			map.put(5, ResourceUtil.srcStr(StringKeysLbl.LBL_ACCORDING_TARGET_IP));
			map.put(6, ResourceUtil.srcStr(StringKeysLbl.LBL_ACCORDING_SOURCE_TARGET_IP));
			
			setModel(lagtypeCB, map);
			lagtypeCB.setEnabled(false);
			portStatus = new JLabel(ResourceUtil.srcStr(StringKeysLbl.FUZAI_MODEL));
			portStatusComboBox = new JCheckBox();
			portStatusComboBox.setSelected(true);
			portMember = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_MEMBER));
			buffer = new JLabel(ResourceUtil.srcStr(StringKeysLbl.MAIN_LAG));
			bufferComboBox = new JComboBox();
			initPortData(bufferComboBox,info);
			mtuLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
			mtuText = new PtnSpinner(PtnSpinner.TYPE_WAITTIME);
			mtuText.getTxt().setText("5");
			relevanceInstall = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ASSOCIATED_SETTINGS));
			exportQueue = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_LAG_PORT));
			portLimitationLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_EXPORT_SPEED_LIMIT));
			portLimitationText = new JTextField("10000");
			broadcastBateLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.WORK_MODEL));
			broadcastBateComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(broadcastBateComboBox, "WORKMODEL");
			broadcastFluxLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BROADCAST_FLOW));
			broadcastFluxText = new JTextField("1000000");
			groupBroadcastBateLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MULTICAST_RESTRAIN));
			groupBroadcastBateComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(groupBroadcastBateComboBox, "VCTRAFFICPOLICING");
			groupBroadcastFluxLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MULTICAST_FLOW));
			groupBroadcastFluxText = new JTextField("1000000");
			floodBateLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.STAND_LAG));
			floodBateComboBox = new JComboBox();
			initPortData(floodBateComboBox,info);
			floodFluxLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FLOODED_FLOW));
			floodFluxText = new JTextField("1000000");
			buttonPanel = new JPanel();
			okButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);
			cancelButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			contentPanel = new JPanel();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 界面布局
	 */
	private void setLayout() {
		// title面板布局
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		// 主面板布局
		layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 100, 50};
		layout.columnWeights = new double[] { 0, 0, 0, 0 };
		layout.rowHeights = new int[] { 30, 30, 30, 30, 30, 30, 30, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		// content面板布局
		contentPanel.setLayout(layout);
		addComponent(contentPanel, lagTypeLabel, 0, 1, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(20, 80, 10, 5), GridBagConstraints.CENTER, c);
		addComponent(contentPanel, lagtypeCB, 1, 1, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(20, 10, 10, 20), GridBagConstraints.CENTER, c);

		addComponent(contentPanel, portStatus, 0, 2, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 80, 10, 5), GridBagConstraints.CENTER, c);
		addComponent(contentPanel, portStatusComboBox, 1, 2, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 10, 10, 20), GridBagConstraints.CENTER, c);

		addComponent(contentPanel, buffer, 0, 3, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 80, 10, 5), GridBagConstraints.CENTER, c);
		addComponent(contentPanel, bufferComboBox, 1, 3, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 10, 10, 20), GridBagConstraints.CENTER, c);

		addComponent(contentPanel, mtuLabel, 0, 5, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 80, 10, 5), GridBagConstraints.CENTER, c);
		addComponent(contentPanel, mtuText, 1, 5, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 10, 10, 20), GridBagConstraints.CENTER, c);

		addComponent(contentPanel, floodBateLabel, 0, 4, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 80, 10, 5), GridBagConstraints.CENTER, c);
		addComponent(contentPanel, floodBateComboBox, 1, 4, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 10, 10, 20), GridBagConstraints.CENTER, c);

		addComponent(contentPanel, broadcastBateLabel, 0, 6, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 80, 10, 5), GridBagConstraints.CENTER, c);
		addComponent(contentPanel, broadcastBateComboBox, 1, 6, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(10, 10, 10, 20), GridBagConstraints.CENTER, c);
		addComponent(contentPanel, relevanceInstall, 0, 7, 0.2, 0, 1, 1, GridBagConstraints.BOTH, new Insets(15, 80, 5, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, exportQueue, 1, 7, 2, 0, 1, 1, GridBagConstraints.BOTH, new Insets(15, 10, 5, 20), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, portMember, 0, 8, 0.2, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 80, 5, 5), GridBagConstraints.NORTHWEST, c);

		addComponent(contentPanel, buttonPanel, 0, 9, 1.0, 0.1, 4, 1, GridBagConstraints.BOTH, new Insets(5, 5, 10, 100), GridBagConstraints.NORTHWEST, c);

		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		this.add(contentPanel);
	}

	// 收集页面数据
	public PortLagInfo get(PortLagInfo portLagInfo) {
         //Integer.parseInt(this.getLagtypeCB().getSelectedItem())
		portLagInfo.setLagMode(this.getLagtypeCB().getSelectedIndex());
		portLagInfo.setPortEnable(portStatusComboBox.isSelected()?0:1);
		PortInst mainportInst = (PortInst) ((ControlKeyValue)this.getBufferComboBox().getSelectedItem()).getObject();
		portLagInfo.setFlowControl(mainportInst.getPortId());
		portLagInfo.setMainLagPortLog(mainportInst.getPortName());
		portLagInfo.setMtu(Integer.parseInt(this.getMtuText().getTxt().getText()));
		portLagInfo.setPortLimitation(Integer.parseInt(this.getPortLimitationText().getText()));
		portLagInfo.setBroadcastFlux(Integer.parseInt(this.getBroadcastFluxText().getText()));
		portLagInfo.setGroupBroadcastBate(Integer.parseInt(((ControlKeyValue) (this.getGroupBroadcastBateComboBox().getSelectedItem())).getId()));
		portLagInfo.setGroupBroadcastFlux(Integer.parseInt(this.getGroupBroadcastFluxText().getText()));
		PortInst standportInst = (PortInst) ((ControlKeyValue)this.getFloodBateComboBox().getSelectedItem()).getObject();
		portLagInfo.setFloodBate(standportInst.getPortId());
		portLagInfo.setStandLagPortLog(standportInst.getPortName());
		portLagInfo.setFloodFlux(Integer.parseInt(this.getFloodFluxText().getText()));
		portLagInfo.setSiteId(ConstantUtil.siteId);
		portLagInfo.setType(1);
		portLagInfo.setLagStatus(EActiveStatus.ACTIVITY.getValue());
		portLagInfo.setBroadcastBate(((Code)((ControlKeyValue)this.broadcastBateComboBox.getSelectedItem()).getObject()).getId());
		return portLagInfo;

	}


	/**
	 * 判断lagId是否被使用
	 * 
	 * @param a
	 * @param portLagInfoList
	 * @return
	 */
	public boolean containId(int a, List<PortLagInfo> portLagInfoList) {
		for (PortLagInfo portLagInfo : portLagInfoList) {
			if (a == portLagInfo.getLagID()) {
				return false;
			}
		}
		return true;
	}

	private void setModel(JComboBox comboBox, Map<Integer, String> keyValues) {
		DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox.getModel();

		for (Integer key : keyValues.keySet()) {
			comboBoxModel.addElement(new ControlKeyValue(key.toString(), keyValues.get(key)));
		}
	}

	//聚合模式下拉列表赋值
	private void comboBoxSelect(JComboBox comboBox,int selectId){
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			if(i == selectId){
				comboBox.setSelectedIndex(i);
			  return;
			}
		}
		
	}
	
	
	
	public JLabel getLblTitle() {
		return lblTitle;
	}

	public void setLblTitle(JLabel lblTitle) {
		this.lblTitle = lblTitle;
	}

	public JPanel getTitlePanel() {
		return titlePanel;
	}

	public void setTitlePanel(JPanel titlePanel) {
		this.titlePanel = titlePanel;
	}

	public JButton getPortMember() {
		return portMember;
	}

	public void setPortMember(JButton portMember) {
		this.portMember = portMember;
	}

	public JLabel getLagTypeLabel() {
		return lagTypeLabel;
	}

	public void setLagTypeLabel(JLabel lagTypeLabel) {
		this.lagTypeLabel = lagTypeLabel;
	}

	public JComboBox getLagtypeCB() {
		return lagtypeCB;
	}

	public void setLagtypeCB(JComboBox lagtypeCB) {
		this.lagtypeCB = lagtypeCB;
	}

	public JLabel getPortStatus() {
		return portStatus;
	}

	public void setPortStatus(JLabel portStatus) {
		this.portStatus = portStatus;
	}


	public JCheckBox getPortStatusComboBox() {
		return portStatusComboBox;
	}

	public void setPortStatusComboBox(JCheckBox portStatusComboBox) {
		this.portStatusComboBox = portStatusComboBox;
	}

	public JLabel getBuffer() {
		return buffer;
	}

	public void setBuffer(JLabel buffer) {
		this.buffer = buffer;
	}

	public JComboBox getBufferComboBox() {
		return bufferComboBox;
	}

	public void setBufferComboBox(JComboBox bufferComboBox) {
		this.bufferComboBox = bufferComboBox;
	}

	public JLabel getMtuLabel() {
		return mtuLabel;
	}

	public void setMtuLabel(JLabel mtuLabel) {
		this.mtuLabel = mtuLabel;
	}

	public JButton getRelevanceInstall() {
		return relevanceInstall;
	}

	public void setRelevanceInstall(JButton relevanceInstall) {
		this.relevanceInstall = relevanceInstall;
	}

	public JButton getExportQueue() {
		return exportQueue;
	}

	public void setExportQueue(JButton exportQueue) {
		this.exportQueue = exportQueue;
	}

	public JLabel getPortLimitationLabel() {
		return portLimitationLabel;
	}

	public void setPortLimitationLabel(JLabel portLimitationLabel) {
		this.portLimitationLabel = portLimitationLabel;
	}

	public JTextField getPortLimitationText() {
		return portLimitationText;
	}

	public void setPortLimitationText(JTextField portLimitationText) {
		this.portLimitationText = portLimitationText;
	}

	public JLabel getBroadcastBateLabel() {
		return broadcastBateLabel;
	}

	public void setBroadcastBateLabel(JLabel broadcastBateLabel) {
		this.broadcastBateLabel = broadcastBateLabel;
	}

	public JComboBox getBroadcastBateComboBox() {
		return broadcastBateComboBox;
	}

	public void setBroadcastBateComboBox(JComboBox broadcastBateComboBox) {
		this.broadcastBateComboBox = broadcastBateComboBox;
	}

	public JLabel getBroadcastFluxLabel() {
		return broadcastFluxLabel;
	}

	public void setBroadcastFluxLabel(JLabel broadcastFluxLabel) {
		this.broadcastFluxLabel = broadcastFluxLabel;
	}

	public JTextField getBroadcastFluxText() {
		return broadcastFluxText;
	}

	public void setBroadcastFluxText(JTextField broadcastFluxText) {
		this.broadcastFluxText = broadcastFluxText;
	}

	public JLabel getGroupBroadcastBateLabel() {
		return groupBroadcastBateLabel;
	}

	public void setGroupBroadcastBateLabel(JLabel groupBroadcastBateLabel) {
		this.groupBroadcastBateLabel = groupBroadcastBateLabel;
	}

	public JComboBox getGroupBroadcastBateComboBox() {
		return groupBroadcastBateComboBox;
	}

	public void setGroupBroadcastBateComboBox(JComboBox groupBroadcastBateComboBox) {
		this.groupBroadcastBateComboBox = groupBroadcastBateComboBox;
	}

	public JLabel getGroupBroadcastFluxLabel() {
		return groupBroadcastFluxLabel;
	}

	public void setGroupBroadcastFluxLabel(JLabel groupBroadcastFluxLabel) {
		this.groupBroadcastFluxLabel = groupBroadcastFluxLabel;
	}

	public JTextField getGroupBroadcastFluxText() {
		return groupBroadcastFluxText;
	}

	public void setGroupBroadcastFluxText(JTextField groupBroadcastFluxText) {
		this.groupBroadcastFluxText = groupBroadcastFluxText;
	}

	public JLabel getFloodBateLabel() {
		return floodBateLabel;
	}

	public void setFloodBateLabel(JLabel floodBateLabel) {
		this.floodBateLabel = floodBateLabel;
	}

	public JComboBox getFloodBateComboBox() {
		return floodBateComboBox;
	}

	public void setFloodBateComboBox(JComboBox floodBateComboBox) {
		this.floodBateComboBox = floodBateComboBox;
	}

	public JLabel getFloodFluxLabel() {
		return floodFluxLabel;
	}

	public void setFloodFluxLabel(JLabel floodFluxLabel) {
		this.floodFluxLabel = floodFluxLabel;
	}

	public JTextField getFloodFluxText() {
		return floodFluxText;
	}

	public void setFloodFluxText(JTextField floodFluxText) {
		this.floodFluxText = floodFluxText;
	}

	public PtnButton getOkButton() {
		return okButton;
	}

	public void setOkButton(PtnButton okButton) {
		this.okButton = okButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(JButton cancelButton) {
		this.cancelButton = cancelButton;
	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	public void setButtonPanel(JPanel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public void setContentPanel(JPanel contentPanel) {
		this.contentPanel = contentPanel;
	}

	public String getPortMemberValue() {
		return portMemberValue;
	}

	public void setPortMemberValue(String portMemberValue) {
		this.portMemberValue = portMemberValue;
	}

	@Override
	public void initData(PortLagInfo portLagInfo) {

	}

	@Override
	public PortLagInfo get() {
		 
		return null;
	}

	public PtnSpinner getMtuText() {
		return mtuText;
	}

	public void setMtuText(PtnSpinner mtuText) {
		this.mtuText = mtuText;
	}

	public RelevanceInstallDialog getRelevanceInstallDialog() {
		return relevanceInstallDialog;
	}

	public void setRelevanceInstallDialog(RelevanceInstallDialog relevanceInstallDialog) {
		this.relevanceInstallDialog = relevanceInstallDialog;
	}

	public PortMemberDialog getPortMemberDialog() {
		return portMemberDialog;
	}

	public void setPortMemberDialog(PortMemberDialog portMemberDialog) {
		this.portMemberDialog = portMemberDialog;
	}

	public ExportQueueDialog getExportQueueDialog() {
		return exportQueueDialog;
	}

	public void setExportQueueDialog(ExportQueueDialog exportQueueDialog) {
		this.exportQueueDialog = exportQueueDialog;
	}

	public boolean isHasPortnumber() {
		return hasPortnumber;
	}

	public void setHasPortnumber(boolean hasPortnumber) {
		this.hasPortnumber = hasPortnumber;
	}
	
}

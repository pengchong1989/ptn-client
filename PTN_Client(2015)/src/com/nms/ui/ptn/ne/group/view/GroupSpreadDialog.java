package com.nms.ui.ptn.ne.group.view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.ptn.path.GroupSpreadInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;

public class GroupSpreadDialog extends PtnDialog{

	private static final long serialVersionUID = 45103483210415841L;
	private JLabel smId;
	private JTextField smIdText;
	private JLabel vpls_vs;
	private JComboBox vplsVsComboBox;
	private JButton portChoiceButton;
	private JLabel macAddress;
	private JTextField macAddText;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel contentPanel;
	private JPanel buttonPanel;
	
	public GroupSpreadDialog(JPanel panel){
		this.setModal(true);
		init();
		initVpls();
	}
	
	public GroupSpreadDialog(GroupSpreadPanel panel, GroupSpreadInfo info) {
		init();
		initdata(info);
		initVpls();
		this.vplsVsComboBox.setEnabled(false);
		this.smIdText.setEnabled(false);
	}
	
	private void initdata(GroupSpreadInfo info) {
		
	}

	public void initVpls(){
		EtreeInfoService_MB etreeService = null;
		ElanInfoService_MB elanInfoService = null;
		Map<String, List<EtreeInfo>> ServiceIdAndEtreeListMap = null;
		Map<String, List<ElanInfo>> ServiceIdAndElanListMap = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		List<EtreeInfo> etreeInfos = null;
		ElanInfo elanInfo = null;
		try {
			etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			ServiceIdAndEtreeListMap = etreeService.selectNodeBySite(ConstantUtil.siteId);
			ServiceIdAndElanListMap = elanInfoService.selectSiteNodeBy(ConstantUtil.siteId);
			defaultComboBoxModel = new DefaultComboBoxModel();
			for(String str : ServiceIdAndEtreeListMap.keySet()){
				etreeInfos = ServiceIdAndEtreeListMap.get(str);
				List<ElanInfo> elanInfos = new ArrayList<ElanInfo>();
				for(EtreeInfo etreeInfo : etreeInfos){
					elanInfo = new ElanInfo();
					elanInfo.setId(etreeInfo.getId());
					if(etreeInfo.getRootSite() == ConstantUtil.siteId){
						elanInfo.setAxcId(etreeInfo.getaXcId());
						elanInfo.setZxcId(etreeInfo.getzXcId());
						elanInfo.setActiveStatus(etreeInfo.getActiveStatus());
						elanInfo.setaAcId(etreeInfo.getaAcId());
						elanInfo.setzAcId(etreeInfo.getzAcId());
						elanInfo.setaSiteId(etreeInfo.getRootSite());
						elanInfo.setzSiteId(etreeInfo.getBranchSite());
					}else{
						elanInfo.setAxcId(etreeInfo.getzXcId());
						elanInfo.setZxcId(etreeInfo.getaXcId());
						elanInfo.setActiveStatus(etreeInfo.getActiveStatus());
						elanInfo.setaAcId(etreeInfo.getzAcId());
						elanInfo.setzAcId(etreeInfo.getaAcId());
						elanInfo.setaSiteId(etreeInfo.getBranchSite());
						elanInfo.setzSiteId(etreeInfo.getRootSite());
					}
					
					elanInfo.setServiceId(etreeInfo.getServiceId());
					elanInfo.setPwId(etreeInfo.getPwId());
					elanInfo.setServiceType(etreeInfo.getServiceType());
					elanInfo.setName(etreeInfo.getName());
					
					elanInfo.setIsSingle(etreeInfo.getIsSingle());
					elanInfo.setJobStatus(etreeInfo.getJobStatus());
					elanInfos.add(elanInfo);
				}
				ServiceIdAndElanListMap.put(elanInfos.get(0).getServiceId()+"/"+elanInfos.get(0).getServiceType(), elanInfos);
			}
			
			for(String str : ServiceIdAndElanListMap.keySet()){
				List<ElanInfo> elanInfoList = ServiceIdAndElanListMap.get(str);
				for(ElanInfo info : elanInfoList){
					if (info.getaSiteId() == ConstantUtil.siteId) {
						defaultComboBoxModel.addElement(new ControlKeyValue(info.getName() + "", info.getName(), info));
						break;
					}else{
						defaultComboBoxModel.addElement(new ControlKeyValue(info.getName() + "", info.getName(), info));
						break;
					}
				}
			}
			this.vplsVsComboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(elanInfoService);
			UiUtil.closeService_MB(etreeService);
		}
	}
	private void init() {
		initComponents();
		setLayout();
	}
	/**
	 * 初始化界面控件
	 */
	private void initComponents() {
		smId = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SM_ID));
		smIdText = new JTextField("1");
		vpls_vs = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VPLS_SELECT));
		vplsVsComboBox = new JComboBox();
		portChoiceButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_PORT_SELECT));
		macAddress = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_ADDRESS));
		macAddText = new JTextField("00-00-00-00-00-00");
		okButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		cancelButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		contentPanel = new JPanel();
		buttonPanel = new JPanel();
	}
	/**
	 * 界面布局
	 */
	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {80, 20};
		layout.columnWeights = new double[] {0, 0};
		layout.rowHeights = new int[] {30, 30, 30, 30, 30};
		layout.rowWeights = new double[] {0, 0, 0, 0, 0};
		
		GridBagConstraints c = new GridBagConstraints();
		contentPanel.setLayout(layout);
		
		addComponent(contentPanel, smId, 0, 1, 1.0, 0.001, 1, 1, 
				GridBagConstraints.BOTH, new Insets(20, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, smIdText, 1, 1, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(20, 10, 10, 20), GridBagConstraints.NORTHWEST, c);
		 
		addComponent(contentPanel, vpls_vs, 0, 2, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, vplsVsComboBox, 1, 2, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 10, 10, 20), GridBagConstraints.NORTHWEST, c);
		
		addComponent(contentPanel, macAddress, 0, 3, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, macAddText, 1, 3, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 10, 10, 20), GridBagConstraints.NORTHWEST, c);
		
		addComponent(contentPanel, portChoiceButton, 0, 4, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		
		addComponent(contentPanel, buttonPanel, 1, 5, 1.0, 0.1, 4, 1, 
				GridBagConstraints.BOTH, new Insets(15, 5, 10, 50), GridBagConstraints.NORTHWEST, c);
		
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		this.add(contentPanel);
	}
	
	
	//收集页面数据
	public GroupSpreadInfo get(GroupSpreadInfo groupInfo) {

		groupInfo.setSmId(Integer.parseInt(this.getSmIdText().getText()));
		ElanInfo elanInfo =(ElanInfo)((ControlKeyValue) this.getVplsVsComboBox().getSelectedItem()).getObject();
		if(elanInfo.getaSiteId() == ConstantUtil.siteId){
			groupInfo.setVpls_vs(elanInfo.getAxcId()); 
		}else{
			groupInfo.setVpls_vs(elanInfo.getZxcId()); 
		}
		groupInfo.setMacAddress(this.getMacAddText().getText());
		groupInfo.setSiteId(ConstantUtil.siteId);
		return groupInfo;

	}

	/**
	 * 判断是否选择了VPLS
	 * @return
	 */
	public boolean vplsExist(){
		if((ControlKeyValue) (this.getVplsVsComboBox().getSelectedItem()) == null){
			return false;
		}
		return true;
	}
	
	public JLabel getSmId() {
		return smId;
	}

	public void setSmId(JLabel smId) {
		this.smId = smId;
	}

	public JTextField getSmIdText() {
		return smIdText;
	}

	public void setSmIdText(JTextField smIdText) {
		this.smIdText = smIdText;
	}

	public JLabel getVpls_vs() {
		return vpls_vs;
	}

	public void setVpls_vs(JLabel vplsVs) {
		vpls_vs = vplsVs;
	}

	public JComboBox getVplsVsComboBox() {
		return vplsVsComboBox;
	}

	public void setVplsVsComboBox(JComboBox vplsVsComboBox) {
		this.vplsVsComboBox = vplsVsComboBox;
	}

	public JButton getPortChoiceButton() {
		return portChoiceButton;
	}

	public void setPortChoiceButton(JButton portChoiceButton) {
		this.portChoiceButton = portChoiceButton;
	}

	public JLabel getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(JLabel macAddress) {
		this.macAddress = macAddress;
	}

	public JTextField getMacAddText() {
		return macAddText;
	}

	public void setMacAddText(JTextField macAddText) {
		this.macAddText = macAddText;
	}

	public JButton getOkButton() {
		return okButton;
	}

	public void setOkButton(JButton okButton) {
		this.okButton = okButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(JButton cancelButton) {
		this.cancelButton = cancelButton;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public void setContentPanel(JPanel contentPanel) {
		this.contentPanel = contentPanel;
	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	public void setButtonPanel(JPanel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}
	
}

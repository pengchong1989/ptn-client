package com.nms.ui.ptn.portlag.view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.model.equipment.port.PortService_MB;
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
import com.nms.ui.manager.keys.StringKeysTitle;

public class PortMemberDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2675607030957472979L;

	private JLabel portNumberLabel1;// 端口号1
	private JComboBox portNumeberComboBox1;
	private JLabel portNumberLabel2;// 端口号2
	private JComboBox portNumeberComboBox2;
	private JLabel portNumberLabel3;// 端口号3
	private JComboBox portNumeberComboBox3;
	private JLabel portNumberLabel4;// 端口号4
	private JComboBox portNumeberComboBox4;
	private JButton confirm;
	private JButton cancel;
	private JPanel buttonPanel;
	private JPanel contentPanel;
	private PortLagInfo lagInfo;
	Map<Integer, String> map = new LinkedHashMap<Integer, String>();
	
	public PortMemberDialog(PortLagInfo portLagInfo) {
		this.setModal(true);
		lagInfo = portLagInfo;
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PORT_SELECT));
		// 初始化控件
		initComponent();
		// 界面布局
		setQLDialogLayout();	
		init(lagInfo);
	}

	private void initComponent() {

		portNumberLabel1 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM)+"1");
		portNumberLabel2 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM)+"2");
		portNumberLabel3 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM)+"3");
		portNumberLabel4 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM)+"4");
		
		try {
//			getPortNumber(map);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		portNumeberComboBox1 = new JComboBox();
//		setModel(portNumeberComboBox1, map);
		initPortData(portNumeberComboBox1);
		portNumeberComboBox2 = new JComboBox();
//		setModel(portNumeberComboBox2, map);
		initPortData(portNumeberComboBox2);
		portNumeberComboBox3 = new JComboBox();
		initPortData(portNumeberComboBox3);
//		setModel(portNumeberComboBox3, map);
		portNumeberComboBox4 = new JComboBox();
//		setModel(portNumeberComboBox4, map);
		initPortData(portNumeberComboBox4);
		buttonPanel = new JPanel();
		contentPanel = new JPanel();
		confirm = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));

	}

	private void initPortData(JComboBox portSelcet)  {
		
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

	private void setModel(JComboBox comboBox, Map<Integer, String> keyValues) {
		DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox.getModel();

		for (Integer key : keyValues.keySet()) {
			comboBoxModel.addElement(new ControlKeyValue(key.toString(), keyValues.get(key)));
		}
	}

	private void setQLDialogLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 80, 20 };
		layout.columnWeights = new double[] { 0, 0 };
		layout.rowHeights = new int[] { 30, 30, 30, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0 };

		GridBagConstraints c = new GridBagConstraints();
		contentPanel.setLayout(layout);

		addComponent(contentPanel, portNumberLabel1, 0, 1, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(20, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, portNumeberComboBox1, 1, 1, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(20, 10, 10, 20), GridBagConstraints.NORTHWEST, c);

		addComponent(contentPanel, portNumberLabel2, 0, 2, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, portNumeberComboBox2, 1, 2, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(15, 10, 10, 20), GridBagConstraints.NORTHWEST, c);

		addComponent(contentPanel, portNumberLabel3, 0, 3, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, portNumeberComboBox3, 1, 3, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(15, 10, 10, 20), GridBagConstraints.NORTHWEST, c);

		addComponent(contentPanel, portNumberLabel4, 0, 4, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, portNumeberComboBox4, 1, 4, 1.0, 0.001, 1, 1, GridBagConstraints.BOTH, new Insets(15, 10, 10, 20), GridBagConstraints.NORTHWEST, c);

		addComponent(contentPanel, buttonPanel, 1, 6, 1.0, 0.1, 4, 1, GridBagConstraints.BOTH, new Insets(15, 5, 10, 80), GridBagConstraints.NORTHWEST, c);

		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(confirm);
		buttonPanel.add(cancel);

		this.add(contentPanel);

	}

	/**
	 * 初始化界面的值
	 * 
	 * @param values
	 */
	public void init(PortLagInfo portLagInfo) {
		PortInst portInst = null;
		PortService_MB portService = null;
		List<PortInst> portInsts = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInsts = new ArrayList<PortInst>();
			if (portLagInfo != null ) {
				String[] qlValues = portLagInfo.getPortLagMember().split(",");
				for (int i = 0; i < qlValues.length; i++) {
					String number = qlValues[i];
					portInst = new PortInst();
					if(Integer.parseInt(number)>0){
						portInst.setSiteId(ConstantUtil.siteId);
						portInst.setNumber(Integer.parseInt(number));
						portInst.setLagId(lagInfo.getId());
						portInst = portService.select(portInst).get(0);
					}
					portInsts.add(portInst);
				}
				comboBoxSelect(portNumeberComboBox1, portInsts.get(0).getPortId()+"");
				comboBoxSelect(portNumeberComboBox2, portInsts.get(1).getPortId()+"");
				comboBoxSelect(portNumeberComboBox3, portInsts.get(2).getPortId()+"");
				comboBoxSelect(portNumeberComboBox4, portInsts.get(3).getPortId()+"");
			} else {
				setDefalutInit();
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
		}
	}

	public void comboBoxSelect(JComboBox jComboBox, String selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			if (((ControlKeyValue) jComboBox.getItemAt(i)).getId().equals(selectId)) {
				jComboBox.setSelectedIndex(i);
				return;
			}

		}
	}
	/**
	 * 获取界面数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public String get() {
		StringBuffer sb = null;
		try {
			sb = new StringBuffer();
			PortInst portInst1 = null;
			portInst1 = (PortInst) ((ControlKeyValue) (portNumeberComboBox1.getSelectedItem())).getObject();
			PortInst portInst2 = (PortInst) ((ControlKeyValue) (portNumeberComboBox2.getSelectedItem())).getObject();
			PortInst portInst3 = (PortInst) ((ControlKeyValue) (portNumeberComboBox3.getSelectedItem())).getObject();
			PortInst portInst4 = (PortInst) ((ControlKeyValue) (portNumeberComboBox4.getSelectedItem())).getObject();
			sb.append(portInst1.getNumber()+","+portInst2.getNumber()+","+portInst3.getNumber()+","+portInst4.getNumber());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return sb.toString();
	}
	
	public String getPortName() {
		StringBuffer sb = null;
		try {
			sb = new StringBuffer();
			PortInst portInst1 = null;
			portInst1 = (PortInst) ((ControlKeyValue) (portNumeberComboBox1.getSelectedItem())).getObject();
			PortInst portInst2 = (PortInst) ((ControlKeyValue) (portNumeberComboBox2.getSelectedItem())).getObject();
			PortInst portInst3 = (PortInst) ((ControlKeyValue) (portNumeberComboBox3.getSelectedItem())).getObject();
			PortInst portInst4 = (PortInst) ((ControlKeyValue) (portNumeberComboBox4.getSelectedItem())).getObject();
			sb.append(portInst1.getPortName()+","+portInst2.getPortName()+","+portInst3.getPortName()+","+portInst4.getPortName());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return sb.toString();
	}

	public boolean checkPortMember() {
		int member1 = Integer.parseInt(((ControlKeyValue) (portNumeberComboBox1.getSelectedItem())).getId());
		int member2 = Integer.parseInt(((ControlKeyValue) (portNumeberComboBox2.getSelectedItem())).getId());
		int member3 = Integer.parseInt(((ControlKeyValue) (portNumeberComboBox3.getSelectedItem())).getId());
		int member4 = Integer.parseInt(((ControlKeyValue) (portNumeberComboBox4.getSelectedItem())).getId());
		
		if (member1 == member2 && member1!=0 ) {
			return false;
		}
		if (member1 == member3 && member3!=0) {
			return false;
		}
		if (member1 == member4 && member4!=0) {
			return false;
		}
		if (member2 == member3 && member2!=0) {
			return false;
		}
		if (member2 == member4 && member2!=0) {
			return false;
		}
		if (member3 == member4 && member3!=0) {
			return false;
		}
		if(member1 == member2 && member1 == member3 && member1 == member4){
			return false;
		}
		
		return true;
	}

	/**
	 * 验证四个端口是否是相同速率的端口
	 * @return
	 */
	public boolean checkPortSameSpeed() {
		String name1 = ((ControlKeyValue) (portNumeberComboBox1.getSelectedItem())).getName();
		if(name1 != null){
			name1= name1.split("\\.")[0];
		}
		String name2 = ((ControlKeyValue) (portNumeberComboBox2.getSelectedItem())).getName();
		if(name2 != null){
			name2= name2.split("\\.")[0];
		}
		String name3 = ((ControlKeyValue) (portNumeberComboBox3.getSelectedItem())).getName();
		if(name3 != null){
			name3= name3.split("\\.")[0];
		}
		String name4 = ((ControlKeyValue) (portNumeberComboBox4.getSelectedItem())).getName();
		if(name4 != null){
			name4= name4.split("\\.")[0];
		}
		
		/*添加验证，四个端口必须是同速率的，否则返回false*/
		if(name1 != null && name2 != null){
			if(!name1.equals(name2)){
				return false;
			}
		}
		if(name1 != null && name3 != null){
			if(!name1.equals(name3)){
				return false;
			}
		}
		if(name1 != null && name4 != null){
			if(!name1.equals(name4)){
				return false;
			}
		}
		if(name2 != null && name3 != null){
			if(!name2.equals(name3)){
				return false;
			}
		}
		if(name2 != null && name4 != null){
			if(!name2.equals(name4)){
				return false;
			}
		}
		if(name3 != null && name4 != null){
			if(!name3.equals(name4)){
				return false;
			}
		}
		return true;
	}
	
	private void setDefalutInit() {
		portNumeberComboBox1.setSelectedIndex(0);
		portNumeberComboBox2.setSelectedIndex(0);
		portNumeberComboBox3.setSelectedIndex(0);
		portNumeberComboBox4.setSelectedIndex(0);

	}

	public JButton getConfirm() {
		return confirm;
	}

	public void setConfirm(JButton confirm) {
		this.confirm = confirm;
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	public void setButtonPanel(JPanel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}

}

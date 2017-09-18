package com.nms.ui.ptn.ne.ethernetLoop.view;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.EthLoopInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.ptn.ne.ethernetLoop.controller.EthLoopController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class EthernetLoopPanel extends PtnPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2355117305247582833L;
	
	private JLabel loopLabel;//环回使能
	private JComboBox loopComboBox;
	private JLabel portLabel;//端口号
	private JComboBox portComboBox;
	private JLabel loopRuleLabel;//环回规则
	private JComboBox loopRuleComboBox;
	private JLabel macLabel;//MAC
	private PtnTextField macLabelTextField;
	private JLabel vlanLabel;
	private PtnTextField vlanTextField;
    private JLabel ipLabel;
    private PtnTextField ipTextField;
    private PtnButton confirm;// 保存
    private GridBagLayout gridBagLayout ;
    private JPanel centreJPanel;
    private JPanel buttonPanel;
    private JLabel labMessage ;
    private EthLoopController ethLoopController;
    private EthLoopInfo thLoopInfo;
    public EthernetLoopPanel(){
    	init();
        setLayout();
    	addListener();
    	ethLoopController = new EthLoopController(this);
    	thLoopInfo =this.getThLoopInfo();
    }
	/**
     * 初始化数据
     * 
     */
	private void init() {
		try {
			this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_ETH_LOOP)));
			labMessage = new JLabel();
			buttonPanel = new JPanel();
			centreJPanel = new JPanel();
			gridBagLayout = new GridBagLayout();
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false,RootFactory.CORE_MANAGE);
			loopLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.ETH_LOOPENABEL));
			loopComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(loopComboBox, "ENABLEDSTATUEOAM");
			portLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.ETH_LOOPPORT));
			portComboBox = new JComboBox();
			comboBoxData(portComboBox);
			loopRuleLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.ETH_LOOPRULE));//环回规则
			loopRuleComboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(loopRuleComboBox, "LOOPRULE");
			macLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.ETH_LOOPMAC));//MAC
			macLabelTextField = new PtnTextField();
			macLabelTextField.setText("00-00-00-00-00-00");
			macLabelTextField.setEditable(false);
			vlanLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.ETH_LOOPVLAN));//vlan
			vlanTextField = new PtnTextField();
			vlanTextField.setText("1");
			setValidate(vlanTextField,ConstantUtil.LABOAMETNVLAN_MAXVALUE,ConstantUtil.LABOAMETNVLAN_MINVALUE);
			vlanTextField.setEditable(false);
			ipLabel  = new JLabel(ResourceUtil.srcStr(StringKeysObj.ETH_LOOPIP));//ip
			ipTextField = new PtnTextField();
			ipTextField.setText("0.0.0.0");
			ipTextField.setEditable(false);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		};

	}
	
	private void addListener() {
		
		try {
			loopRuleComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if(loopRuleComboBox.getSelectedIndex()==0){
						ipTextField.setEditable(false);
						vlanTextField.setEditable(false);
						macLabelTextField.setEditable(false);
						ipTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						vlanTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						macLabelTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						vlanTextField.setText("");
						macLabelTextField.setText("");
						ipTextField.setText("");
						confirm.setEnabled(true);
						labMessage.setForeground(Color.BLACK);
						labMessage.setText("");
					}
					else if(loopRuleComboBox.getSelectedIndex()==1){
						ipTextField.setEditable(false);
						vlanTextField.setEditable(true);
						macLabelTextField.setEditable(false);
						ipTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						vlanTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						macLabelTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						labMessage.setForeground(Color.BLACK);
						labMessage.setText("");
						confirm.setEnabled(true);
						if(thLoopInfo!=null && thLoopInfo.getLoopRule()==1){
							vlanTextField.setText(thLoopInfo.getVlanVaue()+"");
						}else{
							vlanTextField.setText("1");
						}
						
						macLabelTextField.setText("");
						ipTextField.setText("");
					}
					else if(loopRuleComboBox.getSelectedIndex()==2){
						ipTextField.setEditable(false);
						vlanTextField.setEditable(false);
						macLabelTextField.setEditable(true);
						ipTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						vlanTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						macLabelTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						labMessage.setForeground(Color.BLACK);
						labMessage.setText("");
						confirm.setEnabled(true);
						vlanTextField.setText("");
						ipTextField.setText("");
						if(thLoopInfo!=null && thLoopInfo.getLoopRule()==2){
							macLabelTextField.setText(thLoopInfo.getMacAddress());
						}else{
							macLabelTextField.setText("00-00-00-00-00-00");
						}
					
					}
					else if(loopRuleComboBox.getSelectedIndex()==3){
						ipTextField.setEditable(false);
						vlanTextField.setEditable(false);
						macLabelTextField.setEditable(true);
						ipTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						vlanTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						macLabelTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						labMessage.setForeground(Color.BLACK);
						labMessage.setText("");
						confirm.setEnabled(true);
						vlanTextField.setText("");
						macLabelTextField.setText("");
						ipTextField.setText("");
						if(thLoopInfo!=null && thLoopInfo.getLoopRule()==3){
							macLabelTextField.setText(thLoopInfo.getMacAddress());
						}else{
							macLabelTextField.setText("00-00-00-00-00-00");
						}
					
					}
					else if(loopRuleComboBox.getSelectedIndex()==4){
						ipTextField.setEditable(true);
						macLabelTextField.setEditable(false);
						vlanTextField.setEditable(false);
						ipTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						vlanTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						macLabelTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						labMessage.setForeground(Color.BLACK);
						labMessage.setText("");
						confirm.setEnabled(true);
						vlanTextField.setText("");
						macLabelTextField.setText("");
						if(thLoopInfo!=null && thLoopInfo.getLoopRule()==4){
							ipTextField.setText(thLoopInfo.getIp());
						}else{
							ipTextField.setText("0.0.0.0");
						}											
					}
					else if(loopRuleComboBox.getSelectedIndex()==5){
						ipTextField.setEditable(true);
						vlanTextField.setEditable(false);
						macLabelTextField.setEditable(false);
						ipTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						vlanTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						macLabelTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						labMessage.setForeground(Color.BLACK);
						labMessage.setText("");
						confirm.setEnabled(true);
						vlanTextField.setText("");
						macLabelTextField.setText("");
						if(thLoopInfo!=null && thLoopInfo.getLoopRule()==5){
							ipTextField.setText(thLoopInfo.getIp());
						}else{
							ipTextField.setText("0.0.0.0");
						}	
					}
				}
			});
			
				vlanTextField.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent e) {
						if(vlanTextField.isEditable()){
							isOK(vlanTextField.getText().trim(),3,vlanTextField,labMessage,confirm);
						
						}
					}
					@Override
					public void focusGained(FocusEvent e) {
						// TODO Auto-generated method stub
					}
				});
			
			macLabelTextField.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					if(macLabelTextField.isEditable()){
						isOK(macLabelTextField.getText().trim(),1,macLabelTextField,labMessage,confirm);
					}
				}
				@Override
				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
				}
			});
			
			ipTextField.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					
                    if(ipTextField.isEditable()){
                    	isOK(ipTextField.getText().trim(),2,ipTextField,labMessage,confirm);
					}
				}
				@Override
				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
   /**
   * @return boolean true 可以使用 false: 不能使用
   */
	private void isOK(String vlaue,int label,PtnTextField textField,JLabel labMessage,PtnButton confirm){
		super.isChecking(vlaue,label,textField,labMessage,confirm);
    }

	
	private void setLayout() {
		//主面板
		GridBagConstraints	gridBagConstraints = new GridBagConstraints();
		GridBagLayout	gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{100,60,100};
		gridBagLayout.rowWeights = new double[]{0,0.1};
		gridBagLayout.columnWidths = new int[]{300};
		gridBagLayout.columnWeights = new double[]{1};
		this.setLayout(gridBagLayout);
		addComponent(this, centreJPanel, 0, 1, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5,5,5,5), GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(this, buttonPanel, 0, 2, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5,5,5,5), GridBagConstraints.CENTER, gridBagConstraints);
       //centerJpanel 面板布局
		gridBagLayout = new GridBagLayout();
		centreJPanel.setLayout(gridBagLayout);
		centreJPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_ETH_LOOP)));
		Insets insert = new Insets(0, 50, 10, 20); 
		addComponent(centreJPanel, loopLabel, 0, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, loopComboBox, 1, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, portLabel, 2, 0,0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, portComboBox, 3, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, loopRuleLabel, 0, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, loopRuleComboBox, 1, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, macLabel, 2, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, macLabelTextField, 3, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, vlanLabel, 0, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, vlanTextField, 1, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, ipLabel, 2, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, ipTextField, 3, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, labMessage, 0, 5, 0, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.EAST, gridBagConstraints);
	// 按钮布局
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[]{60,60,60,6};
		buttonLayout.columnWeights = new double[]{1.0,0,0,0};
		buttonLayout.rowHeights = new int[]{60};
		buttonLayout.rowWeights = new double[]{1};
		buttonPanel.setLayout(buttonLayout);
		addComponent(buttonPanel, confirm, 2, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5,5,5,20), GridBagConstraints.WEST, gridBagConstraints);
		
}

	
	
	// 为端口 下来列表赋值
	private void comboBoxData(JComboBox jComboBox) throws Exception {
		DefaultComboBoxModel defaultComboBoxModel = null;
		PortService_MB portService = null;
		PortInst portInst = null;
		List<PortInst> allportInstList = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			portInst.setPortType(ConstantUtil.portType);
			allportInstList = portService.select(portInst);
			defaultComboBoxModel = (DefaultComboBoxModel) jComboBox.getModel();
				for (PortInst portInsts : allportInstList) {
					if (portInsts.getPortType().equalsIgnoreCase("nni") || portInsts.getPortType().equalsIgnoreCase("uni")
							|| portInsts.getPortType().equalsIgnoreCase("NONE")) {
						defaultComboBoxModel.addElement(new ControlKeyValue(portInsts.getID() + "", portInsts.getPortName(), portInsts));
					}
				}
			jComboBox.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			throw e;
		} finally {
			defaultComboBoxModel = null;
			UiUtil.closeService_MB(portService);
			portInst = null;
			allportInstList = null;
		}
	}
	public PtnButton getConfirm() {
		return confirm;
	}
	public void setConfirm(PtnButton confirm) {
		this.confirm = confirm;
	}
	
	public EthLoopInfo get(EthLoopInfo thLoopInfo) throws Exception{
		ControlKeyValue controlKeyValue = null;
		try {
			thLoopInfo.setSiteId(ConstantUtil.siteId);
			//环回使能
			controlKeyValue = (ControlKeyValue)loopComboBox.getSelectedItem();
			thLoopInfo.setLoopEnable(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			
			//环回规则
			controlKeyValue = (ControlKeyValue)loopRuleComboBox.getSelectedItem();
			thLoopInfo.setLoopRule(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			//端口号
			controlKeyValue = (ControlKeyValue)portComboBox.getSelectedItem();
			thLoopInfo.setPortNumber(((PortInst)controlKeyValue.getObject()).getNumber());
		  //ip
			if(ipTextField.isEditable()){
				thLoopInfo.setIp(ipTextField.getText().trim());
			}
			//mac
			if(vlanTextField.isEditable()){
				thLoopInfo.setVlanVaue(vlanTextField.getText().trim());
			}
			//mac
			if(macLabelTextField.isEditable()){
				thLoopInfo.setMacAddress(macLabelTextField.getText().trim());
			}
		} catch (Exception e) {
			throw e;
		}finally{
			controlKeyValue = null;
		}
		return thLoopInfo;
	}
	
	/**
	 * 初始化界面
	 * 
	 * @param info
	 * @throws Exception
	 */
	public void refresh(EthLoopInfo thLoopInfo) throws Exception {
		
		try {
			this.thLoopInfo=thLoopInfo;
			//环回使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(loopComboBox,thLoopInfo.getLoopEnable()+"");
			//环回规则
			super.getComboBoxDataUtil().comboBoxSelectByValue(loopRuleComboBox,thLoopInfo.getLoopRule()+"");
			//port
			this.comboBoxSelectByValue(portComboBox, thLoopInfo.getPortNumber()+"");
			//MAC
			macLabelTextField.setText(thLoopInfo.getMacAddress());
			//vlan
			vlanTextField.setText(thLoopInfo.getVlanVaue()+"");
			//ip
			ipTextField.setText(thLoopInfo.getIp());
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 下拉列表选中
	 * 
	 * @param jComboBox
	 *            下拉列表对象
	 * @param selectId
	 *            选中的id
	 */
	private  void comboBoxSelectByValue(JComboBox jComboBox, String values) {
		ControlKeyValue controlKeyValue = null;
		String value = null;
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			controlKeyValue = (ControlKeyValue) jComboBox.getItemAt(i);
			value = ((PortInst) controlKeyValue.getObject()).getNumber()+"";
			if (value.equals(values)) {
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}
	public EthLoopController getEthLoopController() {
		return ethLoopController;
	}
	public void setEthLoopController(EthLoopController ethLoopController) {
		this.ethLoopController = ethLoopController;
	}

	public EthLoopInfo getThLoopInfo() {
		return thLoopInfo;
	}

	public void setThLoopInfo(EthLoopInfo thLoopInfo) {
		this.thLoopInfo = thLoopInfo;
	}
	
}

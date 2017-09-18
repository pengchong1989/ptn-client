package com.nms.ui.ptn.ne.l2cp.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.ptn.L2cpInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.l2cp.controller.L2cpController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;


public class L2cpPanel extends PtnPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel l2cpEnableLabel;//L2CP使能
	private JComboBox l2cpEnable;
	private JLabel bpduTrapyLabel;//BPDU协议
	private JComboBox bpDuTrapy;
	private JLabel lldpTrapyLable;//LLDP协议
	private JComboBox lldbTrapy;
	private JLabel lacpTrapyLable;//;lacp协议
	private JComboBox lacpTrapy;
	private JLabel ieeeTrapyLable;//IEEE802.1x协议
	private JComboBox ieeeTrapy;
	private JLabel macAddressLable;//mac
	private PtnTextField macLabelText;
	private JLabel trapyNumberLable;//协议号
	private PtnTextField trapyNumber;
	
	private PtnButton confirm;//保存
	private PtnButton syncbutton;//同步
	private JPanel centreJPanel;
	private JPanel buttonPanel;
	private JLabel labMessage ;
	
	private L2cpController l2cpController;
	
	public L2cpPanel(){
		init();
		setLayout();
		addListener();
		l2cpController = new L2cpController(this);
	}
	//初始化
	private void init() {
		try {
			this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.L2CP_MODEL)));
			labMessage = new JLabel();
			buttonPanel = new JPanel();
			centreJPanel = new JPanel();
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true,RootFactory.CORE_MANAGE);
			syncbutton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SYNCHRO),true,RootFactory.CORE_MANAGE);
			l2cpEnableLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.L2CP_ENABLE));
			l2cpEnable = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(l2cpEnable, "ENABLEDSTATUEOAM");
			bpduTrapyLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.L2CP_BPDUTRAPY));
			bpDuTrapy = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(bpDuTrapy, "TRAPYTYPE");
			lldpTrapyLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.L2CP_LLDP));
			lldbTrapy = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(lldbTrapy, "TRAPYTYPE");
			lacpTrapyLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.L2CP_LACP));
			lacpTrapy = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(lacpTrapy, "TRAPYTYPE");
			ieeeTrapyLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.L2CP_IEEE));
			ieeeTrapy = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(ieeeTrapy, "TRAPYTYPE");
			macAddressLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.L2CP_MAC));
			macLabelText = new PtnTextField();
			macLabelText.setText("00-00-00-00-00-00");
			trapyNumberLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.L2CP_TRAPYENUMBER));
			trapyNumber = new PtnTextField();
			trapyNumber.setText("00-00");
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}
	//设置布局
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
		centreJPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.L2CP_MODEL)));
		Insets insert = new Insets(0, 50, 10, 20); 
		addComponent(centreJPanel, l2cpEnableLabel, 0, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, l2cpEnable, 1, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, bpduTrapyLabel, 2, 0,0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, bpDuTrapy, 3, 0, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, lldpTrapyLable, 0, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, lldbTrapy, 1, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, lacpTrapyLable, 2, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, lacpTrapy, 3, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, ieeeTrapyLable, 0, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, ieeeTrapy, 1, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, macAddressLable, 2, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, macLabelText, 3, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, trapyNumberLable, 0, 3, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, trapyNumber, 1, 3, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
		addComponent(centreJPanel, labMessage, 3, 3, 0.1, 0, 1, 1, GridBagConstraints.BOTH, insert, GridBagConstraints.CENTER, gridBagConstraints);
	// 按钮布局
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[]{410,60,60,6,6};
		buttonLayout.columnWeights = new double[]{1.0,0,0,0,0};
		buttonLayout.rowHeights = new int[]{60};
		buttonLayout.rowWeights = new double[]{1};
		buttonPanel.setLayout(buttonLayout);
		addComponent(buttonPanel, labMessage, 0, 0, 0.1, 0, 1, 1, GridBagConstraints.NONE, insert, GridBagConstraints.WEST, gridBagConstraints);
		addComponent(buttonPanel, syncbutton, 2, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5,5,5,20), GridBagConstraints.WEST, gridBagConstraints);
		addComponent(buttonPanel, confirm, 3, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5,5,5,20), GridBagConstraints.WEST, gridBagConstraints);
  }
	//增加监听事件
	private void addListener() {
		macLabelText.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				isOK(macLabelText.getText().trim(),1,macLabelText,labMessage,confirm);
				if("00-00-00-00-00-00".equals(macLabelText.getText().trim())){
					trapyNumber.setEnabled(true);
				}else{
					trapyNumber.setEnabled(false);
					trapyNumber.setText("00-00");
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
		
		trapyNumber.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (CheckingUtil.checking(trapyNumber.getText().trim(), CheckingUtil.TRAPYNUMBER)) { 
					// 判断填写是否为字母和数字
					if(!"00-00".equals(trapyNumber.getText().trim())){
						macLabelText.setText("00-00-00-00-00-00");
						macLabelText.setEnabled(false);						
					}else{
						macLabelText.setEnabled(true);
					}					
					labMessage.setText("");
					labMessage.setForeground(Color.BLACK);
					trapyNumber.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					confirm.setEnabled(true);
				} else {
					labMessage.setText(ResourceUtil.srcStr(StringKeysTip.L2CPTRAPNUMBER));
					labMessage.setForeground(Color.RED);
					trapyNumber.setBorder(BorderFactory.createLineBorder(Color.RED));
					confirm.setEnabled(false);
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
	}
	  /**
	   * @return boolean true 可以使用 false: 不能使用
	   */
	private void isOK(String vlaue,int label,PtnTextField textField,JLabel labMessage,PtnButton confirm){
		super.isChecking(vlaue,label,textField,labMessage,confirm);
    }
	
	//界面赋值
	public void refresh(L2cpInfo l2cpInfo){
		try {
			//L2CP使能
			super.getComboBoxDataUtil().comboBoxSelectByValue(l2cpEnable,l2cpInfo.getL2cpEnable()+"");
			//BPDU协议
			super.getComboBoxDataUtil().comboBoxSelectByValue(bpDuTrapy,l2cpInfo.getBpduTreaty()+"");
			//LLDP协议
			super.getComboBoxDataUtil().comboBoxSelectByValue(lldbTrapy,l2cpInfo.getLldpTreaty()+"");
			//LACP协议
			super.getComboBoxDataUtil().comboBoxSelectByValue(lacpTrapy,l2cpInfo.getLacpTreaty()+"");
			//IEEE802.1x协议
			super.getComboBoxDataUtil().comboBoxSelectByValue(ieeeTrapy,l2cpInfo.getIeeeTreaty()+"");
			//指配目的MAC
			macLabelText.setText(l2cpInfo.getMacAddress());
			//指配协议号
			trapyNumber.setText(l2cpInfo.getTreatyNumber()+"");
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}
	
	public void get(L2cpInfo l2cpInfo){
		
		ControlKeyValue controlKeyValue = null;
		try {
			l2cpInfo.setSiteId(ConstantUtil.siteId);
			//L2CP使能
			controlKeyValue = (ControlKeyValue)l2cpEnable.getSelectedItem();
			l2cpInfo.setL2cpEnable(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			//BPDU协议
			controlKeyValue = (ControlKeyValue)bpDuTrapy.getSelectedItem();
			l2cpInfo.setBpduTreaty(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			//LLDP协议
			controlKeyValue = (ControlKeyValue)lldbTrapy.getSelectedItem();
			l2cpInfo.setLldpTreaty(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			//LACP协议
			controlKeyValue = (ControlKeyValue)lacpTrapy.getSelectedItem();
			l2cpInfo.setLacpTreaty(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			//IEEE802.1x协议
			controlKeyValue = (ControlKeyValue)ieeeTrapy.getSelectedItem();
			l2cpInfo.setIeeeTreaty(Integer.parseInt(((Code)controlKeyValue.getObject()).getCodeValue()));
			//指配目的MAC
			l2cpInfo.setMacAddress(macLabelText.getText().trim());
			//指配协议号
			l2cpInfo.setTreatyNumber(trapyNumber.getText().trim());
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			controlKeyValue = null;
		}
	}
	public PtnButton getConfirm() {
		return confirm;
	}
	public void setConfirm(PtnButton confirm) {
		this.confirm = confirm;
	}
	
	public PtnButton getSyncbutton() {
		return syncbutton;
	}
	public void setSyncbutton(PtnButton syncbutton) {
		this.syncbutton = syncbutton;
	}
}

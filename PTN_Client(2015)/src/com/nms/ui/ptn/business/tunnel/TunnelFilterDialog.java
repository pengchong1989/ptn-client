package com.nms.ui.ptn.business.tunnel;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.system.code.Code;
import com.nms.db.bean.system.user.UserInst;
import com.nms.model.system.user.UserInstServiece_Mb;
import com.nms.model.util.Services;
import com.nms.model.util.SiteFileTree;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.performance.view.TimeWindow;


/**
 * Tunnel的设置过滤对话框
 * @author sy
 *
 */
public class TunnelFilterDialog extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PtnButton confirm;
	private JButton concel;
	private JPanel butttonPanel;
	
	private JLabel lblMessage;//提示信息
	private JLabel lblname;//名称
	private JTextField txtName;
	private JLabel lblType;//类型
	private JComboBox cmbType;
	private JLabel lblStatus;//激活状态
	private JCheckBox chkStatus;
	private JLabel lblUser;//创建人
	private JComboBox cmbUser;
	private JLabel lblCreateTime;//创建时间
	private JComboBox cmbCreateTime;
	private JLabel lblSite;//A,Z端网元
	private SiteFileTree siteTree;
	private JComboBox cmbSiteTree;
	private PtnButton clear;//清除
	private ControlKeyValue newTime;
	private String startTime;
	private String overTime;

	
	/**
	 * 实例化实体
	 */
	public TunnelFilterDialog(){
		init();
	}
	//初始化
	public void init(){
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		initCompenment();
		setComboBox();
		setLayOut();
		addAction();
		//showWindow();
		
	}
	public void initCompenment(){
		
		this.lblname=new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		this.txtName=new javax.swing.JTextField();
		this.lblUser=new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_FOUNDER));
		this.cmbUser=new JComboBox();
		this.lblCreateTime=new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_CREATE_TIME));
		this.cmbCreateTime=new javax.swing.JComboBox();
		this.lblSite=new javax.swing.JLabel("AZ端网元");
		this.cmbSiteTree=new JComboBox();
		this.cmbSiteTree.addItem(ResourceUtil.srcStr(StringKeysObj.NET_BASE));
		this.siteTree=new SiteFileTree(cmbSiteTree, null, null);
		this.concel=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.confirm=new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		this.lblType=new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));
		this.cmbType=new javax.swing.JComboBox();
		this.lblStatus=new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		this.chkStatus=new javax.swing.JCheckBox();
		this.lblMessage=new javax.swing.JLabel();		
		this.clear=new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));			
		this.butttonPanel=new JPanel();
					
	}
	/**
	 * 设置 下拉列表的中的 成员值
	 */
	public void setComboBox(){
		UserInstServiece_Mb userInstServiceMb=null;
		List<UserInst> userList=null;
		try {
			userInstServiceMb=(UserInstServiece_Mb)ConstantUtil.serviceFactory.newService_MB(Services.UserInst);
			userList=userInstServiceMb.select();
			this.cmbUser.addItem(ResourceUtil.srcStr(StringKeysObj.STRING_ALL));
			if(userList!=null&&userList.size()>0){
				for(UserInst userInst:userList){
					this.cmbUser.addItem(userInst.getUser_Name());
				}
			}
			super.getComboBoxDataUtil().comboBoxData(this.cmbCreateTime, "endTime");
			super.getComboBoxDataUtil().comboBoxData(this.cmbType, "PROTECTTYPE");
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(userInstServiceMb);
		}
	}
	
	//对话框的位置与大小
	public void showWindow(){
		UiUtil.showWindow(this, 500, 600);
	}
	public void addAction(){
		
		this.concel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
		});
		this.clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
				
			}
		});
		this.cmbCreateTime.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					ControlKeyValue todTransmissionTimeTypekey_broad = (ControlKeyValue) cmbCreateTime.getSelectedItem();
					Code code = (Code) (todTransmissionTimeTypekey_broad.getObject());
					if (code != null && code.getCodeValue().equals("6")) {
						TimeWindow timewndow = new TimeWindow();
						UiUtil.showWindow(timewndow, 300, 200);
						if(!(timewndow.getStartTimeText().getText()==null||timewndow.getStartTimeText().getText().equals("")||
								timewndow.getEndTimeText().getText()==null||timewndow.getEndTimeText().getText().equals(""))){
							setStartTime(timewndow.getStartTimeText().getText());
							setOverTime(timewndow.getEndTimeText().getText());
							String addItems = ResourceUtil.srcStr(StringKeysTip.START) + getStartTime()+ ResourceUtil.srcStr(StringKeysTip.END)+ getOverTime();
							if (newTime != null) {
								cmbCreateTime.removeItem(newTime);
							}
							Code cod=new Code();
							cod.setCodeValue("7");
							newTime = new ControlKeyValue("7",addItems,cod);
							
							cmbCreateTime.addItem(newTime);
							cmbCreateTime.setSelectedItem(newTime);
						}
					}
				}
			}
		});
		/**
		 * 设置 名称  的 默认值  ：
		 *    当没有修改 或者没有出入时   显示<所有>  
		 */
		this.txtName.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				//光标不在 txt上
				if(txtName.getText().equals("")){
					txtName.setText("<"+ResourceUtil.srcStr(StringKeysObj.STRING_ALL)+">");
				}				
			}			
			@Override
			public void focusGained(FocusEvent e) {
				//得到光标时
				if(txtName.getText().equals("<"+ResourceUtil.srcStr(StringKeysObj.STRING_ALL)+">")){
					txtName.setText("");
				}
				
			}
		});
	
	}
	// 清除面板信息
	private void clear() {
		this.siteTree.getBox().getSelectionModel().clearSelection();
		chkStatus.setSelected(false);
		this.cmbCreateTime.setSelectedIndex(0);
		this.cmbType.setSelectedIndex(0);
		this.cmbUser.setSelectedIndex(0);
		txtName.setText("<"+ResourceUtil.srcStr(StringKeysObj.STRING_ALL)+">");
		
	}
	public void setButtonLayout(){
		GridBagLayout layout=new GridBagLayout(); 
		layout.columnWidths=new int[]{20,160,20,20};
		layout.columnWeights=new double[]{0,0.1,0,0};
		layout.rowHeights=new int[]{30};
		layout.rowWeights=new double[]{0.1};
		this.butttonPanel.setLayout(layout);
		GridBagConstraints c=new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		//1 
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight=1;
		c.gridwidth=1;
		c.insets=new Insets(5,5,5,10);
		layout.setConstraints(this.clear, c);
		butttonPanel.add(this.clear);
		c.gridx=2;
		layout.setConstraints(this.confirm, c);
		butttonPanel.add(this.confirm);
		c.gridx=3;
		layout.setConstraints(this.concel, c);
		butttonPanel.add(this.concel);
		
	}
	// 设置过滤界面布局
	public void setLayOut(){
		this.setButtonLayout();
		GridBagLayout layout=new GridBagLayout(); 
		layout.columnWidths=new int[]{20,200};
		layout.columnWeights=new double[]{0,0.2};
		layout.rowHeights=new int[]{20,30,80,30,30,30,30,30};
		layout.rowWeights=new double[]{0,0,0.2,0,0,0,0,0};
		this.setLayout(layout);
		GridBagConstraints c=new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		//1 
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight=1;
		c.gridwidth=2;
		c.insets=new Insets(5,5,5,10);
		layout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);
		//2 名称
		c.gridy=1;
		c.gridwidth=1;
		layout.setConstraints(this.lblname, c);
		this.add(this.lblname);
		
		c.gridx=1;
		layout.setConstraints(this.txtName, c);
		this.add(this.txtName);
		//3 网元
		c.gridx=0;
		c.gridy=2;
		layout.setConstraints(this.lblSite, c);
		this.add(this.lblSite);
				
		c.gridx=1;
		layout.setConstraints(this.siteTree, c);
		this.add(this.siteTree);
		//4  类型
		c.gridx=0;
		c.gridy=3;
		layout.setConstraints(this.lblType, c);
		this.add(this.lblType);
		
		c.gridx=1;
		layout.setConstraints(this.cmbType, c);
		this.add(this.cmbType);
		//5 创建人
		c.gridx=0;
		c.gridy=4;
		layout.setConstraints(this.lblUser, c);
		this.add(this.lblUser);
		
		c.gridx=1;
		layout.setConstraints(this.cmbUser, c);
		this.add(this.cmbUser);
		//6 创建时间
		c.gridx=0;
		c.gridy=5;
		layout.setConstraints(this.lblCreateTime, c);
		this.add(this.lblCreateTime);
		
		c.gridx=1;
		layout.setConstraints(this.cmbCreateTime, c);
		this.add(this.cmbCreateTime);
		//7  激活状态
		c.gridx=0;
		c.gridy=6;
		layout.setConstraints(this.lblStatus, c);
		this.add(this.lblStatus);
		
		c.gridx=1;
		layout.setConstraints(this.chkStatus, c);
		this.add(this.chkStatus);
		//8  按钮
		c.gridx=0;
		c.gridy=7;
		c.gridwidth=2;
		layout.setConstraints(this.butttonPanel, c);
		this.add(this.butttonPanel);
		
	}
	public PtnButton getConfirm() {
		return confirm;
	}
	public void setConfirm(PtnButton confirm) {
		this.confirm = confirm;
	}
	public JComboBox getCmbType() {
		return cmbType;
	}
	public void setCmbType(JComboBox cmbType) {
		this.cmbType = cmbType;
	}
	public JCheckBox getChkStatus() {
		return chkStatus;
	}
	public void setChkStatus(JCheckBox chkStatus) {
		this.chkStatus = chkStatus;
	}
	public JTextField getTxtName() {
		return txtName;
	}
	public void setTxtName(JTextField txtName) {
		this.txtName = txtName;
	}
	public JComboBox getCmbUser() {
		return cmbUser;
	}
	public void setCmbUser(JComboBox cmbUser) {
		this.cmbUser = cmbUser;
	}
	public JComboBox getCmbCreateTime() {
		return cmbCreateTime;
	}
	public void setCmbCreateTime(JComboBox cmbCreateTime) {
		this.cmbCreateTime = cmbCreateTime;
	}
	public JComboBox getCmbSiteTree() {
		return cmbSiteTree;
	}
	public void setCmbSiteTree(JComboBox cmbSiteTree) {
		this.cmbSiteTree = cmbSiteTree;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getOverTime() {
		return overTime;
	}
	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}

	
}

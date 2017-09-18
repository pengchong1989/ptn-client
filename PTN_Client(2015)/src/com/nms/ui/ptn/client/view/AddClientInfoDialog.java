package com.nms.ui.ptn.client.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.nms.db.bean.client.Client;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;

public class AddClientInfoDialog extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2134572221366518535L;
	private Client client;
	private JLabel lblMessage;
	private JLabel lblName;
	private JLabel lblAdress;	
	private JLabel lblLinkMan;
	private JLabel lblPhoneNumber;
	private JLabel lblGrade;
	private JLabel lblArea;
	private JLabel lblRemark;
	private JTextField txtName;
	private JTextField txtAdress;
	private JTextField txtLinkMan;
	private JTextField txtPhoneNumber;
	private JComboBox txtGrade;
	private JTextField txtArea;
	private JTextField txtRemark;
	private PtnButton btnSave;
	private JButton btnCanel;
	private ClientManagerPanel clientManagerPanel;
	/**
	 * 构造方法
	 * @param clientManagerPanel
	 * 						客户面板
	 * @param modal
	 * @param client
	 * 			客户信息
	 */
	public AddClientInfoDialog(ClientManagerPanel clientManagerPanel, boolean modal, Client client) {
		try {
			this.setModal(modal);
			this.clientManagerPanel = clientManagerPanel;
			initComponentss();
			this.setLayout();
			this.addListener();
			if(null != client){
				this.client = client;
				this.initData();
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_CLIENT));
			}else{
				this.client = new Client();
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_INSERT_CLIENT));
			}
						
			UiUtil.showWindow(this, 350, 430);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 初始化数据
	 * @throws Exception
	 */
	private void initData() throws Exception {
		try {
			if (null != client) {
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_CLIENTINFO));
				this.txtName.setText(this.client.getName());
				this.txtAdress.setText(this.client.getAdress());
				this.txtLinkMan.setText(this.client.getLinkMan());
				this.txtPhoneNumber.setText(this.client.getPhoneNumber()+"");
				super.getComboBoxDataUtil().comboBoxSelect(txtGrade, UiUtil.getCodeById(Integer.parseInt(this.client.getGrade())).getId()+"");
				this.txtArea.setText(this.client.getArea());
				this.txtRemark.setText(this.client.getRemark());
			} else {
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_CLIENTINFO));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 添加监听
	 */
	private void addListener() {
		this.btnSave.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButton1ActionPerformed(e);
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
		this.btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButton2ActionPerformed(e);
			}
		});
	}

	/**
	 * 初始化控件
	 * @throws Exception
	 */
	private void initComponentss() throws Exception {
		try {
			this.lblMessage=new JLabel();
			this.lblName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_NAME));
			this.lblAdress = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_ADRESS));	
			this.lblLinkMan = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_LINKMAN));
			this.lblPhoneNumber = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_PHONENUMBER));
			this.lblGrade = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_GRADE));
			this.lblArea = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_AREA));
			this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
			this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.lblRemark = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_REMARK));
			this.txtName = new PtnTextField(true,PtnTextField.TYPE_STRING,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
			this.txtAdress = new PtnTextField(false,PtnTextField.TYPE_STRING,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);	
			this.txtLinkMan = new PtnTextField(false,PtnTextField.TYPE_STRING,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
			this.txtPhoneNumber = new PtnTextField(false,PtnTextField.TYPE_STRING,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
			this.txtGrade = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(txtGrade, "clientGrade");
			this.txtArea = new PtnTextField(false,PtnTextField.TYPE_STRING,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
			this.txtRemark = new PtnTextField(false,PtnTextField.TYPE_STRING,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 布局管理
	 */
	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 70, 180, 50 };
		layout.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		layout.rowHeights = new int[] { 25, 40, 40, 40, 40, 40, 40, 40, 40, 25, };
		layout.rowWeights = new double[] { 0.0, 0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0, };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		/** 第0行 */
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblMessage, c);
		this.add(lblMessage);
		
		/** 第一行 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 15, 5);
		layout.setConstraints(lblName, c);
		this.add(lblName);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtName, c);
		this.add(txtName);
		/** 第二行 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblAdress, c);
		this.add(lblAdress);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtAdress, c);
		this.add(txtAdress);
		/** 第三行 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblLinkMan, c);
		this.add(lblLinkMan);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtLinkMan, c);
		this.add(txtLinkMan);
		/** 第四行 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblPhoneNumber, c);
		this.add(lblPhoneNumber);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtPhoneNumber, c);
		this.add(txtPhoneNumber);
		
		/** 第五行 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblGrade, c);
		this.add(lblGrade);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtGrade, c);
		this.add(txtGrade);
		
		/** 第六行 */
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblArea, c);
		this.add(lblArea);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtArea, c);
		this.add(txtArea);
		
	
		/** 第七行 */
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblRemark, c);
		this.add(this.lblRemark);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.txtRemark, c);
		this.add(this.txtRemark);
		

		c.fill = GridBagConstraints.NONE;
		/** 第8行 */
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(btnSave, c);
		this.add(btnSave);
		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(btnCanel, c);
		this.add(btnCanel);

		
	
	}

	/**
	 * 取消按钮
	 * @param evt
	 */
	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	/**
	 * 保存按钮
	 * @param evt
	 */
	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		String beforeName = null;
		ClientService_MB service = null;
		ControlKeyValue grade = null;
		try {
			grade = (ControlKeyValue) this.txtGrade.getSelectedItem();
			service = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			this.client.setAdress(this.txtAdress.getText());
			this.client.setLinkMan(this.txtLinkMan.getText());
			if(!"".equals(this.txtPhoneNumber.getText())){
				this.client.setPhoneNumber(this.txtPhoneNumber.getText().trim());
			}else{
				this.client.setPhoneNumber(null);
			}
			this.client.setGrade(grade.getId());
			this.client.setArea(this.txtArea.getText());
			this.client.setRemark(this.txtRemark.getText());
			
			// 验证名称是否存在
			if (null != this.client && this.client.getId()>0) {
				beforeName = this.client.getName();
			}
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyName(EServiceType.CLIENT.getValue(), this.txtName.getText().trim(), beforeName)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return;
			}
			this.client.setName(this.txtName.getText().toString());
			//验证电话号码是否正确
			if(!this.verifyPhoneNumber()){
				DialogBoxUtil.errorDialog(this, "电话号码格式不正确");
				return;
			}
			service.saveOrUpdate(client);
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED) + "");
			//添加日志记录
			if(0==client.getId()){
				this.btnSave.setOperateKey(EOperationLogType.CLENTINSERT.getValue());
			}else{
				this.btnSave.setOperateKey(EOperationLogType.CLIENTUPDATE.getValue());
			}
			
			btnSave.setResult(1);
			this.dispose();
//				NetworkElementPanel.getNetworkElementPanel().showTopo(true);

			// 如果是null 说明是在拓扑界面弹出的。 不用刷新子网列表
			if (null != this.clientManagerPanel) {
				this.clientManagerPanel.getController().refresh();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			beforeName = null;
		}

	}

	/**
	 * 电话号码验证
	 * 分为手机号码验证和电话号码验证
	 */
	private boolean verifyPhoneNumber() {
		String number = this.txtPhoneNumber.getText().trim();
		if(this.isMobile(number) || this.isPhone(number)){
			return true;
		}
		return false;
	}
	
	/** 
     * 手机号验证 
     * @param  str 
     * @return 验证通过返回true 
     */  
    private boolean isMobile(String str) {   
        boolean b = false;   
        Pattern p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号  
        Matcher m = p.matcher(str);  
        b = m.matches();   
        return b;  
    }  
    
    /** 
     * 电话号码验证 
     * @param  str 
     * @return 验证通过返回true 
     */  
    private boolean isPhone(String str) {   
        Matcher m = null;  
        boolean b = false;    
        Pattern p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的  
        Pattern p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的  
        if(str.length() >9)  
        {   m = p1.matcher(str);  
            b = m.matches();    
        }else{  
            m = p2.matcher(str);  
            b = m.matches();   
        }    
        return b;  
    }  
}

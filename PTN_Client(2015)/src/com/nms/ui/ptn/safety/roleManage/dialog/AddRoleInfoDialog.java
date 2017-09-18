package com.nms.ui.ptn.safety.roleManage.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.system.roleManage.RoleInfo;
import com.nms.db.bean.system.roleManage.RoleManage;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.roleManage.RoleInfoService_MB;
import com.nms.model.system.roleManage.RoleManageService_MB;
import com.nms.model.util.RoleRootPanel;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;

/**
 * 新建角色 对话框
 * 
 * @author Administrator
 * 
 */
public class AddRoleInfoDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel upLabel; // 错误消息文本显示label
	private JLabel roleLabel; // 角色名称label
	private JLabel remarkLabel; // 备注label
	private PtnTextField roleField; // 角色文本框
	private JTextField remarkField; // 备注文本框
	private PtnButton confim; // 确认按钮
	private JButton cancel; // 取消按钮
	private RoleRootPanel roleRootPanel = null; // 权限tree的面板
	private RoleInfo roleInfo = null;
	private JPanel btnPanel = null; // 按钮的面板

	/**
	 * 创建一个新的实例
	 * 
	 * @param roleInfo
	 *            角色bean对象 如果是修改操作，传入对象。 新增传入null;
	 * @param roleManagePanel
	 *            角色主列表页面
	 */
	public AddRoleInfoDialog(RoleInfo roleInfo) {
		try {
			this.roleInfo = roleInfo;
			if(roleInfo==null){
				this.setTitle(ResourceUtil.srcStr(StringKeysTip.TIT_CREATE_ROLE));
			}else{
				this.setTitle(ResourceUtil.srcStr(StringKeysTip.TIT_UPDATE_ROLE));
			}
			this.setModal(true);
			this.initComponents();
			this.setLayout();
			this.addListener();
			this.initData();
			this.bindingData();
			UiUtil.showWindow(this, 400, 500);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 修改时 绑定数据
	 * @throws Exception 
	 */
	private void bindingData() throws Exception{
		if(null != this.roleInfo){
			this.roleField.setText(this.roleInfo.getRoleName());
			this.remarkField.setText(this.roleInfo.getRemark());
			this.roleRootPanel.checkData(this.roleInfo.getRoleManageList());
		}
	}

	/**
	 * 界面初始化数据数据
	 * 
	 * @throws Exception
	 */
	private void initData() throws Exception {
		RoleManageService_MB roleManageService = null;
		List<RoleManage> roleManageList = null;
		try {
			roleManageService = (RoleManageService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ROLEMANAGESERVICE);
			roleManageList = roleManageService.select(new RoleManage());			
			if (null != roleManageList && roleManageList.size() > 0) {
				this.roleRootPanel.bindingData(roleManageList);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(roleManageService);
		}
	}

	/**
	 * 初始化控件
	 */
	public void initComponents() {

		try {
			this.upLabel = new javax.swing.JLabel("");
			this.roleLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROLE_NAME));
			this.remarkLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_REMARK));

			this.remarkField = new javax.swing.JTextField();
			this.confim = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM), false);
			this.roleField = new PtnTextField(true, 100, upLabel, confim, this);
			this.cancel = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.roleRootPanel = new RoleRootPanel(true);
			this.btnPanel = new JPanel();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		this.setBtnLayout();

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 100, 300 };
		layout.columnWeights = new double[] { 0, 0.1 };
		layout.rowHeights = new int[] { 40, 40, 40, 300, 40 };
		layout.rowWeights = new double[] { 0, 0, 0, 0.1, 0 };
		this.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;

		// 第一行 错误消息
		c.gridx = 0;
		c.gridy = 0;
		layout.addLayoutComponent(this.upLabel, c);
		this.add(upLabel);

		// 第二行 角色名称
		c.gridy = 1;
		layout.setConstraints(this.roleLabel, c);
		this.add(roleLabel);
		c.gridx = 1;
		layout.setConstraints(this.roleField, c);
		this.add(roleField);

		// 第3行 备注
		c.gridx = 0;
		c.gridy = 2;
		layout.setConstraints(this.remarkLabel, c);
		this.add(remarkLabel);
		c.gridx = 1;
		layout.setConstraints(this.remarkField, c);
		this.add(remarkField);

		// 第4行 权限tree的面板
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		layout.setConstraints(this.roleRootPanel, c);
		this.add(roleRootPanel);

		// 第五行 按钮
		c.gridy = 4;
		layout.setConstraints(this.btnPanel, c);
		this.add(btnPanel);

	}

	/**
	 * 设置按钮布局
	 */
	private void setBtnLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 260, 70, 70 };
		layout.columnWeights = new double[] { 0.1, 0, 0 };
		layout.rowHeights = new int[] { 40 };
		layout.rowWeights = new double[] { 0 };
		this.btnPanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		layout.addLayoutComponent(this.confim, c);
		this.btnPanel.add(confim);
		c.gridx = 2;
		layout.addLayoutComponent(this.cancel, c);
		this.btnPanel.add(cancel);
	}

	/**
	 * 添加监听
	 */
	private void addListener() {

		// 取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		// 保存按钮
		this.confim.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				confimActionListener();
				
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
	}

	/**
	 * 保存按钮事件
	 */
	public void confimActionListener() {
		RoleInfoService_MB infoService = null;
		List<RoleManage> roleManageList = null; // 选中的菜单
		try {
			// 获取选中的菜单
			roleManageList = this.roleRootPanel.getSelectRoleManage();
			// 如果没有菜单 弹出提示
			if (null == roleManageList || roleManageList.size() == 0) {
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_ROLE));
				return;
			}

			infoService = (RoleInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ROLEINFOSERVICE);
			List<RoleInfo> roleInfoList=infoService.selectNoName(this.roleInfo);
			if(null!=roleInfoList){
				for(int i=0;i<roleInfoList.size();i++){
					RoleInfo info=roleInfoList.get(i);
					if(this.roleField.getText().equals(info.getRoleName())){
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_ROLE_EXIT));
						return;
					}
				}
			}
			// 如果此roleInfo为null 说明是点击新建按钮进入此界面 新实例化一个roleInfo对象
			if (null == this.roleInfo) {
				this.roleInfo = new RoleInfo();
			}
			this.roleInfo.setRoleName(this.roleField.getText());
			this.roleInfo.setRemark(this.remarkField.getText());
			this.roleInfo.setRoleManageList(roleManageList);

			// 如果主键为0 说明是新建操作
			if (this.roleInfo.getId() == 0) {
				infoService.insert(this.roleInfo);
				//添加操作日志记录
				this.insertOpeLog(EOperationLogType.INSERTROLE.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
			} else {
				infoService.update(this.roleInfo);
				//添加操作日志记录
				this.insertOpeLog(EOperationLogType.UPDATEROLE.getValue(), ResultString.CONFIG_SUCCESS, null, null);
			}
			//弹出提示。
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.dispose();

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(infoService);
		}

	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(confim, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTab.TAB_ROLEMANAGE),"");		
	}
}

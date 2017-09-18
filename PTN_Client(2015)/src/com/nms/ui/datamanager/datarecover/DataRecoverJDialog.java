package com.nms.ui.datamanager.datarecover;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.rmi.ui.CheckBoxPanel;
import com.nms.rmi.ui.util.RecoverFileChecking;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.Ptnf;
import com.nms.ui.datamanager.DataManagerDialog;
import com.nms.ui.datamanager.databackup.DataBaseUtil;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.util.Mybatis_DBManager;

public class DataRecoverJDialog extends DataManagerDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	List<String> tableNames = null;
	RecoverFileChecking recoverFileChecking = null;
	Connection conn = null;
	LoginLogServiece_Mb loginLogServiece = null;

	/**
	 * 创建一个新的实例
	 */
	public DataRecoverJDialog() {
		this.initComponent();
		super.setLayout();
		super.addBtnListener();
		this.addListener();

	}

	/**
	 * 添加监听
	 */
	private void addListener() {

		// 恢复按钮
		this.btn.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					btnRecoverAction();
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,this.getClass());
				}
			}
			@Override
			public boolean checking() {
				return recoverCheck();		
			}

		});
	}

	/**
	 * 初始化按钮事件
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	private void btnRecoverAction() {
		// 验证是否启动
		try {
			conn = Mybatis_DBManager.getInstance().getConnection();
			if(null == conn){
				DialogBoxUtil.succeedDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_RUNSERVER));
				return;
			}
		} catch (Exception e) {
			DialogBoxUtil.succeedDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_RUNSERVER));
			return;
		}
		
		Ptnf.getPtnf().getUserLogoutThread().setFlga(false);
		DataBaseUtil dbUtil = new DataBaseUtil();
		boolean flag = dbUtil.recoverRemote(this.txtSelect.getText().trim(), ConstantUtil.serviceIp);
		if(flag){
			// 如果没有异常。提示成功
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_RECOVERY_SUCCESS));
			this.insertOpeLog(EOperationLogType.DATABASERECOVIE.getValue(), ResultString.CONFIG_SUCCESS, null, null);
			if(this.tableNames != null && this.tableNames.contains("user_inst")){
				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USERONLINE_LOGOUT));
				System.exit(0);
			}else{
				dispose();
			}
		}else{
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_IMPORT_FAIL));
			this.insertOpeLog(EOperationLogType.DATABASERECOVIE.getValue(), ResultString.CONFIG_FAILED, null, null);
			Ptnf.getPtnf().getUserLogoutThread().setFlga(true);
		}						
	}

	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(btn, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_RECOVERY_DATA),"");		
	}
	
	/**
	 * 验证
	 */
	private Boolean recoverCheck() {
		
		int result = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_IS_RECOVER));
		try {
//			boolean flag = true;
			if(result != 0){
				return false;
			}
//			if (null == DBManager.getInstance().getConnection()) {
//				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_RUNSERVER));
//				return;
//			}
			if(!checkPanel()){
				return false;
			}

			// 获取应恢复的所有table名称
			tableNames = this.checkBoxPanel.getALlSelectTableName();
			recoverFileChecking = new RecoverFileChecking();
			// 验证文件是否匹配
			if (!recoverFileChecking.checkingFile(tableNames, this.txtSelect.getText().trim())) {
				DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_SQL_CORRECTFILE));
				return false;
			}	
			return true;
		} catch (FileNotFoundException e) {
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_NOFIND_FILE));
			return false;
		} catch (IOException e) {
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_EXCEPTION));
			return false;
		} catch (SQLException e) {
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_SQL_EXCEPTION));
			return false;
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
			return false;
		} finally {
			recoverFileChecking = null;
			try {
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				ExceptionManage.dispose(e,this.getClass());
			}
			UiUtil.closeService_MB(loginLogServiece);
		}
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		this.checkBoxPanel = new CheckBoxPanel(false);
		this.jPanel=new JPanel();
		jPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_RECOVERY_DATA)));
		this.btn = new PtnButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_RECOVERY));
		this.lblSelect = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
		this.txtSelect = new JTextField();
		this.btnSelect = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_FILE));
		this.txtSelect.setEditable(false);
		//取消按钮
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.weight=550;

	}


}

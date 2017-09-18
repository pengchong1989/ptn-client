/*
 * AddFieldDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.basicinfo.dialog.field;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.system.NetWork;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.NetService_MB;
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
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.topology.NetworkElementPanel;

/**
 * 
 * @author __USER__
 */
public class AddNetWorkDialog extends PtnDialog {
	
	private static final long serialVersionUID = -8898061454068563206L;
	private NetWork netWork = new NetWork();

	public AddNetWorkDialog(JPanel jPanel, boolean modal, String fieldId) {
		this.setModal(modal);
		try {
			initComponentss();
			this.setLayout();
			this.addListener();
			this.initData(fieldId);
			UiUtil.showWindow(this, 400, 240);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}
	
	private void initData(String fieldId) throws Exception{
		
		try {
			if(null==fieldId || "".equals(fieldId)){
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_FIELD));
				this.txtx.setText("0");
				this.txty.setText("0");
			}else{
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_FIELD));
				this.getfield(fieldId);
				this.txtname.setText(this.netWork.getNetWorkName());
				this.txtx.setText(this.netWork.getNetX()+"");
				this.txty.setText(this.netWork.getNetY()+"");
			}
		} catch (Exception e) {
			throw e;
		}
	}

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

	private void initComponentss() throws Exception {
		try {
			this.lblMessage=new JLabel();
			this.lblname = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
			this.lblx = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_X_COORDINATE));
			this.lbly = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_Y_COORDINATE));
			this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
			this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.txtname = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
			this.txtx = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave,this);
			this.txty = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave,this);
		} catch (Exception e) {
			throw e;
		}
		
	}

	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 200, 50 };
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 25, 40, 40, 40, 15, 40, 15 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0, 0, 0.2 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(15, 5, 5, 5);
		componentLayout.setConstraints(this.lblname, c);
		this.add(this.lblname);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.txtname, c);
		this.add(this.txtname);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblx, c);
		this.add(this.lblx);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.txtx, c);
		this.add(this.txtx);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lbly, c);
		this.add(this.lbly);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.txty, c);
		this.add(this.txty);
		
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.btnSave, c);
		this.add(this.btnSave);
		c.gridx = 2;
		componentLayout.setConstraints(this.btnCanel, c);
		this.add(this.btnCanel);
	}

	private void getfield(String fieldId) throws Exception {
		List<NetWork> netWorks = null;
		NetService_MB netService = null;
		try {
			netWork.setNetWorkId(Integer.parseInt(fieldId));
			netService = (NetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NETWORKSERVICE);
			netWorks = netService.select(netWork);

			if (null != netWorks && netWorks.size() > 0) {
				netWork = netWorks.get(0);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(netService);
		}
	}


	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

		NetService_MB netService = null;
		List<NetWork> listNetWorks=null;
		String filedName=null;
		NetWork ne=null;
		try {

			listNetWorks=new ArrayList<NetWork>();
			filedName=this.txtname.getText();
			netService = (NetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NETWORKSERVICE);
			//张坤 2013-8-14 修改--防止创建相同的域
			listNetWorks = netService.select();
				if(netWork.getNetWorkName()==null&&isExists(listNetWorks,filedName)){
					DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TiP_FiledISEXISTS));
				}else{
					if(netWork.getNetWorkName()!=null&&!netWork.getNetWorkName().equals(filedName)&&isExists(listNetWorks,filedName)){
						DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TiP_FiledISEXISTS));
					}else{
					this.netWork.setNetWorkName(this.txtname.getText());
					this.netWork.setNetX(Integer.parseInt(this.txtx.getText()));
					this.netWork.setNetY(Integer.parseInt(this.txty.getText()));
					int operateKey = 0;
					//添加日志记录
					if(this.netWork.getNetWorkId() > 0){
						operateKey = EOperationLogType.FIELDUPDATE.getValue();
						NetWork n=new NetWork();
                    	n.setNetWorkId(netWork.getNetWorkId());
                    	ne=netService.select(n).get(0);
					}else{
						operateKey = EOperationLogType.FIELDINSERT.getValue();
					}					
					netService.saveOrUpdate(netWork);
					//添加日志记录
                    if(operateKey == EOperationLogType.FIELDUPDATE.getValue()){						                    	
                     	this.insertOpeLog(EOperationLogType.FIELDUPDATE.getValue(), ResultString.CONFIG_SUCCESS, ne, netWork);
                    }else{
                    	this.insertOpeLog(EOperationLogType.FIELDINSERT.getValue(), ResultString.CONFIG_SUCCESS, null, netWork);
                    }
					DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
					this.dispose();
					NetworkElementPanel.getNetworkElementPanel().showTopo(true);
				}
				}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(netService);
		}

	}
	private void insertOpeLog(int operationType, String result, NetWork oldMac, NetWork newMac){
		AddOperateLog.insertOperLog(btnSave, operationType, result, oldMac, newMac, 0,newMac.getNetWorkName(),"NetWork");		
	}
	
	public boolean isExists(List<NetWork> listNetWorks,String fileName){
		try {
			if(listNetWorks!=null && listNetWorks.size()>0){
				for(NetWork netWork:listNetWorks){
					if(netWork.getNetWorkName().equals(fileName)){
						return true;
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return false;
	}

	private JLabel lblname;
	private JLabel lblx;
	private JLabel lbly;
	private JTextField txtname;
	private JTextField txtx;
	private JTextField txty;
	private PtnButton btnSave;
	private JButton btnCanel;
	private JLabel lblMessage;

}
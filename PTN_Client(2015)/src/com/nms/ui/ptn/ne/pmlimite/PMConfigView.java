package com.nms.ui.ptn.ne.pmlimite;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import com.nms.db.bean.perform.PmValueLimiteInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.perform.PmLimiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 性能门限配置界面 dxh
 */
public class PMConfigView extends JPanel {
	
	private static final long serialVersionUID = 5673448152495549821L;
	private PtnButton OKButton;
	private PtnButton syncButton;
	//private JButton cancelButton;
	private JLabel HighTempLabel;
	private JLabel CrcErrorLabel;
	private JTextField hiTempjtf;
	private JTextField crcErrorjtf;
	private JLabel LossNOLabel;
	private JTextField LossNOjtf;
	private JLabel receiveBadWrapLabel;
	private JTextField receiveBadWrapField;
	private JLabel tmsWorsenLabel;
	private JTextField tmsWorsenField;
	private JLabel tmsLoseLabel;
	private JTextField tmsLoseField;
	private JLabel alignLabel;
	private JTextField alignField;
	private JPanel buttonPanel = null;
	private JLabel tmcWorsenLabel;
	private PtnTextField tmcWorsenField;
	private JLabel tmcLoseLabel;
	private PtnTextField tmcLoseField;
	private JLabel tmpWorsenLabel;
	private PtnTextField tmpWorsenField;
	private JLabel tmpLoseLabel;
	private PtnTextField tmpLoseField;
	private JLabel lowTempLabel;
	private PtnTextField lowTempjtf;
	private PmValueLimiteInfo pmlimiteValue;
	private JLabel lblTitle;
	private JPanel titlePanel;
	private JPanel contentPanel;
	private JScrollPane jScrollPane;

	public PMConfigView() {
		try {
			initComponents();
			setLayout();
			PageGetvalue();
			addButtonListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void addButtonListener() {
		try {
			this.OKButton.addActionListener(new MyActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					PageSetvalue();
				}

				@Override
				public boolean checking() {
					// TODO Auto-generated method stub
					return true;
				}
			});

//			this.cancelButton.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//				}
//			});
		} catch (Exception e) {

			ExceptionManage.dispose(e,this.getClass());
		}
		
		this.syncButton.addActionListener(new MyActionListener(){

			public void actionPerformed(ActionEvent e) {
				sync();
			}

			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
			
		});
	}

	private void sync()
	{
		DispatchUtil pmLimiteDispatch = null;
		try {
			pmLimiteDispatch = new DispatchUtil(RmiKeys.RMI_PMLIMITE);
			String result = pmLimiteDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			this.insertOpeLogSync(EOperationLogType.PMCONFIGSYC.getValue(), result, null, null);	 
			this.PageGetvalue();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			pmLimiteDispatch = null;
		}
	}
	
	private void insertOpeLogSync(int operationType, String result, Object oldMac, Object newMac){		
		AddOperateLog.insertOperLog(syncButton, operationType, result, oldMac, newMac,ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysTab.TAB_PMLIMITCONFIG),"");	
		
	}
	
	private void PageGetvalue() {
		pmlimiteValue = new PmValueLimiteInfo();
		pmlimiteValue.setSiteId(ConstantUtil.siteId);
		PmLimiteService_MB service = null;
		try {
			service = (PmLimiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PmLimiteService);
			pmlimiteValue = service.select(pmlimiteValue);
			if (pmlimiteValue!=null&&pmlimiteValue.getId() > 0) {
				hiTempjtf.setText(pmlimiteValue.getHighTemp() + "");
				crcErrorjtf.setText(pmlimiteValue.getCrcError() + "");
				LossNOjtf.setText(pmlimiteValue.getLossNum() + "");
				tmsWorsenField.setText(pmlimiteValue.getTmsWorsen() + "");
				tmsLoseField.setText(pmlimiteValue.getTmsLose() + "");
				receiveBadWrapField.setText(pmlimiteValue.getReceiveBadWrap() + "");
				alignField.setText(pmlimiteValue.getAlign() + "");
				tmpWorsenField.setText(pmlimiteValue.getTmpWorsen() + "");
				tmpLoseField.setText(pmlimiteValue.getTmpLose() + "");
				tmcWorsenField.setText(pmlimiteValue.getTmcWorsen() + "");
				tmcLoseField.setText(pmlimiteValue.getTmcLose() + "");
				lowTempjtf.setText(pmlimiteValue.getLowTemp() + "");
			} else {
				crcErrorjtf.setText(65535 + "");
				LossNOjtf.setText(65535 + "");
				receiveBadWrapField.setText(65535 + "");
				alignField.setText(65535 + "");
				tmsWorsenField.setText(0 + "");
				tmsLoseField.setText(0 + "");
				tmpWorsenField.setText(0 + "");
				tmpLoseField.setText(0 + "");
				tmcWorsenField.setText(0 + "");
				tmcLoseField.setText(0 + "");
				hiTempjtf.setText(65 + "");
				lowTempjtf.setText(0 + "");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(service);
		}
	}

	private void PageSetvalue() {
		
		if(!check())
		{
			return;
		}
		PmValueLimiteInfo old = new PmValueLimiteInfo();
		PmLimiteService_MB service = null;
		pmlimiteValue = new PmValueLimiteInfo();
		pmlimiteValue.setHighTemp(Integer.valueOf(hiTempjtf.getText()));
		pmlimiteValue.setLowTemp(Integer.valueOf(lowTempjtf.getText()));
		pmlimiteValue.setCrcError(Integer.valueOf(crcErrorjtf.getText()));
		pmlimiteValue.setLossNum(Integer.valueOf(LossNOjtf.getText()));
		pmlimiteValue.setTmsWorsen(Integer.valueOf(tmsWorsenField.getText()));
		pmlimiteValue.setTmsLose(Integer.valueOf(tmsLoseField.getText()));
		pmlimiteValue.setReceiveBadWrap(Integer.valueOf(receiveBadWrapField.getText()));
		pmlimiteValue.setAlign(Integer.valueOf(alignField.getText()));
		pmlimiteValue.setTmpWorsen(Integer.valueOf(tmpWorsenField.getText()));
		pmlimiteValue.setTmpLose(Integer.valueOf(tmpLoseField.getText()));
		pmlimiteValue.setTmcWorsen(Integer.valueOf(tmcWorsenField.getText()));
		pmlimiteValue.setTmcLose(Integer.valueOf(tmcLoseField.getText()));
		pmlimiteValue.setSiteId(ConstantUtil.siteId);
		try {
			service = (PmLimiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PmLimiteService);
			DispatchUtil pmlimitedispatch = new DispatchUtil(RmiKeys.RMI_PMLIMITE);
			PmValueLimiteInfo pmlimite = new PmValueLimiteInfo();
			pmlimite.setSiteId(ConstantUtil.siteId);
			old=service.select(pmlimite);
			String result = pmlimitedispatch.excuteUpdate(pmlimiteValue);
			DialogBoxUtil.succeedDialog(null, result);
			this.insertOpeLog(EOperationLogType.PMCONFIGUPDATE.getValue(), result, old, pmlimiteValue);	 
			PageGetvalue();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	private void insertOpeLog(int operationType, String result, PmValueLimiteInfo oldMac, PmValueLimiteInfo newMac){
		SiteService_MB service = null;
		try {
			service = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			String siteName=service.getSiteName(ConstantUtil.siteId);
			newMac.setSiteName(siteName);
			if(oldMac!=null){
				oldMac.setSiteName(siteName);
			}
		    AddOperateLog.insertOperLog(OKButton, operationType, result, oldMac, newMac,ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysTab.TAB_PMLIMITCONFIG),"PMCONFIG");	
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
	
	private void initComponents() {
		try {
			lblTitle = new JLabel(ResourceUtil.srcStr(StringKeysTab.TAB_PMLIMITCONFIG));
			titlePanel = new JPanel();
			titlePanel.setBorder(BorderFactory.createEtchedBorder());
			titlePanel.setSize(60, ConstantUtil.INT_WIDTH_THREE);
			titlePanel.add(lblTitle);
			contentPanel = new JPanel();
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(contentPanel);
			jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			HighTempLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HIGHTEMP));
			CrcErrorLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CRC_VERIFY));
			hiTempjtf = new JTextField();
			crcErrorjtf = new JTextField();
			LossNOLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PACKET_LOSS_NUM));
			LossNOjtf = new JTextField();
			receiveBadWrapLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PACKET_BAD));
			receiveBadWrapField = new JTextField();
			tmsWorsenLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMS_DEGRADATION));
			tmsWorsenField = new JTextField();
			tmsLoseLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMS_LOSE_EFFICACY));
			tmsLoseField = new JTextField();
			alignLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALIGNING));
			alignField = new JTextField();
			tmcWorsenLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMC_DEGRADATION));
			tmcWorsenField = new PtnTextField();
			tmcLoseLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMC_LOSE_EFFICACY));
			tmcLoseField = new PtnTextField();
			tmpWorsenLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMP_DEGRADATION));
			tmpWorsenField = new PtnTextField();
			tmpLoseLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TMP_LOSE_EFFICACY));
			tmpLoseField = new PtnTextField();
			lowTempLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOWTEMP));
			lowTempjtf = new PtnTextField();

			syncButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SYNCHRO),true,RootFactory.CORE_MANAGE);
			OKButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true,RootFactory.CORE_MANAGE);
//			cancelButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			buttonPanel = new JPanel();
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void setLayout(){
		setGridBagLayout();
		setButtonLayout();
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		GridBagLayout contentLayout = new GridBagLayout();
		this.setLayout(contentLayout);
		contentLayout.columnWeights = new double[] { 1.0 };
		contentLayout.rowHeights = new int[] { 80, 300, 80};
		contentLayout.rowWeights = new double[] { 0, 0, 0};
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 0, 10, 0);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(titlePanel, c);
		this.add(titlePanel);
		c.gridy = 1;
		c.insets = new Insets(0, 0, 10, 0);
		contentLayout.setConstraints(jScrollPane, c);
		this.add(jScrollPane);
		c.gridy = 2;
		c.insets = new Insets(0, 0, 10, 0);
		contentLayout.setConstraints(buttonPanel, c);
		this.add(buttonPanel);
	}
	private void setGridBagLayout()  {
		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagConstraints = new GridBagConstraints();
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 200, 300, 200, 300 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 15, 15, 15 };
			gridBagLayout.rowWeights = new double[] { 0, 0, 0 };
			contentPanel.setLayout(gridBagLayout);
			contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_PMLIMITCONFIG)));
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(alignLabel, gridBagConstraints);
			contentPanel.add(alignLabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(alignField, gridBagConstraints);
			contentPanel.add(alignField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(CrcErrorLabel, gridBagConstraints);
			contentPanel.add(CrcErrorLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(crcErrorjtf, gridBagConstraints);
			contentPanel.add(crcErrorjtf);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(LossNOLabel, gridBagConstraints);
			contentPanel.add(LossNOLabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(LossNOjtf, gridBagConstraints);
			contentPanel.add(LossNOjtf);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(receiveBadWrapLabel, gridBagConstraints);
			contentPanel.add(receiveBadWrapLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(receiveBadWrapField, gridBagConstraints);
			contentPanel.add(receiveBadWrapField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tmsWorsenLabel, gridBagConstraints);
			contentPanel.add(tmsWorsenLabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tmsWorsenField, gridBagConstraints);
			contentPanel.add(tmsWorsenField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tmsLoseLabel, gridBagConstraints);
			contentPanel.add(tmsLoseLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tmsLoseField, gridBagConstraints);
			contentPanel.add(tmsLoseField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(tmpWorsenLabel, gridBagConstraints);
			contentPanel.add(tmpWorsenLabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(tmpWorsenField, gridBagConstraints);
			contentPanel.add(tmpWorsenField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(tmpLoseLabel, gridBagConstraints);
			contentPanel.add(tmpLoseLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(tmpLoseField, gridBagConstraints);
			contentPanel.add(tmpLoseField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(tmcWorsenLabel, gridBagConstraints);
			contentPanel.add(tmcWorsenLabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(tmcWorsenField, gridBagConstraints);
			contentPanel.add(tmcWorsenField);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(tmcLoseLabel, gridBagConstraints);
			contentPanel.add(tmcLoseLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(tmcLoseField, gridBagConstraints);
			contentPanel.add(tmcLoseField);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(HighTempLabel, gridBagConstraints);
			contentPanel.add(HighTempLabel);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(hiTempjtf, gridBagConstraints);
			contentPanel.add(hiTempjtf);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(lowTempLabel, gridBagConstraints);
			contentPanel.add(lowTempLabel);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(lowTempjtf, gridBagConstraints);
			contentPanel.add(lowTempjtf);			
			
			
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 按钮所在panel布局
	 */
	private void setButtonLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout gridBagLayout = null;
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] { 20, 20 };
			gridBagLayout.columnWeights = new double[] { 1.5, 0 };
			gridBagLayout.rowHeights = new int[] { 21,50 };
			gridBagLayout.rowWeights = new double[] { 0 };

			gridBagConstraints.insets = new Insets(5, 5, 5, 0);
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(syncButton, gridBagConstraints);
			buttonPanel.add(syncButton);
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(OKButton, gridBagConstraints);
			buttonPanel.add(OKButton);
//			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
//			gridBagConstraints.gridx = 3;
//			gridBagConstraints.gridy = 0;
//			gridBagLayout.setConstraints(cancelButton, gridBagConstraints);

//			buttonPanel.add(cancelButton);
			buttonPanel.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	public boolean check(){
		if(!checkInt(hiTempjtf,50,100)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_HIGHTEMP)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":50-100");
			return false;
		}
		if(!checkInt(crcErrorjtf,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_CRC_VERIFY)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		
		if(!checkInt(LossNOjtf,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_PACKET_LOSS_NUM)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(receiveBadWrapField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_PACKET_BAD)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(tmsWorsenField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_TMS_DEGRADATION)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(tmsLoseField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_TMS_LOSE_EFFICACY)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(alignField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_ALIGNING)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(tmcWorsenField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_TMC_DEGRADATION)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(tmcLoseField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_TMC_LOSE_EFFICACY)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(tmpWorsenField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_TMP_DEGRADATION)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(tmpLoseField,0,65535)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_TMP_LOSE_EFFICACY)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":0-65535");
			return false;
		}
		if(!checkInt(lowTempjtf,-10,10)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_LOWTEMP)+ResourceUtil.srcStr(StringKeysTip.TIP_SCOPE)+":-10-10");
			return false;
		}
		
		return true;
	}
	
	private boolean checkInt(JTextField jTextField,int min,int max){
		try{
			
			if (CheckingUtil.checking(jTextField.getText(), CheckingUtil.NUMBER_REGULAR)){
				if(Integer.parseInt(jTextField.getText())>=min && Integer.parseInt(jTextField.getText())<=max){
					return true;
				}
			}else if(CheckingUtil.checking(jTextField.getText(), CheckingUtil.NUMBER_NUM)){
				if(Integer.parseInt(jTextField.getText())>=min && Integer.parseInt(jTextField.getText())<=max){
					return true;
				}
			}
		}catch(Exception e)
		{
			ExceptionManage.dispose(e, this.getClass());
			return false;
		}
		return false;
	}

}

package com.nms.ui.ptn.ne.ecn.ospf.redistribution.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.ptn.ecn.OspfRedistribute;
import com.nms.db.bean.system.code.Code;
import com.nms.model.ptn.ecn.RedistributeService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTitle;

/**
 *重分发配置
 * @author sy
 *
 */
public class SaveDistributeDialog extends PtnDialog implements ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DistributePanel panel;
	private boolean isUpdate = false;

	public SaveDistributeDialog(OspfRedistribute ospfRedistribute, DistributePanel panel) {
		this.setModal(true);
		this.panel = panel;
		try {
			initComponents();
			comboBoxDate();
			setLayout();
			
			this.initDate();
			addListener();
			if (ospfRedistribute != null) {
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_REDIMIBUFION));
				initUpdateDate(ospfRedistribute);
			} else {
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_REDIMIBUFION));
			}
			this.showWindow();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void initComponents() {
		
		try {
			jPanel = new JPanel();
			buttonPanel=new JPanel();
			btnsave = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
			btncanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			Distributetype = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_REDISTRIBUTION_TYPE));
			Distributetypecom = new JComboBox();
			Distributesource = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_REDISTRIBUTION_SOURCE));
			Distributesourcecom = new JComboBox();
			routetype = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROUTE_TYPE));
			routetypecom = new JComboBox();
			metricvalue = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_METRIC_VALUE));
			lblMessage=new JLabel();
			metricvaluetext = new PtnTextField(false,7, 8, lblMessage, btnsave, this);
			metricvaluetext.setText("-1");
			metricvaluetext.setCheckingMaxValue(true);
			metricvaluetext.setCheckingMinValue(true);
			metricvaluetext.setMinValue(-1);
			metricvaluetext.setMaxValue(16777214);
			
			
			openrule = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FILTER_RULES));
			operruleistrue = new JCheckBox();
		} catch (Exception e) {
			
			ExceptionManage.dispose(e,SaveDistributeDialog.this.getClass());
		}
		
		
	}
	/**
	 * 按钮布局
	 */
	private void setButtonLayout() {

	//	Dimension dimension = new Dimension(400, 300);
		//this.setPreferredSize(dimension);
		//this.setMinimumSize(dimension);

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 70, 25,25 };
		layout.columnWeights = new double[] { 0.1, 0,0 };
		layout.rowHeights = new int[] { 20 };
		layout.rowWeights = new double[] { 0.1};
		this.buttonPanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();

		/** 第一行 重分发类型 */
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(this.btnsave, c);
		this.buttonPanel.add(btnsave);
		
		c.gridx =2;
		layout.setConstraints(this.btncanel, c);
		buttonPanel.add(btncanel);
	}
	private void setLayout() {
		this.setButtonLayout();
		Dimension dimension = new Dimension(400, 300);
		this.setPreferredSize(dimension);
		this.setMinimumSize(dimension);

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 70, 120 };
		layout.columnWeights = new double[] { 0, 0.2 };
		layout.rowHeights = new int[] { 15, 35, 35, 35, 35, 35, 35, 35 };
		layout.rowWeights = new double[] { 0.1, 0, 0, 0, 0, 0, 0, 0 };
		this.jPanel.setLayout(layout);
		this.add(jPanel);

		GridBagConstraints c = new GridBagConstraints();

		/** 填写数据类型信息提示 */
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 10, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(this.lblMessage, c);
		this.jPanel.add(lblMessage);
		
		/** 第一行 重分发类型 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(Distributetype, c);
		this.jPanel.add(Distributetype);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.addLayoutComponent(Distributetypecom, c);
		this.jPanel.add(Distributetypecom);

		/** 第二行 重分发获取来源 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(Distributesource, c);
		this.jPanel.add(Distributesource);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.addLayoutComponent(Distributesourcecom, c);
		this.jPanel.add(Distributesourcecom);

		/** 第三行 路由类型 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(routetype, c);
		this.jPanel.add(routetype);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.addLayoutComponent(routetypecom, c);
		this.jPanel.add(routetypecom);

		/** 第四行 Metric值 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(metricvalue, c);
		this.jPanel.add(metricvalue);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.addLayoutComponent(metricvaluetext, c);
		this.jPanel.add(metricvaluetext);

		/** 第五行 期望V5 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(openrule, c);
		this.jPanel.add(openrule);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.addLayoutComponent(operruleistrue, c);
		this.jPanel.add(operruleistrue);

		/** 第7行 确定按钮空出一行 */
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.EAST;
		layout.addLayoutComponent(this.buttonPanel, c);
		this.jPanel.add(buttonPanel);

	}

	private void showWindow() {
		this.setLocation(UiUtil.getWindowWidth(this.getWidth()), UiUtil.getWindowHeight(this.getHeight()));
		this.setVisible(true);
	}

	private void comboBoxDate() throws Exception {
		RedistributeService_MB redistributeService=null;
		int codeId = 0;
		try {
			super.getComboBoxDataUtil().comboBoxData(Distributetypecom, "REDISTRIBUTIONTYPE");
			redistributeService = (RedistributeService_MB) ConstantUtil.serviceFactory.newService_MB(Services.REDISTRIBUTE);
			List<OspfRedistribute> redistributeList=redistributeService.queryByNeID(ConstantUtil.siteId);
			/**
			 * 查询数据库，若已有重分发，则不可创建已有的（即排除已有的充分发挥类型）
			 */
			if(redistributeList!=null&&redistributeList.size()>0){
				DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) 	Distributetypecom.getModel();
				
				for(OspfRedistribute redInfo:redistributeList){
					Code code=UiUtil.getCodeByValue("REDISTRIBUTIONTYPE", redInfo.getRedistribute_type());
					
					if(code.getCodeValue().equals("static")){
						codeId=1;
					}else if(code.getCodeValue().equals("default")){
						codeId=2;
					}
					defaultComboBoxModel.removeElementAt(codeId);
				
				}
			}
			super.getComboBoxDataUtil().comboBoxData(routetypecom, "ROUTETYPE");
			Distributesourcecom.setEnabled(false);
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(redistributeService);
			codeId=0;
		}
	}

	private void initDate() throws Exception {
		try {
			if(metricvaluetext.getText().equals("-1")){
				SaveDistributeDialog.this.lblMessage.setText("");
				btnsave.setEnabled(true);
				SaveDistributeDialog.this.metricvaluetext.setBorder(PtnTextField.textFieldBorder);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void initUpdateDate(OspfRedistribute ospfRedistribute) throws Exception {
		try {
			super.getComboBoxDataUtil().comboBoxSelectByValue(Distributetypecom, ospfRedistribute.getRedistribute_type());
			super.getComboBoxDataUtil().comboBoxSelectByValue(routetypecom, ospfRedistribute.getMetrictype());
			metricvaluetext.setText(ospfRedistribute.getMetric() + "");
			Distributetypecom.setEnabled(false);
			if ("default".equals(ospfRedistribute.getRedistribute_type())) {
				super.getComboBoxDataUtil().comboBoxSelectByValue(Distributesourcecom, ospfRedistribute.getType());
			} else {
				operruleistrue.setSelected(ospfRedistribute.getEnable());
			}
			isUpdate = true;
		} catch (Exception e) {
			throw e;
		}
	}

	private void addListener(){ 
		
		btnsave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					SaveDistributeDialog.this.savaDistributeTimeslot();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});

		btncanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SaveDistributeDialog.this.dispose();
			}
		});

		Distributetypecom.addItemListener(this);
	}
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			try {
				if (e.getSource() == Distributetypecom) {
					String value = ((Code) (((ControlKeyValue) Distributetypecom.getSelectedItem()).getObject())).getCodeValue();
					if ("default".equals(value)) {
						Distributesourcecom.removeAllItems();
						super.getComboBoxDataUtil().comboBoxData(Distributesourcecom, "DISTRIBUTESOURCE");
						Distributesourcecom.setEnabled(true);
						operruleistrue.setSelected(false);
						operruleistrue.setEnabled(false);
					} else {
						Distributesourcecom.removeAllItems();
						Distributesourcecom.setEnabled(false);
						operruleistrue.setEnabled(true);
					}
				}
			} catch (Exception e1) {
				ExceptionManage.dispose(e1,this.getClass());
			}
		}
	}

	public void savaDistributeTimeslot() throws Exception {
		DispatchUtil redistributeDispatch = new DispatchUtil(RmiKeys.RMI_REDISTRIBUTE);
		OspfRedistribute ospfRedistribute = new OspfRedistribute();
		String message = "";
		try { 
			
			ospfRedistribute.setNeId(ConstantUtil.siteId + "");
			ospfRedistribute.setRedistribute_type(((Code) (((ControlKeyValue) Distributetypecom.getSelectedItem()).getObject())).getCodeValue());
			ospfRedistribute.setMetrictype(((Code) (((ControlKeyValue) routetypecom.getSelectedItem()).getObject())).getCodeValue());
			ospfRedistribute.setMetric(Integer.valueOf(metricvaluetext.getText()));
			ospfRedistribute.setStatus(1);
			if ("default".equals(ospfRedistribute.getRedistribute_type())) {
				ospfRedistribute.setType(((Code) (((ControlKeyValue) Distributesourcecom.getSelectedItem()).getObject())).getCodeValue());
			} else {
				ospfRedistribute.setEnable(operruleistrue.isSelected() ? true : false);
			}

			if (isUpdate) {
				message = redistributeDispatch.excuteUpdate(ospfRedistribute);
			} else {
				message = redistributeDispatch.excuteInsert(ospfRedistribute);
			}
			DialogBoxUtil.succeedDialog(this, message);
			ospfRedistribute = null;
			this.panel.controller.refresh();
			this.dispose();
		} catch (Exception e) {
			DialogBoxUtil.errorDialog(this, e.getMessage());
		}
	}

	private JLabel Distributetype;
	private JComboBox Distributetypecom;
	private JLabel Distributesource;
	private JComboBox Distributesourcecom;
	private JLabel routetype;
	private JComboBox routetypecom;
	private JLabel metricvalue;
	private PtnTextField metricvaluetext;
	private JLabel openrule;
	private JCheckBox operruleistrue;
	private JPanel jPanel;
	// private JPanel jPane2;
	private JButton btnsave;
	private JButton btncanel;
	private javax.swing.JSplitPane jSplitPane;
	private JPanel buttonPanel;
	private JLabel lblMessage;
}

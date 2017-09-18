package com.nms.ui.ptn.basicinfo.dialog.group.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import com.nms.db.bean.system.NetWork;
import com.nms.model.system.NetService_MB;
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


public class GroupManagerPanel extends PtnDialog {
	
	private static final long serialVersionUID = 1486668727510698070L;
	private GroupTablePanel subnetTablePanel ;
	private JLabel comboLabel;
	private JComboBox comboBox; //域下拉列表
	private JButton queryButton;//查询按钮
	private JButton cancelButton;
	public GroupManagerPanel() {
		try {
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_MANAGER_GROUP));
			super.setModal(true);
			initComponents();
			setLayout();
			setActionListention();
			UiUtil.showWindow(this, 470,440);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}
	
	public void initCombobox(JComboBox comboBox){
		
		NetService_MB service = null;
		List<NetWork> netWorks = null ;
		NetWork f = new NetWork();
		
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
		try {
			service = (NetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NETWORKSERVICE);
			netWorks = new ArrayList<NetWork>();
			netWorks = service.select();
			if(ConstantUtil.user.getIsAll()==1){
				f.setNetWorkName("ALL");
				defaultComboBoxModel.addElement(new ControlKeyValue("ALL", "ALL", f));
			}
			
			if(netWorks!=null&&netWorks.size()>0){
				for (NetWork netWork : netWorks) {
					defaultComboBoxModel.addElement(new ControlKeyValue(netWork.getNetWorkId()+ "", netWork.getNetWorkName(), netWork));
				}
			}
			
			comboBox.setModel(defaultComboBoxModel);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
	private void initComponents() {
		this.comboLabel=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FIELD));
		this.comboBox = new JComboBox();
		initCombobox(this.comboBox);
		this.queryButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT));
		this.subnetTablePanel = new GroupTablePanel(comboBox);
		this.cancelButton = new JButton (ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));;
	}




	private void setLayout(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 80, 230, 80 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0 ,0.0 };
		gridBagLayout.rowHeights = new int[] { 80, 300, 70 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0 ,0.0 };
		this.setLayout(gridBagLayout);
		GridBagConstraints c = new GridBagConstraints();
		addComponent(this, comboLabel, 0, 0, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(50, 50, 20, 0), GridBagConstraints.CENTER, c);
		addComponent(this, comboBox,   1, 0, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(50, 25, 20, 100), GridBagConstraints.CENTER, c);
		addComponent(this, queryButton,1, 0, 0.0, 0.0, 1, 1,  GridBagConstraints.BOTH, new Insets(50, 230, 20, 30), GridBagConstraints.CENTER, c);
		addComponent(this, subnetTablePanel, 0,1, 0.0, 0.0,  0, 0, GridBagConstraints.BOTH, new Insets(0, 0, 80, 0), GridBagConstraints.CENTER, c);
		addComponent(this, cancelButton, 2, 2, 0.0,0.0, 1, 1,  GridBagConstraints.BOTH, new Insets(15, 0, 40, 15), GridBagConstraints.SOUTH, c);
		
	}

	private void addComponent(PtnDialog PtnDialog, JComponent component, int gridx, int gridy, double weightx, double weighty, 
			int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		PtnDialog.add(component,gridBagConstraints);
	}
	private void setActionListention() {
		queryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jButton2ActionPerformed(e);
				
			}

		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButton1ActionPerformed(e);
			}		
		});
	}
	protected void jButton1ActionPerformed(ActionEvent e) {
		this.dispose();
	}
	private void jButton2ActionPerformed(ActionEvent evt) {
		try {
			this.subnetTablePanel.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}		
	
}

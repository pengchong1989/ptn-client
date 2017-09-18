package com.nms.ui.ptn.ne.pw.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTip;
/**
 * <p>文件名称:PWMultiViewDialog.java</p>
 * <p>文件描述:单站侧多段PW的属性配置界面</p>
 * <p>版权所有: 版权所有(C)2013-2015</p>
 * <p>公    司: 北京建博信通软件技术有限公司</p>
 * <p>内容摘要:</p>
 * <p>其他说明: </p>
 * <p>完成日期: 2015年3月2日</p>
 * <p>修改记录1:</p>
 * <pre>
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 * <p>修改记录2：</p>
 * @version 1.0
 * @author zhangkun
 */
public class PWMultiViewDialog extends PtnDialog{
	
	//****** 代码段: 属性定义 ***********************************************/
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6318055381806169631L;
	/***********Pw 对象*************/
	private PwInfo pwInfo;
	/***********前向LSP*************/
	private JLabel frontLspLabel;
	private JComboBox frontLspBox;
	/***********后向LSP*************/
	private JLabel backLspLabel;
	private JComboBox backLspBox;
	/***********前向入标签*************/
	private JLabel frontInLabel;
	private PtnTextField frontInText;
	/***********前向出标签*************/
	private JLabel frontOutLabel;
	private PtnTextField frontOutText;
	/***********后向入标签*************/
    private JLabel backInLabel;
	private PtnTextField backInText;
	/***********后向出标签*************/
	private JLabel backOutLabel;
	private PtnTextField backOutText;
	private JLabel portLabel;
	private JComboBox portFrontBox;
	private JLabel portBackLabel;
	private JComboBox portBackBox;
	private JLabel lblMessage;
	private PtnButton btnSave;
	private JButton btnCanel;
	private JPanel buttonPanel;
	private JLabel mipJLabel;
	private PtnTextField mipTextField;
	
	int frontInValue = 0;
	int frontOutValue = 0;
	int backInValue = 0;
	int backOutValue = 0;
	
	
	//****** 代码段: 构造方法 *******************************************************************************/

	public PWMultiViewDialog(PwInfo pwInfo)
	{
		try {
		this.setModal(true);
		this.setTitle(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_ATTRIBUTE));
		if(pwInfo == null)
		{
			pwInfo = new PwInfo();
			this.pwInfo = pwInfo;
		}else
		{
			this.pwInfo = pwInfo;
		}
		/*********初始化界面*************/
		initComponents();
		/*********设置界面布局***********/
		setLayout();
		/********给界面初始赋值**************/
		initData();
		/********界面修改赋值**************/
		updateData();
		/********增加相应的事件**********/
		addListeners();
		/********显示界面***************/
		UiUtil.showWindow(this, 750, 250);
		
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}
/************无参构造器*****************************/
	public PWMultiViewDialog()
	{}
	
	
	private void addListeners() 
	{
		
		/*****************取消事件****************************/
		btnCanel.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				canelAction();
			}
		});
		
		/****************端口选择 引导tunnel选择****************/
		frontLspBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initPortBox(portFrontBox,frontLspBox);				
			}
		});
		
		backLspBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initPortBox(portBackBox,backLspBox);
			}
		});
		
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				saveAction();
			}
		});
	}


	private void canelAction() 
	{
		this.dispose();
	}


	private void initData() 
	{
		try 
		{
			/***************初始化前后向LSP的tunnel选择****************************/
			initLspBox(frontLspBox,0);
			initLspBox(backLspBox,1);
			/***************初始化端口选项****************/
			initPortBox(portFrontBox,frontLspBox);
			initPortBox(portBackBox,backLspBox);
		} catch (Exception e) 
		{
           ExceptionManage.dispose(e, getClass());
		}
	}



	private void updateData()
	{
		try 
		{
			if(null != pwInfo.getMsPwInfos() && !pwInfo.getMsPwInfos().isEmpty() )
			{
				 frontInValue = pwInfo.getMsPwInfos().get(0).getFrontInlabel();
//				 frontOutValue = pwInfo.getMsPwInfos().get(0).getFrontOutlabel();
				 frontOutValue = pwInfo.getMsPwInfos().get(0).getBackInlabel();
				 backInValue = pwInfo.getMsPwInfos().get(0).getFrontOutlabel();
//				 backInValue = pwInfo.getMsPwInfos().get(0).getBackInlabel();
				 backOutValue = pwInfo.getMsPwInfos().get(0).getBackOutlabel();
				 frontInText.setText(frontInValue+"");
				 frontOutText.setText(frontOutValue+"");
				 backInText.setText(backInValue+"");
				 backOutText.setText(backOutValue+"");
				 super.getComboBoxDataUtil().comboBoxSelect(frontLspBox,   pwInfo.getMsPwInfos().get(0).getFrontTunnelId()+"");
				 super.getComboBoxDataUtil().comboBoxSelect(backLspBox,    pwInfo.getMsPwInfos().get(0).getBackTunnelId()+"");
				 initPortBox(portFrontBox,frontLspBox);
				 initPortBox(portBackBox,backLspBox);
				 mipTextField.setText(pwInfo.getMsPwInfos().get(0).getMipId()+"");
			}
			
		} catch (Exception e) 
		{
			 ExceptionManage.dispose(e, getClass());
		}
	}
	/***************初始化前后向LSP的tunnel选择****************************/
	private void initLspBox(JComboBox tunenlFrontBox2,int label) 
	{
		TunnelService_MB tunnelServiceMB = null;
		List<Tunnel> tunnelList = null;
	  try 
	  {
		  tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			tunnelList = tunnelServiceMB.selectNodesBySiteId(ConstantUtil.siteId);
			if(tunnelList != null && tunnelList.size() >0){
				DefaultComboBoxModel boxModel = (DefaultComboBoxModel) tunenlFrontBox2.getModel();
				ControlKeyValue keyValue = null;
				for(Tunnel tunnelInfo : tunnelList)
				{
					 keyValue = new ControlKeyValue(tunnelInfo.getTunnelId()+"",tunnelInfo.getTunnelName(), tunnelInfo);
					 boxModel.addElement(keyValue);
				}
				if(label ==1 && tunnelList.size()>1)
				{
					tunenlFrontBox2.setSelectedIndex(1);
				}
			}
	   } catch (Exception e) 
	   {
		   ExceptionManage.dispose(e, getClass());
	   }finally
	   {
		   tunnelList = null;
		   UiUtil.closeService_MB(tunnelServiceMB);
	   }
	}

	/***************初始化端口选项****************/
	private void initPortBox(JComboBox portbox,JComboBox tunnelFrontBox2) 
	{
		PortService_MB portService = null;
		List<PortInst> portInstList = null;
		List<Integer> portIds = new ArrayList<Integer>();
		try 
		{
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			Tunnel tunnel = ((Tunnel)((ControlKeyValue)tunnelFrontBox2.getSelectedItem()).getObject());
			if(tunnel.getaPortId() >0)
			{
				portIds.add(tunnel.getaPortId());
			}
             if(tunnel.getzPortId() >0)
			{
				portIds.add(tunnel.getzPortId());
			}
			portInstList = portService.getAllPortByIdsAndSiteId(portIds,ConstantUtil.siteId);
			DefaultComboBoxModel boxModel = (DefaultComboBoxModel) portbox.getModel();
			if(portInstList != null && portInstList.size() >0){
				ControlKeyValue keyValue = null;
				boxModel.removeAllElements();
				for(PortInst portInst : portInstList){
					keyValue = new ControlKeyValue(portInst.getPortId()+"", portInst.getPortName(), portInst);
					boxModel.addElement(keyValue);
				}
			}
		} catch (Exception e) 
		{
			 ExceptionManage.dispose(e, getClass());
		}finally
		{
			portInstList = null;	
			UiUtil.closeService_MB(portService);
		}
	}
	
   /**
    * 保存事件
    * 
    */
	private void saveAction() 
	{
		PwAddDialog pwAddDialog = null;
		MsPwInfo msPw = null;
		List<MsPwInfo> msPwInfos = null;
		try 
		{
			/************验证两条tunnel不能一样*****************/
			if(!checkTunnel()){
				return ;
			}
			/***************验证前向标签**********************************/
			pwAddDialog = new PwAddDialog();
			if(Integer.parseInt(frontInText.getText().trim()) == Integer.parseInt(backInText.getText().trim())){
				//入标签网元唯一
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_FRONTLABEL_BACKLABEL_IS_SAME));
				return;
			}
			List<Integer> labelValues = pwAddDialog.getLabelValues(frontInValue,frontInText.getText().trim());//前向入标签
			String result = pwAddDialog.isLabelUsable(labelValues,(Tunnel)((ControlKeyValue)frontLspBox.getSelectedItem()).getObject(),Integer.parseInt(frontOutText.getText().trim()),frontOutValue);
			if (result.length() > 0) {
				DialogBoxUtil.errorDialog(this, result + ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_OCCUPY));
				return;
			}
			/***************验证后向标签**********************************/
			List<Integer> labelBaclValues = pwAddDialog.getLabelValues(backInValue,backInText.getText().trim());//前向入标签
			result = pwAddDialog.isLabelUsable(labelBaclValues,(Tunnel)((ControlKeyValue)backLspBox.getSelectedItem()).getObject(),Integer.parseInt(backOutText.getText().trim()),backOutValue);
			if (result.length() > 0) {
				DialogBoxUtil.errorDialog(this, result + ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_OCCUPY));
				return;
			}
			msPwInfos = new ArrayList<MsPwInfo>();
			
			if(pwInfo.getMsPwInfos() != null && pwInfo.getMsPwInfos().size()>0)
			{
				msPw = pwInfo.getMsPwInfos().get(0);
			}else
			{
				msPw = new MsPwInfo();
			}
			msPw.setSiteId(ConstantUtil.siteId);
			msPw.setFrontInlabel(Integer.parseInt(frontInText.getText().trim()));
//			msPw.setFrontOutlabel(Integer.parseInt(frontOutText.getText().trim()));
			msPw.setFrontOutlabel(Integer.parseInt(backInText.getText().trim()));
			msPw.setBackInlabel(Integer.parseInt(frontOutText.getText().trim()));
//			msPw.setBackInlabel(Integer.parseInt(backInText.getText().trim()));
			msPw.setBackOutlabel(Integer.parseInt(backOutText.getText().trim()));
			msPw.setFrontTunnelId(Integer.parseInt(((ControlKeyValue)frontLspBox.getSelectedItem()).getId()));
			msPw.setBackTunnelId(Integer.parseInt(((ControlKeyValue)backLspBox.getSelectedItem()).getId()));
			msPw.setMipId(Integer.parseInt(mipTextField.getText().trim()));
			msPwInfos.add(msPw);
			
			pwInfo.setTunnelId(0);
			pwInfo.setInlabelValue(Integer.parseInt(frontInText.getText().trim()));
//			pwInfo.setOutlabelValue(Integer.parseInt(frontOutText.getText().trim()));
			pwInfo.setOutlabelValue(Integer.parseInt(backOutText.getText().trim()));
//			pwInfo.setBackInlabel(Integer.parseInt(backInText.getText().trim()));
			pwInfo.setBackInlabel(Integer.parseInt(frontOutText.getText().trim()));
//			pwInfo.setBackOutlabel(Integer.parseInt(backOutText.getText().trim()));
			pwInfo.setBackOutlabel(Integer.parseInt(backInText.getText().trim()));
			pwInfo.setZoppositeId("0.0.0.0");
			pwInfo.setAoppositeId("0.0.0.0");
			pwInfo.setMsPwInfos(msPwInfos);		
			
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.dispose();
		} catch (Exception e) 
		{
			e.printStackTrace();
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			pwAddDialog = null;
		}
	}

	private boolean checkTunnel() 
	{
		boolean flag = false;
		try {
			if(portFrontBox.getSelectedItem() == null || portBackBox.getSelectedItem() == null )
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_LSPERROR));
				return false;
			}else
			{
				if(Integer.parseInt(((ControlKeyValue)portFrontBox.getSelectedItem()).getId()) == Integer.parseInt(((ControlKeyValue)portBackBox.getSelectedItem()).getId())){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_LSPPORTERROR));
					return false;
				}
				else if(Integer.parseInt(((ControlKeyValue)frontLspBox.getSelectedItem()).getId()) == Integer.parseInt(((ControlKeyValue)backLspBox.getSelectedItem()).getId())){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_LSP));
					return false;
				}
				flag = true;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	  return flag;
    }



	private void setLayout() {
		/*******设置button布局***********/
		super.setButtonLayout(this.btnSave,this.btnCanel,this.buttonPanel,this);
		
		GridBagLayout componetLayout = new GridBagLayout();
		componetLayout.columnWidths = new int[]{5,220,5,220};
		componetLayout.columnWeights = new double[]{0,0,0,0};
		componetLayout.rowHeights = new int[]{30,30,30,30,30,30,30};
		componetLayout.rowWeights = new double[]{0,0,0,0};
		this.setLayout(componetLayout);
		/***************第一行 错误信息****************************/
		GridBagConstraints c  = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 4;
		componetLayout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);
		/***************第二行 选择端口****************************/
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		componetLayout.setConstraints(portLabel, c);
		this.add(portLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(portFrontBox, c);
		this.add(this.portFrontBox);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(frontLspLabel, c); 
		this.add(this.frontLspLabel);
		c.gridx = 3;
		c.gridwidth =1;
		componetLayout.setConstraints(frontLspBox, c); 
		this.add(this.frontLspBox);
		
		/***************第三行 选择tunnel****************************/
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		componetLayout.setConstraints(portBackLabel, c);
		this.add(portBackLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(portBackBox, c);
		this.add(this.portBackBox);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(backLspLabel, c);
		this.add(this.backLspLabel);
		c.gridx = 3;
		c.gridwidth =1;
		componetLayout.setConstraints(backLspBox, c);
		this.add(this.backLspBox);
		
		/***************第四行 选择前向标签****************************/
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		componetLayout.setConstraints(frontInLabel, c);
		this.add(frontInLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(frontInText, c);
		this.add(this.frontInText);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(frontOutLabel, c);
		this.add(this.frontOutLabel);
		c.gridx = 3;
		c.gridwidth =1;
		componetLayout.setConstraints(frontOutText, c);
		this.add(this.frontOutText);
		
		/***************第四行 选择后向标签****************************/
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		componetLayout.setConstraints(backInLabel, c);
		this.add(backInLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(backInText, c);
		this.add(this.backInText);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(backOutLabel, c);
		this.add(this.backOutLabel);
		c.gridx = 3;
		c.gridwidth =1;
		componetLayout.setConstraints(backOutText, c);
		this.add(this.backOutText);
		
		/***************第五行 MIP布局****************************/
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		componetLayout.setConstraints(mipJLabel, c);
		this.add(mipJLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(mipTextField, c);
		this.add(this.mipTextField);
		
		/***************第六行 选择按钮布局****************************/
		c.fill =GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(20, 5, 5, 5);
		c.gridx = 3;
		c.gridy = 6;
		c.gridwidth=3;
		componetLayout.setConstraints(this.buttonPanel, c);
		this.add(this.buttonPanel);
		
	}

	private void initComponents() throws Exception 
	{
		
		this.lblMessage=new JLabel();
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		frontLspLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_FRONTLSP));
		frontLspBox = new JComboBox();
		frontLspBox.setPreferredSize(new Dimension(180,5));
		backLspLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_BALCKLSP));
		backLspBox = new JComboBox();
		backLspBox.setPreferredSize(new Dimension(180,5));
		frontInLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_FRONTINLSP));
		frontInText = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave,this);
		setValue(frontInText);
		frontOutLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_FRONTBLACKLSP));
		frontOutText = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave,this);
		setValue(frontOutText);
		backInLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_BACKINLSP));
		backInText = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave,this);
		setValue(backInText);
		backOutLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_BACLOUTLSP));
		backOutText = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave,this);
		setValue(backOutText);
	    portLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_FRONTPORT));
	    portFrontBox = new JComboBox();
	    portFrontBox.setEnabled(false);
		portBackLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.SING1_MULTE_BLACKPORT));
		portBackBox = new JComboBox();
		portBackBox.setEnabled(false);
		mipJLabel = new JLabel("MIP");
		mipTextField = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave,this);
		Random random = new Random();
		mipTextField.setText((random.nextInt(99)+1)+"");
		buttonPanel = new JPanel();
	}
	
	/**
	 * 设置文本框的值
	 */
	private void setValue(PtnTextField textField)
	
	{
		textField.setCheckingMaxValue(true);
		textField.setCheckingMinValue(true);
		textField.setMaxValue(ConstantUtil.LABEL_MAXVALUE);
		textField.setMinValue(ConstantUtil.LABEL_MINVALUE);
	}
}

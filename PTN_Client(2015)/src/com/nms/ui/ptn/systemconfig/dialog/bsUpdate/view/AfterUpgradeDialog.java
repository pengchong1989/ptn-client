package com.nms.ui.ptn.systemconfig.dialog.bsUpdate.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.util.CodeConfigItem;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.upgradeManage.UpgradeMangeThread;

/**
 * 升级结果界面
 *
 */
public class AfterUpgradeDialog extends PtnDialog implements Runnable{	
	private static final long serialVersionUID = 1486668727510698070L;
	private GradeTablePanel subnetTablePanel ;
	private ButtonGroup restartButtonGroup;
	private JRadioButton setRadioButton;// 设置重启时间
	private JRadioButton manualRadioButton;// 手动重启
	private JLabel timeLabel;
	private JLabel selectTimeLabel;
	private PtnTextField timeField;//时间
	private PtnButton confirmButton;
	private PtnButton cancelButton;
    private AfterUpgradeDialog panel;
    private List<SiteInst> siteInsts;
    private BlockingQueue<Runnable> linkBlockingQueue = new LinkedBlockingQueue<Runnable>(1000000);
	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(30, 150, 30000, TimeUnit.MILLISECONDS, linkBlockingQueue,new ThreadPoolExecutor.AbortPolicy());
	private ZipFile zipFile;
	private List<ZipEntry> zipEntries;
	private int type;
	private int num=0;
	public JProgressBar jpb;
	private JLabel tipLabel;
	private ConcurrentHashMap<SiteInst, String> values;
	private boolean jpbRun = true;
	public AfterUpgradeDialog(List<ZipEntry> zipEntries, ZipFile zipFile, List<SiteInst> siteInstList,int type,JFrame jFrame) {
		try {
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			this.type = type;
			this.zipFile = zipFile;
			this.zipEntries = zipEntries;
	        panel= this;
	        this.siteInsts = siteInstList;
	        values = new ConcurrentHashMap<SiteInst, String>();
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_RESULT));
			super.setModal(true);
			initComponents();
			setLayout();
			setActionListention();
			threadPoolExecutor.execute(this);
			addCount();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}


	/**
	 * 动态生成升级的线程数
	 * @throws InterruptedException 
	 */
	public void addCount() throws InterruptedException {
		int count = 0;
		
		if(siteInsts.size() <CodeConfigItem.getInstance().getBatchSoftwareNumber() || siteInsts.size() ==CodeConfigItem.getInstance().getBatchSoftwareNumber()){
			count = siteInsts.size();
		}else if(siteInsts.size()-values.size()>CodeConfigItem.getInstance().getBatchSoftwareNumber() || siteInsts.size()-values.size()==CodeConfigItem.getInstance().getBatchSoftwareNumber()){
			count = CodeConfigItem.getInstance().getBatchSoftwareNumber();
		}else if(siteInsts.size()-values.size()<CodeConfigItem.getInstance().getBatchSoftwareNumber()){
			count = siteInsts.size()-values.size();
		}
		for (int i = 0; i < count; i++) {
			this.subnetTablePanel.refresh(siteInsts);
			UpgradeMangeThread upgradeMangeThread = new UpgradeMangeThread(siteInsts.get(num), type, zipEntries, values, zipFile,this.subnetTablePanel.getTable().getTableModel(),num);
			threadPoolExecutor.execute(upgradeMangeThread);
			num++;
			Thread.sleep(100);
			
		}
	}
	
	private void initComponents() {
		timeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SET_TIME));
		selectTimeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_TIME));
		timeField = new PtnTextField(); 
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = dateFormat.format(date);
		timeField.setText(time);
		restartButtonGroup = new ButtonGroup();
		setRadioButton = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.AUTORESTART));
		manualRadioButton = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.MANUALRESTART));
		restartButtonGroup.add(setRadioButton);
		restartButtonGroup.add(manualRadioButton);
		manualRadioButton.setSelected(true); 
		this.subnetTablePanel = new GradeTablePanel();
		this.confirmButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true);
		confirmButton.setEnabled(false);
		this.cancelButton = new PtnButton (ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL),true);
		tipLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_UPGRADE));
		jpb = new JProgressBar();  
		jpb.setOrientation(JProgressBar.HORIZONTAL);  
        jpb.setMaximum(100);  
        jpb.setMinimum(0);  
        jpb.setValue(0);  
        jpb.setStringPainted(true);
        Dimension d = jpb.getSize();
		Rectangle rect = new Rectangle(0,0, d.width, d.height);
		jpb.paintImmediately(rect);
        jpb.setEnabled(true);
	}

	private void setLayout(){
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 100, 100, 100,100 };
		layout.columnWeights = new double[] { 0.0, 1.0, 0.0,0.0 };
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20, 20, 20 ,20};
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0 ,0};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.gridwidth = 4;
		c.insets = new Insets(25, 10, 10, 10);
		layout.setConstraints(subnetTablePanel, c);
		this.add(subnetTablePanel);
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(selectTimeLabel, c);
		this.add(selectTimeLabel);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(setRadioButton, c);
		this.add(setRadioButton);
		c.gridx = 2;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(manualRadioButton, c);
		this.add(manualRadioButton);
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(timeLabel, c);
		this.add(timeLabel);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(timeField, c);
		this.add(timeField);
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(tipLabel, c);
		this.add(tipLabel);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(jpb, c);
		this.add(jpb);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 10, 10);
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(confirmButton, c);
		this.add(confirmButton);
		c.gridx = 2;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 10, 10);
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(cancelButton, c);
		this.add(cancelButton);
	}

	private void setActionListention() {
	
		confirmButton.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jButton1ActionPerformed(e);
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		
		cancelButton.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CANCLE_UPDATE));
				if (result == 0) {
					try {
						ConstantUtil.isCancleRun = true;//取消升级
						threadPoolExecutor.shutdownNow();
						for(SiteInst siteInst:siteInsts){
							//清空网元的摘要信息，由于异步原因，网元升级结果此时可能还无法清空
							cancelSofeWare(siteInst);
							siteInst.setBs(null);
							siteInst.setAllFileName("");
							siteInst.setFileName("");
							AddOperateLog.insertOperLog(cancelButton, EOperationLogType.CANCLEBATCHUPGRADE.getValue(), ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null, null,
									siteInst.getSite_Inst_Id(), siteInst.getCellId(), "");
						}
						values.clear();
						zipFile.close();
						zipEntries = null;
						jpbRun = false;
						DialogBoxUtil.succeedDialog(null, "配置成功");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					linkBlockingQueue = null;
					threadPoolExecutor = null;
					panel.dispose();
				}
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		
	}
	
	private String cancelSofeWare(SiteInst siteInst){
		String result = "";
		DispatchUtil dispatch = null;
		try {
			dispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			result = dispatch.executeQueryCancelSoftWare(siteInst);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		}
		return result;
		
	}
	
	protected void jButton1ActionPerformed(ActionEvent evt) {
		try {
			if(setRadioButton.isSelected()){
				cancelButton.setEnabled(false);
				String regex = "^(((20[0-3][0-9]-(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9]-(0[2469]|11)-(0[1-9]|[12][0-9]|30))) (20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])$";
				if(timeField.getText().matches(regex)){
					String result=null;
					DispatchUtil restartTimeDispatch = null;
					restartTimeDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long l = simpleDateFormat.parse(timeField.getText()).getTime();
					if(System.currentTimeMillis()>l){
						DialogBoxUtil.errorDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_REBOOT_TIME_NOW));
						cancelButton.setEnabled(true);
						return;
					}
					for(SiteInst siteInst:siteInsts){
						siteInst.setL(l);
					}
					result = restartTimeDispatch.taskRboot(siteInsts)+"\n";
					DialogBoxUtil.succeedDialog(null, result);
				}else{
					DialogBoxUtil.errorDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_UPGRADE_HOUR));
					return;
				}
			}else{
				for(SiteInst siteInst:siteInsts){
					siteInst.setBs(null);
					siteInst.setResult("");
					siteInst.setAllFileName("");
					siteInst.setFileName("");
					siteInst.setL(0);
				}
			}
			threadPoolExecutor.shutdownNow();
			panel.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	public GradeTablePanel getSubnetTablePanel() {
		return subnetTablePanel;
	}

	public void setSubnetTablePanel(GradeTablePanel subnetTablePanel) {
		this.subnetTablePanel = subnetTablePanel;
	}

	@Override
	public void run() {
		while(jpbRun){
			if(values.size() == 0){
				jpb.setValue(2);
				jpb.setMaximum(100);
			}else{
				jpb.setValue(values.size());
				jpb.setMaximum(siteInsts.size());
			}
			if(values.size() == siteInsts.size()){
				confirmButton.setEnabled(true);
				timeField.setEditable(true);
				setRadioButton.setEnabled(true);
				manualRadioButton.setEnabled(true);
				jpbRun = false;
			}
			try {
				if(siteInsts.size() != values.size() && values.size()%CodeConfigItem.getInstance().getBatchSoftwareNumber()==0 && values.size() != 0 && num == values.size()){
					addCount();
					System.out.println("11");
				}
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
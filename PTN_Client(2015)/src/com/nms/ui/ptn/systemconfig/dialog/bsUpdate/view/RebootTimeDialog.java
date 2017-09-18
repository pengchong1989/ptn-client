package com.nms.ui.ptn.systemconfig.dialog.bsUpdate.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTitle;


public class RebootTimeDialog extends PtnDialog {

	private static final long serialVersionUID = 1320343121147368431L;
	private JLabel timeJLabel;
	private PtnTextField timePtnTextField;	
	private PtnButton confirm;
	private PtnButton cancel;
	private List<SiteInst> siteInsts;
	private JLabel lblMessage;
	private RebootTimeDialog rebootTimeDialog;
	public RebootTimeDialog(List<SiteInst> siteInsts) throws Exception {
		rebootTimeDialog = this;
		this.siteInsts = siteInsts;
		this.setModal(true);
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_SET_RBOOT_TIME));
		initComponent();
		setLayout();
		addActionListener();
	}

	private void initComponent() throws Exception {
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		lblMessage = new JLabel();
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);
		cancel = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL),false);
		timeJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SET_TIME));
		timePtnTextField = new PtnTextField(true, 100, lblMessage, confirm, this);
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = dateFormat.format(date);
		timePtnTextField.setText(time);
		
	}

	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 30, 20 };
		layout.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		layout.rowHeights = new int[] { 20, 10, 20};
		layout.rowWeights = new double[] { 0, 0, 0};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(25, 10, 10, 10);
		layout.setConstraints(timeJLabel, c);
		this.add(timeJLabel);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(25, 10, 10, 10);
		layout.setConstraints(timePtnTextField, c);
		this.add(timePtnTextField);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 10, 10);
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(confirm, c);
		this.add(confirm);
		c.gridx = 2;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 10, 10);
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(cancel, c);
		this.add(cancel);
	}

	private void addActionListener() {
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					String regex = "^(((20[0-3][0-9]-(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9]-(0[2469]|11)-(0[1-9]|[12][0-9]|30))) (20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])$";
					if(timePtnTextField.getText().matches(regex)){
						String result=null;
						DispatchUtil restartTimeDispatch = null;
						restartTimeDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						long l = simpleDateFormat.parse(timePtnTextField.getText()).getTime();
						if(System.currentTimeMillis()>l){
							DialogBoxUtil.errorDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_REBOOT_TIME_NOW));
							return;
						}
						for(SiteInst siteInst:siteInsts){
							siteInst.setL(l);
						}
						result = restartTimeDispatch.taskRboot(siteInsts)+"\n";
						DialogBoxUtil.succeedDialog(null, result);
						UiUtil.insertOperationLog(EOperationLogType.REBOOTTIME.getValue(), result);
					}else{
						DialogBoxUtil.errorDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_UPGRADE_HOUR));
						return;
					}
					rebootTimeDialog.dispose();
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
				
			}
		});
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rebootTimeDialog.dispose();
			}
		});

	}

	public JButton getConfirm() {
		return confirm;
	}

	public void setConfirm(PtnButton confirm) {
		this.confirm = confirm;
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(PtnButton cancel) {
		this.cancel = cancel;
	}
	
	public static void main(String[] args) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long l = simpleDateFormat.parse("0000-00-00 00:00:00").getTime();
			String str = simpleDateFormat.format(simpleDateFormat.parse("0000-00-00 00:00:00").getTime());
			System.out.println(l);
			System.out.println(str);
			System.out.println(simpleDateFormat.parse(str).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

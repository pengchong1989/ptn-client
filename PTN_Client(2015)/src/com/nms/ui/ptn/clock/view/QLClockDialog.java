package com.nms.ui.ptn.clock.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.util.ClockUtil;

public class QLClockDialog extends PtnDialog {

	private static final long serialVersionUID = -2167085858018695344L;
	private JLabel outerClockLabel;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;
	private JLabel label5;
	private JLabel label6;
	private JLabel label7;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	private JLabel label11;
	private JLabel label12;
	private JLabel label13;
	private JLabel label14;
	private JLabel label15;
	private JLabel label16;
	private JLabel label17;
	private JLabel label18;
	private JLabel label19;
	private JLabel label20;
	private JLabel label21;
	private JLabel label22;
	private JLabel label23;
	private JLabel label24;
	private JLabel label25;
	private JLabel label26;
	private JComboBox outerClockComboBox;
	private JComboBox comBox1;
	private JComboBox comBox2;
	private JComboBox comBox3;
	private JComboBox comBox4;
	private JComboBox comBox5;
	private JComboBox comBox6;
	private JComboBox comBox7;
	private JComboBox comBox8;
	private JComboBox comBox9;
	private JComboBox comBox10;
	private JComboBox comBox11;
	private JComboBox comBox12;
	private JComboBox comBox13;
	private JComboBox comBox14;
	private JComboBox comBox15;
	private JComboBox comBox16;
	private JComboBox comBox17;
	private JComboBox comBox18;
	private JComboBox comBox19;
	private JComboBox comBox20;
	private JComboBox comBox21;
	private JComboBox comBox22;
	private JComboBox comBox23;
	private JComboBox comBox24;
	private JComboBox comBox25;
	private JComboBox comBox26;

	private JButton confirm;
	private JButton cancel;
	private JPanel buttonPanel;
	
	private List<PortInst> portList = new ArrayList<PortInst>();
	private List<JLabel> labelList = new ArrayList<JLabel>();
	private List<JComboBox> boxList = new ArrayList<JComboBox>();
	
//	private int siteType;//1代表703A,2代表703B,3代表710A,4代表710B

	public QLClockDialog(String title) {
		this.setModal(true);
		this.setTitle(title);
//		siteType = this.getSiteType();
		ClockUtil clockUtil=new ClockUtil();
		portList = clockUtil.getFrequencyPorts(ConstantUtil.siteId);
		this.addLabelList();
		this.addBoxList();
		// 初始化控件
		initComponent();
		// 界面布局
		setQLDialogLayout();
	}

	private void addLabelList() {
		labelList.add(label1 = new JLabel());
		labelList.add(label2 = new JLabel());
		labelList.add(label3 = new JLabel());
		labelList.add(label4 = new JLabel());
		labelList.add(label5 = new JLabel());
		labelList.add(label6 = new JLabel());
		labelList.add(label7 = new JLabel());
		labelList.add(label8 = new JLabel());
		labelList.add(label9 = new JLabel());
		labelList.add(label10 = new JLabel());
		labelList.add(label11 = new JLabel());
		labelList.add(label12 = new JLabel());
		labelList.add(label13 = new JLabel());
		labelList.add(label14 = new JLabel());
		labelList.add(label15 = new JLabel());
		labelList.add(label16 = new JLabel());
		labelList.add(label17 = new JLabel());
		labelList.add(label18 = new JLabel());
		labelList.add(label19 = new JLabel());
		labelList.add(label20 = new JLabel());
		labelList.add(label21 = new JLabel());
		labelList.add(label22 = new JLabel());
		labelList.add(label23 = new JLabel());
		labelList.add(label24 = new JLabel());
		labelList.add(label25 = new JLabel());
		labelList.add(label26 = new JLabel());
	}

	private void addBoxList() {
		boxList.add(comBox1 = new JComboBox());
		boxList.add(comBox2 = new JComboBox());
		boxList.add(comBox3 = new JComboBox());
		boxList.add(comBox4 = new JComboBox());
		boxList.add(comBox5 = new JComboBox());
		boxList.add(comBox6 = new JComboBox());
		boxList.add(comBox7 = new JComboBox());
		boxList.add(comBox8 = new JComboBox());
		boxList.add(comBox9 = new JComboBox());
		boxList.add(comBox10 = new JComboBox());
		boxList.add(comBox11 = new JComboBox());
		boxList.add(comBox12 = new JComboBox());
		boxList.add(comBox13 = new JComboBox());
		boxList.add(comBox14 = new JComboBox());
		boxList.add(comBox15 = new JComboBox());
		boxList.add(comBox16 = new JComboBox());
		boxList.add(comBox17 = new JComboBox());
		boxList.add(comBox18 = new JComboBox());
		boxList.add(comBox19 = new JComboBox());
		boxList.add(comBox20 = new JComboBox());
		boxList.add(comBox21 = new JComboBox());
		boxList.add(comBox22 = new JComboBox());
		boxList.add(comBox23 = new JComboBox());
		boxList.add(comBox24 = new JComboBox());
		boxList.add(comBox25 = new JComboBox());
		boxList.add(comBox26 = new JComboBox());
	}

//	/**
//	 * 通过siteId获取网元类型
//	 * @return
//	 */
//	private int getSiteType() {
//		String cellType = "";
//		int type = 0;
//		SiteInstDao dao = null;
//		Connection conn = null;
//		try {
//			dao = new SiteInstDao();
//			conn = DBManager.getConnection("");
//			cellType = dao.getSiteTypeList(ConstantUtil.siteId, conn).getCellType();
//			if("703A".equals(cellType)){
//				type = 1;
//			}
//			if("703B".equals(cellType)){
//				type = 2;		
//			}
//			if("710A".equals(cellType)){
//				type = 3;
//			}
//			if("710B".equals(cellType)){
//				type = 4;
//			}
//		} catch (Exception e) {
//			ExceptionManage.dispose(e,this.getClass());
//		}
//		return type;
//	}

	/**
	 * 初始化界面的值
	 * 
	 * @param values
	 */
	public void init(String values) {
		try {
			if (values != null && !"".equals(values)) {
				String[] qlValues = values.split("/");
				int i = 0;
				this.outerClockComboBox.setSelectedIndex(this.getIndex(Integer.parseInt(qlValues[i++]), 4));
				for (int j = 0; j < portList.size(); j++) {	
					if(labelList.get(j).getText().equals(portList.get(j).getPortName())){	
						boxList.get(j).setSelectedIndex(this.getIndex(Integer.parseInt(qlValues[portList.get(j).getNumber()]), 4));
					}
				}
			} else {
				setDefalutInit();
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 设置默认初始值
	 */
	private void setDefalutInit() {
		this.outerClockComboBox.setSelectedIndex(4);
		for (int i = 0; i < portList.size(); i++) {
			boxList.get(i).setSelectedIndex(4);
		}
	}

	/**
	 * 获取界面数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public String get() {
		StringBuffer sb = null;
		try {
			sb = new StringBuffer();
			sb.append(((ControlKeyValue) (outerClockComboBox.getSelectedItem())).getId()).append("/");
//			for (int i = 0; i < portList.size(); i++) {
//				if((i+1)==portList.get(i).getNumber()){
//					sb.append(((ControlKeyValue) (boxList.get(i).getSelectedItem())).getId()).append("/");
//				}else{
//					sb.append("11/");
//				}
//				
//			}
//			for (int i = portList.size()-1; i < 27-portList.size(); i++) {
//				for(int j=0;j<portList.size(); j++) {
//					if((i+1)==portList.get(j).getNumber()){
//						sb.append(((ControlKeyValue) (boxList.get(i).getSelectedItem())).getId()).append("/");
//					}else{
//						sb.append("11/");
//					}
//				}
			for(int i=1;i<27;i++){
				boolean flag=true;
				for(int j=0;j<portList.size();j++){
				    if((i)==portList.get(j).getNumber()){
				    	sb.append(((ControlKeyValue) (boxList.get(j).getSelectedItem())).getId()).append("/");
				    	flag=false;
				    	break;
				    }
				}
				if(flag){
					sb.append("11/");
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		sb.deleteCharAt(sb.toString().length() - 1);
		return sb.toString();
	}

	/**
	 * 根据设备值，得到门限的下拉列表的索引
	 * 
	 * @param num
	 *            门限设备值
	 * @param defaultIndex
	 *            下拉列表的索引
	 * @return
	 */
	private int getIndex(int num, int defaultIndex) {
		int index = defaultIndex;
		switch (num) {
		case 0:
			index = 0;
			break;
		case 2:
			index = 1;
			break;
		case 4:
			index = 2;
			break;
		case 8:
			index = 3;
			break;
		case 11:
			index = 4;
			break;
		case 15:
			index = 5;
			break;
		case 31:
			index = 6;
			break;
		default:
			index = defaultIndex;
			break;
		}
		return index;
	}

	private void initComponent() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, ResourceUtil.srcStr(StringKeysObj.QUALITY_UNKNOWN));
		map.put(2, ResourceUtil.srcStr(StringKeysObj.G811_CLOCK));
		map.put(4, ResourceUtil.srcStr(StringKeysObj.G812_PASS_CLOCK));
		map.put(8, ResourceUtil.srcStr(StringKeysObj.G813_NATIVE_CLOCK));
		map.put(11, ResourceUtil.srcStr(StringKeysObj.G813_CLOCK));
		map.put(15, ResourceUtil.srcStr(StringKeysObj.SYNCHRO_NO));
		map.put(31, ResourceUtil.srcStr(StringKeysObj.AUTO_S1));
		
		outerClockLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.OUTER_CLOCK));
		outerClockComboBox = new JComboBox();
		setModel(outerClockComboBox, map);
		for (int i = 0; i < portList.size(); i++) {
			labelList.get(i).setText(portList.get(i).getPortName());
			setModel(boxList.get(i), map);
		}

		confirm = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		buttonPanel = new JPanel();
	}

	private void setQLDialogLayout() {
		setButtonLayout();

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {5,10,50,50,10,50,5};
		layout.rowHeights = new int[] { 10,10,10,10,10,10,10,10,10,10,10,10,10,10};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(outerClockLabel, c);
		this.add(outerClockLabel);
		c.gridx = 2;
		c.gridy = 0;
		layout.setConstraints(outerClockComboBox, c);
		this.add(outerClockComboBox);
		
		for (int i = 0; i < portList.size(); i++) {
//			if(i == 0){
//				this.addComponent(this, labelList.get(i), 4, 0, c,layout);
//				this.addComponent(this, boxList.get(i), 5, 0, c,layout);
//			}else{
				if(i%2 == 1){
					this.addComponent(this, labelList.get(i), 1, (i+1)/2, c,layout);
					this.addComponent(this, boxList.get(i), 2, (i+1)/2, c,layout);
				}else{
					this.addComponent(this, labelList.get(i), 4, i/2, c,layout);
					this.addComponent(this, boxList.get(i), 5, i/2, c,layout);
				}
//			}
		}
		
		c.gridx = 0;
		c.gridy = 14;
		c.gridwidth = 7;
		layout.setConstraints(buttonPanel, c);
		this.add(buttonPanel);

	}

	private void setButtonLayout() {
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[] { 280, 20 };
		buttonLayout.columnWeights = new double[] { 0.0, 0.0 };
		buttonLayout.rowHeights = new int[] { 30 };
		buttonLayout.rowWeights = new double[] { 0.0 };
		buttonPanel.setLayout(buttonLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(5, 5, 5, 5);
		buttonLayout.setConstraints(confirm, c);
		buttonPanel.add(confirm);
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		buttonLayout.setConstraints(cancel, c);
		buttonPanel.add(cancel);
	}

	private void addComponent(JDialog dialog, JComponent component, int gridx, int gridy, 
			GridBagConstraints gridBagConstraints,GridBagLayout layout) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
//		dialog.add(component, gridBagConstraints);
		layout.setConstraints(component, gridBagConstraints);
		dialog.add(component);
	}
	
	private void setModel(JComboBox comboBox, Map<Integer, String> keyValues) {
		DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox.getModel();

		for (Integer key : keyValues.keySet()) {
			comboBoxModel.addElement(new ControlKeyValue(key.toString(), keyValues.get(key)));
		}
	}

	public JButton getConfirm() {
		return confirm;
	}

	public void setConfirm(JButton confirm) {
		this.confirm = confirm;
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}
}

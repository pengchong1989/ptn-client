package com.nms.ui.ptn.clock.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.ptn.ne.statusData.FrequencyBasisJPanel;
import com.nms.ui.ptn.ne.statusData.FrequencyStatusPanel;

public class FrequencyConfigPanle extends JPanel{
	


	private static final long serialVersionUID = 5960682360870159214L;
	
	private JTabbedPane tabbedPane = null;

	private GridBagLayout gridBagLayout = null;
	
	private FrequencyPanel frequencyPanel = null;
	private FrequencyStatusPanel frequencyStatusPanel = null;
	private FrequencyBasisJPanel frequencyBasisJPanel = null;
	public FrequencyConfigPanle() {

		try {
			init();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void init() throws Exception {

		try {
			
			tabbedPane = new JTabbedPane();
			gridBagLayout = new GridBagLayout();
			frequencyPanel = new FrequencyPanel();
			frequencyStatusPanel = new FrequencyStatusPanel();
			frequencyBasisJPanel = new FrequencyBasisJPanel();
			tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_FREQUENCY_MANAGE), frequencyPanel);
			setGridBagLayout();/* 主页面布局 */
			this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_FREQUENCY_MANAGE)));
			this.setLayout(gridBagLayout);
			this.add(tabbedPane);

		} catch (Exception e) {

			throw e;
		}
	}
	
	/**
	 * <p>
	 * 主页面布局
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void setGridBagLayout() throws Exception {

		GridBagConstraints gridBagConstraints = null;
		try {

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagLayout.setConstraints(tabbedPane, gridBagConstraints);
		} catch (Exception e) {

			throw e;
		} finally {

			gridBagConstraints = null;
		}
	}

}

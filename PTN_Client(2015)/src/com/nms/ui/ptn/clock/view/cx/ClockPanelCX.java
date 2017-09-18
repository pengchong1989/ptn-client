package com.nms.ui.ptn.clock.view.cx;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.clock.view.cx.time.TabPanelOneTCX;
import com.nms.ui.ptn.clock.view.cx.time.TabPanelTwoTCX;

public class ClockPanelCX extends JPanel{

	private static final long serialVersionUID = 5960682360870159214L;
	
	private JTabbedPane tabbedPane = null;

	private GridBagLayout gridBagLayout = null;
	
	private TabPanelOneTCX tabPanelOneTCX = null;
	private TabPanelTwoTCX tabPanelTwoTCX = null;
	
	public ClockPanelCX() {

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
			tabPanelOneTCX = new TabPanelOneTCX();
			tabPanelTwoTCX = new TabPanelTwoTCX();
			
			tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_NE_PTP_CONFIGURATION), tabPanelOneTCX);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_PORT_CONFIGURATION), tabPanelTwoTCX);
			setGridBagLayout();/* 主页面布局 */
			this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.LBL_TIME_MANAGEMENT)));
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

package com.nms.ui.ptn.basicinfo.dialog.group.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import com.nms.db.bean.system.Field;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.ptn.basicinfo.dialog.group.controller.GroupController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class GroupTablePanel extends ContentView<Field>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6767667975563031930L;
	private final JComboBox comboBox;

	public GroupTablePanel(JComboBox comboBox) {
		super("groupTable",RootFactory.TOPOLOGY_MANAGE);
		this.comboBox=comboBox;
		try {
			init();
			super.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void init(){
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_GROUP_TABLE)));
		initComponents();
		setLayout();
		
		
	}

	private void initComponents() {
	
	}

	private void setLayout() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);
		GridBagConstraints c = new GridBagConstraints();
		gridBagLayout.setConstraints(this.getContentPanel(), c);
		this.add(this.getContentPanel());
		
		
	}

	@Override
	public Dimension setDefaultSize() {
		return new Dimension(400, 200);
	}

	
	@Override
	public void setController() {
		controller = new GroupController(this);
		
	}
	
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getRefreshButton());
		return needRemoveButtons;
	}

	public JComboBox getComboBox() {
		return comboBox;
	}
	

}

package com.nms.ui.ptn.statistics.business;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import com.nms.db.bean.report.SSProfess;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.statistics.business.dialog.BussinessFilterDialog;



/**
 * 统计业务列表
 * @author sy
 *
 */
public class ProfessPanel  extends ContentView<SSProfess>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProfessPanel() {
		super("SSProfessPanel",RootFactory.COUNTMODU);
		init();
	}

    public void init()  {
    	
		try {
			getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_PROFESS_COUNT)));
			setLayout();
			this.getController().refresh();
		} catch (Exception e) {
			
		}
	}
	
	public void setLayout(){
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(getContentPanel(), c);
		this.add(getContentPanel());
		
	}
	

	@Override
	public void setController() {
		controller = new ProfessController(this);
	}
	public List<JButton> setAddButtons() {
		List<JButton> needAddButtons = new ArrayList<JButton>();		
		JButton filterJButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER), RootFactory.CORE_MANAGE);
		JButton clearFilterJButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR), RootFactory.CORE_MANAGE);	
		//设置过滤条件
		filterJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new BussinessFilterDialog(controller);
			}
		});
		//清除过滤条件
		clearFilterJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					((ProfessController)controller).getSiteIdList().clear();
					((ProfessController)controller).getPortIdList().clear();
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		needAddButtons.add(filterJButton);
		needAddButtons.add(clearFilterJButton);
		return needAddButtons;
	}
}

package com.nms.ui.ptn.statistics.lable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.nms.db.bean.report.SSLabel;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.statistics.lable.dialog.ShowUsedLabelDialog;
import com.nms.ui.ptn.statistics.lable.dialog.UsedLabelFilterDialog;

/**
 * 标签统计的界面
 * @author sy
 *
 */
public class LableInfoPanel extends ContentView<SSLabel>{
	private static final long serialVersionUID = 1L;

	public LableInfoPanel() {
		super("LableInfoPanel", RootFactory.COUNTMODU);
		try {
			this.init();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

    public void init() throws Exception {
    	getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_LABLE_INFO)));
		setLayout();
		this.getController().refresh();
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
		controller = new LableInfoController(this);
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needAddButtons = new ArrayList<JButton>();
		JButton selectJButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_QUERY_USED_LABEL), RootFactory.CORE_MANAGE);
		JButton filterJButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER), RootFactory.CORE_MANAGE);
		JButton clearFilterJButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR), RootFactory.CORE_MANAGE);
		selectJButton.addActionListener(new ActionListener() {
			//查询已用标签信息
			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					List<SSLabel> labelList = getAllSelect();
					if (labelList == null) {
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
					} else {
						if(labelList.size() == 1){
							new ShowUsedLabelDialog(labelList.get(0));
						}else{
							DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
						}
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		//设置过滤条件
		filterJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new UsedLabelFilterDialog(controller);
			}
		});
		//清除过滤条件
		clearFilterJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					((LableInfoController)controller).getSiteIdList().clear();
					((LableInfoController)controller).getPortIdList().clear();
					controller.refresh();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		needAddButtons.add(selectJButton);
		needAddButtons.add(filterJButton);
		needAddButtons.add(clearFilterJButton);
		return needAddButtons;
	}
}
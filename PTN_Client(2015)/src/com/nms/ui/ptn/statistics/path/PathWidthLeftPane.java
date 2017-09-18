package com.nms.ui.ptn.statistics.path;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import twaver.Node;
import twaver.SubNetwork;
import twaver.TDataBox;
import twaver.tree.TTree;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.user.UserField;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.system.FieldService_MB;
import com.nms.model.system.user.UserFieldService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.util.EquimentDataUtil;
import com.nms.ui.manager.util.TopologyUtil;
import com.nms.ui.manager.xmlbean.EquipmentType;

public class PathWidthLeftPane extends JPanel {

	private static final long serialVersionUID = -1267873479538816905L;
	private JLabel lblQuery;
	private JTextField txtQuery;
	private TDataBox box;
	private TTree tree;
	private JButton btnImport;
	private JScrollPane scrollPane;
	public  Field constField = null;
	
	public PathWidthLeftPane(){
		this.initComponents();
		this.setLayout();
		this.initData();
	}

	private void initData(){
		box.clear();
		initTree(box);
	}

	private void initComponents() {
		TopologyUtil topologyUtil=new TopologyUtil();
		lblQuery = new JLabel("快速查找");
		txtQuery = new JTextField();
		btnImport = new JButton(">");
		scrollPane = new JScrollPane();
		box = new TDataBox(ResourceUtil.srcStr(StringKeysObj.PTN_MANAGE_SYSTEM));
		tree = new TTree(box);
		tree.setTTreeSelectionMode(TTree.CHECK_CHILDREN_SELECTION);
		tree.setCheckableFilter(topologyUtil.FieldFilter);
		tree.getDataBox().getSelectionModel().clearSelection();
		scrollPane = new JScrollPane(tree);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(tree);
	}
	
	private void setLayout() {
		GridBagLayout c = new GridBagLayout();
		c.columnWidths = new int[] { 50,180,20 };
		c.columnWeights = new double[] { 0,0,0,0 };                              
		c.rowHeights = new int[] {10,540};
		c.rowWeights = new double[] {0,0,0,0,0};
		this.setLayout(c);
		
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.insets = new Insets(5,5,5,5);
		
		//第一行
		g.gridx = 0;
		g.gridy = 0;
		c.setConstraints(this.lblQuery, g);
		this.add(this.lblQuery);
		
		g.gridx = 1;
		c.setConstraints(this.txtQuery, g);
		this.add(this.txtQuery);
		
		//第二行
		g.gridx = 0;
		g.gridy = 1;
		g.gridwidth = 2;
		c.setConstraints(this.scrollPane, g);
		this.add(this.scrollPane);
		
		//第三列
		g.gridx = 2;
		g.gridy = 0;
		g.gridwidth = 1;
		g.gridheight = 2;
		g.fill = GridBagConstraints.NONE;
		c.setConstraints(this.btnImport, g);
		this.add(this.btnImport);
	}

	public TDataBox getBox() {
		return box;
	}

	public void setBox(TDataBox box) {
		this.box = box;
	}

	public TTree getTree() {
		return tree;
	}

	public void setTree(TTree tree) {
		this.tree = tree;
	}

	public  Field getConstField() {
		return constField;
	}

	public  void setConstField(Field constField) {
		this.constField = constField;
	}

	/**
	 * 创建过滤条件树
	 */
	public void initTree(TDataBox box) {
		UserFieldService_MB userFieldService = null;
		FieldService_MB fieldService = null;
		List<Field> fieldList = null;
		List<UserField> fields = null;
		List<Integer> ids = null;
		try {
			fieldService = (FieldService_MB)ConstantUtil.serviceFactory.newService_MB(Services.Field);
			userFieldService = (UserFieldService_MB)ConstantUtil.serviceFactory.newService_MB(Services.USERFIELD);
			fields = userFieldService.query(ConstantUtil.user.getUser_Id());

			if (fields.size() == 0 || fields == null) {
				fieldList = fieldService.select();
			} else {
				ids = new ArrayList<Integer>();
				for (int i = 0; i < fields.size(); i++) {
					ids.add(fields.get(i).getField_id());
				}
				fieldList = fieldService.selectfieldidByid(ids);
			}

			refreshTreeBox(this.getBox(), fieldList);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(userFieldService);
			UiUtil.closeService_MB(fieldService);
		}
	}

	/**
	 *  更新树节点
	 * @param box2
	 * @param fieldList
	 */
	private void refreshTreeBox(TDataBox box2, List<Field> fieldList) {
		SiteService_MB siteService = null;
		List<SiteInst> siteInstList = null;
		Node node = null;
		SubNetwork subNetwork = null;
		EquipmentType equipmentType = null;
		String path = null;
		EquimentDataUtil equimentDataUtil=new EquimentDataUtil();
		try {
			box.clear();
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			for (Field field : fieldList) {
				subNetwork = new SubNetwork();
				subNetwork.setName(field.getFieldName());
				subNetwork.setUserObject(field);
				box.addElement(subNetwork);

				SiteInst siteInst = new SiteInst();
				siteInst.setFieldID(field.getId());
				siteInstList = siteService.selectByFieldId(siteInst);

				for (int i = 0; i < siteInstList.size(); i++) {
					node = new Node();
					node.setName(siteInstList.get(i).getCellId() + "");
					node.setLocation(siteInstList.get(i).getSiteX(), siteInstList.get(i).getSiteY());
					node.setParent(subNetwork);

					equipmentType = equimentDataUtil.getEquipmentType(siteInstList.get(i).getCellType());

					if (null != equipmentType) {
						path = equipmentType.getImagePath();
					}

					node.setImage(path + "ne_0.png");
					node.setUserObject(siteInstList.get(i));
					box.addElement(node);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	public JLabel getLblQuery() {
		return lblQuery;
	}

	public void setLblQuery(JLabel lblQuery) {
		this.lblQuery = lblQuery;
	}

	public JTextField getTxtQuery() {
		return txtQuery;
	}

	public void setTxtQuery(JTextField txtQuery) {
		this.txtQuery = txtQuery;
	}

	public JButton getBtnImport() {
		return btnImport;
	}

	public void setBtnImport(JButton btnImport) {
		this.btnImport = btnImport;
	}
}

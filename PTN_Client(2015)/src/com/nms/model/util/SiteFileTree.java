package com.nms.model.util;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import twaver.Card;
import twaver.Element;
import twaver.Node;
import twaver.SubNetwork;
import twaver.TDataBox;
import twaver.VisibleFilter;
import twaver.tree.ElementNode;
import twaver.tree.TTree;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.bean.system.Field;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.equipment.slot.SlotService_MB;
import com.nms.model.system.FieldService_MB;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.util.EquimentDataUtil;
import com.nms.ui.manager.util.TopologyUtil;
import com.nms.ui.manager.xmlbean.EquipmentType;
import com.nms.ui.ptn.alarm.view.CurrentAlarmFilterDialog;

/**
 * 树 的 panel: 需要 (网元，板卡)复选框对象 网元、板卡 树
 * 
 * @author sy
 * 
 */
public class SiteFileTree extends JScrollPane {
	private static final long serialVersionUID = -6767156953328928386L;
	private TDataBox box;
	private TTree tree;
	private JComboBox cbObjectType; // 复选框（网元、板块）
	@SuppressWarnings("unused")
	private String lblPerforType;// 对象类型
	/**
	 * 长期性能任务 （新建） 在 tree的 事件中添加 标记 （自动命名是否选中）
	 */
	private JCheckBox cbAutoName;
	/**
	 * 长期性能任务 （新建） 在 tree选中时 更改 文本框的 内容
	 */
	private JTextField tfName;

	/**
	 * 实例化对象
	 * 
	 * @param cbObjectType
	 *            网元，板卡 复选框
	 * @param cbAutoName
	 *            历史性能 （新建） 自动命名 是否选中
	 * @param tfName
	 *            历史性能（新建） 选中树时，更新 自动命名 文本框的内容
	 */
	public SiteFileTree(JComboBox cbObjectType, JCheckBox cbAutoName, JTextField tfName) {
		this.cbAutoName = cbAutoName;
		this.tfName = tfName;
		this.cbObjectType = cbObjectType;
		init();
	}

	public void init() {
		initComponents();
		initData();
		addListener();
		addNodeListener();
	}

	private void initData() {
		box.clear();
		initTree(box);
	}

	public Element getElement(TreeExpansionEvent e) {
		TreePath path = e.getPath();
		if (path != null) {
			Object comp = path.getLastPathComponent();
			if (comp instanceof ElementNode) {
				ElementNode node = ((ElementNode) comp);
				return node.getElement();
			}
		}
		return null;
	}

	/**
	 * 树的 展开节点事件，（加以控制） TreeExpansionListener expoansionListener
	 */
	private TreeExpansionListener expoansionListener = new TreeExpansionListener() {
		@Override
		public void treeExpanded(TreeExpansionEvent event) {
			Element element = getElement(event);
			if (element != null) {
				if (element instanceof Node) {
					if (element.getParent() != null) {
						// 取出网元 节点，添加 板卡 （查 DB）
						if (element.getUserObject() instanceof SiteInst) {
							// 遍历此网元的子节点
							if (element.getChildren() != null && element.getChildren().size() > 0) {
								for (int i = 0; i < element.getChildren().size(); i++) {

									/**
									 * 删除 所有 子节点 并在 删除最后一个子节点时，添加 查询到的板块（通过网元）
									 */
									if (i == element.getChildren().size() - 1) {
										Node node = (Node) element;
										addCard(node);
										Element e = (Element) element.getChildren().get(i);
										tree.getDataBox().removeElement(e);
										i--;
										break;
									}
									Element e = (Element) element.getChildren().get(i);
									tree.getDataBox().removeElement(e);
									i--;
								}
							}

						}
						// 选择 域时
						else if (element.getUserObject() instanceof Field) {
							box.clear();
							initTree(box);
						}
					}
				}
			}
		}

		// 合并 时
		@Override
		public void treeCollapsed(TreeExpansionEvent event) {
		}
	};

	/**
	 * 添加 节点事件
	 */

	private void addNodeListener() {
		this.tree.addTreeExpansionListener(expoansionListener);
	}

	/**
	 * 添加 板卡
	 * 
	 * @param node
	 */
	public void addCard(Node node) {
		SiteInst site = (SiteInst) node.getUserObject();
		CardService_MB cardService = null;
		SlotInst slotInst = null;
		SlotInst soltinst = null;
		SlotService_MB slotService = null;
		List<CardInst> cardList = new ArrayList<CardInst>();
		List<SlotInst> slotList = new ArrayList<SlotInst>();
		try {
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			slotService = (SlotService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SLOT);
			CardInst cardInst = new CardInst();
			cardInst.setSiteId(site.getSite_Inst_Id());
			cardList = cardService.select(cardInst);
			if (cardList != null && cardList.size() > 0) {
				for (CardInst card_inst : cardList) {
					Card card = new Card();
					card.setSelected(false);
					card.setVisible(true);
					slotInst = new SlotInst();
					/*
					 * 通过 板卡 查询 Slot
					 */
					slotInst.setId(card_inst.getSlotId());
					slotList = slotService.select(slotInst);
					if (slotList != null && slotList.size() > 0) {
						soltinst = slotList.get(0);
					}
					if(soltinst.getBestCardName().equals("FAN")||soltinst.getBestCardName().equals("PSU")){
						
					}else{
						card.setUserObject(soltinst);
						card.setParent(node);
						card.setName(card_inst.getCardName());
						box.addElement(card);
					}
					
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(cardService);
			slotInst = null;
			UiUtil.closeService_MB(slotService);
		}
	}

	/**
	 * 显示不同的界面（并且 添加 子节点站位 ）
	 * 
	 * @param flag
	 *            false 板卡，端口 true 网元
	 * @throws Exception
	 */
	public void addTestNode(String name) throws Exception {
		@SuppressWarnings("unchecked")
		List<Element> elementList = this.tree.getDataBox().getAllElements();
		if (elementList != null && elementList.size() > 0) {
			// 遍历 tree中 的 元素
			for (Element element : elementList) {
				if (element instanceof Node) {
					// 为 网元 或者 板卡
					if (element.getParent() != null) {
						if (ResourceUtil.srcStr(StringKeysObj.NET_BASE).equals(name)) {
							// 存在 板卡时
							if (element.getChildren() != null && element.getChildren().size() > 0) {
								// 移除
								for (int j = 0; j < element.getChildren().size(); j++) {
									element.removeChild((Element) element.getChildren().get(j));
								}
							}
						} else {

							/**
							 * 为每一个网元添加 板卡
							 */
							Node node = (Node) element;
							addTest(node, box);
						}
					} else {
						if (ResourceUtil.srcStr(StringKeysObj.NET_BASE).equals(name)) {
							box.clear();
							initTree(box);
						}
					}
				}
			}

		}
	}

	/**
	 * 创建节点(站位，，展开事件可以使界面看不到这个子节点)
	 * 
	 * @param node
	 *            板卡父节点，即网元节点
	 * @param box
	 *            TDatabox
	 * @throws Exception
	 */
	private Node addTest(Node node, TDataBox box) throws Exception {
		Node testNode = new Node();
		testNode.setVisible(true);
		testNode.setParent(node);
		box.addElement(testNode);
		return testNode;
	}

	/**
	 * 补充 性能模块 ： 因为没有 选择框 事件 所以： 树直接显示到 端口 （所在的子节点）
	 */
	public void addPortInit() {
		TopologyUtil topologyUtil=new TopologyUtil();
		// 移除事件监听
		tree.removeTreeExpansionListener(expoansionListener);
		tree.setCheckableFilter(topologyUtil.CardAndPortFilter);
		tree.getDataBox().getSelectionModel().clearSelection();
		SiteFileTree.this.getVerticalScrollBar().setValue(0);
		lblPerforType = ResourceUtil.srcStr(StringKeysLbl.LBL_PORT);
		try {
			addTestNode(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT));

		} catch (Exception e) {
			ExceptionManage.dispose(e, SiteFileTree.class);
		}
		// 合并
		tree.collapseAll();
		// 添加 展开事件监听
		addNodeListener();
	}

	/**
	 * 网元、板卡 选择事件
	 */
	private void addListener() {
		if (cbObjectType != null) {
			cbObjectType.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent evt) {
					// 移除事件监听
					TopologyUtil topologyUtil=new TopologyUtil();
					tree.removeTreeExpansionListener(expoansionListener);
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						if (evt.getItem().equals(ResourceUtil.srcStr(StringKeysObj.NET_BASE))) {
							try {
								tree.setCheckableFilter(topologyUtil.SiteInstFilter);
								tree.getDataBox().getSelectionModel().clearSelection();
								// 设置 滚动条 恢复默认
								SiteFileTree.this.getVerticalScrollBar().setValue(0);
								// 添加子节点 站位（随意加一个子节点，一边能显示 +号）
								addTestNode(ResourceUtil.srcStr(StringKeysObj.NET_BASE));
							} catch (Exception e) {
								ExceptionManage.dispose(e, CurrentAlarmFilterDialog.class);
							}
						}
						// 选择 板卡
						else if (evt.getItem().equals(ResourceUtil.srcStr(StringKeysObj.BOARD))) {
							tree.setCheckableFilter(topologyUtil.CardFilter);
							tree.getDataBox().getSelectionModel().clearSelection();
							SiteFileTree.this.getVerticalScrollBar().setValue(0);
							lblPerforType = ResourceUtil.srcStr(StringKeysObj.BOARD);
							try {
								addTestNode(ResourceUtil.srcStr(StringKeysObj.BOARD));

							} catch (Exception e) {
								ExceptionManage.dispose(e, CurrentAlarmFilterDialog.class);
							}

						}
					}
					// 合并
					tree.collapseAll();
					// 添加 展开事件监听
					addNodeListener();
				}
			});

		}

		tree.addVisibleFilter(new VisibleFilter() {
			@Override
			public boolean isVisible(Element element) {
				if (element instanceof Card) {
					if (element.getName().equals("710_LEFT")) {
						return false;
					}
				}
				return true;
			}
		});
		/**
		 * 添加 长期性能 任务 树选中 更改 自动命名 ：事件
		 */
		tree.addTreeNodeClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cbAutoName != null) {
					if (cbAutoName.isSelected()) {
						StringBuilder str = new StringBuilder();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						if (box.getLastSelectedElement() instanceof Card) {
							Card card = (Card) box.getLastSelectedElement();
							Node node = (Node) card.getParent();
							str.append(node.getParent().getName()).append("_").append(node.getName()).append("_").append(card.getName()).append("/").append(sdf.format(new Date()).toString()).append("/").append(new Date().getTime());
							tfName.setText(str.toString());
						} else if (box.getLastSelectedElement() instanceof Node) {
							Node node = (Node) box.getLastSelectedElement();
							SubNetwork domain = (SubNetwork) node.getParent();
							str.append(domain.getName()).append("_").append(node.getName()).append("/").append(sdf.format(new Date()).toString()).append("/").append(new Date().getTime());
							tfName.setText(str.toString());
						} else {
							tfName.setText("");
						}

					}
				}

			}
		});
	}

	/**
	 * 验证输入数据的正确性
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean validateParams() {
		Iterator it = box.getSelectionModel().selection();
		if (!it.hasNext()) {
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_ALARMOBJ));
			return false;
		}
		return true;
	}

	private void initComponents() {
		TopologyUtil topologyUtil=new TopologyUtil();
		box = new TDataBox(ResourceUtil.srcStr(StringKeysObj.PTN_MANAGE_SYSTEM));
		tree = new TTree(box);
		tree.setRootVisible(false);
		tree.setTTreeSelectionMode(TTree.CHECK_CHILDREN_SELECTION);
		tree.setCheckableFilter(topologyUtil.SiteInstFilter);
		tree.getDataBox().getSelectionModel().clearSelection();
		this.add(tree);
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setViewportView(tree);
	}

	/**
	 * 创建过滤条件树
	 */
	public void initTree(TDataBox box) {
		FieldService_MB fieldService = null;
		List<Field> fieldList = null;
		try {
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			fieldList = fieldService.select();
			for (int i = 0; i < fieldList.size(); i++) {
				addDomain(box, fieldList.get(i), new Color(100, 100, 240));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(fieldService);
		}
	}

	/**
	 * 创建域节点，挂载在树上
	 * 
	 * @param box
	 * @param field
	 * @param color
	 */
	private void addDomain(TDataBox box, Field field, Color color) {
		SubNetwork domain = null;
		try {
			domain = new SubNetwork(field.getFieldName());
			domain.setName(field.getFieldName());
			domain.setLocation(field.getFieldX(), field.getFieldY());
			domain.putLabelColor(color);
			domain.setUserObject(field);
			box.addElement(domain);
			addSite(domain, box);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			domain = null;
		}
	}

	/**
	 * 根据域，创建网元节点，添加到box中
	 * 
	 * @param domain
	 *            域节点
	 * @param box
	 *            TDatabox
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void addSite(SubNetwork domain, TDataBox box) throws Exception {
		Field field = null;
		Node node = null;

		String path = null;
		List<String> strList = null;
		List<Element> elementList = null;
		SiteService_MB siteService = null;
		List<SiteInst> siteInstList = null;
		EquipmentType equipmentType = null;
		EquimentDataUtil equimentDataUtil=new EquimentDataUtil();
		try {
			strList = new ArrayList<String>();
			elementList = domain.getChildren();
			for (int i = 0; i < elementList.size(); i++) {
				strList.add(elementList.get(i).getID().toString());
			}

			for (int i = 0; i < strList.size(); i++) {
				box.removeElementByID(strList.get(i));
			}

			field = (Field) domain.getUserObject();
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			SiteInst siteInst = new SiteInst();
			siteInst.setFieldID(field.getId());
			siteInstList = siteService.selectByFieldId(siteInst);
			for (int i = 0; i < siteInstList.size(); i++) {
				node = new Node();
				node.setName(siteInstList.get(i).getCellId());
				node.setLocation(siteInstList.get(i).getSiteX(), siteInstList.get(i).getSiteY());
				node.setParent(domain);
				equipmentType = equimentDataUtil.getEquipmentType(siteInstList.get(i).getCellType());

				if (null != equipmentType) {
					path = equipmentType.getImagePath();
				}
				node.setImage(path + "ne_0.png");
				node.setUserObject(siteInstList.get(i));
				box.addElement(node);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 树的选中事件 长期性能） 只能选中一个
	 */
	@SuppressWarnings("unchecked")
	public void isSelect(DefaultMutableTreeNode element) {
		if (this.cbAutoName != null) {
			List<Element> elementList = this.box.getAllElements();
			try {
				if (elementList != null && elementList.size() > 0) {
					for (Element ment : elementList) {
						if (ment.equals(element)) {

							ment.setSelected(true);
						} else {
							ment.setSelected(false);
						}
					}
				}
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				elementList = null;
			}
		}

	}

	/**
	 * 获取所有选中的菜单
	 * 
	 * @return 选中的菜单集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getSelectObject() {
		List<Element> object = null;
		List<Element> elementList = null;
		try {
			object = new ArrayList<Element>();
			elementList = this.box.getAllElements();
			// 遍历box上面所有元素
			for (Element element : elementList) {
				// 如果元素为Node
				if (element instanceof Node) {
					// 如果是选中的，就放入结果集中。
					if (element.isSelected()) {
						object.add(element);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elementList = null;
		}
		return object;
	}

	public TDataBox getBox() {
		return box;
	}

	public void setBox(TDataBox box) {
		this.box = box;
	}

	public JCheckBox getCbAutoName() {
		return cbAutoName;
	}

	public void setCbAutoName(JCheckBox cbAutoName) {
		this.cbAutoName = cbAutoName;
	}

	public JTextField getTfName() {
		return tfName;
	}

	public void setTfName(JTextField tfName) {
		this.tfName = tfName;
	}

	public TTree getTree() {
		return tree;
	}

	public void setTree(TTree tree) {
		this.tree = tree;
	}

	public JComboBox getCbObjectType() {
		return cbObjectType;
	}

	public void setCbObjectType(JComboBox cbObjectType) {
		this.cbObjectType = cbObjectType;
	}

}

/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package layersGUI;



import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import addObjectMenus.ObjectAddingMenu;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import externalToolBar.AbstractExternalToolset;
import externalToolBar.InterfaceExternalTool;
import externalToolBar.ToolBarManager;
import graphicalObjects.FileStandIn;
import graphicalObjects.GraphicEncoder;
import graphicalObjects.FigureDisplayContainer;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects.LayerSpecified;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.ZoomableGraphicGroup;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupCloser;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.GraphicItemOptionsDialog;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.SelectionOperationsMenu;
import standardDialog.graphics.GraphicDisplayComponent;
import ultilInputOutput.ForDragAndDrop;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoReorder;
import utilityClassesForObjects.Hideable;
import utilityClassesForObjects.Selectable;
import utilityClassesForObjects.ShowsOptionsDialog;
import undo.UndoHideUnhide;

/**The user GUI for the layers window. This is a complex window with manipulations of layer structure 
  being done when the user drags and drops*/
public class GraphicTreeUI implements TreeSelectionListener,LayerSelector, DropTargetListener, ActionListener, MouseListener, WindowListener, LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>, MouseMotionListener {
	

	
	
	static ArrayList <MiscTreeOptions> otherOps=new ArrayList <MiscTreeOptions>();
	private FigureDisplayContainer graphicDisplayContainer;
	
	
	
	public JPanel createButtonPanel() {
		JPanel ButtonPanel=new JPanel(); 
		ButtonPanel.setLayout(new FlowLayout());
		ButtonPanel.add(upButton);
		ButtonPanel.add(downButton);

		return ButtonPanel;
	}
	
	static boolean operatorsMade=false;
	private static boolean treeDebugMode=false;
	
	TreeUtil<ZoomableGraphic> tu=new graphicTreeUtil();//new TreeUtil<ZoomableGraphic>();
	
	class graphicTreeUtil extends TreeUtil<ZoomableGraphic> {
		@Override
		public boolean doesNodeRepresentUserObject(TreeNode node, ZoomableGraphic o) {
			if (o==null) return false;
			if (node instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode n2=(DefaultMutableTreeNode) node;
				if (n2.getUserObject()==o) return true;
				if (n2.getUserObject() instanceof ZoomableGraphicGroup) {
					ZoomableGraphicGroup o2=(ZoomableGraphicGroup) n2.getUserObject() ;
					if (o2.getTheLayer()==o) return true;
					
				}
			}
			
			
			return false;
		}
	}

	public TreeUtil<ZoomableGraphic> branchOperation() {
		return tu;
	}
	
	{
		
		if (!operatorsMade) {
		
		otherOps.add(new FancyTree());
		
		operatorsMade=true;
		}
		}
	

	JButton upButton=null;
	JButton downButton=null; 

	
	{
		this.innitializeBMenuBut();
	}
	JPanel ButtonPanel= createButtonPanel(); 
	
	
	public JMenuItem createMenuItem(String text, String ActionCommand, Icon i) {
		JMenuItem jm = new JMenuItem(text, i);
		jm.addActionListener(this);
		jm.setActionCommand(ActionCommand);
		return jm;
	}
	
	public JButton createButtonItem(String text, String ActionCommand, Icon i) {
		JButton jb = new JButton(text, i);
		 jb.setBorderPainted(false);
	        
	       
		AbstractExternalToolset.stripButton(jb);
		jb.addActionListener(this);
		jb.setActionCommand(ActionCommand);
		return jb;
	}
	

	JMenuItem rButton=new JMenuItem("Refresh"); {rButton.addActionListener(this);rButton.setActionCommand("refresh");}
	JMenuItem dButton=new JMenuItem("Debugmode"); {dButton.addActionListener(this);dButton.setActionCommand("debug");}

	
	JMenuBar optionMenuBar=new JMenuBar();
	
	//JMenu basicMenu=new JMenu("Selected Item"); 
	JMenu optionMenu=new SmartJMenu("Options");
	
	
	{optionMenuBar.add(generateBasicMenu());
	
	optionMenuBar.add(getAddingMenu());
	optionMenu.add(rButton);
	optionMenu.add(dButton);
	for( MiscTreeOptions o:otherOps) {
		if (o==null) continue;
		JMenuItem jm = new JMenuItem(o.getMenuText()) ;
		jm.setActionCommand(o.getMenuText());
		jm.addActionListener(this);
		optionMenu.add(jm);
	}
	optionMenuBar.add(optionMenu);
	}
	
	public JMenu generateBasicMenu() {
		innitializeBMenuBut();
		JMenu basicMenu=  SelectionOperationsMenu.getStandardMenu(this);
		 ;
		
		return basicMenu;
	}
	
	public void innitializeBMenuBut() {

		
		upButton=createButtonItem(null, "up", getArrowIcon(true, false));
		upButton.setPressedIcon( getArrowIcon(true, true));
		downButton=createButtonItem(null, "down",getArrowIcon(false,false));
		downButton.setPressedIcon( getArrowIcon(false, true));
		
		
	}
	
	static ArrowGraphic createCartoonArrow(boolean swap, boolean selected) {
		Point p1=new Point(12,0);
		Point p2=new Point(12,30);
		
			ArrowGraphic ag1 =ArrowGraphic.createDefaltOutlineArrow(Color.red.darker(), Color.black);
			//ag1.setArrowHeadSize(10);
			if (selected) ag1.getBackGroundShape().setFillColor(Color.red);
			ag1.setPoints(p1, p2);
			if (swap) ag1.swapDirections();
			return ag1;
	}
	
	

	
	
	
	
	public GraphicDisplayComponent getArrowIcon(boolean b, boolean selected) {
		 GraphicDisplayComponent output = new GraphicDisplayComponent(createCartoonArrow(b,  selected));
		 output.setRelocatedForIcon(false);
		 //output.setSelected(selected);
		 return output;
	}
	
	
	
	
	JScrollPane pane=new JScrollPane();
	//pane.
	GraphicSetDisplayTree tree;
	Selectable s=null;
	TreePath lastPath=null;
	JFrame frame=null;
	private GridBagLayout layout;
	private GridBagConstraints cons;
	
	
	DefaultMutableTreeNode lastnode=null;
	DefaultMutableTreeNode masternode=null;
	private ZoomableGraphic selecteditem=null;
	private JPopupMenu currentpopup;
	private static GraphicSetDisplayTree lastGradTree;//the last tree clicked in
	
	
	public GraphicTreeUI(FigureDisplayContainer setcont) {
		setGraphicDisplayContainer(setcont);
	}
	
	public void closeWindow() {
		frame.setVisible(false);
	}
	
	public  GraphicSetDisplayTree makeTreeForSet(GraphicLayer set) {

		
		masternode = new DefaultMutableTreeNode(set, true);
		if (set.getTree()!=null) ((GraphicTreeUI) set.getTree()).closeWindow();
		set.setTree(this);
		
		for(ZoomableGraphic l: set.getItemArray()) {
			addGraphicToTreeNode(masternode, l);
		}
	
		 GraphicSetDisplayTree output = new GraphicSetDisplayTree(this.getGraphicDisplayContainer(), masternode) ;
		
		 output.setPreferredSize(new Dimension(300,500));
		return output;
	}
	
	public void showTreeForLayerSet(FigureDisplayContainer set) {
		showTreeForLayerSet(set, set.getTitle());
	}
	
	public void showTreeForLayerSet(FigureDisplayContainer set, String title) {
		this.setGraphicDisplayContainer(set);
		frame = new JFrame("Layer Tree "+title);
		frame.addWindowListener(this);
		frame.addMouseListener(new TreeFrameListener());
		
		refreshTreeFrame();
		

		
	}
	
	void refreshTreeFrame() {
		frame.remove(pane);
		frame.remove(ButtonPanel);
		tree = makeTreeForSet(getGraphicDisplayContainer().getTopLevelLayer());
		
		layout=new GridBagLayout();
		frame.setLayout(layout);
		
		
		new DropTarget(tree, this);
		pane=new JScrollPane(tree);
	//	pane.setPreferredSize(new Dimension(550,1800));
	//	pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		tree.setPreferredSize(new Dimension(280,1500));
		cons=new GridBagConstraints();
		cons.gridx=0;
		cons.gridy=0;
		frame.add(pane, cons);
		
		cons.gridy++;
		cons.anchor=GridBagConstraints.WEST;
		frame.add(ButtonPanel, cons);
		
		tree.addTreeSelectionListener(this);
		tree.setDragEnabled(true);
		tree.addMouseListener(this);
		tree.addMouseMotionListener(this);
		tree.addKeyListener(new TreeWindowKeyListener());
		
	
		frame.setJMenuBar(optionMenuBar);
		frame.pack();
		frame.setLocation(0, 280);
		
		frame.setVisible(true);
		
	}

	
	
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (e==null) return;
		setSelecteditem(null);
		lastPath=e.getNewLeadSelectionPath();
		if (lastPath==null) return;
		
		if (lastPath.getLastPathComponent() instanceof DefaultMutableTreeNode)
		lastnode=(DefaultMutableTreeNode) lastPath.getLastPathComponent();
		
		
		if (lastPath==null) return;
			Object o = tree.getItemForTreePath(lastPath);
			
			if (o==null) return;
			if (s!=null) s.deselect();
			
		
			if (o instanceof Selectable) {
				s=(Selectable)o;
				s.select();
			}
			if (o instanceof ZoomableGraphic) {
				setSelecteditem((ZoomableGraphic) o);
			}
			
		if (getGraphicDisplayContainer()==null ) return;
		getGraphicDisplayContainer().updateDisplay();
	}
	
	
public void addGraphicToTreeNode(DefaultMutableTreeNode t,ZoomableGraphic z) {
		
		if (z instanceof GraphicLayer) {
			
			GraphicLayer z1=(GraphicLayer) z;
			//IssueLog.log("adding layer "+z1+" to tree");
			
			DefaultMutableTreeNode node = branchOperation().getOrCreateChildWithUserObject(t, z, true);
			ArrayList<ZoomableGraphic> graphics2 = z1.getItemArray();
			
			for (ZoomableGraphic l: graphics2) {
				addGraphicToTreeNode(node, l);
			}
			return;
		}
		
		else if (z instanceof ZoomableGraphicGroup) {
			DefaultMutableTreeNode node = branchOperation().getOrCreateChildWithUserObject(t, z, true);
			ZoomableGraphicGroup z1=(ZoomableGraphicGroup) z;
			
ArrayList<ZoomableGraphic> graphics2 = z1.getTheLayer().getItemArray();
			
			for (ZoomableGraphic l: graphics2) {
				addGraphicToTreeNode(node, l);
			}
			//	addGraphicToTreeNode(node, z1.getTheLayer());
			
		}
		
		
		branchOperation().getOrCreateChildWithUserObject(t, z, false);
		
	}
	
	
	
	/**called when an item is added to a graphic container*/
	public void itemAddedToContainer( GraphicLayer gc, ZoomableGraphic z) {
		DefaultMutableTreeNode node = branchOperation().findDescendantWithuserObject(masternode, gc);
		addGraphicToTreeNode(node,z);
		DefaultTreeModel t=(DefaultTreeModel) tree.getModel();
		t.nodeStructureChanged(node);
		
	//	node = branchOperation().findDescendantWithuserObject(masternode, z);
		//tree.expandPath(branchOperation().getPath(node));//makes sure the item is visible in the tree
	}
	
	
	/**called when an item is added to a graphic container*/
	public void itemRemovedFromContainer( GraphicLayer gc, ZoomableGraphic z) {
		if (isTreeDebugMode())IssueLog.log("will update tree");
		DefaultMutableTreeNode node = branchOperation().findDescendantWithuserObject(masternode, gc);
		DefaultMutableTreeNode node2 = branchOperation().findDescendantWithuserObject(node, z);
		
		if (node2!=null) try {
			if (isTreeDebugMode())IssueLog.log("Removed tree node "+node2+"from "+"tree node "+node);
			node.remove(node2);
		DefaultTreeModel t=(DefaultTreeModel) tree.getModel();
		t.nodeStructureChanged(node);
		
		
		
		//tree.expandPath(branchOperation().getPath(node));//makes sure the item is visible in the tree
		
		} catch (Throwable t) {}
	}
	
	/**called when an item is added to a graphic container. updates the nodes*/
	public void itemsSwappedInContainer( GraphicLayer container, ZoomableGraphic z1, ZoomableGraphic z2 ) {
		
		DefaultMutableTreeNode node = branchOperation().findDescendantWithuserObject(masternode, container);
		
		DefaultMutableTreeNode nodez1 = branchOperation().findDescendantWithuserObject(masternode, z1);
		DefaultMutableTreeNode nodez2 =branchOperation().findDescendantWithuserObject(masternode, z2);
		if(this.isTreeDebugMode()) IssueLog.log("will try to swap tree nodes accordingly. "+"will swap "+nodez1+" with "+nodez2);
		if (node==null&&nodez1!=null) {
			node=(DefaultMutableTreeNode) nodez1.getParent();
		}
		if (nodez1==null||nodez2==null) return;
		int ind1=node.getIndex(nodez1);
		int ind2=node.getIndex(nodez2);
		node.insert(nodez1, ind2);
		node.insert(nodez2, ind1);
		
	
		
		DefaultTreeModel t=(DefaultTreeModel) tree.getModel();
		
		t.nodeStructureChanged(node);
		
	}
	

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dragExit(DropTargetEvent arg0) {
		
		
	}


	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		Point p1 = arg0.getLocation();
		TreePath tp = this.tree.getPathForLocation(p1.x, p1.y);
		
		tree.setIndicatorRect(tree.getPathBounds(tp));
		
	}




	public boolean willItemAcceptContainer(ZoomableGraphic destinatoinContainer, ZoomableGraphic itemtobeadded) {
		if (destinatoinContainer instanceof ZoomableGraphicGroup) {
			ZoomableGraphicGroup z=(ZoomableGraphicGroup) destinatoinContainer;
			return willItemAcceptContainer(z.getTheLayer(), itemtobeadded);
		}
		
		if (destinatoinContainer instanceof GraphicLayer && ((GraphicLayer)destinatoinContainer).canAccept(itemtobeadded)) return true;
		
		if (!(destinatoinContainer instanceof GraphicLayer) )  return false;
		else {
			GraphicLayer gc = (GraphicLayer)destinatoinContainer;
			if (gc.canAccept(destinatoinContainer)) return true;
			else return false;
		}
	}
	
	
	
	public void dropFromTree(DropTargetDropEvent arg0, GraphicSetDisplayTree  tree) {
	
		Point p1 = arg0.getLocation();
		TreePath tp = this.tree.getPathForLocation(p1.x, p1.y);
		 
		
		//ZoomableGraphic item = getItemForTreePath(lastPath);
		ArrayList<ZoomableGraphic> selectedGraphics = tree.getSelecteditems();
		
		TreePath[] selPaths = tree.getSelectionPaths();
		
		CombinedEdit undo = new CombinedEdit();
		
		/**What to do when a group of items are moved to the same destination.
		  The loops work in a specific order so that the original object order
		  is maintained*/
		if (destinationValidForAllObjects(selectedGraphics, tp)) {
					ZoomableGraphic itemAtDroplocation = tree.getItemForTreePath(tp);
					ZoomableGraphic item1 = selectedGraphics.get(0);//a representitive item
					/**determines what layer is appropriate for that object drop*/
					GraphicLayer destination = this.getDestinationLayerForObjectDrop(tp, item1);
					if (destination==null) return;//if no layer can accept the item, returns
					
					//if the item is already in the destination layer its index must be known to do an items swap
					int startIndex=destination.getItemArray().indexOf(item1);
					
					
									
					/**since the tree nodes might not be iterated in the order of the objects, this corrects the issue by creating an array in that order*/
					ArrayList<ZoomableGraphic> fixedOrder=new ArrayList<ZoomableGraphic>();
					ArrayList<ZoomableGraphic> all = this.getImageWrapper().getTopLevelLayer().getObjectsAndSubLayers();
					for(ZoomableGraphic g: all) {
						if (selectedGraphics.contains(g)) fixedOrder.add(g);
						
						/**if not selecting in group*/
						if (g instanceof ZoomableGraphicGroup) {
							ArrayList<ZoomableGraphic> all2 = ((ZoomableGraphicGroup) g).getTheLayer().getAllGraphics();
							for(ZoomableGraphic g2: all2) {
								if (selectedGraphics.contains(g2)) fixedOrder.add(g2);
								}
						}
						
					}
					
					/**if dropped on another item within the destination layer, its index must also be known*/
					int endingIndex = destination.getItemArray().indexOf(itemAtDroplocation);
					
							if ((startIndex<endingIndex &&startIndex!=-1 ) || endingIndex==-1) {
								for(int i=0; i<fixedOrder.size(); i++) {
									ZoomableGraphic z= fixedOrder.get(i);
									Object ob = dropItemOnDestination(z,destination, endingIndex, tree);
									if(ob instanceof UndoableEdit) undo.addEditToList((UndoableEdit) ob);
								}
							}
							else
								
								
								for(int i=fixedOrder.size()-1; i>=0; i--) {
									ZoomableGraphic z= fixedOrder.get(i);
									Object ob = dropItemOnDestination(z,destination, endingIndex, tree);
									if(ob instanceof UndoableEdit) undo.addEditToList((UndoableEdit) ob);
								}
			
		}
		
		else for(ZoomableGraphic z: selectedGraphics) {
			/**runs this loop if one of the objects has an invalid destination
			  possibly not needed. The program might still work  if 
			  this method just returned at this point but I chose not to test it. no time to*/
			Object ob = itemDroppedFromTree(z,tp, tree);
			if(ob instanceof UndoableEdit) undo.addEditToList((UndoableEdit) ob);
		}
		
		
		undo.setTree(tree);
		this.getGraphicDisplayContainer().getUndoManager().addEdit(undo);
		
		
		/**makes sure that destination paths are visible*/
		for(TreePath z: selPaths) {
			this.makePathVisible(z.getParentPath());
		}
		TreePath[] p2 = tree.getPathsForUserObjects(selectedGraphics);
		tree.setSelectionPaths(p2);

	}
	
/**when an item is dropped on a tree path, determines tha appropriate destination layer*/
	GraphicLayer getDestinationLayerForObjectDrop( TreePath tp, ZoomableGraphic item ) {
		GraphicLayer output=null;
		ZoomableGraphic itemAtDroplocation = tree.getItemForTreePath(tp);
		
			if (itemAtDroplocation instanceof ZoomableGraphicGroup) {
				ZoomableGraphicGroup group = ((ZoomableGraphicGroup) itemAtDroplocation);
				output=group.getTheLayer();
				/**if the group cannot accept the item, then refers it to a parent layer. 
				 since the top of the tree is always a layer that can accept anything, output should not be null*/
				while (!output.canAccept(item)) {
					output=output.getParentLayer();
				} 
				return output;
			} else
			/**if the destination layer cannot accept the item, it also differs to a parent layer. */
			if (itemAtDroplocation instanceof GraphicLayer) {
				output=(GraphicLayer) itemAtDroplocation;
				while (!output.canAccept(item)) {
					output=output.getParentLayer();
				} 
				return output;
			} else {
				if (itemAtDroplocation==null) return null;
						/**what to do if a non-layer is clicked*/
					output= itemAtDroplocation.getParentLayer();
						if (!output.canAccept(item)) {
							output=null;
						} 
				}
			
			return output;
	}
	
	/**Checks if the destination can accept every object in the list*/
	private boolean destinationValidForAllObjects(ArrayList<ZoomableGraphic> graphis, TreePath dropPath) {
		Object destination = getDestinationLayerForObjectDrop(dropPath, graphis.get(0));
		
		for( int i=1; i<graphis.size(); i++) {
			if (destination!=getDestinationLayerForObjectDrop(dropPath, graphis.get(i)) )
					return false;
		
		}
		return true;
	}
	
	/**Determines the layer that an item came from*/
	public GraphicLayer getSourceLayerOfItemDrop(ZoomableGraphic item) {
		GraphicLayer sourceLayer =item.getParentLayer();
		if (sourceLayer==null)	sourceLayer= tree.getContainerForTreePath(lastPath.getParentPath());
		 return sourceLayer;
	}
	
	
	
	public Object itemDroppedFromTree(ZoomableGraphic item, TreePath tp, GraphicSetDisplayTree sourceTree) {
		ZoomableGraphic itemAtDroplocation = tree.getItemForTreePath(tp);
		GraphicLayer destination = this.getDestinationLayerForObjectDrop(tp, item);
		if (destination==null) return null;
		int index = destination.getItemArray().indexOf(itemAtDroplocation);
	
		return dropItemOnDestination(item, destination, index, sourceTree);
	
	}
	
	public Object dropItemOnDestination(ZoomableGraphic item, GraphicLayer destination, int index, GraphicSetDisplayTree sourceTree) {
			GraphicLayer source = this.getSourceLayerOfItemDrop(item);
		if (destination==source&&source!=null) {
			return  moveItemPositionsForDrop(destination, item, index);
		}
		
		return  moveItemBetweenLayers(item, source, destination, sourceTree, index);
		
	}
	
	
	public Object itemDroppedFromTree2(ZoomableGraphic item, TreePath tp, GraphicSetDisplayTree sourceTree) {
		if (tp==null) return null;
		ZoomableGraphic itemAtDroplocation = tree.getItemForTreePath(tp);
		
		
		/**if the destination tree path is not a layer or teh layer cannot accept the item
		  do this*/
		if (!willItemAcceptContainer(itemAtDroplocation, item) &&sourceTree==tree ) {
			
			GraphicLayer destinationLayer = tree.getContainerForTreePath(tp.getParentPath());
			
			GraphicLayer sourceLayer =item.getParentLayer();
			if (sourceLayer==null)	sourceLayer= tree.getContainerForTreePath(lastPath.getParentPath());
			
			
			/**if destination and source are in same layer, just swaps the item positions*/
			if (destinationLayer==sourceLayer && isinstanceofContainer(destinationLayer)) {
				return  moveItemPositionsForDrop((GraphicLayer) destinationLayer, item, itemAtDroplocation);
			}
		} 

		return handleItemDropOntoNewLayer(item, tp, sourceTree) ;
		
		
	}
	
	/**Called when an item is dragged and dropped. method for placing the item in a new layer*/
	Object handleItemDropOntoNewLayer(ZoomableGraphic item, TreePath tp, GraphicSetDisplayTree source) {
		GraphicLayer destination = tree.getContainerForTreePath(tp);
		DefaultMutableTreeNode nodeForItem = branchOperation().findDescendantWithuserObject(source.masternode, item);	
		DefaultMutableTreeNode pnode = (DefaultMutableTreeNode) nodeForItem.getParent();
		
		
		GraphicLayer origin=null;
		if (pnode.getUserObject() instanceof GraphicLayer) {
				origin = (GraphicLayer) pnode.getUserObject() ;  }
		else if (pnode.getUserObject() instanceof ZoomableGraphicGroup){
			origin=((ZoomableGraphicGroup ) pnode.getUserObject()).getTheLayer();
		}
		
	
			
				Object object = moveItemBetweenLayers(item, origin, destination, source, 100);
				makePathVisible(tp);
				return object;
				}
	
	/***/
	Object moveItemPositionsForDrop(GraphicLayer gl, ZoomableGraphic item, int index/**ZoomableGraphic item2*/) {
		UndoReorder undo = new UndoReorder(gl);
		
		gl.moveItemToIndex(item, index);
		//gl.swapmoveObjectPositionsInArray(item, item2);
	
		undo.saveNewOrder();
		//this.getGraphicDisplayContainer().getUndoManager().addEdit(undo);
		tree.setSelectionPath(tree.getPathForUserObject(item));
		
	
		
		return undo;
	}
	
	/***/
	Object moveItemPositionsForDrop(GraphicLayer gl, ZoomableGraphic item, ZoomableGraphic item2) {
		UndoReorder undo = new UndoReorder(gl);
		gl.swapmoveObjectPositionsInArray(item, item2);
		undo.saveNewOrder();
		tree.setSelectionPath(tree.getPathForUserObject(item));
		
	
		
		return undo;
	}
	
	/**Moves an item between an origin folder and a destination folder
	 * @param index */
	Object moveItemBetweenLayers(ZoomableGraphic item, GraphicLayer origin, GraphicLayer destination, GraphicSetDisplayTree source, int index) {
		
		if (item==destination||item==origin) return null;
		
		if (origin==destination) return null;
		if (destination==null) return null;
		if (origin==null) return null;
		
		try{
			if (!destination.canAccept(item)) return null;
			if (!origin.canRelease(item)) return null;
			CombinedEdit undo2 = new CombinedEdit();
			undo2.addEditToList(new UndoAbleEditForRemoveItem(origin, item, tree));
			undo2.addEditToList(new UndoAddItem(destination, item, tree));
			
		origin.remove(item);
		removeKey(item);
		destination.add(item);
		
		Object object = moveItemPositionsForDrop(destination, item, index);
		if (object instanceof UndoableEdit) undo2.addEditToList((UndoableEdit) object);
		
		
		//this.getGraphicDisplayContainer().getUndoManager().addEdit(undo2);
		//makePathVisible(lastPath);
		
		
		if (source!=tree) {
			this.getGraphicDisplayContainer().onItemLoad(item);//moved the item in between trees
			
			/**updates both displays*/
			if (graphicDisplayContainer!=null)graphicDisplayContainer.updateDisplay();		
			if (source instanceof GraphicSetDisplayTree) {
				source.getSetDisplayContainer().updateDisplay();
			}
			
			IssueLog.log("Warning: When doing transfers between images trees"+" make sure to also transfer locked and linked items as well", "failure to do so can result in problems");	
		}
		
		//if (isTreeDebugMode())IssueLog.log("Item "+item+" will be removed from "+origin+" and put in "+destination);
		
		return undo2;
		
		} catch (Throwable r) {IssueLog.logT(r);}
		
		return null;
	}
	
	
	/**when given a tree path, makes sure the nodes are expanded*/
	void makePathVisible(TreePath tp) {
		ZoomableGraphic item2 = tree.getItemForTreePath(tp);
		if(item2==null||tp==null) return;
		if(item2 instanceof GraphicLayer)
		tree.expandPath(tp);//so the new location will be visible
		else if(tp.getParentPath()!=null) tree.expandPath(tp.getParentPath());
	}
	
	boolean isinstanceofContainer(Object c1) {
		return c1 instanceof GraphicLayer||c1 instanceof ZoomableGraphicGroup;
	}
	
	public void fireModeStructureChange(DefaultMutableTreeNode node) {
		if (tree==null) return;
		DefaultTreeModel t=(DefaultTreeModel) tree.getModel();
		t.nodeStructureChanged(node);
		
		
	}
	
	public void fireNodeStructureChange(ZoomableGraphic node) {
		fireModeStructureChange(
		branchOperation().findDescendantWithuserObject(masternode, node)
						);
		
	}


	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	


	void redoDisplay() {
		addGraphicToTreeNode(masternode, getGraphicDisplayContainer().getTopLevelLayer());
		fireModeStructureChange(masternode);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try{
		
		
		if (arg0.getActionCommand().equals("refresh")) {
			/**this updates the tree to include the current contents of the set*/
			redoDisplay();
				}
		if (arg0.getActionCommand().equals("debug")) {
			
			setTreeDebugMode(!isTreeDebugMode());
		IssueLog.log(isTreeDebugMode()? "tree is now in debugmode": "Tree is out of debug mode");
				}
	
		if (arg0.getActionCommand().equals("delete")) {
			if (lastPath==null) return;
			TreePath p = lastPath;
			DefaultMutableTreeNode originnode = branchOperation().getNodeforPath(lastPath.getParentPath());
			
			//swapnode);
			fireModeStructureChange(originnode);
			tree.setSelectionPath(p);
		}
		
		
		if (arg0.getActionCommand().equals("up")) {
			if (lastPath==null) return;
			TreePath p = lastPath;
			DefaultMutableTreeNode originnode = branchOperation().getNodeforPath(lastPath.getParentPath());
			ZoomableGraphic swapnode = tree.getItemForTreeNode((DefaultMutableTreeNode)originnode.getChildBefore(lastnode));
			if (originnode==null||swapnode==null) return;
			if (isTreeDebugMode()) IssueLog.log("Recieved command for Items "+selecteditem+" and "+swapnode+" to change places");
		GraphicLayer gc = tree.getContainerForTreePath(lastPath.getParentPath());
		gc.swapmoveObjectPositionsInArray( swapnode, selecteditem); 
			tree.setSelectionPath(lastPath);
			//swapnode);
			fireModeStructureChange(originnode);
			tree.setSelectionPath(p);
		}
		
		if (arg0.getActionCommand().equals("down")) {
			if (lastPath==null) return;
			TreePath p = lastPath;
			DefaultMutableTreeNode originnode = branchOperation().getNodeforPath(lastPath.getParentPath());
			if (originnode==null) return;
			ZoomableGraphic swapnode = tree.getItemForTreeNode((DefaultMutableTreeNode)originnode.getChildAfter(lastnode));
			if (swapnode==null) return;
			if (isTreeDebugMode()) IssueLog.log("Item "+selecteditem+" and "+swapnode+" will change places");
			GraphicLayer gc = tree.getContainerForTreePath(lastPath.getParentPath());
			gc.swapmoveObjectPositionsInArray( swapnode, selecteditem); 
			tree.setSelectionPath(lastPath);
		
			fireModeStructureChange(originnode);
			tree.setSelectionPath(p);
		}
	
		
	for (MiscTreeOptions ad:otherOps){
			 if (ad.getMenuText().equals(arg0.getActionCommand())) try{
				 ad.run();
				 
				 
			 } catch (Throwable t) {}
		
		}

		getGraphicDisplayContainer().updateDisplay();
		
		} catch (Throwable r) {
			IssueLog.log("problem", r);
		}
	}
	
	


	
	public GraphicLayer getSelectedLayer() {
		if (getSelecteditem()==null) return getGraphicDisplayContainer().getTopLevelLayer();
		if (getSelecteditem() instanceof GraphicLayer) return (GraphicLayer) getSelecteditem();
		
		if (lastPath!=null ) {
			ZoomableGraphic parent = tree.getItemForTreePath(lastPath.getParentPath());
		
			if (parent instanceof GraphicLayer) return (GraphicLayer) parent;
		}
		
		
		return getGraphicDisplayContainer().getTopLevelLayer();
		}


	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		Point p1=new Point(arg0.getX(), arg0.getY());
		ZoomableGraphic z = tree.getItemForPoint(p1);
		//Object source = arg0.getSource();
	
		
		boolean triggerpop=arg0.getButton()==MouseEvent.BUTTON3||arg0.isPopupTrigger()||arg0.isControlDown();
		
		if (arg0.getClickCount()==2&&!triggerpop) {
			//RectangleDialog.setGraphicSetContainer(getGraphicDisplayContainer());
			
			if (z instanceof ShowsOptionsDialog) {
				ShowsOptionsDialog so=(ShowsOptionsDialog) z;
				so.showOptionsDialog();
			}
		}
		
		
		//if (triggerpop) IssueLog.log("popup triggered");
		if (triggerpop&&  z instanceof HasUniquePopupMenu)try  {
			//IssueLog.log("will attempt to display menu");
			HasUniquePopupMenu so=(HasUniquePopupMenu) z;
			currentpopup = so.getMenuSupplier().getJPopup();
			if (currentpopup instanceof SmartPopupJMenu) {
				((SmartPopupJMenu) currentpopup).setUndoManager(getGraphicDisplayContainer().getUndoManager());
			}
			//tree.add(pp);
			new PopupCloser(currentpopup);
			currentpopup.show((Component) tree, arg0.getX(), arg0.getY());
		} catch (Throwable t) {IssueLog.logT(t);}
		getGraphicDisplayContainer().updateDisplay();
		frame.repaint();
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		//GregGd.setGraphicSetContainer(getGraphicDisplayContainer());
		GraphicItemOptionsDialog.setSetContainer(getGraphicDisplayContainer());
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		tree.setIndicatorRect(null);
		tree.repaint();
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
	
		TreePath tp =  tree.getPathForMouseEvent(arg0) ;
		Rectangle bounds = tree.getPathBounds(tp);
		ZoomableGraphic z =tree. getItemForMouseEvent(arg0);
		
		if (bounds==null) return;
		if (arg0.getX()<bounds.x+14) {
			if (z instanceof Hideable) {
				Hideable h=(Hideable) z;
				
				UndoHideUnhide undo = new UndoHideUnhide(h, !h.isHidden());
				undo.setTree(tree);
				h.setHidden(!h.isHidden());
				
				this.getGraphicDisplayContainer().getUndoManager().addEdit(undo);
				
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public JMenu getAddingMenu() {
		return ObjectAddingMenu.getStandardAddingMenu(this);
	}
	
	
	void removeKey(ZoomableGraphic z) {
		if (z instanceof LayerSpecified) {
			LayerSpecified l=(LayerSpecified)z;
			l.setLayerKey(null);
		}
	}
	
	

	@Override
	public void drop(DropTargetDropEvent arg0) {
		//IssueLog.log("Drop done");
		tree.setIndicatorRect(null);
		if(arg0.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			//IssueLog.log("Drop is file list ");
			ArrayList<File> files = ForDragAndDrop.dropedFiles(arg0, true);
			GraphicLayer cont = tree.getContainerForPoint(arg0.getLocation());
			if (files.size()>0) {
				for(File f: files) {
					addFileImageGraphic(f, cont);
				}
				
			}
			getGraphicDisplayContainer().updateDisplay();
			return;
			}
		
		GraphicSetDisplayTree sourcetree=lastGradTree;
		if (sourcetree==null) sourcetree=this.tree;

		if (arg0.getDropTargetContext().getComponent() instanceof GraphicSetDisplayTree)
			this.dropFromTree(arg0, sourcetree);
		
	}
	
	public void addFileImageGraphic(File f, GraphicLayer cont) {
		
		boolean fileAdded=false;
		
		IssueLog.log("dropped file "+f.getAbsolutePath());;
		if (f.getAbsolutePath().endsWith(".gra")) try {
			addSavedGraphic(f, cont);
			fileAdded=true;
			return;
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		if (!f.isDirectory()) try {
				
				ImagePanelGraphic g = new ImagePanelGraphic(f);
				if (g.isFilefound()) {
				
					cont.add(g);
					fileAdded=true;
					g.setLocationUpperLeft(0, 0);
					//this.addGraphicToTreeNode(branchOperation().findDescendantWithuserObject(masternode, cont), g);
					return;
				} else {
					IssueLog.log("Cannot open file as image: "+f);
					return;
				}
		} catch (Throwable t) {
			IssueLog.log("Cannot open file as image: "+f);
		}
		
		IssueLog.log("Adding file reference to image "+f);
		if (!fileAdded) FileStandIn.addFileStandIn(f,cont);
		
	}
	

	/***/
	public void addSavedGraphic(File f, GraphicLayer gc) {
		GraphicEncoder ag = new GraphicEncoder(gc);
		
		ZoomableGraphic ob = ag.readGraphicFromFile(f.getAbsolutePath());
	
		gc.add(ob);
		getGraphicDisplayContainer().onItemLoad(ob);
	}

	public ZoomableGraphic getSelecteditem() {
		return selecteditem;
	}

	public void setSelecteditem(ZoomableGraphic selecteditem) {
		this.selecteditem = selecteditem;
		
		InterfaceExternalTool<DisplayedImage> tool = ToolBarManager.getCurrentTool();
		if(tool!=null)
			try {
				tool.userSetSelectedItem(selecteditem);
			} catch (Exception e) {
				IssueLog.logT(e);
			}
	
	}



	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	




	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowClosing(WindowEvent e) {
		if (e.getWindow()==frame) getGraphicDisplayContainer().getTopLevelLayer().treeEliminated();
		
	}



	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		//GraphicLayerPane gpane = new GraphicLayerPane("");
		setTreeDebugMode(true);
		GraphicCellRenderer.setTreeDebug(true);
		
	}

	public boolean isTreeDebugMode() {
		return treeDebugMode;
	}

	public static void setTreeDebugMode(boolean treeDebugMode) {
		GraphicTreeUI.treeDebugMode = treeDebugMode;
	}
	
	public class TreeFrameListener implements WindowListener, MouseListener {

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			if (currentpopup!=null) currentpopup.setVisible(false);
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			if (currentpopup!=null) currentpopup.setVisible(false);
			tree.setIndicatorRect(null);
			tree.repaint();
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		lastGradTree=this.tree;
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<ZoomableGraphic> getSelecteditems() {
		// TODO Auto-generated method stub
		return tree.getSelecteditems();
	}

	public FigureDisplayContainer getGraphicDisplayContainer() {
		return graphicDisplayContainer;
	}

	public void setGraphicDisplayContainer(FigureDisplayContainer graphicDisplayContainer) {
		this.graphicDisplayContainer = graphicDisplayContainer;
	}
	
	
	
	public class TreeWindowKeyListener implements  KeyListener {

				@Override
				public void keyReleased(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
		
				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
					/**implementation of undo and redo*/
					UndoManager undo = getGraphicDisplayContainer() .getUndoManager();
					boolean meta=arg0.isMetaDown();
					if (IssueLog.isWindows()) meta=arg0.isControlDown();
			 		if (arg0.getKeyCode()==KeyEvent.VK_Z&&meta) {
			 			
						if (undo .canUndo())undo .undo();
					}
			 		
					if (arg0.getKeyCode()==KeyEvent.VK_Y&&meta) {
								
								if (undo .canRedo())undo .redo();
							}
					
					getGraphicDisplayContainer().updateDisplay();
					if (KeyEvent.VK_ESCAPE==arg0.getKeyCode()) {closeWindow();}
				}
		
				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}}

	@Override
	public ImageWrapper getImageWrapper() {
		return getGraphicDisplayContainer().getAsWrapper();
	}
	
	
	
	
	
}

package layersGUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import graphicalObjects.FigureDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.ZoomableGraphicGroup;
import logging.IssueLog;

/**The JTree for the tree UI*/
public class GraphicSetDisplayTree extends JTree {

	/**
	 * 
	 */
TreeUtil<ZoomableGraphic> tu=new TreeUtil<ZoomableGraphic>();
	

	public TreeUtil<ZoomableGraphic> branchOperation() {
		return tu;
	}
	
	private static final long serialVersionUID = 1L;
	private FigureDisplayContainer setDisplayContainer;

	 DefaultMutableTreeNode masternode;
	GraphicLayer baseLayer;
	
	
	public GraphicSetDisplayTree(FigureDisplayContainer cont, DefaultMutableTreeNode masternode) {
		super(masternode, true);
	
		this.masternode=masternode;
		 baseLayer=cont.getGraphicLayerSet();
		 setDisplayContainer=cont;
		if (TreeMode.fancy)this.setRowHeight(0);
		setCellRenderer(new GraphicCellRenderer());
		
	
	}

	
	public DefaultMutableTreeNode makeMasterNode(GraphicLayer baseLayer) {
		this.baseLayer=baseLayer;
		masternode = new DefaultMutableTreeNode(baseLayer, true);
		return masternode ;
	}
	

	/**when  given a tree path, this returns the last zoomable graphic*/
	public ZoomableGraphic getItemForTreePath(TreePath p) {
			DefaultMutableTreeNode tn= branchOperation().getNodeforPath( p);
			if (tn==null) return null;
			return getItemForTreeNode(tn);
}
	
	
	public ZoomableGraphic getItemForTreeNode(DefaultMutableTreeNode tn) {
		if (tn==null) return null;
		Object s2 = tn.getUserObject();
		if (s2 instanceof ZoomableGraphic) {
			ZoomableGraphic output = (ZoomableGraphic)s2;
			return output;
		}
		else return null;
	}
	
	
	/**when  given a tree path, this returns the last zoomable graphic*/
	public GraphicLayer getContainerForTreePath(TreePath p) {
		Object o=p.getLastPathComponent();
		if (o instanceof DefaultMutableTreeNode) {
			
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode) o;
			Object s2 = tn.getUserObject();
			if (s2 instanceof GraphicLayer) {
				GraphicLayer output = (GraphicLayer)s2;
				return output;
			}
			else if 
			 (s2 instanceof ZoomableGraphicGroup) {
				ZoomableGraphicGroup output = (ZoomableGraphicGroup)s2;
				return output.getTheLayer();
			}
			
			else {
				GraphicLayer gc2 = getContainerForTreePath(p.getParentPath());
				if (gc2!=null) return gc2; else return null;
			}
			
	} else return null;

}
	
	public ZoomableGraphic getItemForPoint(Point p1) {
		TreePath tp = getPathForLocation(p1.x, p1.y);
		ZoomableGraphic item = getItemForTreePath(tp);
		return item;
	}
	
	public GraphicLayer getContainerForPoint(Point p1) {
		TreePath tp = getPathForLocation(p1.x, p1.y);
		GraphicLayer destination = this.getContainerForTreePath(tp);
		return destination;
	}
	
	public TreePath getPathForMouseEvent(MouseEvent arg0) {
		Point p1=new Point(arg0.getX(), arg0.getY());
		TreePath tp = getPathForLocation(p1.x, p1.y);
		return tp;
	}
	public ZoomableGraphic getItemForMouseEvent(MouseEvent arg0) {
		Point p1=new Point(arg0.getX(), arg0.getY());
		return  this.getItemForPoint(p1);
	}
	
	/**returns all selected zoomable graphics*/
	public ArrayList<ZoomableGraphic> getSelecteditems() {
		ArrayList<ZoomableGraphic> output = new ArrayList<ZoomableGraphic>();
	
		TreePath[] paths = getSelectionPaths();
		if (paths==null||paths.length==0) return output;
		for(TreePath path:paths) {
			output.add(this.getItemForTreePath(path));
		}
		return output ;
		
	}
	
	public String toString() {
		return "Display for "+getSetDisplayContainer();
	}
	
	private HashMap<ZoomableGraphic, TreePath> getMap() {
		HashMap<ZoomableGraphic, TreePath> output=new HashMap<ZoomableGraphic, TreePath>();
		for(int i=0; i<getRowCount(); i++) {
			TreePath tp = this.getPathForRow(i);
			ZoomableGraphic item = this.getItemForTreePath(tp);
			output.put(item, tp);
		}
		
		
		return output;
	}
	
	
	/**gets the tree paths for the relevant user objects*/
	TreePath getPathForUserObject(Object z) {
		return getMap().get(z);
	}
	
	/**Adds the given used object to the selection*/
	public void addUserObjectsToSelection(ArrayList<ZoomableGraphic> addeds) {
		for(ZoomableGraphic added: addeds) {
			addUserObjectToSelection(added);
		}
	}
	
	public void addUserObjectToSelection(ZoomableGraphic added) {
		
		TreePath path = getPathForUserObject(added);
		if (path==null) path=branchOperation().findPathWithUserObject(this.masternode, added);
		IssueLog.log("Expanding path "+path);
		this.addSelectionPath(path);
		this.expandPath(path);
		
	}
	public void expandPathForUserObject(ZoomableGraphic added) {
		TreePath  path=branchOperation().findPathWithUserObject(this.masternode, added);
		this.expandPath(path);
	}
	
	TreePath[] getPathsForUserObjects(ArrayList<ZoomableGraphic> z) {
		HashMap<ZoomableGraphic, TreePath> map2 = getMap();
		TreePath[] output = new TreePath[z.size()];
		for(int i=0; i<z.size(); i++) {output[i]=map2.get(z.get(i));}
		return output;
	}
	public FigureDisplayContainer getSetDisplayContainer() {
		return setDisplayContainer;
	}

	
	
	/**
	public static void main(String[] args) {
		ImageAndDisplaySet tt = ImageDisplayTester.showExample(true);
		
		GraphicSetDisplayTree gt = ImageAndDisplaySet.exampletree.tree;
		TreePath tp=gt.getPathForRow(1);
		gt.expandPath(tp);
		tp=gt.getPathForRow(1);
		
		
		
		
		for(int i=0; i<gt.getRowCount(); i++) {
			IssueLog.log(gt.getPathForRow(i));
		}
	
	}*/
	
	
	boolean transferTest=true;
	private Rectangle rect;
	public TransferHandler getTransferHandler() {
		if (!transferTest) return super.getTransferHandler();
		return new GraphicTreeTransferHandler(super.getTransferHandler(), this);
	}
	
	public void setIndicatorRect(Rectangle pathBounds) {
		if (rect!=null&&!rect.equals(pathBounds)) repaint();
		if (rect==null &&pathBounds!=null) repaint();
		this.rect=pathBounds;
		
	}
	
	/**Paints a rectangle on the location that is specified by the field
	  I use this as an indicator when dragging a tree item around so the user can 
	  see where he is targetting*/
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		if (rect!=null) {
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
			
		}
	}
	


}

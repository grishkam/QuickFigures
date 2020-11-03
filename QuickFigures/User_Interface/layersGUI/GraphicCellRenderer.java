package layersGUI;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import graphicalObjects_BasicShapes.TextGraphic;
import utilityClassesForObjects.Hideable;

/**a tree cell rendered that will display different icons depending on whether 
  the objects in the default mutable tree nodes implement a certain interface*/
public class GraphicCellRenderer implements TreeCellRenderer  {
	
	/**
	 * 
	 */
	static boolean useEyeIcon=true;
	private static boolean treeDebug=false;
	//Icon oriIcon=this.getLeafIcon();
	//{this.setLeafIcon(hasTreeIcon.defaultLeaf);}

	public Component  	getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		TreeCellRenderer ren = getUniqueRenderer(value);
		
		if (ren==null&&TreeMode.fancy) {return this.getPanel(tree, value, selected, expanded, leaf, row, hasFocus);}
		//if (ren==null&&!leaf &&TreeMode.fancy) {return this.getPanel(tree, value, selected, expanded, leaf, row, hasFocus);}
		
		
		if (ren!=null) return ren.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
	
			
		Component output = new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	
		 return output;
	}
	
	
TextGraphicListCellComponent getPanel(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	
	//TreeCellPanel panel = new TreeCellPanel(value.toString(), selected, useEyeIcon);
	TextGraphicListCellComponent panel=new TextGraphicListCellComponent(value.toString(), selected);
	panel.setIcon(HasTreeLeafIcon.defaultLeaf);
	if (!leaf) {
		if (expanded) {
			panel.setIcon(HasTreeBranchIcon.defaultLeaf);
		}
		else panel.setIcon(HasTreeBranchIcon.defaultLeaf2);
	}
	if (value instanceof DefaultMutableTreeNode) {
		DefaultMutableTreeNode node=(DefaultMutableTreeNode) value;
		
		if (leaf&& (node.getUserObject() instanceof HasTreeLeafIcon)) {
			HasTreeLeafIcon t=(HasTreeLeafIcon) node.getUserObject() ;
			panel.setIcon(t.getTreeIcon());
		}
		if (!leaf&& (node.getUserObject() instanceof HasTreeBranchIcon)) {
			HasTreeBranchIcon t=(HasTreeBranchIcon) node.getUserObject() ;
			if (expanded) {
				panel.setIcon(t.getTreeIcon(true));
			}
			else panel.setIcon(t.getTreeIcon(false));
		}
		
		
		if (node.getUserObject() instanceof TextGraphic)panel.setToImmitate((TextGraphic) node.getUserObject());
		if (node.getUserObject() instanceof Hideable) {
			Hideable h=(Hideable) node.getUserObject();
			//panel.getVisibilityCheckBox().setSelected(h.isHidden());
			if (h.isHidden()) panel.setIcon(HasTreeLeafIcon.defaultHiddenLeaf);
		}
	
	}
panel.setMinimumWidth(100);
	
	return panel;
	
}
	
	public Icon getLeafIconForValue(Object value) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode v=(DefaultMutableTreeNode) value;
			if (v.getUserObject() instanceof HasTreeLeafIcon) {
				return ((HasTreeLeafIcon)v.getUserObject() ).getTreeIcon();
			} else return null;
		}
		return  	null;
	}
	
	public TreeCellRenderer getUniqueRenderer(Object value) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode v=(DefaultMutableTreeNode) value;
			if (v.getUserObject() instanceof HadOwnCellRenderer) {
				return ((HadOwnCellRenderer)v.getUserObject() ).getCellRenderer();
			} else return null;
	}
		return null;
	
	
	
	}
	
	public static boolean isTreeDebug() {
		return treeDebug;
	}
	public static void setTreeDebug(boolean treeDebug) {
		GraphicCellRenderer.treeDebug = treeDebug;
	}


	

	
	
	
}


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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import graphicalObjects_SpecialObjects.TextGraphic;
import utilityClassesForObjects.Hideable;

/**a tree cell renderer that will display different icons depending on whether 
  the objects in the default mutable tree nodes implement a certain interface*/
public class GraphicCellRenderer implements TreeCellRenderer  {
	
	/**
	 * 
	 */
	static boolean useEyeIcon=true;
	
	/**set to true if programmer sants a simplified tree */
	private static boolean treeDebug=false;

	public Component  	getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		TreeCellRenderer ren = getUniqueRenderer(value);
		
		if (ren==null&&TreeMode.fancy) {return this.getPanel(tree, value, selected, expanded, leaf, row, hasFocus);}
		
		if (ren!=null) return ren.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		

		Component output = new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	
		 return output;
	}
	
	/**returns a list cell component for graphical objects*/
TextGraphicListCellComponent getPanel(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
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
		
		
		if (node.getUserObject() instanceof TextGraphic)
			panel.setToImmitate((TextGraphic) node.getUserObject());
		if (node.getUserObject() instanceof Hideable) {
			Hideable h=(Hideable) node.getUserObject();
			if (h.isHidden()) panel.setIcon(HasTreeLeafIcon.defaultHiddenLeaf);// hidden objects are displayed with a red X
		}
	
	}
	panel.setMinimumWidth(100);
	
	return panel;
	
}
	/**returns the tree leaf icon for displaying the object*/
	public Icon getLeafIconForValue(Object value) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode v=(DefaultMutableTreeNode) value;
			if (v.getUserObject() instanceof HasTreeLeafIcon) {
				return ((HasTreeLeafIcon)v.getUserObject() ).getTreeIcon();
			} else return null;
		}
		return  	null;
	}
	
	/**returns the tree cell renderer if there is a special one*/
	public TreeCellRenderer getUniqueRenderer(Object value) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode v=(DefaultMutableTreeNode) value;
			if (v.getUserObject() instanceof HasOwnCellRenderer) {
				return ((HasOwnCellRenderer)v.getUserObject() ).getCellRenderer();
			} else return null;
	}
		return null;
	}
	
	/**returns true if the tree will be rendered in a less graphically complex form*/
	public static boolean isTreeDebug() {
		return treeDebug;
	}
	/**switches the tree to a less graphically complex version*/
	public static void setTreeDebug(boolean treeDebug) {
		GraphicCellRenderer.treeDebug = treeDebug;
	}


	

	
	
	
}


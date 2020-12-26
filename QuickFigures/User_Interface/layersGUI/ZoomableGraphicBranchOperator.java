/**
 * Author: Greg Mazo
 * Date Modified: Dec 25, 2020
 * Copyright (C) 2020 Gregory Mazo
 * Version: 2021
 */
package layersGUI;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.ZoomableGraphicGroup;

/**A version of the branch operations class that takes into acount the special case of 
  groups*/
class ZoomableGraphicBranchOperator extends TreeBranchOperations<ZoomableGraphic> {
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
/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
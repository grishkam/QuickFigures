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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2023.1
 */
package objectDialogs;

import graphicalObjects_Shapes.PathGraphic;
import locatedObject.PathPoint;
import standardDialog.booleans.BooleanInputPanel;
import undo.PathEditUndo;

/**A dialog for editing the properties of a single path point*/
public class PathPointOptionsDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected PathGraphic targetPath;
	PathPoint point1;
	
	

	public PathPointOptionsDialog(PathGraphic s, PathPoint point) {
		this.targetPath=s;	
		this.point1=point;
		 addOptionsToDialog();
		 undo=new PathEditUndo(s);
	}
	
	
	
	
	
	protected void addOptionsToDialog() {
		
		this.add("Closed", new BooleanInputPanel("Is closed ", point1.isClosePoint()));
		
	}
	
	
	protected void setItemsToDiaog() {
		point1.setClosePoint(this.getBoolean("Closed"));
		targetPath.updatePathFromPoints();
}
	
}

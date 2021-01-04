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
package objectDialogs;

import graphicalObjects_Shapes.PathGraphic;
import locatedObject.PathPoint;
import standardDialog.booleans.BooleanInputPanel;

public class PathPointOptionsDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected PathGraphic s;
	PathPoint p;
	
	

	public PathPointOptionsDialog(PathGraphic s, PathPoint p) {
	this.s=s;	
	this.p=p;
	 addOptionsToDialog();
	}
	
	
	
	
	
	protected void addOptionsToDialog() {
		
		this.add("Closed", new BooleanInputPanel("Is closed ", p.isClosePoint()));
		
	}
	
	
	protected void setItemsToDiaog() {
		p.setClosePoint(this.getBoolean("Closed"));
		s.updatePathFromPoints();
}
	
}

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
package popupMenusForComplexObjects;

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;

public class MontageLayoutDisplayOptions extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelLayoutGraphic mg;
	
	public MontageLayoutDisplayOptions(PanelLayoutGraphic mg) {
		this.setWindowCentered(true);
		this.setModal(true);
		this.mg=mg;
		addOptionsToDialog() ;
	}
	
	public void addOptionsToDialog() {
		this.addNameField(mg);
		this.add("editmode", new ChoiceInputPanel("How to handle edits", new String[] {"Contents of parent layer", "Layout only"}, mg.getEditMode()));
		this.add("always show", new BooleanInputPanel("Always Show", mg.isAlwaysShow()));
		this.add("locked in place", new BooleanInputPanel("Protected from mouse Drags ", mg.isUserLocked()==1));
	}
	
	protected void setItemsToDiaog() {
		mg.setName(this.getString("name"));
		mg.setEditMode(this.getChoiceIndex("editmode"));
		mg.setAlwaysShow(this.getBoolean("always show"));
		mg.setUserLocked(this.getBoolean("locked in place")?1:0);
	}

}

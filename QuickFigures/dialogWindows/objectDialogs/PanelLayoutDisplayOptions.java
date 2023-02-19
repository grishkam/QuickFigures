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

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;

/**An options dialog for layouts.
 * TODO: write more detailed descriptions */
public class PanelLayoutDisplayOptions extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelLayoutGraphic mg;
	
	public PanelLayoutDisplayOptions(PanelLayoutGraphic mg) {
		this.setWindowCentered(true);
		this.setModal(true);
		this.mg=mg;
		addOptionsToDialog() ;
	}
	
	public void addOptionsToDialog() {
		this.addNameField(mg);
		this.add("editmode", new ChoiceInputPanel("How to handle edits", new String[] {"Contents of parent layer", "Layout only"}, mg.getEditMode()));
		this.add("always show", new BooleanInputPanel("Always Show", mg.isAlwaysShow()));
		this.add("locked in place", new BooleanInputPanel("Protected from mouse Drags ", mg.isUserLocked()==PanelLayoutGraphic.LOCKED));
	}
	
	protected void setItemsToDiaog() {
		mg.setName(this.getString("name"));
		mg.setEditMode(this.getChoiceIndex("editmode"));
		mg.setAlwaysShow(this.getBoolean("always show"));
		mg.setUserLocked(this.getBoolean("locked in place")?1:0);
	}

}

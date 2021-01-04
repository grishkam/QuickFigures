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
package undo;

import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner.InsetGraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;

public class UndoInsetDefinerGraphic extends AbstractUndoableEdit2 {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InsetGraphicLayer iLayer;
	private DefaultLayoutGraphic iLayout;
	private PanelGraphicInsetDefiner def;
	private InsetGraphicLayer fLayer;
	private DefaultLayoutGraphic fLayout;

	public UndoInsetDefinerGraphic(PanelGraphicInsetDefiner def) {
		this.def=def;
		iLayer=def.personalLayer;
		iLayout=def.personalLayout;
	}
	

	public void establishFinalState() {
		
		fLayer=def.personalLayer;
		fLayout=def.personalLayout;
	}
	public void redo() {
		def.personalLayer=fLayer;
		def.personalLayout=fLayout;
	
	}
	
	public void undo() {
		def.personalLayer=iLayer;
		def.personalLayout=iLayout;
	}

}

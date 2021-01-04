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

public class UndoInsetDefChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelGraphicInsetDefiner item;
	private double iScale;
	private double fScale;

	public UndoInsetDefChange(PanelGraphicInsetDefiner insetDefiner) {
		this.item=insetDefiner;
		iScale=item.getBilinearScale();
	}
	

	public void establishFinalState() {
		fScale=item.getBilinearScale();
	}
	
	public void redo() {
		item.setBilinearScale(iScale);
	}
	
	public void undo() {
		item.setBilinearScale(fScale);
	}

}

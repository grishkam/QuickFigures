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
package undo;

import channelMerging.ImageDisplayLayer;
import genericMontageKit.PanelList;

public class UndoStackEdit extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList stack;
	private double iScale;
	private double fScale;
	private ImageDisplayLayer display;

	public UndoStackEdit(PanelList stack) {
		this.stack=stack;
		
		iScale=stack.getScaleBilinear();
				
	}
	
	public void establishFinalState() {
		fScale=stack.getScaleBilinear();
	}
	
	public void redo() {
		stack.setScaleBilinear(fScale);
		if (this.display!=null) display.updatePanels();
	}
	
	public void undo() {
		stack.setScaleBilinear(iScale);
		if (this.display!=null) display.updatePanels();
	}

	public void setDisplayLayer(ImageDisplayLayer layer) {
		this.display=layer;
		
	}

}

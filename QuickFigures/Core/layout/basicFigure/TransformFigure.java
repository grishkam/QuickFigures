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
 * Date Created: April 24, 2021
 * Date Modified: April 24, 2021
 * Version: 2023.2
 */
package layout.basicFigure;

import figureOrganizer.FigureOrganizingLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import undo.UndoLayoutEdit;

/**
 this class contains methods that transform a stored figure or layout
 work in progress. designed to make it easier for programmer to edit layout
 */
public class TransformFigure {
	
	private DefaultLayoutGraphic layout;

	public TransformFigure(DefaultLayoutGraphic layout) {
		
		this.layout=layout;
		layout.generateCurrentImageWrapper();
	}
	
	/**
	 * @param figureOrganizingLayerPane
	 * @param montageLayoutGraphic
	 */
	public TransformFigure(FigureOrganizingLayerPane figureOrganizingLayerPane,
			DefaultLayoutGraphic montageLayoutGraphic) {
		this(montageLayoutGraphic);
	}

	/**A simple method to move the layout*/
	public UndoLayoutEdit move(double dx, double dy) {
		UndoLayoutEdit undo = new UndoLayoutEdit(layout);
		layout.getEditor().moveLayout(layout.getPanelLayout(), dx, dy);
		
		return undo;
	}
	

}

/**
 * Author: Greg Mazo
 * Date Created: April 24, 2021
 * Date Modified: April 24, 2021
 * Version: 2021.1
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

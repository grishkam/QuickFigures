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
 * Date Modified: Dec 8, 2021
 * Version: 2022.1
 */
package figureOrganizer;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import addObjectMenus.LaneLabelCreationOptions;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import textObjectProperties.TextParagraph;

/**A class with methods to add labels to figures*/
public class FigureLabelOrganizer implements Serializable {

	/**
	The default names for each kind of label
	 */
	private static final String RIGHT_ROW_NAME = "Row ", LEFT_ROW_NAME = "        Row ", PANEL_NAME = "Panel ", COLUMN_NAME = "Column ";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**A class dedicated for panel labels*/
	public static class PanelLabelTextGraphic extends ComplexTextGraphic {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		 public ComplexTextGraphic createAnother() {
			return new PanelLabelTextGraphic();
		}
	}

	/**A class dedicated for column labels*/
	public static class ColumnLabelTextGraphic extends ComplexTextGraphic {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public ComplexTextGraphic createAnother() {
			return new ColumnLabelTextGraphic();
		}
	
	}

	/**A class dedicated for row labels*/
	public static class RowLabelTextGraphic extends ComplexTextGraphic {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public ComplexTextGraphic createAnother() {
			return new RowLabelTextGraphic();
		}
	
	}

	/**Adds a panel label
	 * @param textContent the text
	 * @param index the index
	 * @param thisLayer the target layer
	 * @param theLayout the layout*/
	public static ComplexTextGraphic addPanelLabel(String textContent, int index, GraphicLayer thisLayer, DefaultLayoutGraphic theLayout) {
		if (textContent==null) return null;
		if (textContent.trim().equals("")) return null;
		textContent=textContent.replace("_", " ");
		ComplexTextGraphic tg=new FigureLabelOrganizer.PanelLabelTextGraphic();
		tg.setAttachmentPosition(AttachmentPosition.defaultInternalPanel());
		tg.getParagraph().setAllLinesToCodeString(textContent, theLayout.getFigureType().getForeGroundDrawColor());
		tg.getParagraph().setJustification(TextParagraph.JUSTIFY_CENTER);
		thisLayer.add(tg);
		Rectangle2D p = theLayout.getPanelLayout().makeAltered(LayoutSpaces.PANELS).getPanel(index);
		
		
		try {
			/**makes sure the panel label can fit*/
			while (tg.getBounds().getWidth()>theLayout.getPanelLayout().getPanelWidth(index) &&tg.getFont().getSize()-2>3) {
				tg.setFontSize(tg.getFont().getSize()-2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tg.setLocation(new Point2D.Double(p.getCenterX(), p.getCenterY()));
		
		theLayout.addLockedItem(tg);
		return tg;
	}
	
	/**Adds a column label
	 * @param textContent the text
	 * @param colIndex the index
	 * @param thisLayer the target layer
	 * @param theLayout the layout*/
	public static ComplexTextGraphic addColLabel(String textContent, int colIndex, GraphicLayer thisLayer, DefaultLayoutGraphic theLayout) {
		if (textContent==null) return null;
		if (textContent.trim().equals("")) return null;
		textContent=textContent.replace("_", " ");
		ComplexTextGraphic tg=new FigureLabelOrganizer.ColumnLabelTextGraphic();
		tg.setAttachmentPosition(AttachmentPosition.defaultColSide());
		tg.getParagraph().setAllLinesToCodeString(textContent, Color.black);
		tg.getParagraph().setJustification(TextParagraph.JUSTIFY_CENTER);
		thisLayer.add(tg);
		Rectangle2D p = theLayout.getPanelLayout().makeAltered(LayoutSpaces.COLS).getPanel(colIndex);
		
		try {
			/**makes sure the col label can fit*/
			while (tg.getBounds().getWidth()>theLayout.getPanelLayout().getPanelWidthOfColumn(colIndex) &&tg.getFont().getSize()-2>3) {
				tg.setFontSize(tg.getFont().getSize()-2);
			}
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		tg.setLocationType(RectangleEdges.BOTTOM);
		tg.setLocation(new Point2D.Double(p.getCenterX(), p.getY()+4));
		
		theLayout.addLockedItem(tg);
		
		return tg;
	}
	
	/**Adds a row label 
	 * @param textContent the text
	 * @param rowIndex the index
	 * @param thisLayer the target layer
	 * @param theLayout the layout
	 * @param rightSide if true, label will be added to the right of row instead of the deault left*/
	public static ComplexTextGraphic addRowLabel(String textContent, int rowIndex, GraphicLayer thisLayer, DefaultLayoutGraphic theLayout, boolean rightSide) {
		if (textContent==null) return null;
		if (textContent.trim().equals("")) return null;
		textContent=textContent.replace("_", " ");
		ComplexTextGraphic tg=new FigureLabelOrganizer.RowLabelTextGraphic();
		tg.setAttachmentPosition(AttachmentPosition.defaultRowSide());
		tg.getParagraph().setAllLinesToCodeString(textContent, Color.black);
		tg.getParagraph().setJustification(TextParagraph.JUSTIFY_RIGHT);
		thisLayer.add(tg);
		
		
		if(rightSide) {
			tg.setAttachmentPosition(AttachmentPosition.rightOfRowSide());
			tg.getParagraph().setJustification(TextParagraph.JUSTIFY_LEFT);
		}
		
		Rectangle2D p = theLayout.getPanelLayout().makeAltered(LayoutSpaces.ROWS).getPanel(rowIndex);
		
		tg.setLocationType(RectangleEdges.RIGHT);
		tg.setLocation(new Point2D.Double(p.getX(), p.getCenterY()));
		//g.getEditor().expandSpacesToInclude(g.getPanelLayout(), tg.getBounds());
			
		theLayout.addLockedItem(tg);
		return tg;
	}
	
	/**Adds a label to the layout
	 * @param labelType the type of label
	 * @param index the index
	 * @param destinationLayer the target layer
	 * @param targetLayout the layout*/
	public static TextGraphic addLabelOfType(int labelType, int index, GraphicLayer destinationLayer, DefaultLayoutGraphic targetLayout, boolean opposideSide) {
		TextGraphic item=null;
		
		return addLabelOfType(null, labelType, index, destinationLayer, targetLayout, opposideSide, item);
	}

	/**
	 * @param name the text of the label
	 * @param labelType the type of label
	 * @param index the index
	 * @param destinationLayer the target layer
	 * @param targetLayout the layout
	 * @param opposideSide
	 * @param item
	 * @return
	 */
	public static TextGraphic addLabelOfType(String name, int labelType, int index, GraphicLayer destinationLayer,
			DefaultLayoutGraphic targetLayout, boolean opposideSide, TextGraphic item) {
		if(name==null)
			name=getDefaultNameOfType(labelType, opposideSide)+index;
		else 
			name=name.replaceAll(getNumberCode(), ""+index);
		
		if (labelType==BasicLayout.ROWS) item=addRowLabel(name, index, destinationLayer, targetLayout, opposideSide);
		else 
			if (labelType==BasicLayout.COLS) item= addColLabel(name, index, destinationLayer, targetLayout);
			else 
				if (labelType==BasicLayout.PANELS)item= addPanelLabel(name, index, destinationLayer, targetLayout);
		return item;
	}

	/**returns the string that indicates that the panel, row or column number should replace it within the text.
	 * @return
	 */
	public static String getNumberCode() {
		return LaneLabelCreationOptions.numberCode;
	}
	
	public static String getDefaultNameOfType(int labelType, boolean oppositeSide) {
		if (labelType==BasicLayout.ROWS) return (oppositeSide?RIGHT_ROW_NAME:LEFT_ROW_NAME);
		else 
			if (labelType==BasicLayout.COLS) return COLUMN_NAME;
			
		return PANEL_NAME;
	}
}

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
package figureOrganizer;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import textObjectProperties.TextParagraph;

/**A class with methods to add labels to figures*/
public class FigureLabelOrganizer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**TODO. write methods to add row label, col label and panel label specific options to the popup menus
	  and handles*/
	public static class PanelLabelTextGraphic extends ComplexTextGraphic {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		 public ComplexTextGraphic createAnother() {
			return new PanelLabelTextGraphic();
		}
	}

	public static class ColumnLabelTextGraphic extends ComplexTextGraphic {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public ComplexTextGraphic createAnother() {
			return new ColumnLabelTextGraphic();
		}
	
	}

	public static class RowLabelTextGraphic extends ComplexTextGraphic {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public ComplexTextGraphic createAnother() {
			return new RowLabelTextGraphic();
		}
	
	}

	
	public static ComplexTextGraphic addPanelLabel(String st, int colNum, GraphicLayer thisLay, DefaultLayoutGraphic g) {
		if (st==null) return null;
		if (st.trim().equals("")) return null;
		st=st.replace("_", " ");
		ComplexTextGraphic tg=new FigureLabelOrganizer.PanelLabelTextGraphic();
		tg.setAttachmentPosition(AttachmentPosition.defaultInternalPanel());
		tg.getParagraph().setAllLinesToCodeString(st, Color.white);
		tg.getParagraph().setJustification(TextParagraph.JUSTIFY_CENTER);
		thisLay.add(tg);
		Rectangle2D p = g.getPanelLayout().makeAltered(LayoutSpaces.PANELS).getPanel(colNum);
		
		
		try {
			/**makes sure the panel label can fit*/
			while (tg.getBounds().getWidth()>g.getPanelLayout().getPanelWidth(colNum) &&tg.getFont().getSize()-2>3) {
				tg.setFontSize(tg.getFont().getSize()-2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tg.setLocation(new Point2D.Double(p.getCenterX(), p.getCenterY()));
		
		g.addLockedItem(tg);
		return tg;
	}
	
	
	public static ComplexTextGraphic addColLabel(String st, int colNum, GraphicLayer thisLay, DefaultLayoutGraphic g) {
		if (st==null) return null;
		if (st.trim().equals("")) return null;
		st=st.replace("_", " ");
		ComplexTextGraphic tg=new FigureLabelOrganizer.ColumnLabelTextGraphic();
		tg.setAttachmentPosition(AttachmentPosition.defaultColSide());
		tg.getParagraph().setAllLinesToCodeString(st, Color.black);
		tg.getParagraph().setJustification(TextParagraph.JUSTIFY_CENTER);
		thisLay.add(tg);
		Rectangle2D p = g.getPanelLayout().makeAltered(LayoutSpaces.COLS).getPanel(colNum);
		
		try {
			/**makes sure the row label can fit*/
			while (tg.getBounds().getWidth()>g.getPanelLayout().getPanelWidthOfColumn(colNum) &&tg.getFont().getSize()-2>3) {
				tg.setFontSize(tg.getFont().getSize()-2);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tg.setLocationType(RectangleEdges.BOTTOM);
		tg.setLocation(new Point2D.Double(p.getCenterX(), p.getY()+4));
	//	g.getEditor().expandSpacesToInclude(g.getPanelLayout(), tg.getBounds());
		
		g.addLockedItem(tg);
		//g.snapLockedItems();
		//g.mapPanelLocation(tg);
		
		return tg;
	}
	
	/**Adds a row label to the figure*/
	public static ComplexTextGraphic addRowLabel(String st, int rowNum, GraphicLayer thisLay, DefaultLayoutGraphic g) {
		if (st==null) return null;
		if (st.trim().equals("")) return null;
		st=st.replace("_", " ");
		ComplexTextGraphic tg=new FigureLabelOrganizer.RowLabelTextGraphic();
		tg.setAttachmentPosition(AttachmentPosition.defaultRowSide());
		tg.getParagraph().setAllLinesToCodeString(st, Color.black);
		tg.getParagraph().setJustification(TextParagraph.JUSTIFY_RIGHT);
		thisLay.add(tg);
		Rectangle2D p = g.getPanelLayout().makeAltered(LayoutSpaces.ROWS).getPanel(rowNum);
		
		tg.setLocationType(RectangleEdges.RIGHT);
		tg.setLocation(new Point2D.Double(p.getX(), p.getCenterY()));
		//g.getEditor().expandSpacesToInclude(g.getPanelLayout(), tg.getBounds());
			
		g.addLockedItem(tg);
		return tg;
	}
	
	public static TextGraphic addLabelOfType(int type, int i, GraphicLayer thisLay, DefaultLayoutGraphic g) {
		TextGraphic item=null;
		if (type==BasicLayout.ROWS) item=addRowLabel("          Row "+i, i, thisLay, g);
		else 
			if (type==BasicLayout.COLS) item= addColLabel("Column "+i, i, thisLay, g);
			else 
				if (type==BasicLayout.PANELS)item= addPanelLabel("Panel "+i, i, thisLay, g);
		return item;
	}
}

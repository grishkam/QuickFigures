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
package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.RhombusGraphic;
import locatedObject.DefaultPaintProvider;
import locatedObject.PaintProvider;
import locatedObject.RectangleEdges;

/**A class for creation of a folder icon object. This actually consists of a few objects
 folder icons appear as expected*/
public class FolderIconGraphic extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color folderColor=new Color(180,0,0);
	boolean open;
	private RhombusGraphic r;
	private RectangularGraphic r2;
	private RectangularGraphic r3;
	boolean tiltfolder=true;
	boolean darkopen=true;
	
	public FolderIconGraphic(Color folderColor, boolean open) {
		this.open=open;
		this.setFolderColor(folderColor);
		createItems() ;
		setItemColors();
		
		addItems();
	}
	
	
	
	public void createItems() {
		r = new RhombusGraphic();
		r.setRectangle(new Rectangle(0,2,14,10));
		;
		r.setAntialize(true);
		r2 = RectangularGraphic.filledRect(new Rectangle(2,0, 6,5));
		 r3 = RectangularGraphic.filledRect(new Rectangle(1,1, 12,9));
		 r.makeNearlyDashLess();
		 setItemColors() ;
	}
	
	
	/**returns the shape of Rectangle 1. this will depend on the circumstances*/
	private Rectangle getR1rect() {
		if (open) {
			if (tiltfolder) {
				return new Rectangle(5,2,10,7);
				
			} else {return new Rectangle(0,4,14,8);}
			
		}
		else {
			return new Rectangle(0,2,14,10);
		}
	}
	
	public double getR1angle() {
		if (tiltfolder&&open) {
			return -Math.PI/8;
		}
		else {
			return 0;
		}
	}
	
	/**alters r1's properties depending on the condition of the folder.
	   open or closed*/
	public void setR1Dims() {
		r.setRectangle(getR1rect());
		r.setAngleBend(getR1angle());
	}
	
	public void setOpen(boolean o) {
		this.open=o;
		this.setR1Dims();
		this.setItemColors();
	}
	
	public void setColor(Color c) {
		setFolderColor(c);
		setItemColors();
	}
	public void setItemColors() {
		setR1Dims();
		
		
		 r2.setFillColor(getFolderColor());
			r3.setFillColor(getFolderColor());
			r.setFilled(true);
			PaintProvider pp = r.getFillPaintProvider();
			
			pp.setColor(getFolderColor().brighter());
			pp.setColor(1, getFolderColor().darker());
			r.setStrokeColor(getFolderColor().darker());
			
			if (open) {
				pp.setColor(getFolderColor().darker());
				//r.setRectangle(new Rectangle(0,4,14,8));
				r.setStrokeColor(getFolderColor().darker().darker());
		}
			 
			if (pp instanceof DefaultPaintProvider) {
				DefaultPaintProvider dp=(DefaultPaintProvider) pp;
				dp.setType(DefaultPaintProvider.SHAPE_GRADIENT_PAINT);
				dp.setFe1(RectangleEdges.TOP);
				dp.setFe2(RectangleEdges.BOTTOM);
				
				if (open&&darkopen) {
					dp.setColor(getFolderColor().darker());
					dp.setColor(1, getFolderColor().darker().darker());
				}
				
			}
			
			
			
	}
	
	public void addItems() {
		getTheInternalLayer().add(r3);
		getTheInternalLayer().add(r2);
		getTheInternalLayer().add(r);
	}



	public Color getFolderColor() {
		if (folderColor==null) {
			folderColor=Color.gray;
		}
		return folderColor;
	}



	public void setFolderColor(Color folderColor) {
		this.folderColor = folderColor;
	}
	
}
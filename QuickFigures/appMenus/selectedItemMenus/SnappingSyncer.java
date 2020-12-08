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
package selectedItemMenus;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import objectDialogs.MultiSnappingDialog;
import standardDialog.graphics.GraphicDisplayComponent;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.AttachmentPosition;

public class SnappingSyncer extends BasicMultiSelectionOperator {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean copySnap=true;
	private LocatedObject2D model;

	public SnappingSyncer(LocatedObject2D modelObject) {
		this.model=modelObject;
	}
	public SnappingSyncer(boolean b, LocatedObject2D modelObject) {
		this(modelObject);
		copySnap=!b;
	}



	@Override
	public String getMenuCommand() {
		return "Change relative positions";
	}



	@Override
	public void run() {
		
		createFromArray(array, copySnap);
	

	}
	
	public static void createFromArray(ArrayList<?> array, boolean copySnap) {
		MultiSnappingDialog d = new MultiSnappingDialog(copySnap);
		d.setGraphics(array);
		if(d.isEmpty()) return;
		d.showDialog();
	}
	
	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		ArrayList<Rectangle> rects = getRectanglesForIcon();
		Color[] colors=new Color[] {Color.red,  Color.blue,  new Color((float)0.0,(float)0.0,(float)0.0, (float)0.0)};
		
		for(int i=0; i<rects.size(); i++ ) {
			Rectangle r=rects.get(i);
			
			RectangularGraphic rect = RectangularGraphic.blankRect(r, colors[i]);
			rect.setStrokeWidth(0);
			rect.setFillColor(colors[i]);
			gg.getTheLayer().add(rect);
				}
		
		
		
		 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		 output.setRelocatedForIcon(false);
		
		 return output;
	}
	
	private ArrayList<Rectangle> getRectanglesForIcon() {
		ArrayList<Rectangle> output = new ArrayList<Rectangle>();
		
				output.add(new Rectangle(5,5,12,10));
				
			AttachmentPosition s = AttachmentPosition.defaultColLabel();
				output.add(new Rectangle(0,0,5,5));
				setTomodel(s);
				s.snapRects(output.get(1), output.get(0));
				
				output.add(new Rectangle(0,0,20,15));
		return output;
	}
	
	
	private void setTomodel(AttachmentPosition s) {
		if(this.model==null) return;
		AttachmentPosition a = this.model.getAttachmentPosition();
		if(a==null)return;
		s.setLocationTypeInternal(a.getSnapLocationTypeInternal());
		s.setLocationTypeExternal(a.getSnapLocationTypeExternal());
		s.setLocationType(a.getSnapType());
		s.setHorizontalOffset(0);
		s.setVerticalOffset(0);
	}
	public Icon getIcon() {
		return  getItemIcon(true);
	}

}

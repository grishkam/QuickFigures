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
 * Version: 2022.2
 */
package selectedItemMenus;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.RectangularGraphic;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import objectDialogs.MultiAttachmentPositionDialog;
import standardDialog.graphics.GraphicDisplayComponent;

/**A shows a dialog for chaning that attachment position of multiple objects
 * @see AttachmentPosition
 * */
public class AttachmentPositionAdjuster extends BasicMultiSelectionOperator {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean copySnap=true;//set to true if distinct copies of an attachment position are to be used
	private LocatedObject2D primaryObject;

	public AttachmentPositionAdjuster(LocatedObject2D modelObject) {
		this.primaryObject=modelObject;
	}
	public AttachmentPositionAdjuster(boolean usesameAttachmentPositionforManyObjects, LocatedObject2D modelObject) {
		this(modelObject);
		copySnap=!usesameAttachmentPositionforManyObjects;
	}



	@Override
	public String getMenuCommand() {
		return "Change relative positions";
	}



	@Override
	public void run() {
		
		createFromArray(array, copySnap);

	}
	
	/**shows an attachment position dialog for the list of items*/
	public static void createFromArray(ArrayList<?> array, boolean copySnap) {
		MultiAttachmentPositionDialog d = new MultiAttachmentPositionDialog(copySnap);
		d.setGraphics(array);
		if(d.isEmpty()) return;
		d.showDialog();
	}
	
	/**returns an icon containing a red rectangle for tha parent panel and a blue rectangle for the attached item*/
	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		ArrayList<Rectangle> rects = getRectanglesForIcon();
		Color[] colors=new Color[] {Color.red,  Color.blue,  new Color((float)0.0,(float)0.0,(float)0.0, (float)0.0)};
		
		for(int i=0; i<rects.size(); i++ ) {
			Rectangle r=rects.get(i);
			
			RectangularGraphic rect = RectangularGraphic.blankRect(r, colors[i]);
			rect.setStrokeWidth(0);
			rect.setFillColor(colors[i]);
			gg.getTheInternalLayer().add(rect);
				}
		
		
		
		 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		 output.setRelocatedForIcon(false);
		
		 return output;
	}
	
	/**returns the rectangles that are drawn onto the icon*/
	private ArrayList<Rectangle> getRectanglesForIcon() {
		ArrayList<Rectangle> output = new ArrayList<Rectangle>();
		
				output.add(new Rectangle(5,8,12,10));//the large rectangle
				
			AttachmentPosition s = AttachmentPosition.defaultColLabel();
				output.add(new Rectangle(0,0,5,5));//the small rectangle
				setAttachmentPositionIconToModelItem(s);
				s.snapRects(output.get(1), output.get(0));
				
				output.add(new Rectangle(0,0,20,20));
		return output;
	}
	
	/***/
	private void setAttachmentPositionIconToModelItem(AttachmentPosition position) {
		if(this.primaryObject==null) return;
		AttachmentPosition a = this.primaryObject.getAttachmentPosition();
		if(a==null)return;
		position.setLocationTypeInternal(a.getSnapLocationTypeInternal());
		position.setLocationTypeExternal(a.getSnapLocationTypeExternal());
		position.setLocationCategory(a.getLocationCategory());
		position.setHorizontalOffset(0);
		position.setVerticalOffset(0);
	}
	public Icon getIcon() {
		return  getItemIcon(true);
	}

}

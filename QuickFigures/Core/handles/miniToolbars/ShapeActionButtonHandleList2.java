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
 * Date Modified: Jan 5, 2021
 * Version: 2021.1
 */
package handles.miniToolbars;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import actionToolbarItems.EditManyShapes;
import actionToolbarItems.SetAngle;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import selectedItemMenus.MultiSelectionOperator;

/**A set of smart handles that acts as a mini toolbar for shapes*/
public class ShapeActionButtonHandleList2 extends ActionButtonHandleList {

	private Color[] standardColor=new Color[] { Color.white, Color.black,Color.blue, Color.green, Color.red,  Color.cyan, Color.magenta, Color.yellow , new Color(0,0,0,0)};
	
	{maxGrid=12;
	numHandleID=80000;//large number selected so that id numbers do not conflict with paths
	}

	private ShapeGraphic shape;
	public static final int ARROW_ONLYFORM=4, NORMAL_FORM=0;
	private int specialForm=NORMAL_FORM;


	public ShapeActionButtonHandleList2(ShapeGraphic t) {
		this.shape=t;
		addItems();
		updateLocation();
	}
	
	public ShapeActionButtonHandleList2(ShapeGraphic t, int form) {
		this.shape=t;
		this.specialForm=form;
		if(specialForm==ARROW_ONLYFORM) {
			this.createForArrowHeadType();
		} else
			addItems();
		updateLocation();
	}

	public void updateLocation() {
		ShapeGraphic t=shape;
		Rectangle bounds = t.getOutline().getBounds();
		super.setLocation(new Point2D.Double(bounds.getX()+5, bounds.getMaxY()+40));
	
	}
	public void updateHandleLocations(double magnify) {
		 
		super.updateHandleLocations(magnify);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void addItems() {
		
		EditManyShapes itemForIcon=null;
	
		if (shape.isFillable()){
			itemForIcon = new EditManyShapes(false, shape.getFillColor());
			itemForIcon.setModelItem(shape);
	
			GeneralActionListHandle hf = addOperationList(itemForIcon, fillColorActs());
			hf.setAlternativePopup(new ColoringButton(itemForIcon, 78341));
		
		}
		
		
		itemForIcon = new EditManyShapes(true, shape.getStrokeColor());
		itemForIcon.setModelItem(shape);
		
		GeneralActionListHandle h = addOperationList(itemForIcon, strokeColorActs());
		h.setAlternativePopup(new ColoringButton(itemForIcon, 782417));
		
		
		
		itemForIcon=new EditManyShapes(true, 2);
		itemForIcon.setModelItem(shape);
		
		addOperationList(itemForIcon, getStrokes() );
		
		
		
		
			itemForIcon=new EditManyShapes(BasicStroke.JOIN_BEVEL, null);
			itemForIcon.setModelItem(shape);
			GeneralActionListHandle cj =new GeneralActionHandleJoiner(itemForIcon, this.numHandleID,getJions()) ;
			super. addOperationList(itemForIcon, cj );
			cj.setxShift(cj.getxShift() + 4);
			cj.setyShift(cj.getyShift() + 6);
		
		
		itemForIcon=new EditManyShapes(null, BasicStroke.CAP_BUTT);
		itemForIcon.setModelItem(shape);
		 addOperationList(itemForIcon, getCaps() );
		 
		 itemForIcon=new EditManyShapes(true, new float[] {2,2});
			itemForIcon.setModelItem(shape); itemForIcon.alwaysDashIcon=true;
			addOperationList(itemForIcon, getDashes() );
		 
		if(shape instanceof ArrowGraphic) {
			createArrowButtons();
		 
				 } else 
		if(shape instanceof PathGraphic) {
			 itemForIcon=new EditManyShapes((PathGraphic) shape, shape.isClosedShape());
			 itemForIcon.setModelItem(shape);
			 addOperationList(itemForIcon,new EditManyShapes[] {new EditManyShapes(null, true), new EditManyShapes(null, false)} );
				 
					 }
		else {
			SetAngle itemForIcon2 = new SetAngle(45);
			 addOperationList(itemForIcon2,SetAngle.createManyAngles() );
				
		}

		setLocation(location);
	}



	/**
	 returns the stroke colors options
	 */
	public EditManyShapes[] strokeColorActs() {
		return EditManyShapes.getForColors(true, standardColor);
	}

	/**
	returns the fill colors options
	 */
	public EditManyShapes[] fillColorActs() {
		return EditManyShapes.getForColors(false, standardColor);
	}
	
	/**
	 generates all of the handles for the arrows
	 */
	public void createArrowButtons() {
		EditManyShapes itemForIcon;
		itemForIcon=new EditManyShapes((ArrowGraphic) shape, 1, 0, 1);
		 itemForIcon.setModelItem(shape);
		 addOperationList(itemForIcon,EditManyShapes. createOptionsforNumberOfArrowHeads() );
		 createForArrowHeadType();
	}

	/**creates the arrow head style action handles*/
	protected void createForArrowHeadType() {
		EditManyShapes itemForIcon;
		
		 itemForIcon=new EditManyShapes((ArrowGraphic) shape, 1, 2, ArrowGraphic.SECOND_HEAD);
		 itemForIcon.setModelItem(shape);
		  GeneralArrowHeadButton h2 = new GeneralArrowHeadButton(itemForIcon, numHandleID,EditManyShapes.createForArrow2(ArrowGraphic.SECOND_HEAD),ArrowGraphic.SECOND_HEAD);
		h2.usePalete=true;
		  super. addOperationList(itemForIcon, h2 );
		
		itemForIcon=new EditManyShapes((ArrowGraphic) shape, 1, 2, ArrowGraphic.FIRST_HEAD);
		 itemForIcon.setModelItem(shape);
		 
		 GeneralActionListHandle h = new GeneralArrowHeadButton(itemForIcon, numHandleID,EditManyShapes.createForArrow2(ArrowGraphic.FIRST_HEAD),ArrowGraphic.FIRST_HEAD);
		 h.usePalete=true;
		 super. addOperationList(itemForIcon, h );
		 
		
	}

	/**An action handle that is only visible if the given arrow head is visible*/
	class GeneralArrowHeadButton extends GeneralActionListHandle{

		private int hNumber;
		public GeneralArrowHeadButton(MultiSelectionOperator i, int num, MultiSelectionOperator[] items, int headNumber) {
			super(i, num, items);
			hNumber=headNumber;
		}
		@Override
		public boolean isHidden() {
			if (shape instanceof ArrowGraphic) {
					ArrowGraphic arrowGraphic = (ArrowGraphic) shape;
					if (hNumber==ArrowGraphic.SECOND_HEAD && arrowGraphic.headsAreSame()) { return true;}
					if (arrowGraphic.getNHeads()<hNumber) return true;
				
			}
			
			return false;
		}
		private static final long serialVersionUID = 1L;}

	/**A handle that is only visible if the given shape has joints. Circles and strait lies should not
	  have joints. */
	class GeneralActionHandleJoiner extends GeneralActionListHandle {

		public GeneralActionHandleJoiner(MultiSelectionOperator i, int num, MultiSelectionOperator[] items) {
			super(i, num, items);
		}
		@Override
		public boolean isHidden() {
			return !shape.doesJoins();
		}
		private static final long serialVersionUID = 1L;}


	
	public static EditManyShapes[] getDashes() {
		return new EditManyShapes[] {
				new EditManyShapes(true, new float[] {2,2}),
				new EditManyShapes(true, new float[] {}),
				
				new EditManyShapes(true, new float[] {4,4}),
				new EditManyShapes(true, new float[] {8,8}),
			
				new EditManyShapes(true, new float[] {8,16}),
				new EditManyShapes(true, new float[] {12,24})
				,new EditManyShapes(true, new float[] {4,4,8,4})
			
	};
	}
	
	public static EditManyShapes[] getStrokes() {
		return new EditManyShapes[] {
				new EditManyShapes(true, (float) 0.5),
				new EditManyShapes(true, 1),
				new EditManyShapes(true, 2),
				new EditManyShapes(true, 4),
				new EditManyShapes(true, 8),
				new EditManyShapes(true, 16),
				new EditManyShapes(true, 30),
			};
	}
	

	public static EditManyShapes[] getJions() {
		return new EditManyShapes[] {
				new EditManyShapes(BasicStroke.JOIN_BEVEL, null),
				new EditManyShapes(BasicStroke.JOIN_MITER, null),
				new EditManyShapes(BasicStroke.JOIN_ROUND, null)
				
				};
	}
	
	public static EditManyShapes[] getCaps() {
		return new EditManyShapes[] {
				new EditManyShapes(null, BasicStroke.CAP_BUTT),
				new EditManyShapes(null, BasicStroke.CAP_ROUND),
				new EditManyShapes(null, BasicStroke.CAP_SQUARE)
				
				};
	}
	

}

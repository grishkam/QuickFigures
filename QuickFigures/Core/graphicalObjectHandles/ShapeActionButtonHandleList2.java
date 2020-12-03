package graphicalObjectHandles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import actionToolbarItems.EditManyShapes;
import actionToolbarItems.SetAngle;
import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import selectedItemMenus.MultiSelectionOperator;
import standardDialog.ColorInputEvent;
import standardDialog.ColorInputListener;

public class ShapeActionButtonHandleList2 extends ActionButtonHandleList {

	private Color[] standardColor=new Color[] { Color.white, Color.black,Color.blue, Color.green, Color.red,  Color.cyan, Color.magenta, Color.yellow , new Color(0,0,0,0)};
	
	{maxGrid=12;
	numHandleID=80000;
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
	 * 
	 */
	public void createArrowButtons() {
		EditManyShapes itemForIcon;
		itemForIcon=new EditManyShapes((ArrowGraphic) shape, 1, 0, 1);
		 itemForIcon.setModelItem(shape);
		 addOperationList(itemForIcon,EditManyShapes. createForArrow() );
		 createForArrowHeadType();
	}

	/**
	 * @return
	 */
	public EditManyShapes[] strokeColorActs() {
		return EditManyShapes.getForColors(true, standardColor);
	}

	/**
	 * @return
	 */
	public EditManyShapes[] fillColorActs() {
		return EditManyShapes.getForColors(false, standardColor);
	}

	/**creates the arrow head style action buttons*/
	protected void createForArrowHeadType() {
		EditManyShapes itemForIcon;
		
		 itemForIcon=new EditManyShapes((ArrowGraphic) shape, 1, 2, ArrowGraphic.SECOND_HEAD);
		 itemForIcon.setModelItem(shape);
		  GeneralArrowHeadButton h2 = new GeneralArrowHeadButton(itemForIcon, numHandleID,EditManyShapes.createForArrow2(ArrowGraphic.SECOND_HEAD),ArrowGraphic.SECOND_HEAD);
		 super. addOperationList(itemForIcon, h2 );
		
		itemForIcon=new EditManyShapes((ArrowGraphic) shape, 1, 2, ArrowGraphic.FIRST_HEAD);
		 itemForIcon.setModelItem(shape);
		 
		 GeneralActionListHandle h = new GeneralArrowHeadButton(itemForIcon, numHandleID,EditManyShapes.createForArrow2(ArrowGraphic.FIRST_HEAD),ArrowGraphic.FIRST_HEAD);
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

	class GeneralActionHandleJoiner extends GeneralActionListHandle {

		public GeneralActionHandleJoiner(MultiSelectionOperator i, int num, MultiSelectionOperator[] items) {
			super(i, num, items);
			// TODO Auto-generated constructor stub
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
	
	public static class ColoringButton extends IconHandle implements ColorInputListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private EditManyShapes item;
		private transient CanvasMouseEvent lastPress;

		public ColoringButton(EditManyShapes itemForIcon, int handleNumber ) {
			
			super(itemForIcon.getIcon(), new Point(0,0));
			
			this.item=itemForIcon;
			this.xShift=5;
			this.yShift=5;
			this.setIcon(itemForIcon.getIcon());
			this.setHandleNumber(handleNumber);
		}
		
		@Override
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			showPopupMenu(canvasMouseEventWrapper);
		
		}

		public void showPopupMenu(CanvasMouseEvent canvasMouseEventWrapper) {
			lastPress=canvasMouseEventWrapper;
			String message="Change Fill Color";
			if(item.doesStroke()) message="Change Stroke Color";
			new ColorButtonHandleList(this).showInPopupPalete(canvasMouseEventWrapper, message);;
		}

		@Override
		public void ColorChanged(ColorInputEvent fie) {
			
			item.setSelector(fie.event.getSelectionSystem());
			
			if (item.doesStroke())item.getModelItem().setStrokeColor(fie.getColor());
			else
				item.getModelItem().setFillColor(fie.getColor());
			
			this.setIcon(item.getIcon());
			item.run();
			lastPress.getAsDisplay().updateDisplay();
			
		}

	}
}

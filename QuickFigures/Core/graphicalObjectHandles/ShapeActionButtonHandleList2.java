package graphicalObjectHandles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import actionToolbarItems.EditAndColorizeMultipleItems;
import actionToolbarItems.SetAngle;
import applicationAdapters.CanvasMouseEventWrapper;
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
		
		EditAndColorizeMultipleItems itemForIcon=null;
	
		if (shape.isFillable()){
		itemForIcon = new EditAndColorizeMultipleItems(false, shape.getFillColor());
		itemForIcon.setModelItem(shape);

		GeneralActionListHandle hf = addOperationList(itemForIcon, fillColorActs());
		hf.setAlternativePopup(new ColoringButton(itemForIcon, 78341));
		
		}
		
		
		itemForIcon = new EditAndColorizeMultipleItems(true, shape.getStrokeColor());
		itemForIcon.setModelItem(shape);
		
		GeneralActionListHandle h = addOperationList(itemForIcon, strokeColorActs());
		h.setAlternativePopup(new ColoringButton(itemForIcon, 782417));
		
		
		
		itemForIcon=new EditAndColorizeMultipleItems(true, 2);
		itemForIcon.setModelItem(shape);
		
		addOperationList(itemForIcon, getStrokes() );
		
		
		
		
			itemForIcon=new EditAndColorizeMultipleItems(BasicStroke.JOIN_BEVEL, null);
			itemForIcon.setModelItem(shape);
			GeneralActionListHandle cj =new GeneralActionHandleJoiner(itemForIcon, this.numHandleID,getJions()) ;
			super. addOperationList(itemForIcon, cj );
			cj.setxShift(cj.getxShift() + 4);
			cj.setyShift(cj.getyShift() + 6);
		
		
		itemForIcon=new EditAndColorizeMultipleItems(null, BasicStroke.CAP_BUTT);
		itemForIcon.setModelItem(shape);
		 addOperationList(itemForIcon, getCaps() );
		 
		 itemForIcon=new EditAndColorizeMultipleItems(true, new float[] {2,2});
			itemForIcon.setModelItem(shape); itemForIcon.alwaysDashIcon=true;
			addOperationList(itemForIcon, getDashes() );
		 
		if(shape instanceof ArrowGraphic) {
		 itemForIcon=new EditAndColorizeMultipleItems((ArrowGraphic) shape, 1, 0);
		 itemForIcon.setModelItem(shape);
		 addOperationList(itemForIcon,EditAndColorizeMultipleItems. createForArrow() );
		 createForArrowHeadType();
		 
				 } else 
		if(shape instanceof PathGraphic) {
			 itemForIcon=new EditAndColorizeMultipleItems((PathGraphic) shape, shape.isClosedShape());
			 itemForIcon.setModelItem(shape);
			 addOperationList(itemForIcon,new EditAndColorizeMultipleItems[] {new EditAndColorizeMultipleItems(null, true), new EditAndColorizeMultipleItems(null, false)} );
				 
					 }
		else {
			SetAngle itemForIcon2 = new SetAngle(45);
			 addOperationList(itemForIcon2,SetAngle.createManyAngles() );
				
		}

		setLocation(location);
	}

	/**
	 * @return
	 */
	public EditAndColorizeMultipleItems[] strokeColorActs() {
		return EditAndColorizeMultipleItems.getForColors(true, standardColor);
	}

	/**
	 * @return
	 */
	public EditAndColorizeMultipleItems[] fillColorActs() {
		return EditAndColorizeMultipleItems.getForColors(false, standardColor);
	}

	protected void createForArrowHeadType() {
		EditAndColorizeMultipleItems itemForIcon;
		itemForIcon=new EditAndColorizeMultipleItems((ArrowGraphic) shape, 1, 2);
		 itemForIcon.setModelItem(shape);
		 addOperationList(itemForIcon,EditAndColorizeMultipleItems. createForArrow2() );
	}




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



	public static EditAndColorizeMultipleItems[] getDashes() {
		return new EditAndColorizeMultipleItems[] {
				new EditAndColorizeMultipleItems(true, new float[] {2,2}),
				new EditAndColorizeMultipleItems(true, new float[] {}),
				
				new EditAndColorizeMultipleItems(true, new float[] {4,4}),
				new EditAndColorizeMultipleItems(true, new float[] {8,8}),
			
				new EditAndColorizeMultipleItems(true, new float[] {8,16}),
				new EditAndColorizeMultipleItems(true, new float[] {12,24})
				,new EditAndColorizeMultipleItems(true, new float[] {4,4,8,4})
			
	};
	}
	
	public static EditAndColorizeMultipleItems[] getStrokes() {
		return new EditAndColorizeMultipleItems[] {
				new EditAndColorizeMultipleItems(true, (float) 0.5),
				new EditAndColorizeMultipleItems(true, 1),
				new EditAndColorizeMultipleItems(true, 2),
				new EditAndColorizeMultipleItems(true, 4),
				new EditAndColorizeMultipleItems(true, 8),
				new EditAndColorizeMultipleItems(true, 16),
				new EditAndColorizeMultipleItems(true, 30),
			};
	}
	

	public static EditAndColorizeMultipleItems[] getJions() {
		return new EditAndColorizeMultipleItems[] {
				new EditAndColorizeMultipleItems(BasicStroke.JOIN_BEVEL, null),
				new EditAndColorizeMultipleItems(BasicStroke.JOIN_MITER, null),
				new EditAndColorizeMultipleItems(BasicStroke.JOIN_ROUND, null)
				
				};
	}
	
	public static EditAndColorizeMultipleItems[] getCaps() {
		return new EditAndColorizeMultipleItems[] {
				new EditAndColorizeMultipleItems(null, BasicStroke.CAP_BUTT),
				new EditAndColorizeMultipleItems(null, BasicStroke.CAP_ROUND),
				new EditAndColorizeMultipleItems(null, BasicStroke.CAP_SQUARE)
				
				};
	}
	
	public static class ColoringButton extends IconHandle implements ColorInputListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private EditAndColorizeMultipleItems item;
		private transient CanvasMouseEventWrapper lastPress;

		public ColoringButton(EditAndColorizeMultipleItems itemForIcon, int handleNumber ) {
			
			super(itemForIcon.getIcon(), new Point(0,0));
			
			this.item=itemForIcon;
			this.xShift=5;
			this.yShift=5;
			this.setIcon(itemForIcon.getIcon());
			this.setHandleNumber(handleNumber);
		}
		
		@Override
		public void handlePress(CanvasMouseEventWrapper canvasMouseEventWrapper) {
			showPopupMenu(canvasMouseEventWrapper);
		
		}

		public void showPopupMenu(CanvasMouseEventWrapper canvasMouseEventWrapper) {
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

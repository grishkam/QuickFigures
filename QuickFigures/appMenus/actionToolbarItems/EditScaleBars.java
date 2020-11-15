package actionToolbarItems;


import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import gridLayout.MontageSpaces;
import menuUtil.SmartPopupJMenu;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.ColorInputEvent;
import standardDialog.ColorInputListener;
import standardDialog.GraphicDisplayComponent;
import standardDialog.InfoDisplayPanel;
import standardDialog.NumberInputEvent;
import standardDialog.NumberInputListener;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.UndoScaleBarEdit;
import utilityClassesForObjects.Fillable;
import utilityClassesForObjects.LocatedObject2D;

/**Applies a single color or stroke style to all the objects.*/
public class EditScaleBars extends BasicMultiSelectionOperator implements  MontageSpaces, ColorInputListener,  Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean stroke;
	private Color theColor=null;
	private RectangularGraphic rect;
	
	private Float strokeWidth=null;

	RectangularGraphic colorObject=new RectangularGraphic();
	
	private BarGraphic modelItem=null;
	
	int projectionType=0;
	private int type;

	private double projectionLength=8;
	private double unitLength=1;

	private String unit="";
	
	public static int TYPE_PROJ=0, TYPE_WIDTH=1, TYPE_PROJ_LENGTH=2, TYPE_LENGTH_UNITS=3;

	public boolean doesStroke() {
		return stroke;
	}
	
	public void setUnit(String un) {unit=un;}
	
	public double projectionLength() {
		if(modelItem!=null)
		return modelItem.getProjectionLength();
		return projectionLength;
	}


	public EditScaleBars(int form, double input) {
		this.type=form;
		if(type==TYPE_PROJ) projectionType=(int) input;
		if(type==TYPE_WIDTH) strokeWidth=(float) input;
		if(type==TYPE_PROJ_LENGTH)  projectionLength= input;
		if(type==TYPE_LENGTH_UNITS) unitLength=input;
	}
	
	
	public static EditScaleBars[] getProjectionList() {
		return new  EditScaleBars[] {
				new  EditScaleBars(TYPE_PROJ, 0),
				new  EditScaleBars(TYPE_PROJ, 1),
				new  EditScaleBars(TYPE_PROJ, 2),
		};
	}
	
	public static EditScaleBars[] getUnitLengthList(String unit) {
		EditScaleBars[] output = new  EditScaleBars[BarGraphic.reccomendedBarLengths.length];
		for(int i=0; i<output.length; i++) {
			output[i]=new  EditScaleBars(TYPE_LENGTH_UNITS, BarGraphic.reccomendedBarLengths[i]);
			output[i].setUnit(unit);
		}
		return output;
	}
	

	public static String[] projTypes=new String[] {"Bar with 2 Projections", "Bar with 1 Projections", "no projection"};
	

	@Override
	public String getMenuCommand() {
		if(type==TYPE_PROJ)
			return projTypes[projectionType]; 
		if(type==TYPE_LENGTH_UNITS)
			return "make "+unitLength+" "+unit;
			

		
		return "Alter Scale Bars";
	}


	@Override
	public void run() {
		long time = System.currentTimeMillis();
		
	
		
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		
		CombinedEdit edit = new CombinedEdit();//an edit for the undo manager
		
		for(LocatedObject2D a: all) 
			if(a instanceof BarGraphic) edit.addEditToList(applyTo((BarGraphic) a));
		
		if (selector!=null&&selector.getGraphicDisplayContainer()!=null)
		selector.getGraphicDisplayContainer().getUndoManager().addEdit(edit);
		
	}
	
	

/**applies the change and returns an undoable edit*/
	private AbstractUndoableEdit applyTo(BarGraphic a) {
		CombinedEdit edit = new CombinedEdit();
		ColorEditUndo edit4 = new ColorEditUndo(a);
		
	
	
		
		if (!stroke && a instanceof Fillable) {
			Fillable s=(Fillable) a;
			if (getTheColor()!=null &&s.isFillable())s.setFillColor(getTheColor());
		}
		
		UndoScaleBarEdit edit2 = new UndoScaleBarEdit(a);
		if(type==TYPE_PROJ) {
			a.setProjectionType(projectionType);
		}
		if(type==TYPE_PROJ_LENGTH) {
			a.setLengthProjection(this.projectionLength);;
		}
		if(type==TYPE_WIDTH) {
			a.setBarStroke(strokeWidth);
		}
		if(type==TYPE_LENGTH_UNITS) {
			a.setLengthInUnits(unitLength);;
		}
		
		edit2.establishFinalState();
		edit.addEditToList(edit2);
		
		edit4.establishFinalColors();
		edit.addEditToList(edit4);
		
		return edit;
		
	}

	@Override
	public String getMenuPath() {
	
		return "Actions";
	}
	
	
	
	public Icon getIcon() {
		
		if(TYPE_PROJ==type)
			return getProjectionIcon();
		
		return getGenericIcon();
	}


	protected Icon getProjectionIcon() {
		GraphicGroup gg=new GraphicGroup();
		gg.getTheLayer().add(RectangularGraphic.blankRect(new Rectangle(0,0,25,25), new Color(0,0,0,0)));
		
		BarGraphic barGraphic = new BarGraphic();
		barGraphic.setProjectionType(projectionType);
		barGraphic.setFillColor(Color.BLACK);
		
		BarGraphic createBarForIcon = barGraphic.createBarForIcon();
		createBarForIcon.setStrokeColor(Color.black);
		createBarForIcon.setFillColor(Color.black);
		createBarForIcon.moveLocation(3,5);
		createBarForIcon.setShowText(false);
		gg.getTheLayer().add(createBarForIcon);
		GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		
		
		 return output;
	}
	
	protected Icon getGenericIcon() {
		GraphicGroup gg=new GraphicGroup();
		gg.getTheLayer().add(RectangularGraphic.blankRect(new Rectangle(0,0,25,25), new Color(0,0,0,0)));
		
		BarGraphic barGraphic = new BarGraphic();
		barGraphic.setProjectionType(2);
		barGraphic.setFillColor(Color.BLACK);
		
		BarGraphic createBarForIcon = barGraphic.createBarForIcon();
		createBarForIcon.getBarText().setTextColor(Color.black);
		createBarForIcon.setFillColor(Color.black);
		createBarForIcon.moveLocation(5,7);
		createBarForIcon.setShowText(true);
		if(type==TYPE_LENGTH_UNITS)  createBarForIcon.setLengthInUnits(unitLength);
		gg.getTheLayer().add(createBarForIcon);
		GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		
		
		 return output;
	}

	public Color getTheColor() {
		if (theColor==null) return null;
		if( getModelItem()!=null) {
			return stroke? getModelItem().getStrokeColor(): getModelItem().getFillColor();
		}
		
		return theColor;
	}

	public void setTheColor(Color theColor) {
		this.theColor = theColor;
	}

	public ShapeGraphic getModelItem() {
		return modelItem;
	}

	public void setModelItem(BarGraphic modelItem) {
		this.modelItem = modelItem;
	}
	



	public Float getStrokeWidth() {
		if(strokeWidth==null) return null;
		if(getModelItem()!=null)return getModelItem().getStrokeWidth();
		return strokeWidth;
	}

	public void setStrokeWidth(Float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}




	@Override
	public void ColorChanged(ColorInputEvent fie) {
		theColor=fie.getColor();
		run();
	}
	
	public SmartPopupJMenu getPopup() {
		SmartPopupJMenu out = new SmartPopupJMenu();
		out.add(getInputPanel());
		return out;
	}
	
	public Component getInputPanel() {
		if(TYPE_PROJ==type) 
			return StandardDialog.combinePanels(getProjectionInput(),getStrokeWidthInput(), new InfoDisplayPanel("  ", ""), new InfoDisplayPanel("  ", ""));;
		
		
		return StandardDialog.combinePanels( getUnitInput(),getStrokeWidthInput(), new InfoDisplayPanel("  ", ""), new InfoDisplayPanel("  ", ""));
	}


	
	protected NumberInputPanel getProjectionInput() {
		
		NumberInputPanel panel = new NumberInputPanel("Length Projection", this.projectionLength(), 0,50);
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				
				EditScaleBars runner = new EditScaleBars(TYPE_PROJ_LENGTH, ne.getNumber());
				runner.setSelector(selector);
				runner.run();
				
				selector.getGraphicDisplayContainer().updateDisplay();
				
			}
		});
		return panel;
	}

	protected NumberInputPanel getStrokeWidthInput() {
		
		NumberInputPanel panel = new NumberInputPanel("Bar Thickness", modelItem.getBarStroke(), 0,50);
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				strokeWidth=(float) ne.getNumber();
				EditScaleBars runner = new EditScaleBars(TYPE_WIDTH, (float)ne.getNumber());
				runner.setSelector(selector);
				runner.run();
				
				selector.getGraphicDisplayContainer().updateDisplay();
				
			}
		});
		
		return panel;
	}
	
	protected NumberInputPanel getUnitInput() {
		if(this.getStrokeWidth()==null) return null;
		NumberInputPanel panel = new NumberInputPanel("Length in "+modelItem.getScaleInfo().getUnits(), modelItem.getLengthInUnits());
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				EditScaleBars runner = new EditScaleBars(TYPE_LENGTH_UNITS, (float)ne.getNumber());
				runner.setSelector(selector);
				runner.run();
				selector.getGraphicDisplayContainer().updateDisplay();
				
			}
		});
		return panel;
	}
	
	

}

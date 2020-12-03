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
import gridLayout.LayoutSpaces;
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

/**This class applies specific properties to all the selected scale bars.*/
public class EditScaleBars extends BasicMultiSelectionOperator implements  LayoutSpaces, ColorInputListener,  Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int TYPE_CHANGE_PROJECTIONS=0, TYPE_BAR_THICKNESS_WIDTH=1, TYPE_PROJ_LENGTH=2, TYPE_LENGTH_UNITS=3;

	public static String[] projTypes=new String[] {"Bar with 2 Projections", "Bar with 1 Projections", "no projection"};
	

	private boolean stroke;
	private Color theColor=null;
	
	private Float strokeWidth=null;

	RectangularGraphic colorObject=new RectangularGraphic();
	
	private BarGraphic modelItem=null;
	
	int projectionType=0;
	private int type;

	private double projectionLength=8;
	private double unitLength=1;

	private String unit="";
	
	
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
		if(type==TYPE_CHANGE_PROJECTIONS) projectionType=(int) input;
		if(type==TYPE_BAR_THICKNESS_WIDTH) strokeWidth=(float) input;
		if(type==TYPE_PROJ_LENGTH)  projectionLength= input;
		if(type==TYPE_LENGTH_UNITS) unitLength=input;
	}
	
	
	public static EditScaleBars[] getProjectionList() {
		return new  EditScaleBars[] {
				new  EditScaleBars(TYPE_CHANGE_PROJECTIONS, 0),
				new  EditScaleBars(TYPE_CHANGE_PROJECTIONS, 1),
				new  EditScaleBars(TYPE_CHANGE_PROJECTIONS, 2),
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
	

	

	@Override
	public String getMenuCommand() {
		if(type==TYPE_CHANGE_PROJECTIONS)
			return projTypes[projectionType]; 
		if(type==TYPE_LENGTH_UNITS)
			return "make "+unitLength+" "+unit;
			

		
		return "Alter Scale Bars";
	}


	@Override
	public void run() {
		
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
		if(type==TYPE_CHANGE_PROJECTIONS) {
			a.setProjectionType(projectionType);
		}
		if(type==TYPE_PROJ_LENGTH) {
			a.setLengthProjection(this.projectionLength);;
		}
		if(type==TYPE_BAR_THICKNESS_WIDTH) {
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
		
		if(TYPE_CHANGE_PROJECTIONS==type)
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
		if(TYPE_CHANGE_PROJECTIONS==type) 
			return StandardDialog.combinePanels(getProjectionInput(),getStrokeWidthInput(), new InfoDisplayPanel("  ", ""), new InfoDisplayPanel("  ", ""));;
		
		
		return StandardDialog.combinePanels( getUnitInput(),getStrokeWidthInput(), new InfoDisplayPanel("  ", ""), new InfoDisplayPanel("  ", ""));
	}


	/**returns a number input panel for setting the length of the projects*/
	protected NumberInputPanel getProjectionInput() {
		
		NumberInputPanel panel = new NumberInputPanel("Projection", this.projectionLength(), 0,50);
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

	/**returns a number input panel for setting the thickness of the scale bar */
	protected NumberInputPanel getStrokeWidthInput() {
		
		NumberInputPanel panel = new NumberInputPanel("Bar Thickness", modelItem.getBarStroke(), 0,50);
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				strokeWidth=(float) ne.getNumber();
				EditScaleBars runner = new EditScaleBars(TYPE_BAR_THICKNESS_WIDTH, (float)ne.getNumber());
				runner.setSelector(selector);
				runner.run();
				
				selector.getGraphicDisplayContainer().updateDisplay();
				
			}
		});
		
		return panel;
	}
	
	/**returns a number input panel for setting the unit length */
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

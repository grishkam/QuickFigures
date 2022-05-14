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
 * Version: 2022.1
 */
package actionToolbarItems;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.CountParameter;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.LayoutSpaces;
import locatedObject.Fillable;
import locatedObject.LocatedObject2D;
import locatedObject.RainbowPaintProvider;
import locatedObject.StrokedItem;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import objectDialogs.StrokeInputPanel;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.ColorMultiSelectionOperator;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;
import standardDialog.graphics.GraphicDisplayComponent;
import standardDialog.numbers.NumberArrayInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.PathEditUndo;
import undo.SimpleItemUndo;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;
import undo.UndoTextEdit;

/**Applies color, stroke, style, font or other property a series of selected items
  That property can be a defined value or can match an example object*/
public class EditManyObjects extends BasicMultiSelectionOperator implements  LayoutSpaces, ColorInputListener,  ColorMultiSelectionOperator, Serializable {


	/**
	 * 
	 */
	public static final String FONT_UP = "up", FONT_DOWN="down";
	/**
	values stored here determine what this operation does
	if one of those values is set to null, this editor will not affect it
	by setting a value to a non-null value
	 */
	private static final long serialVersionUID = 1L;
	private boolean rainbowColor=false;
	private boolean stroke;
	private Color theColor=null;
	private RectangularGraphic rect;
	private float[] dashes;
	private Float strokeWidth=null;
	private Double miterLimit=null;
	private Boolean pathClosed=null;
	private Integer strokeJoin=null;
	private Integer strokeCap=null;
	long lastTime=0;
	private Integer arrowHeadNumber=null;
	private Integer arrowStyle=null;
	RectangularGraphic colorObject=new RectangularGraphic();
	private ShapeGraphic modelItem=null;
	private Integer fontStyle=null;
	private boolean fontUp=false;
	private boolean fontDown=false;
	private TextGraphic modelTextItem;
	private boolean bigIcon;
	public boolean alwaysDashIcon=false;
	private int setTargetHead=ArrowGraphic.FIRST_HEAD;
	
	
	/**creates a series of operators that chanes the color*/
	public static EditManyObjects[]  getForColors(boolean doStroke, Color... color) {
		EditManyObjects[] out = new EditManyObjects[color.length+1];
		for(int i=0; i<color.length; i++) {
			Color c1= color[i];
			out[i]=new EditManyObjects(doStroke, c1);
		}
		out[color.length]=new EditManyObjects();
		out[color.length].stroke=doStroke;
		return out;
	}
	
	/**returns true if this targets the stroke.
	 * for example, one that changes the stroke color rather than the fill color*/
	public boolean doesStroke() {
		return stroke;
	}

	/**returns an editor that shows a color choose to the user*/
	public EditManyObjects() {
		setTheColor(Color.white);
		this.rainbowColor=true;
	}
	
	/**creates an editor for changing the properties of an arrow*/
	public EditManyObjects(ArrowGraphic a, Integer i, int type, int targetHead) {
		
		if(i==null) setNumberOfArrowHeads(a.getNHeads());
		else setNumberOfArrowHeads(i);
		if(type==2) {
			this.setNumberOfArrowHeads(null);
			this.setArrowStyle(i);
			this.setTargetHead=targetHead;
		}
	}
	
	/**returns an editor that changes the path between closed and open form*/
	public EditManyObjects(PathGraphic a, Boolean i) {
		this.setModelItem(a);
		this.setPathCloser(i);
	}

	
	
	/**creates an editor for either stroke color or fill color
	 * @param stroke whether to do the stroke color*/
	public EditManyObjects(boolean stroke, Color c) {
		this.stroke=stroke;;
		setTheColor(c);
	}
	
	/**creates an editor that */
	public EditManyObjects(String special) {
		if (special!=null) special=special.toLowerCase(); else return;
		if (special.contains(FONT_UP)) this.fontUp=true;
		if (special.contains(FONT_DOWN)) this.fontDown=true;
	}
	
	/**creates an editor that changes the font style*/
	public EditManyObjects(int fontStyle) {
		this.stroke=true;
		this.fontStyle=fontStyle;
	}
	
	/**creates an editor to apply dashes*/
	public EditManyObjects(boolean stroke, float[] c) {
		this.stroke=stroke;;
		setDashes(c);
	}
	
	
	/**creates an editor that alters stroke width*/
	public EditManyObjects(boolean stroke, float c) {
		this.stroke=stroke;;
		this.setStrokeWidth(c);
	}
	
	/**creates an editor that alters stroke caps*/
	public EditManyObjects(Integer join, Integer cap) {
		stroke=true;
		this.setStrokeJoin(join);
		this.setStrokeCap(cap);
	}
	
	/**creates a shape editor that alters the miter limit of the stroke*/
	public EditManyObjects(String category, String c2, double miterLimit2) {
		stroke=true;
		
		this.miterLimit=miterLimit2;
	}

	/**returns the menu command for this shape editor*/
	@Override
	public String getMenuCommand() {
		if (this.getDashes()!=null)  {return "Change Dashes"; }
		if (this.getStrokeWidth()!=null&&this.getStrokeWidth()==0)  {return "Stroke Width To Zero"; }
		if (this.getStrokeWidth()!=null)  {return "Change Stroke Width "+(this.getStrokeWidth()); }
		
		if(doesOpenClosePath()) {
			if(this.isPathCloser())
				return "Closed Path";
			if(!isPathCloser())
				return "Un-Closed Path";
			
		}
		
		if (fontStyle!=null) {
			if (this.fontStyle==Font.PLAIN)  {return "Make Plain (All text)"; }
			if (this.fontStyle==Font.BOLD)  {return "Make Bold (All text)"; }
			if (this.fontStyle==Font.ITALIC)  {return "Make Italic (All text)"; }
			if (this.fontStyle==Font.BOLD+Font.ITALIC)  {return "Make Bold + Italic (All text)"; }
		}
		if (fontUp) return "Increase Font Size";
		if (fontDown) return "Decrease Font Size";
		String t="";
		if (getTheColor()!=null &&getTheColor().getAlpha()==0) t="to transparent";
		
		if (getStrokeJoin()!=null) {
			if (getStrokeJoin()==BasicStroke.JOIN_ROUND) return "Rounded Corners";
			if (getStrokeJoin()==BasicStroke.JOIN_BEVEL) return "Bevel Corners";
			if (getStrokeJoin()==BasicStroke.JOIN_MITER) return "Sharp Corners";
		}
		
		if (getStrokeCap()!=null) {
			if (getStrokeCap()==BasicStroke.CAP_BUTT) return "Normal";
			if (getStrokeCap()==BasicStroke.CAP_ROUND) return "Round";
			if (getStrokeCap()==BasicStroke.CAP_SQUARE) return "Square";
		}
		if(getNumberOfArrowHeads()!=null) {
			return getNumberOfArrowHeads()+ " Headed Arrow";
		}
		if (this.getArrowStyle()!=null) 
			return "Change Arrow Head Style";
		if(this.getMiterLimit()!=null) return "Change Miter Limit";
		return "Change "+(stroke? "Stroke":"Fill")+" Color "+t;
	}

	/**returns true if this editor opens/closes a path2d*/
	protected boolean doesOpenClosePath() {
		return this.isPathCloser()!=null;
	}


	@Override
	public void run() {
		
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		
		CombinedEdit edit = new CombinedEdit();//an undo
		
		/**edits the items and add combines the undo*/
		for(LocatedObject2D a: all)
			edit.addEditToList(
					editShape(a)
					);
		
		/**Adds the edit to the undo manager*/
		if (selector!=null&&selector.getWorksheet()!=null)
			{
				selector.getWorksheet().getUndoManager().addEdit(edit);
					}
		else {IssueLog.log("failed to add undo to udo manager "+this);}
		
	}
	
	

/**applies the change to one object and returns an undoable edit*/
	private AbstractUndoableEdit editShape(Object a) {
		CombinedEdit edit = new CombinedEdit();
		ColorEditUndo edit4 = new ColorEditUndo(a);
		
		if (stroke && a instanceof StrokedItem) {
			
			StrokedItem s=(StrokedItem) a;
			UndoStrokeEdit edit2 = new UndoStrokeEdit(s);
			
			if (getTheColor()!=null) {
				
				s.setStrokeColor(getTheColor());
			
			}
			if (this.getDashes()!=null) s.setDashes(getDashes());
			if (this.getStrokeWidth()!=null) s.setStrokeWidth(getStrokeWidth());
			if (this.getStrokeJoin()!=null) s.setStrokeJoin(getStrokeJoin());
			if (this.getStrokeCap()!=null) s.setStrokeCap(getStrokeCap());
			if(this.getMiterLimit()!=null) s.setMiterLimit(getMiterLimit());
			edit2.establishFinalState();
			edit.addEditToList(edit2);
		}
		
		if(a instanceof ArrowGraphic ) {
			
			ArrowGraphic b=(ArrowGraphic) a;
			
			if ( arrowHeadNumber!=null) {
				SimpleItemUndo<CountParameter> edit2 = b.createHeadNumberHandle().createUndo();
				b.setNumerOfHeads(this.getNumberOfArrowHeads());
				edit2.establishFinalState();
				edit.addEditToList(edit2);
			}
			
			if ( arrowStyle!=null) {
				UndoScalingAndRotation edit2 = new UndoScalingAndRotation(b);
				b.getHead(this.setTargetHead).setArrowStyle(this.getArrowStyle());
				edit2.establishFinalState();
				edit.addEditToList(edit2);
			}
		}
		
		if(a instanceof PathGraphic ) {
			if (doesOpenClosePath()) {
				PathEditUndo edit5 = new PathEditUndo((PathGraphic) a);
				((PathGraphic) a).setClosedShape(isPathCloser());
				edit5.establishFinalState();
				edit.addEditToList(edit5);
			}
		}
		
		if (a instanceof TextGraphic) {
			
			TextGraphic s=(TextGraphic) a;
			
			UndoTextEdit edit3 = new UndoTextEdit(s);
			
			if (getTheColor()!=null) {
				s.setTextColor(getTheColor());
			}
			if (this.fontStyle!=null) {s.setFontStyle(fontStyle);}
			float sizeFont = s.getFont().getSize();
			if (fontUp) {
				s.setFont(s.getFont().deriveFont(sizeFont+2));
			}
			if (fontDown) {
				if (sizeFont>2)s.setFont(s.getFont().deriveFont(sizeFont-2));
			}
			
			edit3.setUpFinalState();
			edit.addEditToList(edit3);
		}
		
		
		if (!stroke && a instanceof Fillable) {
			Fillable s=(Fillable) a;
			if (getTheColor()!=null &&s.isFillable()) {s.setFillColor(getTheColor());
			}
		}
		
		edit4.establishFinalColors();
		edit.addEditToList(edit4);
		
		return edit;
		
	}

	/**returns the menu path*/
	@Override
	public String getMenuPath() {
	
		return "Actions";
	}
	
	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		gg.getTheInternalLayer().add(RectangularGraphic.blankRect(new Rectangle(0,0,ICON_SIZE,ICON_SIZE), TRANSPARENT_COLOR));
		
		
		if (getTheColor()!=null) {
					Rectangle r=new Rectangle(3,3, 10,10);
					if(this.bigIcon)r=new Rectangle(3,3, 20,20);
					rect = RectangularGraphic.blankRect(r, getTheColor());
					 rect.setDashes(null);
					if (!stroke) { 
							 rect = RectangularGraphic.filledRect(r);
							 rect.setFillColor(getTheColor());
							 if (this.rainbowColor) {
								 rect.setFillPaintProvider(new RainbowPaintProvider());
							 }
							 rect.setStrokeWidth(1);
							 rect.setDashes(new float[] {});
							 rect.setStrokeColor(Color.black);
							 colorObject=rect;
							 gg.getTheInternalLayer().add(rect);
					 } else {
						 rect.setStrokeWidth(4);
						 
						 rect.setStrokeColor(getTheColor());
						 if (this.rainbowColor) {
							 rect.setStrokePaintProvider(new RainbowPaintProvider());
						 }
						 rect.setFilled(false);
						 colorObject=rect;
						 rect.setStrokeJoin(BasicStroke.JOIN_MITER);
						 rect.setDashes(new float[] {});
						 rect.deselect();
						/** Shape shape = rect.getStroke().createStrokedShape(rect.getBounds());
						 ShapeGraphic sg = new BasicShapeGraphic(shape);
						 sg.setFillColor(theColor);
						 sg.setStrokeWidth(0);
						 sg.setDashes(new float[] {});
						 sg.deselect();*/
						 Rectangle r2=new Rectangle(5,5, 5,5); 
						 RectangularGraphic rect2 = new RectangularGraphic(r2); 
						 rect2.setFillColor(Color.lightGray);
						 rect2.setStrokeWidth(1); rect2.setStrokeColor(Color.black);rect2.makeNearlyDashLess();
						rect2.setStrokeCap(BasicStroke.CAP_SQUARE);
						 
						 Rectangle r3=new Rectangle(0,0, 15,15); 
						 RectangularGraphic rect3 = new RectangularGraphic(r3); 
						 rect3.setFillColor(TRANSPARENT_COLOR);
						 rect3.setStrokeWidth(1); rect3.setStrokeColor(Color.black);rect3.makeNearlyDashLess();
						
						 gg.getTheInternalLayer().add(rect3);
						 gg.getTheInternalLayer().add(rect);
						 gg.getTheInternalLayer().add(rect2);
						
					 }
					
		}
		
		if (this.getDashes()!=null||this.getStrokeWidth()!=null) {
			ArrowGraphic drawn = new ArrowGraphic(new Point(17,8), new Point(-2, 8));
			drawn.setNumerOfHeads(0);
			drawn.setDashes(getDashes());
			if(alwaysDashIcon) drawn.setDashes(new float[] {2,2});
			drawn.setStrokeColor(Color.black);
			drawn.setStrokeWidth(2);
			if (this.getStrokeWidth()!=null) drawn.setStrokeWidth(getStrokeWidth());
			if (this.getStrokeWidth()!=null&&this.getStrokeWidth()>18) drawn.setStrokeWidth(18);
			
			gg.getTheInternalLayer().add(drawn);
		}
		
		
		if (this.getStrokeJoin()!=null)  {
			PathGraphic path = new PathGraphic(new Point(7, 18));
			path.addPoint(new Point(7, 5));
			path.addPoint(new Point(21, 5));
			
			
			 		path.setStrokeJoin(getStrokeJoin());
			 		
					 path.setFillColor(null);
					 path.setStrokeWidth(10);
					 path.makeNearlyDashLess();
					 path.setStrokeColor(Color.black);
					 gg=new GraphicGroup();
					 gg.getTheInternalLayer().add(path);
			 
			
		}
		
		if (this.getStrokeCap()!=null)  {
			PathGraphic path = new PathGraphic(new Point(0, 10));
			path.addPoint(new Point(10, 10));
			//path.addPoint(new Point(21, 5));
			
			
			 		path.setStrokeCap(getStrokeCap());
					 path.setFillColor(null);
					 path.setStrokeWidth(10);
					 path.makeNearlyDashLess();
					 path.setStrokeColor(Color.black);
					 gg=new GraphicGroup();
					 gg.getTheInternalLayer().add(path);
			 
			
		}
		
		if (this.doesOpenClosePath()) {
			PathGraphic path = new PathGraphic(new Point(3, 12));
			path.setStrokeColor(Color.black);
			path.addPoint(new Point(3, 4));
			path.addPoint(new Point(8, 0));
			path.addPoint(new Point(15, 8));
			path.addPoint(new Point(15, 11));
			path.setClosedShape(this.isPathCloser());
			path.moveLocation(2, 5);
			 gg=new GraphicGroup();
			 gg.getTheInternalLayer().add(path);
		}
		
		if (this.fontStyle!=null)  {
			TextGraphic text = new TextGraphic("Bold");
			
			if (fontStyle==Font.BOLD) text.setText("Bold");
			if (fontStyle==Font.PLAIN) text.setText("Plain");
			if (fontStyle==Font.ITALIC) text.setText("Italic");
			if (fontStyle==Font.ITALIC+Font.BOLD) text.setText("B+I");
			
			text.setLocation(-2, 15);
			text.setTextColor(Color.BLACK);
			text.setFontSize(10);
			text.setFontStyle(this.fontStyle);
			
			gg.getTheInternalLayer().add(text);
			
		}
		
		
		
		if (fontUp||fontDown)  {
			TextGraphic text = new TextGraphic("A");
		
			text.setLocation(0, 15);
			text.setTextColor(Color.BLACK);
			if (fontUp)text.setFontSize(10); else text.setFontSize(16);
			
			gg.getTheInternalLayer().add(text);
			
			text = new TextGraphic("A");
			text.setLocation(8, 15);
			if (fontDown)text.setLocation(10, 15);
			text.setTextColor(Color.BLACK);
			if (fontUp)text.setFontSize(16); else text.setFontSize(10);
			
			
			gg.getTheInternalLayer().add(text);
			
		}
		
		if(this.arrowHeadNumber!=null||this.arrowStyle!=null) {
			ArrowGraphic a = new ArrowGraphic();
			if(this.getNumberOfArrowHeads()!=null)	a.setNumerOfHeads(getNumberOfArrowHeads());
			a.setPoints(new Point(2,2), new Point(18,18));
			
			a.getHead().setArrowHeadSize(10);
			if(this.getArrowStyle()!=null) {
				
				a.getHead().setArrowStyle(this.getArrowStyle());
				a.setPoints(new Point(0,9), new Point(17,9));
				if (setTargetHead==ArrowGraphic.SECOND_HEAD) {
					a.setPoints(new Point(17,9), new Point(0,9));
				}
				a.setNumerOfHeads(1);
				if(!a.getHead().isLineHead()) 
					a.getHead().setArrowHeadSize(1.5*a.getHead().getArrowHeadSize());
				else {
					a.getHead().setArrowHeadSize(0.8*a.getHead().getArrowHeadSize());
					a.setPoints(new Point(0,9), new Point(15,9));
					if (setTargetHead==ArrowGraphic.SECOND_HEAD) {a.setPoints(new Point(15,9), new Point(0,9));}
				}
			}
			a.setStrokeColor(Color.black);
			a.setStrokeWidth(4);
			gg.getTheInternalLayer().add(a);
		}
		
		
		 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		 output.setRelocatedForIcon(false);
		
		 return output;
	}
	
	
	public Icon getIcon() {
		return  getItemIcon(true);
	}

	public Color getTheColor() {
		if (theColor==null) return null;
		if( getModelItem()!=null) {
			return stroke? getModelItem().getStrokeColor(): getModelItem().getFillColor();
		}
		if(this.modelTextItem!=null) return modelTextItem.getTextColor();
		return theColor;
	}

	public void setTheColor(Color theColor) {
		this.theColor = theColor;
	}

	public ShapeGraphic getModelItem() {
		return modelItem;
	}

	public void setModelItem(ShapeGraphic modelItem) {
		this.modelItem = modelItem;
		
	}
	public void setModelTextItem(TextGraphic modelItem) {
		this.modelTextItem = modelItem;
	}

	/**returns the dashes that will be set*/
	public float[] getDashes() {
		if (dashes==null) return null;
		if(getModelItem()!=null)return getModelItem().getDashes();
		return dashes;
	}

	/**sets up the dashes*/
	public void setDashes(float[] dashes) {
		this.dashes = dashes;
	}

	public Float getStrokeWidth() {
		if(strokeWidth==null) return null;
		if(getModelItem()!=null)return getModelItem().getStrokeWidth();
		return strokeWidth;
	}
	
	public Double getMiterLimit() {
		if(this.miterLimit==null) return null;
		if(getModelItem()!=null)return (Double) getModelItem().getMiterLimit();
		return miterLimit;
	}

	public void setStrokeWidth(Float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public Integer getStrokeJoin() {
		if (strokeJoin==null) return null;
		if(getModelItem()!=null) {
			return getModelItem().getStrokeJoin();
			
			}
		return strokeJoin;
	}

	public void setStrokeJoin(Integer strokeJoin) {
		this.strokeJoin = strokeJoin;
	}

	public Integer getStrokeCap() {
		if (strokeCap==null) return null;
		if(getModelItem()!=null)return getModelItem().getStrokeCap();
		return strokeCap;
	}

	public void setStrokeCap(Integer strokeCap) {
		this.strokeCap = strokeCap;
	}

	public void setBigIcon(boolean b) {
		bigIcon=b;
		
	}


	@Override
	public void ColorChanged(ColorInputEvent fie) {
		
		
		theColor=fie.getColor();
		run();
	}
	
	public SmartPopupJMenu getPopup() {
		if(this.getStrokeWidth()==null) return null;
		SmartPopupJMenu out = new SmartPopupJMenu();
		out.add(getInputPanel());
		return out;
	}
	
	public Component getInputPanel() {
		
		if(this.getStrokeJoin()!=null)
		{
			
			return this.getMiterInput();
			}
		
		if(this.getStrokeWidth()!=null) {
			NumberInputPanel strokeWidthInput = getStrokeWidthInput();
			return getPaddedPanel(strokeWidthInput);
		}
		if(this.getDashes()!=null)
			return getDashInput();
		return null;
	}

	

	protected NumberInputPanel getStrokeWidthInput() {
		if(this.getStrokeWidth()==null) return null;
		NumberInputPanel panel = new NumberInputPanel("Stroke Width", this.getStrokeWidth(), 1,50);
		panel.setDecimalPlaces(2);
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				strokeWidth=(float) ne.getNumber();
				EditManyObjects runner = new EditManyObjects(true, (float)ne.getNumber());
				runOperation(runner);
				
			}
		});
		return panel;
	}
	
	/**returns a panel for input of a new miter limit*/
	protected NumberInputPanel getMiterInput() {
		
		NumberInputPanel panel = new NumberInputPanel("Miter Limit", this.getModelItem().getMiterLimit());
		panel.setDecimalPlaces(2);
		panel.addNumberInputListener(new NumberInputListener() {
			public void numberChanged(NumberInputEvent ne) {
				strokeWidth=(float) ne.getNumber();
				EditManyObjects runner = new EditManyObjects("Miter", "", ne.getNumber());
				runOperation(runner);
			}

			
		});
		return panel;
	}
	
	/**returns a panel for input of a new dashes*/
	protected Component getDashInput() {
		if(this.getModelItem()==null) return null;
		JMenu output = new JMenu("Current dashes");
		NumberArrayInputPanel panel=new StrokeInputPanel(this.getModelItem()).getDashInput();
		panel.placeItems(panel, 0, 0);
		panel.setArray(getDashes());
		
		panel.addNumberInputListener(new NumberInputListener() {
			public void numberChanged(NumberInputEvent ne) {
				
				float[] dashes2 = ne.getNumbers();
				EditManyObjects runner = new EditManyObjects(true, dashes2);
				runOperation(runner);
			}
		});
		output.add("Dash Lengths");
		output.add(panel);
		return output;
	}
	
	protected void runOperation(EditManyObjects runner) {
		runner.setSelector(selector);
		runner.run();
		selector.getWorksheet().updateDisplay();
	}

	public Integer getNumberOfArrowHeads() {
		if(modelItem instanceof ArrowGraphic) return ((ArrowGraphic) modelItem).getNHeads();
		return arrowHeadNumber;
	}

	public void setNumberOfArrowHeads(Integer arrowHeadNumber) {
		this.arrowHeadNumber = arrowHeadNumber;
	}

	public Integer getArrowStyle() {
		if(arrowStyle==null)
			return null;
		if(modelItem instanceof ArrowGraphic) 
		{
			return ((ArrowGraphic) modelItem).getHead(this.setTargetHead).getArrowStyle();
		}
		return arrowStyle;
	}

	public void setArrowStyle(Integer arrowStyle) {
		this.arrowStyle = arrowStyle;
	}

	/**returns true if this action sets shapes to be closed*/
	public Boolean isPathCloser() {
		if(pathClosed==null) return null;
		if (this.modelItem instanceof PathGraphic) return modelItem.isClosedShape();
		return pathClosed;
	}
	/**sets whether this action sets shapes to be closed*/
	public void setPathCloser(Boolean pathClosed) {
		this.pathClosed = pathClosed;
	}
	
	/**generates a set of operators that change the number of arrow heads*/
	public static EditManyObjects[] createOptionsforNumberOfArrowHeads() {
		EditManyObjects[] output = new EditManyObjects[3];
		for(int i=0; i<3; i++)
			output[i]=new EditManyObjects(null,i,0, 1);
		
		return output;
	}
	
	/**Creates a set of operators for changing the style of an arrow head*/
	public static EditManyObjects[] createForArrow2(int head) {
		EditManyObjects[] output = new EditManyObjects[ArrowGraphic.arrowStyleList.length];
		for(int i=0; i<ArrowGraphic.arrowStyleList.length; i++)
			output[i]=new EditManyObjects(null,ArrowGraphic.arrowStyleList[i],2, head);
		
		return output;
	}

	/**responds to the color palette popup
	 * @param fie
	 */
	public void onColorInput(ColorInputEvent fie) {
		ShapeGraphic model = getModelItem();
		setModelItem(null);
		setTheColor(fie.getColor());
		run();
		setModelItem(model);
	}
	

}

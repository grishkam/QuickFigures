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

import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.CountParameter;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import gridLayout.LayoutSpaces;
import menuUtil.SmartPopupJMenu;
import objectDialogs.StrokeInputPanel;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.ColorInputEvent;
import standardDialog.ColorInputListener;
import standardDialog.GraphicDisplayComponent;
import standardDialog.InfoDisplayPanel;
import standardDialog.NumberArrayInputPanel;
import standardDialog.NumberInputEvent;
import standardDialog.NumberInputListener;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.PathEditUndo;
import undo.SimpleItemUndo;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;
import undo.UndoTextEdit;
import utilityClassesForObjects.Fillable;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RainbowPaintProvider;
import utilityClassesForObjects.StrokedItem;

/**Applies a single color or stroke style to all the selected shapes*/
public class EditManyShapes extends BasicMultiSelectionOperator implements  LayoutSpaces, ColorInputListener,  Serializable {

	/**
	 * 
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
	

	public boolean doesStroke() {
		return stroke;
	}

	public static EditManyShapes[]  getForColors(boolean Stroke, Color... color) {
		EditManyShapes[] out = new EditManyShapes[color.length+1];
		for(int i=0; i<color.length; i++) {
			Color c1= color[i];
			out[i]=new EditManyShapes(Stroke, c1);
		}
		out[color.length]=new EditManyShapes();
		out[color.length].stroke=Stroke;
		return out;
	}

	public EditManyShapes() {
		setTheColor(Color.white);
		this.rainbowColor=true;
	}
	
	/**creates an editor for changing the properties of an arrow*/
	public EditManyShapes(ArrowGraphic a, Integer i, int type, int targetHead) {
		
		if(i==null) setArrowHeadNumber(a.getNHeads());
		else setArrowHeadNumber(i);
		if(type==2) {
			this.setArrowHeadNumber(null);
			this.setArrowStyle(i);
			this.setTargetHead=targetHead;
		}
	}
	

	public EditManyShapes(PathGraphic a, Boolean i) {
		this.setModelItem(a);
		this.setPathCloser(i);
	}

	
	
	/**creates an editor for either stroke color or fill color*/
	public EditManyShapes(boolean stroke, Color c) {
		this.stroke=stroke;;
		setTheColor(c);
	}
	
	public EditManyShapes(String special) {
		if (special.contains("up")) this.fontUp=true;
		if (special.contains("down")) this.fontDown=true;
	}
	
	public EditManyShapes(int fontStyle) {
		this.stroke=true;
		this.fontStyle=fontStyle;
	}
	
	/**creates an editor to apply dashes*/
	public EditManyShapes(boolean stroke, float[] c) {
		this.stroke=stroke;;
		setDashes(c);
	}
	
	
	/**creates an editor that alters stroke width*/
	public EditManyShapes(boolean stroke, float c) {
		this.stroke=stroke;;
		this.setStrokeWidth(c);
	}
	
	/**creates an editor that alters stroke caps*/
	public EditManyShapes(Integer join, Integer cap) {
		stroke=true;
		this.setStrokeJoin(join);
		this.setStrokeCap(cap);
	}
	
	/**creates a shape editor that alters the miter limit of the stroke*/
	public EditManyShapes(String category, String c2, double miterLimit2) {
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
		if(getArrowHeadNumber()!=null) {
			return getArrowHeadNumber()+ " Headed Arrow";
		}
		if (this.getArrowStyle()!=null) 
			return "Change Arrow Head Style";
		if(this.getMiterLimit()!=null) return "Change Miter Limit";
		return "Change "+(stroke? "Stroke":"Fill")+" Color "+t;
	}

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
		if (selector!=null&&selector.getGraphicDisplayContainer()!=null)
			selector.getGraphicDisplayContainer().getUndoManager().addEdit(edit);
		
	}
	
	

/**applies the change to one object and returns an undoable edit*/
	private AbstractUndoableEdit editShape(Object a) {
		CombinedEdit edit = new CombinedEdit();
		ColorEditUndo edit4 = new ColorEditUndo(a);
		
		if (stroke && a instanceof StrokedItem) {
			
			StrokedItem s=(StrokedItem) a;
			UndoStrokeEdit edit2 = new UndoStrokeEdit(s);
			
			if (getTheColor()!=null)s.setStrokeColor(getTheColor());
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
				b.setNumerOfHeads(this.getArrowHeadNumber());
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
			if (getTheColor()!=null &&s.isFillable())s.setFillColor(getTheColor());
		}
		
		edit4.establishFinalColors();
		edit.addEditToList(edit4);
		
		return edit;
		
	}

	@Override
	public String getMenuPath() {
	
		return "Actions";
	}
	
	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		gg.getTheLayer().add(RectangularGraphic.blankRect(new Rectangle(0,0,25,25), new Color(0,0,0,0)));
		
		//Color[] colors=new Color[] {Color.red, Color.green, Color.blue, new Color((float)0.0,(float)0.0,(float)0.0, (float)0.5)};
	
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
							 gg.getTheLayer().add(rect);
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
						 rect3.setFillColor(new Color(0,0,0,0));
						 rect3.setStrokeWidth(1); rect3.setStrokeColor(Color.black);rect3.makeNearlyDashLess();
						
						 gg.getTheLayer().add(rect3);
						 gg.getTheLayer().add(rect);
						 gg.getTheLayer().add(rect2);
						
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
			
			gg.getTheLayer().add(drawn);
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
					 gg.getTheLayer().add(path);
			 
			
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
					 gg.getTheLayer().add(path);
			 
			
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
			 gg.getTheLayer().add(path);
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
			
			gg.getTheLayer().add(text);
			
		}
		
		
		
		if (fontUp||fontDown)  {
			TextGraphic text = new TextGraphic("A");
		
			text.setLocation(0, 15);
			text.setTextColor(Color.BLACK);
			if (fontUp)text.setFontSize(10); else text.setFontSize(16);
			
			gg.getTheLayer().add(text);
			
			text = new TextGraphic("A");
			text.setLocation(8, 15);
			if (fontDown)text.setLocation(10, 15);
			text.setTextColor(Color.BLACK);
			if (fontUp)text.setFontSize(16); else text.setFontSize(10);
			
			
			gg.getTheLayer().add(text);
			
		}
		
		if(this.arrowHeadNumber!=null||this.arrowStyle!=null) {
			ArrowGraphic a = new ArrowGraphic();
			if(this.getArrowHeadNumber()!=null)	a.setNumerOfHeads(getArrowHeadNumber());
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
			gg.getTheLayer().add(a);
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

	private void setTheColor(Color theColor) {
		this.theColor = theColor;
	}

	public ShapeGraphic getModelItem() {
		return modelItem;
	}

	public void setModelItem(ShapeGraphic modelItem) {
		this.modelItem = modelItem;
	}
	public void setModelItem(TextGraphic modelItem) {
		this.modelTextItem = modelItem;
	}

	public float[] getDashes() {
		if (dashes==null) return null;
		if(getModelItem()!=null)return getModelItem().getDashes();
		return dashes;
	}

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
		
		if(this.getStrokeWidth()!=null)	
			return StandardDialog.combinePanels(getStrokeWidthInput(),  new InfoDisplayPanel("  ", ""), new InfoDisplayPanel("  ", ""));
		if(this.getDashes()!=null)
			return getDashInput();
		return null;
	}

	protected NumberInputPanel getStrokeWidthInput() {
		if(this.getStrokeWidth()==null) return null;
		NumberInputPanel panel = new NumberInputPanel("Stroke Width", this.getStrokeWidth(), 0,50);
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				strokeWidth=(float) ne.getNumber();
				EditManyShapes runner = new EditManyShapes(true, (float)ne.getNumber());
				runOperation(runner);
				
			}
		});
		return panel;
	}
	
	/**returns a panel for input of a new miter limit*/
	protected NumberInputPanel getMiterInput() {
		
		NumberInputPanel panel = new NumberInputPanel("Miter Limit", this.getModelItem().getMiterLimit());
		panel.addNumberInputListener(new NumberInputListener() {
			public void numberChanged(NumberInputEvent ne) {
				strokeWidth=(float) ne.getNumber();
				EditManyShapes runner = new EditManyShapes("Miter", "", ne.getNumber());
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
				EditManyShapes runner = new EditManyShapes(true, dashes2);
				runOperation(runner);
			}
		});
		output.add("Dash Lengths");
		output.add(panel);
		return output;
	}
	
	protected void runOperation(EditManyShapes runner) {
		runner.setSelector(selector);
		runner.run();
		selector.getGraphicDisplayContainer().updateDisplay();
	}

	public Integer getArrowHeadNumber() {
		if(modelItem instanceof ArrowGraphic) return ((ArrowGraphic) modelItem).getNHeads();
		return arrowHeadNumber;
	}

	public void setArrowHeadNumber(Integer arrowHeadNumber) {
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

	public Boolean isPathCloser() {
		if(pathClosed==null) return null;
		if (this.modelItem instanceof PathGraphic) return modelItem.isClosedShape();
		return pathClosed;
	}

	public void setPathCloser(Boolean pathClosed) {
		this.pathClosed = pathClosed;
	}
	
	
	public static EditManyShapes[] createForArrow() {
		EditManyShapes[] output = new EditManyShapes[3];
		for(int i=0; i<3; i++)
			output[i]=new EditManyShapes(null,i,0, 1);
		
		return output;
	}
	
	public static EditManyShapes[] createForArrow2(int head) {
		EditManyShapes[] output = new EditManyShapes[ArrowGraphic.arrowStyleList.length];
		for(int i=0; i<ArrowGraphic.arrowStyleList.length; i++)
			output[i]=new EditManyShapes(null,ArrowGraphic.arrowStyleList[i],2, head);
		
		return output;
	}
	

}

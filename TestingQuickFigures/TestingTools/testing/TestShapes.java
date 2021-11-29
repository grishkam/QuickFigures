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
 * Date Modified: Mar 28, 2021
 * Version: 2021.2
 */
package testing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import actionToolbarItems.EditManyObjects;
import actionToolbarItems.SetAngle;
import addObjectMenus.CartoonPolygonAdder;
import applicationAdapters.ImageWorkSheet;
import basicMenusForApp.SelectedSetLayerSelector;
import genericTools.ToolBit;
import graphicTools.ArrowGraphicTool;
import graphicTools.RectGraphicTool;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.FigureDisplayWorksheet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.ConnectorGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.RegularPolygonGraphic;
import graphicalObjects_Shapes.RightTriangleGraphic;
import graphicalObjects_Shapes.SimpleStar;
import graphicalObjects_Shapes.TrapezoidGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.miniToolbars.ShapeActionButtonHandleList2;
import imageDisplayApp.ImageWindowAndDisplaySet;
import includedToolbars.ObjectToolset1;
import layout.basicFigure.BasicLayout;
import locatedObject.AttachmentPosition;
import locatedObject.ColorDimmer;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelectionSystem;
import selectedItemMenus.MultiSelectionOperator;
import textObjectProperties.TextLineSegment;

/**contains methods that generate example worksheets containing different types of shapes*/
public class TestShapes {
	
	
	public static TestExample[] each=new TestExample[] {
			TestExample.DIVERSE_SHAPES, TestExample.RECTANGLE_AND_OTHERS, TestExample.MANY_STROKES, TestExample.MANY_COLORS, TestExample.MANY_ANGLES, TestExample.MANY_ANGLE_TEXT, TestExample.MULTIPLE_FONTS, TestExample.MULTIPLE_FONT_DIMS, TestExample.MANY_ANGLE_COMPLEX_TEXT
	,TestExample.MANY_ARROWS
	
	};
	
	/**Creates a worksheet with example objects
	 * @param type the sort of example object*/
	public static ImageWindowAndDisplaySet createExample(TestExample type) {
		ImageWindowAndDisplaySet i = ImageWindowAndDisplaySet.createAndShowNew(type.name(), 500,400);
		addExampleObjects(i, type);
		i.updateDisplay();
		return i;
	}
	
	/**creates an example image with a diversity of shapes*/
	public static void addExampleObjects(ImageWindowAndDisplaySet ids, TestExample type) {
		GraphicLayer l=ids.getImageAsWorksheet().getTopLevelLayer();
		if (type== TestExample.DIVERSE_SHAPES)addDiverseShapes(l);
		if (type==TestExample.RECTANGLE_AND_OTHERS)addAllRectangleShapeTools(l);
		if (type==TestExample.MANY_COLORS) {
			
			RectangularGraphic r = RectangularGraphic.blankRect(new Rectangle(10,10, 32, 22), Color.black);
			ShapeActionButtonHandleList2 sa = new ShapeActionButtonHandleList2(r);
			addMultipleVersions(ids, r, sa.fillColorActs(), sa.strokeColorActs());
			
		}
		
		
		if (type==TestExample.MANY_STROKES) {
			
			RectangularGraphic r = RightTriangleGraphic.blankShape(new Rectangle(20,20, 40, 30), Color.red.darker());
			r.setFillColor(Color.cyan.darker());
			r.makeNearlyDashLess();
			
			addMultipleVersions(ids, r,  ShapeActionButtonHandleList2.getCaps(), ShapeActionButtonHandleList2.getDashes());
			
		
			
			r = RightTriangleGraphic.blankShape(new Rectangle(20,20, 40, 30), Color.magenta.darker());
			r.setFillColor(Color.cyan.brighter());
			
			addMultipleVersions(ids, r, ShapeActionButtonHandleList2.getJoins(), ShapeActionButtonHandleList2.getStrokes(), 0, 200);
			
		}
		
		Rectangle rect1 = new Rectangle(20,100, 40, 30);
	if (type==TestExample.MANY_ANGLES) {
			
			
			RectangularGraphic r = RightTriangleGraphic.blankShape(rect1, Color.red.darker());
			r.setFillColor(Color.cyan.darker());
			RectangularGraphic r2 = RectangularGraphic.blankRect(new Rectangle(10,10, 32, 22), Color.black);
			r2.setFillColor(Color.green.brighter());
	
			BasicGraphicalObject[] s = new BasicGraphicalObject[] {r, r2,  CircularGraphic.blankOval(rect1, Color.cyan, CircularGraphic.PI_ARC)};
			addMultipleVersions(ids, s,createManyAngles(), 25, 25, r2.getRectangle().getBounds());

		}
	
	if (type==TestExample.MANY_ARROWS) {
		
		
		ArrowGraphic r = new ArrowGraphic(new Point(25,25), new Point(80, 80));
		r.setNumerOfHeads(2);
		
		r.setStrokeColor(Color.black);
		BasicGraphicalObject[] s = new BasicGraphicalObject[] {r};
		
		
		
		Rectangle bounds = new Rectangle(20,20, 80, 80);
		BasicLayout bl = new BasicLayout( 5,5, bounds.width, bounds.height,  bounds.y,  bounds.x, true);
		
		addMultipleVersions(ids.getImageAsWorksheet(), s, EditManyObjects.createForArrow2(ArrowGraphic.FIRST_HEAD), 0,0, bl);

	}
	
	if (type==TestExample.MANY_ANGLE_TEXT) {
		
		TextGraphic textGraphic = new TextGraphic("Plain Text");
		textGraphic.getBounds();
		rect1 = new Rectangle(0,0, 80, 30);
		LocatedObject2D[] s = new LocatedObject2D[] {  textGraphic};
		
		BasicLayout bl = new BasicLayout( 5, 5, 20, 30, 70, 70, true);
		
		
		addMultipleVersions(ids.getImageAsWorksheet(), s,createManyAngles(), 50, 50, bl);
		
		
		
	
		
	}
	
	
	if (type==TestExample.MULTIPLE_FONTS) {
		
		TextGraphic textGraphic = new TextGraphic("Plain Text");
		textGraphic.getBounds();
		rect1 = new Rectangle(0,0, 80, 30);
		
		String[] fonts =new String[] {"Arial","Monospaced", "Courier New", "SansSerif", "Times New Roman", "Helvetica"};
		int[] styles=  new int[] {Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD+Font.ITALIC};
		
		
		int step=0;
		
		for( int size=8; size<=16; size+=4)
		{
			
			int count=1;
			for(String font: fonts)
				for(int style: styles) {
					Font f=new Font(font, style, size);
					 TextGraphic t = textGraphic.copy();
					 t.setFont(f);t.setText(f.getFontName());
					 t.moveLocation(5+150*step, (5+size)*count);
					l.addItemToLayer(t);
					count++;
				}
			step++;//changes horizontal position
		}
	
		
	}
	
	
	if (type==TestExample.MULTIPLE_FONT_DIMS) {
		
		addMultipleColorText(l);
		
	
		
	}
	
if (type==TestExample.MANY_ANGLE_COMPLEX_TEXT) {
		
		
		ComplexTextGraphic createRainbow = ComplexTextGraphic.createRainbow("Hello", new int[] {1,1,2},  new Color[] {Color.red, Color.blue, Color.green});
		createRainbow.setFontSize(10);
		 createRainbow.getParagraph().addLineFromCodeString("style", Color.pink);
		 
		
		 long time=System.currentTimeMillis();
		 
		 createRainbow.getParagraph().get(0).addSegment("Super", Color.DARK_GRAY, TextLineSegment.SUPER_SCRIPT);
		 createRainbow.getParagraph().get(0).addSegment("Sub", Color.DARK_GRAY, TextLineSegment.SUB_SCRIPT);
		 createRainbow.getParagraph().get(0).addSegment("Under", Color.DARK_GRAY, TextLineSegment.NORMAL_SCRIPT).setUnderlined(true);;
		 createRainbow.getParagraph().get(0).addSegment("Strike", Color.DARK_GRAY, TextLineSegment.NORMAL_SCRIPT).setStrikeThough(true);
		 
		//createRainbow.getParagraph().get(1).addSegment("Plain", Color.DARK_GRAY, TextLineSegment.NORMAL_SCRIPT).setUniqueStyle(1+Font.PLAIN);;
		createRainbow.getParagraph().get(1).addSegment("Bold", Color.DARK_GRAY, TextLineSegment.NORMAL_SCRIPT).setUniqueStyle(1+Font.BOLD);;
		createRainbow.getParagraph().get(1).addSegment("Italic", Color.DARK_GRAY, TextLineSegment.NORMAL_SCRIPT).setUniqueStyle(1+Font.ITALIC);;
		createRainbow.getParagraph().get(1).addSegment("B+I", Color.DARK_GRAY, TextLineSegment.NORMAL_SCRIPT).setUniqueStyle(1+Font.ITALIC+Font.BOLD);;
		createRainbow.getParagraph().get(1).addSegment("Plain", Color.DARK_GRAY, TextLineSegment.NORMAL_SCRIPT).setUniqueStyle(1+Font.PLAIN);;
		 
		
	
		LocatedObject2D[] s = new LocatedObject2D[] { createRainbow};
		
		BasicLayout bl = new BasicLayout( 5, 5, 40, 80, 70, 80, true);
		
		 time=System.currentTimeMillis();
		addMultipleVersions(ids.getImageAsWorksheet(), s,createManyAngles(), 20, 70, bl);
		System.out.println("finished creating multiple angle versions of text "+(System.currentTimeMillis()-time));
		
		
		
	
		
	}
	
	
		
		
	}

	/**Adds text items of a few different colors
	 * @param l
	 */
	protected static void addMultipleColorText(GraphicLayer l) {
		TextGraphic textGraphic = new TextGraphic("Plain Text");
		textGraphic.getBounds();
		textGraphic.setTextColor(Color.red);
	
		
		int size=16;
			
			int count=1;
			for(ColorDimmer v: ColorDimmer.values())
			{
					 TextGraphic t2 = textGraphic.copy();
					 t2.setDimming(v);t2.setDimColor(true);
					 t2.setText(v.name());
					 t2.moveLocation(5, (5+size)*count);
					 
					TextGraphic t3=t2.copy();
					t3.moveLocation(200, 0);t3.setTextColor(Color.green);
					l.addItemToLayer(t2);
					l.addItemToLayer(t3);
					count++;
				}
	}

	/**
	 * @return
	 */
	public static BasicMultiSelectionOperator[] createManyAngles() {
		return SetAngle.createManyAnglesLimited();
	}
	
	public static void  addMultipleVersions(ImageWindowAndDisplaySet h, LocatedObject2D object, MultiSelectionOperator[] o1, MultiSelectionOperator[] o2) {
		 addMultipleVersions(h, object, o1, o2,0,0);
	}
	
	public static void  addMultipleVersions(ImageWindowAndDisplaySet h, LocatedObject2D object, MultiSelectionOperator[] o1, MultiSelectionOperator[] o2, double dx, double dy) {
		ImageWorkSheet l = h.getImageAsWorksheet();
		int row = o1.length;
		int col= o2.length;
		BasicLayout bl = new BasicLayout( col, row, object.getBounds().width, object.getBounds().height, object.getBounds().x, object.getBounds().y, true);
		
		createManyVersions(object, o1, o2, dx, dy, l, bl);
		
	}

	/** creates copies of an object arranged in a grid
	 * @param object
	 * @param o1 one set of edits to be done
	 * @param o2 another set of edits to be done
	 * @param dx
	 * @param dy
	 * @param l
	 * @param bl
	 */
	public static void createManyVersions(LocatedObject2D object, MultiSelectionOperator[] o1,
			MultiSelectionOperator[] o2, double dx, double dy, ImageWorkSheet l, BasicLayout bl) {
		int p=1;
		for(MultiSelectionOperator act1: o2) {
			for(MultiSelectionOperator act2: o1) {
				if(act1==null) continue;
				if(act2==null) continue;
				Rectangle2D panel = bl.getPanel(p);
				LocatedObject2D c = object.copy();
				c.setLocationUpperLeft(panel.getX(), panel.getY());
				c.select();
				l.addItemToImage(c);;
				
				
				act1.setSelector(new SelectedSetLayerSelector(l));
				act2.setSelector(new SelectedSetLayerSelector(l));
				act1.run();act2.run();
				
				c.moveLocation(dx, dy);
				c.deselect();
				
				p++;
			}
			
		}
	}
	
	
	/**Adds several objects to the displayed worksheet, */
	public static void  addMultipleVersions(ImageWindowAndDisplaySet h, LocatedObject2D[] objects, MultiSelectionOperator[] o1, double dx, double dy, Rectangle r) {
		ImageWorkSheet l = h.getImageAsWorksheet();
		int row = objects.length;
		int col=o1.length; 
		if(col>8) { row*=(row+col/8);col=8;}
		Rectangle bounds = objects[0].getBounds();
		if(r!=null) bounds=r;
		BasicLayout bl = new BasicLayout( col,row, bounds.width, bounds.height,  bounds.y,  bounds.x, true);
		
		addMultipleVersions(l, objects, o1, dx, dy, bl);
		
	}

	/**Adds several versions of the objects in the list
	 * @param objects
	 * @param o1
	 * @param dx
	 * @param dy
	 * @param l
	 * @param bl
	 */
	public static void addMultipleVersions(ImageWorkSheet l,LocatedObject2D[] objects, MultiSelectionOperator[] o1, double dx, double dy,
			 BasicLayout bl) {
		int p=1;
		for(LocatedObject2D object: objects) {
			for(MultiSelectionOperator act2: o1) {
				
				if(act2==null) continue;
				Rectangle2D panel = bl.getPanel(p);
				LocatedObject2D c = object.copy();
				
				c.select();
				l.addItemToImage(c);
				
				act2.setSelector(new SelectedSetLayerSelector(l));
				act2.run();
				if (c instanceof ArrowGraphic) {
					c.setLocation(panel.getCenterX(), panel.getCenterY());
				}
				else {c.setLocationUpperLeft(panel.getX(), panel.getY());
				     c.moveLocation(dx, dy);
				     }
				c.deselect();
				
				p++;
			}
			
		}
	}
	
	
	/**uses a selection operator to alter the objects in the list*/
	public static void diversify(ArrayList<LocatedObject2D> list, MultiSelectionOperator[] o1) {
		for(int i=0; i<list.size(); i++) {
			LocatedObject2D object = list.get(i);

			int operation=i%(o1.length);
			MultiSelectionOperator multiSelectionOperator = o1[operation];
			multiSelectionOperator.setSelector(new LayerSelectionSystem() {

				@Override
				public GraphicLayer getSelectedLayer() {
					if(object instanceof ZoomableGraphic) {
						return ((ZoomableGraphic) object).getParentLayer();
						
					}
					return null;
				}

				@Override
				public ArrayList<ZoomableGraphic> getSelecteditems() {
					if(object instanceof ZoomableGraphic) {
						ArrayList<ZoomableGraphic> arrayList = new ArrayList<ZoomableGraphic> ();
						arrayList.add((ZoomableGraphic) object);
						return arrayList;
					}
					return null;
				}

				@Override
				public FigureDisplayWorksheet getWorksheet() {
					
					return null;
				}

				@Override
				public ImageWorkSheet getImageWrapper() {
					// TODO Auto-generated method stub
					return null;
				}});
			
			multiSelectionOperator.run();
		}
	}

	/**adds many shapes to the layer
	 * @param l
	 */
	public static void addDiverseShapes(GraphicLayer l) {
		l.add(RectangularGraphic.blankRect(new Rectangle(150,100,40, 30), Color.cyan));
	
		CircularGraphic filledCircle = CircularGraphic.filledCircle(new Rectangle(50,120,80, 30));
		filledCircle.setFillColor(Color.green.darker());
		l.add(filledCircle);
		
		
		l.add(filledCircle.copy());
		filledCircle.moveLocation(-20, 100);
		filledCircle.setAngle(Math.PI/8);
		
		
		l.add(RectangularGraphic.blankRect(new Rectangle(15,10,90, 130), Color.orange));
		
		
		RegularPolygonGraphic z = new RegularPolygonGraphic(new Rectangle(250,100,60, 60));
		z.setFillColor(Color.darkGray);
		l.add(z);
		
		
		ArrowGraphic a1 = new ArrowGraphic(new Point(50,50), new Point(50, 175));
		a1.setStrokeColor(Color.pink);
		a1.setDashes(new float[] {4, 6});
		l.add(a1);
		
		l.add(a1.copy());
		a1.moveLocation(100, 20);
		a1.rotateAbout(new Point(50,65), Math.PI/6);
		a1.setStrokeColor(Color.magenta);
		
		l.add(a1.copy());
		a1.moveLocation(90, 70);
		a1.rotateAbout(new Point(50,65), Math.PI/6);
		a1.setStrokeColor(Color.cyan.darker());
		a1.setNumerOfHeads(2);
		a1.getHead().setArrowHeadSize(30*2);
		a1.getHead().setArrowStyle(ArrowGraphic.BALL_HEAD);
		
		l.add(z.copy());
		
		z.moveLocation(5, -25);
		z.scaleAbout(new Point(100,  100), 1.5);
		z.setStrokeWidth(4);
		z.setNvertex(3);
		z.setFillColor(Color.red);
		z.setStrokeColor(Color.BLUE.darker());
		
		SimpleStar star = new SimpleStar(z);
		l.add(star);
		star.setFillColor(Color.yellow);
		star.moveLocation(-70, 50);
		
		l.add(star.copy());
		star.moveLocation(-200, -20);
		star.setFillColor(Color.orange);
		star.setNvertex(12);
		star.setStrokeWidth(2);star.setStrokeColor(Color.black);
		star.setStarRatio(0.15);
		
		CircularGraphic h = CircularGraphic.halfCircle(z.getBounds());
		h.moveLocation(-25, 80);
		h.setFillColor(Color.orange.darker());
		l.add(h);
		h.setDashes(new float[] {10, 15});
		h.setStrokeWidth(10);h.setStrokeColor(Color.green);
		h.setStrokeCap(BasicStroke.CAP_ROUND);
		
		/**varies every parameter of a shape graphic*/
		for (int i2=0; i2<4; i2++){
			TrapezoidGraphic t = new TrapezoidGraphic(new Rectangle(100+i2*30,150-i2*30,80, 30));
			t.setFillColor(new Color(100, i2*40, i2*50));
			t.getParameter().setRatioToMaxLength(i2*0.2);
			t.setStrokeWidth(2*i2);
			t.setStrokeColor(Color.black); t.setDashes(new float[] {2*i2, 3*i2});
			t.setAngle(i2*Math.PI/3);
			l.add(t);
			t.scaleAbout(RectangleEdges.getLocation(RectangleEdges.CENTER, t.getBounds()), 1.1+0.2*i2);
		}
		PathGraphic p = new CartoonPolygonAdder(0).createCartoon(false, false);
		l.add(p);
		
		
		ConnectorGraphic h2 = new ConnectorGraphic(true, new Point(100, 200), new Point(165, 200));
		h2.setStrokeColor(Color.green.darker());
		l.add(h2);
		
		
		h2 = new ConnectorGraphic(false, new Point(150, 400), new Point(265, 200));
		h2.setStrokeColor(Color.red.darker());
		l.add(h2);
	}
	
	
	public static void addAllRectangleShapeTools(GraphicLayer l){
		ArrayList<ToolBit> shape = ObjectToolset1.getRectangularShapeGraphicBits();
		shape.addAll(ObjectToolset1.getCircularShapeGraphicBits());
		shape.addAll(ObjectToolset1.getRegularPolygonShapeTools());
		shape.addAll(ObjectToolset1.getArrowGraphicBits());
		
		testShapeTools(l, shape);
	
	}
	
	public static void testShapeTools(GraphicLayer l, ArrayList<?> list) {
		
		if (list.size()==0) return;
		int row = 5;
		int col=5;
		BasicLayout bl = new BasicLayout(row, col, 60, 40, 30, 30, true);
		bl.move(20, 20);
		bl.resetPtsPanels();
		
		int panel=1;
		for(int i=1; i<=list.size(); i++) {
			
			Object x = list.get(i-1);
			Color red = Color.red;
			if(x instanceof RectGraphicTool )  {
				RectGraphicTool tool=(RectGraphicTool) x;
				Rectangle2D p = bl.getPanel(panel);
				RectangularGraphic createNewRect = tool.createShape(p.getBounds());
				createNewRect .setFillColor(red);
				createNewRect .setStrokeColor(Color.black);
				l.add(createNewRect);
				TextGraphic t = new TextGraphic(createNewRect.getName());
				AttachmentPosition.defaultColLabel().snapObjectToRectangle(t, p);
				l.add(t);
				
				panel++;
			}
			
			if(x instanceof  ArrowGraphicTool )  {
				 ArrowGraphicTool tool=( ArrowGraphicTool) x;
				Rectangle2D p = bl.getPanel(panel);
				ArrowGraphic createNewRect = tool.createArrow(p.getX(), p.getY());
				createNewRect .setStrokeColor(red);
				l.add(createNewRect);
				TextGraphic t = new TextGraphic(createNewRect.getName() +"H="+createNewRect.getNHeads());
				AttachmentPosition.defaultColLabel().snapObjectToRectangle(t, p);
				l.add(t);
				
				panel++;
			}
			
		}
		
		
	}
}

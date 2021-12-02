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
 * Date Modified: Nov 28, 2021
 * Version: 2021.2
 */
package includedToolbars;


import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import genericMontageLayoutToolKit.Shift_Object_Between_Panels;
import genericTools.GeneralTool;
import genericTools.ObjectColorFillTool;
import genericTools.Object_Mover;
import genericTools.ToolBit;
import graphicActionToolbar.PSActionTool;
import graphicActionToolbar.QuickFigureMaker;
import graphicTools.ArrowGraphicTool;
import graphicTools.ComplexText_GraphicTool;
import graphicTools.ConnectorLineTool;
import graphicTools.RectGraphicTool;
import graphicTools.RegularPolygonGraphicTool;
import graphicTools.Text_GraphicTool;
import graphicalObjects_Shapes.BlobShape;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.ComplexBlobShape;
import graphicalObjects_Shapes.ComplexStar;
import graphicalObjects_Shapes.GearShape;
import graphicalObjects_Shapes.NotchedRectangleGraphic;
import graphicalObjects_Shapes.OpenRectangleGraphic;
import graphicalObjects_Shapes.OpenTriangleGraphic;
import graphicalObjects_Shapes.PlusGraphic;
import graphicalObjects_Shapes.RightTriangleGraphic;
import graphicalObjects_Shapes.RoundedRectangleGraphic;
import graphicalObjects_Shapes.SimpleRing;
import graphicalObjects_Shapes.SimpleStar;
import graphicalObjects_Shapes.TrapezoidGraphic;
import graphicalObjects_Shapes.TriangleGraphic;
import locatedObject.RectangleEdgePositions;
import graphicTools.ShapeGraphicTool;
import logging.IssueLog;
import multiChannelFigureUI.BasicChannelLabelTool;
import multiChannelFigureUI.ChannelSwapperToolBit;
import multiChannelFigureUI.InsetTool;
import multiChannelFigureUI.LutSwapperTool;
import pathGraphicToolFamily.AddRemoveAnchorPointTool;
import pathGraphicToolFamily.BrushTool;
import pathGraphicToolFamily.PathGraphicTool;
import pathGraphicToolFamily.PathReflectTool;
import pathGraphicToolFamily.PathTool;
import pathGraphicToolFamily.MoveManyPoints;

/**The main toolbar for QuickFigures. Includes all critical tools*/
public class ObjectToolset1 extends QuickFiguresToolBar{
	
	boolean includeStatusPanel=true;
	
	/**Extra tool installers. A programmer can write extensions to quickfigures
	  the plot package is such an extension. additional tools that are part of an extension 
	  are added to this arraylist*/
	static ArrayList<ToolInstallers> bonusTools=new ArrayList<ToolInstallers> ();
	
	public static ObjectToolset1 currentToolset;
	
	public void start() {
		
	}
	
	public void graphicTools() {
		
		maxGridx=12;
		
		addToolBit(new Object_Mover());
		ArrayList<ToolBit> layoutTools = LayoutToolSet.getMinimumLayoutToolBits();
		layoutTools.addAll( LayoutToolSet.getStandardLayoutToolBits());
		
		layoutTools.add(new Shift_Object_Between_Panels());
		addTool(new GeneralTool(layoutTools));
		
		addShapeTools();
		addToolBit(new ObjectColorFillTool());
		addTool(new PSActionTool());
		for(ToolBit b: ObjectToolset1.getBits()) {
		
			boolean nameChan=(b instanceof InsetTool);
			
			if (nameChan)addTool(new QuickFigureMaker());
			addToolBit(b);
			
			
		}
		
		addTool(new GeneralTool(new ChannelSwapperToolBit(), new BasicChannelLabelTool(), new LutSwapperTool()));
		
		
		for(ToolInstallers bonusTool: bonusTools) try {
			bonusTool.installTools(this);
		} catch (Throwable t) {IssueLog.logT(t);}
	
		this.setCurrentTool(this.tools.get(0));
		
	}
	
	public static void includeBonusTool(ToolInstallers tool) {bonusTools.add(tool);}
	
	
	

	static ArrayList<ToolBit> bits=null;
	public static ArrayList<ToolBit> getBits() {
		bits=null;
		if (bits==null) {
			bits=new ArrayList<ToolBit>();
			
			
			
			
			bits.add(new  InsetTool());
			
			
			
		}
		
		return bits;
	} 
	
	/** TODO: determine if these obsolete tools have any use. delete these if not
	private static ArrayList<ToolBit> getLockToolBits() {
		ArrayList<ToolBit> output = new ArrayList<ToolBit>();
		output.add(new BarGraphicTool());
		output.add(new LockGraphicTool2(false));
		output.add(new LockGraphicTool2(true));
		output.add(new LockedObjectSwapper());
		
		return output;
		
	}*/
	
	public static ArrayList<ToolBit> getRectangularShapeGraphicBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
	out.add(new RectGraphicTool());
	
	
	Rectangle standardRectangle = new Rectangle(0,0,5,5);
	
	out.add(new ShapeGraphicTool(new RoundedRectangleGraphic(standardRectangle)));
	
	out.add(new ShapeGraphicTool(new TrapezoidGraphic(standardRectangle)));

	out.add(new ShapeGraphicTool(new TriangleGraphic(standardRectangle)));
	
	
	out.add(new  ShapeGraphicTool(new RightTriangleGraphic(standardRectangle, RectangleEdgePositions.LOWER_RIGHT)));
	out.add(new  ShapeGraphicTool(new RightTriangleGraphic(standardRectangle, RectangleEdgePositions.LOWER_LEFT)));
	out.add(new ShapeGraphicTool(new PlusGraphic(standardRectangle)));
	out.add(new ShapeGraphicTool(new NotchedRectangleGraphic(standardRectangle)));
	
	
	return out;}
	
	public static ArrayList<ToolBit> getCircularShapeGraphicBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		
		Rectangle standardRectangle = new Rectangle(0,0,5,5);
		out.add(new ShapeGraphicTool(new CircularGraphic(standardRectangle)));
		

		out.add(new ShapeGraphicTool(new SimpleRing(standardRectangle, CircularGraphic.NO_ARC)));
		
		out.add(new ShapeGraphicTool(new SimpleRing(standardRectangle, CircularGraphic.PI_ARC)));
		
		
		Rectangle sRectangle = new Rectangle(0,0, 2,2);
		out.add(new RegularPolygonGraphicTool(new GearShape(sRectangle,12, 0.75)));
		out.add(new RegularPolygonGraphicTool(new BlobShape(sRectangle,7, 0.75)));
		out.add(new RegularPolygonGraphicTool(new ComplexBlobShape(sRectangle,7, 0.75)));

		out.add(new ShapeGraphicTool(new CircularGraphic(standardRectangle, CircularGraphic.PI_ARC)));
		out.add(new ShapeGraphicTool(new CircularGraphic(standardRectangle, CircularGraphic.CHORD_ARC)));

		return out;
	}
	
	public static ArrayList<ToolBit> getRegularPolygonShapeTools() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		
		out.add(new RegularPolygonGraphicTool(5));//5 for pentagon
		out.add(new RegularPolygonGraphicTool(6));//6 for hexagon
		out.add(new RegularPolygonGraphicTool(4));//4 for diamond
		
		
		out.add(new RegularPolygonGraphicTool(new SimpleStar(new Rectangle(0,0, 2,2),5)));
		out.add(new RegularPolygonGraphicTool(new ComplexStar(new Rectangle(0,0, 2,2),5)));
		
		return out;
	}
	
	static ArrayList<ToolBit> getTextToolBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		
		out.add(new ComplexText_GraphicTool(true));
		out.add(new Text_GraphicTool(false));
		//out.add(new ComplexText_GraphicTool(false));
		out.addAll(LayoutToolSet.getLayoutLabelBits3());
		
		return out;
	}
	
	static ArrayList<ToolBit> getPathGraphicBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		
		out.add(new PathGraphicTool(false,0));
		out.add(new PathGraphicTool(false,1));
		out.add(new PathGraphicTool(true, 0));
		out.add(new BrushTool(false,0));
		out.add(new AddRemoveAnchorPointTool(false));
		out.add(new AddRemoveAnchorPointTool(true));
		out.add(new PathTool(false, false));
		out.add(new PathTool(true, false));
		out.add(new PathTool(true, true));
		
		out.add(new MoveManyPoints());
		out.add(new PathReflectTool(PathReflectTool.REFLECT));
		out.add(new PathReflectTool(PathReflectTool.SCALE));
		out.add(new PathReflectTool(PathReflectTool.ROTATE));
		out.add(new PathReflectTool(PathReflectTool.MOVE));
		
		return out;
		
	}
	
	public static ArrayList<ToolBit> getArrowGraphicBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		out.add(new ArrowGraphicTool(1));
		out.add(new ArrowGraphicTool(2));
		out.add(new ArrowGraphicTool(2, true));
		out.add(new ArrowGraphicTool(0));
		return out;
		
	}
	
	/**Returns tool for drawing simple lines*/
	public static ArrayList<ToolBit> getLineGraphicBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		
		Rectangle standardRectangle = new Rectangle(0,0,5,5);
		out.add(new ShapeGraphicTool(new OpenRectangleGraphic(standardRectangle)));
		out.add(new ShapeGraphicTool(new OpenTriangleGraphic(standardRectangle)));
		out.add(new ConnectorLineTool(3,true));
		out.add(new ConnectorLineTool(3,false));
		out.add(new ConnectorLineTool(2,true));
		out.add(new ConnectorLineTool(2,false));
		ArrowGraphicTool e = new ArrowGraphicTool(0);
		e.getModelArrow().setStrokeWidth(1);
		out.add(e);
		
	
		return out;
		
	}
	
	public void addToolBit(ToolBit t) {
		addTool(new  GeneralTool( t));
	}
	
	

	public void addShapeTools() {
	
		addTool(new  GeneralTool( getRectangularShapeGraphicBits()));
		addTool(new  GeneralTool( getCircularShapeGraphicBits()));
		
		addTool(new  GeneralTool( getRegularPolygonShapeTools()));
		addTool(new  GeneralTool( getArrowGraphicBits() ));
		addTool(new  GeneralTool( getLineGraphicBits() ));
		
		addTool(new  GeneralTool( new BrushTool(false, 0)));
		addTool(new  GeneralTool( getPathGraphicBits()));
		addTool(new  GeneralTool( getTextToolBits()));
		
		//addTool(new GeneralTool(getLockToolBits()));
	}
	
	
	/**shows the window with the toolbar*/
public void run(String s) {
		
		
		if (currentToolset!=null&&currentToolset!=this) 
			currentToolset.getframe().setVisible(false);//removes the previous toolbar
		
		graphicTools();
	

		//if (this.usesMenuBar())getframe().setJMenuBar(new MenuBarForApp());
		showFrame();
		currentToolset=this;
		this.getframe().setLocation(new Point(5, 150));
		
		selectDefaultTool();
		
		addDragAndDrop();
		
	}

protected boolean usesMenuBar() {
	return true;
}


private void selectDefaultTool() {
	setCurrentTool(new Object_Mover().getToolName());
}
	


	public void showFrame() {
		super.showFrame();
		getframe().setTitle("Object Tools");
		addToolKeyListeners();
		
		if (includeStatusPanel) {
				GridBagConstraints c=new GridBagConstraints(); 
				c.anchor=GridBagConstraints.WEST;
				c.gridy=3; 
				c.gridx=0;
				getframe().add(new StatusPanel() ,c ); 
		}
		getframe().pack();
		
	}
}

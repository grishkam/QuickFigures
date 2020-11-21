package includedToolbars;


import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuBarForApp;
import externalToolBar.InterfaceExternalTool;
import externalToolBar.ToolBarManager;
import genericMontageUIKit.GeneralTool;
import genericMontageUIKit.ObjectColorFillTool;
import genericMontageUIKit.Roi_Into_Multiple_Panels2;
import genericMontageUIKit.Object_Mover;
import genericMontageUIKit.ToolBit;
import graphicActionToolbar.PSActionTool;
import graphicActionToolbar.QuickFigureMaker;
import graphicTools.ArrowGraphicTool;
import graphicTools.BarGraphicTool;
import graphicTools.CircleGraphicTool;
import graphicTools.ComplexText_GraphicTool;
import graphicTools.LockGraphicTool2;
import graphicTools.LockedObjectSwapper;
import graphicTools.RectGraphicTool;
import graphicTools.RegularPolygonGraphicTool;
import graphicTools.RightTriangleGraphicTool;
import graphicTools.RingGraphicTool;
import graphicTools.RoundRectGraphicTool;
import graphicTools.Text_GraphicTool;
import graphicTools.OtherShapeGraphicTool;
import graphicalObjects_BasicShapes.BlobShape;
import graphicalObjects_BasicShapes.ComplexBlobShape;
import graphicalObjects_BasicShapes.ComplexStar;
import graphicalObjects_BasicShapes.GearShape;
import graphicalObjects_BasicShapes.NotchedRectangleGraphic;
import graphicalObjects_BasicShapes.PlusGraphic;
import graphicalObjects_BasicShapes.SimpleStar;
import graphicalObjects_BasicShapes.TrapezoidGraphic;
import graphicalObjects_BasicShapes.TriangleGraphic;
import logging.IssueLog;
import multiChannelFigureUI.BasicChannelLabelTool;
import multiChannelFigureUI.ChannelSwapperToolBit;
import multiChannelFigureUI.InsetTool;
import multiChannelFigureUI.LutSwapperTool;
import pathGraphicToolFamily.AddRemoveAnchorPointTool;
import pathGraphicToolFamily.BrushTool;
import pathGraphicToolFamily.PathGraphicTool;
import pathGraphicToolFamily.PathReflactTool;
import pathGraphicToolFamily.PathTool;
import pathGraphicToolFamily.PathTool2;

public class ObjectToolset1 extends QuickFiguresToolBar{
	
	static ArrayList<ToolInstallers> bonusTools=new ArrayList<ToolInstallers> ();
	
	public static ObjectToolset1 currentToolset;
	
	public void start() {
		
	}
	
	public void graphicTools() {
		//Roi_Mover mover = new Roi_Mover();
		//mover.setExcludedClass(PanelLayoutGraphic.class);
		
		
		
		maxGridx=11;
		
		addToolBit(new Object_Mover());
		ArrayList<ToolBit> layoutTools = LayoutToolSet.getMinimumLayoutToolBits();
		layoutTools.addAll( LayoutToolSet.getStandardLayoutToolBits());
		//layoutTools.addAll(LayoutToolSet.getOptionalToolBits() );
		layoutTools.add(new Roi_Into_Multiple_Panels2());
		addTool(new GeneralTool(layoutTools));
		
		addShapeTools();
		addToolBit(new ObjectColorFillTool());
		addTool(new PSActionTool());
		for(ToolBit b: ObjectToolset1.getBits()) {
		
			boolean nameChan=(b instanceof InsetTool);
			
			if (nameChan)addTool(new QuickFigureMaker());
			addToolBit(b);
			
			
		}
		
		//addTool(new GeneralTool(new LutSwapperTool()));
		addTool(new GeneralTool(new ChannelSwapperToolBit(), new BasicChannelLabelTool(), new LutSwapperTool()));
		//addTool(new GeneralTool(new BasicChannelLabelTool()));
		
		
		
		
		//addTool(new QuickFigFromXML() );
		
		for(ToolInstallers bonusTool: bonusTools) try {
			bonusTool.installTools(this);
		} catch (Throwable t) {IssueLog.logT(t);}
	
		this.setCurrentTool(this.tools.get(0));
		
	}
	
	public static void includeBonusTool(ToolInstallers tool) {bonusTools.add(tool);}
	//AdapterKit<DisplayedImageWrapper> ak=new AdapterKit <DisplayedImageWrapper>(new ToolAdapterG());
	
	public void setCurrentTool(InterfaceExternalTool<DisplayedImage> currentTool) {
		super.setCurrentTool(currentTool);
		ToolBarManager.setCurrentTool(currentTool);
	}
	

	static ArrayList<ToolBit> bits=null;
	public static ArrayList<ToolBit> getBits() {
		bits=null;
		if (bits==null) {
			bits=new ArrayList<ToolBit>();
			
			
			
		
		
			
			bits.add(new  InsetTool());
			
			
			
		}
		
		return bits;
	} 
	
	static ArrayList<ToolBit> getLockToolBits() {
		ArrayList<ToolBit> output = new ArrayList<ToolBit>();
		output.add(new BarGraphicTool());
		output.add(new LockGraphicTool2(false));
		output.add(new LockGraphicTool2(true));
		output.add(new LockedObjectSwapper());
		
		return output;
		
	}
	
	public static ArrayList<ToolBit> getRectangularShapeGraphicBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
	out.add(new RectGraphicTool());
	out.add(new RoundRectGraphicTool());
	out.add(new OtherShapeGraphicTool(new TrapezoidGraphic(new Rectangle(0,0,5,5))));

	out.add(new OtherShapeGraphicTool(new TriangleGraphic(new Rectangle(0,0,5,5))));
	out.add(new RightTriangleGraphicTool(2));
	out.add(new RightTriangleGraphicTool(3));
	out.add(new OtherShapeGraphicTool(new PlusGraphic(new Rectangle(0,0,5,5))));
	out.add(new OtherShapeGraphicTool(new NotchedRectangleGraphic(new Rectangle(0,0,5,5))));
	return out;}
	
	public static ArrayList<ToolBit> getCircularShapeGraphicBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		out.add(new CircleGraphicTool(0));
	
	
		
		out.add(new RingGraphicTool(0));
		out.add(new RingGraphicTool(1));
		out.add(new RegularPolygonGraphicTool(new GearShape(new Rectangle(0,0, 2,2),12, 0.75)));
		out.add(new RegularPolygonGraphicTool(new BlobShape(new Rectangle(0,0, 2,2),7, 0.75)));
		out.add(new RegularPolygonGraphicTool(new ComplexBlobShape(new Rectangle(0,0, 2,2),7, 0.75)));

		
		out.add(new CircleGraphicTool(1));
		out.add(new CircleGraphicTool(2));
		return out;
	}
	
	public static ArrayList<ToolBit> getRegularPolygonShapeTools() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		
		out.add(new RegularPolygonGraphicTool(5));
		out.add(new RegularPolygonGraphicTool(6));
		//out.add(new RegularPolygonGraphicTool(3));
		out.add(new RegularPolygonGraphicTool(4));
		
		

		//out.add(new RightTriangleGraphicTool(13));
		//out.add(new RightTriangleGraphicTool(20));
		
		//out.add(new RightTriangleGraphicTool(2));
		//out.add(new RightTriangleGraphicTool(3));
		
		out.add(new RegularPolygonGraphicTool(new SimpleStar(new Rectangle(0,0, 2,2),5)));
		out.add(new RegularPolygonGraphicTool(new ComplexStar(new Rectangle(0,0, 2,2),5)));
		
		return out;
	}
	
	static ArrayList<ToolBit> getTextToolBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		
		out.add(new ComplexText_GraphicTool(true));
		out.add(new Text_GraphicTool(false));
		out.add(new ComplexText_GraphicTool(false));
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
		
		out.add(new PathTool2());
		out.add(new PathReflactTool(0));
		out.add(new PathReflactTool(1));
		out.add(new PathReflactTool(2));
		out.add(new PathReflactTool(3));
		//out.add(new PathAnchorPointTool());
		return out;
		
	}
	
	public static ArrayList<ToolBit> getArrowGraphicBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		out.add(new ArrowGraphicTool(1));
		out.add(new ArrowGraphicTool(2));
		out.add(new ArrowGraphicTool(0));
		return out;
		
	}
	
	public void addToolBit(ToolBit t) {
		addTool(new  GeneralTool( t));
	}
	
	/**public static ArrayList<ToolBit> moverBits() {
		ArrayList<ToolBit> out = new ArrayList<ToolBit>();
		Object_Mover mover = new Object_Mover();
		
		//mover.setExcludedClass(PanelLayoutGraphic.class);
		out.add(mover);
		out.add(new  Roi_Into_Multiple_Panels2());
		out.add(new LayoutMover());
		return out;
	}*/
	
	

	public void addShapeTools() {
	
		addTool(new  GeneralTool( getRectangularShapeGraphicBits()));
		addTool(new  GeneralTool( getCircularShapeGraphicBits()));
		
		addTool(new  GeneralTool( getRegularPolygonShapeTools()));
		addTool(new  GeneralTool( getArrowGraphicBits() ));
		addTool(new  GeneralTool( new BrushTool(false, 0)));
		addTool(new  GeneralTool( getPathGraphicBits()));
		addTool(new  GeneralTool( getTextToolBits()));
		
		//addTool(new GeneralTool(getLockToolBits()));
	}
	
	
	
public void run(String s) {
		
		
		if (currentToolset!=null&&currentToolset!=this) currentToolset.getframe().setVisible(false);
		//innitialize();
		graphicTools();
	

		getframe().setJMenuBar(new MenuBarForApp());
		showFrame();
		currentToolset=this;
		this.getframe().setLocation(new Point(5, 150));
		
		selectDefaultTool();
		
		addDragAndDrop();
		
		//getframe().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}



private void selectDefaultTool() {
	setCurrentTool(new Object_Mover().getToolName());
}
	
	public void showFrame() {
		super.showFrame();
		getframe().setTitle("Object Tools");
		addToolKeyListeners();
		
		GridBagConstraints c=new GridBagConstraints(); 
		c.anchor=GridBagConstraints.WEST;
		c.gridy=3; 
		c.gridx=0;
		getframe().add(new StatusPanel() ,c ); 
		getframe().pack();
		// statusPanel.currentStatus.updateStatus("showing bar", 0);
	}
}

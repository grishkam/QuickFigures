package graphicTools;

import java.awt.Color;

import applicationAdapters.ImageWrapper;
import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import utilityClassesForObjects.LocatedObject2D;

public class ArrowGraphicTool extends GraphicTool {
	private boolean line=false;

	ArrowGraphic model = new ArrowGraphic(); {} //{super.temporaryTool=true;}
	
	
	
	void setUpModel() {super.set=TreeIconWrappingToolIcon.createIconSet(model);model.setStrokeColor(Color.black);}
		{setUpModel(); }
	public ArrowGraphicTool() {
		//setUpModel();
	}
	public ArrowGraphicTool(int head) {
		this.line=head==0;
		if (line) {
			model = new ArrowGraphic();
			model.setHeadnumber(0);
			model.setArrowStyle(0);
		} else {
			model = new ArrowGraphic();
			model.setHeadnumber(head);
			model.setArrowStyle(0);
		}
		setUpModel();
	}
	
	
	
	
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
	
		if (getSelectedObject() instanceof ArrowGraphic) return;
		
		ArrowGraphic bg = new ArrowGraphic();
		if (line) bg.setName("Line");
		bg.copyAttributesFrom(model);
		bg.copyArrowAtributesFrom(model);
		bg.copyColorsFrom(model);
		
		bg.setLocation(getClickedCordinateX(), getClickedCordinateY());
		//bg.setStrokeColor(getForeGroundColor());
		setSelectedObject(bg);
		setSelectedHandleNum(1);
		super.setPressedSmartHandle(bg.getSmartHandleList().getHandleNumber(1));;
		
		GraphicLayer layer = gmp.getGraphicLayerSet().getSelectedContainer();
				layer.add(bg);
				addUndoerForAddItem(gmp, layer, bg);
				
		//bg.showOptionsDialog();
		gmp.updateDisplay();
		
		
	}
	
	
	@Override
	public void showOptionsDialog() {
		model.showOptionsDialog();
	}
	
	@Override
	public String getToolTip() {
			
			return "Draw an Arrow";
		}
	

	@Override
	public String getToolName() {
		if(line) return "Draw Line";
		return "Draw Arrow";
	}

}

package graphicTools;

import java.awt.Color;
import java.awt.Point;

import applicationAdapters.ImageWrapper;
import externalToolBar.TreeIconWrappingToolIcon;
import figureTemplates.ScaleBarPicker;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.CompoundEdit2;
import undo.UndoAddItem;
import undo.UndoTakeLockedItem;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.SnappingPosition;
public class BarGraphicTool extends GraphicTool {
	
	private static BarGraphicTool currentBar;
	
	public BarGraphicTool() {
		BarGraphicTool.currentBar = this;
	}
	
	BarGraphic model = new BarGraphic(); {
		
		model.setFillColor(Color.white);
	model.setSnappingBehaviour(SnappingPosition.defaultScaleBar());
	model.setProjectionType(2);
	}
	
	BarGraphic modelIcon() {
		BarGraphic output = model.copy();
		output.setFillColor(Color.black);
		output.getBarText().setTextColor(Color.black);
		return output;
	}
	
	{super.set=TreeIconWrappingToolIcon.createIconSet(modelIcon());}
	
	/**creates a scale bar for image panel roi2.
	 * @return */
	public BarGraphic getOrcreateBar(ImageWrapper gmp, LocatedObject2D roi2) {
		if (roi2 instanceof 	BarGraphic) {
			return (BarGraphic) roi2;
		}
		GraphicLayer selectedContainer = gmp.getGraphicLayerSet().getSelectedContainer();
			CompoundEdit2 undo=new CompoundEdit2();
			int x = getClickedCordinateX();
			int y = getClickedCordinateY();
			
		BarGraphic bg = addBarGraphic( roi2, selectedContainer, undo, x, y);
		
		gmp.updateDisplay();
		this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
		
		bg.showOptionsDialog();
		
		return bg;
	}


	/**adds a scale bar to the image*/
	public BarGraphic addBarGraphic(LocatedObject2D roi2, GraphicLayer selectedContainer,
			CompoundEdit2 undo, int x, int y) {
		BarGraphic bg = new BarGraphic();
	
		if(bg!=null) {
			bg.copyAttributesFrom(model);
			bg.copyColorsFrom(model);
			bg.setLocation(x, y);
	}
	
		
		if (roi2 instanceof ImagePanelGraphic ) {
			ImagePanelGraphic b=(ImagePanelGraphic) roi2;
			b.addLockedItem(bg);
			undo.addEditToList(new UndoTakeLockedItem(b, bg, false));
			
			/**	double[] dims = b.getScaleInfo().convertPixelsToUnits(new Dimension(b.getUnderlyingImageWidth(), b.getUnderlyingImageHeight()));
			double num = NumberUse.findNearest(dims[0]/3, BarGraphic.reccomendedBarLengths);
			bg.setLengthInUnits(num);*/
			
			BarGraphic.optimizeBar(bg, b);
			
			 new ScaleBarPicker(model).setBarDefaultsBasedOnHeight(b.getBounds().height, bg, model.getFillColor());
		
			bg.getSnappingBehaviour().setToNearestInternalSnap(bg.getBounds(), b.getBounds(), new Point(x, y));
			//bg.setLengthInUnits(length);
			
			//bg.setSnappingBehaviour(SnappingBehaviour.defaultScaleBar());
			
			//bg.setScaleProvider((ScalededItem) roi2);
		
						} /**else if (gmp instanceof ImagePlusWrapper) {
							ImagePlusWrapper ip=(ImagePlusWrapper) gmp;
							IJ1Bar bg2 = new IJ1Bar();
							bg2.updateContainerImageObject(ip.getImagePlus());
							bg2.setScaleInfo(getImageWrapperClick().getScaleInfo());
							bg=bg2;
						}*/
		
		selectedContainer.add(bg);
		
		UndoAddItem undo2 = new UndoAddItem(selectedContainer, bg);
		undo.addEditToList(undo2);
		
		
		
		
		return bg;
	}
	
	
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		getOrcreateBar(gmp, roi2);
		
	}
	
	@Override
	public void showOptionsDialog() {
		model.showOptionsDialog();
		{super.set=TreeIconWrappingToolIcon.createIconSet(modelIcon());}
	}
	
	
	@Override
	public String getToolTip() {
			return "Add a scale bar";
		}
	
	@Override
	public String getToolName() {
			return "Add Scale Bar";
		}


	public static BarGraphicTool getCurrentBarTool() {
		if(currentBar==null) currentBar=new BarGraphicTool();
		return currentBar;
	}




}
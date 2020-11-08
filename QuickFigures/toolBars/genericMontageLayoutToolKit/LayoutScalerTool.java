package genericMontageLayoutToolKit;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import externalToolBar.IconSet;
import genericMontageKit.BasicOverlayHandler.LocatedObjectFilter;
import genericMontageUIKit.Object_Mover;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.FigureScaler;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import standardDialog.BooleanInputPanel;
import standardDialog.StandardDialog;
import undo.CompoundEdit2;
import undo.UndoLayoutEdit;
import utilityClasses1.ArraySorter;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.LocatedObject2D;

public class LayoutScalerTool extends Object_Mover {
	{super.bringSelectedToFront=true; 
	super.onlySelectThoseOfClass=PanelLayoutGraphic.class;
	set= new IconSet(
			new LayoutScaleIcon(0),
			new LayoutScaleIcon(1),
			new LayoutScaleIcon(2)
			);
	}
	
	boolean continus=false;
	boolean keepPPI=false;
	boolean rezisePanels=false;
	
	 MontageLayoutGraphic theLayout=null;
	private UndoLayoutEdit undoOriginalUndo;
	private GenericMontageEditor editor;
	private double scale;
	private Point2D loc;
	private double yMin;
	private boolean minimalDrag;
	private MontageLayoutGraphic duplicate;
	@Override
	public String getToolTip() {
			
			return "Scale Layouts";
		}
	

	@Override
	public String getToolName() {
			
			return "Scale Layouts";
		}
	
	public void mousePressed() {
		super.mousePressed();
		this.minimalDrag=false;
		if (this.getSelectedObject() instanceof MontageLayoutGraphic) {
			theLayout=(MontageLayoutGraphic) getSelectedObject();
			theLayout.generateCurrentImageWrapper();
			yMin=theLayout.getPanelLayout().getBoundry().getBounds().getMinY();
			duplicate=theLayout.copy();
			duplicate.setStrokeWidth(8);
			duplicate.boundryColor=Color.cyan;
			duplicate.panelColor=Color.magenta;
			getImageWrapperClick().getSelectionManagger().setSelection(duplicate, 0);
		} else {
			eliminateSelection();
	
			
		}
		
	}


	public void eliminateSelection() {
		duplicate=null;
		getImageWrapperClick().getSelectionManagger().setSelection(null, 0);
	}
	
	public void mouseReleased() {
		if (!minimalDrag) {
			if (theLayout==null) return;
			/**repeated up and down scalings can cause the layout to fits its contents poorly,
			   this fixes it. If the layouts and panel sizes had double precision then this would 
			   not be an issue. However this helps*/
			new GenericMontageEditor().alterPanelWidthAndHeightToFitContents(this.theLayout.getPanelLayout());
			eliminateSelection();
			return;
		}
		if (undoOriginalUndo!=null)
			{
			undoOriginalUndo.undo(); 
			undoOriginalUndo=null;
			}
		//this.getImageDisplayWrapperClick().getUndoManager().addEdit(undoOriginalUndo);
		
		if (theLayout==null) return;
		FigureScaler scaler = new FigureScaler(keepPPI);
		
		
		CompoundEdit2 undo1 = scaler.scaleFigure(theLayout, scale, loc);
		this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo1);
		
		if (theLayout!=null) {
			BasicMontageLayout ml = theLayout.getPanelLayout();
			ml.resetPtsPanels();
			UndoLayoutEdit undo2 = new UndoLayoutEdit(theLayout);
			editor=new GenericMontageEditor();
			//editor.setQualificationsForPanelObject(new careFullPanelItentifier());
			
			editor.placePanelsInCorners(ml, new ArraySorter<LocatedObject2D>().getThoseOfClass(ml.getWrapper().getLocatedObjects(), ImagePanelGraphic.class));
			
			
			//getImageWrapperClick().updateDisplay();
			//theLayout.generateCurrentImageWrapper();
			//new GenericMontageEditor().alterPanelWidthAndHeightToFitContents(ml);
			if (rezisePanels) editor.alterPanelWidthAndHeightToFitContents(ml);
			undo2.establishFinalLocations();
			undo1.addEditToList(undo2);
			
		}
		eliminateSelection();
		super.resizeCanvas();
		this.getImageWrapperClick().updateDisplay();
	}
	
	public void mouseDragged() {
		minimalDrag=true;
		double[] possibleScales = new double[] { .50, 0.75, 0.8,  1, 1.2, 1.5, 1.75, 1.8,  2, 2.25,  2.5,  3, 4,1,1,1,1,};
		if (undoOriginalUndo!=null) {
			undoOriginalUndo.undo();
			undoOriginalUndo=null;
			duplicate.getPanelLayout().resetPtsPanels();
		}
		CompoundEdit2 curentEdit = new CompoundEdit2();;
		
		
		if (duplicate!=null) {
			BasicMontageLayout ml =duplicate.getPanelLayout();
		
			ml.resetPtsPanels();//must be done or strange things happen later. makes no sense why as other methods call this one
			
			
			
			double w1 = ml.getBoundry().getBounds().getMaxY()-yMin;
			double w2 = this.getDragCordinateY()-yMin;
			
			loc = ml.getReferenceLocation();
			
			Double ratio=w2/w1;
			
			scale=NumberUse.findNearest(ratio, possibleScales);
			if  (this.continus) scale=ratio;
			
			undoOriginalUndo=new UndoLayoutEdit(ml);
			duplicate.scaleAbout(loc, scale);
			
			undoOriginalUndo.establishFinalLocations();
			
		}
		this.getImageDisplayWrapperClick().updateDisplay();
	}
	
	
	class LayoutScaleIcon extends LayoutShowingToolIcon implements Icon{
		

		public LayoutScaleIcon(int rollover) {
			super(rollover);
			}

		@Override
		public BasicMontageLayout getDrawnLayout() {
			BasicMontageLayout layout = new BasicMontageLayout(2, 2, 3, 3, 2,2, true);
			
				if (super.type==2)
					layout = new BasicMontageLayout(2, 2, 6, 6, 2,2, true);
				layout.setLabelSpaces(2, 2,2,2);
				layout.move(2,2);
				return layout;
		}

		
		
		
	}
	
	class careFullPanelItentifier implements LocatedObjectFilter {

		@Override
		public boolean isObjectDesireableForPanel(Rectangle2D gra, LocatedObject2D objects) {
		
			Rectangle bPanel = gra.getBounds();
			Rectangle bOb = objects.getBounds();
			
			if (bOb.getCenterX()+  bOb.getWidth()*.25 >bPanel.getMaxX()) return false;
			if (bOb.getCenterY()+ bOb.getHeight()*0.25>bPanel.getMaxY()) return false;
			
			return true;
		}}
	
	
	public void showOptionsDialog() {
		StandardDialog sd = new StandardDialog("Layout Scaling Tool Options", true);
		sd.add("PPI", new BooleanInputPanel("Attempt to maintain image PPI", keepPPI));
		sd.add("levels", new BooleanInputPanel("Allow any scale factor", this.continus));
		sd.add("resize", new BooleanInputPanel("Resize Row/Columns for contents", this.rezisePanels));
		
		sd.showDialog();
		
		if (sd.wasOKed())  {
			keepPPI=sd.getBoolean("PPI");
			 this.continus=sd.getBoolean("levels");
			 this.rezisePanels=sd.getBoolean("resize");
		}
	}
	
	
}
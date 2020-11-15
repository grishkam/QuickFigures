package objectDialogs;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import standardDialog.AngleInputPanel;
import standardDialog.BooleanInputPanel;
import standardDialog.ColorDimmingBox;
import standardDialog.ComboBoxPanel;
import standardDialog.FontChooser;
import undo.Edit;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.SnappingPosition;

public class MultiTextGraphicSwingDialog extends TextGraphicSwingDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean unifyPosition=false;

	private boolean hasComplexGraphic;

	private boolean sameSnap;
	
	public MultiTextGraphicSwingDialog() {
		super();
	}
	
	public MultiTextGraphicSwingDialog(ArrayList<? extends ZoomableGraphic> zs, boolean sameSnap) {
		super();
		if (zs!=null)this.setGraphics(zs);
		if (array.size()==0) return;
		addOptionsToDialog();
		this.sameSnap=sameSnap;
	}
	
	public  ComplexTextGraphic getComplex(ArrayList<?> arr) {
		
		
		for( Object a:arr) {
			if (a instanceof ComplexTextGraphic) {
				return ( ComplexTextGraphic)a;
			}
			}
		return null;
	}
	
	public void setGraphics(ArrayList<? extends ZoomableGraphic> zs) {
		array=new ArrayList<TextGraphic>();
		addGraphicsToArray(array, zs);
		if (array.size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		
		ComplexTextGraphic comp = getComplex(array);
		if (comp!=null)  {this.hasComplexGraphic=true;
		super.textItem=comp;
		} else
		
			super.textItem	=array.get(0);
		super.undoableEdit=Edit.createGenericEdit(zs);
	}
	
	
	public void addGraphicsToArray(ArrayList<TextGraphic> array, ArrayList<? extends ZoomableGraphic> zs) {
		for(ZoomableGraphic z:zs) {
			if (z instanceof TextGraphic) {array.add((TextGraphic) z);}
			if (z instanceof BarGraphic) {array.add(((BarGraphic) z).getBarText() );}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}
	
	protected void addOptionsToDialog() {
		this.addFixedEdgeToDialog(textItem);
		if (this.hasComplexGraphic) this.addJustificationToDialog(textItem);
		FontChooser sb = new FontChooser(textItem.getFont());
		add("font", sb);
		
		AngleInputPanel pai2 = new AngleInputPanel("Angle ", textItem.getAngle(), true);
		add("angle", pai2);
		ComboBoxPanel cp = new ComboBoxPanel("Color Dims ",  new ColorDimmingBox(textItem.getDimming()));
		this.add("dim", cp);
		addBackgroundOptionsToDialog();
	
		for(TextGraphic t: array) {
			super.bgDialog. addShapeToDialog(t.getBackGroundShape());
		}
		
		addSnappingBehviourToDialog(textItem);
	}
	
	protected void addBackgroundOptionsToDialog() {
		this.add("backGround", new BooleanInputPanel("Use background", textItem.isFillBackGround()));
		addInsetsTab();
		
		
		ArrayList<Object> listBG=new ArrayList<Object>();
		for(TextGraphic t: array) {
			listBG.add(t.getBackGroundShape());
		}
		
		bgDialog=new ShapeGraphicOptionsSwingDialog(listBG, false);
		
		this.addSubordinateDialog("Background", bgDialog);
	
		
	}
	
	void addInsetsTab() {
		
		TextInsetsDialog id = new TextInsetsDialog(array, false);
		this.addSubordinateDialog("Insets", id);
		//this.getOptionDisplayTabs().addTab("Insets", id.removeOptionsTab());
		
	}
	
	public void setObjectSnappingBehaviourToDialog(LocatedObject2D l) {
		if (snappingPanel==null) return;
		l.setSnappingBehaviour(snappingPanel.getSnappingBehaviour().copy());
		if (sameSnap)l.setSnappingBehaviour(snappingPanel.getSnappingBehaviour());
	}

	

	protected void setItemsToDiaog() {
		SnappingPosition snap = array.get(0).getSnapPosition();
		
		for(TextGraphic s: array) {
			setAtrributesToDialog(s);
			if (this.isUnifyPosition()) {
				s.setSnappingBehaviour(snap);
			}
			if (s instanceof ComplexTextGraphic) {
				setComplexProperteisToDialog((ComplexTextGraphic) s);
			}
		}
}

	public boolean isUnifyPosition() {
		return unifyPosition;
	}

	public void setUnifyPosition(boolean unifyPosition) {
		this.unifyPosition = unifyPosition;
	}
	

	
	
}

package objectDialogs;

import java.util.ArrayList;

import graphicalObjects.HasBackGroundShapeGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import standardDialog.AngleInputPanel;
import standardDialog.BooleanInputPanel;
import standardDialog.ColorComboboxPanel;
import undo.Edit;
import utilityClasses1.ArraySorter;

public class ShapeGraphicOptionsSwingDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ShapeGraphic s;
	
	ArrayList<ShapeGraphic> array=new 	ArrayList<ShapeGraphic>();

	public ShapeGraphicOptionsSwingDialog(ShapeGraphic s) {
	this.s=s;	
	 addOptionsToDialog();
	 super.undoableEdit=Edit.createGenericEditForItem(s);
	}
	
	public boolean hasItems() {
		if (s!=null)
		return true;
		
		if(array.size()>0) return true;
		
		return false;
	}
	
	public ShapeGraphicOptionsSwingDialog(ArrayList<?> items, boolean backgroundShapes) {
		setArray(items);
		if (backgroundShapes) setArrayToTextBackGround(items);
		s=new ArraySorter<ShapeGraphic>().getFirstNonNull(array);
		if (s!=null)
		addOptionsToDialog();
		}
	
	
	
	public void setArray(ArrayList<?> items) {
		array=new 	ArrayList<ShapeGraphic>();
		for(Object i: items) {
			if (i instanceof ShapeGraphic) {
				array.add((ShapeGraphic) i);
			}
		}
		super.undoableEdit=Edit.createGenericEdit(items);
	}
	
	public void setArrayToTextBackGround(ArrayList<?> items) {
		array=new 	ArrayList<ShapeGraphic>();
		for(Object i: items) {
			addShapeToDialog(i);
		}
		
	}
	
	public void addShapeToDialog(Object i) {
		if (i instanceof HasBackGroundShapeGraphic) {
			if (!array.contains(i)) {
				HasBackGroundShapeGraphic i2 = (HasBackGroundShapeGraphic) i;
				i2.setFillBackGround(true);
				array.add(i2.getBackGroundShape());
			}
		}
	}
	
	protected void addOptionsToDialog() {
		// TODO Auto-generated method stub
		 addOptionsToDialogPart1();
		addOptionsToDialogPart2();
	}
	
	
	protected void addOptionsToDialogPart1() {
		this.addNameField(s);
		this.addFixedEdgeToDialog(s);
	}
	
	
	protected void addOptionsToDialogPart2() {
		// TODO Auto-generated method stub
		this.add("AntiA", new BooleanInputPanel("Antialize Appeareance", s.isAntialize()));
		this.addStrokePanelToDialog(s);
		ColorComboboxPanel filpanel = new ColorComboboxPanel("Fill Color", null, s.getFillColor());
		this.add("FillColor", filpanel);
		BooleanInputPanel fillpanel2 = new BooleanInputPanel("fill?", s.isFilled());
		moveGrid(2, -1);
		this.add("fill", fillpanel2 );
		moveGrid(-2, 0);
		AngleInputPanel aip = new AngleInputPanel("Angle", s.getAngle(), true);
		this.add("Angle", aip);
		if (s.isHasCloseOption()) {
			BooleanInputPanel colsepanel2 = new BooleanInputPanel("Closed?", s.isClosedShape());
			add("Closed", colsepanel2);
		}
		if (s.getSnappingBehaviour()!=null) this.addSnappingBehviourToDialog(s);
	}
	
	protected void setItemsToDiaog() {
		setItemsToDiaog(s);
		for(ShapeGraphic s: array) {
			setItemsToDiaog(s);
		}
}
	
	protected void setItemsToDiaog(ShapeGraphic s) {
		if (s==null) return;
		this.setFixedEdgeToDialog(s);
		this.setNameFieldToDialog(s);
		this.setStrokedItemtoPanel(s);
		s.setFillColor(this.getColor("FillColor"));
		
		s.setFilled(this.getBoolean("fill"));
		s.setAntialize(this.getBoolean("AntiA"));
		s.setAngle(this.getNumber("Angle"));
		if (s.isHasCloseOption()) {
			s.setClosedShape(this.getBoolean("Closed"));
		}
		setObjectSnappingBehaviourToDialog(s);
}
}

package dialogs;

import java.util.ArrayList;

import objectDialogs.GraphicItemOptionsDialog;
import plotParts.DataShowingParts.ErrorBarShowingShape;
import standardDialog.BooleanInputPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;

public class ErrorBarDialog  extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ErrorBarShowingShape rect;
	ArrayList<ErrorBarShowingShape> additionalBars=new ArrayList<ErrorBarShowingShape>();

	private boolean bareBones;
	
	
	public ErrorBarDialog(ErrorBarShowingShape b, boolean bareBones) {
		this.bareBones=bareBones;
		rect=b;
		addOptionsToDialog();
	}
	
	public  ErrorBarDialog(ArrayList<?> objects) {
		this.bareBones=true;
		
		for(Object o: objects) {
			if (o instanceof ErrorBarShowingShape) {
				if (rect==null) {
					rect=(ErrorBarShowingShape) o;
					addOptionsToDialog();
				}
				else additionalBars.add((ErrorBarShowingShape) o);
			}
		}
	}
	
	public void addAdditionalBars(ArrayList<ErrorBarShowingShape> bars) {
		additionalBars=bars;
	}


	@Override
	public void addOptionsToDialog() {
		addBarAttributesToDialog(rect);
		
		
	}
	
	public void addBarAttributesToDialog(ErrorBarShowingShape  rect) {
		
		if (!bareBones)
			{
			super.addNameField(rect);
			super.addStrokePanelToDialog(rect);
		}
		
		addErrorBarSpecificParts(rect);
		
	}


	protected void addErrorBarSpecificParts(ErrorBarShowingShape rect) {
		NumberInputPanel nip = new NumberInputPanel("Bar Width", rect.getBarWidth(), 0, 80);
		nip.setDecimalPlaces(2);
		
		this.add("upper", new BooleanInputPanel("Shows upper", rect.isUpperBarShown()));
		this.add("lower", new BooleanInputPanel("Shows lower", rect.isLowerBarShown()));
		
		this.add("width", nip);
		
		this.add("typ",
				new ComboBoxPanel("Show as", new String[] {"Lines only", "Capped Lines"}, rect.getBarType()));
		
		this.add("errorT",
				new ComboBoxPanel("Show Error Bar as", new String[] {"Standard Dev", "SEM", "95% Interval (2*SEM)", "99% (3*SEM)"}, rect.getErrorDepiction()));
	}
	
	@Override
	public void setItemsToDiaog() {
		setItemsToDialog(rect);
		for(ErrorBarShowingShape r: additionalBars) {
			setItemsToDialog(r);
		}
		return ;
	}
	
	public void setItemsToDialog(ErrorBarShowingShape  rect) {
		
		if (!bareBones)
		{super.setNameFieldToDialog(rect);

		super.setStrokedItemtoPanel(rect);}
		
		setErrorBarSpecificOptions(rect);
}


	protected void setErrorBarSpecificOptions(ErrorBarShowingShape rect) {
		rect.setBarWidth((int)getNumber("width"));
		rect.setBarType(this.getChoiceIndex("typ"));
		rect.setLowerBarShown(this.getBoolean("lower"));
		rect.setUpperBarShown(this.getBoolean("upper"));
		rect.setErrorDepiction(this.getChoiceIndex("errorT"));
		rect.requestShapeUpdate();
		rect.updatePlotArea();
	}
	
	
}
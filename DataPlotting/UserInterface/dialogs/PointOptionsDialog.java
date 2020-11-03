package dialogs;

import java.util.ArrayList;

import objectDialogs.GraphicItemOptionsDialog;
import plotParts.DataShowingParts.PointModel;
import standardDialog.AngleInputPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.DialogItemChangeEvent;
import standardDialog.NumberInputPanel;
import standardDialog.SwingDialogListener;

public class PointOptionsDialog extends GraphicItemOptionsDialog{

	/**
	 * 
	 */
	protected boolean bareBones;
	private PointModel pmodel;
	ArrayList<PointModel> additionalPoints=new ArrayList<PointModel> ();
	private static final long serialVersionUID = 1L;
	PointOptionsDialog(){}
	PointOptionsDialog(PointModel p, boolean bareBones, SwingDialogListener lis) {
		this.bareBones=bareBones;
		pmodel=p;
		this.addPointModelOptions(pmodel);
		this.addDialogListener(new SwingDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				 setPointModelToDialog(pmodel);
				for(PointModel p: additionalPoints) {setPointModelToDialog(p);}
			}});
		
		this.addDialogListener(lis);
	}
	
	public void addAdditionalPoint(PointModel p) {
		additionalPoints.add(p);
	}

	protected void addPointModelOptions(PointModel p) {
	
		NumberInputPanel nip = new NumberInputPanel("Point Width", p.getPointSize());
		nip.setDecimalPlaces(2);
		this.add("width", nip);
			if (bareBones) return;
		NumberInputPanel nip2 = new NumberInputPanel("N sides", p.getNSides());
		nip2.setDecimalPlaces(2);
		this.add("sides", nip2);
		
		this.add("typ",
				new ComboBoxPanel("Show as", new String[] {"Simple Points", "Complex Points"}, p.getPointType()));
		AngleInputPanel aip = new AngleInputPanel("Angle", p.getModelShape().getAngle(), true);
		this.add("Angle", aip);
	}
	
	public void setPointModelToDialog(PointModel p) {
		p.setPointSize(getNumber("width"));
		if (bareBones) return;
		p.setPointType(this.getChoiceIndex("typ"));
		p.setNVertex((int) getNumber("sides"));
		p.getModelShape().setAngle(getNumber("Angle"));
	}

}

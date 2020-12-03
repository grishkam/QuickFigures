/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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

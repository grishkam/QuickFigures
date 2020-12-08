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
package sUnsortedDialogs;

import applicationAdapters.HasScaleInfo;
import standardDialog.GriddedPanel;
import standardDialog.StandardDialog;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.StringInputPanel;
import standardDialog.StandardDialogListener;
import utilityClassesForObjects.ScaleInfo;

/**Alternative to the set Scale dialog of imageJ. does exactly that but after this dialog,
   QuickFigures will update the targeted image panels*/
public class ScaleSettingDialog  extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HasScaleInfo scaled;
	ScaleInfo info;
	private ScaleResetListener scaleResetListen;
	
	boolean alternateDialog=false;
	
	public ScaleSettingDialog(HasScaleInfo scaled, StandardDialogListener listener) {
		this(scaled, listener, true);
	}
	
	public ScaleSettingDialog(HasScaleInfo scaled, StandardDialogListener listener, boolean alternate) {
		 alternateDialog=alternate;
		this.getOptionDisplayTabs().remove(this.getMainPanel());
		this.scaled=scaled;
		 info = scaled.getScaleInfo();
		
		this.addScaleInfoToDialog(scaled.getScaleInfo());
		
		this.addDialogListener(listener);
	}
	
	@Override
	public void onOK() {
		setScaleInfoToDialog(info);
		scaled.setScaleInfo(info);
		if (scaleResetListen!=null) scaleResetListen.scaleReset(scaled, info);
	}



	public void setScaleResetListen(ScaleResetListener scaleResetListen) {
		this.scaleResetListen = scaleResetListen;
	}
	
	public void addScaleInfoToDialog(ScaleInfo si) {
		if (!alternateDialog) { super.addScaleInfoToDialog(si); return;}
		GriddedPanel omp = this.getMainPanel();
		this.setMainPanel(new GriddedPanel());
		
		this.add("units",new StringInputPanel("Units ", si.getUnits(),  5));
		this.add("dn",new NumberInputPanel("Distance In Pixels", 1/si.getPixelWidth(), 4));
		this.add("kd",new NumberInputPanel("Known Distance", 1, 4));
		
		
		this.getOptionDisplayTabs().addTab("Calibration", this.getMainPanel());
		this.setMainPanel(omp);
	}

	public void setScaleInfoToDialog(ScaleInfo si) {
		if (!alternateDialog) {super.setScaleInfoToDialog(si);return;}
		si.setUnits(this.getString("units"));
		double number = this.getNumber("dn");
		double knownDist=this.getNumber("kd");
		si.setPixelHeight(knownDist/number);
		si.setPixelWidth(knownDist/number);
	}
	

	
}

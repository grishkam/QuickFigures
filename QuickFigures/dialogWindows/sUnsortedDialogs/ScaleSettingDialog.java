/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Oct 13, 2022
 * Version: 2022.2
 */
package sUnsortedDialogs;

import java.util.HashMap;

import applicationAdapters.HasScaleInfo;
import locatedObject.ScaleInfo;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.GriddedPanel;
import standardDialog.StandardDialog;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.StringInputPanel;
import undo.AbstractUndoableEdit2;
import standardDialog.StandardDialogListener;

/**Alternative to the "Set Scale" dialog of imageJ. does exactly that but after this dialog,
   QuickFigures has other methods that will also update the targeted image panels. 
   @see ScaleInfo
   @see HasScaleInfo 
   @see ScaleResetListener
   */
public class ScaleSettingDialog  extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HasScaleInfo scaled;
	private ScaleInfo originalScale;
	ScaleInfo info;
	private ScaleResetListener scaleResetListen;
	
	boolean alternateDialog=false;//two versions of this dialog are possible. indicates which one
	
	
	public ScaleSettingDialog(HasScaleInfo scaled, StandardDialogListener listener) {
		this(scaled, listener, true);
	}
	
	public ScaleSettingDialog(HasScaleInfo scaled, StandardDialogListener listener, boolean alternate) {
		 alternateDialog=alternate;
		this.getOptionDisplayTabs().remove(this.getMainPanel());
		this.scaled=scaled;
		 info = scaled.getScaleInfo();
		 originalScale=info.copy();
		
		this.addScaleInfoToDialog(scaled.getScaleInfo());
		
		this.addDialogListener(listener);
	}
	
	public AbstractUndoableEdit2 getUndo() {return undo;}
	
	@Override
	public void onOK() {
		setScaleInfoToDialog(info);
		afterDialogInput();
	}

	/**
	 * 
	 */
	public void afterDialogInput() {
		scaled.setScaleInfo(info);
		if (scaleResetListen!=null) scaleResetListen.scaleReset(scaled, info);
	}



	public void setScaleResetListen(ScaleResetListener scaleResetListen) {
		this.scaleResetListen = scaleResetListen;
	}
	
	/**Adds components to the dialog*/
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

	/**Alters object to match the dialog*/
	public void setScaleInfoToDialog(ScaleInfo si) {
		if (!alternateDialog) {super.setScaleInfoToDialog(si);return;}
		si.setUnits(this.getString("units"));
		double number = this.getNumber("dn");
		double knownDist=this.getNumber("kd");
		si.setPixelHeight(knownDist/number);
		si.setPixelWidth(knownDist/number);
	}
	

	/**a list of units that con be converted into each other*/
	public static final String[] unitnames=new String[]{"µm", "nm"};
	double[] unitsizes=new double[]{  1.0 , 1000.0};
	
	/**Changes the unit*/
	public AbstractUndoableEdit2 switchto(String units) {
		
		HashMap<String, Double> unitsReference=new HashMap<String, Double> ();
		for(int i=0; i<unitnames.length; i++) {
			unitsReference.put(unitnames[i], unitsizes[i]);
		}
		
		String startingUnit = info.getUnits();
		Double ratio = unitsReference.get(startingUnit);
		if(ratio==null)
			{
			ShowMessage.showOptionalMessage("unit not convertible", false, "could not find conversion factor between units");
			return null;
			}
		ratio=ratio/unitsReference.get(units);
		
		info.setUnits(units);
		info.scaleXY(ratio);
		
		this. afterDialogInput();
		
	
	return getUndo();
	}
	
	/**returns the ratio*/
	public double getRatioChange() {return this.originalScale.getPixelWidth()/info.getPixelWidth();}
}

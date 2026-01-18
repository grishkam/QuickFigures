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
 * Date Created: April 18, 2021
 * Date Modified: April 20, 2021
 * Version: 2023.2
 */
 
package figureEditDialogs;

import java.util.HashMap;

import imageScaling.Interpolation;
import imageScaling.PixelDensityInstructions;
import imageScaling.ScaleInformation;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.InfoDisplayPanel;

/**
 A dialog that allows the user to input a scale level and
 an interpolation method
 */
public class PPIChangeDialog extends StandardDialog {
 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PPI_KEY="pre scale", INTERPOLATION_KEY="interpolarion";
	
	private PixelDensityInstructions ppiInformation=null;

	
	
	public PPIChangeDialog(PixelDensityInstructions scale) {
		this.ppiInformation=scale;
		if(scale==null)
			ppiInformation=new PixelDensityInstructions();
		this.setTitle("Input pixels per inch");
		this.setWindowCentered(true);
		PPIChangeDialog targetDialog = this;
		addScaleInformationToDialog(ppiInformation, targetDialog);
		
	}


	/**Creates a dialog with extra messages*/
	public PPIChangeDialog(PixelDensityInstructions scale, HashMap<String, String> hm, String title) {
		this(scale);
		for(String key: hm.keySet()) {
			this.add("Display",  new InfoDisplayPanel(key, hm.get(key)));
		}

		
		if(title!=null)
			this.setTitle(title);
	}
	
	/**
	 * @param scale
	 * @param targetDialog
	 */
	public static void addScaleInformationToDialog(PixelDensityInstructions scale, StandardDialog targetDialog) {
		targetDialog.add(PPI_KEY, new NumberInputPanel("PPI", scale.getPPI(),3));
		addInterpolationToDialog(scale, targetDialog);
	}





	/**adds an interpolation field to the dialog
	 * @param scale
	 * @param targetDialog
	 */
	protected static void addInterpolationToDialog(PixelDensityInstructions scale, StandardDialog targetDialog) {
		ChoiceInputPanel interpolationChoice = ChoiceInputPanel.buildForEnum("Interpolation", Interpolation.values(), scale.getInterpolationType());
		targetDialog.add(INTERPOLATION_KEY, interpolationChoice);
	}
	
	
	/**returns the scale information based on the values of the dialog*/
	public static PixelDensityInstructions getPixelDensityInformationFromDialog(StandardDialog targetDialog) {
		double outputScale=targetDialog.getNumber(PPI_KEY);
		
		Interpolation outptuInterPolation = Interpolation.values()[targetDialog.getChoiceIndex(INTERPOLATION_KEY)];
		
		
		PixelDensityInstructions densityInformation = new PixelDensityInstructions(outputScale, outptuInterPolation);
		
		return densityInformation;
		
	}
	
	
	/**shows the user a modal dialog and returns the users choice*/
	public static PixelDensityInstructions showUserTheDialog(PixelDensityInstructions input) {
		PPIChangeDialog dialog = new  PPIChangeDialog(input);
		return dialog.showUserOption();
	}


	/**shows the user a modal dialog and returns the users choice*/
	public static PixelDensityInstructions showUserTheDialog(double input) {
		PPIChangeDialog dialog = new  PPIChangeDialog(new PixelDensityInstructions(input));
		return dialog.showUserOption();
	}
	
	/**shows the user a modal dialog and returns the users choice*/
	public static PixelDensityInstructions showUserTheDialog(double input, Interpolation interpolationType) {
		PPIChangeDialog dialog = new  PPIChangeDialog(new PixelDensityInstructions(input, interpolationType));
		return dialog.showUserOption();
	}



	/**shows the dialog for the given scale information 
	 * @param input
	 * @param dialog
	 * @return
	 */
	public  PixelDensityInstructions showUserOption() {
		setModal(true);
		showDialog();
		
		if(wasOKed)
			return getPixelDensityInformationFromDialog(this);
		
		return this.ppiInformation;
	}

}

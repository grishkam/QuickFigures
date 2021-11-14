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
 * Version: 2021.2
 */
 
package figureEditDialogs;

import imageScaling.Interpolation;
import imageScaling.ScaleInformation;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**
 A dialog that allows the user to input a scale level and
 an interpolation method
 */
public class ScaleLevelInputDialog extends StandardDialog {
 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String SCALE_KEY="pre scale", INTERPOLATION_KEY="interpolarion";
	
	private ScaleInformation scalingInformation=null;
	
	
	public ScaleLevelInputDialog(ScaleInformation scale) {
		this.scalingInformation=scale;
		if(scale==null)
			scalingInformation=new ScaleInformation();
		this.setTitle("Input scale factor");
		this.setWindowCentered(true);
		ScaleLevelInputDialog targetDialog = this;
		addScaleInformationToDialog(scalingInformation, targetDialog);
		
	}


	
	
	
	/**
	 * @param scale
	 * @param targetDialog
	 */
	public static void addScaleInformationToDialog(ScaleInformation scale, StandardDialog targetDialog) {
		targetDialog.add(SCALE_KEY, new NumberInputPanel("Scale Factor", scale.getScale(),3));
		addInterpolationToDialog(scale, targetDialog);
	}





	/**adds an interpolation field to the dialog
	 * @param scale
	 * @param targetDialog
	 */
	protected static void addInterpolationToDialog(ScaleInformation scale, StandardDialog targetDialog) {
		ChoiceInputPanel interpolationChoice = ChoiceInputPanel.buildForEnum("Interpolation", Interpolation.values(), scale.getInterpolationType());
		targetDialog.add(INTERPOLATION_KEY, interpolationChoice);
	}
	
	
	/**returns the scale information based on the values of the dialog*/
	public static ScaleInformation getScaleLevelInformationFromDialog(StandardDialog targetDialog) {
		double outputScale=targetDialog.getNumber(SCALE_KEY);
		
		Interpolation outptuInterPolation = Interpolation.values()[targetDialog.getChoiceIndex(INTERPOLATION_KEY)];
		
		
		ScaleInformation scaleInformation = new ScaleInformation(outputScale, outptuInterPolation);
		
		return scaleInformation;
		
	}
	
	
	/**shows the user a modal dialog and returns the users choice*/
	public static ScaleInformation showUserTheDialog(ScaleInformation input) {
		ScaleLevelInputDialog dialog = new  ScaleLevelInputDialog(input);
		dialog.setModal(true);
		dialog.showDialog();
		
		if(dialog.wasOKed)
			return getScaleLevelInformationFromDialog(dialog);
		
		return input;
	}

}

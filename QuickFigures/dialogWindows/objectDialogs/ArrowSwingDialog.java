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
 * Date Modified: Jan 6, 2021
 * Version: 2022.0
 */
package objectDialogs;

import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.ArrowGraphic.ArrowHead;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**a dialog for altering the appearance of the arrow heads*/
public class ArrowSwingDialog extends ShapeGraphicOptionsSwingDialog{

	
	/**
	 * 
	 */
	private static final String OUTLINE = "Outline", NUMBER_OF_HEADS = "Number of heads";
	
	/**conversion factor to use for angles*/
	private static final double DEGREE_RADIANS = 180/Math.PI;
	private static final int FULL_DIALOG = 0, LIMITED_DIALOG=1;;
	ArrowGraphic arrow;
	private int dialogType=FULL_DIALOG;
	private ArrowHeadDialog a2;
	private ArrowHeadDialog a1;
	public ArrowSwingDialog(ArrowGraphic s, int limited) {
		super(s, limited==LIMITED_DIALOG);
		this.dialogType=limited;
		this.a1=new ArrowHeadDialog(s.getHead(ArrowGraphic.FIRST_HEAD),ArrowGraphic.FIRST_HEAD);
		this.a2=new ArrowHeadDialog(s.getHead(ArrowGraphic.SECOND_HEAD),ArrowGraphic.SECOND_HEAD);
		addOptionsToDialog2();
		
	}
	
	
	protected void addOptionsToDialogPart1() {
		this.addNameField(s);
	}
	
	
	protected void addOptionsToDialog2() {
		
		if((dialogType!=LIMITED_DIALOG)) addOptionsToDialogPart1();
		addOptionsToDialogPart2();
	}
	
	protected void addOptionsToDialog() {
		
		
	}
	
	/**Adds the arrow specific items to the dialog*/
	protected void addOptionsToDialogPart2() {
		
		if ((dialogType!=LIMITED_DIALOG))this.addStrokePanelToDialog(s);
		if (s instanceof ArrowGraphic) {
			arrow=(ArrowGraphic) s;
		}
		
		ChoiceInputPanel cip = new ChoiceInputPanel(NUMBER_OF_HEADS, new String[] {"0", "1", "2"},arrow.getNHeads());
		this.add("HeadNum", cip);
		
		cip = new ChoiceInputPanel(OUTLINE, new String[] {"Do Not Outline", "Draw Outline Arrow"/**, "Outline for heads"*/},arrow.drawnAsOutline());
		this.add("outline", cip);
		
		a1.addArrowHeadOptions();
			a2.addArrowHeadOptions();
		
			if (arrow.headsAreSame())
				super.addSubordinateDialogsAsTabs("heads", a1);
			else
			super.addSubordinateDialogsAsTabs("heads", a1, a2);
		
		
		
		if (arrow.getBackGroundShape()!=null) {
			this.getOptionDisplayTabs().setTitleAt(0, "Arrow");
			this.addSubordinateDialog(OUTLINE, arrow.getBackGroundShape().getOptionsDialog());
		}
		
		
	
	}



	
	protected void setItemsToDiaog() {
		this.setNameFieldToDialog(s);
		if ((dialogType!=LIMITED_DIALOG))this.setStrokedItemtoPanel(s);
		arrow.setNumerOfHeads(this.getChoiceIndex("HeadNum"));
		
		
		
		a1.setArrowHeadToDialog();
		a2.setArrowHeadToDialog();
		
		arrow.setDrawAsOutline(this.getChoiceIndex("outline"));
		
}


	public ArrowHeadDialog getHeadDialog(int head) {
		if (head==ArrowGraphic.SECOND_HEAD) return a2;
		return a1;
	}
	
	class ArrowHeadDialog extends GraphicItemOptionsDialog {
		
		
		/**
		 * 
		 */
		private String ARROW_STYLE = "style", NOTCH_ANGLE = "NotchAngle", HEAD_ANGLE = "HeadAngle", HEADSIZE = "headsize";
		private ArrowHead head;
		
		public ArrowHeadDialog(ArrowHead a, int headID) {
			this.setName("Arrow head "+headID);
			this.head=a;
			
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		protected void setItemsToDiaog() {setArrowHeadToDialog();}
		
		/**
		sets the arrow heads options based on dialog
		 */
		public void setArrowHeadToDialog() {
			head.setArrowHeadSize((int)this.getNumber(HEADSIZE));
			
			head.setArrowTipAngle(this.getNumber(HEAD_ANGLE)/DEGREE_RADIANS);
			head.setNotchAngle(this.getNumber(NOTCH_ANGLE)/DEGREE_RADIANS);
			
			head.setArrowStyle(this.getChoiceIndex(ARROW_STYLE));
		}
		
		/**
		 Adds options for the given arrow head to the dialog
		 */
		public void addArrowHeadOptions() {
			NumberInputPanel nip = new NumberInputPanel("Head Size", head.getArrowHeadSize(),true, true, 0, 100);
			this.add(HEADSIZE, nip) ;
			
			NumberInputPanel aip = new NumberInputPanel("Tip Angle", head.getArrowTipAngle()*DEGREE_RADIANS, true, true, 0, 180);
			this.add(HEAD_ANGLE, aip);
			aip = new NumberInputPanel("Notch Angle", head.getNotchAngle()*DEGREE_RADIANS, true, true, 0, 180);
			this.add(NOTCH_ANGLE, aip);
			
			ChoiceInputPanel cp = new ChoiceInputPanel("Head Style", ArrowGraphic.arrowStyleChoices, head.getArrowStyle());
			this.add(ARROW_STYLE, cp);
		}
	}
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}

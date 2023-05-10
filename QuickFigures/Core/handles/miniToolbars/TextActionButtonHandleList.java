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
 * Date Modified: May 10, 2023
 * Version: 2023.2
 */
package handles.miniToolbars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JMenuItem;

import actionToolbarItems.EditManyObjects;
import actionToolbarItems.SetAngle;
import actionToolbarItems.SuperTextButton;
import applicationAdapters.CanvasMouseEvent;
import channelLabels.ChannelLabelTextGraphic;
import graphicalObjects.CordinateConverter;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.IconHandle;
import iconGraphicalObjects.DialogIcon;
import locatedObject.LocatedObject2D;
import menuUtil.SmartPopupJMenu;
import selectedItemMenus.LayerSelectionSystem;
import selectedItemMenus.MultiSelectionOperator;
import selectedItemMenus.SelectAllButton;
import selectedItemMenus.AttachmentPositionAdjuster;
import selectedItemMenus.TextOptionsSyncer;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialog;
import standardDialog.StandardDialogListener;
import standardDialog.fonts.FontChooser;
import standardDialog.numbers.NumberInputPanel;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;
import undo.CombinedEdit;

/**A list of handles that works like a mini toolbar for Text Graphics
 * @see TextGraphic
 * @see ComplexTextGraphic
 * */
public class TextActionButtonHandleList extends ActionButtonHandleList {

	

	{maxGrid=12;}

	protected TextGraphic text;
	private boolean hideAngleAndDialogHandles;
	
	public TextActionButtonHandleList(TextGraphic t) {
		this(t, false);
	}
	
	public TextActionButtonHandleList(TextGraphic t, boolean hideangle) {
		this.hideAngleAndDialogHandles=hideangle;
		this.text=t;
		addItems();
		updateLocationBasedOnParentItem();
	}

	public void updateHandleLocations(double magnify) {
		super.updateHandleLocations(magnify);
	}

	public void updateLocationBasedOnParentItem() {
		double x = text.getBounds().getX();
		if (x>30)x-=30;
		setLocation(new Point2D.Double(x, text.getBounds().getMaxY()+15));
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**creates the handles and adds them to the list*/
	public void addItems() {
		
		if (text instanceof ComplexTextGraphic )
			{
			for(int j=0; j<=SuperTextButton.STRIKES; j++) {	createSuperscriptButton( j);}
			}
		else {
			createSuperscriptButton(SuperTextButton.BOLDENS);
			createSuperscriptButton(SuperTextButton.ITALICIZES);
			
		}
		
		addFontSizeHandle();
		addFontFamilyHandle();
		
		addTextColor();
		createColorDimmer();
		
		
		
		
		if (!this.hideAngleAndDialogHandles)this.add(new TextDialogHandle(800210));
		if (!this.hideAngleAndDialogHandles)this.add(new TextHandleNonEditmode(new SelectAllButton(text), 819100));
		add(new JustifyButtonForText(1488925));
		setLocation(getLocation());
		
		
		addAttachmentPositionButton(text);
		
		if (!this.hideAngleAndDialogHandles)addAngleHandleToList();
		
	}

	/**
	 * 
	 */
	private void addAngleHandleToList() {
		SetAngle itemForIcon2 = new SetAngle(45);
		GeneralActionListHandle h2 = addOperationList(itemForIcon2,SetAngle.createManyAngles() );
		h2.itemForInputPanel=new SetAngle(text);
	}

	/**
	 * 
	 */
	public void addFontFamilyHandle() {
		MultiSelectionOperator[] ff = SuperTextButton.getForFonts(text);
		GeneralActionListHandle h = new FontFamilyActionHandle(ff[ff.length-1], 18,  ff);
		this.add(h);
	}

	/**
	 * 
	 */
	public void addFontSizeHandle() {
		SuperTextButton fontSizer = new SuperTextButton(text, true);
		TextActionHandle sizeHandle = addButton(fontSizer);
		sizeHandle.maxWidth=30;
	}

	protected void addAttachmentPositionButton(LocatedObject2D t2) {
		if (t2.getAttachmentPosition()!=null)this.add(new TextHandleNonEditmode(new AttachmentPositionAdjuster(true, t2), 741905));
	}
	
	/**An action handle that is hidden if the text is in edit mode */
	class TextHandleNonEditmode extends GeneralActionHandle {


		public TextHandleNonEditmode(MultiSelectionOperator i, int num) {
			super(i, num);
		}
		@Override
		public boolean isHidden() {
			if(text.isEditMode()) {return true;}
			
			return super.isHidden();
			
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	class JustifyButtonForText extends GeneralActionListHandle {

		public JustifyButtonForText( int num) {
			super(new SuperTextButton(text, SuperTextButton.JUSTIFIES_TEXT, 0), num, SuperTextButton.getJustifications());
			
		}


		
		@Override
		public boolean isHidden() {
			if(text instanceof ComplexTextGraphic ) {
				
				TextParagraph para = ((ComplexTextGraphic) text).getParagraph();
				return !(para.size()>1) ;
				
				
			}
			
			return true;
			
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}


	
	public class TextDialogHandle extends GeneralActionHandle {

		public TextDialogHandle( int num) {
			super(new TextOptionsSyncer(), num);
		}
		
		public void updateIcon() {
			super.setIcon(DialogIcon.getIcon());
		}
		
		@Override
		public boolean isHidden() {
			if (text.isEditMode()) return true;
			return false;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}


	private void createColorDimmer() {
		
		SuperTextButton[] forDims = SuperTextButton.getForDims(text);
		GeneralActionListHandle h2 = new ColorDimmingHandle(forDims[0], numHandleID,forDims);;
		h2.setxShift(h2.getxShift() + 2);
		h2.setyShift(h2.getyShift() + 2);
		addHandle(h2);
	}



	public void addTextColor() {
		
		if (text instanceof ChannelLabelTextGraphic) return;
		//SuperscriptButton itemForIcon = new SuperscriptButton(text, text.getTextColor());
		EditManyObjects i = new EditManyObjects();
		i.setModelTextItem(text);
		i.setBigIcon(true);
		GeneralActionListHandle h = new TextColorHandle2(i, numHandleID,getTextColors());;
		h.setxShift(0);
		h.setyShift(0);
		addHandle(h);
		
	}
	
	
	private static Color[] standardColor=new Color[] {Color.black, Color.white, Color.blue, Color.green, Color.red,  Color.cyan, Color.magenta, Color.yellow };
	
	public static SuperTextButton[] getTextColors() {
		return SuperTextButton.getForColors(true, standardColor);
	}



	private void addHandle(GeneralActionListHandle h) {
		add(h);
		numHandleID++;
	}



	private void createSuperscriptButton( int j) {
		SuperTextButton i=new SuperTextButton(j);
		i.setModelText(text);
		addButton(i);
	}



	public TextActionHandle addButton(SuperTextButton i) {
		TextActionHandle h = new TextActionHandle(i, numHandleID);
		add(h);
		numHandleID++;
		return h;
	}
	
	
	/**A handle for altering text in a few ways, including size, style.
	  handle appearance may be different depending on whether the selected text is 
	  bold, italic, superscript or something else.*/
	public class TextActionHandle extends IconHandle {
		
		
		private SuperTextButton button;
		private boolean operateOnAll=true;

		public TextActionHandle(SuperTextButton i, int num) {
			super(i.getIcon(), new Point(0,0));
			this.setHandleNumber(num);
			this.button=i;
		}
		
		public void draw(Graphics2D graphics, CordinateConverter cords) {
			updateHandleLocations(cords.getMagnification());
			if (button.objectIsAlready(text))
				this.setHandleColor(Color.DARK_GRAY); 
			else this.setHandleColor(Color.white);
			super.draw(graphics, cords);
		}
		
		public boolean isHidden() {
			if(button.boldens()) return false;
			if(button.italicizes()) return false;
			if (!text.isEditMode() &&button.resizesFont()) return false;
			if (text.isEditMode() &&button.resizesFont()) return true;
			return !text.isEditMode();
		}
		
		private static final long serialVersionUID = 1L;
		
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			CombinedEdit edit = new CombinedEdit();
			boolean rightSide=canvasMouseEventWrapper. getClickedXScreen()>lastDrawShape.getBounds().getCenterX();
			boolean down=canvasMouseEventWrapper. getClickedYScreen()>lastDrawShape.getBounds().getCenterY();
			
			/**handles the activity for a font resizing version of this button*/
			if (button.resizesFont()) {
				if(!rightSide||canvasMouseEventWrapper.clickCount()>5||canvasMouseEventWrapper.isPopupTrigger()) {
					showFontSizeInputPopup(canvasMouseEventWrapper);
					return;
				}
				if(rightSide)
					button.makeResizeDown(down);
			}

			/**performs the action. if the text item is in edit mode, this will only affect the user selected text 
			  with the object*/
			if (operateOnAll&&!text.isEditMode()) {
				LayerSelectionSystem selector = canvasMouseEventWrapper.getSelectionSystem();
				button.setSelector(selector);
				button.run();
			} else {
				button.actOnObject(edit, text);
				canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(edit);
			}
			
			canvasMouseEventWrapper.getAsDisplay().updateDisplay();
			
		}

		
	/**displays a popup that allows the user to input a font size*/
		protected void showFontSizeInputPopup(CanvasMouseEvent canvasMouseEventWrapper) {
			NumberInputPanel panel = button.getFontInputPanel(canvasMouseEventWrapper.getSelectionSystem());
			SmartPopupJMenu menu = new SmartPopupJMenu();
			menu.add(panel);
			menu.showForMouseEvent(canvasMouseEventWrapper, 0, 15);
		}

	}
	
	/**A handle for choosing text colors*/
	public class TextColorHandle2 extends GeneralActionListHandle {
		{setxShift(4); setyShift(4);}
		public TextColorHandle2(MultiSelectionOperator i, int num, MultiSelectionOperator[] items) {
			super(i, num, items);
			
		}
		private static final long serialVersionUID = 1L;
		
		public boolean isHidden() {
			//if(!text.isEditMode() && text instanceof ComplexTextGraphic) return true;//since a user must select text for this handle to affect complex text, it is hidden when text cannot be selected
			return false;
		}

	}
	
	/**a special handle for the color dimming feature. allows the user
	 * to change the dimming applied to text colors, 
	 * for certain colors it is not applicable and hidden*/
	public class ColorDimmingHandle extends GeneralActionListHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ColorDimmingHandle(SuperTextButton superscriptButton, int numHandleID,
				SuperTextButton[] forDims) {
			super(superscriptButton, numHandleID, forDims);
		}
		
		public boolean isHidden() {
			if(text.isEditMode()) return true;
			if (isTextPitchBlack()) return true;
			return false;
		}

		protected boolean isTextPitchBlack() {
			if (text instanceof ComplexTextGraphic) {
				TextParagraph par = ((ComplexTextGraphic) text).getParagraph();
				for(TextLineSegment seq:par.getAllSegments()) {
					if(seq.getTextColor().equals(Color.black)) continue;
					return false;
				}
				return true;
			}
			return text.getTextColor().equals(Color.black)&& !(text instanceof ComplexTextGraphic);
		}

	}
	
	/**A handle with an additional menu option called more fonts, that allows the user to 
	 * select a font. Handle for choosing a font family*/
	public class FontFamilyActionHandle extends GeneralActionListHandle implements ActionListener, StandardDialogListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private FontChooser fontChoser;

		public FontFamilyActionHandle(MultiSelectionOperator multiSelectionOperator, int i,
				MultiSelectionOperator[] ff) {
			super( multiSelectionOperator, i, ff);
	
			JMenuItem jM = new JMenuItem("More Fonts");
			jM.addActionListener(this);
			popupMenuForListHandle.add(jM);
		}
		
		public boolean isHidden() {
			if(text.isEditMode()) return true;
			return false;
		}

	
		/**displays a font chooser dialog*/
		@Override
		public void actionPerformed(ActionEvent e) {
			StandardDialog s = new StandardDialog("Select font", true);
			s.setWindowCentered(true);
			fontChoser = new FontChooser(text.getFont(), FontChooser.LIMITED_FONT_LIST);
			
			s.add("font", fontChoser);
			
			s.addDialogListener(this);
			
			StandardDialog s2 = new StandardDialog("Select font", true);
			s2.setWindowCentered(true);
			s2.remove(s2.getOptionDisplayTabs());
			s2.add(fontChoser.getFontFamilyComboBox());
			s2.showDialog();
		}

		/**After user has chosen a font in the dialog, this method applies it*/
		@Override
		public void itemChange(DialogItemChangeEvent event) {
			
			String fam = fontChoser.getSelectedFont().getFamily();
			SuperTextButton newb = new SuperTextButton(fam);
			JMenuItem item = setItemsforMenu("other fonts", newb);
			for(ActionListener a: item.getActionListeners()) {
				a.actionPerformed(new ActionEvent(item, 0, ""));
			}
			
		}

	}
	
}

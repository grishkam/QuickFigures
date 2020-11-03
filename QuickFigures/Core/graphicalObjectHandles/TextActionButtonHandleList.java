package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import actionToolbarItems.EditAndColorizeMultipleItems;
import actionToolbarItems.SuperTextButton;
import applicationAdapters.CanvasMouseEventWrapper;
import channelLabels.ChannelLabelTextGraphic;
import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import iconGraphicalObjects.IconUtil;
import menuUtil.SmartPopupJMenu;
import objectDialogs.DialogIcon;
import objectDialogs.MultiSnappingDialog;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.MultiSelectionOperator;
import selectedItemMenus.SelectAllButton;
import selectedItemMenus.SnappingSyncer;
import selectedItemMenus.TextOptionsSyncer;
import standardDialog.DialogItemChangeEvent;
import standardDialog.FontChooser;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import standardDialog.SwingDialogListener;
import undo.CompoundEdit2;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.TextLineSegment;
import utilityClassesForObjects.TextParagraph;

public class TextActionButtonHandleList extends ActionButtonHandleList {

	

	{maxGrid=12;}

	private TextGraphic text;
	private boolean dimmerExists;
	private SuperTextButton sizeField;


	public TextActionButtonHandleList(TextGraphic t) {
		this.text=t;
		addItems();
		updateLocationBasedOnParentItem();
	}

	public void updateHandleLocations(double magnify) {
	
		super.updateHandleLocations(magnify);
	}

	public void updateLocationBasedOnParentItem() {
		setLocation(new Point2D.Double(text.getBounds().getX(), text.getBounds().getMaxY()+15));
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void addItems() {
		
		if (text instanceof ComplexTextGraphic )
			{
			for(int j=0; j<6; j++) {	createSuperscriptButton( j);}
			}
		else {
			createSuperscriptButton(SuperTextButton.BOLDENS);
			createSuperscriptButton(SuperTextButton.ITALICIZES);
			
		}
		
		SuperTextButton fontSizer = new SuperTextButton(text, true);
		
		TextActionHandle sizeHandle = addButton(fontSizer);
		sizeHandle.maxWidth=30;
		addTextColor();
		createColorDimmer();
		
		MultiSelectionOperator[] ff = SuperTextButton.getForFonts(text);
		GeneralActionListHandle h = new FontFamilyActionHandle(ff[ff.length-1], 18,  ff);
		this.add(h);
		
		
		this.add(new textSyncHandle(800210));
		
		this.add(new GeneralActionHandleForText(new SelectAllButton(text), 819100, 0));
	//	this.createGeneralButton(new EditAndColorizeMultipleItems("up"));
	//	this.createGeneralButton( new EditAndColorizeMultipleItems("down"));
	//	this.createGeneralButton( new EditAndColorizeMultipleItems(false, text.getTextColor()));
		
		sizeField = new SuperTextButton(text, SuperTextButton. RESIZES_FONT_TO, text.getFont().getSize());
		add(new JustifyButtonForText(1488925));
		setLocation(location);
		adddSnapPositionButton(text);
		
		
	}

	protected void adddSnapPositionButton(LocatedObject2D t2) {
		if (t2.getSnappingBehaviour()!=null)this.add(new GeneralActionHandleForText(new SnappingSyncer(true, t2), 741905, 0));
	}
	
	class GeneralActionHandleForText extends GeneralActionHandle {

		private int form;

		public GeneralActionHandleForText(MultiSelectionOperator i, int num, int form) {
			super(i, num);
			this.form=form;
		}
		@Override
		public boolean isHidden() {
			if(form==0&&text.isEditMode()) {return true;}
			
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


	
	public class textSyncHandle extends GeneralActionHandle {

		public textSyncHandle( int num) {
			super(new TextOptionsSyncer(), num);
			//super.setIcon(DialogIcon.getIcon());
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


	protected void createColorDimmer() {
		if (dimmerExists) return; else dimmerExists=true;
		SuperTextButton[] forDims = SuperTextButton.getForDims(text);
		GeneralActionListHandle h2 = new TextDimmingColorHandle2(forDims[0], numHandleID,forDims);;
		h2.setxShift(h2.getxShift() + 2);
		h2.setyShift(h2.getyShift() + 2);
		addHandle(h2);
	}



	public void addTextColor() {
		
		if (text instanceof ChannelLabelTextGraphic) return;
		//SuperscriptButton itemForIcon = new SuperscriptButton(text, text.getTextColor());
		EditAndColorizeMultipleItems i = new EditAndColorizeMultipleItems();
		i.setModelItem(text);
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



	public void createSuperscriptButton( int j) {
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
	
	
	
	public class TextActionHandle extends IconHandle {
		public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
			updateHandleLocations(cords.getMagnification());
			if (button.objectIsAlready(text)) this.setHandleColor(Color.DARK_GRAY); else this.setHandleColor(Color.white);
			super.draw(graphics, cords);
		}
		
		private SuperTextButton button;
		private boolean operateOnAll=true;

		public TextActionHandle(SuperTextButton i, int num) {
			super(i.getIcon(), new Point(0,0));
			this.setHandleNumber(num);
			this.button=i;
		}
		
		public boolean isHidden() {
			if(button.boldens()) return false;
			if(button.italicizes()) return false;
			if (!text.isEditMode() &&button.resizesFont()) return false;
			if (text.isEditMode() &&button.resizesFont()) return true;
			return !text.isEditMode();
		}
		
		private static final long serialVersionUID = 1L;
		
		public void handlePress(CanvasMouseEventWrapper canvasMouseEventWrapper) {
			CompoundEdit2 edit = new CompoundEdit2();
			boolean rightSide=canvasMouseEventWrapper. getClickedXScreen()>lastDrawShape.getBounds().getCenterX();
			boolean down=canvasMouseEventWrapper. getClickedYScreen()>lastDrawShape.getBounds().getCenterY();
			
			if (button.resizesFont()) {
				if(!rightSide||canvasMouseEventWrapper.clickCount()>5||canvasMouseEventWrapper.isPopupTrigger()) {
					showFontSizeInputPopup(canvasMouseEventWrapper);
					return;
				}
				
				
				if(rightSide)
					button.makeResizeDown(down);
			}

			if (operateOnAll&&!text.isEditMode()) {
				LayerSelector selector = canvasMouseEventWrapper.getSelectionSystem();
				button.setSelector(selector);
				button.run();
			} else {
				button.actOnObject(edit, text);
				canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(edit);
			}
			
			
			
			canvasMouseEventWrapper.getAsDisplay().updateDisplay();
			
		}

		protected void showFontSizeInputPopup(CanvasMouseEventWrapper canvasMouseEventWrapper) {
			NumberInputPanel panel = button.getFontInputPanel(canvasMouseEventWrapper.getSelectionSystem());
			SmartPopupJMenu menu = new SmartPopupJMenu();
			menu.add(panel);
			menu.showForMouseEvent(canvasMouseEventWrapper, 0, 15);
		}

	}
	
	public class TextColorHandle2 extends GeneralActionListHandle {
		{setxShift(4); setyShift(4);}
		public TextColorHandle2(MultiSelectionOperator i, int num, MultiSelectionOperator[] items) {
			super(i, num, items);
			
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public boolean isHidden() {
			if(!text.isEditMode() && text instanceof ComplexTextGraphic) return true;
			
			return false;
		}

	}
	
	public class TextDimmingColorHandle2 extends GeneralActionListHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TextDimmingColorHandle2(SuperTextButton superscriptButton, int numHandleID,
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
	
	public class FontFamilyActionHandle extends GeneralActionListHandle implements ActionListener, SwingDialogListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private FontChooser fontChoser;

		public FontFamilyActionHandle(MultiSelectionOperator multiSelectionOperator, int i,
				MultiSelectionOperator[] ff) {
			super( multiSelectionOperator, i, ff);
			//familyC=new FontChooser(text.getFont()).generateFamilyCombo();
		//	familyC.addMouseListener(this);
			
			JMenuItem jM = new JMenuItem("More Fonts");
			jM.addActionListener(this);
			//jM.add(familyC);
			p.add(jM);
		}
		
		public boolean isHidden() {
			if(text.isEditMode()) return true;
			return false;
		}

	

		@Override
		public void actionPerformed(ActionEvent e) {
			StandardDialog s = new StandardDialog("Select font", true);
			s.setWindowCentered(true);
			fontChoser = new FontChooser(text.getFont());
		
			s.add("font", fontChoser);
			
			s.addDialogListener(this);
			
			StandardDialog s2 = new StandardDialog("Select font", true);
			s2.setWindowCentered(true);
			s2.remove(s2.getOptionDisplayTabs());
			s2.add(fontChoser.getFamChoser());
			s2.showDialog();
		}

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

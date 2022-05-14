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
 * Version: 2022.1
 */
package actionToolbarItems;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import externalToolBar.AbstractExternalToolset;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import graphicalObjects_SpecialObjects.TextGraphicContainer;
import locatedObject.ColorDimmer;
import locatedObject.LocatedObject2D;
import locatedObject.RainbowPaintProvider;
import locatedObject.RectangleEdges;
import objectDialogs.TextGraphicSwingDialog;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelectionSystem;
import selectedItemMenus.MultiSelectionOperator;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;
import undo.CombinedEdit;
import undo.UndoTextEdit;

/**Performs a specified edit on a series of Text objects*/
public class SuperTextButton extends BasicMultiSelectionOperator implements Serializable {





	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Color color=null;
	public ColorDimmer colorDimming=ColorDimmer.FULL_BRIGTHNESS;

	
	private TextGraphic modelText;
	private int startFontSize;
	private Font startFont=null;
	
	public static final int DO_NOTHIING=-1, TO_SUPERSCRIPT = 0, TO_SUBSCRIPT = 1, BOLDENS=2, ITALICIZES=3, UNDERLINES = 4, STRIKES = 5,
			CHANGES_COLOR = 7,
			RESIZES_FONT_UP=8, RESIZES_FONT_DOWN=9, SELECT_COLOR=10,
			DIMS_COLOR = 11, FONT_FAM=12,
			RESIZES_FONT_TO=14, JUSTIFIES_TEXT=15;
	
	private int type=DO_NOTHIING;
	
	private String fontFamily=null;
	private Integer justification=null;
	

	public SuperTextButton(String fam) {
		this.type=FONT_FAM;
		fontFamily=fam;
		
	}
	
	public SuperTextButton(int subscript) {
		this.type=subscript;
	}
	
	public SuperTextButton(Color c) {
		this.type=CHANGES_COLOR;
		color=c;
	}
	public SuperTextButton(TextGraphic m,Color c) {
		this.type=CHANGES_COLOR;
		color=c;
		 setModelText(m);
	}
	public SuperTextButton(TextGraphic m, ColorDimmer c) {
		this.type=DIMS_COLOR;
		colorDimming=c;
		 setModelText(m);
	}
	
	public SuperTextButton(TextGraphic c, boolean down) {
		this.type=RESIZES_FONT_UP;
		if (down)type=RESIZES_FONT_DOWN;
		setModelText(c);
		if(c==null) setModelText(new TextGraphic());
	}
	
	public SuperTextButton(TextGraphic c, int type, int variable) {
		if(type==RESIZES_FONT_TO) {
			this.type=type;
			this.startFontSize=variable;
		}
		if (setsJustification(type)) {
			this.type=type;
			setJustification(variable);
		}
		
			
		setModelText(c);
		if(c==null) setModelText(new TextGraphic());
		
	}
	
	
	
	boolean makesSuperScript() {
		return type==TO_SUPERSCRIPT;
	}
	boolean makesSubScript() {
		return type==TO_SUBSCRIPT;
	}
	
	public boolean resizesFont() {
		return type==RESIZES_FONT_UP||type==RESIZES_FONT_DOWN|| RESIZES_FONT_TO==type;
	}
	
	/**sets to resizes down if true, sets to resizes up otherwise*/
	public boolean makeResizeDown(boolean b) {
		if(b) type=RESIZES_FONT_DOWN; else type=RESIZES_FONT_UP;
		return true;
	}
	
	
	public boolean boldens() {
		return type==BOLDENS;
	}
	
	public boolean italicizes() {
		return type==ITALICIZES;
	}
	private boolean underlines() {
		return type==UNDERLINES;
	}
	
	private boolean strikes() {
		return type==STRIKES;
	}


	@Override
	public String getMenuCommand() {
		if (this.doesFontFamily()) return this.getFontFamily();
		if (makesSubScript()) return "Make Subscript "+getKeyBoardCommand() ;
		if (this.boldens()) return "Make Bold "+getKeyBoardCommand() ;
		if (this.italicizes()) return "Make Italic "+getKeyBoardCommand() ;
		if (this.underlines()) return "Make Underlined "+getKeyBoardCommand() ;
		if (this.strikes()) return "Strike-through "+getKeyBoardCommand() ;
		if (this.doesDimColor()) return "Dim Color to: "+ ColorDimmer.colorModChoices2[colorDimming.ordinal()];
		if (this.setsFontSize()) return "Resize Font ("+startFontSize+")";
		if (setsJustification() ) return "Align Text "+ TextGraphicSwingDialog.JUSTIFICATION_CHOICES[getJustification()];
		if(doesRecolor()) return "";
		return "Make Superscript From Selected "+getKeyBoardCommand() ;
	}
	
	
	private boolean setsFontSize() { 
		return type==RESIZES_FONT_TO;
	}
	
	private boolean setsJustification() {return setsJustification(type)&&getJustification()!=null;}
	
	private boolean setsJustification(int type) { 
		if (	JUSTIFIES_TEXT==type) 
						return true;
			return false;
	}
	
	/**keyboard shortcuts not yet implemented*/
	String getKeyBoardCommand() {
		return "";
	}

	public void run() {
		
		if(selector==null) return;//can do nothing if no selection system present
		
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		CombinedEdit edits=new CombinedEdit();
		
		if(this.getModelText()!=null)
			{
			startFont=this.getModelText().getFont();
				if (this.resizesFont()&&type==RESIZES_FONT_UP) startFontSize=this.getModelText().getFont().getSize()+2;
				if (this.resizesFont()&&type==RESIZES_FONT_DOWN) startFontSize=this.getModelText().getFont().getSize()-2;
		
			}
		
		
		for(LocatedObject2D a: all) {
			actOnObject(edits, a);
				}
		addUndo(edits);
		
		
	}


	
	/**returns true if the object is already in sthe stake that this class would transform it into*/
	public boolean objectIsAlready(Object a) {
		
		
		
		if (a instanceof ComplexTextGraphic && ((ComplexTextGraphic) a).isEditMode()) {
			
			ComplexTextGraphic c=(ComplexTextGraphic) a;
			
			if (makesSubScript()&&c.isSelectionASubScript())return true;
			if (makesSuperScript()&&c.isSelectionASuperScript())return true;
			if (italicizes()&&c.isSelectionItalic()) return true;
			if (boldens()&&c.isSelectionBold()) return true;
			if (underlines()&&c.isSelectionUnderlined())return true;
			if (strikes()&&c.isSelectionStrikedThrough()) return true;

		} else if (a instanceof TextGraphic) {
			TextGraphic t=(TextGraphic) a;
		
			if (italicizes()&&t.getFont().isItalic()) return true;
			if (boldens()&&t.getFont().isBold()) return true;
			if(doesRecolor() && t.getTextColor().equals(color)) return true;
		}
		
		if(a instanceof ComplexTextGraphic &&this.setsJustification()) {
			ComplexTextGraphic c=(ComplexTextGraphic) a;
			if(c.getParagraph().getJustification()==this.justification) return true;
		}
		
		return false;
	}

	public void actOnObject(CombinedEdit edits, LocatedObject2D a) {
		if(a instanceof TextGraphicContainer) {
			a=((TextGraphicContainer) a).getText();
		}
		
		
		if (a instanceof ComplexTextGraphic && ((ComplexTextGraphic) a).isEditMode()) {
			
			ComplexTextGraphic c=(ComplexTextGraphic) a;
			UndoTextEdit undo = new UndoTextEdit(c);
			if (makesSubScript())c.selectedRegionToSubscript();
			if (makesSuperScript())c.selectedRegionToSuperScript();
			if (italicizes()) c.italicizeSelectedRegion();
			if (boldens()) c.emboldenSelectedRegion();
			if (underlines()) c.underlineSelectedRegion();
			if (strikes()) c.strikeLineThroughSelectedRegion();
			if (doesRecolor()) c.colorSelectedRegion(color);
			if (resizesFont()&& startFontSize>0) c.setFontSize(startFontSize);
			
			undo.setUpFinalState();
			edits.addEditToList(undo);
		} else if (a instanceof TextGraphic) {
			TextGraphic t=(TextGraphic) a;
			UndoTextEdit undo = new UndoTextEdit(t);
			
			if (italicizes()) {
				boolean italic = t.getFont().isItalic();
				if(startFont!=null) italic=startFont.isItalic();
				t.setFont(TextGraphic.italicize(t.getFont(), italic));
			}
			if (boldens()) {
				boolean bold = t.getFont().isBold();
				if(startFont!=null) bold=startFont.isBold();
				t.setFont(TextGraphic.embolden(t.getFont(), bold));
			}
			
			if (this.resizesFont()&& startFontSize>0) t.setFontSize(startFontSize);
			if(doesRecolor()) t.setTextColor(color);
			if (this.doesDimColor()) t.setDimming(colorDimming);
			if (this.doesFontFamily()) t.setFontFamily(this.getFontFamily());
			undo.setUpFinalState();
			edits.addEditToList(undo);
		}
		
		if(a instanceof ComplexTextGraphic && setsJustification()) {
			ComplexTextGraphic c=(ComplexTextGraphic) a;
			UndoTextEdit undo = new UndoTextEdit(c);
			
				c.getParagraph().setJustification(getJustification());
			
			undo.setUpFinalState();
			edits.addEditToList(undo);
		}
		
	}

	public boolean doesRecolor() {
		if(type==SELECT_COLOR) return true;
		return color!=null;
	}
	
	private boolean doesDimColor() {
		return type==DIMS_COLOR;
	}
	
	private boolean doesFontFamily() {
		return this.fontFamily!=null;
	}
	
	
private String getFontFamily() {
		if(getModelText()!=null) return getModelText().getFont().getFamily();
		return this.fontFamily;
	}

/**the font to use for the menu item*/
public Font getMenuItemFont() {
	if(this.doesFontFamily()) return new Font(this.getFontFamily(), 1,  10);
	return null;
	}

String menPath=null;

public String getMenuPath() {
	
	return menPath;
}

public Component getInputPanel() {
	
	return null;
	}



/**returns a panel meant for the user to input a font size*/
public NumberInputPanel getFontInputPanel(LayerSelectionSystem s) {
	if(s!=null) this.setSelector(s);
	NumberInputPanel panel = new NumberInputPanel("Input Font Size", getModelText().getFont().getSize());
	panel.addNumberInputListener(new NumberInputListener() {
		
		@Override
		public void numberChanged(NumberInputEvent ne) {
			
			SuperTextButton runner = new SuperTextButton(getModelText(), RESIZES_FONT_TO, (int)ne.getNumber());
			runner.setSelector(selector);
			runner.setSelection(selector.getSelecteditems());
			runner.run();
			
			selector.getWorksheet().updateDisplay();
			
		}
	});
	
	return panel;
}



		
		public Icon getIcon() {
			if(this.setsJustification()) {
				if(getJustification()==TextParagraph.JUSTIFY_LEFT) return new AlignItem(RectangleEdges.LEFT).getDarkIcon();
				if(getJustification()==TextParagraph.JUSTIFY_CENTER) return new AlignItem(RectangleEdges.MIDDLE).getDarkIcon();
				if(getJustification()==TextParagraph.JUSTIFY_RIGHT) return new AlignItem(RectangleEdges.RIGHT).getDarkIcon();
			}
			return  new SuperTextIcon();
		}
		
		

		class SuperTextIcon implements Icon, Serializable { 

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;



			@Override
			public int getIconHeight() {
				
				return AbstractExternalToolset.DEFAULT_ICONSIZE;
			}

			@Override
			public int getIconWidth() {
				if(resizesFont()) return AbstractExternalToolset.DEFAULT_ICONSIZE+10;
				return AbstractExternalToolset.DEFAULT_ICONSIZE;
			}

			@Override
			public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
				Font startFont=arg1.getFont();
				if (doesUserSelectColor()) {
					Paint paint = RainbowPaintProvider.getRaindowGradient(new Point(arg2,  arg3), new Point(arg2+20,  arg3+20) );
				if (arg1 instanceof Graphics2D) {
					Graphics2D g=(Graphics2D) arg1;
					g.setPaint(paint);
					g.fillRect(arg2, arg3, 20, 20);
				}
				
				}
				
				if(doesDimColor()) {
					
					int x=arg2;
					int y=arg3;
					int w=20;
					int h=4;
					Color baseColor = getColorForDimmerText();
					
					for(int i=0; i<ColorDimmer.OUTSIDE_WHITE_INSITE_BLACK.ordinal(); i++) {
						Color col = ColorDimmer.modifyColor(baseColor , ColorDimmer.values()[i], true);
						arg1.setColor(col);
						arg1.fillRect(x, y, w, h);
						y+=4;
					} 
					
				}
				
				
				TextGraphic.setAntialiasedText(arg1, true);
				arg1.setColor(Color.black);
				if(color!=null) arg1.setColor(color);
				if (doesUserSelectColor()) {arg1.setColor(Color.white);};
				arg1.setFont(new Font("Arial", 0, 15));
				if(doesDimColor()) 
					{
					arg1.setFont(new Font("Arial", Font.BOLD, 16));
					arg1.setColor(ColorDimmer.modifyColor(getColorForDimmerText(), colorDimming, true));
					}
				if(color!=null) arg1.setFont(new Font("Arial", Font.BOLD, 18));
				if (boldens()) {
					arg1.setFont(new Font("Arial", Font.BOLD, 16));
					arg1.drawString("B", arg2+5, arg3+16);
					}else 
				if (italicizes()) {
					arg1.setFont(new Font("Arial", Font.ITALIC, 16));
					arg1.drawString("I", arg2+5, arg3+16);
					} else if (underlines()) {
						arg1.setFont(TextLineSegment.deriveUnderlinedFont(new Font("Arial", Font.PLAIN, 16)));
						
						arg1.drawString("U", arg2+5, arg3+16);
						} else
							if (strikes()) {
								arg1.setFont(TextLineSegment.deriveStrikedFont(new Font("Arial", Font.PLAIN, 16)));
								
								arg1.drawString("S", arg2+5, arg3+16);
								} else
									if (resizesFont()) {
										arg1.setFont(new Font("Arial", Font.PLAIN, 10));
										int xCubicles = arg2+20;
										arg1.drawLine(xCubicles, arg3+2, xCubicles,arg3+ 22);
										int splitLine = arg3+12;
										arg1.drawLine(xCubicles, splitLine, xCubicles+10,splitLine);
									    arg1.drawString("+", xCubicles+2, arg3+10);
										arg1.drawString("-", xCubicles+4, arg3+21);
										//arg1.drawRect(arg2+12, arg3+12, 12, 12);
										arg1.drawString(""+getModelText().getFont().getSize(), arg2+6, arg3+16);
										} 
									else
										if (doesFontFamily()) {
											arg1.setFont(new Font(getFontFamily(), Font.BOLD, 9));
											arg1.drawString("Font", arg2+2, arg3+16);
										}
										else 
				arg1.drawString("A", arg2+5, arg3+16);
				
				
				if (makesSuperScript()||makesSubScript()) {
					arg1.setFont(new Font("Arial", 1, 8));
				}
				if ((makesSuperScript())) arg1.drawString("b", arg2+14, arg3+10);
				if ( (makesSubScript()))	arg1.drawString("b", arg2+14, arg3+16);
				
				
				arg1.setFont(arg1.getFont().deriveFont((float) 14));
				
				arg1.setFont(startFont);
				
			}

			protected Color getColorForDimmerText() {
				Color baseColor = getModelText().getTextColor();
				if (getModelText() instanceof ComplexTextGraphic) {
					ComplexTextGraphic c=(ComplexTextGraphic) getModelText();
					baseColor = c.getParagraph().getAllSegments().get(0).getTextColor();
				}
				if(baseColor.equals(Color.black))baseColor=Color.white;
				return baseColor;
			}

			

			public boolean doesUserSelectColor() {
				return type==SELECT_COLOR;
			}
			}
		
		
		public static SuperTextButton[]  getForColors(boolean Stroke, Color... color) {
			SuperTextButton[] out = new SuperTextButton[color.length+1];
			for(int i=0; i<color.length; i++) {
				Color c1= color[i];
				out[i]=new SuperTextButton(c1);
			}
			SuperTextButton c = new SuperTextButton(Color.white);
			c.type=SELECT_COLOR;
			out[color.length]=c;
			return out;
		}
		
		public static SuperTextButton[]  getForDims(TextGraphic t) {
			SuperTextButton[] out = new SuperTextButton[ColorDimmer.values().length];
			for(int i=0; i<out.length; i++) {
				out[i]=new SuperTextButton(t, ColorDimmer.values()[i]);
			}
			return out;
		}
		
		
		public static MultiSelectionOperator[] getForFonts(TextGraphic t) {
			return getForFonts(t, getStandardFonts());
		}
		
		public static MultiSelectionOperator[] getForAllFonts() {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			 String[] f = ge.getAvailableFontFamilyNames();
			 
				MultiSelectionOperator[] out = new MultiSelectionOperator[f.length];
				for(int i=0; i<f.length; i++) {
					String c1= f[i];
					SuperTextButton superTextButton = new SuperTextButton(c1);
					out[i]=superTextButton;
					superTextButton.menPath="More Fonts";
				}
				
			return out;
		}
		
		public static String[] getStandardFonts() {
			return standardFonts;
		}

		public static final String[] standardFonts=new String[] {"Arial","Courier New", "SansSerif", "Times New Roman", "Helvetica"};
		
		public static MultiSelectionOperator[]  getForFonts(TextGraphic t, String... f) {
			MultiSelectionOperator[] out = new MultiSelectionOperator[f.length+1];
			for(int i=0; i<f.length; i++) {
				String c1= f[i];
				out[i]=new SuperTextButton(c1);
				
			}
			SuperTextButton c = new SuperTextButton(t.getFont().getFamily());
			out[f.length]=c;
			return out;
		}
		
		/**a set of operators that changes the font size, used for testing*/
		public static MultiSelectionOperator[]  getForFontSizes() {
			int[] sizes=new int[] {8, 12, 16, 20, 24};
			MultiSelectionOperator[] out = new MultiSelectionOperator[sizes.length];
			for(int i=0; i<sizes.length; i++) {
				SuperTextButton c = new SuperTextButton(null, RESIZES_FONT_TO, sizes[i]);
				out[i]=c;
				
			}
			
			return out;
		}
		
		
		/**returns the justification change operations*/
		public static MultiSelectionOperator[]  getJustifications() {
			MultiSelectionOperator[] out = new MultiSelectionOperator[3];
			for(int i=0; i<3; i++) {
				out[i]=new SuperTextButton(null, JUSTIFIES_TEXT, i);
			}
			return out;
		}

		public Integer getJustification() {
			if(this.getModelText() instanceof ComplexTextGraphic) {
				return ((ComplexTextGraphic) getModelText()).getParagraph().getJustification();
			}
			return justification;
		}

		public void setJustification(Integer justification) {
			this.justification = justification;
		}

		public TextGraphic getModelText() {
			return modelText;
		}

		public void setModelText(TextGraphic modelText) {
			this.modelText = modelText;
		}
		
		
		/**if the menu item is rendered in a special way returns the renderer*/
		public JMenuItem getMenuItemRenderer() {
			//if(this.doesDimColor())
			//	return new ColorDimRenderer(this.colorDimming);
			return null;
		}
		
		/**
		 Work in progress, a component that more clearly shows what colors are the target
		 */
	public class ColorDimRenderer extends JMenuItem {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private ColorDimmer dim;

			/**
			 * @param colorDimming
			 */
			public ColorDimRenderer(ColorDimmer colorDimming) {
				this.dim=colorDimming;
				
			}
			
			public void paintComponent(Graphics g) {
				
				
				super.paintComponent(g);
				
				/**paints a rectangle with possible colors*/
				if (g instanceof Graphics2D) try {
					Graphics2D g2=(Graphics2D) g;
					int gStart=this.getIconTextGap()+this.getIcon().getIconWidth();
					int gW=g2.getFontMetrics().stringWidth("Dim Color to:");
					g2.setPaint(RainbowPaintProvider.getRaindowGradient(gStart, gStart+gW, dim, new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.cyan,Color.magenta, Color.YELLOW}));
					g2.fillRect(gStart, 0,  gW, 20);
				} catch (Throwable t) {}
			}

	}
		
	}
	



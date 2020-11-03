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
import javax.swing.JColorChooser;

import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import logging.IssueLog;
import objectDialogs.TextGraphicSwingDialog;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.MultiSelectionOperator;
import standardDialog.NumberInputEvent;
import standardDialog.NumberInputListener;
import standardDialog.NumberInputPanel;
import undo.CompoundEdit2;
import undo.UndoManagerPlus;
import undo.UndoTextEdit;
import utilityClassesForObjects.ColorDimmer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RainbowPaintProvider;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.TextLineSegment;

public class SuperTextButton extends BasicMultiSelectionOperator implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int script;
	private Color color=null;
	public int colorDimming=0;
	private long lastTime;
	private transient UndoManagerPlus undoManager;
	private TextGraphic modelText;
	private int startFontSize;
	private Font startFont=null;
	public static final int BOLDENS=2, ITALICIZES=3, RESIZES_FONT_UP=8, RESIZES_FONT_DOWN=9, SELECT_COLOR=10;
	private static final int DIMS_COLOR = 11, FONT_FAM=12;
	public static final int RESIZES_FONT_TO=14, JUSTIFIES_TEXT=15;
	private String fontFamily=null;
	private Integer justification=null;
	

	public SuperTextButton(String fam) {
		this.script=FONT_FAM;
		fontFamily=fam;
		
	}
	
	public SuperTextButton(int subscript) {
		this.script=subscript;
	}
	
	public SuperTextButton(Color c) {
		this.script=7;
		color=c;
	}
	public SuperTextButton(TextGraphic m,Color c) {
		this.script=7;
		color=c;
		 setModelText(m);
	}
	public SuperTextButton(TextGraphic m, int c) {
		this.script=DIMS_COLOR;
		colorDimming=c;
		 setModelText(m);
	}
	
	public SuperTextButton(TextGraphic c, boolean down) {
		this.script=8;
		if (down)script=9;
		setModelText(c);
		if(c==null) setModelText(new TextGraphic());
	}
	
	public SuperTextButton(TextGraphic c, int type, int variable) {
		if(type==RESIZES_FONT_TO) {
			this.script=type;
			this.startFontSize=variable;
		}
		if (setsJustification(type)) {
			this.script=type;
			setJustification(variable);
		}
		
			
		setModelText(c);
		if(c==null) setModelText(new TextGraphic());
		
	}
	
	
	
	boolean makesSuperScript() {
		return script==0;
	}
	
	public boolean resizesFont() {
		return script==8||script==9|| RESIZES_FONT_TO==script;
	}
	
	/**sets to resizes down if true, sets to resizes up otherwise*/
	public boolean makeResizeDown(boolean b) {
		if(b) script=9; else script=8;
		return true;
	}
	
	boolean makesSubScript() {
		return script==1;
	}
	
	public boolean boldens() {
		return script==BOLDENS;
	}
	
	public boolean italicizes() {
		return script==ITALICIZES;
	}
	private boolean underlines() {
		return script==4;
	}
	
	private boolean strikes() {
		return script==5;
	}


	@Override
	public String getMenuCommand() {
		if (this.doesFontFamily()) return this.getFontFamily();
		if (makesSubScript()) return "Make Subscript "+getKeyBoardCommand() ;
		if (this.boldens()) return "Make Bold"+getKeyBoardCommand() ;
		if (this.italicizes()) return "Make Italic "+getKeyBoardCommand() ;
		if (this.underlines()) return "Make Underlined "+getKeyBoardCommand() ;
		if (this.strikes()) return "Strike-through "+getKeyBoardCommand() ;
		if (this.doesDimColor()) return "Dim Color to: "+ ColorDimmer.colorModChoices2[colorDimming];
		if (this.setsFontSize()) return "Resize Font ("+startFontSize+")";
		if (setsJustification() ) return "Align Text "+ TextGraphicSwingDialog.JUSTIFICATION_CHOICES[getJustification()];
		if(doesRecolor()) return "";
		return "Make Superscript From Selected "+getKeyBoardCommand() ;
	}
	
	
	private boolean setsFontSize() { 
		return script==RESIZES_FONT_TO;
	}
	
	private boolean setsJustification() {return setsJustification(script)&&getJustification()!=null;}
	
	private boolean setsJustification(int type) { 
		if (	JUSTIFIES_TEXT==type) 
						return true;
			
			return false;
	}
	
	String getKeyBoardCommand() {
		return "";
	}

	public void run() {
		
		long time = System.currentTimeMillis();
		
		if (time-lastTime<500||script==SELECT_COLOR) {
			if (doesRecolor()){
			color  = JColorChooser.showDialog(null, "Color",color);
			 }
			
		} else  lastTime=System.currentTimeMillis();
		if(selector==null) return;//can do nothing if no selection system present
		
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		CompoundEdit2 edits=new CompoundEdit2();
		if (this.resizesFont()&&script==8) startFontSize=this.getModelText().getFont().getSize()+2;
		if (this.resizesFont()&&script==9) startFontSize=this.getModelText().getFont().getSize()-2;
		if(this.getModelText()!=null)
			startFont=this.getModelText().getFont();
			
		for(LocatedObject2D a: all) {
			actOnObject(edits, a);
				}
		if (getCurrentUndoManager()!=null) getCurrentUndoManager().addEdit(edits);
		
		
	}
	
	
	public boolean objectIsAlready(Object a) {
		
		if (a instanceof ComplexTextGraphic && ((ComplexTextGraphic) a).isEditMode()) {
			
			ComplexTextGraphic c=(ComplexTextGraphic) a;
			
			if (makesSubScript()&&c.isSelectionASubScript())return true;
			if (makesSuperScript()&&c.isSelectionASuperScript())return true;
			if (italicizes()&&c.isSelectionItalic()) return true;
			if (boldens()&&c.isSelectionBold()) return true;
			if (underlines()&&c.isSelectionUnderlined())return true;
			if (strikes()&&c.isSelectionStrikedThrough()) return true;
			//if (color!=null) c.colorSelectedRegion(color);
			
			
			
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

	public void actOnObject(CompoundEdit2 edits, LocatedObject2D a) {
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
		if(script==SELECT_COLOR) return true;
		return color!=null;
	}
	
	private boolean doesDimColor() {
		return script==DIMS_COLOR;
	}
	
	private boolean doesFontFamily() {
		
		return this.fontFamily!=null;
	}
	
	
private String getFontFamily() {
		if(getModelText()!=null) return getModelText().getFont().getFamily();
		return this.fontFamily;
	}


public Font getMenuItemFont() {
	if(this.doesFontFamily()) return new Font(this.getFontFamily(), 1,  10);
	return null;
	}

String menPath=null;

public String getMenuPath() {
	
	return menPath;
}

public Component getInputPanel() {
//if(this.setsFontSize()||this.doesFontFamily()) return getFontInputPanel() ;
	
	return null;
	}




public NumberInputPanel getFontInputPanel(LayerSelector s) {
	if(s!=null) this.setSelector(s);
	NumberInputPanel panel = new NumberInputPanel("Input Font Size", getModelText().getFont().getSize());
	panel.addNumberInputListener(new NumberInputListener() {
		
		@Override
		public void numberChanged(NumberInputEvent ne) {
			
			SuperTextButton runner = new SuperTextButton(getModelText(), RESIZES_FONT_TO, (int)ne.getNumber());
			runner.setSelector(selector);
			runner.setSelection(selector.getSelecteditems());
			runner.run();
			
			selector.getGraphicDisplayContainer().updateDisplay();
			
		}
	});
	
	return panel;
}


	public UndoManagerPlus getCurrentUndoManager() {
		if (selector!=null&&selector.getGraphicDisplayContainer()!=null)
		return selector.getGraphicDisplayContainer().getUndoManager();
		return undoManager;
	}
		
		public Icon getIcon() {
			if(this.setsJustification()) {
				if(getJustification()==0) return new AlignItem(RectangleEdges.LEFT).getIcon();
				if(getJustification()==1) return new AlignItem(RectangleEdges.MIDDLE).getIcon();
				if(getJustification()==2) return new AlignItem(RectangleEdges.RIGHT).getIcon();
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
				
				return 25;
			}

			@Override
			public int getIconWidth() {
				if(resizesFont()) return 35;
				return 25;
			}

			@Override
			public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
				Font font0 = arg1.getFont();
				if (doesUserSelectColor()) {
					Paint paint = RainbowPaintProvider.getRaindowGradient(new Point(arg2,  arg3), new Point(arg2+20,  arg3+20) );
				if (arg1 instanceof Graphics2D) {
					Graphics2D g=(Graphics2D) arg1;
					g.setPaint(paint);
					g.fillRect(arg2, arg3, 20, 20);
				}
				
				}
				
				if(doesDimColor()) {
					int ind=0;
					int x=arg2;
					int y=arg3;
					int w=20;
					int h=4;
					Color baseColor = getColorForDimmerText();
					
					for(int i=0; i<5; i++) {
						Color col = ColorDimmer.modifyColor(baseColor , i, true);
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
				
				Graphics2D g2d=(Graphics2D) arg1;
				
				arg1.setFont(arg1.getFont().deriveFont((float) 14));
				
				
				
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
				return script==SELECT_COLOR;
			}
			}
		
		
		public static SuperTextButton[]  getForColors(boolean Stroke, Color... color) {
			SuperTextButton[] out = new SuperTextButton[color.length+1];
			for(int i=0; i<color.length; i++) {
				Color c1= color[i];
				out[i]=new SuperTextButton(c1);
			}
			SuperTextButton c = new SuperTextButton(Color.white);
			c.script=SELECT_COLOR;
			out[color.length]=c;
			return out;
		}
		
		public static SuperTextButton[]  getForDims(TextGraphic t) {
			SuperTextButton[] out = new SuperTextButton[6];
			for(int i=0; i<out.length; i++) {
				out[i]=new SuperTextButton(t, i);
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
		
		private static String[] getStandardFonts() {
			return standardFonts;
		}

		public static String[] standardFonts=new String[] {"Arial","Courier", "SansSerif", "Times New Roman", };
		
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
		
	}
	



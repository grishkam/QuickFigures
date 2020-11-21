package standardDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import graphicalObjects_BasicShapes.ComplexTextGraphic;
import utilityClassesForObjects.ColorDimmer;

/**A JCombo box designed for the user to select a color dimming effect
 * see ColorDimmer class for more information*/
public class ColorDimmingBox extends JComboBox<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**the colors used to display the text of the menu options*/
	static Color[] segColors=new Color[] {Color.red, Color.green, Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.GRAY};
	
	/**creates a new color dimming box*/
	public ColorDimmingBox() {
		super(ColorDimmer.colorModChoices2);
		ColorDimmingCellRenerer cc = new ColorDimmingCellRenerer();
		cc.setBox(this);
		cc.setFont(cc.getFont().deriveFont((float)20));
		this.setRenderer(cc);
	}
	
	/**creates a new color dimming box with initial value initial*/
	public ColorDimmingBox(int innitial) {
		this();
		this.setSelectedIndex(innitial);
		
	}

	/**draws the given text in several colors*/
	public static void drawRainbowString(Graphics g, int x, int y,  String st, int[] ints, Color[] colors) {
		ArrayList<String> stringarr = ComplexTextGraphic .splitStringBasedOnArray(st, ints);
		drawRainBowString(g,x,y, stringarr, ints, colors);
		
	}
	/**draws the given array of strings as a line of text with several colors*/
	public static void drawRainBowString(Graphics g, int x, int y,  ArrayList<String> stringarr, int[] ints, Color[] colors) {
		int ci=0;
		for(String st1: stringarr) {
			if (ci>=colors.length) ci=0;
			FontMetrics fm = g.getFontMetrics();
			g.setColor(colors[ci]);
			g.drawString(st1, x, y);
			x+=fm.stringWidth(st1);
			ci++;
		}
	}
	
	
	/**how to render the combo box choices*/
	public class ColorDimmingCellRenerer extends BasicComboBoxRenderer {
		
		/**
		 * 
		 */
		public int colorDims=0;
		private ColorDimmingBox box;
		
		private static final long serialVersionUID = 1L;
		
		public ColorDimmingCellRenerer() {
		
		}
	

		public void paint(Graphics g) {
			super.paint(g);
			int dim = this.colorDims;
			if (this.colorDims==-1) dim=box.getSelectedIndex();
			drawRainbowStringForDimmingBox(g, dim, this.getText());
			
		}

		/**Based on the dimming type, draws a many colored text from the string given with the dimmed colors*/
		public void drawRainbowStringForDimmingBox(Graphics g, int dimmingType, String text) {
			ColorDimmingBox.drawRainbowString(g, 1,this.getFont().getSize()+1, text, new int[]{3,2,2}, ColorDimmer.modifyArray( segColors, dimmingType, true));
		}
		
		public  Component	getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (out instanceof ColorDimmingCellRenerer) {
				ColorDimmingCellRenerer c=(ColorDimmingCellRenerer) out;
				c.colorDims=index;
				
					{this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont((float)20.0));}
					
				if (cellHasFocus) {
					c.colorDims=getBox().getSelectedIndex();
		
					}
			}
		
			return out;
				}

		public ColorDimmingBox getBox() {
			return box;
		}

		public void setBox(ColorDimmingBox box) {
			this.box = box;
			this.colorDims=box.getSelectedIndex();
		}
	}
	
	
	
	public static void main(String[] arg) {
	
	}

}

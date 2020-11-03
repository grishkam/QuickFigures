package standardDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import graphicalObjects_BasicShapes.ComplexTextGraphic;
import utilityClassesForObjects.ColorDimmer;

public class ColorDimmingBox extends JComboBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Color[] segColors=new Color[] {Color.red, Color.green, Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.GRAY};
	
	
	public ColorDimmingBox() {
		super(ColorDimmer.colorModChoices2);
		colorCellRenerer cc = new colorCellRenerer();
		cc.setBox(this);
		cc.setFont(cc.getFont().deriveFont((float)20));
		this.setRenderer(cc);
	}
	
	public ColorDimmingBox(int innitial) {
		this();
		this.setSelectedIndex(innitial);
		
	}
	
	//public static void indexOfValue(String value, ) {}
	
	
	public static void drawRainbowString(Graphics g, int x, int y,  String st, int[] ints, Color[] colors) {
		
		ArrayList<String> stringarr = ComplexTextGraphic .splitStringBasedOnArray(st, ints);
		drawRainBowString(g,x,y, stringarr, ints, colors);
		
	}
	
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
	
	
	
	public class colorCellRenerer extends BasicComboBoxRenderer {
		
		/**
		 * 
		 */
		public int colorDims=0;
		private ColorDimmingBox box;
		
		private static final long serialVersionUID = 1L;
		
		public colorCellRenerer() {
		
		}
	

		public void paint(Graphics g) {
			super.paint(g);
			int dim = this.colorDims;
			if (this.colorDims==-1) dim=box.getSelectedIndex();
			drawRainbowStringForDimmingBox(g, dim, this.getText());
			
		}


		public void drawRainbowStringForDimmingBox(Graphics g, int dim, String text) {
			ColorDimmingBox.drawRainbowString(g, 1,this.getFont().getSize()+1, text, new int[]{3,2,2}, ColorDimmer.modifyArray( segColors, dim, true));
		}
		
		public  Component	getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (out instanceof colorCellRenerer) {
				colorCellRenerer c=(colorCellRenerer) out;
				c.colorDims=index;
					{this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont((float)20.0));}
				if (cellHasFocus) {
					c.colorDims=getBox().getSelectedIndex();
		
					}
			}
		
			//Font font=new Font(out.getFont().getFamily(), index, out.getFont().getSize());
			//out.setFont(font);
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
		JFrame jf = new JFrame();
		jf.add(new ColorDimmingBox());
		jf.pack();jf.setVisible(true);
	}

}

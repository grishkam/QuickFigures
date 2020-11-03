package externalToolBar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;

public class textGraphicTreeIcon implements Icon {

	private Font font;
	private String letter1="a";
	private String letter2="b";
	private Color c1=Color.black;
	private Color c2=Color.black;

	public textGraphicTreeIcon(Font font, String t, Color...arg1) {
		this.setFont(font);
		this.letter1=t.substring(0,1);
	if (t.length()>1)	this.letter2=t.substring(1,2);
		setColors(arg1);
	}
	
	public void setColors(Color... c){
		if (c.length==0) return;
		 c1=c[0];
		if (c.length==1) c2=c[0];
		else  c2=c[1];
	}
	
	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return 12;
	}

	@Override
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return 14;
	}

	@Override
	public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
		if (arg1 instanceof Graphics2D) {
		Graphics2D g2d = (Graphics2D) arg1;
		
		BasicStroke bs = new BasicStroke(1);
		Rectangle r = new Rectangle(arg2, arg3, 12,12);
		arg1.setColor(Color.black);
		g2d.setStroke(bs);
		g2d.draw(r);
			
			
			
			
			
			g2d.setColor(c1);
			g2d.setFont(getFont().deriveFont((float )10));
			g2d.drawString(letter1, arg2+1, arg3+9);
			g2d.setColor(c2);
			g2d.drawString(letter2, arg2+6, arg3+9);
			
		}
		
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}}
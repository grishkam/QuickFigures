package externalToolBar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import graphicalObjects_BasicShapes.TextGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.MontageSpaces;

public class ToolIconWithText extends GraphicToolIcon{

	String[] text=new String[] {"ab", "cd"};
	
	private Font font=new Font("Arial", Font.BOLD, 8);

	private int place;
	
	public ToolIconWithText(int type, int place) {
		super(type);
		this.place=place;
		// TODO Auto-generated constructor stub
	}
	
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
			int arg3) {
		TextGraphic.setAntialiasedText(g, true);
		g.setColor(Color.black);
		g.setFont(font);
		Graphics2D g2=(Graphics2D) g;
		
		BasicMontageLayout bl = new BasicMontageLayout();
		bl.setHorizontalBorder(2);
		bl.setVerticalBorder(2);
		
		if (place==MontageSpaces.ROW_OF_PANELS) {
			bl.setLayoutBasedOnRect(new Rectangle(arg2+12, arg3+2, 9,9));
			bl.setCols(1);
			bl.setRows(2);
			for(int i=0; i<bl.getPanels().length; i++) {
				Rectangle2D p=bl.getPanels()[i];
				g2.draw(p);
				g2.drawString(text[i], (int)p.getX()-10, (int)p.getY()+8);
			}
		}
		
		if (place==MontageSpaces.COLUMN_OF_PANELS) {
			bl.setLayoutBasedOnRect(new Rectangle(arg2+2, arg3+12, 9,9));
			bl.setCols(2);
			bl.setRows(1);
			for(int i=0; i<bl.getPanels().length; i++) {
				Rectangle2D p=bl.getPanels()[i];
				g2.draw(p);
				g2.drawString(text[i], (int)p.getX()+1, (int)p.getY()-1);
			}
		}
		
		if (place==MontageSpaces.PANELS) {
			bl.setLayoutBasedOnRect(new Rectangle(arg2+2, arg3+2, 20,20));
			bl.setCols(1);
			bl.setRows(1);
			for(int i=0; i<bl.getPanels().length; i++) {
				Rectangle2D p=bl.getPanels()[i];
				g2.draw(p);
				g2.drawString(text[i], (int)p.getX()+1, (int)p.getY()+8);
			}
		}
		
		
		
 		
		
		
		
	}

}

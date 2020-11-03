package genericMontageLayoutToolKit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import externalToolBar.GraphicToolIcon;
import gridLayout.BasicMontageLayout;

public class LayoutShowingToolIcon extends GraphicToolIcon implements Icon{
	
	protected int type;

	public LayoutShowingToolIcon(int type) {
		super(type);
		this.type=type;
	}
	
	
	protected Color getPanelColor() {
		return Color.blue.darker().darker();
	}
	
	protected Color getBoundryColor() {
		return Color.red.darker().darker();
	}
	
	@Override
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2, int arg3) {
			Graphics2D g2d=(Graphics2D) g;
			g.setColor(getPanelColor());
			
			BasicMontageLayout layout = getDrawnLayout();
			layout.move(arg2, arg3);
			
			g2d.setStroke(new BasicStroke(1));
			for(Rectangle2D p: layout.getPanels()) {
				g2d.draw(p);
			}
			g2d.setColor(getBoundryColor());
			g2d.draw(layout.getBoundry());
		
	}

	protected BasicMontageLayout getDrawnLayout() {
		BasicMontageLayout layout = new BasicMontageLayout(2, 2, 6, 6, 2,2, true);
		layout.setLabelSpaces(2, 2, 3, 3);
		layout.move(3, 3);
		return layout;
	}

	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return 25;
	}

	@Override
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return 25;
	}

}

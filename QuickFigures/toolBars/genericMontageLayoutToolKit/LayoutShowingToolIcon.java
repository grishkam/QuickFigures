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

/***/
public class LayoutShowingToolIcon extends GraphicToolIcon implements Icon{
	
	private static final int ICONSIZE = 25;
	protected int type;
	Color[] panelColor    = new Color[] {Color.blue.darker().darker()};

	Color boundryColors = Color.red.darker().darker();

	public LayoutShowingToolIcon(int type) {
		super(type);
		this.type=type;
	}
	
	protected Color getPanelColor(int i) {
		return getPanelColors()[i%getPanelColors().length];
	}

	/**
	 * @return
	 */
	protected Color[] getPanelColors() {
		return panelColor;
	}
	
	protected Color getBoundryColor() {
		return boundryColors;
	}
	
	@Override
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2, int arg3) {
			Graphics2D g2d=(Graphics2D) g;
			
			
			BasicMontageLayout layout = getDrawnLayout();
			layout.move(arg2, arg3);
			
			g2d.setStroke(new BasicStroke(1));
			for(Rectangle2D p: layout.getPanels()) {
				g.setColor(getPanelColor(0));
				g2d.draw(p);
			}
			g2d.setColor(getBoundryColor());
			g2d.draw(layout.getBoundry());

	}
	
	

	protected BasicMontageLayout getDrawnLayout() {
		return createSimpleIconLayout(type);
	}

	/**
	creates a layout for drawing and icon
	 */
	protected BasicMontageLayout createSimpleIconLayout( int type) {
		BasicMontageLayout layout = new BasicMontageLayout(2, 2, 6, 6, 2,2, true);
		layout.setLabelSpaces(2, 2, 3, 3);
		layout.move(3, 3);
		return layout;
	}

	@Override
	public int getIconHeight() {
		return ICONSIZE;
	}

	@Override
	public int getIconWidth() {
		return ICONSIZE;
	}
	
	public LayoutShowingToolIcon copy(int type) {
		return new LayoutShowingToolIcon(type);
	}

}

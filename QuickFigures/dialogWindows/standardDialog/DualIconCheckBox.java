package standardDialog;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JCheckBox;

public class DualIconCheckBox extends JCheckBox {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public DualIconCheckBox(Icon icon1, Icon icon2) {
		super();
		this.icon1 = icon1;
		this.icon2 = icon2;
	}
	Icon icon1;
	Icon icon2;
	Insets iconInsets=new Insets(2,2,2,2);
	
	
	public void paintComponent(Graphics g) {
		
		
		
		if (!this.isSelected()) {
			icon1.paintIcon(this, g, iconInsets.left, iconInsets.top);
		} else {
			icon2.paintIcon(this, g,  iconInsets.left, iconInsets.top);
		}
		//else super.paintComponent(g);
		
		
	//	g.setColor(Color.darkGray);
		
		
	}
	
	@Override
	public Dimension getPreferredSize() {
	
		int width=icon1.getIconWidth()+iconInsets.left+iconInsets.right;
		int height=icon1.getIconHeight()+iconInsets.top+iconInsets.bottom;
		return  new Dimension(width, height) ;
	}
	
	public int getHeight() {
		return  getPreferredSize().height;
	}
	
	
	public int getWidth() {
		return  getPreferredSize().width;
	}

}

package externalToolBar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;


	public abstract class GraphicToolIcon implements Icon{

		private Component lastComponent;
		static int normalType=0, pressedType=1, rolloverType=2;
		protected int type=0;
		protected int iconDim=25;
		
		Color lightColor=new Color(210,210,210);
		Color darkColor=new Color(120,120,120);

		public GraphicToolIcon(int type) {
			this.type=type;
			// TODO Auto-generated constructor stub
		}

		/**
		 * 
		 */
		
		public void paintIcon(Component arg0, Graphics g, int arg2, int arg3) {
			this.lastComponent=arg0;
			g.setColor(lightColor);
			if (type==pressedType) {
				g.setColor(darkColor);
			}
			g.fillRect(arg2+1, arg3+1, 23,23);
			
			paintObjectOntoIcon(arg0, g, arg2, arg3);
			
			g.setColor(Color.white);
			g.drawLine(arg2+25, arg3, arg2, arg3);
			g.setColor(Color.white);
			g.drawLine(arg2, arg3, arg2, arg3+24);
			
			if (g instanceof Graphics2D) {
				Graphics2D g2d=(Graphics2D) g;
				g2d.setStroke(new BasicStroke(1));
			}
			g.setColor(Color.black);
			g.drawLine(arg2+25, arg3+24, arg2, arg3+24);
			g.setColor(Color.darkGray);
			g.drawLine(arg2+24, arg3, arg2+24, arg3+24);
		}
		
		
		
		protected abstract void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
				int arg3);



		public int getIconHeight() {return iconDim;}
	
		public int 	getIconWidth(){return iconDim;}
		
	
}

package standardDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JFrame;

import graphicalObjectHandles.TextHandle;
import graphicalObjects_BasicShapes.TextGraphic;

public class FontSizeSelector  extends GraphicComponent implements MouseListener, MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{this.addMouseListener(this); this.addMouseMotionListener(this);setMagnification(1);}
	
	private float fontSize=12;
	private int handleclick=-1;
	TextGraphic textItem=new TextGraphic("Text");
	 {textItem.select();textItem.setLocationUpperLeft(10, 40); textItem.setTextColor(Color.black);}
	public void setFont(Font f) {
		textItem.setFont(f);
		setFontSize(f.getSize());
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		Point2D p =new Point(arg0.getX(), arg0.getY());
		handleclick=textItem.handleNumber((int)p.getX(), (int)p.getY());
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		Point2D p = this.getCord().transformME(arg0);//.getInverse().transformP(new Point(arg0.getX(), arg0.getY()));
		if(handleclick>0) {
			textItem.handleMove(handleclick, getMousePosition(), new Point( (int)p.getX(), (int)p.getY()));
			if (TextHandle.TEXT_FONT_SIZE_HANDLE==handleclick) {
				double newsize = textItem.getBaseLineStart().getY()-p.getY();
				textItem.setFontSize((int) newsize);
			}
			
			setFontSize(textItem.getFont().getSize());
		//	textItem.getSmartHandleList().getHandleNumber(handleclick).handleDrag(new GMouseEvent(null, arg0));

		}
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintComponent(Graphics g) {
		//getSnappingBehaviour().snapLocatedObjects(r2, r1);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(),  this.getHeight());
		
		
		textItem.draw((Graphics2D) g,getCord());
	
	}
	
	public int getHeight() {
		return 60;
		
	}
	
	public int getWidth() {
		return 150;
		
	}
	
	  public Dimension getPreferredSize() {
	        return new Dimension(getWidth(),getHeight());
	    }
	
	  
		public static void main(String[] args) {
			JFrame ff = new JFrame("frame");
			ff.setLayout(new FlowLayout());
			ff.add(new JButton("button"));
			FontSizeSelector sb = new FontSizeSelector();
			ff.add(sb);
			ff.pack();
			
			ff.setVisible(true);
		}

		public float getFontSize() {
			return fontSize;
		}

		public void setFontSize(float fontSize) {
			this.fontSize = fontSize;
		}

	
	
}

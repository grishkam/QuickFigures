package standardDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import graphicalObjects.*;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.SnappingPosition;

/***/
public class SnapBox extends GraphicSelectable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SnappingPosition snappingBehaviour=SnappingPosition.defaultExternal();
	
	private RectangularGraphic referenceObject=new RectangularGraphic(new Rectangle(50,30,120,90));
	RectangularGraphic r2=new RectangularGraphic(new Rectangle(0,0,40,30));
	
	private ZoomableGraphic overrideObject=null;
	
	TextGraphic t=new TextGraphic(getSnappingBehaviour().getShortDescription());
	TextGraphic t2=new TextGraphic(getSnappingBehaviour().getSecondDescription());
	private ArrowGraphic l1;
	private ArrowGraphic l2;
	
	private boolean isGridded=true;

	
	

	/**if one wants the gui to depict a grid of squares as the item being relocated, one calls this method*/
	public void setToMontageMode() {
		 isGridded=true;
		 double drx2=r2.getBounds().getX()+r2.getObjectWidth()/2;
		 double dry2=r2.getBounds().getY()+r2.getObjectHeight()/2;
		 
		 l1=ArrowGraphic.createLine(Color.black, Color.black, new Point2D.Double(drx2, r2.getBounds().getY()), new Point2D.Double(drx2, r2.getBounds().getMaxY()));
		 l2=ArrowGraphic.createLine(Color.black, Color.black, new Point2D.Double(r2.getBounds().getX(), dry2), new Point2D.Double( r2.getBounds().getMaxX(),dry2));
			l2.setStrokeWidth(1);
			l1.setStrokeWidth(1);
	
	}

	{setTextItemUp(t,100); setTextItemUp(t2,130);}
	
	//private String label; 
	public void setTextItemUp(TextGraphic t, int y) {
		t.setLocationUpperLeft(220, y); t.setTextColor(Color.black); t.setFont(new Font("Arial", Font.BOLD, 30));
		}
	
	
	{setRectProp(r2);setRectProp(getReferenceObject());r2.setFillColor(Color.blue.brighter()); getReferenceObject().setFillColor(Color.red.darker());
	//setRectProp(r2);
	}
	
	public void setRectProp(RectangularGraphic r1) {
		r1.setFilled(true);
		r1.setDashes(new float[]{10000});
		r1.setStrokeWidth(4);
		
	}
	
	
	public SnapBox(SnappingPosition snappingBehaviour) {
		this. snappingBehaviour= snappingBehaviour;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		getSnappingBehaviour().snapLocatedObjects(r2, getReferenceObject());
		
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(),  this.getHeight());
		Graphics2D g2 = (Graphics2D) g;
		
		getReferenceObject().draw((Graphics2D) g,getCord());
		
		
		 if (getOverrideObject()!=null) getOverrideObject().draw(g2, cords); else 
			 	r2.draw(g2,getCord());
		
		t.setText(getSnappingBehaviour().getShortDescription());
		t2.setText(getSnappingBehaviour().getSecondDescription());
		t.draw((Graphics2D) g,getCord());
		t2.draw((Graphics2D) g,getCord());
		
		if (isGridded) {
			this.setToMontageMode();
			l1.draw(g2, cords);
			l2.draw(g2, cords);
		}
		
	}
	

	

	public int getHeight() {
		return 60;
		
	}
	
	public int getWidth() {
		return 180;
		
	}
	
	  public Dimension getPreferredSize() {
	        return new Dimension(getWidth(),getHeight());
	    }
	
	public static void main(String[] args) {
	/**	JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		ff.add(new JButton("button"));
		SnapBox sb = new SnapBox();
		ff.add(sb);
		ff.pack();
		
		ff.setVisible(true);*/
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		int x = arg0.getX();
		int y = arg0.getY();
		Point p1 = new Point(x,y);
		Point p2 = new Point();
		try {
			this.getCord().getAfflineTransform().createInverse().transform(p1, p2);
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LocatedObject2D willSnap=r2;
		if (getOverrideObject()!=null && getOverrideObject() instanceof LocatedObject2D) {
			 willSnap=(LocatedObject2D) getOverrideObject();
		}
		
		getSnappingBehaviour().setToNearestSnap(r2.getBounds(), getReferenceObject().getBounds(), p2);
		getSnappingBehaviour().snapLocatedObjects(willSnap, getReferenceObject());
		
		
		
		
		super.notifyListeners(new ItemEvent(this, 0, this.getSnappingBehaviour(), 0));
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public SnappingPosition getSnappingBehaviour() {
		return snappingBehaviour;
	}


	public void setSnappingBehaviour(SnappingPosition snappingBehaviour) {
		this.snappingBehaviour = snappingBehaviour;
		this.repaint();
	}

	@Override
	public Object[] getSelectedObjects() {
		// TODO Auto-generated method stub
		return new Object[] {getSnappingBehaviour() };
	}


	


	@Override
	public int getSelectionNumber() {
		// TODO Auto-generated method stub
		return this.getSnappingBehaviour().getSnapType();
	}
	@Override
	public void setSelectionNumber(int index) {
		this.getSnappingBehaviour().setSnapType(index);
		
	}


	@Override
	public Object getSelectedItem() {
		// TODO Auto-generated method stub
		return this.getSnappingBehaviour();
	}


	public RectangularGraphic getReferenceObject() {
		return referenceObject;
	}


	public ZoomableGraphic getOverrideObject() {
		return overrideObject;
	}


	public void setOverrideObject(ZoomableGraphic overrideObject) {
		this.overrideObject = overrideObject;
	}


	


	

}
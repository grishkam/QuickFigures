package standardDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayerPane;

public class GraphicComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayerPane graphicLayers=new GraphicLayerPane("pane");
	int width=250;
	int height=180;
	
	public void setPrefferedSize(double width, double height) {
		this.width=(int) width;
		this.height=(int) height;
	}
	@Override
	public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
	
	
	
	BasicCoordinateConverter cords=null;
	private double magnification=0.4;
	
	
	Color background=Color.white;
	
	public BasicCoordinateConverter getCord() {
		if (cords==null) {
		BasicCoordinateConverter bcc = new BasicCoordinateConverter();
		bcc.setMagnification(getMagnification());
		cords=bcc;
		}
		return cords;
	}
	
	
	
	@Override
	public void paintComponent(Graphics g) {
	//	getSnappingBehaviour().snapLocatedObjects(r2, r1);
		
		g.setColor(background);
		g.fillRect(0, 0, this.getWidth(),  this.getHeight());
		
		
		getGraphicLayers().draw((Graphics2D)g, this.getCord());
	}
	public GraphicLayerPane getGraphicLayers() {
		return graphicLayers;
	}
	public void setGraphicLayers(GraphicLayerPane graphicLayers) {
		this.graphicLayers = graphicLayers;
	}
	public double getMagnification() {
		return magnification;
	}
	public void setMagnification(double magnification) {
		this.magnification = magnification;
	}
	
	

}

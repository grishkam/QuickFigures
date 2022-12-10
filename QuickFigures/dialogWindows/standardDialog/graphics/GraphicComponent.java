/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Dec 10, 2022
 * Version: 2022.2
 */
package standardDialog.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import standardDialog.graphics.GraphicComponent.CanvasMouseListener;
import standardDialog.graphics.GraphicComponent.PassalongMouseListener;

/**A component that contains a graphic layer with object to draw
 * objects are drawn on this component at a specific magnification
 * @see GraphicLayerPane
 * */
public class GraphicComponent extends JComponent {

	

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayerPane graphicLayers=new GraphicLayerPane("pane");
	protected int width=250;
	protected int height=180;
	protected BasicCoordinateConverter cords=null;
	private double magnification=0.4;
	
	
	
	protected Color background=Color.white;
	private ArrayList<CanvasMouseListener> canvaslistenerList=new ArrayList<CanvasMouseListener>();
	
	
	public GraphicComponent() {
		PassalongMouseListener l = new PassalongMouseListener(this);
		this.addMouseListener(l);
		this.addMouseMotionListener(l);
	}
	
	public void setPrefferedSize(double width, double height) {
		this.width=(int) width;
		this.height=(int) height;
	}
	@Override
	public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
	

	public BasicCoordinateConverter getCord() {
		if (cords==null) {
			BasicCoordinateConverter bcc = new BasicCoordinateConverter();
			bcc.setMagnification(getMagnification());
			cords=bcc;
		}
		cords.setMagnification(getMagnification());
		return cords;
	}
	
	
	
	@Override
	public void paintComponent(Graphics g) {
		
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
	
	public void addComponentMouseListener(CanvasMouseListener cml) {
		this.canvaslistenerList.add(cml);
		
	}
	
	/**
	 
	 * 
	 */
	public interface CanvasMouseListener {

		/**
		 * @param item
		 * @param e
		 */
		void itemAction(LocatedObject2D item, MouseEvent e);
			
	}
	
	public class CanvasMouseListenerEvent {
		
	}
	
	/**
	 
	 * 
	 */
public class PassalongMouseListener implements MouseListener, MouseMotionListener {

	private GraphicComponent targetedCoponent;

	/**
	 * @param graphicComponent
	 */
	public PassalongMouseListener(GraphicComponent graphicComponent) {
		targetedCoponent=graphicComponent;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		notifyProxy(e);

	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		notifyProxy(e);
	}

	/**
	 * @param e
	 */
	public void notifyProxy(MouseEvent e) {
		Point2D drag = getCord().unTransformClickPoint(e);
		LocatedObject2D item = new BasicObjectListHandler().getClickedRoi(graphicLayers, (int) drag.getX(),
				(int) drag.getY());
		
		if(item==null)
			return;
		
		for(CanvasMouseListener c:canvaslistenerList) try {
			c.itemAction(item, e);
		} catch (Throwable t) {
			IssueLog.log(t);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		notifyProxy(e);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		notifyProxy(e);

	}

	@Override
	public void mouseExited(MouseEvent e) {
		notifyProxy(e);

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		notifyProxy(e);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		notifyProxy(e);
		
	}

}

}

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
package standardDialog.graphics;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

import graphicalObjects_Shapes.RectangularGraphic;

public class FixedEdgeSelectable extends GraphicSelectable{

	/**
	 * 
	 */
	{this.addMouseListener(this);}
	RectangularGraphic r1=RectangularGraphic.blackRect();
	{super.getGraphicLayers().add(r1); ; r1.select();r1.setRectangle(new Rectangle(10,10,80,60));
	r1.hideStrokeHandle=true; r1.hideRotationHandle=true;
	}
	
	private static final long serialVersionUID = 1L;

	public FixedEdgeSelectable(int edge) {
		r1.setLocationType(edge);
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		r1.setLocationType(r1.handleNumber(e.getX(), e.getY()));
		super.notifyListeners(new ItemEvent(this, 0, r1, r1.getLocationType()));
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSelectionNumber() {
		// TODO Auto-generated method stub
		return r1.getLocationType();
	}
	
	@Override
	public void setSelectionNumber(int index) {
		r1.setLocationType(index);
		
	}

	@Override
	public Object getSelectedItem() {
		// TODO Auto-generated method stub
		return r1.getLocationType();
	}

	@Override
	public Object[] getSelectedObjects() {
		// TODO Auto-generated method stub
		return new Object[] {r1};
	}
	
	 public Dimension getPreferredSize() {
	        return new Dimension(40,32);
	    }


	

}

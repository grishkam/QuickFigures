/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package standardDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorChoicePopup extends JComponent implements MouseListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color start;
	JColorChooser cc;
	private ChangeListener change;

	public  ColorChoicePopup(Color start) {
		this.setSelectedColor(start);
		 cc = new JColorChooser(start);
		
		// this.add(cc);
		 this.addMouseListener(this);
		}
	
	public void addChangeListener(ChangeListener c) {
		this.change=c;
		cc.getSelectionModel().addChangeListener(c);
		cc.addMouseListener(this);
	}
	
	
	
	public static void main(String[] args) {
		JFrame jf = new JFrame();
	jf.add(new ColorChoicePopup(Color.red));
		jf.pack();
		jf.setVisible(true);
	}
	

		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(15,20);
		}

		@Override
		public void paint(Graphics g) {
			
			g.setColor(getSelectedColor());
			g.fillRect(0, 0, this.getWidth(),  this.getHeight());
		//	g.setColor(Color.darkGray);
			
			
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
			
			
			if (arg0.getSource()!=this)return;
			cc.setColor(start);
			
			JDialog d = JColorChooser.createDialog(this, "Select Color", true, cc, this, null);
			d.setVisible(true);
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (arg0.getSource()==cc) {
				setColorToBox() ;
			}
		}



		public Color getSelectedColor() {
			return start;
		}



		public void setSelectedColor(Color start) {
			this.start = start;
		}



		@Override
		public void actionPerformed(ActionEvent arg0) {
			setColorToBox() ;
		}
		
		public void setColorToBox() {
			setSelectedColor(cc.getColor());
		if (change!=null) {
			change.stateChanged(new ChangeEvent(this));
		}
			repaint();
		}
	}
	


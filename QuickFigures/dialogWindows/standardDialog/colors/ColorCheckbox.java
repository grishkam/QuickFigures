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
 * Date Modified: Jan 6, 2021
 * Version: 2023.1
 */
package standardDialog.colors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

/**A checkbox that appears as a color*/
public class ColorCheckbox extends JCheckBox {

	/**
	 * 
	 */
	Color sCol=Color.red;
	public ColorCheckbox(Color c) {
		sCol=c;
	}
	
	public void paintComponent(Graphics g) {
		
		
		g.setColor(this.getSelectedColor());
		if (!this.isSelected())  {
			
			if (g instanceof Graphics2D) {
				Graphics2D g2=(Graphics2D) g;
				g2.setStroke(new BasicStroke(4,1,1));
			}
			g.fillRect(4, 4, this.getWidth()-4,  this.getHeight()-4);
			g.setColor(this.getSelectedColor().darker().darker());
			g.drawLine(2, 16, 9, 20);
			g.drawLine( 9, 20, 19,2);
		
		}
		else {
			
			g.setColor(getSelectedColor().brighter());
			g.drawRect(7, 7,  this.getWidth()-14,  this.getHeight()-14);
			}
		
		//else super.paintComponent(g);
		
		
	//	g.setColor(Color.darkGray);
		
		
	}
	
	
	private Color getSelectedColor() {
		// TODO Auto-generated method stub
		return sCol;
	}
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		ff.add(new JButton("button"));
		//ColorCheckbox sb = ;
		ff.add(new ColorCheckbox(Color.red));
		ff.add(new ColorCheckbox(Color.green));
		ff.add(new ColorCheckbox(Color.blue));
		ff.pack();
		
		ff.setVisible(true);
	}
	
	public static ArrayList<JCheckBox> get4Channel() {
		ArrayList<JCheckBox> ff = new ArrayList<JCheckBox>();
		ff.add(new ColorCheckbox(Color.red));
		ff.add(new ColorCheckbox(Color.green));
		ff.add(new ColorCheckbox(Color.blue));
		ff.add(new ColorCheckbox(Color.black));
		return ff;
	}

}

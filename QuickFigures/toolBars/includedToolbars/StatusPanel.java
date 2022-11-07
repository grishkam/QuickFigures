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
 * Version: 2022.2
 */
package includedToolbars;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.RectangularGraphic;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**A panel for displaying the current status of an ongoing process.
  In general, One main status panel (the most recent one created)
  is used */
public class StatusPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int percentdone=0;//in a range of 0 to 100 progress

	String message="                              ";
	int barwidth=100;
	int barheight=12;
	
	
	public static StatusPanel currentStatus=new StatusPanel();
	
	JLabel lab0=new JLabel("status"); {lab0.setFont(new Font("Arial", 0, 9));}
	JLabel lab1=new JLabel(message); {lab1.setFont(new Font("Arial", 0, 9));}
	private GraphicObjectDisplayBasic<GraphicGroup> displayGroup;
	private RectangularGraphic rect1;
	private RectangularGraphic rect2;
	
	
	public static void updateStatus(String mess) {
		if(currentStatus!=null) {
			currentStatus.updateStatus(mess, 0);
		}
	}
	
	public static void updateLoopStatus(String msg, int i, int total) {
		currentStatus.updateStatus(msg, (int) (100*((double)i)/total));
	}
	
	public static void updateStatusBar(int progress) {
		if(currentStatus!=null) {
			currentStatus.updateStatus(currentStatus.lab1.getText(), progress);
		}
	}
	
	public StatusPanel() {
		setLayout(new GridBagLayout()); 
		GridBagConstraints c=new GridBagConstraints(); 
		c.gridy=2; 
		c.gridx=0; 
		c.anchor=GridBagConstraints.WEST;
		
		//creates the status bar graphic
			displayGroup=new GraphicObjectDisplayBasic<GraphicGroup>();
			GraphicGroup group1 = new GraphicGroup();
			displayGroup.setCurrentDisplayObject(group1);
			
			rect1=RectangularGraphic.blankRect(new Rectangle(1,1, 102, barheight), Color.black);
			rect1.setStrokeWidth(1);
			rect1.setFilled(false);	
			
			rect2=RectangularGraphic.blankRect(new Rectangle(2,2, percentdone, barheight-2), Color.blue.darker());
			rect2.setStrokeWidth(0);
			rect2.setFillColor(Color.blue.darker());
				group1.getTheInternalLayer().add(rect1);group1.getTheInternalLayer().add(rect2);
		
		this.add(lab0, c);	
		c.gridx=1; 
		this.add(displayGroup, c);
		c.gridx=2; 
		this.add(lab1, c);
		
		currentStatus=this;
	}
	
	/**updates the message and the percent*/
	void updateStatus(String message, int percent) {
		if (message.length()>38) message=message.substring(0, 37);
		this.message=message;
		this.percentdone=percent;
		updatePercent();
		this.repaint();
	}
	
	private void updatePercent() {
		rect2.setRectangle(new Rectangle(2,2, percentdone, barheight-2));
		lab1.setText(message);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("test me");
		StatusPanel panel = currentStatus;
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		
		
		currentStatus.updateStatus("is 80% done", 80);
	}
	
}

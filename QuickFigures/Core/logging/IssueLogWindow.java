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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**A text windows that can be used for displaying messages that the users can see and copy/paste
 * stack traces from exceptions are alsos printed on these windows
  useful if anyone wants to send a description of the bugs they encounter to the programmer (me).*/
public class IssueLogWindow  extends JFrame {

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	    int fontSize = 12;
		private JTextArea area;
	   
	    public IssueLogWindow(String title) {
	    	area=new JTextArea(8, 30);
	    	JScrollPane pane = new JScrollPane(area);
	    
	    	
	    	
	    	this.setTitle(title);
	    	
	    	this.add(pane);
	    	pack();
	    }
	  

	   
	    public static void main(String[] args) {
	    	
	    	IssueLogWindow win = new IssueLogWindow("Error report");
	    			win.setVisible(true);
	    			for(int i=0; i<40; i++)win.appendLine("Hello");
	    			win.appendToLine("World");
	    }
	   
	  public  void appendLine(String ln) {
		  appendToLine('\n'+"");
		  appendToLine(ln);
		  
	  }

	  public  void appendToLine(String ln) {
		 String tx = area.getText()+ln;
		 area.setText(tx);
	  }
	  
	  public PrintStream getStream() {
		  return new PrintStream(new specialErrowStream());
	  }
	  
	  class specialErrowStream extends OutputStream{

			@Override
			public void write(int arg0) throws IOException {
				appendToLine(((char)arg0)+"");
			}
			
			public void  	write(byte[] b) {
				
				appendToLine(new String(b));
			}
			
		}

	}


package logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**A text windows that can be used for displaying messages that the users can see and copy/paste
  useful if anyone wants to send a description of the bugs they encounter to the programmer (me).*/
public class MyTextWindow  extends JFrame {

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	    int fontSize = 12;
		private JTextArea area;
	   
	    public MyTextWindow(String title) {
	    	area=new JTextArea(8, 30);
	    	JScrollPane pane = new JScrollPane(area);
	    
	    	
	    	
	    	this.setTitle(title);
	    	
	    	this.add(pane);
	    	pack();
	    }
	  

	   
	    public static void main(String[] args) {
	    	
	    	MyTextWindow win = new MyTextWindow("Error report");
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


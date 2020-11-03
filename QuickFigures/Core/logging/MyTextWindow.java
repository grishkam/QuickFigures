package logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MyTextWindow  extends JFrame {

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		public static final String LOC_KEY = "results.loc";
	    public static final String WIDTH_KEY = "results.width";
	    public static final String HEIGHT_KEY = "results.height";
	    public static final String LOG_LOC_KEY = "log.loc";
	    public static final String LOG_WIDTH_KEY = "log.width";
	    public static final String LOG_HEIGHT_KEY = "log.height";
	    public static final String DEBUG_LOC_KEY = "debug.loc";
	    static final String FONT_SIZE = "tw.font.size";
	    static final String FONT_ANTI= "tw.font.anti";
	   
	    int[] sizes = {9, 10, 11, 12, 13, 14, 16, 18, 20, 24, 36, 48, 60, 72};
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


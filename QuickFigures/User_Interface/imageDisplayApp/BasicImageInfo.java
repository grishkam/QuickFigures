package imageDisplayApp;

import java.awt.Dimension;
import java.io.Serializable;

/**this class stores simple information about an image: canvas size */
public class BasicImageInfo implements Serializable {
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		String name="";private int width=500, height=300;

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}
		
		public Dimension getDimensions() {
			return new Dimension(getWidth(), getHeight());
		}
	
}

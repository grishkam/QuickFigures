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
 * Date Modified: Jan 12, 2021
 * Version: 2022.0
 */
package imageDisplayApp;

import java.awt.Dimension;
import java.io.Serializable;

/**this class stores simple information about an image: canvas size */
public class BasicImageInfo implements Serializable {
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


		private int width=500, height=300;
		private boolean autoResizeBlocked=false;

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

		public boolean isAutoResizeBlocked() {
			return autoResizeBlocked;
		}

		public void setAutoResizeBlocked(boolean autoResizeBlocked) {
			this.autoResizeBlocked = autoResizeBlocked;
		}
		
}

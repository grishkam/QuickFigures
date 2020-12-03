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
package graphicalObjects;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**like buffered image Graphic but the image here is a constant that 
  must be retrieved after deserialization.*/
public class SavableBufferedImageGraphic extends BufferedImageGraphic {
	
	public SavableBufferedImageGraphic(BufferedImage bi) {
		super(bi);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public BufferedImage getBufferedImage() {
		if (img==null) try {
			ByteArrayInputStream baos = new ByteArrayInputStream(serializedIm);
			img=ImageIO.read( baos);
			baos=null;
		} catch (Throwable t) {
			img=new BufferedImage((int)(getObjectWidth()/getScale()), (int)(getObjectHeight()/getScale()),  BufferedImage.TYPE_INT_RGB);
		}
		return img;
		}
	
	public void setImage(BufferedImage img) {
		this.img = img;
		try {
			serializedIm=imageToByteArray(img);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		
		}
	}
}

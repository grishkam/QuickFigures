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
package applicationAdapters;

/**an interface for any objects that contain a canvas with a raster of pixels (of some sort).
  some implementations contain a raster of pixels with width and height some implementations 
  just contain objects*/
public interface PixelContainer {
	
	public PixelWrapper getPixelWrapper();// if the object has a raster of pixels

	
	/**returns the dimensions*/
	public int width();
	public int height();

}

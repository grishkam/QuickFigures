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
 * Date Modified: Jan 4, 2021
 * Version: 2022.2
 */
package locatedObject;

/**Location change listener objects are called in response
 * to edits that affect a located object*/
public interface LocationChangeListener extends Mortal{
	
	
	public void objectMoved(LocatedObject2D object);
	public void objectSizeChanged(LocatedObject2D object);
	public void objectEliminated(LocatedObject2D object);
	public void userMoved(LocatedObject2D object);
	public void userSizeChanged(LocatedObject2D object);
	
}

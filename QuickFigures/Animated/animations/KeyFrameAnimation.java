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
package animations;

import java.util.ArrayList;

public interface KeyFrameAnimation {
	public void recordKeyFrame(int frame);
	public void updateKeyFrame(int frame);
	public void removeKeyFrame(int frame);
	
	public BasicKeyFrame isKeyFrame(int frame);
	public ArrayList<? extends BasicKeyFrame> getKeyFrames();

}

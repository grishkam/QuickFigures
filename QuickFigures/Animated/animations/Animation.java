package animations;

import java.io.Serializable;

/**an interface for animations*/
public interface Animation extends Serializable{
	
	/**sets which time point in the animation is currently.
	  Each implementation of animation does something different based on its condition at that time point */
	void setToFrame(int frameNum);

}

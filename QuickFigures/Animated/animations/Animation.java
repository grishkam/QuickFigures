package animations;

import java.io.Serializable;

public interface Animation extends Serializable{
	
	/**sets the positions and states of the animation to the given frame number*/
	void setToFrame(int frameNum);

}

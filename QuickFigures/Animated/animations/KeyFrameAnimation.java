package animations;

import java.util.ArrayList;

public interface KeyFrameAnimation {
	public void recordKeyFrame(int frame);
	public void updateKeyFrame(int frame);
	public void removeKeyFrame(int frame);
	
	public BasicKeyFrame isKeyFrame(int frame);
	public ArrayList<? extends BasicKeyFrame> getKeyFrames();

}

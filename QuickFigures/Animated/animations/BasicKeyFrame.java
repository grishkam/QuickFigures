package animations;

/**An interface that is implemented by animated objects */
public interface BasicKeyFrame {

	/**returns the frame number of this key frame*/
	public int getFrame();
	/**sets the frame number of this key frame*/
	public void setFrame(int t);
}

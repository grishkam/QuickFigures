package infoStorage;



public interface MetaDataSaver {

	
	public void setMetaDatause(
			BasicMetaDataHandler ijMetadataUse);
	public BasicMetaDataHandler IJMetaDatause() ;
	
	public void addOptionsToMetaData(MetaInfoWrapper ActiveStackImage) ;
	public void setOptionsToMetaData(MetaInfoWrapper imeta);
	//public void draw(ImageType imeta);
	
}

package fieldReaderWritter;

/**Classes that implements this, return an instance of class SVG exporter
  that adds them to the exported SVG file*/
public interface SVGExportable {
	public SVGExporter getSVGEXporter();
}

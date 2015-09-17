/**
 * GISrecord with all possible fields, setters and getters for each
 * @author Chase
 *
 */
public class GISrecord extends record {

	private int featureID = 0;
	private long offset = 0;
	private String featureName = "";
	private String featureClass = "";
	private String stateAlphabeticCode = "";
	private int stateNumericCode = 0;
	private String CountryName = "";
	private int countryNumericCode = 0;
	private String primaryLatitude = "";
	private String primaryLongitude = "";
	private String sourceLatitude = "";
	private String sourceLongitude = "";
	private double sourceLat = 0.0;
	private double sourceLong = 0.0;
	private int feetElev = 0;
	private int meterElev = 0;
	private String mapName = "";
	private String dateCreated = "";
	private String dateEdited = "";

	public double getSourceLat() {
		return sourceLat;
	}

	public void setSourceLat(double sourceLat) {
		this.sourceLat = sourceLat;
	}

	public double getSourceLong() {
		return sourceLong;
	}

	public void setSourceLong(double sourceLong) {
		this.sourceLong = sourceLong;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public GISrecord(Long plac, double x, double y, String nam) {
		super(x, y, nam);
		offset = plac;
	}

	public GISrecord() {
		super();
	}

	public void setOff(Long x) {
		offset = x;
	}

	public void setX1(double x) {
		x1 = x;
	}

	public void sety1(double x) {
		y1 = x;
	}

	public Long getOff() {
		return offset;
	}

	public int getFeatureID() {
		return featureID;
	}

	public void setFeatureID(int featureID) {
		this.featureID = featureID;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getFeatureClass() {
		return featureClass;
	}

	public void setFeatureClass(String featureClass) {
		this.featureClass = featureClass;
	}

	public String getStateAlphabeticCode() {
		return stateAlphabeticCode;
	}

	public void setStateAlphabeticCode(String stateAlphabeticCode) {
		this.stateAlphabeticCode = stateAlphabeticCode;
	}

	public int getStateNumericCode() {
		return stateNumericCode;
	}

	public void setStateNumericCode(int stateNumericCode) {
		this.stateNumericCode = stateNumericCode;
	}

	public String getCountryName() {
		return CountryName;
	}

	public void setCountryName(String countryName) {
		CountryName = countryName;
	}

	public int getCountryNumericCode() {
		return countryNumericCode;
	}

	public void setCountryNumericCode(int countryNumericCode) {
		this.countryNumericCode = countryNumericCode;
	}

	public String getPrimaryLatitude() {
		return primaryLatitude;
	}

	public void setPrimaryLatitude(String primaryLatitude) {
		this.primaryLatitude = primaryLatitude;
	}

	public String getPrimaryLongitude() {
		return primaryLongitude;
	}

	public void setPrimaryLongitude(String primaryLongitude) {
		this.primaryLongitude = primaryLongitude;
	}

	public String getSourceLatitude() {
		return sourceLatitude;
	}

	public void setSourceLatitude(String sourceLatitude) {
		this.sourceLatitude = sourceLatitude;
	}

	public String getSourceLongitude() {
		return sourceLongitude;
	}

	public void setSourceLongitude(String sourceLongitude) {
		this.sourceLongitude = sourceLongitude;
	}

	public double getLat() {
		return x1;
	}

	public void setLat(double primaryLatitudeDec) {
		x1 = primaryLatitudeDec;
	}

	public double getPrimaryLongitudeDec() {
		return y1;
	}

	public void setPrimaryLongitudeDec(double primaryLongitudeDec) {
		y1 = primaryLongitudeDec;
	}

	public int getFeetElev() {
		return feetElev;
	}

	public void setFeetElev(int feetElev) {
		this.feetElev = feetElev;
	}

	public int getMeterElev() {
		return meterElev;
	}

	public void setMeterElev(int meterElev) {
		this.meterElev = meterElev;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	/**
	 * 
	 * @return the Cat-Name for the HashTable
	 */
	public String getName() {
		return stateAlphabeticCode.trim()+ featureName.trim();
	}

	/**
	 * prints out the GISrecord as it appeared when it was created
	 */
	public String toString() {
		String sourceLatS = "";
		String sourceLongS = "";
		String feetElevS = "";
		String meterElevS = "";
		String countryNumericCodeS = "";
		String featureIDS = "";
		String stateNumericCodeS = "";
		String lat = "";
		String longi = "";
		if (sourceLat != 0.0) {
			sourceLatS = Double.toString(sourceLat);
		}
		if (sourceLong != 0.0) {
			sourceLongS = Double.toString(sourceLong);
		}
		if (feetElev != 0) {
			feetElevS = Integer.toString(feetElev);
		}
		if (meterElev != 0) {
			meterElevS = Integer.toString(meterElev);
		}
		if (countryNumericCode != 0) {
			countryNumericCodeS = Integer.toString(countryNumericCode);
		}
		if (featureID != 0) {
			featureIDS = Integer.toString(featureID);
		}
		if (stateNumericCode != 0) {
			stateNumericCodeS = Integer.toString(stateNumericCode);
		}
		if (y1 != 0) {
			lat = Double.toString(y1);
		}
		if (x1 != 0) {
			longi = Double.toString(x1);
		}
		if (offset < 0) {
			return "messed up record";
		}
		return featureIDS + "|" + featureName + "|" + featureClass + "|"
				+ stateAlphabeticCode + "|" + stateNumericCodeS + "|"
				+ CountryName + "|" + countryNumericCodeS + "|"
				+ primaryLatitude + "|" + primaryLongitude + "|" + longi + "|"
				+ lat + "|" + sourceLatitude + "|" + sourceLongitude + "|"
				+ sourceLatS + "|" + sourceLongS + "|" + meterElevS + "|"
				+ feetElevS + "|" + mapName + "|" + dateCreated + "|"
				+ dateEdited;
	}

	public String getDateEdited() {
		return dateEdited;
	}

	public void setDateEdited(String dateEdited) {
		this.dateEdited = dateEdited;
	}
}

package com.lycha.example.augmentedreality;
/**
 * Created by krzysztofjackowski on 24/09/15.
 */
public class AugmentedPOI {
	private int mId;
	private String mName;
	private String mDescription;
	private double mLatitude;
	private double mLongitude;
	
	public AugmentedPOI(Object newName, Object newDescription,
						Object newLatitude, Object newLongitude) {
		this.mName = (String) newName;
        this.mDescription = (String) newDescription;
        this.mLatitude = (double) newLatitude;
        this.mLongitude = (double) newLongitude;
	}
	
	public int getPoiId() {
		return mId;
	}
	public void setPoiId(int poiId) {
		this.mId = poiId;
	}
	public String getPoiName() {
		return mName;
	}
	public void setPoiName(String poiName) {
		this.mName = poiName;
	}
	public String getPoiDescription() {
		return mDescription;
	}
	public void setPoiDescription(String poiDescription) {
		this.mDescription = poiDescription;
	}
	public double getPoiLatitude() {
		return mLatitude;
	}
	public void setPoiLatitude(double poiLatitude) {
		this.mLatitude = poiLatitude;
	}
	public double getPoiLongitude() {
		return mLongitude;
	}
	public void setPoiLongitude(double poiLongitude) {
		this.mLongitude = poiLongitude;
	}
}

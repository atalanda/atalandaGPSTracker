package com.atalanda.gpstracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.location.Location;

public class LocationCache {
  private Location location;
  private String parameters;
  private String url;
  private String additionalHeaderKey;
  private String additionalHeaderValue;
  private Float batteryLevel;
  private String timestamp;

  public LocationCache(Location location, String parameters, String url, Float batteryLevel, String additionalHeaderKey, String additionalHeaderValue) {
    this.setLocation(location);
    this.setParameters(parameters);
    this.setUrl(url);
    this.setAdditionalHeaderKey(additionalHeaderKey);
    this.setAdditionalHeaderValue(additionalHeaderValue);
    this.setBatteryLevel(batteryLevel);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    this.setTimestamp(sdf.format(new Date()));
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Float getBatteryLevel() {
    return batteryLevel;
  }

  public void setAdditionalHeaderKey(String additionalHeaderKey) {
    this.additionalHeaderKey = additionalHeaderKey;
  }

  public String getAdditionalHeaderKey() {
    return additionalHeaderKey;
  }

  public void setAdditionalHeaderValue(String additionalHeaderValue) {
    this.additionalHeaderValue = additionalHeaderValue;
  }

  public String getAdditionalHeaderValue() {
    return additionalHeaderValue;
  }

  public void setBatteryLevel(Float batteryLevel) {
    this.batteryLevel = batteryLevel;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}

package com.saucelabs.tests;

public class Config {
    public static final String region = System.getProperty("region", "eu");
    public static final String SAUCE_EU_URL = "https://ondemand.eu-central-1.saucelabs.com:443/wd/hub";
    public static final String SAUCE_US_URL = "https://ondemand.us-west-1.saucelabs.com/wd/hub";
    public static final String SAUCE_CAP = "sauce_";
    public static final String VISUAL_CAP = "visual_";
}

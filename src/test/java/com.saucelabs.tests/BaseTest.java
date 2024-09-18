package com.saucelabs.tests;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.saucelabs.tests.Config.region;
import static com.saucelabs.tests.Config.SAUCE_EU_URL;
import static com.saucelabs.tests.Config.SAUCE_US_URL;
import static com.saucelabs.tests.Config.SAUCE_CAP;


public class BaseTest {
    protected static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    protected String WEB_URL = "https://www.saucedemo.com/";

    @BeforeSuite
    public void beforeSuite(ITestContext context) {
        System.out.println("beforeSuite hook " + context.getSuite().getName());
    }

    @BeforeTest
    public void beforeTest(ITestContext context)  {
        System.out.println("beforeTest hook " + context.getName());
    }

    @BeforeMethod
    public void setup(Method method) throws MalformedURLException {

        System.out.println("BeforeMethod hook " + method.getName());
        String methodName = method.getName();
        String responsive = "";

        URL url = switch (region) {
            case "us" -> new URL(SAUCE_US_URL);
            default -> new URL(SAUCE_EU_URL);
        };

        boolean isBuildCap = false;
        MutableCapabilities caps = new MutableCapabilities();
        MutableCapabilities sauceOptions = new MutableCapabilities();

        for (Map.Entry<String, String> entry : Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getAllParameters().entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();

            if (k.startsWith(SAUCE_CAP)) {
                // Sauce capability
                String sauceCap = k.replaceFirst(SAUCE_CAP, "");
                if (sauceCap.equals("build")) {
                    isBuildCap = true;
                }

                if (v.contains(" ")) {
                    // handle a list such as in tags cap
                    List<String> capList = Arrays.asList(v.split(" "));
                    sauceOptions.setCapability(sauceCap, capList);
                } else {
                    sauceOptions.setCapability(sauceCap, v);
                }
            } else {
                if (k.matches("responsive")) {
                    responsive = v;
                    System.out.println("*** responsive is " + responsive);
                }
                else {
                    caps.setCapability(k, v);
                }
            }
        }

        sauceOptions.setCapability("username", System.getenv("SAUCE_USERNAME"));
        sauceOptions.setCapability("accessKey", System.getenv("SAUCE_ACCESS_KEY"));
        sauceOptions.setCapability("name", methodName);

        if (!isBuildCap) { //handle build cap
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH");
            String buildLocal = "sauceDemo-" +dateTime.format(formatter);
            String buildVal = System.getenv("BUILD_TAG");
            sauceOptions.setCapability("build", buildVal == null ? buildLocal : buildVal);
        }

        // handle tunnel
        String tunnelName = System.getenv("SAUCE_TUNNEL_NAME");
        if (!(tunnelName == null)) { //handle build cap
            System.out.println("*** tunnel name is " + tunnelName);
            sauceOptions.setCapability("tunnelName", tunnelName);
        }

        caps.setCapability("sauce:options", sauceOptions);
        try {
            driver.set(new RemoteWebDriver(url, caps));
        } catch (Exception e) {
            System.out.println("*** Problem to create the driver " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Set the browser window size to simulate mobile screen resolution (e.g., 375x812 for iPhone X)
        if (!responsive.isEmpty()) {
            String[] dimensions = responsive.split(",");
            try {
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);
                driver.get().manage().window().setSize(new Dimension(width, height));
            } catch (NumberFormatException e) {
                System.out.println("Invalid dimensions provided.");
            }
        }
    }

    @AfterMethod
    public void teardown(ITestResult result) {
        System.out.println("AfterMethod hook " + result.getMethod().getMethodName());
        try {
            boolean bSuccess = result.isSuccess();
            ((JavascriptExecutor) getDriver()).executeScript("sauce:job-result=" + (bSuccess ? "passed" : "failed"));
            if (!bSuccess)
                ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +result.getThrowable().getMessage());

        } finally {
            System.out.println("Release driver");
            getDriver().quit();
        }
    }

    @AfterTest
    public void afterTest(ITestContext context)  {
        System.out.println("afterTest hook " + context.getName());
    }

    @AfterSuite
    public void afterSuite(ITestContext context)  {
        System.out.println("afterSuite hook " + context.getSuite().getName());
    }

    public WebDriver getDriver() {
        return driver.get();
    }

}

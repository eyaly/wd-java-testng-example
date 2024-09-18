package com.saucelabs.tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;


public class DemoBasicTest extends com.saucelabs.tests.BaseTest {

    @Test
    public void checkSwagLabsTitle() {
        System.out.println("Start checkSwagLabsTitle test");
        WebDriver driver = getDriver();

        driver.navigate().to(WEB_URL);

        String getTitle = driver.getTitle();
        Assert.assertEquals(getTitle, "Swag Labs");
    }
}
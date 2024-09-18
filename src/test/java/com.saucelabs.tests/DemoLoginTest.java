package com.saucelabs.tests;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DemoLoginTest extends BaseTest {


    @Test
    public void swagLabsSuccessLogin() {
        System.out.println("Start swagLabsSuccessLogin test");
        WebDriver driver = getDriver();

        // login
        driver.navigate().to(WEB_URL);


        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // verify we in the product page
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, WEB_URL + "inventory.html");
    }

    @Test
    public void swagLabsFailedLogin() {
        System.out.println("Start swagLabsFailedLogin test");
        WebDriver driver = getDriver();

        // login
        driver.navigate().to(WEB_URL);

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce1");
        driver.findElement(By.id("login-button")).click();

        // verify we still in main page
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, WEB_URL);
    }
}
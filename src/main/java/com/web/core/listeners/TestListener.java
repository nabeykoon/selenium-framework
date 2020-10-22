package com.web.core.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.web.core.base.BaseTest;
import com.web.core.util.ExtentReportManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;

public class TestListener implements ITestListener {

    Logger log;
    String testSuiteName;
    String testName;

    ExtentReports extent = ExtentReportManager.getReporter ();
    ExtentTest test;
    ThreadLocal<ExtentTest> ExtentTest = new ThreadLocal<ExtentTest> ();

    @Override
    public void onTestStart(ITestResult result) {
        //this.testMethodName=result.getMethod().getMethodName();
        log.info ("[Starting " + result.getMethod ().getMethodName () + "]");
        test = extent.createTest (result.getMethod ().getMethodName (), (String) result.getTestContext ().getAttribute ("browser"));
        ExtentTest.set (test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info ("[Test " + result.getMethod ().getMethodName () + " passed]");
        ExtentTest.get ().log (Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.info ("[Test " + result.getMethod ().getMethodName () + " failed]");
        //WebDriver driver=(WebDriver) result.getTestContext().getAttribute("WebDriver");
        Object currentClass = result.getInstance ();
        WebDriver driver = ((BaseTest) currentClass).getDriver ();
      // In case of returning RemoteWebDriver: Use Augmenter class to enhances the RemoteWebDriver by adding to it various interfaces including the TakesScreenshot interface.
        driver = new Augmenter ().augment(driver);
        log.info ("Driver hashcode for screenshot " + driver.hashCode ());
        String path = takeScreenshotDuringFailure (testName, result.getMethod ().getMethodName (), driver);
        ExtentTest.get ().fail ("Failure Screenshot", MediaEntityBuilder.createScreenCaptureFromPath (path, result.getMethod ().getMethodName ()).build ());
        ExtentTest.get ().log (Status.FAIL, "Test Failed");
        ExtentTest.get ().fail (result.getThrowable ());
    }

    protected String takeScreenshotDuringFailure(String testName, String testMethodName, WebDriver driver) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs (OutputType.FILE);
        String path = System.getProperty ("user.dir") + "\\Extent Reports\\Screenshots\\" + testName + "-" + testMethodName + ".png";
        try {
            FileUtils.copyFile (scrFile, new File (path));
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return path;
    }


    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {
        this.testSuiteName = context.getSuite ().getName ();
        this.testName = context.getCurrentXmlTest ().getName ();
        this.log = LogManager.getLogger (testName);
        log.info ("[TEST " + testName + " STARTED]");
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info ("[ALL " + testName + " FINISHED]");
        extent.flush ();
        ExtentTest.remove ();
    }
}
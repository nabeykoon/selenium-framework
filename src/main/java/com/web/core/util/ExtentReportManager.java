package com.web.core.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {

    private static ExtentReports extent;

    public synchronized static ExtentReports getReporter(){
        //ExtentReports, ExtentSparkReporter
        String pathToReport = System.getProperty("user.dir")+"\\Extent Reports\\ExtentReportResults.html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(pathToReport);
        reporter.config().setReportName("Web Automation Results");
        reporter.config().setDocumentTitle("Test Results");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Tester", System.getProperty("user.name"));
        return extent;

    }
}

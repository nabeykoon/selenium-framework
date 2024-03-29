package com.web.core.base;

import com.paulhammant.ngwebdriver.NgWebDriver;
import com.web.core.Components.ButtonComponent;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BasePageObject {

    protected WebDriver driver;
    protected Logger log;
    protected NgWebDriver ngDriver;

    public BasePageObject(WebDriver driver, Logger log) {
        this.driver = driver;
        this.log = log;
    }

    /**
     * Open page with given url
     * @param url
     */
    protected void openUrl(String url) {
        driver.get(url);
    }

    /**
     * Get Url of current page from browser
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** Get title of current page */
    public String getCurrentPageTitle() {
        return driver.getTitle();
    }

    /** Get source of current page */
    public String getCurrentPageSource() {
        return driver.getPageSource();
    }

    /**
     * Find element using given locator
     * @param locator
     * @return
     */

    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    /**
     * Find all elements using given locator
     * @param locator
     * @return
     */
    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Click on element with given locator when its visible
     * @param locator
     */
    protected void click(By locator) {
        waitForVisibilityOf(locator, 10);
        find(locator).click();
    }

    /**
     * Focus on element and then click on it with given locator when its clickable
     * @param locator
     */

    protected void focusedClick(By locator){
        waitUntilClickable (locator,10);
        WebElement element = find (locator);
        Actions action = new Actions(driver);
        //Focus to element
        action.moveToElement(element).perform();
        // To click on the element
        action.moveToElement(element).click().perform();
    }

    /**
     * Click on slow loadable buttons (Buttons with synchronization issues)
     * @param locator
     */

    protected void clickOnSlowLoadableButton(By locator){
        ButtonComponent button = new ButtonComponent (locator,driver);
        button.get ();
        button.click ();
    }

    /**
     * Focus and click on slow loadable buttons (Buttons with synchronization issues)
     * @param locator
     */

    protected void focusedClickOnSlowLoadableButton(By locator){
        ButtonComponent button = new ButtonComponent (locator,driver);
        button.get ();
        button.focusedClick ();
    }

    /**
     * Type given text into element with given locator
     * @param text
     * @param locator
     */
    protected void type(String text, By locator) {
        waitForVisibilityOf(locator, 10);
        find(locator).sendKeys(text);
    }

    /**
     * Select a value for a dropdown list by visible text
     * @param value
     * @param locator
     */
    protected void selectValueForDropdown(String value, By locator){
        waitUntilClickable (locator, 10);
        Select select = new Select (find (locator));
        select.selectByVisibleText (value);
    }

    /**
     * Wait for specific ExpectedCondition for the given amount of time in seconds
     * @param condition
     * @param timeOutInSeconds
     */
    private void waitFor(ExpectedCondition<WebElement> condition, Integer timeOutInSeconds) {
        timeOutInSeconds = timeOutInSeconds != null ? timeOutInSeconds : 30;
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(condition);
    }

    /**
     * Wait for given number of seconds for element with given locator to be visible on the page
     * @param locator
     * @param timeOutInSeconds
     */
    protected void waitForVisibilityOf(By locator, Integer... timeOutInSeconds) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                waitFor(ExpectedConditions.visibilityOfElementLocated(locator),
                        (timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : null));
                break;
            } catch (StaleElementReferenceException e) {
            }
            attempts++;
        }
    }

    /**
     * Wait for given number of seconds for element with given locator until clickable
     * @param locator
     * @param timeOutInSeconds
     */
    protected void waitUntilClickable(By locator, Integer... timeOutInSeconds) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                waitFor(ExpectedConditions.elementToBeClickable (locator),
                        (timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : null));
                break;
            } catch (StaleElementReferenceException e) {
            }
            attempts++;
        }
    }

    /**
     * wait until all angular request to be complete before interactive with elements. This will solve most of the synchronization issues in angular based web apps
     */
    protected void waitForAngularRequestsToFinish(){
        ngDriver = new NgWebDriver ((JavascriptExecutor)driver);
        ngDriver.waitForAngularRequestsToFinish ();
    }

    /**
     * Wait for alert present and then switch to it
     */

    protected Alert switchToAlert() {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert();
    }

    /**
     * Switch to another Tab
     * @param expectedTitle
     */
    public void switchToWindowWithTitle(String expectedTitle) {
        // Switching to new window
        String firstWindow = driver.getWindowHandle();

        Set<String> allWindows = driver.getWindowHandles();
        Iterator<String> windowsIterator = allWindows.iterator();

        while (windowsIterator.hasNext()) {
            String windowHandle = windowsIterator.next().toString();
            if (!windowHandle.equals(firstWindow)) {
                driver.switchTo().window(windowHandle);
                if (getCurrentPageTitle().equals(expectedTitle)) {
                    break;
                }
            }
        }
    }

    /**
     * Switch to iFrame using it's locator
     * @param frameLocator
     */
    protected void switchToFrame(By frameLocator) {
        driver.switchTo().frame(find(frameLocator));
    }

    /**
     * Press Key on locator
     * @param locator
     * @param key
     */
    protected void pressKey(By locator, Keys key) {
        find(locator).sendKeys(key);
    }

    /**
     * Press Key using Actions class
     * @param key
     */
    public void pressKeyWithActions(Keys key) {
        log.info("Pressing " + key.name() + " using Actions class");
        Actions action = new Actions(driver);
        action.sendKeys(key).build().perform();
    }

    public void performJavascriptActionOnElement(By locator, String arg){
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript (arg, find (locator));
    }

    /** Perform scroll to the bottom */
    public void scrollToBottom() {
        log.info("Scrolling to the bottom of the page");
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    /**
     * Drag 'from' element to 'to' element
     * @param from
     * @param to
     */
    protected void performDragAndDrop(By from, By to) {

        //Due to selenium bug following approach may not work in some pages
        // Actions action = new Actions(driver);
        // action.dragAndDrop(find(from), find(to)).build().perform();

        // Work around using JS executor to drag and drop
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript(
                "function createEvent(typeOfEvent) {\n" + "var event =document.createEvent(\"CustomEvent\");\n"
                        + "event.initCustomEvent(typeOfEvent,true, true, null);\n" + "event.dataTransfer = {\n"
                        + "data: {},\n" + "setData: function (key, value) {\n" + "this.data[key] = value;\n" + "},\n"
                        + "getData: function (key) {\n" + "return this.data[key];\n" + "}\n" + "};\n"
                        + "return event;\n" + "}\n" + "\n" + "function dispatchEvent(element, event,transferData) {\n"
                        + "if (transferData !== undefined) {\n" + "event.dataTransfer = transferData;\n" + "}\n"
                        + "if (element.dispatchEvent) {\n" + "element.dispatchEvent(event);\n"
                        + "} else if (element.fireEvent) {\n" + "element.fireEvent(\"on\" + event.type, event);\n"
                        + "}\n" + "}\n" + "\n" + "function simulateHTML5DragAndDrop(element, destination) {\n"
                        + "var dragStartEvent =createEvent('dragstart');\n"
                        + "dispatchEvent(element, dragStartEvent);\n" + "var dropEvent = createEvent('drop');\n"
                        + "dispatchEvent(destination, dropEvent,dragStartEvent.dataTransfer);\n"
                        + "var dragEndEvent = createEvent('dragend');\n"
                        + "dispatchEvent(element, dragEndEvent,dropEvent.dataTransfer);\n" + "}\n" + "\n"
                        + "var source = arguments[0];\n" + "var destination = arguments[1];\n"
                        + "simulateHTML5DragAndDrop(source,destination);",
                find(from), find(to));
    }

    /**
     * Perform mouse hover over element
     * @param element
     */
    protected void hoverOverElement(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();
    }

    /**
     * Add cookie
     * @param ck
     */
    public void setCookie(Cookie ck) {
        log.info("Adding cookie " + ck.getName());
        driver.manage().addCookie(ck);
        log.info("Cookie added");
    }

    /**
     * Get cookie value using cookie name
     * @param name
     * @return
     */
    public String getCookie(String name) {
        log.info("Getting value of cookie " + name);
        return driver.manage().getCookieNamed(name).getValue();
    }
}

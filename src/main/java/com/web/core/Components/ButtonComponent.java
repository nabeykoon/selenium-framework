package com.web.core.Components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.SlowLoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Clock;

// This class introduce to fix synchronization problems with Buttons
public class ButtonComponent extends SlowLoadableComponent<ButtonComponent> {

    private final By locator;
    private final WebDriver driver;

    public ButtonComponent(By locator, WebDriver driver) {
        super (Clock.systemDefaultZone (), 10);
        this.locator = locator;
        this.driver = driver;
    }

    public void click() {
        this.driver.findElement(this.locator).click();
    }
    public void focusedClick() {
        WebElement element = this.driver.findElement(this.locator);
        Actions action = new Actions(this.driver);
        //Focus to element
        action.moveToElement(element).perform();
        // To click on the element
        action.moveToElement(element).click().perform();
    }

    @Override
    protected void load() {
        // not to implement load method for components.
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            WebElement elem = this.driver.findElement(this.locator);
            if (elem.isDisplayed() && elem.isEnabled()) {
                return;
            } else {
                throw new Error(String.format("Button %s not ready", this.locator));
            }
        }catch(Exception e){
            throw new Error(e.getMessage(), e);
        }
    }

}

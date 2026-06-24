package ma.cfgbank.lcn_api.e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginE2ETest extends SeleniumTestBase {

    @Test
    public void testConnexionReussie() {
        driver.get("http://localhost:5173/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));

        emailInput.sendKeys("admin@cfgbank.ma");
        passwordInput.sendKeys("admin123");
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/cherche-lcn"));
        assertTrue(driver.getCurrentUrl().contains("/cherche-lcn"));
    }

    @Test
    public void testMotDePasseIncorrect() {
        driver.get("http://localhost:5173/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));

        emailInput.sendKeys("admin@cfgbank.ma");
        passwordInput.sendKeys("wrongpassword");
        submitButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Identifiants incorrects')]")));

        assertTrue(errorMessage.isDisplayed());
    }

    @Test
    public void testEmailInexistant() {
        driver.get("http://localhost:5173/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));

        emailInput.sendKeys("doesnotexist@cfgbank.ma");
        passwordInput.sendKeys("password123");
        submitButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Identifiants incorrects')]")));

        assertTrue(errorMessage.isDisplayed());
    }

    @Test
    public void testValidationChampsVides() {
        driver.get("http://localhost:5173/login");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
        submitButton.click();

        // Le navigateur doit bloquer la soumission via la validation HTML5 (champs requis)
        // L'URL ne doit pas changer
        assertTrue(driver.getCurrentUrl().contains("/login"));
        
        // On vérifie que l'élément email a bien l'attribut required
        WebElement emailInput = driver.findElement(By.id("email"));
        String requiredAttr = emailInput.getAttribute("required");
        assertTrue(requiredAttr != null && (requiredAttr.equals("true") || requiredAttr.equals("")));
    }

    @Test
    public void testFormatEmailInvalide() {
        driver.get("http://localhost:5173/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));

        emailInput.sendKeys("invalid-email-format");
        passwordInput.sendKeys("password123");
        submitButton.click();

        // L'attribut type="email" doit bloquer la soumission du formulaire
        // L'URL ne doit pas changer
        assertTrue(driver.getCurrentUrl().contains("/login"));
        
        // On vérifie que le champ email a bien le bon type
        assertTrue("email".equals(emailInput.getAttribute("type")));
    }
}

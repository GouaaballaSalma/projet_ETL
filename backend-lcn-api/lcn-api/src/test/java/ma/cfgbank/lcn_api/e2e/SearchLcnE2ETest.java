package ma.cfgbank.lcn_api.e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SearchLcnE2ETest extends SeleniumTestBase {

    private void loginAsAdmin() {
        driver.get("http://localhost:5173/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("admin@cfgbank.ma");
        driver.findElement(By.id("password")).sendKeys("admin123"); 
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/cherche-lcn"));
    }

    @Test
    public void testRechercheNominale() {
        loginAsAdmin();

        WebElement typePersonneSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(), 'Type de personne')]/following-sibling::select")));
        new Select(typePersonneSelect).selectByValue("PP");

        // 1. Sélectionner "CIN" et saisir "JH67683"
        WebElement typeIdentifiantSelect = driver.findElement(By.xpath("//label[contains(text(), 'Type identifiant 1')]/following-sibling::select"));
        new Select(typeIdentifiantSelect).selectByValue("CIN");

        WebElement inputValeurId = driver.findElement(By.xpath("//label[contains(text(), 'Valeur identifiant 1')]/following-sibling::input"));
        inputValeurId.sendKeys("JH67683");

        // 4. Sélecteur robuste pour le bouton Rechercher
        WebElement searchButton = driver.findElement(By.xpath("//button[contains(., 'Rechercher')]"));
        searchButton.click();

        // Le tableau doit afficher au moins un résultat
        WebElement firstResultRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody/tr[not(contains(., 'Aucune recherche')) and not(contains(., 'Aucun résultat'))]")));
        
        assertTrue(firstResultRow.isDisplayed());
    }

    @Test
    public void testRechercheInexistante() throws InterruptedException {
        loginAsAdmin();

        WebElement typePersonneSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(), 'Type de personne')]/following-sibling::select")));
        new Select(typePersonneSelect).selectByValue("PP");

        WebElement inputValeurId = driver.findElement(By.xpath("//label[contains(text(), 'Valeur identifiant 1')]/following-sibling::input"));
        inputValeurId.sendKeys("ZZ00000");

        WebElement searchButton = driver.findElement(By.xpath("//button[contains(., 'Rechercher')]"));
        searchButton.click();

        Thread.sleep(1000); 

        // Vérifier la présence du message exact "Aucun résultat trouvé."
        WebElement noResultMsg = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'Aucun résultat trouvé.')]")));
                
        assertTrue(noResultMsg.isDisplayed());
    }

    @Test
    public void testValidationVide() {
        loginAsAdmin();

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Rechercher')]")));
        searchButton.click();

        boolean hasDataRows = false;
        try {
            org.openqa.selenium.support.ui.WebDriverWait shortWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tbody/tr[not(contains(., 'Aucune recherche')) and not(contains(., 'Aucun résultat'))]")));
            hasDataRows = true;
        } catch (Exception e) {
            hasDataRows = false;
        }
        
        assertFalse(hasDataRows, "La validation a échoué : le tableau s'est chargé sans paramètres de recherche !");
    }

    @Test
    public void testModaleEdit() throws InterruptedException {
        loginAsAdmin();

        WebElement typePersonneSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(), 'Type de personne')]/following-sibling::select")));
        new Select(typePersonneSelect).selectByValue("PP");

        WebElement typeIdentifiantSelect = driver.findElement(By.xpath("//label[contains(text(), 'Type identifiant 1')]/following-sibling::select"));
        new Select(typeIdentifiantSelect).selectByValue("CIN");

        WebElement inputValeurId = driver.findElement(By.xpath("//label[contains(text(), 'Valeur identifiant 1')]/following-sibling::input"));
        inputValeurId.sendKeys("JH67683");

        WebElement searchButton = driver.findElement(By.xpath("//button[contains(., 'Rechercher')]"));
        searchButton.click();

        Thread.sleep(1000); 

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@title='Modifier']")));
        editButton.click();

        // 2. Attendre que la modale soit visible en ciblant spécifiquement le titre : "Modifier la saisie manuelle : "
        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(), 'Modifier la saisie manuelle : ')]")));
        assertTrue(modalTitle.isDisplayed());

        // Cibler le conteneur principal de la modale
        WebElement modalContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'fixed inset-0')]")));
        assertTrue(modalContainer.isDisplayed());
    }

    @Test
    public void testAnnulationModale() throws InterruptedException {
        loginAsAdmin();

        WebElement typePersonneSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(), 'Type de personne')]/following-sibling::select")));
        new Select(typePersonneSelect).selectByValue("PP");

        WebElement typeIdentifiantSelect = driver.findElement(By.xpath("//label[contains(text(), 'Type identifiant 1')]/following-sibling::select"));
        new Select(typeIdentifiantSelect).selectByValue("CIN");

        WebElement inputValeurId = driver.findElement(By.xpath("//label[contains(text(), 'Valeur identifiant 1')]/following-sibling::input"));
        inputValeurId.sendKeys("JH67683");

        WebElement searchButton = driver.findElement(By.xpath("//button[contains(., 'Rechercher')]"));
        searchButton.click();

        Thread.sleep(1000); 

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@title='Modifier']")));
        editButton.click();

        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(), 'Modifier la saisie manuelle : ')]")));
        assertTrue(modalTitle.isDisplayed());

        // 3 & 4. Clique sur le bouton "Annuler" avec un sélecteur robuste
        WebElement annulerButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Annuler']")));
        annulerButton.click();

        // 3. Vérifie bien que la modale disparaît (en utilisant le conteneur principal)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'fixed inset-0')]")));
        
        assertTrue(driver.findElements(By.xpath("//div[contains(@class, 'fixed inset-0')]")).isEmpty() || 
                   !driver.findElement(By.xpath("//div[contains(@class, 'fixed inset-0')]")).isDisplayed());
    }
}

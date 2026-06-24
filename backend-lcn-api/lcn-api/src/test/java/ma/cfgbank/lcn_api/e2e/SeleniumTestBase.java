package ma.cfgbank.lcn_api.e2e;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class SeleniumTestBase {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        // Utilisation de WebDriverManager pour télécharger automatiquement le bon ChromeDriver
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        // Optionnel : stratégie de chargement de page pour les apps React
        // options.setPageLoadStrategy(PageLoadStrategy.NORMAL); 
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        // Attente implicite de base (ex: 5s) pour laisser le temps au DOM de s'attacher
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        
        // WebDriverWait poussé à 15 secondes pour les éléments asynchrones plus longs
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            // [DEBUG] On commente driver.quit() pour maintenir le navigateur ouvert après une erreur
            // N'oubliez pas de le décommenter pour votre pipeline CI/CD !
            // driver.quit();
        }
    }
}

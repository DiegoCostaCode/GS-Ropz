package br.fiap.ropz.ropz.Selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class testsSelenium {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Teste de criação de usuário e localização")
    public void cadastrarUsuario() {
        driver.get("http://localhost:8080/cadaster");

        driver.findElement(By.id("nome")).sendKeys("Lucas Minozzo");
        driver.findElement(By.id("telefone")).sendKeys("(85) 99305-7572");
        driver.findElement(By.id("email")).sendKeys("lucas@gmail.com");
        driver.findElement(By.id("senha")).sendKeys("@Lucas123");
        driver.findElement(By.id("cep")).sendKeys("04516-012");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @Order(2)
    @DisplayName("Teste para realização de login")
    public void login() {
        driver.get("http://localhost:8080/login");

        driver.findElement(By.id("email")).sendKeys("lucas@gmail.com");
        driver.findElement(By.id("senha")).sendKeys("@Lucas123");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assertions.assertTrue(driver.getCurrentUrl().contains("/"));
    }

    @Test
    @Order(3)
    @DisplayName("Teste update de usuario e localização")
    public void updateUsuario() {
        driver.get("http://localhost:8080/login");

        driver.findElement(By.id("email")).sendKeys("lucas@gmail.com");
        driver.findElement(By.id("senha")).sendKeys("@Lucas123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        driver.findElement(By.id("nome")).clear();
        driver.findElement(By.id("nome")).sendKeys("Lucas Minozzo");

        driver.findElement(By.id("telefone")).clear();
        driver.findElement(By.id("telefone")).sendKeys("(85) 99305-7572");

        driver.findElement(By.id("email")).clear();
        driver.findElement(By.id("email")).sendKeys("lucas@gmail.com");

        driver.findElement(By.id("senha")).clear();
        driver.findElement(By.id("senha")).sendKeys("@Lucas123");

        driver.findElement(By.id("cep")).clear();
        driver.findElement(By.id("cep")).sendKeys("04516-012");

        driver.findElement(By.className("btn-update-info")).click();

        Assertions.assertTrue(driver.getCurrentUrl().contains("/"));

        Assertions.assertEquals("Lucas Minozzo", driver.findElement(By.id("nome")).getAttribute("value"));
        Assertions.assertEquals("(85) 99305-7572", driver.findElement(By.id("telefone")).getAttribute("value"));
        Assertions.assertEquals("lucas@gmail.com", driver.findElement(By.id("email")).getAttribute("value"));
        Assertions.assertEquals("04516-012", driver.findElement(By.id("cep")).getAttribute("value"));
    }

    @Test
    @Order(4)
    @DisplayName("Teste de delete usuário")
    public void deleteUsuario() {
        driver.get("http://localhost:8080/login");

        driver.findElement(By.id("email")).sendKeys("lucas@gmail.com");
        driver.findElement(By.id("senha")).sendKeys("@Lucas123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        try {
            driver.findElement(By.linkText("deletar minha conta")).click();
        } catch (Exception e) {
            driver.findElement(By.linkText("delete your account")).click();
        }

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/cadaster") ||
                        driver.getCurrentUrl().contains("/login")
        );
    }
}

package Stream;

import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.Message;
import com.mailosaur.models.MessageSearchParams;
import com.mailosaur.models.SearchCriteria;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrowOTPTest {

    String apiKey = "your api key";
    String serverId = "your server id";
    String serverDomain = "mailosaur.net";
    String from = "noreply@groww.in";

    public String getRandomEmail()
    {
        return "user" + System.currentTimeMillis() + "@" +serverDomain;
    }
    public Message waitForEmail(String emailId, MailosaurClient mailosaur) throws MailosaurException {
        Wait<MailosaurClient> wait = new FluentWait<>(mailosaur)
                .withTimeout(Duration.ofSeconds(30)) // Maximum wait 30 seconds
                .pollingEvery(Duration.ofMillis(100)) // Check every 500 milliseconds
                .ignoring(Exception.class); // Ignore Mailosaur exceptions

        return wait.until(mailosaurClient -> {
            try {
                // Search for email
                MessageSearchParams params = new MessageSearchParams();
                params.withServer(serverId);

                SearchCriteria criteria = new SearchCriteria();
                criteria.withSentTo(emailId);
                criteria.withSentFrom(from);

                Message message = mailosaurClient.messages().get(params, criteria);
                return message;
            } catch (MailosaurException | IOException e) {
                // Return null if email not found
                return null;
            }
        });
    }
    @Test
    public  void testEmailExample () throws MailosaurException, IOException {
        String emailID = getRandomEmail();

        WebDriver driver = new ChromeDriver();
        driver.get("https://groww.in/login");
        driver.findElement(By.id("login_email1")).sendKeys(emailID);
        driver.findElement(By.xpath("//span[text()='Continue']")).click();


        MailosaurClient mailosaur = new MailosaurClient(apiKey);
        Message message = waitForEmail(emailID,mailosaur);

          /*  if applying
        MessageSearchParams params = new MessageSearchParams();
        params.withServer(serverId);

        SearchCriteria criteria = new SearchCriteria();
        criteria.withSentTo(emailID);
        criteria.withSentFrom(from); */

        // Set receivedAfter to a time before your test starts
//        LocalDateTime receivedAfter = LocalDateTime.now().minusHours(2); // Example: 2 hours ago
//        ZonedDateTime zdt = receivedAfter.atZone(ZoneId.systemDefault());
//        Date date = Date.from(zdt.toInstant());

  //      Message message = mailosaur.messages().get(params, criteria);
        String subject = message.subject();
        System.out.println(subject);

        // regex code
        System.out.println("--regex otp----");
        System.out.println(message.text().body()); // "Your access code is 243546."

        Pattern pattern = Pattern.compile("Your email verification OTP is .*([0-9]{6}).*");
        Matcher matcher = pattern.matcher(subject);
        matcher.find();

        String otp = matcher.group(1);

        System.out.println(otp); // "243546"
        driver.findElement(By.xpath("//input[@id='signup_otp1']")).sendKeys(otp);
        driver.quit();
    }
}

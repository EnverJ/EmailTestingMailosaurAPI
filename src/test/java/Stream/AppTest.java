package Stream;

import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class AppTest {

    @Test
    public  void testExample() throws MailosaurException, IOException {
        String apiKey = "your api key";
        String serverId = "your serverid";
        String serverDomain = "er95ezyy.mailosaur.net";

        MailosaurClient mailosaur = new MailosaurClient(apiKey);

        MessageSearchParams params = new MessageSearchParams();
        params.withServer(serverId);

        SearchCriteria criteria = new SearchCriteria();
        criteria.withSentTo("anything@" + serverDomain);

        // Set receivedAfter to a time before your test starts
//        LocalDateTime receivedAfter = LocalDateTime.now().minusHours(2); // Example: 2 hours ago
//        ZonedDateTime zdt = receivedAfter.atZone(ZoneId.systemDefault());
//        Date date = Date.from(zdt.toInstant());

        Message message = mailosaur.messages().get(params, criteria);
        System.out.println(message.subject());
        System.out.println(message.cc());
        System.out.println(message.to().get(0).email());
        System.out.println(message.from().get(0).email());
        System.out.println(message.bcc());

        // body
        System.out.println("-------body-----");
        System.out.println(message.text().body());
        System.out.println(message.text().body().contains("email testing"));

        // links
        System.out.println("-------checking links------");
        System.out.println(message.html().links().size());

        Link firstLink = message.html().links().get(2);
        System.out.println(firstLink.text());
        System.out.println(firstLink.href());


        System.out.println("---All Links-----");
        List<Link> allLinks = message.html().links();
        for(Link e: allLinks)
        {
            String text = e.text();
            System.out.println(text);

            String href = e.href();
            System.out.println(href);
        }
        // attachment

        System.out.println("----attachment information----");

        System.out.println(message.attachments().size());

        Attachment firstAttachment = message.attachments().get(0);
        System.out.println(firstAttachment.fileName());  // java-learning-map.pdf
        System.out.println(firstAttachment.contentType()); // application/pdf

        // attachment file size
        Attachment firstAttachment2 = message.attachments().get(0);
        System.out.println(firstAttachment2.length());

        // save attachment
        Attachment firstAttachment3 = message.attachments().get(0);
        byte[] file = mailosaur.files().getAttachment(firstAttachment3.id());
        Files.write(Paths.get(firstAttachment3.fileName()), file);

        // images
        System.out.println("-----images-------");
        System.out.println(message.html().images().size()); // 2

        // How many codes?
        System.out.println("-----code------");
        System.out.println(message.text().codes().size()); // 2

        Code firstCode = message.text().codes().get(0);
        System.out.println(firstCode.value());


        // regex code
        System.out.println("--regex otp----");
        System.out.println(message.text().body()); // "Your access code is 243546."

        Pattern pattern = Pattern.compile(".*([0-9]{6}).*");
        Matcher matcher = pattern.matcher(message.text().body());
        matcher.find();

        System.out.println(matcher.group(1)); // "243546"






        assertNotNull(message);
        assertEquals("Fwd: checking attachment", message.subject());

    }
}

package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.model.RegisteredUser;
import server.model.RegistrationResponse;
import server.model.SendMoneyResponse;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = Controller.URI)
@PropertySource("classpath:application.properties")
public class Controller {

    public static final String URI = "/jserra";

    @Autowired
    private SimpMessagingTemplate stompTemplate;

    @Value("${startingBalance:100.00}")
    private String defaultBalance;

    private List<RegisteredUser> registeredUsers = new ArrayList<RegisteredUser>();

    @ResponseBody
    @RequestMapping(method = POST,
            value = "/register",
            consumes = APPLICATION_FORM_URLENCODED_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public RegistrationResponse register(@RequestParam(value = "username") final String username,
                                       HttpServletRequest request) throws Exception {
        System.out.println("received REGISTER request:");
        System.out.println("    username = " + username);

        Boolean isNewUser = false;
        RegisteredUser userMatch = findUserByName(username);
        if (userMatch == null) {
            RegisteredUser newUser = new RegisteredUser();
            newUser.setUsername(username);
            newUser.setBalance(new BigDecimal(defaultBalance));
            registeredUsers.add(newUser);
            isNewUser = true;
            userMatch = newUser;
        }

        RegistrationResponse registrationResponse = new RegistrationResponse();
        registrationResponse.setUsername(userMatch.getUsername());
        registrationResponse.setBalance(userMatch.getBalance());

        if (isNewUser) {
            String destination = "/topic/registrations";
            stompTemplate.convertAndSend(destination, registrationResponse);
        }

        postToRabbit(registrationResponse);
        return registrationResponse;
    }

    @ResponseBody
    @RequestMapping(method = POST,
            value = "/sendmoney",
            consumes = APPLICATION_FORM_URLENCODED_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public SendMoneyResponse sendmoney(@RequestParam(value = "sender") final String sender,
                                       @RequestParam(value = "recipient") final String recipient,
                                       @RequestParam(value = "amount") final String amount,
                                       @RequestParam(value = "message") final String message,
                                       HttpServletRequest request) throws Exception {
        System.out.println("received SENDMONEY request:");
        System.out.println("    sender = " + sender);
        System.out.println("    recipient = " + recipient);
        System.out.println("    amount = " + amount);
        System.out.println("    message = " + message);

        RegisteredUser registeredUserSender = findUserByName(sender);
        RegisteredUser registeredUserRecipient = findUserByName(recipient);
        if ((registeredUserSender != null) && (registeredUserRecipient != null)) {
            registeredUserSender.setBalance(registeredUserSender.getBalance().subtract(new BigDecimal(amount)));
            registeredUserRecipient.setBalance(registeredUserRecipient.getBalance().add(new BigDecimal(amount)));
        }

        // TODO: add a status code and/or message to the response object
        SendMoneyResponse sendMoneyResponse = new SendMoneyResponse();
        sendMoneyResponse.setSender(sender);
        sendMoneyResponse.setRecipient(recipient);
        sendMoneyResponse.setAmount(amount);
        sendMoneyResponse.setMessage(message);

        String destination = "/topic/receipts";
        stompTemplate.convertAndSend(destination, sendMoneyResponse);

        postToRabbit(sendMoneyResponse);
        return sendMoneyResponse;
    }

    private RegisteredUser findUserByName(String username) {
        RegisteredUser userMatch = null;
        for (RegisteredUser user : registeredUsers) {
            if (user.getUsername().equals(username)) {
                userMatch = user;
                break;
            }
        }
        return userMatch;
    }

    private void postToRabbit(Object object) {
        ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        AmqpTemplate template = context.getBean(AmqpTemplate.class);

        String jsonRepresentation = convertObjectToJSON(object);
        template.convertAndSend(RabbitConfiguration.AMQP_EXCHANGE_NAME, null, jsonRepresentation);
    }

    private String convertObjectToJSON(Object object) {
        String json = null;
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String jsonContent = ow.writeValueAsString(object);
            json = "{ \"type\" : \"" + object.getClass().getSimpleName() + "\",\n \"content\" : " + jsonContent + " }";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}

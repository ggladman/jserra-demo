package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.service.BalanceService;
import server.service.UserRegistryService;
import server.model.RegisteredUser;
import server.model.RegistrationResponse;
import server.model.SendMoneyResponse;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = Controller.URI)
@PropertySource("classpath:application.properties")
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    static final String URI = "/jserra";

    private static final Integer MESSAGE_QUEUE_SIZE = 40;

    @Autowired
    private SimpMessagingTemplate stompTemplate;

    @Autowired
    private UserRegistryService userRegistryService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private final ArrayBlockingQueue<SendMoneyResponse> messageHistoryQueue = new ArrayBlockingQueue<SendMoneyResponse>(MESSAGE_QUEUE_SIZE);

    @RequestMapping(value = "/messageHistory", method = GET)
    public List<SendMoneyResponse> getMessageHistory(@SuppressWarnings("unused") final HttpServletRequest request) {
        return new ArrayList<SendMoneyResponse>(messageHistoryQueue);
    }

    @RequestMapping(value = "/userList", method = GET)
    public List<RegisteredUser> getUserList(@SuppressWarnings("unused") final HttpServletRequest request) {
        return userRegistryService.getRegisteredUsers();
    }

    @ResponseBody
    @RequestMapping(method = POST,
                    value = "/register",
                    consumes = APPLICATION_FORM_URLENCODED_VALUE,
                    produces = APPLICATION_JSON_VALUE)
    public RegistrationResponse register(@RequestParam(value = "username") final String username,
                                         @SuppressWarnings("unused") final HttpServletRequest request) {
        System.out.println("received REGISTER request:");
        System.out.println("    username = " + username);

        RegisteredUser userMatch = userRegistryService.findOrAddUser(username);
        System.out.println("usermatch " + userMatch);

        if (userMatch != null && userMatch.isNewlyRegistered()) {
            System.out.println("new user " + username);
        }

        List<RegisteredUser> registeredUsers = userRegistryService.getRegisteredUsers();
        List<Integer> balances = new ArrayList<>();
        for (RegisteredUser registeredUser : registeredUsers) {
            balances.add(registeredUser.getBalance().intValue());
        }
        int averageBalance = balanceService.average(balances);
        System.out.println("average balance = " + averageBalance);

        final RegistrationResponse registrationResponse = new RegistrationResponse();

        if (userMatch != null) {
            registrationResponse.setUsername(userMatch.getUsername());
            registrationResponse.setBalance(userMatch.getBalance());
            registrationResponse.setAverageBalance(new BigDecimal(averageBalance));
            registrationResponse.setRegisteredUsers(registeredUsers);

            if (userMatch.isNewlyRegistered()) {
                final String destination = "/topic/registrations";
                stompTemplate.convertAndSend(destination, registrationResponse);
            }

            postToRabbit(registrationResponse);
        }

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
                                       @SuppressWarnings("unused") final HttpServletRequest request) {
        System.out.println("received SENDMONEY request:");
        System.out.println("    sender = " + sender);
        System.out.println("    recipient = " + recipient);
        System.out.println("    amount = " + amount);
        System.out.println("    message = " + message);

        userRegistryService.transferBalance(sender, recipient, new BigDecimal(amount));

        // TODO: add a status code and/or message to the response object
        final SendMoneyResponse sendMoneyResponse = new SendMoneyResponse();
        sendMoneyResponse.setSender(sender);
        sendMoneyResponse.setRecipient(recipient);
        sendMoneyResponse.setAmount(amount);
        sendMoneyResponse.setMessage(message);

        if (messageHistoryQueue.size() == MESSAGE_QUEUE_SIZE) {
            messageHistoryQueue.remove();
        }
        messageHistoryQueue.add(sendMoneyResponse);

        final String destination = "/topic/receipts";
        stompTemplate.convertAndSend(destination, sendMoneyResponse);

        postToRabbit(sendMoneyResponse);
        return sendMoneyResponse;
    }

    @RequestMapping(value = "/isBalanced", method = GET)
    public boolean isBalanced(@SuppressWarnings("unused") final HttpServletRequest request) {
        List<RegisteredUser> registeredUsers = userRegistryService.getRegisteredUsers();
        List<Integer> balances = new ArrayList<>();
        for (RegisteredUser registeredUser : registeredUsers) {
            balances.add(registeredUser.getBalance().intValue());
        }
        return balanceService.isEvenlyBalanced(balances);
    }


    private void postToRabbit(final Object object) {
        final String jsonRepresentation = convertObjectToJSON(object);
        amqpTemplate.convertAndSend(RabbitConfiguration.AMQP_EXCHANGE_NAME, null, jsonRepresentation);
    }

    private String convertObjectToJSON(final Object object) {
        String json = null;
        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            final String jsonContent = ow.writeValueAsString(object);
            json = "{ \"type\" : \"" + object.getClass().getSimpleName() + "\",\n \"content\" : " + jsonContent + " }";
        } catch (final JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", object.getClass().getSimpleName(), e);
        }
        return json;
    }

}

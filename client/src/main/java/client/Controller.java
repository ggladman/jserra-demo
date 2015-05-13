package client;

import client.model.HttpResponseData;
import client.model.RegistrationResponse;
import client.model.SendMoneyRequest;
import client.model.SendMoneyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = Controller.URI)
@PropertySource("classpath:application.properties")
public class Controller implements MessageListener {

    private static final Configurator configurator = new Configurator();

    public static final String URI = "/jserra";

    @Value("${rabbitHost:localhost}")
    private String amqpHostName;

    @Autowired
    private SimpMessagingTemplate stompTemplate;

    @Value("${teamName:TeamRandom}")
    private String teamName;

    @Value("${serverURI:http://localhost:9090/jserra}")
    private String baseURI;

    @Value("${rabbitUserName:xoom}")
    private String rabbitUserName;

    @Value("${rabbitUserPassword:xoom123}")
    private String rabbitUserPassword;

    @PostConstruct
    private void PostConstruction() {
        try {
            setupRabbitListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@RequestMapping(value = "/messageHistory", method = GET)
    public RegistrationResponse register() throws Exception {

    }*/

        @RequestMapping(value = "/register", method = GET)
    public RegistrationResponse register() throws Exception {
        System.out.println("received REGISTER request from webapp.");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("username", getSender()));

        HttpResponseData responseData = postToServer(baseURI + "/register", nameValuePairs);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseData.getResultBody(), RegistrationResponse.class);
    }

    @ResponseBody
    @RequestMapping(method = POST,
            value = "/sendmoney",
            consumes = APPLICATION_FORM_URLENCODED_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public SendMoneyResponse sendmoney(@RequestParam(value = "recipient") final String recipient,
                                       @RequestParam(value = "amount") final String amount,
                                       @RequestParam(value = "message") final String message,
                                       HttpServletRequest request) throws Exception {
        System.out.println("received SENDMONEY request from webapp:");
        System.out.println("    recipient = " + recipient);
        System.out.println("    amount = " + amount);
        System.out.println("    message = " + message);


        Number amountNumber = new BigDecimal(amount);
        SendMoneyRequest sendMoneyRequest = configurator.buildSendMoneyRequest(recipient, amountNumber, message);

        // Round to two decimal places.
        double amountRounded = Math.round(sendMoneyRequest.getAmount().doubleValue() * 100.0) / 100.0;

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("sender", getSender()));
        nameValuePairs.add(new BasicNameValuePair("recipient", sendMoneyRequest.getRecipient()));
        nameValuePairs.add(new BasicNameValuePair("amount", Double.toString(amountRounded)));
        nameValuePairs.add(new BasicNameValuePair("message", sendMoneyRequest.getMessage()));

        HttpResponseData responseData = postToServer(baseURI + "/sendmoney", nameValuePairs);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseData.getResultBody(), SendMoneyResponse.class);
    }

    @Override
    public void onMessage(Message message) {
        JSONParser parser = new JSONParser();
        String messageContent = new String(message.getBody());
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(messageContent);
            processRabbitMessage(jsonObject);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSender() {
        String sender = configurator.getTeamName();

        // Use teamName property if kids return null.
        if (sender == null) {
            sender = teamName;
        }

        return sender;
    }

    private void processRabbitMessage(JSONObject jsonObject) throws IOException {
        System.out.println("Rabbit message received: " + jsonObject);

        String messageType = jsonObject.get("type").toString();
        String content = jsonObject.get("content").toString();

        if (messageType.equals("RegistrationResponse")) {
            ObjectMapper mapper = new ObjectMapper();
            RegistrationResponse registrationResponse = mapper.readValue(content, RegistrationResponse.class);
            handleRegistrationNotification(registrationResponse);
        }
        else if (messageType.equals("SendMoneyResponse")) {
            ObjectMapper mapper = new ObjectMapper();
            SendMoneyResponse sendMoneyResponse = mapper.readValue(content, SendMoneyResponse.class);
            handleSendMoneyNotification(sendMoneyResponse);
        }
    }

    private void handleRegistrationNotification(RegistrationResponse registrationResponse) {
        System.out.println("handleRegistrationNotification");
        System.out.println("    username : " + registrationResponse.getUsername());
        System.out.println("    balance  : " + registrationResponse.getBalance());

        String destination = "/topic/registrations";
        stompTemplate.convertAndSend(destination, registrationResponse);
    }

    private void handleSendMoneyNotification(SendMoneyResponse sendMoneyResponse) {
        System.out.println("handleSendMoneyNotification");
        System.out.println("    sender    : " + sendMoneyResponse.getSender());
        System.out.println("    recipient : " + sendMoneyResponse.getRecipient());
        System.out.println("    amount    : " + sendMoneyResponse.getAmount());
        System.out.println("    message   : " + sendMoneyResponse.getMessage());

        String destination = "/topic/receipts";
        stompTemplate.convertAndSend(destination, sendMoneyResponse);
    }

    private void setupRabbitListener() throws IOException {
        CachingConnectionFactory cf = new CachingConnectionFactory(amqpHostName);
        cf.setUsername(rabbitUserName);
        cf.setPassword(rabbitUserPassword);
        Connection connection = cf.createConnection();
        Channel channel = connection.createChannel(true);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "jserra", "");

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(cf);
        container.setQueueNames(queueName);
        container.setMessageListener(this);
        container.start();
    }

    private HttpResponseData postToServer(String uri, List<NameValuePair> paramList) {
        HttpResponseData responseData = new HttpResponseData();

        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpPost postRequest = new HttpPost(uri);
        try {
            postRequest.setEntity(new UrlEncodedFormEntity(paramList));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder responseBody = new StringBuilder();

        try {
            HttpResponse response = httpClient.execute(postRequest);
            responseData.setResultCode(response.getStatusLine().getStatusCode());
            BufferedReader responseBodyReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = responseBodyReader.readLine()) != null) {
                responseBody.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        responseData.setResultBody(responseBody.toString());

        return responseData;
    }
}

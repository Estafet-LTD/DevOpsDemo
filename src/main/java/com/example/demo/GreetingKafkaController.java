package com.example.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingKafkaController {
	
	private static final Logger logger =
            LoggerFactory.getLogger(GreetingKafkaController.class);
 
    private final KafkaTemplate<String, Object> template;
    private final String topicName;
    private final AtomicLong counter = new AtomicLong();
    private static String messageTemplate = "Hiya, %s!";
 
    public GreetingKafkaController(
            final KafkaTemplate<String, Object> template,
            @Value("${tpd.topic-name}") final String topicName,
            @Value("${tpd.messages-per-request}") final int messagesPerRequest) {
        this.template = template;
        this.topicName = topicName;
        
    }
 
    @GetMapping("/greetings")
    public String getGreetings() {
    	// how to retrieve the messages?
       logger.info("Messages received");
       return "Hello from Kafka!";
    }
    
    @PostMapping("/greetings")
    public String postGreetings(@RequestParam(value="name", defaultValue="World") String name) {
    	Greeting greeting = new Greeting(counter.incrementAndGet(),
                String.format(messageTemplate, name));
         this.template.send(topicName, greeting);
        logger.info("Message sent: " + greeting);
        return "Message sent: " + greeting;
    }
}



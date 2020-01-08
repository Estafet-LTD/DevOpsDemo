package com.example.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingKafkaController {
	
	private static final Logger logger =
            LoggerFactory.getLogger(GreetingKafkaController.class);
 
    private final KafkaTemplate<String, Object> template;
    private final String topicName;
    private final int messagesPerRequest;
    private final AtomicLong counter = new AtomicLong();
    private static String messageTemplate = "Hello, %s!";
 
    public GreetingKafkaController(
            final KafkaTemplate<String, Object> template,
            @Value("${tpd.topic-name}") final String topicName,
            @Value("${tpd.messages-per-request}") final int messagesPerRequest) {
        this.template = template;
        this.topicName = topicName;
        this.messagesPerRequest = messagesPerRequest;
        
    }
 
 //   @GetMapping("/greeting")
  //  public String getGreetings() throws Exception {
       //latch = new CountDownLatch(messagesPerRequest);
       // IntStream.range(0, messagesPerRequest)
       //         .forEach(i -> this.template.send(topicName, String.valueOf(i),
       //                 new Greeting(i, "Greeting"))
       //         );
       // latch.await(60, TimeUnit.SECONDS);
       // logger.info("All messages received");
    //    return "Hello from Kafka!";
  //  }
    
    @PostMapping("/greeting")
    public String postGreetings(@RequestParam(value="name", defaultValue="World") String name) throws Exception {
    	Greeting greeting = new Greeting(counter.incrementAndGet(),
                String.format(messageTemplate, name));
         this.template.send(topicName, greeting);
       // latch.await(60, TimeUnit.SECONDS);
        logger.info("Message sent: " + greeting);
        return "Message sent: " + greeting;
    }
}



package com.cei;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfiguration {
		
	@Value("${spring.activemq.broker-url}")
	private String brokerUrl;
	
	@Value("${spring.activemq.user}")
	private String user;
	
	@Value("${spring.activemq.password}")
	private String password;
	
	
	@Bean
	public ObjectMapper objectMapper(){
		return new ObjectMapper();
	}


	@Bean
	ConnectionFactory connectionFactory() {
	    
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, brokerUrl);
		factory.setClientID("order-service");
	    
        CachingConnectionFactory cachingConnectionFactory =
                new CachingConnectionFactory(factory);
        cachingConnectionFactory.setSessionCacheSize(10);
	    
	    return cachingConnectionFactory;
	    
	}
	
//	===== FOR SENDING MESSAGES =====
	
	
    @Bean
    public JmsTemplate queueJmsTemplate(ConnectionFactory connectionFactory) {
        
    	JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setPubSubDomain(false); // false = Queue
        return template;
        
    }
    
    
    @Bean
    public JmsTemplate topicJmsTemplate(ConnectionFactory connectionFactory) {
        
    	JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setPubSubDomain(true); // true = Topic
        return template;
        
    }
	
    
// ===== FOR RECEIVING MESSAGES =====

    @Bean
    public DefaultJmsListenerContainerFactory queueListenerContainerFactory(ConnectionFactory connectionFactory) {
    	
    	DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    	factory.setPubSubDomain(false); // false = Queue
    	factory.setConnectionFactory(connectionFactory);
    	
		factory.setConcurrency("1-10");
		factory.setSessionTransacted(false); // Disable transactions for simpler debugging
		
		factory.setErrorHandler(t -> {
			System.err.println("=== JMS QUEUE ERROR HANDLER ===");
			System.err.println("Exception type: " + t.getClass().getName());
			System.err.println("Exception message: " + t.getMessage());
			t.printStackTrace();
		});
		
		return factory;
    }
  
    @Bean
    public DefaultJmsListenerContainerFactory topicListenerContainerFactory(ConnectionFactory connectionFactory) {
		
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		
		factory.setConnectionFactory(connectionFactory);
		factory.setPubSubDomain(true); // true = Topic
		
		factory.setSubscriptionDurable(true); // Enable durable subscription for topics
		
		factory.setErrorHandler(t -> {
			System.err.println("=== JMS TOPIC ERROR HANDLER ===");
			System.err.println("Exception type: " + t.getClass().getName());
			System.err.println("Exception message: " + t.getMessage());
			t.printStackTrace();
		});
		
		return factory;
    }
}
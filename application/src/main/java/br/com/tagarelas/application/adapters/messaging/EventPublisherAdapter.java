package br.com.tagarelas.application.adapters.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import br.com.tagarelas.services.ports.EventPublisher;

@Component 
public class EventPublisherAdapter implements EventPublisher{

    private final KafkaTemplate<String,Object> kafkaTemplate;

    

    public EventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void publish(String topic, Object event) {
        kafkaTemplate.send(topic,event);
    }

}

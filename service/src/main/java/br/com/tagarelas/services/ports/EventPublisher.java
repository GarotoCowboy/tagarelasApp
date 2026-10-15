package br.com.tagarelas.services.ports;

public interface EventPublisher {
void publish(String topic, Object event);
}

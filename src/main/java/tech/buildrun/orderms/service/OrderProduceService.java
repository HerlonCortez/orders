package tech.buildrun.orderms.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tech.buildrun.orderms.config.RabbitMqConfig;
import tech.buildrun.orderms.listener.dto.OrderCreatedEvent;

@Service
public class OrderProduceService {
    private final RabbitTemplate rabbitTemplate;

    public OrderProduceService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.ORDER_CREATED_QUEUE,
                event
        );
    }
}

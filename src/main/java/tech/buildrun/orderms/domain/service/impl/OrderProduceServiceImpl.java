package tech.buildrun.orderms.domain.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tech.buildrun.orderms.config.RabbitMqConfig;
import tech.buildrun.orderms.domain.service.OrderProduceService;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderCreatedEvent;

@Service
public class OrderProduceServiceImpl implements OrderProduceService {
    private final RabbitTemplate rabbitTemplate;

    public OrderProduceServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.ORDER_CREATED_QUEUE,
                event
        );
    }
}

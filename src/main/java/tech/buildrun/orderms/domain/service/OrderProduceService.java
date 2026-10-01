package tech.buildrun.orderms.domain.service;

import org.springframework.stereotype.Service;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderCreatedEvent;

@Service
public interface OrderProduceService {
    void publish(OrderCreatedEvent event);
}

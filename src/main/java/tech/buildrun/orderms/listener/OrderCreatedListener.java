package tech.buildrun.orderms.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import tech.buildrun.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.orderms.service.OrderService;

import static tech.buildrun.orderms.config.RabbitMqConfig.ORDER_CREATED_QUEUE;

@Component
public class OrderCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);
    private final OrderService orderService;

    public OrderCreatedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void listen(Message<OrderCreatedEvent> message) {
        try {
            log.info("Received OrderCreatedEvent {}", message);

            OrderCreatedEvent event = message.getPayload();

            log.info("Event: {}", event);
            log.info("OrderId: {}", event.codigoPedido());
            log.info("CustomerId: {}", event.codigoCliente());
            log.info("Itens: {}", event.itens());

          orderService.save(event);

        } catch (Exception e) {
            log.error("ERRO AO PROCESSAR OrderCreatedEvent", e.getMessage());
            throw e;
        }
    }
}

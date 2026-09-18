package tech.buildrun.orderms.listener.dto;

import tech.buildrun.orderms.entity.OrderItem;

import java.util.List;

public record OrderCreatedEvent(
        Long codigoPedido,
        Long codigoCliente,
        List<OrderItemEvent> itens
) {
}

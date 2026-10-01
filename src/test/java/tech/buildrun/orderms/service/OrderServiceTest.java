package tech.buildrun.orderms.service;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.domain.entity.OrderEntity;
import tech.buildrun.orderms.domain.entity.OrderItem;
import tech.buildrun.orderms.domain.service.impl.OrderServiceImpl;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderCreatedEvent;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderItemEvent;
import tech.buildrun.orderms.infrastructure.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    OrderEntity orderEntity = new OrderEntity();
    OrderCreatedEvent  orderCreatedEvent;
    OrderItemEvent  orderItemEvent;

    @BeforeEach
    public void setup() {
        orderItemEvent = new OrderItemEvent("1001", 1, BigDecimal.TEN);
        orderCreatedEvent = new OrderCreatedEvent(1L, 1L, List.of(orderItemEvent));
        orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderCreatedEvent.codigoPedido());
        orderEntity.setCustomerId(orderCreatedEvent.codigoCliente());
        orderEntity.setItens(List.of(new OrderItem("1001", 1, BigDecimal.TEN)));
    }
    @Test
    void deveraSalvarUmEventoComSucesso() {

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        orderService.save(orderCreatedEvent);

        var captor = ArgumentCaptor.forClass(OrderEntity.class);

        verify(orderRepository).save(captor.capture());

        var saveOrder = captor.getValue();

        assertEquals(1L, saveOrder.getOrderId());
        assertEquals(1L, saveOrder.getCustomerId());
        assertEquals(List.of(orderEntity.getItens().get(0).getProduct()), List.of(saveOrder.getItens().get(0).getProduct()));
        assertEquals(BigDecimal.TEN, saveOrder.getTotal());


    }

    @Test
    void deveListarTotalOrdersPorCustomer(){
        var document = new Document();
        document.put("total",1L);

        var aggregationResults = new AggregationResults<>(
                List.of(document), new Document()
        );

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("tb_orders"), eq(Document.class))).thenReturn(aggregationResults);

        var result = orderService.findTotalOnOrdersByCutomerId(1L);

        assertEquals(new BigDecimal(1L), result);
    }

    @Test
    void deveraRetornarTodosOsClientes(){
        var response = OrderResponse.fromEntity(orderEntity);
        PageRequest pageRequest = PageRequest.of(0, 10);
        var orders = List.of(orderEntity);
        Page<OrderEntity> page = new PageImpl<>(orders, pageRequest, orders.size());
        Page<OrderResponse> responseOrder = new PageImpl<>(List.of(response), pageRequest, orders.size());

        when(orderRepository.findAllByCustomerId(1L, pageRequest)).thenReturn(page);

        var result = orderService.findAllCustomerId(1L, pageRequest);

        assertEquals(responseOrder, result);

        verify(orderRepository).findAllByCustomerId(1L, pageRequest);

    }
}

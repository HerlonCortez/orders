package tech.buildrun.orderms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.buildrun.orderms.controller.dto.ApiResponse;
import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.controller.impl.OrderControllerImpl;
import tech.buildrun.orderms.domain.entity.OrderEntity;
import tech.buildrun.orderms.domain.entity.OrderItem;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderCreatedEvent;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderItemEvent;
import tech.buildrun.orderms.domain.service.impl.OrderProduceServiceImpl;
import tech.buildrun.orderms.domain.service.impl.OrderServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerImplTest {
    @InjectMocks
    private OrderControllerImpl controller;

    @Mock
    private OrderServiceImpl orderService;

    @Mock
    private OrderProduceServiceImpl orderProduceService;

    private OrderEntity orderEntity = new OrderEntity();
    private OrderCreatedEvent  orderCreatedEvent;
    private OrderItemEvent  orderItemEvent;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private OrderResponse orderResponse;
    private ApiResponse<OrderResponse> apiResponse;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper.registerModules(new JavaTimeModule());

        orderItemEvent = new OrderItemEvent("1001", 1, BigDecimal.TEN);
        orderCreatedEvent = new OrderCreatedEvent(1L, 1L, List.of(orderItemEvent));
        orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderCreatedEvent.codigoPedido());
        orderEntity.setCustomerId(orderCreatedEvent.codigoCliente());
        orderEntity.setItens(List.of(new OrderItem("1001", 1, BigDecimal.TEN)));
    }

    @Test
    public void deveCriarOrderComSucesso() throws Exception {
       doNothing().when(orderProduceService).publish(orderCreatedEvent);
       mockMvc.perform(
               post("/customers")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(orderCreatedEvent)))
               .andExpect(status().isAccepted());

                verify(orderProduceService, times(1)).publish(orderCreatedEvent);
    }

    @Test
    public void develistarOrdersComSucesso() throws Exception {
        var pageRequest = PageRequest.of(0, 10);
        var orderResponse = OrderResponse.fromEntity(orderEntity);
        var page = new PageImpl<>(
                List.of(orderResponse),
                pageRequest,
                1
        );

        when(orderService.findAllCustomerId(1L, pageRequest)).thenReturn(page);
        when(orderService.findTotalOnOrdersByCutomerId(1L)).thenReturn(BigDecimal.ONE);


        mockMvc.perform(
                get("/customers/1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalOnOrders").value(1))
                .andExpect(jsonPath("$.data[0].orderId").value(1))
                .andExpect(jsonPath("$.data[0].customerId").value(1));
    }
}

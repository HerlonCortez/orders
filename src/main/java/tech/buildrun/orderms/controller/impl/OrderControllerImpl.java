package tech.buildrun.orderms.controller.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.orderms.controller.OrderController;
import tech.buildrun.orderms.controller.dto.ApiResponse;
import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.controller.dto.PaginationResponse;
import tech.buildrun.orderms.domain.service.OrderProduceService;
import tech.buildrun.orderms.domain.service.OrderService;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderCreatedEvent;

import java.util.Map;

@RestController
@RequestMapping("customers")
public class OrderControllerImpl implements OrderController {
    private final OrderService orderService;
    private final OrderProduceService orderProduceService;

    public OrderControllerImpl(OrderService orderService, OrderProduceService orderProduceService) {
        this.orderService = orderService;
        this.orderProduceService = orderProduceService;
    }

   @Override
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(
            @PathVariable Long customerId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        var pageResponse= orderService.findAllCustomerId(customerId, PageRequest.of(page, pageSize));
        var totalOnOrders = orderService.findTotalOnOrdersByCutomerId(customerId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        Map.of("totalOnOrders", totalOnOrders),
                        pageResponse.getContent(),
                        PaginationResponse.fromPage(pageResponse)
                )
        );
    }

  @Override
    public ResponseEntity<Void> createOrder(@RequestBody OrderCreatedEvent event)  {
        orderProduceService.publish(event);
        return ResponseEntity.accepted().build();
    }
}

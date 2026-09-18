package tech.buildrun.orderms.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.orderms.controller.dto.ApiResponse;
import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.controller.dto.PaginationResponse;
import tech.buildrun.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.orderms.service.OrderProduceService;
import tech.buildrun.orderms.service.OrderService;

@RestController
@RequestMapping("customers")
public class OrderController {
    private final OrderService orderService;
    private final OrderProduceService orderProduceService;

    public OrderController(OrderService orderService, OrderProduceService orderProduceService) {
        this.orderService = orderService;
        this.orderProduceService = orderProduceService;
    }

    @GetMapping("/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(
            @PathVariable Long customerId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        var pageResponse= orderService.findAllCustomerId(customerId, PageRequest.of(page, pageSize));

        return ResponseEntity.ok(
                new ApiResponse<>(
                        pageResponse.getContent(),
                        PaginationResponse.fromPage(pageResponse)
                )
        );
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody OrderCreatedEvent event)  {
        orderProduceService.publish(event);
        return ResponseEntity.accepted().build();
    }
}

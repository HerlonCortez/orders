package tech.buildrun.orderms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.orderms.controller.dto.ApiResponse;
import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderCreatedEvent;

@RestController
@RequestMapping("customers")
public interface OrderController {

    @GetMapping("/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(
            @PathVariable Long customerId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize);

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody OrderCreatedEvent event);
}

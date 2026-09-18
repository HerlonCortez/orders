package tech.buildrun.orderms.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.orderms.controller.dto.ApiResponse;
import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.controller.dto.PaginationResponse;
import tech.buildrun.orderms.service.OrderService;

@RestController
@RequestMapping
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("customers/{customerId}/orders")
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
}

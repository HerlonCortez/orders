package tech.buildrun.orderms.domain.service;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.domain.entity.OrderEntity;
import tech.buildrun.orderms.domain.entity.OrderItem;
import tech.buildrun.orderms.infrastructure.listener.dto.OrderCreatedEvent;
import tech.buildrun.orderms.infrastructure.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public interface OrderService {

    void save(OrderCreatedEvent event);

    BigDecimal findTotalOnOrdersByCutomerId(Long customerId);

    Page<OrderResponse> findAllCustomerId(Long customerId, PageRequest pageRequest);
}

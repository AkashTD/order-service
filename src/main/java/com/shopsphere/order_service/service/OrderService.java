package com.shopsphere.order_service.service;

import com.shopsphere.order_service.dto.OrderLineItemDto;
import com.shopsphere.order_service.dto.OrderRequest;
import com.shopsphere.order_service.entity.Order;
import com.shopsphere.order_service.entity.OrderLineItem;
import com.shopsphere.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public void createProduct(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItemList= orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto).toList();

        order.setOrderLineItemList(orderLineItemList);

        orderRepository.save(order);
    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }
}

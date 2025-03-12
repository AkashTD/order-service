package com.shopsphere.order_service.service;

import com.shopsphere.order_service.dto.InventoryResponse;
import com.shopsphere.order_service.dto.OrderLineItemDto;
import com.shopsphere.order_service.dto.OrderRequest;
import com.shopsphere.order_service.entity.Order;
import com.shopsphere.order_service.entity.OrderLineItem;
import com.shopsphere.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient webClient;

    public void createProduct(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItemList= orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto).toList();

        order.setOrderLineItemList(orderLineItemList);

        List<String> skuCodeList = order.getOrderLineItemList().stream().
                map(OrderLineItem::getSkuCode).toList();

        //before creating order check if product exist in inventory
        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8082/inventory", uriBuilder -> uriBuilder.queryParam("skuCodeList", skuCodeList).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean areAllProductAvailable = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        if(areAllProductAvailable) {
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Product not available, please try again after some time");
        }
    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }
}

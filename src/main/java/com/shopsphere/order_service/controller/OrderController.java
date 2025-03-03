package com.shopsphere.order_service.controller;

import com.shopsphere.order_service.dto.OrderRequest;
import com.shopsphere.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderRequest orderRequest){

        orderService.createProduct(orderRequest);
        return "Order created successfully";
    }
}

package com.project.springboot.controller;

import com.project.springboot.entity.OrderInput;
//import com.project.springboot.entity.TransactionDetails;
import com.project.springboot.entity.OrderDetail;
import com.project.springboot.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @PreAuthorize("hasRole('User')")
    @PostMapping({ "/placeOrder/{isSingleProductCheckout}" })
    public void placeOrder(@PathVariable(name = "isSingleProductCheckout") boolean isSingleProductCheckout,
            @RequestBody OrderInput orderInput) {
        orderDetailService.placeOrder(orderInput, isSingleProductCheckout);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping({ "/getOrderDetails/{status}" })
    public List<OrderDetail> getOrderDetails(@PathVariable(name = "status") String status) {
        return orderDetailService.getOrderDetails(status);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping({ "/getAllOrderDetails/{status}" })
    public List<OrderDetail> getAllOrderDetails(@PathVariable(name = "status") String status) {
        return orderDetailService.getAllOrderDetails(status);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping({ "/markOrderAsDelivered/{orderId}" })
    public void markOrderAsDelivered(@PathVariable(name = "orderId") Integer orderId) {
        orderDetailService.markOrderAsDelivered(orderId);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping({ "/markOrderAsCancelled/{orderId}" })
    public void markOrderAsCancelled(@PathVariable(name = "orderId") Integer orderId) {
        orderDetailService.markOrderAsCancelled(orderId);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping({ "/markOrderAsCancelledByUser/{orderId}" })
    public void markOrderAsCancelledByUser(@PathVariable(name = "orderId") Integer orderId) {
        orderDetailService.markOrderAsCancelledByUser(orderId);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping({ "/markOrderAsSuccessful/{orderId}" })
    public void markOrderAsSuccessful(@PathVariable(name = "orderId") Integer orderId) {
        orderDetailService.markOrderAsSuccessful(orderId);
    }

    // @PreAuthorize("hasRole('User')")
    // @GetMapping({ "/createTransaction/{amount}" })
    // public TransactionDetails createTransaction(@PathVariable(name = "amount")
    // Double amount) {
    // return orderDetailService.createTransaction(amount);
    // }
}

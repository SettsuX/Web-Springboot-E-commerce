package com.project.springboot.service;

import com.project.springboot.configuration.JwtRequestFilter;
import com.project.springboot.entity.*;
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
import com.project.springboot.dao.CartDao;
import com.project.springboot.dao.OrderDetailDao;
import com.project.springboot.dao.ProductDao;
import com.project.springboot.dao.UserDao;
//import com.project.springboot.entity.*;
//import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailService {

    private static final String ORDER_PLACED = "Placed";

    // private static final String KEY = "rzp_test_AXBzvN2fkD4ESK";
    // private static final String KEY_SECRET = "bsZmiVD7p1GMo6hAWiy4SHSH";
    // private static final String CURRENCY = "INR";

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CartDao cartDao;

    public List<OrderDetail> getAllOrderDetails(String status) {
        List<OrderDetail> orderDetails = new ArrayList<>();

        if (status.equals("All")) {
            orderDetailDao.findAll().forEach(
                    x -> orderDetails.add(x));
        } else {
            orderDetailDao.findByOrderStatus(status).forEach(
                    x -> orderDetails.add(x));
        }

        return orderDetails;
    }

    public List<OrderDetail> getOrderDetails(String status) {
        String currentUser = JwtRequestFilter.CURRENT_USER;
        User user = userDao.findById(currentUser).get();
        List<OrderDetail> orderDetails = new ArrayList<>();

        if (status.equals("All")) {
            orderDetailDao.findByUser(user);
            // return orderDetailDao.findByOrderStatus(status);
        } else {
            orderDetailDao.findByOrderStatus(status).forEach(
                    x -> orderDetails.add(x));
        }
        return orderDetailDao.findByUser(user);
    }

    public void placeOrder(OrderInput orderInput, boolean isSingleProductCheckout) {
        List<OrderProductQuantity> productQuantityList = orderInput.getOrderProductQuantityList();

        for (OrderProductQuantity o : productQuantityList) {
            Product product = productDao.findById(o.getProductId()).get();

            String currentUser = JwtRequestFilter.CURRENT_USER;
            User user = userDao.findById(currentUser).get();

            OrderDetail orderDetail = new OrderDetail(
                    orderInput.getFullName(),
                    orderInput.getFullAddress(),
                    orderInput.getContactNumber(),
                    orderInput.getAlternateContactNumber(),
                    ORDER_PLACED,
                    product.getProductDiscountedPrice() * o.getQuantity(),
                    o.getQuantity(),
                    product,
                    user
            // orderInput.getTransactionId()
            );

            // empty the cart.
            if (!isSingleProductCheckout) {
                List<Cart> carts = cartDao.findByUser(user);
                carts.stream().forEach(x -> cartDao.deleteById(x.getCartId()));
            }

            product.setProductStock(product.getProductStock() - o.getQuantity());

            productDao.save(product);

            orderDetailDao.save(orderDetail);
        }
    }

    public void markOrderAsDelivered(Integer orderId) {
        OrderDetail orderDetail = orderDetailDao.findById(orderId).get();

        if (orderDetail != null) {
            orderDetail.setOrderStatus("Delivered");
            orderDetailDao.save(orderDetail);
        }

    }

    public void markOrderAsCancelled(Integer orderId) {
        OrderDetail orderDetail = orderDetailDao.findById(orderId).get();

        if (orderDetail != null) {
            orderDetail.setOrderStatus("Cancelled By Admin");
            orderDetailDao.save(orderDetail);
        }

    }

    public void markOrderAsCancelledByUser(Integer orderId) {
        OrderDetail orderDetail = orderDetailDao.findById(orderId).get();

        if (orderDetail != null) {
            orderDetail.setOrderStatus("Cancelled By User");
            orderDetailDao.save(orderDetail);
        }

    }

    public void markOrderAsSuccessful(Integer orderId) {
        OrderDetail orderDetail = orderDetailDao.findById(orderId).get();

        if (orderDetail != null) {
            orderDetail.setOrderStatus("Successful");
            orderDetailDao.save(orderDetail);
        }

    }

    // public TransactionDetails createTransaction(Double amount) {
    // try {

    // JSONObject jsonObject = new JSONObject();
    // jsonObject.put("amount", (amount * 100));
    // jsonObject.put("currency", CURRENCY);

    // RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);

    // Order order = razorpayClient.orders.create(jsonObject);

    // TransactionDetails transactionDetails = prepareTransactionDetails(order);
    // return transactionDetails;
    // } catch (Exception e) {
    // System.out.println(e.getMessage());
    // }
    // return null;
    // }

    // private TransactionDetails prepareTransactionDetails(Order order) {
    // String orderId = order.get("id");
    // String currency = order.get("currency");
    // Integer amount = order.get("amount");

    // TransactionDetails transactionDetails = new TransactionDetails(orderId,
    // currency, amount, KEY);
    // return transactionDetails;
    // }
}

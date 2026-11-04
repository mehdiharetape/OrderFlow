package com.example.order_flow.scheduler;

import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrderExpirationScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(OrderExpirationScheduler.class);

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderExpirationScheduler(OrderService orderService,
                                    OrderRepository orderRepository)
    {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 60000)
    public void expireOrders(){
        List<Integer> expires = orderRepository.findAllExpiredOrder();

        if(expires.isEmpty())
            return;

        for(Integer orderId : expires){
            try {
                orderService.expireOrder(orderId);
            }
            catch (Exception ex){
                log.error("Failed to expire order with id: {}", orderId, ex);
            }
        }
    }
}

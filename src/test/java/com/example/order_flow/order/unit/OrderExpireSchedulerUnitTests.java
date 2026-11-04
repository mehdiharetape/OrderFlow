package com.example.order_flow.order.unit;

import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.scheduler.OrderExpirationScheduler;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderExpireSchedulerUnitTests {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private OrderExpirationScheduler expirationScheduler;


    @Test
    public void orderIdListIsEmptyTest(){
        given(orderRepository.findAllExpiredOrder())
                .willReturn(List.of());

        expirationScheduler.expireOrders();

        verify(orderRepository).findAllExpiredOrder();
        verify(orderService, never()).expireOrder(anyInt());
    }

    @Test
    public void orderIdListIdNotEmptyAndOrdersShouldExpire(){
        given(orderRepository.findAllExpiredOrder())
                .willReturn(List.of(3001, 3002));

        expirationScheduler.expireOrders();

        verify(orderRepository).findAllExpiredOrder();
        verify(orderService).expireOrder(3001);
        verify(orderService).expireOrder(3002);

        //ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        //verify(orderService, times(2))
        //        .expireOrder(captor.capture());
        //assertEquals(1001, captor.getAllValues().get(0));
        //assertEquals(1002, captor.getAllValues().get(1));
    }

    @Test
    public void exceptionOccurredOnSecondExpiration(){
        given(orderRepository.findAllExpiredOrder())
                .willReturn(List.of(3001, 3002, 3003));

        willThrow(new RuntimeException("Simulated Exception"))
                .given(orderService).expireOrder(3002);

        expirationScheduler.expireOrders();

        verify(orderRepository).findAllExpiredOrder();

        InOrder inOrder = inOrder(orderService);
        inOrder.verify(orderService).expireOrder(3001);
        inOrder.verify(orderService).expireOrder(3002);
        inOrder.verify(orderService).expireOrder(3003);

        //ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        //verify(orderService, times(3))
                //.expireOrder(captor.capture());
        //assertEquals(1001, captor.getAllValues().get(0));
        //assertEquals(1002, captor.getAllValues().get(1));
        //assertEquals(1003, captor.getAllValues().get(2));
    }
}

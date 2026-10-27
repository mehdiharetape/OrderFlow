package com.example.order_flow.product.unit;

import com.example.order_flow.dto.product.UpdateProductStatusDto;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductStatusUnitTests {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    public void updateStatusHappyFlow(){
        Integer id = 1001;
        UpdateProductStatusDto statusDto = new UpdateProductStatusDto();
        statusDto.setStatus(ProductStatus.ACTIVE);

        when(productRepository.existsById(id)).
                thenReturn(true);

        productService.updateProductStatus(id, statusDto);

        verify(productRepository).updateProductStatus(id, statusDto.getStatus());
    }

    //product not found
    @Test
    public void productNotFoundTest(){
        Integer id = 1010;
        UpdateProductStatusDto statusDto = new UpdateProductStatusDto();
        statusDto.setStatus(ProductStatus.ACTIVE);

        when(productRepository.existsById(id))
                .thenReturn(false);

       assertThrows(ProductNotFoundException.class,
               () ->  productService.updateProductStatus(id, statusDto));

       verify(productRepository).existsById(id);
       verify(productRepository, never()).
               updateProductStatus(anyInt(), any());
    }

    //invalid id
    @Test
    public void nullProductIdUnitTest(){
        UpdateProductStatusDto statusDto = new UpdateProductStatusDto();
        statusDto.setStatus(ProductStatus.ACTIVE);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateProductStatus(null ,statusDto));

        verify(productRepository, never()).updateProductStatus(anyInt(), any());
    }

    @Test
    public void invalidProductIdUnitTest(){
        Integer productId = 999;
        UpdateProductStatusDto statusDto = new UpdateProductStatusDto();
        statusDto.setStatus(ProductStatus.ACTIVE);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateProductStatus(productId ,statusDto));

        verify(productRepository, never()).updateProductStatus(anyInt(), any());
    }

    //null or empty status
    @Test
    public void nullStatusValueTest(){
        Integer productId = 1001;
        UpdateProductStatusDto statusDto = new UpdateProductStatusDto();

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateProductStatus(productId ,statusDto));

        verify(productRepository, never()).updateProductStatus(anyInt(), any());
    }
}

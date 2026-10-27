package com.example.order_flow.product.unit;

import com.example.order_flow.dto.product.UpdateProductNamePriceDto;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductNamePriceUnitTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    public void updateProductHappyFlow(){
        Integer productId = 1001;
        UpdateProductNamePriceDto dto = new UpdateProductNamePriceDto();
        dto.setName("milk");
        dto.setNewtPrice(14.56);

        when(productRepository.existsById(productId))
                .thenReturn(true);

        productService.updateNamePrice(productId, dto);

        verify(productRepository).existsById(productId);
        verify(productRepository).updateProductNamePrice(productId, dto.getName(),
               BigDecimal.valueOf(dto.getNewPrice()));
    }


    //invalid id
    @Test
    public void nullProductIdUnitTest(){
        UpdateProductNamePriceDto dto = new UpdateProductNamePriceDto();
        dto.setName("milk");
        dto.setNewtPrice(15.65);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateNamePrice(null ,dto));

        verify(productRepository, never()).updateProductNamePrice(anyInt(),
                anyString(), BigDecimal.valueOf(anyDouble()));
    }

    @Test
    public void invalidProductIdUnitTest(){
        Integer productId = 999;
        UpdateProductNamePriceDto dto = new UpdateProductNamePriceDto();
        dto.setName("milk");
        dto.setNewtPrice(15.65);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateNamePrice(productId ,dto));

        verify(productRepository, never()).updateProductNamePrice(anyInt(), anyString(),
                BigDecimal.valueOf(anyDouble()));
    }

    //product exists test
    @Test
    public void productNotFoundUnitTest(){
        Integer productId = 1004;
        UpdateProductNamePriceDto dto = new UpdateProductNamePriceDto();
        dto.setName("milk");
        dto.setNewtPrice(15.65);

        when(productRepository.existsById(productId))
                .thenReturn(false);

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateNamePrice(productId, dto));

        verify(productRepository).existsById(productId);
        verify(productRepository, never()).updateProductNamePrice(anyInt(), anyString(),
                BigDecimal.valueOf(anyDouble()));
    }

    //null or empty name
    @Test
    public void nullProductNameUnitTest(){
        Integer productId = 1001;
        UpdateProductNamePriceDto dto = new UpdateProductNamePriceDto();
        dto.setName(null);
        dto.setNewtPrice(15.65);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateNamePrice(productId ,dto));

        verify(productRepository, never()).updateProductNamePrice(anyInt(), anyString(),
                BigDecimal.valueOf(anyDouble()));
    }

    @Test
    public void emptyProductNameUnitTest(){
        Integer productId = 1001;
        UpdateProductNamePriceDto dto = new UpdateProductNamePriceDto();
        dto.setName("");
        dto.setNewtPrice(15.65);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateNamePrice(productId ,dto));

        verify(productRepository, never()).updateProductNamePrice(anyInt(), anyString(),
                BigDecimal.valueOf(anyDouble()));
    }

    //current price <=0
    @Test
    public void zeroProductPriceUnitTest(){
        Integer productId = 1001;
        UpdateProductNamePriceDto dto = new UpdateProductNamePriceDto();
        dto.setName("milk");
        dto.setNewtPrice(0);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateNamePrice(productId, dto));

        verify(productRepository, never()).updateProductNamePrice(productId, dto.getName(),
                BigDecimal.valueOf(dto.getNewPrice()));
    }

    @Test
    public void negativeProductPriceUnitTest(){
        Integer productId = 1001;
        UpdateProductNamePriceDto dto = new UpdateProductNamePriceDto();
        dto.setName("milk");
        dto.setNewtPrice(-1);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.updateNamePrice(productId, dto));

        verify(productRepository, never()).updateProductNamePrice(productId, dto.getName(),
                BigDecimal.valueOf(dto.getNewPrice()));
    }
}

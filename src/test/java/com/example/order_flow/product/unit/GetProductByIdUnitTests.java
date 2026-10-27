package com.example.order_flow.product.unit;

import com.example.order_flow.dto.product.ProductDetailResponseDto;
import com.example.order_flow.entity.Product;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GetProductByIdUnitTests {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    public void getProductHappyFlow(){
        Integer productId = 1001;

        Product product = new Product(productId);
        product.setName("milk");
        product.setStatus(ProductStatus.INACTIVE);
        product.setCurrentPrice(BigDecimal.valueOf(10.56));

        given(productRepository.findById(productId)).
                willReturn(Optional.of(product));

        ProductDetailResponseDto result =
                productService.getProductById(productId);

        assertEquals(productId, result.getProductId());
        assertEquals("milk", result.getName());
        assertEquals(ProductStatus.INACTIVE.toString() , result.getStatus());
    }

    //null or invalid id
    @Test
    public void nullProductIdUnitTest()
    {
        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.getProductById(null));
    }

    @Test
    public void invalidProductIdUnitTest()
    {
        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.getProductById(999));
    }

    //null product
    @Test
    public void nullProductUnitTest(){
        given(productRepository.findById(1000))
                .willReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById(1000));
    }
}

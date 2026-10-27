package com.example.order_flow.product.unit;

import com.example.order_flow.dto.product.ProductDetailResponseDto;
import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GetAllProductsUnitTests {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    public void getAllProductsHappyFlow(){
        Product product1 = new Product(1001);
        product1.setName("milk");
        product1.setStatus(ProductStatus.INACTIVE);
        product1.setCurrentPrice(BigDecimal.valueOf(10.56));
        Product product2 = new Product(1002);
        product2.setName("tv");
        product2.setStatus(ProductStatus.INACTIVE);
        product2.setCurrentPrice(BigDecimal.valueOf(11.43));

        given(productRepository.findAll())
                .willReturn(List.of(product1, product2));

        List<ProductDetailResponseDto> result =
                productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals(1001, result.get(0).getProductId());
        assertEquals(1002, result.get(1).getProductId());
    }

    //empty list not null
    @Test
    public void emptyProductsListUnitTest(){
        given(productRepository.findAll())
                .willReturn(List.of());

        List<ProductDetailResponseDto> result =
                productService.getAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

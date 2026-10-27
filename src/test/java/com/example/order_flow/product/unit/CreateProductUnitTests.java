package com.example.order_flow.product.unit;

import com.example.order_flow.dto.product.CreateProductDto;
import com.example.order_flow.entity.Product;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateProductUnitTests {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    //create happy flow
    @Test
    public void createProductHappyFlow(){
        CreateProductDto dto = new CreateProductDto();
        dto.setName("milk");
        dto.setCurrentPrice(15.65);

        productService.createProduct(dto);

        verify(productRepository).save(any(Product.class));
    }

    //null or empty name
    @Test
    public void nullProductNameUnitTest(){
        CreateProductDto dto = new CreateProductDto();
        dto.setName(null);
        dto.setCurrentPrice(15.65);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.createProduct(dto));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void emptyProductNameUnitTest(){
        CreateProductDto dto = new CreateProductDto();
        dto.setName("");
        dto.setCurrentPrice(15.65);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.createProduct(dto));

        verify(productRepository, never()).save(any(Product.class));
    }

    //current price <=0
    @Test
    public void zeroProductPriceUnitTest(){
        CreateProductDto dto = new CreateProductDto();
        dto.setName("milk");
        dto.setCurrentPrice(0);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.createProduct(dto));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void negativeProductPriceUnitTest(){
        CreateProductDto dto = new CreateProductDto();
        dto.setName("milk");
        dto.setCurrentPrice(-1);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> productService.createProduct(dto));

        verify(productRepository, never()).save(any(Product.class));
    }
}

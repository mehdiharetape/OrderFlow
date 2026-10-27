package com.example.order_flow.service;

import com.example.order_flow.dto.product.CreateProductDto;
import com.example.order_flow.dto.product.ProductDetailResponseDto;
import com.example.order_flow.dto.product.UpdateProductNamePriceDto;
import com.example.order_flow.dto.product.UpdateProductStatusDto;
import com.example.order_flow.entity.Product;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.ProductExistsException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.mapper.ProductMapper;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.validation.ProductValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    //create product
    public Integer createProduct(CreateProductDto productDto){
        ProductValidation.createProductValidation(productDto);

        if(productRepository.existsByName(productDto.getName()))
            throw new ProductExistsException();

        Product entity = ProductMapper.createDtoToProduct(productDto);

        return productRepository.save(entity).getProductId();
    }

    //get product by id
    public ProductDetailResponseDto getProductById(Integer productId)
    {
        ProductValidation.productIdValidation(productId);

        Product founded = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        return ProductMapper.productToDetailDto(founded);
    }

    //get all products
    public List<ProductDetailResponseDto> getAllProducts(){
        Iterable<Product> products
                = productRepository.findAll();

        List<ProductDetailResponseDto> responseDtoList = new ArrayList<>();

        products.forEach(r ->
                responseDtoList.add(ProductMapper.productToDetailDto(r)));

        return responseDtoList;
    }

    //update name & current price
    public void updateNamePrice(Integer productId, UpdateProductNamePriceDto updateDto){
        ProductValidation.updateNamePriceValidation(updateDto);

        ProductValidation.productIdValidation(productId);

        if(!productRepository.existsById(productId))
            throw new ProductNotFoundException();

        if(productRepository.existsByName(updateDto.getName()))
            throw new ProductExistsException();

        productRepository.updateProductNamePrice(productId, updateDto.getName(),
                BigDecimal.valueOf(updateDto.getNewPrice()));
    }

    //update product status
    public void updateProductStatus(Integer productId, UpdateProductStatusDto updateDto)
    {
        ProductValidation.UpdateStatusValidation(updateDto);

        ProductValidation.productIdValidation(productId);

        if(!productRepository.existsById(productId))
            throw new ProductNotFoundException();

        productRepository.updateProductStatus(productId, updateDto.getStatus());
    }
}

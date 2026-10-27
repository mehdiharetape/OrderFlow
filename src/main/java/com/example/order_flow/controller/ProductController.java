package com.example.order_flow.controller;

import com.example.order_flow.dto.product.CreateProductDto;
import com.example.order_flow.dto.product.ProductDetailResponseDto;
import com.example.order_flow.dto.product.UpdateProductNamePriceDto;
import com.example.order_flow.dto.product.UpdateProductStatusDto;
import com.example.order_flow.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    //
    @PostMapping("/product")
    public ResponseEntity<String> createProduct(
            @RequestBody CreateProductDto productDto)
    {
        Integer id = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Product created successfully with id '" + id + "'");
    }

    //
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductById(
            @PathVariable Integer productId)
    {
        var product = productService.getProductById(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(product);
    }

    //
    @GetMapping("/product")
    public ResponseEntity<List<ProductDetailResponseDto>> getAllProduct()
    {
        var products = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK)
                .body(products);
    }

    //
    @PutMapping("/product/{productId}/update")
    public ResponseEntity<String> updateProductNamePrice(
            @PathVariable Integer productId,
            @RequestBody UpdateProductNamePriceDto dto)
    {
        productService.updateNamePrice(productId, dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Product updated successfully.");
    }

    @PatchMapping("/product/{productId}/update")
    public ResponseEntity<String> updateProductStatus(
            @PathVariable Integer productId,
            @RequestBody UpdateProductStatusDto dto)
    {
        productService.updateProductStatus(productId, dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Product status updated successfully.");
    }
}

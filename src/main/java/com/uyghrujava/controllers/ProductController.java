package com.uyghrujava.controllers;

import com.uyghrujava.dto.EntityResponse;
import com.uyghrujava.models.Product;
import com.uyghrujava.services.product_service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllProducts() {
        return EntityResponse.generateResponse("Retrieve all products", HttpStatus.OK, productService.retrieveAllProducts());
    }

    @PostMapping
    public ResponseEntity<Object> addProduct(@RequestBody Product product) {
        return EntityResponse.generateResponse("Add a product", HttpStatus.OK, productService.addProduct(product));
    }
}

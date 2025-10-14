package org.clientpr.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clientpr.demo.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import ru.t1hwork.starter.aop.annotations.HttpIncomeRequestLog;
import org.clientpr.demo.model.dto.ProductDTO;
import org.clientpr.demo.model.enums.ProductKey;
import org.clientpr.demo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final RoleService roleService;
    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           @RequestHeader("X-User-Id") Long userId) {
        log.info("🎯 Creating product: {}, user ID: {}", productDTO.getName(), userId);
        String userRole = roleService.getUserRole(userId);
        log.info("🔍 User {} role: {}", userId, userRole);
        if (!"MASTER".equals(userRole)) {
            log.warn("🚫 User {} with role {} cannot create products", userId, userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only users with MASTER role can create products");
        }
        log.info("✅ User {} has MASTER role, creating product...", userId);
        try {
            ProductDTO createdProduct = productService.createProduct(productDTO);
            log.info("✅ Product created successfully: {}", createdProduct.getId());
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            log.error("❌ Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating product: " + e.getMessage());
        }
    }
//@PostMapping
//@HttpIncomeRequestLog
//public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
//                                       @RequestHeader("X-User-Id") Long userId) {
//    log.info("🎯 Creating product: {}, user ID: {}", productDTO.getName(), userId);
//    log.info("🔧 TEMPORARY: Skipping role check for user {}", userId);
//
//    log.info("✅ Creating product for user {}...", userId);
//    try {
//        ProductDTO createdProduct = productService.createProduct(productDTO);
//        log.info("✅ Product created successfully: {}", createdProduct.getId());
//        return ResponseEntity.ok(createdProduct);
//    } catch (Exception e) {
//        log.error("❌ Error creating product: {}", e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error creating product: " + e.getMessage());
//    }
//}
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    @GetMapping("/product-id/{productId}")
    public ResponseEntity<ProductDTO> getProductByProductId(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    @GetMapping("/key/{key}")
    public ResponseEntity<List<ProductDTO>> getProductsByKey(@PathVariable ProductKey key) {
        List<ProductDTO> products = productService.getProductsByKey(key);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProductsByName(@RequestParam String name) {
        List<ProductDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @Valid @RequestBody ProductDTO productDTO,
                                           @RequestHeader("X-User-Id") Long userId) {
        String userRole = roleService.getUserRole(userId);
        if (!"MASTER".equals(userRole) && !"GRAND_EMPLOYEE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only users with MASTER or GRAND_EMPLOYEE role can update products");
        }
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id,
                                           @RequestHeader("X-User-Id") Long userId) {
        String userRole = roleService.getUserRole(userId);
        if (!"MASTER".equals(userRole) && !"GRAND_EMPLOYEE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only users with MASTER or GRAND_EMPLOYEE role can delete products");
        }

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

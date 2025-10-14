package org.clientpr.demo.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.t1hwork.starter.aop.annotations.LogDatasourceError;
import org.clientpr.demo.model.Product;
import org.clientpr.demo.model.dto.ProductDTO;
import org.clientpr.demo.model.enums.ProductKey;
import org.clientpr.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    @LogDatasourceError(type = "ERROR")
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("🎯 Starting product creation: {}", productDTO.getName());

        if (productRepository.existsByProductId(productDTO.getProductId())) {
            log.error("❌ Product with productId already exists: {}", productDTO.getProductId());
            throw new IllegalArgumentException("Product with this productId already exists: " + productDTO.getProductId());
        }
        Product product = Product.builder(
                productDTO.getName(),
               productDTO.getKey(),
                LocalDateTime.now()
                ).build();

        Product savedProduct = productRepository.save(product);
        log.info("✅ Product created successfully with ID: {}", savedProduct.getId());
        String generatedProductId = savedProduct.getKey().name() + savedProduct.getId();
        savedProduct.setProductId(generatedProductId);
        Product finalProduct = productRepository.save(savedProduct);
        return convertToDTO(finalProduct);
    }
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }
        public List<ProductDTO> getProductsByKey(ProductKey key) {
        return productRepository.findByKey(key)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        existingProduct.setName(productDTO.getName());
        existingProduct.setKey(productDTO.getKey());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .key(product.getKey())
                .createDate(product.getCreateDate())
                .productId(product.getProductId())
                .build();
    }
}

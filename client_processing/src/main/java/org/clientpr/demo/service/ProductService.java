package org.clientpr.demo.service;


import lombok.RequiredArgsConstructor;
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
public class ProductService {
    private final ProductRepository productRepository;

    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = Product.hiddenBuilder()
                .name(productDTO.getName())
                .key(productDTO.getKey())
                .createDate(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);
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

    public ProductDTO getProductByProductId(String productId) {
        return productRepository.findByProductId(productId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with productId: " + productId));
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

package org.clientpr.demo.service;

import org.clientpr.demo.model.Product;
import org.clientpr.demo.model.dto.ProductDTO;
import org.clientpr.demo.model.enums.ProductKey;
import org.clientpr.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
private ProductRepository productRepository;
    @Mock
    private ProductMetricsService productMetricsService;
    @InjectMocks
    private ProductService productService;

    private ProductDTO validProductDTO;
    private Product validProduct;

    @BeforeEach
    void setUp() {
        validProductDTO = ProductDTO.builder()
                .name("Дебетовая карта Classic")
                .key(ProductKey.DC)
                .productId("DC_TEMP1")
                .build();

        validProduct = Product.builder(
                "Дебетовая карта Classic",
                ProductKey.DC,
                LocalDateTime.now()
        ).build();
        validProduct.setId(1L);
        validProduct.setProductId("DC1");
    }

    @Test
    void createProduct_WithValidData_ShouldCreateProduct() {
        when(productRepository.existsByProductId("DC_TEMP1")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(validProduct);

        ProductDTO result = productService.createProduct(validProductDTO);

        assertNotNull(result);
        assertEquals("Дебетовая карта Classic", result.getName());
        assertEquals(ProductKey.DC, result.getKey());
        verify(productMetricsService).recordProductCreated(ProductKey.DC);
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void createProduct_WithExistingProductId_ShouldThrowException() {
        when(productRepository.existsByProductId("DC_TEMP1")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.createProduct(validProductDTO));

        assertEquals("Product with this productId already exists: DC_TEMP1", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
        verify(productMetricsService, never()).recordProductCreated(any());
    }

    @Test
    void getProductById_WithExistingId_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(validProduct));

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Дебетовая карта Classic", result.getName());
    }

    @Test
    void updateProduct_WithValidData_ShouldUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(validProduct));
        when(productRepository.save(any(Product.class))).thenReturn(validProduct);

        ProductDTO updateDTO = ProductDTO.builder()
                .name("Updated Product Name")
                .key(ProductKey.CC)
                .build();

        ProductDTO result = productService.updateProduct(1L, updateDTO);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
        verify(productMetricsService, never()).recordProductCreated(any());
    }
}

package org.clientpr.demo.service;

import lombok.RequiredArgsConstructor;
import ru.t1hwork.starter.aop.annotations.LogDatasourceError;
import org.clientpr.demo.model.ClientProduct;
import org.clientpr.demo.model.dto.ClientProductDTO;
import org.clientpr.demo.model.enums.ProductStatus;
import org.clientpr.demo.repository.ClientProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientProductService {
    private final ClientProductRepository clientProductRepository;
    private final KafkaProducerService kafkaProducerService;
    @LogDatasourceError(type = "ERROR")
    public ClientProductDTO createClientProduct(ClientProductDTO clientProductDTO) {
        if (clientProductRepository.existsByClientIdAndProductId(
                clientProductDTO.getClientId(),
                clientProductDTO.getProductId())) {
            throw new IllegalArgumentException("Client already has this product");
        }

        ClientProduct clientProduct = ClientProduct.builder(
                clientProductDTO.getClientId(),
                clientProductDTO.getProductId(),
                clientProductDTO.getOpenDate() != null ?
                        clientProductDTO.getOpenDate() : LocalDateTime.now(),
                clientProductDTO.getCloseDate(),
               clientProductDTO.getStatus()
                ).build();
        ClientProduct savedClientProduct = clientProductRepository.save(clientProduct);
 kafkaProducerService.sendToClientProducts(savedClientProduct, "CREATE");

        return convertToDTO(savedClientProduct);
    }

    public List<ClientProductDTO> getAllClientProducts() {
        return clientProductRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ClientProductDTO getClientProductById(Long id) {
        return clientProductRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("ClientProduct not found with id: " + id));
    }

    public List<ClientProductDTO> getClientProductsByClientId(Long clientId) {
        return clientProductRepository.findByClientId(clientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ClientProductDTO> getClientProductsByProductId(Long productId) {
        return clientProductRepository.findByProductId(productId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ClientProductDTO> getClientProductsByStatus(ProductStatus status) {
        return clientProductRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ClientProductDTO> getClientProductsByClientIdAndStatus(Long clientId, ProductStatus status) {
        return clientProductRepository.findByClientIdAndStatus(clientId, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ClientProductDTO updateClientProduct(Long id, ClientProductDTO clientProductDTO) {
        ClientProduct existingClientProduct = clientProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ClientProduct not found with id: " + id));

        if (!existingClientProduct.getClientId().equals(clientProductDTO.getClientId()) ||
                !existingClientProduct.getProductId().equals(clientProductDTO.getProductId())) {

            if (clientProductRepository.existsByClientIdAndProductId(
                    clientProductDTO.getClientId(),
                    clientProductDTO.getProductId())) {
                throw new IllegalArgumentException("Client already has this product");
            }
        }
        existingClientProduct.setClientId(clientProductDTO.getClientId());
        existingClientProduct.setProductId(clientProductDTO.getProductId());
        existingClientProduct.setOpenDate(clientProductDTO.getOpenDate());
        existingClientProduct.setCloseDate(clientProductDTO.getCloseDate());
        existingClientProduct.setStatus(clientProductDTO.getStatus());
        ClientProduct updatedClientProduct = clientProductRepository.save(existingClientProduct);
        kafkaProducerService.sendToClientProducts(updatedClientProduct, "UPDATE");
        return convertToDTO(updatedClientProduct);     }

    public ClientProductDTO updateClientProductStatus(Long id, ProductStatus status) {
        ClientProduct existingClientProduct = clientProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ClientProduct not found with id: " + id));

        existingClientProduct.setStatus(status);
        if (status == ProductStatus.CLOSED && existingClientProduct.getCloseDate() == null) {
            existingClientProduct.setCloseDate(LocalDateTime.now());
        }
        ClientProduct updatedClientProduct = clientProductRepository.save(existingClientProduct);
        return convertToDTO(updatedClientProduct);
    }

    public void deleteClientProduct(Long id) {
        ClientProduct prod=clientProductRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("ClientProduct not found with id: " + id));

        clientProductRepository.deleteById(id);
        kafkaProducerService.sendToClientProducts(prod, "DELETE");
    }

    private ClientProductDTO convertToDTO(ClientProduct clientProduct) {
        return ClientProductDTO.builder()
                .id(clientProduct.getId())
                .clientId(clientProduct.getClientId())
                .productId(clientProduct.getProductId())
                .openDate(clientProduct.getOpenDate())
                .closeDate(clientProduct.getCloseDate())
                .status(clientProduct.getStatus())
                .build();
    }
}

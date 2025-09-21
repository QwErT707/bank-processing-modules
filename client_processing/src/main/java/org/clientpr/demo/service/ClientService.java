package org.clientpr.demo.service;

import lombok.RequiredArgsConstructor;
import org.clientpr.demo.model.Client;
import org.clientpr.demo.model.dto.ClientDTO;
import org.clientpr.demo.model.enums.DocumentType;
import org.clientpr.demo.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientDTO createClient(ClientDTO clientDTO) {
        if (clientRepository.existsByClientId(clientDTO.getClientId())) {
            throw new IllegalArgumentException("Client with this clientId already exists: " + clientDTO.getClientId());
        }
        if (clientRepository.existsByDocumentId(clientDTO.getDocumentId())) {
            throw new IllegalArgumentException("Client with this documentId already exists: " + clientDTO.getDocumentId());
        }

        Client client = Client.hiddenBuilder()
                .clientId(clientDTO.getClientId())
                .userId(clientDTO.getUserId())
                .firstName(clientDTO.getFirstName())
                .middleName(clientDTO.getMiddleName())
                .lastName(clientDTO.getLastName())
                .dateOfBirth(clientDTO.getDateOfBirth())
                .documentType(clientDTO.getDocumentType())
                .documentId(clientDTO.getDocumentId())
                .documentPrefix(clientDTO.getDocumentPrefix())
                .documentSuffix(clientDTO.getDocumentSuffix())
                .build();

        Client savedClient = clientRepository.save(client);
        return convertToDTO(savedClient);
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO getClientById(Long id) {
        return clientRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + id));
    }

    public ClientDTO getClientByClientId(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with clientId: " + clientId));
    }

    public List<ClientDTO> getClientsByUserId(Long userId) {
        return clientRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ClientDTO> getClientsByDocumentType(DocumentType documentType) {
        return clientRepository.findByDocumentType(documentType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + id));

        if (!existingClient.getClientId().equals(clientDTO.getClientId()) &&
                clientRepository.existsByClientId(clientDTO.getClientId())) {
            throw new IllegalArgumentException("ClientId already exists: " + clientDTO.getClientId());
        }

        if (!existingClient.getDocumentId().equals(clientDTO.getDocumentId()) &&
                clientRepository.existsByDocumentId(clientDTO.getDocumentId())) {
            throw new IllegalArgumentException("DocumentId already exists: " + clientDTO.getDocumentId());
        }

        existingClient.setClientId(clientDTO.getClientId());
        existingClient.setUserId(clientDTO.getUserId());
        existingClient.setFirstName(clientDTO.getFirstName());
        existingClient.setMiddleName(clientDTO.getMiddleName());
        existingClient.setLastName(clientDTO.getLastName());
        existingClient.setDateOfBirth(clientDTO.getDateOfBirth());
        existingClient.setDocumentType(clientDTO.getDocumentType());
        existingClient.setDocumentId(clientDTO.getDocumentId());
        existingClient.setDocumentPrefix(clientDTO.getDocumentPrefix());
        existingClient.setDocumentSuffix(clientDTO.getDocumentSuffix());

        Client updatedClient = clientRepository.save(existingClient);
        return convertToDTO(updatedClient);
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    private ClientDTO convertToDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .clientId(client.getClientId())
                .userId(client.getUserId())
                .firstName(client.getFirstName())
                .middleName(client.getMiddleName())
                .lastName(client.getLastName())
                .dateOfBirth(client.getDateOfBirth())
                .documentType(client.getDocumentType())
                .documentId(client.getDocumentId())
                .documentPrefix(client.getDocumentPrefix())
                .documentSuffix(client.getDocumentSuffix())
                .build();
    }
}

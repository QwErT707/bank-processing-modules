package org.clientpr.demo.service;

import lombok.RequiredArgsConstructor;
import org.clientpr.demo.model.Client;
import org.clientpr.demo.model.enums.ClientRole;
import org.clientpr.demo.repository.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClientAccessService {
    private final ClientRepository clientRepository;
    public void checkClientAccess(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        if (ClientRole.BLOCKED_CLIENT.toString().equals(client.getClientRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Client is blocked and cannot perform this operation");
        }
    }    public boolean isClientBlocked(Long clientId) {
        return clientRepository.findById(clientId)
                .map(client -> ClientRole.BLOCKED_CLIENT.toString().equals(client.getClientRole()))
                .orElse(false);
    }
}

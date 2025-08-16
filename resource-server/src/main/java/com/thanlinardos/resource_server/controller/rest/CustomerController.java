package com.thanlinardos.resource_server.controller.rest;

import com.thanlinardos.resource_server.model.info.Client;
import com.thanlinardos.resource_server.model.info.Customer;
import com.thanlinardos.resource_server.model.info.OwnerDetailsInfo;
import com.thanlinardos.resource_server.model.info.RegisterCustomerDetails;
import com.thanlinardos.resource_server.model.mapped.ClientModel;
import com.thanlinardos.resource_server.model.mapped.CustomerModel;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.service.ClientService;
import com.thanlinardos.resource_server.service.CustomerService;
import com.thanlinardos.resource_server.service.OwnerService;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OwnerService ownerService;
    private final UserService userService;
    private final ClientService clientService;

    @GetMapping("/customers/{email}")
    public ResponseEntity<CustomerModel> getCustomerByUsernameOrEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerModel>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientModel>> getClients() {
        return ResponseEntity.ok(clientService.getClients());
    }

    @GetMapping("/clients/{name}")
    public ResponseEntity<ClientModel> getClientByName(@PathVariable String name) {
        return clientService.getClientByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    public ResponseEntity<@Valid Customer> getUserDetailsAfterLogin(Authentication authentication) {
        return ownerService.getOwnerByPrincipalName(authentication.getName())
                .map(OwnerModel::toCustomerInfo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client")
    public ResponseEntity<@Valid Client> getClientDetailsAfterLogin(Authentication authentication) {
        return ownerService.getOwnerByPrincipalName(authentication.getName())
                .map(OwnerModel::toClientInfo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner")
    public ResponseEntity<@Valid OwnerDetailsInfo> getAuthenticatedOwnerDetailsAfterLogin(Authentication authentication) {
        return ownerService.getOwnerByPrincipalName(authentication.getName())
                .map(OwnerModel::toInfo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/customers")
    public ResponseEntity<Void> registerCustomer(@Valid @RequestBody RegisterCustomerDetails customerDetails) {
        return ResponseEntity.created(URI.create("/customers/" + userService.createCustomer(customerDetails)))
                .build();
    }

    @PostMapping("/user")
    public ResponseEntity<Customer> registerCustomerWithRoles(@Valid @RequestBody Customer customer) {
        return ResponseEntity.ok(userService.createCustomerWithRoles(customer).toCustomerInfo());
    }
}

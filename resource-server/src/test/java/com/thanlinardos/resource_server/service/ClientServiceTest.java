package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.CoreTest;
import com.thanlinardos.resource_server.model.entity.ClientJpa;
import com.thanlinardos.resource_server.model.entity.OwnerJpa;
import com.thanlinardos.resource_server.model.mapped.ClientModel;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@CoreTest
class ClientServiceTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 1, 1);

    @Mock
    private EntityManager entityManager;
    @Mock
    private UserService userService;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setup() {
        clientService = new ClientService(entityManager, userService);
    }

    public static Stream<Arguments> getClientsParams() {
        return Stream.of(
                Arguments.of("empty list", Collections.emptyList()),
                Arguments.of("has clients", List.of(
                        ClientModel.builder()
                                .id(1L)
                                .name("client1")
                                .build(),
                        ClientModel.builder()
                                .id(2L)
                                .name("client2")
                                .build()
                ))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getClientsParams")
    void getClients(String description, List<ClientModel> expected) {
        when(entityManager.createQuery(anyString(), any())).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(anyString(), any()).getResultList()).thenReturn((List) expected.stream().map(ClientJpa::fromModel).toList());
        List<ClientModel> actual = clientService.getClients();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getClientByName() {
        String clientName = "testClient";
        ClientJpa clientJpa = ClientJpa.builder().name(clientName).owner(OwnerJpa.builder().id(1L).build()).build();
        TypedQuery<Object> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(clientJpa));

        ClientModel expected = new ClientModel(clientJpa);
        ClientModel actual = clientService.getClientByName(clientName)
                .orElseThrow(() -> new AssertionError("Client not found"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getOwnerByServiceAccountId() {
        UUID serviceAccountId = UUID.randomUUID();
        OwnerJpa ownerJpa = buildOwnerWithClient();
        ClientJpa clientJpa = ClientJpa.builder().serviceAccountId(serviceAccountId).owner(ownerJpa).build();
        TypedQuery<Object> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(clientJpa));

        clientService.getOwnerByServiceAccountId(serviceAccountId)
                .ifPresentOrElse(
                        ownerModel -> Assertions.assertEquals(ownerJpa.getId(), ownerModel.getId()),
                        () -> Assertions.fail("Owner not found")
                );
    }

    private OwnerJpa buildOwnerWithClient() {
        return OwnerJpa.builder().id(1L).uuid(UUID.randomUUID()).type("CLIENT").build();
    }

    @Test
    void getClientInfoByName() {
        String clientName = "testClient";
        ClientJpa clientJpa = ClientJpa.builder().name(clientName).owner(buildOwnerWithClient(clientName)).build();
        TypedQuery<Object> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(clientJpa));

        clientService.getClientInfoByName(clientName)
                .ifPresentOrElse(
                        client -> Assertions.assertEquals(clientName, client.getName()),
                        () -> Assertions.fail("Client info not found")
                );
    }

    private OwnerJpa buildOwnerWithClient(String clientName) {
        ClientJpa client = ClientJpa.builder().name(clientName).build();
        OwnerJpa owner = OwnerJpa.builder().id(1L).type("CLIENT").client(client).createdAt(CREATED_AT).build();
        client.setOwner(owner);
        return owner;
    }
}
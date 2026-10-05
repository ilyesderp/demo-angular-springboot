package com.example.backend.adapter.out.persistence;

import com.example.backend.application.exception.NotFoundException;
import com.example.backend.application.port.out.ClientRepository;
import com.example.backend.application.port.out.InvoiceRepository;
import com.example.backend.application.port.out.UserRepository;
import com.example.backend.application.port.out.WorkspaceRepository;
import com.example.backend.domain.Client;
import com.example.backend.domain.Invoice;
import com.example.backend.domain.InvoiceItem;
import com.example.backend.domain.InvoiceStatus;
import com.example.backend.domain.User;
import com.example.backend.domain.Workspace;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class WorkspacePersistenceAdapter implements WorkspaceRepository {

    private final WorkspaceJpaRepository jpa;

    WorkspacePersistenceAdapter(WorkspaceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Workspace save(Workspace workspace) {
        jpa.save(new WorkspaceEntity(workspace.id(), workspace.name()));
        return workspace;
    }

    @Override
    public int nextInvoiceNumber(UUID workspaceId) {
        WorkspaceEntity workspace = jpa.findByIdForUpdate(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));
        int reserved = workspace.nextInvoiceNumber;
        workspace.nextInvoiceNumber = reserved + 1;
        return reserved;
    }
}

@Component
class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    UserPersistenceAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    public User save(User user) {
        jpa.save(new UserEntity(user.id(), user.workspaceId(), user.email(), user.passwordHash()));
        return user;
    }

    private static User toDomain(UserEntity entity) {
        return new User(entity.id, entity.workspaceId, entity.email, entity.passwordHash);
    }
}

@Component
class ClientPersistenceAdapter implements ClientRepository {

    private final ClientJpaRepository jpa;

    ClientPersistenceAdapter(ClientJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Client save(Client client) {
        jpa.save(new ClientEntity(client.id(), client.workspaceId(), client.name(), client.email()));
        return client;
    }

    @Override
    public List<Client> findAllByWorkspace(UUID workspaceId) {
        return jpa.findAllByWorkspaceIdOrderByNameAsc(workspaceId).stream()
                .map(ClientPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<Client> findByIdAndWorkspace(UUID id, UUID workspaceId) {
        return jpa.findByIdAndWorkspaceId(id, workspaceId).map(ClientPersistenceAdapter::toDomain);
    }

    @Override
    public long countByWorkspace(UUID workspaceId) {
        return jpa.countByWorkspaceId(workspaceId);
    }

    private static Client toDomain(ClientEntity entity) {
        return new Client(entity.id, entity.workspaceId, entity.name, entity.email);
    }
}

@Component
class InvoicePersistenceAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository jpa;

    InvoicePersistenceAdapter(InvoiceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Invoice save(Invoice invoice) {
        jpa.save(toEntity(invoice));
        return invoice;
    }

    @Override
    public List<Invoice> findAllByWorkspace(UUID workspaceId) {
        return jpa.findAllByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream()
                .map(InvoicePersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public long countByWorkspace(UUID workspaceId) {
        return jpa.countByWorkspaceId(workspaceId);
    }

    @Override
    public Map<InvoiceStatus, Long> totalCentsByStatus(UUID workspaceId) {
        Map<InvoiceStatus, Long> totals = new EnumMap<>(InvoiceStatus.class);
        for (Object[] row : jpa.sumTotalByStatus(workspaceId)) {
            totals.put((InvoiceStatus) row[0], ((Number) row[1]).longValue());
        }
        return totals;
    }

    private static InvoiceEntity toEntity(Invoice invoice) {
        InvoiceEntity entity = new InvoiceEntity();
        entity.id = invoice.id();
        entity.workspaceId = invoice.workspaceId();
        entity.clientId = invoice.clientId();
        entity.number = invoice.number();
        entity.status = invoice.status();
        entity.currency = invoice.currency();
        entity.issueDate = invoice.issueDate();
        entity.dueDate = invoice.dueDate();
        entity.totalCents = invoice.totalCents();
        invoice.items().forEach(item -> entity.items.add(
                new InvoiceItemEntity(item.id(), item.description(), item.quantity(), item.unitPriceCents())));
        return entity;
    }

    private static Invoice toDomain(InvoiceEntity entity) {
        List<InvoiceItem> items = entity.items.stream()
                .map(item -> new InvoiceItem(item.id, item.description, item.quantity, item.unitPriceCents))
                .toList();
        return new Invoice(
                entity.id, entity.workspaceId, entity.clientId, entity.number, entity.status,
                entity.currency, entity.issueDate, entity.dueDate, items);
    }
}

package com.example.backend.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface WorkspaceJpaRepository extends JpaRepository<WorkspaceEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WorkspaceEntity w where w.id = :id")
    Optional<WorkspaceEntity> findByIdForUpdate(@Param("id") UUID id);
}

interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}

interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {

    List<ClientEntity> findAllByWorkspaceIdOrderByNameAsc(UUID workspaceId);

    Optional<ClientEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    long countByWorkspaceId(UUID workspaceId);
}

interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID> {

    List<InvoiceEntity> findAllByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);

    long countByWorkspaceId(UUID workspaceId);

    @Query("select i.status, sum(i.totalCents) from InvoiceEntity i where i.workspaceId = :workspaceId group by i.status")
    List<Object[]> sumTotalByStatus(@Param("workspaceId") UUID workspaceId);
}

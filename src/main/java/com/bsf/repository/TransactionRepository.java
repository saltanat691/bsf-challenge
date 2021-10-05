package com.bsf.repository;

import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    @Query("select t from TransactionEntity t where t.source = :account or t.target = :account ")
    List<TransactionEntity> findAllBySourceOrTarget(@Param("account") final AccountEntity account);
}

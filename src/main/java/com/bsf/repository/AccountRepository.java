package com.bsf.repository;

import com.bsf.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ae from AccountEntity ae where ae.id = :id")
    Optional<AccountEntity> findByIdWithLock(@Param("id") final UUID id);
}

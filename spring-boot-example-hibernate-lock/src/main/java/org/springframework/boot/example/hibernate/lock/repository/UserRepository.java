package org.springframework.boot.example.hibernate.lock.repository;

import jakarta.persistence.LockModeType;
import org.springframework.boot.example.hibernate.lock.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select u from User u where u.id = ?1")
    User findByIdWithOptimisticLock(Long id);

    @Lock(value = LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select u from User u where u.id = ?1")
    User findByIdWithOptimisticForceIncrementLock(Long id);
}

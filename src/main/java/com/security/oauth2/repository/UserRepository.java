package com.security.oauth2.repository;

import com.security.oauth2.document.UserCred;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserCred, String> {
    Optional<UserCred> findByUsername(String username);
    boolean existsByUsername(String username);
}

package com.alcadrial.kangu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.alcadrial.kangu.data.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, String> {
	
	@Modifying
	@Query(nativeQuery = true, value = """
		INSERT INTO users (username, password, email, enabled)
			VALUES (?1, ?2, ?3, ?4)
		""")
	void createNew(String username, String password, String email, boolean enabled);
	
	Optional<UserEntity.Safe> findByUsername(String username);
}

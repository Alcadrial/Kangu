package com.alcadrial.kangu.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alcadrial.kangu.data.MessageReadCursorEntity;


public interface MessagesReadCursorsRepository extends CrudRepository<MessageReadCursorEntity, MessageReadCursorEntity.PrimaryKey> {

	@Modifying
	@Query(nativeQuery = true, value = """
		UPDATE messages_read_cursors
			SET is_blocked = ?3
			WHERE sender = ?1 AND
				receiver = ?2
		""")
	void updateBlockedStatus(String sender, String receiver, boolean isBlocked);

	@Query(nativeQuery = true, value = """
		WITH q(is_blocked) AS (
			SELECT is_blocked
				FROM messages_read_cursors
				WHERE sender = ?1 AND
					receiver = ?2
		)
		SELECT COALESCE(is_blocked, false)
			FROM dual LEFT JOIN q ON true
		""")
	boolean isSenderBlocked(String sender, String receiver);
	
}

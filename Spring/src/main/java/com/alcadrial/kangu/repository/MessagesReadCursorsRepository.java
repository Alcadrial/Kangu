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
	
}

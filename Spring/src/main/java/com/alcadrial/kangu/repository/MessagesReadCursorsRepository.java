package com.alcadrial.kangu.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.jspecify.annotations.Nullable;
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
	
	record MessageReadCursorData(
		LocalDateTime receiveTimestampOther,
		LocalDateTime readTimestampSelf,
		LocalDateTime readTimestampOther,
		boolean isBlockedBySelf,
		boolean isBlockedByOther
	) {}
	
	@Query("""
		SELECT t.receiveTimestamp as receiveTimestampOther, t.readTimestamp as readTimestampSelf,
			s.readTimestamp as readTimestampOther, t.isBlocked as isBlockedByOther, s.isBlocked as isBlockedBySelf
			FROM MessageReadCursorEntity t, MessageReadCursorEntity s
			WHERE t.sender = ?1 AND
				t.receiver = ?2 AND
				s.sender = ?2 AND
				s.receiver = ?1
		""")
	@Nullable MessageReadCursorData findById(String sender, String receiver);
	
	@Query("""
		SELECT new com.alcadrial.kangu.repository.MessagesReadCursorsRepository$MessageReadCursorData(t.receiveTimestamp AS receiveTimestampOther, t.readTimestamp AS readTimestampSelf,
			s.readTimestamp AS readTimestampOther, t.isBlocked AS isBlockedByOther, s.isBlocked AS isBlockedBySelf)
			FROM MessageReadCursorEntity t, MessageReadCursorEntity s
			WHERE (t.sender = ?1 AND s.receiver != ?1) OR
				(t.receiver = ?1 AND s.sender != ?1)
		UNION ALL
		SELECT new com.alcadrial.kangu.repository.MessagesReadCursorsRepository$MessageReadCursorData(local_datetime, local_datetime, local_datetime, false, false)
			FROM MessageReadCursorEntity t
			WHERE t.sender = ?1 AND
				t.receiver = ?1
		""")
	List<MessageReadCursorData> findAllUserChats(String username);
}

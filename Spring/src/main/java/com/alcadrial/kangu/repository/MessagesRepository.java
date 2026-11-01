package com.alcadrial.kangu.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alcadrial.kangu.data.MessageEntity;

public interface MessagesRepository extends CrudRepository<MessageEntity, MessageEntity.PrimaryKey> {
	
	public record Message(String sender, String receiver, String message, String messageMimeType, LocalDateTime sendTimestamp, int partIndex) {}
	
	@Query(nativeQuery = true, value = """
		SELECT sender, receiver, message, message_mime_type, send_timestamp, part_index
			FROM messages
			WHERE sender = ?1 AND
				receiver = ?2 AND (
					send_timestamp < ?3 OR (
						send_timestamp = ?3 AND
						part_index < ?4
				))
			ORDER BY send_timestamp DESC, part_index DESC
			FETCH FIRST ?5 ROWS ONLY
		""")
	List<Message> findMessagesSentBeforeTimeAndPart(String sender, String receiver, LocalDateTime lastReadTime, int lastReadPart, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT sender, receiver, message, message_mime_type, send_timestamp, part_index
			FROM messages
			WHERE sender IN ( ?1, ?2 ) AND
				receiver IN ( ?1, ?2 ) AND
				receiver != sender AND (
					send_timestamp < ?3 OR (
						send_timestamp = ?3 AND
						part_index < ?4
				))
			ORDER BY send_timestamp DESC, part_index DESC
			FETCH FIRST ?5 ROWS ONLY
		""")
	List<Message> findMessagesSentOrReceivedBeforeTimeAndPart(String sender, String receiver, LocalDateTime lastReadTime, int lastReadPart, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT sender, receiver, message, message_mime_type, send_timestamp, part_index
			FROM messages
			WHERE sender = ?1 AND
				receiver = sender AND (
					send_timestamp < ?2 OR (
						send_timestamp = ?2 AND
						part_index < ?3
				))
			ORDER BY send_timestamp DESC, part_index DESC
			FETCH FIRST ?4 ROWS ONLY
		""")
	List<Message> findMessagesSelfSentBeforeTimeAndPart(String username, LocalDateTime lastReadTime, int lastReadPart, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT sender, receiver, message, message_mime_type, send_timestamp, part_index
			FROM messages
			WHERE sender = ?1 AND
				receiver = ?2
			ORDER BY send_timestamp DESC, part_index DESC
			FETCH FIRST ?3 ROWS ONLY
		""")
	List<Message> findMessagesSent(String sender, String receiver, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT sender, receiver, message, message_mime_type, send_timestamp, part_index
			FROM messages
			WHERE sender IN ( ?1, ?2 ) AND
				receiver IN ( ?1, ?2 ) AND
				receiver != sender
			ORDER BY send_timestamp DESC, part_index DESC
			FETCH FIRST ?3 ROWS ONLY
		""")
	List<Message> findMessagesSentOrReceived(String sender, String receiver, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT sender, receiver, message, message_mime_type, send_timestamp, part_index
			FROM messages
			WHERE sender = ?1 AND
				receiver = sender
			ORDER BY send_timestamp DESC, part_index DESC
			FETCH FIRST ?2 ROWS ONLY
		""")
	List<Message> findMessagesSelfSent(String username, int limit);
}

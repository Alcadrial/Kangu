package com.alcadrial.kangu.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alcadrial.kangu.data.MessageLookupEntity;

public interface MessagesRepository extends CrudRepository<MessageLookupEntity, MessageLookupEntity.PrimaryKey> {
	
	@Query(nativeQuery = true, value = """
		SELECT message, send_timestamp, part_index
			FROM messages
			WHERE sender = ?1 AND
				receiver = ?2 AND (
					send_timestamp < ?3 OR (
						send_timestamp = ?3 AND
						part_index < ?4
				))
			LIMIT ?5
		""")
	List<String> findMessagesSentBeforeTimeAndPart(String sender, String receiver, LocalDateTime lastReadTime, int lastReadPart, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT message, send_timestamp, part_index
			FROM messages
			WHERE sender IN ( ?1, ?2 ) AND
				receiver IN ( ?1, ?2 ) AND
				receiver != sender AND (
					send_timestamp < ?3 OR (
						send_timestamp = ?3 AND
						part_index < ?4
				))
			LIMIT ?5
		""")
	List<String> findMessagesSentOrReceivedBeforeTimeAndPart(String sender, String receiver, LocalDateTime lastReadTime, int lastReadPart, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT message, send_timestamp, part_index
			FROM messages
			WHERE sender = ?1 AND
				receiver = sender AND (
					send_timestamp < ?2 OR (
						send_timestamp = ?2 AND
						part_index < ?3
				))
			LIMIT ?4
		""")
	List<String> findMessagesSelfSentBeforeTimeAndPart(String username, LocalDateTime lastReadTime, int lastReadPart, int limit);

	@Query(nativeQuery = true, value = """
		SELECT message, send_timestamp, part_index
			FROM messages
			WHERE sender = ?1 AND
				receiver = ?2
			LIMIT ?3
		""")
	List<String> findMessagesSent(String sender, String receiver, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT message, send_timestamp, part_index
			FROM messages
			WHERE sender IN ( ?1, ?2 ) AND
				receiver IN ( ?1, ?2 ) AND
				receiver != sender
			LIMIT ?3
		""")
	List<String> findMessagesSentOrReceived(String sender, String receiver, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT message, send_timestamp, part_index
			FROM messages
			WHERE sender = ?1 AND
				receiver = sender
			LIMIT ?2
		""")
	List<String> findMessagesSelfSent(String username, int limit);
}

package com.alcadrial.kangu.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alcadrial.kangu.data.MessageLookupEntity;

public interface MessagesRepository extends CrudRepository<MessageLookupEntity, MessageLookupEntity.PrimaryKey> {
	
	@Query(nativeQuery = true, value = """
		SELECT LISTAGG(message, ', ') OVER (part_index)
			FROM messages
			WHERE sender = ?1 AND
				receiver = ?2 AND
				send_timestamp < ?3
			GROUP BY send_timestamp
			LIMIT ?4
		""")
	List<String> findMessagesFromSentBeforeTime(String sender, String receiver, LocalDateTime lastReadTime, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT LISTAGG(message, ', ') OVER (part_index)
			FROM messages
			WHERE sender IN ( ?1, ?2 ) AND
				receiver IN ( ?1, ?2 ) AND
				receiver != sender AND
				send_timestamp < ?3
			GROUP BY send_timestamp
			LIMIT ?4
		""")
	List<String> findMessagesFromSentAndReceivedBeforeTime(String sender, String receiver, LocalDateTime lastReadTime, int limit);
	
	@Query(nativeQuery = true, value = """
		SELECT LISTAGG(message, ', ') OVER (part_index)
			FROM messages
			WHERE sender = ?1 AND
				receiver = sender AND
				send_timestamp < ?2
			GROUP BY send_timestamp
			LIMIT ?3
		""")
	List<String> findMessagesFromSelfBeforeTime(String username, LocalDateTime lastReadTime, int limit);
}

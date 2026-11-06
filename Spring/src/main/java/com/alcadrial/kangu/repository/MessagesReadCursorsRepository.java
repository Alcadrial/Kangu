package com.alcadrial.kangu.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alcadrial.kangu.data.MessageReadCursorEntity;

public interface MessagesReadCursorsRepository extends CrudRepository<MessageReadCursorEntity, MessageReadCursorEntity.PrimaryKey> {
	
	@Modifying
	@Query("""
		UPDATE MessageReadCursorEntity
			SET isBlocked = ?3
			WHERE sender = ?1 AND
				receiver = ?2
		""")
	void updateBlockedStatus(String sender, String receiver, boolean isBlocked);
	
	@Query("""
		SELECT isBlocked
			FROM MessageReadCursorEntity
			WHERE sender = ?1 AND
				receiver = ?2
		""")
	Optional<Boolean> isSenderBlocked(String sender, String receiver);
	
	/**
	 * Data relative to the state of a chat between two users (self and other)
	 * 
	 * @param receiveTimestampOther Timestamp of last message received by other
	 * @param readTimestampSelf Timestamp of last message read by self
	 * @param readTimestampOther Timestamp of last message read by other
	 * @param isBlockedBySelf True if other is currently blocked by self
	 * @param isBlockedByOther True if self is currently blocked by other
	 */
	record MessageReadCursorData(
		LocalDateTime receiveTimestampOther,
		LocalDateTime readTimestampSelf,
		LocalDateTime readTimestampOther,
		boolean isBlockedBySelf,
		boolean isBlockedByOther
	) {}
	
	@Query("""
		SELECT SC.receiveTimestamp, RC.readTimestamp, SC.readTimestamp, RC.isBlocked, SC.isBlocked
			FROM MessageReadCursorEntity SC,
				MessageReadCursorEntity RC
			WHERE SC.sender = ?1 AND
				SC.receiver = ?2 AND
				RC.sender = ?2 AND
				RC.receiver = ?1
		""")
	
	Optional<MessageReadCursorData> findById(String sender, String receiver);
	
	/**
	 * @param username The username of other
	 * @param lastMessage The last message in the chat
	 * @param lastMessageMimeType The mime type of the last message in the chat
	 * @param messageTimestamp Timestamp of last message in the chat
	 * @param receiveTimestampOther Timestamp of last message received by other
	 * @param readTimestampSelf Timestamp of last message read by self
	 * @param readTimestampOther Timestamp of last message read by other
	 * @param isBlockedBySelf True if other is currently blocked by self
	 * @param isBlockedByOther True if self is currently blocked by other
	 */
	public static record ChatPreviewData(
		String username,
		String lastMessage,
		String lastMessageMimeType,
		LocalDateTime messageTimestamp,
		LocalDateTime receiveTimestampOther,
		LocalDateTime readTimestampSelf,
		LocalDateTime readTimestampOther,
		boolean isBlockedBySelf,
		boolean isBlockedByOther
	) {}
	
	@Query("""
		SELECT CASE WHEN ?1 = M.sender THEN M.receiver ELSE M.sender END,
			CASE WHEN M.messageMimeType = 'text/plain' THEN M.message ELSE '' END,
			M.messageMimeType, M.sendTimestamp,
			CASE WHEN ?1 = M.sender THEN SC.receiveTimestamp ELSE RC.receiveTimestamp END,
			CASE WHEN ?1 = M.sender THEN RC.readTimestamp ELSE SC.readTimestamp END,
			CASE WHEN ?1 = M.sender THEN SC.readTimestamp ELSE RC.readTimestamp END,
			CASE WHEN ?1 = M.sender THEN RC.isBlocked ELSE SC.isBlocked END,
			CASE WHEN ?1 = M.sender THEN SC.isBlocked ELSE RC.isBlocked END
			FROM MessageEntity M,
				MessageReadCursorEntity SC,
				MessageReadCursorEntity RC
				LEFT JOIN (SELECT sendTimestamp AS time, sender AS sender, receiver AS receiver, partIndex AS partIndex,
						rank() OVER (PARTITION BY sender, receiver ORDER BY sendTimestamp DESC, partIndex DESC) AS rnk
					FROM MessageEntity
					WHERE sender = ?1 OR
						receiver = ?1
				) L
			WHERE M.sendTimestamp = L.time AND
				M.sender = L.sender AND
				M.receiver = L.receiver AND
				M.partIndex = L.partIndex AND
				L.rnk = 1 AND
				M.sender = SC.sender AND
				M.receiver = SC.receiver AND
				M.receiver = RC.sender AND
				M.sender = RC.receiver
			ORDER BY M.sendTimestamp DESC
		""")
	List<ChatPreviewData> getChatPreviewDataWithDuplicates(String username);
}

package com.zhongbei.repository;

import com.zhongbei.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    
    List<Word> findByIsLearnedFalse();
    
    List<Word> findByIsLearnedTrue();
    
    @Query("SELECT w FROM Word w WHERE w.isLearned = false ORDER BY w.wrongCount DESC, RAND()")
    List<Word> findUnlearnedWordsForReview();
    
    @Query(value = "SELECT * FROM words WHERE is_learned = 0 ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Word> findRandomUnlearnedWords(@Param("limit") int limit);
    
    @Query(value = "SELECT * FROM words ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Word> findRandomWords(@Param("limit") int limit);
    
    long countByIsLearnedFalse();
    
    long countByIsLearnedTrue();
    
    List<Word> findByWordContaining(String keyword);
}

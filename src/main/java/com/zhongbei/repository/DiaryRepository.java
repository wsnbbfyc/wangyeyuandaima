package com.zhongbei.repository;

import com.zhongbei.model.Diary;
import com.zhongbei.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByOrderByPinnedAndCreateTimeDesc();
    
    List<Diary> findByIsDeletedTrue();
    
    @Query("SELECT d FROM Diary d JOIN d.tags t WHERE t.id = :tagId")
    List<Diary> findByTagId(@Param("tagId") Long tagId);
    
    @Query("SELECT d FROM Diary d WHERE d.title LIKE %:keyword% OR d.content LIKE %:keyword%")
    List<Diary> searchByKeyword(@Param("keyword") String keyword);
    
    List<Diary> findAllDeleted();
}

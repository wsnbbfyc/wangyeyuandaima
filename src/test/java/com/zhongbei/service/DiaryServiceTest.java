package com.zhongbei.service;

import com.zhongbei.model.Diary;
import com.zhongbei.model.Tag;
import com.zhongbei.repository.DiaryRepository;
import com.zhongbei.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DiaryService diaryService;

    private Diary testDiary;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        testDiary = new Diary();
        testDiary.setId(1L);
        testDiary.setTitle("测试随笔");
        testDiary.setContent("这是测试内容");
        testDiary.setCreateTime(LocalDateTime.now());
        testDiary.setUpdateTime(LocalDateTime.now());
        testDiary.setIsPinned(false);
        testDiary.setIsDeleted(false);
        testDiary.setTags(new HashSet<>());

        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("测试标签");
        testTag.setColor("#FF5733");
    }

    @Test
    @DisplayName("测试查找所有随笔")
    void testFindAll() {
        List<Diary> diaries = Arrays.asList(testDiary);
        when(diaryRepository.findAllByOrderByPinnedAndCreateTimeDesc()).thenReturn(diaries);

        List<Diary> result = diaryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试随笔", result.get(0).getTitle());
        verify(diaryRepository, times(1)).findAllByOrderByPinnedAndCreateTimeDesc();
    }

    @Test
    @DisplayName("测试根据ID查找随笔")
    void testFindById() {
        when(diaryRepository.findById(1L)).thenReturn(Optional.of(testDiary));

        Optional<Diary> result = diaryService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("测试随笔", result.get().getTitle());
        verify(diaryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("测试保存随笔")
    void testSave() {
        when(diaryRepository.save(any(Diary.class))).thenReturn(testDiary);

        Diary result = diaryService.save(testDiary);

        assertNotNull(result);
        assertEquals("测试随笔", result.getTitle());
        verify(diaryRepository, times(1)).save(any(Diary.class));
    }

    @Test
    @DisplayName("测试软删除随笔")
    void testSoftDelete() {
        when(diaryRepository.findById(1L)).thenReturn(Optional.of(testDiary));
        when(diaryRepository.save(any(Diary.class))).thenReturn(testDiary);

        diaryService.softDelete(1L);

        assertTrue(testDiary.getIsDeleted());
        verify(diaryRepository, times(1)).save(testDiary);
    }

    @Test
    @DisplayName("测试恢复已删除的随笔")
    void testRestore() {
        testDiary.setIsDeleted(true);
        when(diaryRepository.findById(1L)).thenReturn(Optional.of(testDiary));
        when(diaryRepository.save(any(Diary.class))).thenReturn(testDiary);

        diaryService.restore(1L);

        assertFalse(testDiary.getIsDeleted());
        verify(diaryRepository, times(1)).save(testDiary);
    }

    @Test
    @DisplayName("测试切换置顶状态 - 从未置顶设为置顶")
    void testTogglePin_UnpinnedToPinned() {
        testDiary.setIsPinned(false);
        when(diaryRepository.findById(1L)).thenReturn(Optional.of(testDiary));
        when(diaryRepository.save(any(Diary.class))).thenReturn(testDiary);

        diaryService.togglePin(1L);

        assertTrue(testDiary.getIsPinned());
        verify(diaryRepository, times(1)).save(testDiary);
    }

    @Test
    @DisplayName("测试切换置顶状态 - 从置顶设为未置顶")
    void testTogglePin_PinnedToUnpinned() {
        testDiary.setIsPinned(true);
        when(diaryRepository.findById(1L)).thenReturn(Optional.of(testDiary));
        when(diaryRepository.save(any(Diary.class))).thenReturn(testDiary);

        diaryService.togglePin(1L);

        assertFalse(testDiary.getIsPinned());
        verify(diaryRepository, times(1)).save(testDiary);
    }

    @Test
    @DisplayName("测试查找已删除的随笔")
    void testFindAllDeleted() {
        List<Diary> deletedDiaries = Arrays.asList(testDiary);
        testDiary.setIsDeleted(true);
        when(diaryRepository.findAllDeleted()).thenReturn(deletedDiaries);

        List<Diary> result = diaryService.findAllDeleted();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsDeleted());
        verify(diaryRepository, times(1)).findAllDeleted();
    }

    @Test
    @DisplayName("测试根据标签ID查找随笔")
    void testFindByTagId() {
        List<Diary> taggedDiaries = Arrays.asList(testDiary);
        when(diaryRepository.findByTagId(1L)).thenReturn(taggedDiaries);

        List<Diary> result = diaryService.findByTagId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(diaryRepository, times(1)).findByTagId(1L);
    }

    @Test
    @DisplayName("测试搜索随笔")
    void testSearch() {
        List<Diary> searchResults = Arrays.asList(testDiary);
        when(diaryRepository.searchByKeyword("测试")).thenReturn(searchResults);

        List<Diary> result = diaryService.search("测试");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().contains("测试"));
        verify(diaryRepository, times(1)).searchByKeyword("测试");
    }

    @Test
    @DisplayName("测试统计随笔数量")
    void testCount() {
        when(diaryRepository.count()).thenReturn(5L);

        long count = diaryService.count();

        assertEquals(5L, count);
        verify(diaryRepository, times(1)).count();
    }

    @Test
    @DisplayName("测试根据不存在的ID查找随笔返回空")
    void testFindById_NotFound() {
        when(diaryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Diary> result = diaryService.findById(999L);

        assertFalse(result.isPresent());
        verify(diaryRepository, times(1)).findById(999L);
    }
}

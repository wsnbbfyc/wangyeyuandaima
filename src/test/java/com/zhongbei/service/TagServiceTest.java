package com.zhongbei.service;

import com.zhongbei.model.Tag;
import com.zhongbei.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag testTag;

    @BeforeEach
    void setUp() {
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("测试标签");
        testTag.setColor("#FF5733");
    }

    @Test
    @DisplayName("测试查找所有标签")
    void testFindAll() {
        List<Tag> tags = Arrays.asList(testTag);
        when(tagRepository.findAllByOrderByNameAsc()).thenReturn(tags);

        List<Tag> result = tagService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试标签", result.get(0).getName());
        verify(tagRepository, times(1)).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("测试根据ID查找标签")
    void testFindById() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));

        Tag result = tagService.findById(1L);

        assertNotNull(result);
        assertEquals("测试标签", result.getName());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("测试保存标签")
    void testSave() {
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        Tag result = tagService.save(testTag);

        assertNotNull(result);
        assertEquals("测试标签", result.getName());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("测试删除标签")
    void testDeleteById() {
        doNothing().when(tagRepository).deleteById(1L);

        tagService.deleteById(1L);

        verify(tagRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试检查标签是否存在 - 存在")
    void testExistsByName_True() {
        when(tagRepository.existsByName("测试标签")).thenReturn(true);

        boolean result = tagService.existsByName("测试标签");

        assertTrue(result);
        verify(tagRepository, times(1)).existsByName("测试标签");
    }

    @Test
    @DisplayName("测试检查标签是否存在 - 不存在")
    void testExistsByName_False() {
        when(tagRepository.existsByName("不存在")).thenReturn(false);

        boolean result = tagService.existsByName("不存在");

        assertFalse(result);
        verify(tagRepository, times(1)).existsByName("不存在");
    }

    @Test
    @DisplayName("测试统计标签数量")
    void testCount() {
        when(tagRepository.count()).thenReturn(10L);

        long count = tagService.count();

        assertEquals(10L, count);
        verify(tagRepository, times(1)).count();
    }

    @Test
    @DisplayName("测试根据不存在的ID查找标签返回空")
    void testFindById_NotFound() {
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        Tag result = tagService.findById(999L);

        assertNull(result);
        verify(tagRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("测试创建带颜色的标签")
    void testSaveWithColor() {
        testTag.setColor("#00FF00");
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        Tag result = tagService.save(testTag);

        assertNotNull(result);
        assertEquals("#00FF00", result.getColor());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }
}

package com.zhongbei.repository;

import com.zhongbei.model.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileResourceRepository extends JpaRepository<FileResource, Long> {
    List<FileResource> findAllByOrderByUploadTimeDesc();
}

package com.chumbok.filestorage.domain.repository;

import com.chumbok.filestorage.domain.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Page<File> findAllByCreatedBy(Pageable pageable, String owner);

    Optional<File> findFileByPath(String path);
}

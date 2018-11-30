package com.chumbok.filestorage.service;

import com.chumbok.exception.presentation.ResourceNotFoundException;
import com.chumbok.filestorage.domain.model.File;
import com.chumbok.filestorage.domain.repository.FileRepository;
import com.chumbok.filestorage.dto.response.IdentityResponse;
import com.chumbok.filestorage.dto.response.FileResponse;
import com.chumbok.filestorage.dto.response.FilesResponse;
import com.chumbok.filestorage.exception.IORuntimeException;
import com.chumbok.testable.common.DateUtil;
import com.chumbok.testable.common.FileSystemUtil;
import com.chumbok.testable.common.SlugUtil;
import com.chumbok.testable.common.SystemUtil;
import com.chumbok.testable.common.UuidUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Load and save resources from file system.
 */
@Service
public class FileSystemStorageService implements StorageService {

    /**
     * Directory name where files will be stored under OpenBlog home directory.
     */
    private static final String FILES_DIR = "files";

    /**
     * File resource URL prefix
     */
    private static final String FILE_URL_PREFIX = "files";


    private final ResourceLoader resourceLoader;
    private final FileRepository fileRepository;
    private final DateUtil dateUtil;
    private final UuidUtil uuidUtil;
    private final SystemUtil systemUtil;
    private final FileSystemUtil fileSystemUtil;
    private final SlugUtil slugUtil;


    public FileSystemStorageService(ResourceLoader resourceLoader,
                                    FileRepository fileRepository,
                                    DateUtil dateUtil,
                                    UuidUtil uuidUtil,
                                    SystemUtil systemUtil,
                                    FileSystemUtil fileSystemUtil,
                                    SlugUtil slugUtil) {
        this.resourceLoader = resourceLoader;
        this.fileRepository = fileRepository;
        this.dateUtil = dateUtil;
        this.uuidUtil = uuidUtil;
        this.systemUtil = systemUtil;
        this.fileSystemUtil = fileSystemUtil;
        this.slugUtil = slugUtil;
    }

    public FilesResponse listByPage(Pageable pageable) {

        Page<File> filePage = fileRepository.findAllByCreatedBy(pageable);

        long totalElements = filePage.getTotalElements();
        int totalPage = filePage.getTotalPages();
        int size = filePage.getSize();
        int page = filePage.getNumber();

        List<FileResponse> fileResponseList = new ArrayList<>();
        for (File file : filePage.getContent()) {
            FileResponse fileResponse = new FileResponse();
            fileResponse.setId(file.getId());
            fileResponse.setPath(file.getPath());
            fileResponse.setOriginalName(file.getOriginalName());
            fileResponse.setCreatedAt(file.getCreatedAt());
            fileResponse.setAdditionalProperties(file.getAdditionalProperties());
            fileResponseList.add(fileResponse);
        }

        return FilesResponse.builder()
                .items(fileResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();

    }

    /**
     * Load resource from file system.
     *
     * @param url the URL for the resource.
     * @return the resource handle for the relative resource.
     * @throws FileNotFoundException when file is not exist in file system.
     */
    @Override
    public Resource loadFileAsResource(String url) {

        String filePath = String.format("file:%s/%s/%s", systemUtil.getUserHome(), FILES_DIR, url);

        Resource resource = resourceLoader.getResource(filePath);
        if (resource.exists()) {
            return resource;
        }

        throw new ResourceNotFoundException(String.format("Requested file [%s] not found", url));

    }

    /**
     * Save resource to file system.
     *
     * @param file a MultipartFile.
     * @return identity object of newly created file.
     * @throws IORuntimeException when file can not be written in the file system.
     */
    @Override
    public IdentityResponse store(MultipartFile file) {

        Path uploadPath = Paths.get(String.format("%s/%s/%s", systemUtil.getUserHome(), FILES_DIR,
                dateUtil.getCurrentYearMonthDateString()));

        createUploadDirectoryIfNotExist(uploadPath);

        String newFileName = buildNewFilename(file.getOriginalFilename());

        Path path = Paths.get(uploadPath.toString(), newFileName);

        writeFileInFileSystem(path, readFileBytes(file));

        return new IdentityResponse(String.format("%s/%s/%s",
                FILE_URL_PREFIX,
                dateUtil.getCurrentYearMonthDateString(),
                newFileName)
        );
    }

    private void createUploadDirectoryIfNotExist(Path uploadPath) {
        if (!uploadPath.toFile().exists()) {
            try {
                fileSystemUtil.createDirectories(uploadPath);
            } catch (IOException ex) {
                throw new IORuntimeException("Could not create storage directory.", ex);
            }
        }
    }

    private String buildNewFilename(String originalFilename) {
        String filenameWithoutExt = getFilenameWithoutExtension(originalFilename);
        String sluggedFilenameWithoutExt = slugUtil.toSlug(filenameWithoutExt);
        String fileExt = getFileExtension(originalFilename);
        return uuidUtil.getUuid() + "_" + sluggedFilenameWithoutExt + "." + fileExt;
    }

    private byte[] readFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new IORuntimeException("Could not read file.", ex);
        }
    }

    private void writeFileInFileSystem(Path path, byte[] fileBytes) {
        try {
            fileSystemUtil.write(path, fileBytes);
        } catch (IOException ex) {
            throw new IORuntimeException("Could not write file.", ex);
        }
    }

    private String getFilenameWithoutExtension(String filename) {
        int pos = filename.lastIndexOf('.');
        return pos > 0 ? filename.substring(0, pos) : filename;
    }

    private String getFileExtension(String filename) {
        int pos = filename.lastIndexOf('.');
        return pos > 0 ? filename.substring(pos + 1) : filename;
    }
}

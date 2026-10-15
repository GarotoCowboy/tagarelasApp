package br.com.tagarelas.core;

import java.util.UUID;

import br.com.tagarelas.core.exceptions.DomainValidationException;

public class Attachment {
    private UUID id;
    private String url;
    private String fileName;
    private long fileSize;
    private String contentType;

    public Attachment() {
    }

    public Attachment(String url, String fileName, long fileSize, String contentType) {
        validateContentType(contentType);
        validateFileName(fileName);
        validateUrl(url);
        validatefileSize(fileSize);

        this.id = UUID.randomUUID();
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public Attachment(UUID id, String url, String fileName, long fileSize, String contentType) {
        this(url, fileName, fileSize, contentType);
        this.id = id;
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new DomainValidationException("Attachment URL cannot be null or blank");
        }
    }

    private void validateContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new DomainValidationException("Attachment ContentType cannot be null or blank");
        }
    }

    private void validatefileSize(long fileSize) {
        if (fileSize <= 0) {
            throw new DomainValidationException("Attachment fileSize must be greater than 0");
        }
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new DomainValidationException("Attachment fileName cannot be null or blank");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }
}
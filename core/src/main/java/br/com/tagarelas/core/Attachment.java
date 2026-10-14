package br.com.tagarelas.core;

import java.util.UUID;

public class Attachment {
    private UUID id;
    private String url;
    private String fileName;
    private long fileSize;
    private String contentType;


    


    public Attachment() {
    }

    
    public Attachment(String url, String fileName, long fileSize, String contentType) throws Exception {
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        validateContentType(contentType);
        validateFileName(fileName);
        validateUrl(url);
        validatefileSize(fileSize);
    }

    


    public Attachment(UUID id, String url, String fileName, long fileSize, String contentType) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    private void validateUrl(String url) throws Exception{
        if (url == null || url.isBlank()){
            throw new Exception("Url cannot be null");
        }
    }
      private void validateContentType(String contentType) throws Exception{
        if (contentType == null || contentType.isBlank()){
            throw new Exception("ContentType cannot be null");
        }
    }

          private void validatefileSize(long fileSize) throws Exception{
        if (fileSize <= 0){
            throw new Exception("Filesize cannot be equal or under than 0");
        }
    }

      private void validateFileName(String fileName) throws Exception{
        if (fileName == null || fileName.isBlank()){
            throw new Exception("File Name cannot be null");
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

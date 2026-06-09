package com.snapora.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {
    @NotBlank @Size(max = 1000) private String content;
    private Long parentId;

    public String getContent() { return content; }
    public void setContent(String c) { this.content = c; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long p) { this.parentId = p; }
}

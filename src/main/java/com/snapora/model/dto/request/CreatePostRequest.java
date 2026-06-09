package com.snapora.model.dto.request;

import jakarta.validation.constraints.Size;

public class CreatePostRequest {
    @Size(max = 2200) private String caption;
    @Size(max = 100) private String location;

    public String getCaption() { return caption; }
    public void setCaption(String c) { this.caption = c; }
    public String getLocation() { return location; }
    public void setLocation(String l) { this.location = l; }
}

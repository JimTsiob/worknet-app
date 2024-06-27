package com.syrtsiob.worknet.model;

import java.io.Serializable;

public class SmallCustomFileDTO implements Serializable {

    private Long id;
    private String fileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

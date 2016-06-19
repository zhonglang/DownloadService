package com.lang.downloaddemo.com.lang.entities;

import java.io.Serializable;

/**
 * 文件实体类
 */
public class FileInfo implements Serializable{
    private int id;
    private int finished;
    private int length;
    private String fileName;
    private String url;



    public FileInfo() {

    }

    public FileInfo(int id,String url,String fileName,int length,int finished) {
        this.id = id;
        this.url=url;
        this.fileName=fileName;
        this.length=length;
        this.finished=finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", finished=" + finished +
                ", length=" + length +
                ", fileName='" + fileName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getUrl() {
        return url;

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {

        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {

        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {

        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

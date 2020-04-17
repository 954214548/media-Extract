package lm.com.audioextract.model;

public class VideoModel {

    private String  name;
    private String  url;
    private long  duation;
    private long    size;
    private boolean action = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDuation() {
        return duation;
    }

    public void setDuation(long duation) {
        this.duation = duation;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }
}

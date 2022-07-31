package entity;

public class ResultString {

    // 翻译后
    private String dst;
    // 原来的
    private String src;

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public String toString() {
        return "entity.ResultString{" +
                "dst='" + dst + '\'' +
                ", src='" + src + '\'' +
                '}';
    }
}
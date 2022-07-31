package entity;

public class Info{
    // 翻译前的字符串
    private String info1;
    // 翻译后的字符串
    private String info2;
    // 开始下标
    private int start;
    // 结束下标
    private int end;
    public void setInfo1(String info1) {
        this.info1 = info1;
    }
    public String getInfo1() {
        return info1;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getStart() {
        return start;
    }
    public void setEnd(int end) {
        this.end = end;
    }
    public int getEnd() {
        return end;
    }
    public void setInfo2(String info2) {
        this.info2 = info2;
    }
    public String getInfo2() {
        return info2;
    }


}


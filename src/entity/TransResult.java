package entity;

import java.util.Arrays;

// 实体类
public class TransResult {
    private ResultString[] trans_result;
    private String from;
    private String to;

    public ResultString[] getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(ResultString[] trans_result) {
        this.trans_result = trans_result;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "entity.TransResult{" +
                "trans_result=" + Arrays.toString(trans_result) +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}

package entity;

/**
 *   根据API文档，查看返回的内容
 *   "code": 0,
 *   "flag": true,
 *   "message": "string",
 *   "data":
 * */
public class Result {
    private Boolean flag;   //是否成功
    private Integer code;   //返回码
    private String message; //返回信息
    private Object data;    //返回数据

    //无参构造
    public Result() {
    }

    //只有前三种参数的构造
    public Result(Boolean flag, Integer code,  String message) {
        this.code = code;
        this.flag = flag;
        this.message = message;
    }

    //包含所有参数的构造
    public Result(Boolean flag, Integer code,  String message, Object data) {
        this.code = code;
        this.flag = flag;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "flag=" + flag +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}


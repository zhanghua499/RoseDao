package rose.dao;

public class Identity {
    @SuppressWarnings("unused")
	private static final long serialVersionUID = 1250164545871014812L;

    // 返回的对象 ID.
    protected Number number;

    /**
     * 构造对象容纳返回的对象 ID.
     * 
     * @param number - 返回的对象 ID
     */
    public Identity(Number number) {
        this.number = number;
    }

    
    public int intValue() {
        return number.intValue();
    }

    
    public long longValue() {
        return number.longValue();
    }

    
    public float floatValue() {
        return number.floatValue();
    }

    
    public double doubleValue() {
        return number.doubleValue();
    }

    
    public String toString() {
        return Long.toString(number.longValue());
    }
}

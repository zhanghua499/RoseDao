package rose.dao.interpreter;

public interface SQLInterpreterResult {
    String getSQL();
    Object[] getParameters();
}

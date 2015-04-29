package rose.dao.provider;

public interface DaoProvider {
	public Dao createDataAccess(Class<?> daoClass);
}

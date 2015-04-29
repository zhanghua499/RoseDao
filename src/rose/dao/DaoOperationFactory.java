package rose.dao;

import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;

public interface DaoOperationFactory {
	public DaoOperation getDaoOperation(Dao dao, DaoDescription desc);
}

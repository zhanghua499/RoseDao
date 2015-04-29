package rose.dao;

import java.util.Map;

import rose.dao.provider.DaoDescription;

public interface DaoOperation {
	public DaoDescription getDesc();
	public Object execute(Map<String, Object> parameters);
}

package rose.dao;

import rose.dao.provider.DaoDescription;

import org.springframework.jdbc.core.RowMapper;

public interface RowMapperFactory {
	public RowMapper getRowMapper(DaoDescription desc);
}

package rose.dao.mapper;

import java.util.ArrayList;
import java.util.Collection;

import rose.dao.provider.DaoDescription;

public class ListRowMapper extends AbstractCollectionRowMapper {

    public ListRowMapper(DaoDescription desc) {
        super(desc);
    }

	@Override
    protected Collection<Object> createCollection(int columnSize) {
        return new ArrayList<Object>(columnSize);
    }
}

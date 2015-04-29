package rose.dao.mapper;

import java.util.Collection;
import java.util.HashSet;

import rose.dao.provider.DaoDescription;

public class SetRowMapper extends AbstractCollectionRowMapper {

    public SetRowMapper(DaoDescription desc) {
        super(desc);
    }

    @Override
    protected Collection<Object> createCollection(int columnSize) {
        return new HashSet<Object>(columnSize * 2);
    }
}

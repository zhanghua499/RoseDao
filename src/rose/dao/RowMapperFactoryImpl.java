package rose.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rose.dao.annotation.RowHandler;
import rose.dao.BeanPropertyRowMapper;
import rose.dao.TypeUtils;
import rose.dao.mapper.ArrayRowMapper;
import rose.dao.mapper.ListRowMapper;
import rose.dao.mapper.MapEntryRowMapper;
import rose.dao.mapper.SetRowMapper;

import org.apache.commons.lang.ClassUtils;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import rose.dao.provider.DaoDescription;

public class RowMapperFactoryImpl implements RowMapperFactory {

	private Map<String, RowMapper> rowMappers = new HashMap<String, RowMapper>();
	
	public RowMapper getRowMapper(DaoDescription desc) {
        RowHandler rowHandler = desc.getAnnotation(RowHandler.class);
        if (rowHandler != null) {
            if (rowHandler.rowMapper() != RowHandler.ByDefault.class) {
                try {
                    RowMapper rowMapper = rowHandler.rowMapper().newInstance();
                    return rowMapper;
                } catch (Exception ex) {
                    throw new BeanInstantiationException(rowHandler.rowMapper(), ex.getMessage(),
                            ex);
                }
            }
        }
        //

        Class<?> returnClassType = desc.getReturnType();
        Class<?> rowType = getRowType(desc);

        // BUGFIX: SingleColumnRowMapper 处理  Primitive Type 抛异�?
        if (rowType.isPrimitive()) {
            rowType = ClassUtils.primitiveToWrapper(rowType);
        }

        // 根据类型创建  RowMapper
        RowMapper rowMapper;

        // 返回单列的查询的(或�?返回只有2列的Map类型查询�?
        if (TypeUtils.isColumnType(rowType)) {
        	rowMapper = new SingleColumnRowMapper(rowType);
        }
        // 返回多列的，用Bean对象、集合�?映射、数组来表示每一行的
        else {
            if (rowType == Map.class) {
                rowMapper = new ColumnMapRowMapper();
            } else if (rowType.isArray()) {
                rowMapper = new ArrayRowMapper(rowType);
            } else if ((rowType == List.class) || (rowType == Collection.class)) {
                rowMapper = new ListRowMapper(desc);
            } else if (rowType == Set.class) {
                rowMapper = new SetRowMapper(desc);
            } else {
                boolean checkColumns = (rowHandler == null) ? true : rowHandler.checkColumns();
                boolean checkProperties = (rowHandler == null) ? false : rowHandler
                        .checkProperties();
                String key = rowType.getName() + "[checkColumns=" + checkColumns
                        + "&checkProperties=" + checkProperties + "]";
                rowMapper = rowMappers.get(key);
                if (rowMapper == null) {
                    rowMapper = new BeanPropertyRowMapper(rowType, checkColumns, checkProperties);
                    rowMappers.put(key, rowMapper);
                }
            }
            // 如果DAO方法�?��返回的是Map，rowMapper要返回Map.Entry对象
            if (returnClassType == Map.class) {
                rowMapper = new MapEntryRowMapper(desc, rowMapper);
            }
        }

        return rowMapper;
	}
	//////////////////////////////help//////////////////////////////////
    // 获得返回的集合元素类�?
    private static Class<?> getRowType(DaoDescription desc) {
        Class<?> returnClassType = desc.getReturnType();
        if (Collection.class.isAssignableFrom(returnClassType)) {
            return getRowTypeFromCollectionType(desc, returnClassType);
        } else if (Map.class == returnClassType) {
            return getRowTypeFromMapType(desc, returnClassType);
        } else if (returnClassType.isArray() && returnClassType != byte[].class) {
            // 数组类型, 支持多重数组
            return returnClassType.getComponentType();
        }

        // 此时代表整个DAO方法只关心结果集第一�?
        return returnClassType;
    }

    private static Class<?> getRowTypeFromMapType(DaoDescription desc, Class<?> returnClassType) {
        Class<?> rowType;
        // 获取  Map<K, V> 值元素类�?
        Class<?>[] genericTypes = desc.getGenericReturnTypes();
        if (genericTypes.length != 2) {
            throw new IllegalArgumentException("the returned generic type '"
                    + returnClassType.getName() + "' should has two actual type parameters.");
        }
        rowType = genericTypes[1]; // �? V 类型
        return rowType;
    }

    private static Class<?> getRowTypeFromCollectionType(DaoDescription desc, Class<?> returnClassType) {
        Class<?> rowType;
        // 仅支�? List / Collection / Set
        if ((returnClassType != List.class) && (returnClassType != Collection.class)
                && (returnClassType != Set.class)) {
            throw new IllegalArgumentException("error collection type " + returnClassType.getName()
                    + "; only support List, Set, Collection");
        }
        // 获取集合元素类型
        Class<?>[] genericTypes = desc.getGenericReturnTypes();
        if (genericTypes.length != 1) {
            throw new IllegalArgumentException("the returned generic type '"
                    + returnClassType.getName() + "' should has a actual type parameter.");
        }
        rowType = genericTypes[0];
        return rowType;
    }
}

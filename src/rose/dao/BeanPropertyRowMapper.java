package rose.dao;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

public class BeanPropertyRowMapper implements RowMapper {

    private final Class<?> mappedClass;

    private Map<String, PropertyDescriptor> mappedFields;

    private final boolean checkColumns;

    private final boolean checkProperties;

    private Set<String> mappedProperties = new HashSet<String>();

    public BeanPropertyRowMapper(Class<?> mappedClass, boolean checkColumns, boolean checkProperties) {
        this.mappedClass = mappedClass;
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        this.checkProperties = checkProperties;
        this.checkColumns = checkColumns;
        initialize();
    }

    protected void initialize() {
        this.mappedFields = new HashMap<String, PropertyDescriptor>();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            if (pd.getWriteMethod() != null) {
                this.mappedProperties.add(pd.getName());
                this.mappedFields.put(pd.getName().toLowerCase(), pd);
                for (String underscoredName : underscoreName(pd.getName())) {
                    if (!pd.getName().toLowerCase().equals(underscoredName)) {
                        this.mappedFields.put(underscoredName, pd);
                    }
                }
            }
        }
    }

    private String[] underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            result.append(name.substring(0, 1).toLowerCase());
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if (s.equals(s.toUpperCase())) {
                    result.append("_");
                    result.append(s.toLowerCase());
                } else {
                    result.append(s);
                }
            }
        }
        return new String[] { result.toString() };
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
    	Object mappedObject = instantiateClass(this.mappedClass);
        BeanWrapper bw = new BeanWrapperImpl(mappedObject);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        
        Set<String> populatedProperties = (checkProperties ? new HashSet<String>() : null);

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index).toLowerCase();
            PropertyDescriptor pd = this.mappedFields.get(column);
            if (pd != null) {
                try {
                    Object value = JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
                    bw.setPropertyValue(pd.getName(), value);
                    if (populatedProperties != null) {
                        populatedProperties.add(pd.getName());
                    }
                } catch (NotWritablePropertyException ex) {
                    throw new DataRetrievalFailureException("Unable to map column " + column
                            + " to property " + pd.getName(), ex);
                }
            } else {
            	if (Map.class.isAssignableFrom(mappedObject.getClass())){
            		Object value = JdbcUtils.getResultSetValue(rs, index);
            		((Map)mappedObject).put(column, value);
            	}
            	else if (checkColumns) {
                    throw new InvalidDataAccessApiUsageException("Unable to map column '" + column
                            + "' to any properties of bean " + this.mappedClass.getName());
                }
            }
        }

        if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
            throw new InvalidDataAccessApiUsageException(
                    "Given ResultSet does not contain all fields "
                            + "necessary to populate object of class [" + this.mappedClass + "]: "
                            + this.mappedProperties);
        }

        return mappedObject;
    }

    private static Object instantiateClass(Class<?> clazz) throws BeanInstantiationException {

        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            throw new BeanInstantiationException(clazz, ex.getMessage(), ex);
        }
    }

}

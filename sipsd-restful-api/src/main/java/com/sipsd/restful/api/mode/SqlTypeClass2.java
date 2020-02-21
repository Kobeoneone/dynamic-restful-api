package com.sipsd.restful.api.mode;


public class SqlTypeClass2 {

    /**
     * Translates a data type from an integer (java.sql.value) to a string
     * that represents the corresponding class.
     *
     * REFER: https://www.cis.upenn.edu/~bcpierce/courses/629/jdkdocs/guide/jdbc/getstart/mapping.doc.html
     *
     * @param type
     *            The java.sql.value to convert to its corresponding class.
     * @return The class that corresponds to the given java.sql.Types
     *         value, or Object.class if the type has no known mapping.
     */
    public static Class<?> toClass(String type) {
        Class<?> result = Object.class;
        type = type.toUpperCase();

        switch (type) {
            case "CHAR":
            case "VARCHAR":
            case "LONGVARCHAR":
            case "TEXT":
                result = String.class;
                break;

            case "NUMERIC":
            case "DECIMAL":
                result = java.math.BigDecimal.class;
                break;

            case "BIT":
                result = Boolean.class;
                break;

            case "TINYINT":
                result = Byte.class;
                break;

            case "SMALLINT":
                result = Short.class;
                break;

            case "INTEGER":
            case "INT":
                result = Integer.class;
                break;

            case "BIGINT":
                result = Long.class;
                break;

            case "REAL":
            case "FLOAT":
                // result = Float.class;
                result = Double.class;
                break;

            case "DOUBLE":
                result = Double.class;
                break;

            case "BINARY":
            case "VARBINARY":
            case "LONGVARBINARY":
                result = Byte[].class;
                break;

            case "DATE":
                result = java.sql.Date.class;
                break;

            case "TIME":
                result = java.sql.Time.class;
                break;

            case "TIMESTAMP":
            case "DATETIME":
                result = java.sql.Timestamp.class;
                break;

            case "BLOB":
                result = Byte[].class;
                break;
        }

        return result;
    }


}

package info.puzz.a10000sentences.utils;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import info.puzz.a10000sentences.models.Sentence;

public class SqlFilterUtils {
    private SqlFilterUtils() throws Exception {
        throw new Exception();
    }

    public static String prepareLikeFilter(String text) {
        StringBuilder likeFilter = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c) || Character.isSpaceChar(c)) {
                likeFilter.append(c);
            } else {
                likeFilter.append(' ');
            }
        }
        return likeFilter.toString();
    }

    /**
     * Important, make sure that all previous complex SQL expressions are in braces!
     */
    public static void addFilter(From sql, String[] columns, String filter) {
        filter = prepareLikeFilter(filter);
        String whereExpression = new String();
        String[] args = new String[columns.length * 2];
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            if (whereExpression.length() > 0) {
                whereExpression += " or ";
            }
            whereExpression += String.format(" %s like ? or %s like ? ", column, column);
            args[i * 2] = filter + "%";
            args[i * 2 + 1] = "% " + filter + "%";
        }
        sql.and("(" + whereExpression + ")", args);
    }
}

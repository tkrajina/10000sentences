package info.puzz.a10000sentences.apimodels;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class CourseVO {
    String learningLangId;
    String knownLangId;
}

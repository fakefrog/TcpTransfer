package util.testBean;

import lombok.Data;
import util.EncodeOrderAttribute;

/**
 * <p>Package:util.testBean</p>
 * <p>Description: </p>
 * <p>Company: com.lowan</p>
 *
 * @author wjj
 * @date 2018/9/13 9:12
 */
@Data
public class BasePersonBean {

    @EncodeOrderAttribute(level = 1, order = 1, size = 1)
    private Integer age;

    @EncodeOrderAttribute(level = 1, order = 2, size = 1)
    private Integer sex;

}

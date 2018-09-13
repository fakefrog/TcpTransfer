package util.testBean;

import lombok.Data;
import util.EncodeOrderAttribute;

/**
 * <p>Package:util.testBean</p>
 * <p>Description: </p>
 * <p>Company: com.lowan</p>
 *
 * @author wjj
 * @date 2018/9/13 9:17
 */
@Data
public class StudentBean {

    @EncodeOrderAttribute(level = 1,order = 1,size = 1)
    private Integer age;

    @EncodeOrderAttribute(level = 1,order = 2,size = 4)
    private Integer money;

    @EncodeOrderAttribute(level = 1,order = 3,size = 2)
    private Integer friends;

}

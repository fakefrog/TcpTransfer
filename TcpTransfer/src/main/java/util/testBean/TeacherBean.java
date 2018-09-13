package util.testBean;


import java.util.ArrayList;

import lombok.Data;
import util.EncodeOrderAttribute;

/**
 * <p>Package:util.testBean</p>
 * <p>Description: </p>
 * <p>Company: com.lowan</p>
 *
 * @author wjj
 * @date 2018/9/13 9:14
 */
@Data
public class TeacherBean extends BasePersonBean {

    @EncodeOrderAttribute(level = 2,order = 1,size = 4)
    private Integer studentCounts;

    @EncodeOrderAttribute(level = 2,order = 2,size = 4)
    private ArrayList<StudentBean> studentlist;

    @EncodeOrderAttribute(level = 2,order = 3,size = 1)
    private Integer teachingAge;

    @EncodeOrderAttribute(level = 2,order = 4,size = 100)
    private byte[] bytes;

}

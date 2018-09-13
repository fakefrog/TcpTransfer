package util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Package:com.lowan.tcp.bean.clientMsgBean</p>
 * <p>Description: </p>
 * <p>Company: com.lowan</p>
 *
 * @author wjj
 * @date 2018/9/12 13:59
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EncodeOrderAttribute {
    int level();
    int order();
    int size();
}

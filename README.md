# TcpTransfer
transfer object (with annotation ) to tcp byte array   
将带有注解的对象转换为tcp协议传输的byte数组

@Retention(RetentionPolicy.RUNTIME)
public @interface EncodeOrderAttribute {
    int level();
    int order();
    int size();
}

注解意义:
level为层级,默认为最高3级,继承的时候父类level为1,子类为2,子子类为3.
order为当前层级顺序,1为最优先,-1为优先级最低
顺序算法为高层级包裹底层级,
解析A类,假如A类继承自B,那么B类的order为正的字段按升序排列,再迭代考虑A类.再考虑B类Order为负字段,-1为最后,-2为倒数第二.
支持一个类最多约100个字段
size为此int转换为的字节数


对于下面示例:
@Data
public class BasePersonBean {

    @EncodeOrderAttribute(level = 1, order = 1, size = 1)
    private Integer age;

    @EncodeOrderAttribute(level = 1, order = 2, size = 1)
    private Integer sex;

}


@Data
public class TeacherBean extends BasePersonBean {

    @EncodeOrderAttribute(level = 2,order = 1,size = 4)
    private Integer studentCounts;

    @EncodeOrderAttribute(level = 2,order = 2,size = 4)
    private ArrayList<StudentBean> studentlist;

    @EncodeOrderAttribute(level = 2,order = 3,size = 1)
    private Integer teachingAge;

}

@Data
public class StudentBean {

    @EncodeOrderAttribute(level = 1,order = 1,size = 1)
    private Integer age;

    @EncodeOrderAttribute(level = 1,order = 2,size = 4)
    private Integer money;

    @EncodeOrderAttribute(level = 1,order = 3,size = 2)
    private Integer friends;

}


        StudentBean s1 = new StudentBean();
        s1.setAge(20);
        s1.setFriends(380);
        s1.setMoney(1577878);
        StudentBean s2 = new StudentBean();
        s2.setAge(20);
        s2.setFriends(380);
        s2.setMoney(1577878);
        StudentBean s3 = new StudentBean();
        s3.setAge(20);
        s3.setFriends(380);
        s3.setMoney(1577878);
        TeacherBean teacherBean = new TeacherBean();
        teacherBean.setTeachingAge(30);
        teacherBean.setStudentCounts(3);
        ArrayList<StudentBean> studentList = new ArrayList<>();
        studentList.add(s1);
        studentList.add(s2);
        studentList.add(s3);
        teacherBean.setStudentlist(studentList);
        teacherBean.setSex(1);
        teacherBean.setAge(50);

        byte[] bytes = ObjectToTcpBytes(teacherBean, teacherBean.getClass());
        System.out.println(Arrays.toString(bytes));
        
 解析teacherBean对象,结果为
[50, 1, 0, 0, 0, 3, 20, 0, 24, 19, -106, 1, 124, 20, 0, 24, 19, -106, 1, 124, 20, 0, 24, 19, -106, 1, 124, 30]
50 -> age(BasePersonBean)
1 -> sex(BasePersonBean)
0,0,0,3 -> teacherBean.studentCounts
20-> teacherBean.studentlist[0].age
0, 24, 19, -106 ->  teacherBean.studentlist[0].money
1, 124, ->teacherBean.studentlist[0].friends
...
...
...
字节序和字节值没有差错


v1.0
基础功能实现,一些核验并未做,处于一个demo阶段,可以进行初步使用(可能会有BUG),后期功能完善优化,
开发经验较弱,如有幸请指点

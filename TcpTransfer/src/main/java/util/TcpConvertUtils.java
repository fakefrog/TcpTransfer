package util;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import lombok.Data;
import util.testBean.StudentBean;
import util.testBean.TeacherBean;

public class TcpConvertUtils {


    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(PackageUtils.class);

    public static byte[] ObjectToTcpBytes(Object object, Class clazz) throws IllegalAccessException {
        ArrayList<FieldInfo> infos = map.get(clazz);
        ArrayList<byte[]> bytesList = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++) {
            FieldInfo fieldInfo = infos.get(i);
            int size = fieldInfo.getSize();
            Field field = fieldInfo.getField();
            Object fetchObj = field.get(object);
            String typeName = fieldInfo.getTypeName();
            byte[] bytes = null;
            switch (typeName) {
                case "Integer":
                    Integer value = (Integer) fetchObj;
                    bytes = intToBytes(value, size);
                    bytesList.add(bytes);
                    break;
                case "ArrayList":
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Class actualClz = (Class) parameterizedType.getActualTypeArguments()[0];
                    ArrayList listObject = (ArrayList) fetchObj;
                    int listSize = listObject.size();
                    for (int index = 0; index < listSize; index++) {
                        Object listO = listObject.get(index);
                        bytes = ObjectToTcpBytes(listO, actualClz);
                        bytesList.add(bytes);
                    }
                    break;
                case "Byte[]":
                    bytes = (byte[])fetchObj;
                    bytesList.add(bytes);
                    break;
                default:
                    Class<?> objectClass = field.getType();
                    bytes = ObjectToTcpBytes(fetchObj, objectClass);
                    bytesList.add(bytes);
                    break;
            }
        }
        int totalLen = 0;
        for (byte[] bytes : bytesList) {
            totalLen += bytes.length;
        }
        byte[] result = new byte[totalLen];
        int index = 0;
        for (byte[] bytes : bytesList) {
            System.arraycopy(bytes, 0, result, index, bytes.length);
            index += bytes.length;
        }
        return result;
    }

    public static byte[] intToBytes(Integer in, Integer size) {
        Integer transferIn = in;
        byte[] bytes = new byte[size];
        int index = 0;
        while (true) {
            if (index == size) {
                break;
            }
            Integer i = transferIn / (1 << (8 * (size - 1 - index)));
            transferIn = transferIn % (1 << (8 * (size - 1 - index)));
            bytes[index] = i.byteValue();
            index++;
        }
        return bytes;
    }

    public static void main(String[] args) throws IllegalAccessException, IOException {
        loadMessage("util.testBean");

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
    }

    private static HashMap<Class, ArrayList<FieldInfo>> map = new HashMap<>();

    public static void loadMessage(String packageName) throws IOException {
        if (StringUtils.isEmpty(packageName)) {
            return;
        }
        String[] list = PackageUtils.getResourceInPackage(packageName);
        logger.info("--------------注入Encode协议，开始------------");
        for (String className : list) {
            try {
                if (!className.endsWith(".class")) {
                    continue;
                }
                className = className.substring(0, className.length() - 6);
                Class<?> clz = Class.forName(className);
                Field[] fields = clz.getDeclaredFields();
                ArrayList<FieldInfo> fieldInfos = new ArrayList<>();
                for (Field field : fields) {
                    FieldInfo fieldInfo = new FieldInfo();
                    field.setAccessible(true);
                    EncodeOrderAttribute encodeOrderAttribute = field.getAnnotation(EncodeOrderAttribute.class);
                    if (encodeOrderAttribute == null) {
                        continue;
                    }
                    int level = encodeOrderAttribute.level();
                    int size = encodeOrderAttribute.size();
                    int order = encodeOrderAttribute.order();
                    fieldInfo.setOrder(order);
                    fieldInfo.setLevel(level);
                    fieldInfo.setSize(size);
                    fieldInfo.setField(field);
                    if (field.getType().equals(Integer.class)) {
                        fieldInfo.setTypeName("Integer");
                    } else if (field.getType().equals(ArrayList.class)) {
                        fieldInfo.setTypeName("ArrayList");
                    } else if (field.getType().equals(byte[].class)) {
                        fieldInfo.setTypeName("Byte[]");
                    } else {
                        fieldInfo.setTypeName("Object");
                    }
                    fieldInfo.setClz(clz);
                    fieldInfos.add(fieldInfo);
                }
                getSuperFields(fieldInfos, clz);
                Collections.sort(fieldInfos);
                map.put(clz, fieldInfos);
            } catch (Exception e) {
                logger.error("--------------注入Encode协议，失败------------{}", e);
            }
        }
        logger.info("--------------注入Encode协议，结束------------");
    }

    private static void getSuperFields(ArrayList<FieldInfo> fieldInfos, Class<?> childClass) {
        Class<?> superclass = childClass.getSuperclass();
        if (superclass == null) {
            return;
        }
        if (!superclass.equals(Object.class)) {
            Field[] declaredFields = superclass.getDeclaredFields();
            for (Field field : declaredFields) {
                FieldInfo fieldInfo = new FieldInfo();
                field.setAccessible(true);
                EncodeOrderAttribute encodeOrderAttribute = field.getAnnotation(EncodeOrderAttribute.class);
                if (encodeOrderAttribute == null) {
                    continue;
                }
                int level = encodeOrderAttribute.level();
                int size = encodeOrderAttribute.size();
                int order = encodeOrderAttribute.order();
                fieldInfo.setOrder(order);
                fieldInfo.setLevel(level);
                fieldInfo.setSize(size);
                fieldInfo.setField(field);
                if (field.getType().equals(Integer.class)) {
                    fieldInfo.setTypeName("Integer");
                } else if (field.getType().equals(ArrayList.class)) {
                    fieldInfo.setTypeName("ArrayList");
                } else if (field.getType().equals(byte[].class)) {
                    fieldInfo.setTypeName("Byte[]");
                } else {
                    fieldInfo.setTypeName("Object");
                }
                fieldInfo.setClz(superclass);
                fieldInfos.add(fieldInfo);
            }
            getSuperFields(fieldInfos, superclass);
        }
    }

    @Data
    static class FieldInfo implements Comparable<FieldInfo> {
        private int level;
        private int order;
        private int size;
        private Field field;
        private Class clz;
        private String typeName;
        static final int MAX_LEVEL = 3;
        static final int LEVEL_CONTAINS = 100;

        @Override
        public int compareTo(FieldInfo o) {
            int level = this.level;
            int order = this.order;
            int weight = levelValue(level) + (order + levelValue(level)) % levelValue(level);
            int levelTo = o.getLevel();
            int orderTo = o.getOrder();
            int weightTo = levelValue(levelTo) + (orderTo + levelValue(levelTo)) % levelValue(levelTo);
            return weight - weightTo;
        }

        private int levelValue(int level) {
            if (level > MAX_LEVEL) {
                throw new RuntimeException();
            }
            double levelContain = LEVEL_CONTAINS;
            int baseValue = 0;
            for (int i = 1; i <= level; i++) {
                Double value = Math.pow(levelContain, MAX_LEVEL + 1 - i);
                int intValue = value.intValue();
                baseValue += intValue;
            }

            return baseValue;
        }
    }
}


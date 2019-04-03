package com.pepper.boot.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClassNameTest {

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException {

       /* System.out.println(String.class.getName());
        System.out.println(String.class.getSimpleName());
        System.out.println(String.class.getTypeName());
        Class type = String.class;
        System.out.println(String.class == type);*/


        List<User> list = new ArrayList<>();

        User user2 = new User(18,"tom");
        list.add(user2);
        list.add(new User(20,"jack"));

        String str = JSON.toJSONString(list);

        String str2 = JSON.toJSONString(user2);

        System.out.println(str);
        System.out.println(str2);

        Class clazz = ClassNameTest.class;
        Method method = clazz.getDeclaredMethod("getUsers");

        Method method1 = clazz.getDeclaredMethod("getUser");

        Type resultType1 = method1.getGenericReturnType();

        Type[] resultArgType = null;

        Type resultType = method.getGenericReturnType();
        if (resultType instanceof ParameterizedType
                && ((ParameterizedType) resultType).getRawType() == java.util.List.class){
            resultArgType = ParameterizedType.class.cast(resultType).getActualTypeArguments();
        }

        Object object = JSONObject.parse(str);

        if(object instanceof JSONObject){
            System.out.println("JSONObject");
        }else if(object instanceof JSONArray){
            System.out.println("JSONArray");
        }

        User temp = JSON.parseObject(str2,resultType1);

        List list2 = JSONObject.parseArray(str, Class.forName(resultArgType[0].getTypeName()));


        System.out.println(list2);

        System.out.println(temp);

    }


    public List<User> getUsers(){
        return null;
    }


    public User getUser(){
        return null;
    }



}

class User{
    private int age;

    private String name;

    public User(){

    }

    public User(int age,String name){
        this.age = age;
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

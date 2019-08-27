import zhc.java.com.zhc.manager.TestProxy;
import zhc.java.com.zhc.manager.impl.TestOtherProxyImpl;
import zhc.java.com.zhc.manager.impl.TestProxyImpl;
import zhc.java.com.zhc.manager.proxy.SubjectProxyImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

//        shuiXianHua();

//        jiuJiuChenFa();

//        Thread test1 = new Thread(new threadTest1());
//        Thread test2 = new Thread(new threadTest2());
//        Thread test3 = new Thread(new threadTest3());
//
//        test1.run();
//        test2.run();
//        test3.run();
//
//        test1.start();
//        test2.start();
//        test3.start();

//        List<String> list = new ArrayList<>();
//
//        List<String> list1 = new ArrayList<>();
//       System.out.println(list1.contains("1"));


//        list.addAll(list1);
//
//        System.out.println(list.size());
//        for (String s : list){
//            System.out.println(s);
//        }


//        List<String> l = new ArrayList<>();
//        l.add("1");
//        l.add("2");
//        l.add("3");
//        l.add("1");
//
//        Iterator i = l.iterator();
//        while(i.hasNext()){
//            if(i.equals(i.next())){
//                System.out.println("123123");
//            }else{
//                System.out.println("222");
//            }
//        }

        /**
         * 2019-06-17
         */
//        doubleCastInt(1.2);

//        subStringTest();

        /**
         * 2019-06-19
         */
//        listOperationDemo();

        // 2019-07-16 代理模式demo
//        TestProxy testProxy = new SubjectProxyImpl(new TestOtherProxyImpl());
//        testProxy.test();


        // 2019-08-01 验证null的list在for循环中是否报错
       /* try {
            List<String> listString = null;
            for (String s : listString) {
                System.out.println("123");
            }
            System.out.println("no exception");
        }catch (Exception e){
            System.out.println("have exception");
        }finally {
            System.out.println("finally");
        }*/


       // 2019-08-12 文件流
        try {

            File file = new File("D:/1/a.txt");
            File f = new File("D:/1/b.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            if(f.exists()){
                f.renameTo(new File("D:/1/c.txt"));
            }

            FileInputStream fis = new FileInputStream("D:/1/a.txt");
            FileOutputStream fos = new FileOutputStream("D:/1/b.txt");

            int len = 0;
            byte[] b = new byte[1024];
            while((len = fis.read(b)) != -1){
                fos.write(b, 0, len);
            }

        }catch (FileNotFoundException fe){
            fe.printStackTrace();
        }catch (IOException ie){
            ie.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 水仙花数
     */
    public static void shuiXianHua(){

        for (int n = 100; n < 1000; n++){

            // 百位数
            int a = (n - n % 100) / 100;
            // 十位数
            int b = (n % 100 - n % 10) / 10;
            // 个位数
            int c = n % 10;

            if(a * a * a + b * b * b + c * c * c == n){
                System.out.println(n);
            }
        }
    }

    /**
     * 九九乘法表
     */
    public static void jiuJiuChenFa(){

        for(int n = 1; n < 10; n++){
            for(int m = 1; m <= n; m++){
                System.out.print(m + "*" + n + "=" + n * m + "\t");
            }
            System.out.println();
        }
    }

    /**
     * 多线程
     */
    static class threadTest1 extends Thread {
        public void run(){
            System.out.println("test1");
        }
    }

    static class threadTest2 implements Runnable{
        @Override
        public void run() {
            System.out.println("test2");
        }
    }
    static class threadTest3 implements Runnable{
        @Override
        public void run() {
            System.out.println("test3");
        }
    }


    public static void doubleCastInt(double m){
//        double m = 0.0;

        int n = (int) m;

        System.out.println(n);
    }

    public static void subStringTest(){
        String m = "123456789000";

        String n = "1";

        System.out.println(n.length());

        System.out.println(n.substring(n.length()-1));
    }

    /**
     * list java8中的操作
     */
    public static void listOperationDemo(){
        List<String> list = Arrays.asList("A","B","A","C","S");

        List<String> newList = list.stream().distinct().collect(Collectors.toList());

//        newList.forEach(s -> {System.out.println("newList" + s);});
//        list.forEach(s -> {System.out.println("list" + s);});

//        User user1 = new User();
//        user1.setName("A");
//        user1.setPassword("123");
//        user1.setSex("1");
//        user1.setAge("21");
//
//        User user2 = new User();
//        user2.setName("B");
//        user2.setPassword("1234");
//        user2.setSex("1");
//        user2.setAge("22");
//
//        User user3 = new User();
//        user3.setName("C");
//        user3.setPassword("12345");
//        user3.setSex("2");
//        user3.setAge("23");
//
//        User user4 = new User();
//        user4.setName("D");
//        user4.setPassword("123456");
//        user4.setSex("2");
//        user4.setAge("24");
//
//        List<User> userList = Arrays.asList(user1, user2, user3, user4);
//
//        List<String> nameList = new ArrayList<>();
//
//        userList.forEach(user -> {nameList.add(user.getName());});
//
//        nameList.forEach(s -> {System.out.println(s);});

        List<User> userList = Stream.of(new User("M","123","1","21"),
                new User("B","123","1","22"),
                new User("S","123","2","23"),
                new User("M","123","2","24")).collect(Collectors.toList());

        List<User>  users= userList.stream().sorted(Comparator.comparing(User::getName).thenComparing(User::getAge).reversed()).collect(Collectors.toList());

        users.forEach(user -> {System.out.println(user.getName() + "," + user.getAge());});

    }

    static class User{
        private String name;
        private String password;
        private String sex;
        private String age;

        private User(String name, String password, String sex, String age){
            this.name = name;
            this.password = password;
            this.sex = sex;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

}

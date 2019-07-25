package com.shadow.verify.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author accumulate
 * @Description
 * @data 2019-05-16
 */
@Component
public class TestMain {
    @Autowired
    List<Test> tests;

    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"SpringContext.xml"});
        TestMain testMain = (TestMain) context.getBean("testMain");
//        TestMain testMain = new TestMain();
//        testMain.setTests(context.getBeansOfType(Test.class));
        testMain.test();
    }

    public void setTests(Map<String, Test> tests) {
        tests.forEach((name, test) -> this.tests.add(test));
    }

    private void test() {
        System.out.println(LocalDateTime.now());
        this.tests.stream().parallel().map(Test::test).peek(test -> {
            System.out.println(LocalDateTime.now());
        }).forEach(System.out::println);
    }
}

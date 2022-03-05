package learning;

import org.junit.jupiter.api.Test;

public class VarArgsTest {
    /*
    * 1. 가변인자에 아무것도 전달 되지 않을때 어떻게 전달 받는지(null 또는 빈배열) -> 빈배열
    *
    * 2. 전달 받은 가변 인자를 다른 메소드의 가변 인자로 그대로 전달했을때, 어떻게 전달 받는지
    * (새로운 배열 또는 기존의 배열 또는 배열 자체를 하나의 오브젝트로 취급) -> 기존의 배열
    *
    * 3. 오브젝트 배열을 하나의 오브젝트로 전달 했을 때, 어떻게 전달 받는지
    * (기존의 배열 또는 하나의 오브젝트로 취급) -> 하나의 오브젝트로 취급
    *
    * Object의 toString은 hashcode를 통해서 메모리 번지를 응답해주기 때문에, 출력결과로 위의 결과 도출
    * */
    @Test
    void varArgsTest(){
        test1();
        System.out.println("================");
        test1("1","2","3");
    }

    private void test1(Object... args){
        System.out.println("test1 args = " + args.length + " " + args);
        test2(args);

        Object[] args2 = args;
        test2(args2);

        Object args3 =args2;
        test2(args3);
    }

    private void test2(Object... args){
        System.out.println("test2 args = " + args.length + " " + args);
    }
}

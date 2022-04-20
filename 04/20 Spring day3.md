<h2>ModelAndView & Tiles</h2>

Service 단은 데이터베이스와 관련된 일을 한다. , 트랜잭션처리 담당, DB와 관련된 업무처리를 해서 넘긴다.
DAO는 dml(select, update, inser, delete ) 등을 하는 것
그 결과에 대한 업무처리(비즈니스)는 Service에서 한다.



View단을 나타낼 때, String도 쓰이지만 String 대신 ModelAndView도 많이 쓰임.  (주인장이 자바가 아닌 Spring이다)
모델앤뷰 : 모델데이터와 뷰단. select 해온 결과물인 model(addObject로 넣을 꺼)과, 뷰단(setViewName으로 보여줄것)을 말한다. 

ModelAndView mav

request.setAttribute("key",객체); 할 것들을
mav.addObject("key", 객체);로 수행한 후 
return mav 한다. 

mav.setViewName("뷰단의 경로명");은 return "뷰단의 경로명";과 같다. 

* 웹클라이언트가 WAS에 붙기만하면, session은 자동적으로 생성된다.

이클립스로는
jsp:include
했지만

Spring에서는 Tiles를 사용한다.

1순위 2순위

공통 패키지 com.spring.board.common 


맵퍼에서 읽어온 컬럼값이 MemberVO로 찾아가서  쑥 들어간다.
MyMVC 처럼 memeber.set 작업을 할 필요가 없다.


<h2>로그인처리</h2>

#29 .xml mapper 기본설정 / 루트 엘리먼트 & 네임스페이스 설정 ( 프로젝트 전체 내에서 유일해야 한다.) 일반적으로 파일명 사용
<mapper namespace="board">
	.xml 이 바뀌면 왓스가 구동되어질 때 읽어오기 때문에 반드시 변경 후 재구동해줘야 함.

#30 컨트롤러 선언
@Controller  <- 빼도 안 빼도 상관이 없음
자동으로 올라가게 하기위해선 어노테이션 @Component 

XML에서 빈을 만드는 대신에 클래스명 앞에 @Component 어노테이션을 적어주면 해당 클래스는 bean으로 자동 등록된다.
그리고 bean의 이름(첫글자는 소문자)은 해당 클래스명이 된다. 
즉, 여기서 bean의 이름은 boardController 이 된다. 
여기서는 @Controller 를 사용하므로 @Component 기능이 이미 있으므로 @Component를 명기하지 않아도 BoardController 는 bean 으로 등록되어 스프링컨테이너가 자동적으로 관리해준다. 

@Controller
public class BoardController {
~~


#31. Service 선언  
트랜잭션 처리를 담당하는곳 , 업무를 처리하는 곳, 비지니스(Business)단   , @Service에는  @Component 포함이므로 생략가능 
@Service


#32. DAO 선언  
//@Component
@Repository  
 이 클래스가 DAO입니다 선언해주는게 @Repository 이다 . 얘도 @Component 포함. 자동적으로 bean으로 올라간다. 

#33. 의존객체 주입하기(DI: Dependency Injection)
의존 객체 자동 주입(Automatic Dependency Injection)은
스프링 컨테이너가 자동적으로 의존 대상 객체를 찾아서 해당 객체에 필요한 의존객체를 주입하는 것을 말한다. 
단, 의존객체는 스프링 컨테이너속에 bean 으로 등록되어 있어야 한다. 

 - 의존 객체 자동 주입(Automatic Dependency Injection)방법 3가지 
  1. @Autowired ==> Spring Framework에서 지원하는 어노테이션이다. 
                        스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.
   
   2. @Resource  ==> Java 에서 지원하는 어노테이션이다.
                        스프링 컨테이너에 담겨진 의존객체를 주입할때 필드명(이름)을 찾아서 연결(의존객체주입)한다.
   
   3. @Inject    ==> Java 에서 지원하는 어노테이션이다.  @Autowired 랑 기능이 똑같지만 원소속이 스프링이냐 자바냐 라는 점이 차이점이다. 
                        스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.

#34. 의존객체 주입하기(DI: Dependency Injection) ===
	DAO 필드를 만든다. 
```
@Autowired  // Type에 따라 알아서 Bean을 주입해준다.   // BoardDAO를 찾아서 주입
private InterBoardDAO dao; // null이 되지 않게 어노테이션
```
Type 에 따라 Spring 컨테이너가 알아서 bean 으로 등록된 com.spring.board.BoardDAO 의 bean 을  dao 에 주입시켜준다. 
그러므로 dao 는 null 이 아니다


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

<br><br>
<h2>AES256 암호화 복호화 설정해두기</h2>
	
![image](https://user-images.githubusercontent.com/57201495/164265085-7f6aa285-ac03-4563-aa99-4a9de4010379.png)
AES256 클래스와 같이 파라미터가 있는 생성자는 기본생성자를 없앤다.
@Component 쓰려면 그 클래스에 기본생성자가 존재해야 함.
@Component 써봤자 bean으로 안올라간다 => xml에서 해줘야 함.

스프링에서는 매번 new new 안해주고, 
bean으로 메모리에 올라가 있는 걸 불러서 사용한다.

<br><br>
<h2>Sevice, DAO 선언 등 초기설정</h2>

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

#35. 의존객체 주입하기(DI: Dependency Injection) 
- IoC(Inversion of Control == 제어의 역전)<br>
	     개발자가 인스턴스 변수 객체를 필요에 의해 생성해주던 것에서 탈피하여 <u>스프링은 컨테이너에 객체를 담아 두고</u>, <br>
	     필요할 때에 컨테이너로부터 객체를 가져와 사용할 수 있도록 하고 있다. <br>
	     스프링은 객체의 생성 및 생명주기를 관리할 수 있는 기능을 제공하고 있으므로, <br>더이상 개발자에 의해 객체를 생성 및 소멸하도록 하지 않고<br>
	     객체 생성 및 관리를 스프링 프레임워크가 가지고 있는 객체 관리기능을 사용하므로<br> Inversion of Control == 제어의 역전 이라고 부른다.  <br>
	     그래서 스프링 컨테이너를 IOC(Inversion of Control) 컨테이너라고도 부른다.
- 느슨한 결합
스프링 컨테이너가 BoardController 클래스 객체에서 BoardService 클래스 객체를 사용할 수 있도록 <br>
만들어주는 것을 "느슨한 결합" 이라고 부른다.<br>
느스한 결합은 BoardController 객체가 메모리에서 삭제되더라도 <br>BoardService service 객체는 메모리에서 동시에 삭제되는 것이 아니라 남아 있다.
	   
- 단단한 결합(개발자가 인스턴스 변수 객체를 필요에 의해서 생성해주던 것)
private InterBoardService service = new BoardService(); <br>
BoardController 객체가 메모리에서 삭제 되어지면  BoardService service 객체는 멤버변수(필드)이므로 메모리에서 자동적으로 삭제되어진다.
<br><br>	
<h2>게시판 시작</h2>	
#36 메인 페이지 요청<br>
*BoardController<br>
service.getImgfilenameList();<br>

/WEB-INF/views/tiles1/main/index.jsp 파일을 생성한다.<br>

#37 시작페이지에서 메인 이미지를 보여주는 것<br>
*BoardService<br>
dao.getImgfilenameList();<br>
	
#38 시작페이지에서 메인 이미지를 보여주는 것 <br>
*BoardDAO<br>
List<String> imgfilenameList = sqlsession.selectList("board.getImgfilenameList");<br>
	
#39 시작페이지에서 메인 이미지를 보여주는 것<br>
	```
	<select id="getImgfilenameList" resultType="String"><br>
		select문 <br>
	</select><br>
	```
<br>
#40 로그인 폼 페이지 요청<br>
@RequestMapping(value="/login.action", method= {RequestMethod.GET})	//form 태그가 떠야하니깐 method는 오로지 get<br>
	
#41 로그인 처리하기<br>
```
@RequestMapping(value="/loginEnd.action", method= {RequestMethod.POST})
public ModelAndView loginEnd(ModelAndView mav, HttpServletRequest request) {
	...
	MemberVO loginuser = service.getLoginMember(paraMap);
	if(loginuser == null) { //로그인 실패시
		...
	}
	else { // 아이디와 암호가 존재하는 경우
		...
		}
		else { //로그인 한지 1년 이내인 경우
			...
			}
			else { // 암호를 마지막으로 변경한 것이 3개월 이내인 경우
				...
			}
		}
	}
	return mav;
}
```	
	
#42 로그인 처리하기<br>
dao.getLoginMember(paraMap);
	
#43 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스 생성하기 <br>
(기본생성자가 없으므로 @Componet 를 쓰면 오류가 발생한다.<br>
그래서 servlet-context.xml 파일에 직접 파라미터가 있는 생성자로 bean 등록을 해주어야 한다.)<br>

#44 양방향 암호화 알고리즘인 AES256 를 사용하여 복호화 하기 위한 클래스(파라미터가 있는 생성자) 의존객체 bean 생성하기 
```
<beans:bean id="aes" class="com.spring.board.common.AES256">
   <beans:constructor-arg>
      <beans:value>abcd0070#cclass$</beans:value> <!-- abcd0070#cclass$ 은 각자 com.spring.board.common.SecretMyKey 에 사용되던 암호화/복호화 키 이다. -->   
   </beans:constructor-arg>
</beans:bean>
```
	
#45 양방향 암호화 알고리즘인 AES256 를 사용하여 복호화 하기 위한 클래스 의존객체 주입하기(DI: Dependency Injection)<br>
@Autowired
private AES256 aes;
	<br>
Type 에 따라 Spring 컨테이너가 알아서 bean 으로 등록된 com.spring.board.common.AES256 의 bean 을  aes 에 주입시켜준다. <br>
그러므로 aes 는 null 이 아니다.<br>
com.spring.board.common.AES256 의 bean 은 /webapp/WEB-INF/spring/appServlet/servlet-context.xml 파일에서 bean 으로 등록시켜주었음.  <br>

#46 로그인 처리하기<br>
MemberVO loginuser = sqlsession.selectOne("board.getLoginMember", paraMap);<br>
	
#47 board.xml 로그인 처리하기<br>
- HashMap 타입으로 매개변수를 받아온 것을 꺼내서 사용할때 <br>
1. 데이터로 사용할때는 #{key명} 이고,<br>
2. 식별자(테이블명, 컬럼명)로 사용할때는 ${key명} 이고,<br>
3. myBatis 에서 제공하는 if 엘리먼트나 choose 엘리먼트 안에서 사용할때는 <br>
  그냥 <if test="key명"> <when test="key명"> 으로 사용한다. <br>
```
<select id="getLoginMember" resultType="com.spring.board.model.MemberVO" parameterType="HashMap">
	select문
</select>
<update id="updateIdle" parameterType="String">
	update tbl_member set idle = 1
	where userid = #{userid}
</update>
```
	
#48 aes 의존객체를 사용하여 로그인 되어진 사용자(loginuser)의 이메일 값을 복호화 하도록 한다. <br>
  또한 암호변경 메시지와 휴면처리 유무 메시지를 띄우도록 업무처리를 하도록 한다.<br>
	
#49 로그인이 성공되어지면 로그인되어진 사용자의 이메일 주소를 출력하기 <br>
	<c:if></c:if> 사용함.


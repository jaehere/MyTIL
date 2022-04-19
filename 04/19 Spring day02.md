<context:component-scan base-package="com.spring.*" />

- 어노테이션 @을 줘서 bean으로 올리기 때문에 메모리에 올라가 있다.<br>
  @Controller , @Service , @Repository (dao)   속에는 이미 @Component 기능이 있기 때문에 @Component 빼도 괜찮다. 

- @RequestMapping(value="/test/test_insert.action")<br>
  앞에 contextPathname이 생략되어져 있다 .  <br>
  프로젝트 생성시 com.lg.sales 일 경우, sales가 path name이다.

- 트랜잭션처리는 서비스단에서 해준다.

---<br>
```
private InterBoardService service = new BoardService();

---<br>와 대비하여,

BoardController ctrl = new BoardController();
 
ctrl ==> 삭제
객체를 다 쓰고나면, 메모리에서 소멸. 
JVM(자바버츄얼머신) 이 알아서 쓰레기를 메모리에서 비운다.


----> 단단한결합  ===> BoardController 객체가 메모리에서 삭제 되어지면  BoardService service 객체는 멤버변수(필드)이므로 메모리에서 자동적으로 삭제되어진다.

---<br>
ex) Controller 가 혼자만 Service를 쓰는게 아니다. 여러가지 컨트롤러가 어떠한 서비스객체를 매번 쓰고 싶다.
정통적인 방법으로 하면 매번 만들고(new) 지우고(JVM) 해야한다. 

EmpController empctrl = new EmpController();

---<br>
==> 이러면 안좋으니깐, 불편하니깐 이거 개발자는 하지말고 그냥 bean으로 올려두고 필요하면 끌어다 써! 가 바로 스프링이다.
```

@Autowired  // Type에 따라 알아서 Bean을 주입해준다.      //넣었다가 뺐다가 느슨한결합으로 넣어준다.
private InterBoardService service; 

-- InterBoardService 에는 이 인터페이스 를 구현해준 클래스만 들어올 수 있다.

---<br>


- 스프링은 우리가 배운 이전내용에서 service가 추가된 것. <br>
  Model단[Repository](DAO, DTO) 단에서는 dml 오로지 셀렉트 같은 것만 기입.


-  .xml 이 바뀌면 왓스가 구동되어질 때 읽어오기 때문에 반드시 변경 후 껐다켜줘야한다.


- MyMVC 뿐만아니라 user hr에도 넣어주고 싶다.


- BoardDAO, InterBoardDAO 그냥 복붙해두면<br>
  org.springframework.beans.factory.BeanDefinitionStoreException: Unexpected exception parsing XML document from ServletContext resource [/WEB-INF/spring/appServlet/servlet-context.xml]; nested exception is org.springframework.context.annotation.ConflictingBeanDefinitionException: Annotation-specified bean name 'boardDAO' for bean class [com.spring.employees.model.BoardDAO] conflicts with existing, non-compatible bean definition of same name and class [com.spring.board.model.BoardDAO]
<br>
그렇다고 DAO2 이런식으로 하기엔 너무 복잡.<br>
그래서 bean 이름에 패키지명까지 해준다.
<br>
com.spring.employees.model.boardDAO<br>
그럼 빈이름이 중복될 일이 없다.

면접 Q. DI가 무엇인가요?? 의존객체 주입하기 Dependency Injection
	내용도 : 

느슨한결합 이런거 bean에 넣어주기만 하면 스프링 프레임워크가 알아서 한다. 

Q. 느슨한 결합 / 단단한 결합 차이점

Q.IOC (Inversion of Control) 제어의 역전
 : 객체 생성및 소멸에 대한 제어권을 개발자가 하는 것이 아니라 스프링 컨트롤러가 한다. 개발자가 하지마 ! 

컨트롤러는 항상 서비스한테 보낸다.

sql이 있는 xml파일을 mapper라고 한다.


vo는 resultType, HashMap은 resultMap

int test_insert(); //간단하게 데이터베이스에 인서트할 수 있게 메소드 생성
int test_insert(Map<String, String> paraMap);  //메소드의 오버로딩

우리가 만드는 클래스는 


 #### 중요 #### 
		HashMap 타입으로 매개변수를 받아온 것을 꺼내서 사용할때 
		1. 데이터로 사용할때는 #{key명} 이고,
		2. 식별자(테이블명, 컬럼명)로 사용할때는 ${key명} 이고,
		3. myBatis 에서 제공하는 if 엘리먼트나 choose 엘리먼트 안에서 사용할때는 
		      그냥 <if test="key명"> <when test="key명"> 으로 사용한다. 


@ResponseBody 란?
```
     메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단 페이지를 통해서 출력되는 것이 아니라 
   return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 
     일반적으로 JSON 값을 Return 할때 많이 사용된다. 
```
- vo를 사용할 때,
//request.getParamter가 필요없는 이유는 form태그에 넣어준 no, nmae의 name이 TestVO와 이름이 같아서  getParamter할 필요 없이 찾아간다., 자동적으로 쏙 들어간다.

 - 스프링에서 json 또는 gson을 사용한 ajax 구현시 데이터를 화면에 출력해 줄때 한글로 된 데이터가 '?'로 출력되어 한글이 깨지는 현상이 있다. 
	             이것을 해결하는 방법은 @RequestMapping 어노테이션의 속성 중 produces="text/plain;charset=UTF-8" 를 사용하면 
	             응답 페이지에 대한 UTF-8 인코딩이 가능하여 한글 깨짐을 방지 할 수 있다.

- 해쉬맵이면 key값쓰면 되는데,
  VO를 쓸땐, name을 똑같이 대소문자까지 맞춰서 넣어주면 알아서 찾아간다.
  controller에 getParameter가 없다. vo에 자동적으로 set set set 되어지고 있다. 
  form태그의 name과 vo의 getName의 Name 부분이 같으면 자동으로 들어옴. 코드명이 확 줄어버림. 

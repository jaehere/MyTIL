오늘은 ~~ 스프링 시작하는날 

<h2> [오전]</h2><br/>
STS.exe 는 이클립스와 비슷한 모습의 스프링 <br/>
Spring tool suite

 - top-level package (기본 패키지명) 입력(3단계 이상 권장. 일반적으로 회사도메인명.컨텍스트명으로 함)<br/>
          우리는 com.spring.board 라고 입력하고 Finish 버튼을 클릭한다.    <br/>
  <b>**** 여기서 맨뒤 board가 context name이다. </b>


- 미리 오류를 방지하고자<br/>
/Board/src/main/webapp/WEB-INF/web.xml<br/>
배치서술자를 수정해준다.


- web-inf 에 보면 lib 대신 ㅡmaven을 쓰는거임.

- 오라클과 연동해야한다. ojdbc6 <br/>
https://mvnrepository.com/search?q=ojdbc6

 -  오라클사에서 소송걸었음.
너네 왜 허락없이 우리꺼 막 쓰냐 ? 해서 오라클 홈페이지 와서 써라 이러는중'
꼼수는 <br/>

- #01. ojdbc.jar 를 받기 위한 리포지터리 주소 변경 
```
   <repositories> 
      <repository>
         <id>codelds</id>
            <url>https://code.lds.org/nexus/content/groups/main-repo</url>
      </repository>
      
      <!-- ===== 네이버 스마트 에디터에서 사진첨부 관련하여 이미지의 크기를 구하기위한 라이브러리 다운받는곳 =====  -->
      <repository>
          <id>osgeo</id>
          <name>OSGeo Release Repository</name>
          <url>https://repo.osgeo.org/repository/release/</url>
          <snapshots><enabled>false</enabled></snapshots>
          <releases><enabled>true</enabled></releases>
        </repository>
        
   </repositories>
```

- 라이브러리를 어디서 다운받는지 알아야 한다 <br/>
Window- Preferences- Maven - User Settings

 - Maven 세팅은 설치 경로만 확인할것(수정할 일 없음)<br/>
  메뉴 Window > Preferences > Maven > User Settings > User Settings : Local Repository 경로(C:\Users\사용자\.m2\repository : 메이븐이 다운로드 받은 라이브러리가 저장되는 폴더)

 - 탐색기에<br/>
C:\Users\sist\.m2\repository<br/>
  기존까진 이클립스에서 lib에 하나하나 넣어줘야했는데<br/>
  메이븐은 이와같이 xml에 넣어주기만 하면 자기가 알아서 쫙 받아온다.


- spring tx  란 transaction


- 파일업로드 다운로드 할 때 cos.jar 썼는데 이제는 직접 만들 것이다.

- excel 다운 받는 건 poi-ooxml


- 이클립스에서 실제로 작동 되는건 .java 파일이 아니라 .class파일이 움직이고 있는 것이다.



**** JSPServletBegin web.xml 수정하기!!



```
	<!-- The definition of the Root Spring Container shared by all Servlets and Filters -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>/WEB-INF/spring/root-context.xml</param-value>
	</context-param>
```
환경셋팅을 이 파일대로 해준다는 말




- bean 이 객체를 하나 만들어준다는 말<br/>
클래스를 가지고 객체를 하나만든다.<br/>
참고<br/>
/JSPServletBegin/src/main/webapp/chap03_StandardAction/05_useBean_execute.jsp



- MyMVC 의 DAO같은게 이제 필요없다.<br/>
sql만 쓰면 된다. !!<br/>

sql문이 어디에 있다고 알려주는 거
<property name="mapperLocations" value="classpath*:com/spring/board/mapper/*.xml" />

<br/><br/>

<hr/>
<h2> [오후]</h2>


 #29. 루트 엘리먼트 & 네임스페이스 설정(프로젝트 전체내에서 유일해야 한다.) 일반적으로 파일명을 쓴다.
<b>
<mapper namespace="board">
  </b>
namespace 중복되면 안돼 그래서 일반적으로 파일명을 namespace로 맞춰줌




- json과 비스무리한 jackson




 #18. 이미지, 동영상, js, css 파일등이 위치하는 경로를 의미한다.
 그 경로는 /webapp/resources 아래에 위치하거나 또는 /webapp/resources 아래에 폴더를 만들어서 저장시켜야 한다. ==== -->

  ```
  <!-- Handles HTTP GET requests for /resources/** by efficiently serving up static resources in the ${webappRoot}/resources directory -->
	<mvc:resources mapping="/resources/**" location="/resources/" />	
```
  앞으로 /는 webapp이다. 파일들은 늘 resources 폴더 밑에 있어야한다.는 뜻
bootstrap-4.6.0-dis , jquery-ui-1.13.1.custon , js 폴더도 resources 폴더 밑에 넣어주었다.


```
	<!-- Resolves views selected for rendering by @Controllers to .jsp resources in the /WEB-INF/views directory -->
	<beans:bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<beans:property name="prefix" value="/WEB-INF/views/" />
		<beans:property name="suffix" value=".jsp" />
	</beans:bean>
```
- 접두어 접미어가 있어서
return "member";
라고만 기입해도
return "/WEB-INF/views/member.jsp";가 알아서 된다.


- 뷰단페이지는 어디에있다 보고,


-  뷰 리졸버 :뷰단이 어떻게 됩니까?

- bean은 클래스화 시킨다음에 객체화 시킨다. 객체가 이미 메모리에 올라가있는게 bean이다


```
<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
```
위는 마치 MyMVC의 FrontController 같은 존재

왓스가 구동되어지는 순간 bean은 메모리에 쫙 올라간다.
```
<beans:property name="defaultEncoding" value="utf-8" /> 이거면 따로 encoding필요없다.
```

```
	사용자 웹브라우저 요청(View)  ==> DispatcherServlet ==> @Controller 클래스 <==>> Service단(핵심업무로직단, business logic단) <==>> Model단[Repository](DAO, DTO) <==>> myBatis <==>> DB(오라클)           
	(http://...  *.action)                                  |                                                                                                                              
	 ↑                                                View Resolver
	 |                                                      ↓
	 |                                                View단(.jsp 또는 Bean명)
	 -------------------------------------------------------| 
	
	사용자(클라이언트)가 웹브라우저에서 http://localhost:9090/board/test_insert.action 을 실행하면
	배치서술자인 web.xml 에 기술된 대로  org.springframework.web.servlet.DispatcherServlet 이 작동된다.
	DispatcherServlet 은 bean 으로 등록된 객체중 controller 빈을 찾아서  URL값이 "/test_insert.action" 으로
	매핑된 메소드를 실행시키게 된다.                                               
	Service(서비스)단 객체를 업무 로직단(비지니스 로직단)이라고 부른다.
	Service(서비스)단 객체가 하는 일은 Model단에서 작성된 데이터베이스 관련 여러 메소드들 중 관련있는것들만을 모아 모아서
	하나의 트랜잭션 처리 작업이 이루어지도록 만들어주는 객체이다.
	여기서 업무라는 것은 데이터베이스와 관련된 처리 업무를 말하는 것으로 Model 단에서 작성된 메소드를 말하는 것이다.
	이 서비스 객체는 @Controller 단에서 넘겨받은 어떤 값을 가지고 Model 단에서 작성된 여러 메소드를 호출하여 실행되어지도록 해주는 것이다.
	실행되어진 결과값을 @Controller 단으로 넘겨준다.
```

단단한결합
느슨한 결합이 있다.

### 형변환 필수 String -> int -> double -> int

 point 는 conimoney의 10% 이다.

스트링을 인트 로, 실수를 인트 로 형변환

```
pstmt.setString(1, paraMap.get("coinmoney"));
**pstmt.setInt(2, (int) (Integer.parseInt(paraMap.get("coinmoney")) * 0.01) );**
pstmt.setString(3, paraMap.get("userid"));

```

---

### 회원정보수정

```
@login.jsp

1.
<%-- *** 로그인 되어진 화면 *** --%>
[<a href="javascript:goEditPersonal('${(sessionScope.loginuser).userid}')">나의정보</a>]&nbsp;&nbsp;

2.
// == 나의 정보 수정하기 == //
function goEditPersonal(userid){

}

```

**value 값에 미리 값을 넣어줘서 창을 띄웠을 때, 자동적으로 정보가 들어가 있게 한다.**

**input 태그 안에 value="${[sessionScope.loginuser.name](http://sessionscope.loginuser.name/) }" 넣어준다.**

→ 세미프로젝트에서 게시판 글쓰기에서도 작성자 이름, 작성자이메일 등 정보 넣어줄 수 있겠다 !

---

### 팝업창 가운데 위치시키기

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a71883b3-d484-4950-a3db-8550452f699d/Untitled.png)

 - window.screen.width : 모니터의 크기

 - 예를 들어 전체스크린 2000 기준 가운데 800 양옆 600
 - Math.ceil 는 실수보다 큰 최소의 정수

```java
// == 나의 정보 수정하기 == //
	function goEditPersonal(userid){
		
		// 나의 정보 수정하기 팝업창 띄우기
		const url = "<%= request.getContextPath()%>/member/memberEdit.up?userid="+userid;
		
		//너비 800, 높이 600 인 팝업창을 화면 가운데 위치시키기
		const pop_width = 800; //number타입 단위는 안준다 기본이 px
		const pop_height = 600;
		const pop_left = Math.ceil( (window.screen.width - pop_width) / 2 ); //정수로 만든다.
		const pop_top = Math.ceil( (window.screen.heigth - pop_height) / 2 ); //정수로 만든다.
			//예를 들어 전체스크린 2000 기준 가운데 800 양옆 600
			// Math.ceil 는 실수보다 큰 최소의 정수
		window.open(url, "memberEdit", 
		 			"left="+pop_left+", top="+pop_top+", width="+pop_width+", height="+pop_height); //팝업창을 딱 가운데 띄우겠다.
	}
```

 - 듀얼모니터를 쓰고 있어 모니터 1은 가운데정렬을 감지하나 모니터 2는 width 가운데를 인식하지 못한다는 점 발견

---

### substring으**로 전화번호 자르기**

휴대폰번호 mobile에서 hp2, hp3 으로 자르기 010  / 2222 / 3333 

참고파일 →C:\NCS\workspace(jsp)\JSPServletBegin\src\main\webapp\chap04_JSTL\07_function_view_02.jsp

**substring(str, idx1, idx2)** 

: str.substring(idx1, idx2)의 결과를 반환, idx2가 -1일 경우 str.substring(idx1)과 동일

```jsx
<input type="text" id="hp1" name="hp1" size="6" maxlength="3" value="010" readonly />&nbsp;-&nbsp;
<input type="text" id="hp2" name="hp2" size="6" maxlength="4" value="${fn:substring(sessionScope.loginuser.mobile,3,7)}" />&nbsp;-&nbsp;
<input type="text" id="hp3" name="hp3" size="6" maxlength="4" value="${fn:substring(sessionScope.loginuser.mobile,7,11)}" />
```

---

### 정보 수정 후 팝업창 닫기, 부모창 새로고침

msg.jsp에서

- 팝업창 닫기
    - self.close();
- 부모창 새로고침
    - opener.location.reload(true);
    - opener.history.go(0);

```
location.href="javascript:history.go(-2);";  // 이전이전 페이지로 이동
location.href="javascript:history.go(-1);";  // 이전 페이지로 이동
location.href="javascript:history.go(0);";   // 현재 페이지로 이동(==새로고침) 캐시에서 읽어옴.
location.href="javascript:history.go(1);";   // 다음 페이지로 이동
location.href="javascript:history.back();";       // 이전 페이지로 이동
location.href="javascript:location.reload(true)"; // 현재 페이지로 이동(==새로고침) 서버에 가서 다시 읽어옴.
location.href="javascript:history.forward();";    // 다음 페이지로 이동.
```

---

### 관리자 전용 회원목록 - 페이징처리

관리자 계정 만든 후

<c:if test=""> </c:if> 안에 관리자 전용 넣어준다.

<c:if test="${ not empty sessionScope.loginuser and sessionScope.loginuser.userid eq 'admin' }">

or

<c:if test="${ not empty sessionScope.loginuser && sessionScope.loginuser.userid eq 'admin' }">

eq는 같다 ne는 같지 않다.

```jsx
<c:if test="${ not empty sessionScope.loginuser}">
	<li class="nav-item dropdown">
		<a class="nav-link dropdown-toggle menufont_size text-info" href="#" id="navbarDropdown" data-toggle="dropdown">
		관리자전용                     <%-- .text-info 는 글자색으로 청록색임 --%>
		</a>
		<div class="dropdown-menu" aria-labelledby="navbarDropdown">
		<a class="dropdown-item text-info" href="<%= ctxPath%>/member/memberList.up">회원목록</a>
		<a class="dropdown-item text-info" href="<%= ctxPath%>/shop/admin/productRegister.up">제품등록</a>
		<div class="dropdown-divider"></div>
		<a class="dropdown-item text-info" href="<%= ctxPath%>/shop/orderList.up">전체주문내역</a>
		</div>
	</li>
</c:if>
```

### **rownum** (!!!! 게시판 등 웹에서 아주 많이 사용됩니다. !!!!)

- 기존 자료
    
    ```sql
    --  1 페이지 ==>  rownum : 1 ~ 2  /  boardno : 5 ~ 4 
     
        /*
            >>> rownum : 1 ~ 2 를 구하는 공식 <<<
            (currentShowPageNo * sizePerPage) - (sizePerPage - 1);
                            1  *  2  -  ( 2 - 1 )  ==> 1         
                    sizePerPage 가 10인 서울교대 게시판
                            1  * 10  -  (10 - 1)   ==> 1
                    
            (currentShowPageNo * sizePerPage);
                            1  *  2  ==> 2
                    sizePerPage 가 10인 서울교대 게시판
                            1  * 10  ==> 10
        */
        
        
        -- 재정의 해줘야하고, 다시한번 inline view 처리 해줘야함. RNO, boardno, subject , userid, registerday  이 하나의 테이블로 봐야함.
        --RNO가 컬럼으로 인식될 수 있게.
        select boardno, subject , userid, registerday 
        from
        (
            select rownum AS RNO  --<-- 반드시 별칭 써줘야함
                 , boardno, subject , userid, registerday 
            from
            (
                select boardno
                     , subject
                     , userid 
                     , to_char(registerday,'yyyy-mm-dd hh24:mi:ss') as REGISTERDAY
                from tbl_board
                order by boardno desc
            ) V
        )T
        where T.RNO between 1 and 2;  --1페이지
        -- t.RNO 에 T.은 생략가능 
        
        
        
        --  2 페이지 ==>  rownum : 3 ~ 4  /  boardno : 3 ~ 2 
        
        /*
            >>> rownum : 1 ~ 2 를 구하는 공식 <<<
            (currentShowPageNo * sizePerPage) - (sizePerPage - 1);
                            2  *  2  -  ( 2 - 1 )  ==> 3         
                    sizePerPage 가 10인 서울교대 게시판
                            2  * 10  -  (10 - 1)   ==> 11
                    
            (currentShowPageNo * sizePerPage);
                            2  *  2  ==> 4
                    sizePerPage 가 10인 서울교대 게시판
                            2  * 10  ==> 20
        */
        
        select boardno, subject , userid, registerday 
        from
        (
            select rownum AS RNO  --<-- 반드시 별칭 써줘야함
                 , boardno, subject , userid, registerday 
            from
            (
                select boardno
                     , subject
                     , userid 
                     , to_char(registerday,'yyyy-mm-dd hh24:mi:ss') as REGISTERDAY
                from tbl_board
                order by boardno desc
            ) V
        )T
        where T.RNO between 3 and 4;
        
        
        
        
        --  3 페이지 ==>  rownum : 5 ~ 6  /  boardno : 1 
        /*
            >>> rownum : 1 ~ 2 를 구하는 공식 <<<
            (currentShowPageNo * sizePerPage) - (sizePerPage - 1);
                            3  *  2  -  ( 2 - 1 )  ==> 5         
                    sizePerPage 가 10인 서울교대 게시판
                            3  * 10  -  (10 - 1)   ==> 21
                    
            (currentShowPageNo * sizePerPage);
                            3  *  2  ==> 6
                    sizePerPage 가 10인 서울교대 게시판
                            3  * 10  ==> 30
        */
        select boardno, subject , userid, registerday 
        from
        (
            select rownum AS RNO  --<-- 반드시 별칭 써줘야함
                 , boardno, subject , userid, registerday 
            from
            (
                select boardno
                     , subject
                     , userid 
                     , to_char(registerday,'yyyy-mm-dd hh24:mi:ss') as REGISTERDAY
                from tbl_board
                order by boardno desc
            ) V
        )T
        where T.RNO between 5 and 6;
    ```
    
- 적용
    
    ```sql
    select rno, userid, name, email, gender        
    from
    (
    select rownum AS rno, userid, name, email, gender        
    from
        (
        select userid, name, email, gender        
        from tbl_member
        where userid !='admin'
        order by registerday desc
        ) V
    ) T
    where rno between 1 and 10;-- 1 페이지( 한 페이지 당 10개를 보여줄 때 )
    ```
    

 - 멤버는 가입일의 내림차순 기준을 줬음. 게시판같은 경우는 작성일자 기준
 - rownum 은 where절에 바로 못 쓴다. 별칭을 달아줌

 - where rno between 1 and 10;-- 1 페이지( 한 페이지 당 10개를 보여줄 때 )

 - where rno between 11 and 20;-- 2 페이지( 한 페이지 당 10개를 보여줄 때 )

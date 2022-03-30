
### 페이징처리
---

<b>where rno between A and B  </b>

A 와 B 를 구하는 공식 




<b>currentShowPageNo</b> : 보고자 하는 페이지 번호.     즉, 1페이지, 2페이지, 3페이지... 를 말한다.

<b>sizePerPage</b> : 한페이지당 보여줄 행의 개수.     즉, 3개, 5개, 10개를 보여줄 때의 개수를 말한다.

A 는 (currentShowPageNo * sizePerPage) - (sizePerPage - 1) 이다.

B 는 (currentShowPageNo * sizePerPage) 이다.





```
    where rno between 1 and 10;  -- 1 페이지( 한 페이지 당 10개를 보여줄 때 )
    
    A 는 (1 * 10) - (10 - 1)  ==> 10 - 9 ==> 1
    B 는 (1 * 10) ==> 10

    where rno between 11 and 20; -- 2 페이지 (한 페이지 당 10개를 보여줄 때 )

    A 는 (2 * 10) - (10 - 1)  ==> 20 - 9 ==> 11
    B 는 (2 * 10) ==> 20
		
		where rno between 21 and 30; -- 3 페이지 (한 페이지 당 10개를 보여줄 때 )
        
    A 는 (3 * 10) - (10 - 1)  ==> 30 - 9 ==> 21 
    B 는 (3 * 10) ==> 30
```




### **회원목록 접근 막기**

---

관리자전용 회원목록 페이징처리

DB에서 보여줄 회원정보를 읽어와야 한다 - DAO가 필요하다.

관리자가 아닌 사람이 url을 알아왔다  - admin 아이디가 아닌 내 아이디 leejh로 들어갈 때는 막아줘야한다. - 메뉴바에서만 막으면 안된다. url 도 막아줘야한다. // 이중으로 막기 !

 - 메뉴바 막기

 - 직접 막기

[http://localhost:9090/MyMVC/member/memberList.up](http://localhost:9090/MyMVC/member/memberList.up)

@MemberListAction

// == 관리자(admin) 로 로그인 했을 때만 조회가 가능하도록 해야 한다. == //

```
  HttpSession session = request.getSession();

	MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");//세션에 가서 있는지 없는지 알아온다.

	//로그인을 안했든지, admin이 아닌경우

	if( loginuser == null || !"admin".equals(loginuser.getUserid())) {
		// 로그인을 안한 경우 또는 일반사용자로 로그인 한 경우

		String message = "관리자만 접근이 가능합니다.";
		String loc = "javascript:history.back()";

		request.setAttribute("message", message);
		request.setAttribute("loc", loc);

	//	super.setRedirect(false);
		super.setViewPage("/WEB-INF/msg.jsp");
	}

	else {
		// 관리자(admin)로 로그인 했을 경우

	}

```



 - **목록 페이징처리** get/ post 중 

 - 속도는 get이 더 빠르지만 보안성은 post가 더 좋다.

 - 페이징처리에서 보여만 주기 때문에 보안성이 필요없는 작업 . 빨리빨리 보여주는게 좋음

   → get방식을 쓰는 게 효율적

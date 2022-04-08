<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
    
<%
    String ctxPath = request.getContextPath();
    //    /MyMVC
%>
<!DOCTYPE html>
<html>
<head>

<title>:::HOMEPAGE:::</title>

<!-- Required meta tags -->
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

<!-- Bootstrap CSS -->
<link rel="stylesheet" type="text/css" href="<%= ctxPath%>/bootstrap-4.6.0-dist/css/bootstrap.min.css" > 

<!-- Font Awesome 5 Icons -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

<!-- 직접 만든 CSS -->
<link rel="stylesheet" type="text/css" href="<%= ctxPath%>/css/style.css" />

<!-- Optional JavaScript -->
<script type="text/javascript" src="<%= ctxPath%>/js/jquery-3.6.0.min.js"></script>
<script type="text/javascript" src="<%= ctxPath%>/bootstrap-4.6.0-dist/js/bootstrap.bundle.min.js" ></script> 

<link rel="stylesheet" type="text/css" href="<%= ctxPath%>/jquery-ui-1.13.1.custom/jquery-ui.css" > 
<script type="text/javascript" src="<%= ctxPath%>/jquery-ui-1.13.1.custom/jquery-ui.js"></script>

<script type="text/javascript">
	
	$(document).ready(function(){
		
	});

</script>

</head>
<body>

<%-- === <c:set var="변수명" value="${값}" scope="" /> 를 테스트 하기 위해서 사용하는 것 시작. === --%>
<c:set var="name1" value="쌍용강북" scope="page"/>  <%-- scope는 사용되어지는 범위 scope="page" 는 var ="name1"은 오로지 header.jsp 안에서 만 쓰인다.--%>
<c:set var="name2" value="교육센터" /> <%-- 생략하면 기본이 scope="page" 이다. 이 변수는 header.jsp에 만 쓰인다. --%>

<c:set var="name_scope_page"    value="${name1}&nbsp;${name2}" scope="page"/>
<c:set var="name_scope_request"    value="${name2}"            scope="request"/>
<c:set var="name_scope_session"    value="${name1}&nbsp;${name2}" scope="session"/>

<%-- 
	scope="page"    로 선언된 변수의 사용범위는 선언되어진 jsp 파일  
                                   및 선언되어진 파일 속에<%@ include file="ㅇㅇㅇ.jsp" %> 되어진 ㅇㅇㅇ.jsp 파일내에서도 사용되어진다.
 
 	scope="request" 로 선언된 변수의 사용범위는 선언되어진 jsp 파일 
                                       및 선언되어진 파일 속에 <%@ include file="ㅇㅇㅇ.jsp" %> 되어진 ㅇㅇㅇ.jsp 파일  
                                       및 <jsp:include page="선언되어진 jsp파일명" /> 이 들어있는 파일내에서도 사용되어진다.
 	scope="session" 로 선언된 변수의 사용범위는 모든 jsp 파일내에서 사용되어진다.
 	
 	변수의 삭제는
      <c:remove var="변수이름" scope="영역"/> scope 생략시 모든 영역의 동일한 변수 이름 삭제 됨.
 
 --%>

<%-- === <c:set var="변수명" value="${값}" scope="" /> 를 테스트 하기 위해서 사용하는 것 끝. === --%>

	<!-- 상단 네비게이션 시작 -->
	<nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top mx-4 py-3">
		<!-- Brand/logo --> <!-- Font Awesome 5 Icons -->
		<a class="navbar-brand" href="<%= ctxPath%>/index.up" style="margin-right: 10%;"><img src="<%= ctxPath %>/images/sist_logo.png" /></a>
		
		<!-- 아코디언 같은 Navigation Bar 만들기 -->
	    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#collapsibleNavbar">
	      <span class="navbar-toggler-icon"></span>
	    </button>
		
		<div class="collapse navbar-collapse" id="collapsibleNavbar">
		  <ul class="navbar-nav" style="font-size: 16pt;">
		     <li class="nav-item active">
		        <a class="nav-link menufont_size" href="<%= ctxPath%>/index.up">Home</a>
		     </li>
		     <li class="nav-item active">
		     	<a class="nav-link menufont_size" href="<%= ctxPath%>/member/memberRegister.up">회원가입</a>
		     </li>
		     <li class="nav-item active">
		     	<a class="nav-link menufont_size" href="<%= ctxPath%>/shop/mallHome1.up">쇼핑몰홈[더보기]</a>
		     </li>
		     <li class="nav-item active">
		     	<a class="nav-link menufont_size" href="<%= ctxPath%>/shop/mallHome2.up">쇼핑몰홈[스크롤]</a>
		     </li>
			 <li class="nav-item active">			 
			 	<a class="nav-link menufont_size" href="<%= ctxPath%>/shop/location.up">매장찾기</a>
			 </li>
			 
			 <c:if test="${ not empty sessionScope.loginuser && sessionScope.loginuser.userid eq 'admin' }"> <%-- admin으로 로그인 했을 때만 --%>
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
			 
			 <c:if test="${ not empty sessionScope.loginuser && sessionScope.loginuser.userid ne 'admin' }"> <%-- 관리자가 아닌 일반사용자로 로그인 했을 때만 --%>
			     <li class="nav-item dropdown">
			        <a class="nav-link dropdown-toggle menufont_size text-info" href="#" id="navbarDropdown" data-toggle="dropdown"> 
			                      장바구니/주문                     <%-- .text-info 는 글자색으로 청록색임 --%>  
			        </a>
			        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
			           <a class="dropdown-item text-info" href="<%= ctxPath%>/shop/cartList.up">장바구니</a>
			           <a class="dropdown-item text-info" href="<%= ctxPath%>/shop/admin/orderList.up">나의주문내역</a>
			          
			        </div>
			     </li>
			 </c:if>
			 
		  </ul>
		</div>
	</nav>
	<!-- 상단 네비게이션 끝 -->

    <hr style="background-color: gold; height: 1.2px; position: relative; top:85px; margin: 0 1.7%;"> 

    <div class="container-fluid" style="position: relative; top:90px; padding: 0.1% 2.5%;">
      
	  <div class="row">
		 <div class="col-md-3" id="sideinfo">
		 
		    <%-- 유트브 넣기 header.jsp 에만 있음 --%>
			<div class="row">
				<div class="col-md-8 offset-md-2 mt-3 embed-responsive embed-responsive-16by9">
			   		<iframe class="embed-responsive-item" src="https://www.youtube.com/embed/E0W5sJZ2d64" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
				</div>
			</div>
		 
			<div style="height: 200px; text-align: left; padding: 20px;">
				로그인/Tree/View
				
				<%@ include file="/WEB-INF/login/login.jsp" %>
			</div>
			
			<%-- == 쇼핑몰 카테고리목록만을 보여주는 부분  == --%>
			<div  id="sidecontent" style="text-align: left; padding: 20px; margin-top: 250px;">
				<%@ include file="/WEB-INF/myshop/categoryList.jsp" %>
			</div>
			
			
		 </div>
		 <div class="col-md-9" id="maininfo" >
			<div id="maincontent">
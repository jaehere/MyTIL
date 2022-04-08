<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 

<%
   String ctxPath = request.getContextPath();
    //     /MyMVC     
%>

<jsp:include page="../header2.jsp" />

<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" /> 
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>

<style type="text/css">
          
   li {margin-bottom: 10px;} 
   
   div#viewComments {width: 80%;
                      margin: 1% 0 0 0; 
                     text-align: left;
                     max-height: 300px;
                     overflow: auto;
                     /* border: solid 1px red; */
   }
   
   span.markColor {color: #ff0000; }
   
   div.customDisplay {display: inline-block;
                      margin: 1% 3% 0 0;
   }
                   
   div.spacediv {margin-bottom: 5%;}
   
   div.commentDel {font-size: 8pt;
                   font-style: italic;
                   cursor: pointer; }
   
   div.commentDel:hover {background-color: navy;
                         color: white;   }
   
   
   /* ~~~~ 일반적으로 태블릿 PC 가로 및 일반적으로 데스크탑 PC 에서만 CSS transform 을 사용하여 3D 효과를 주는 flip-card 를 만들어 보기 시작 ~~~~ */
   @media screen and (min-width:1024px){
      
      .flip-card {
            background-color: transparent; /* 투명 */
            perspective: 2000px;  /* perspective는 3D 환경을 만들때 사용하는 것으로서 원근감을 주는 것이다. 
                               이 값이 작으면 작을 수록 보고있는 사람의 위치를 더 가까이에서 보는 것으로 처리하므로 엘리먼트(요소)가 커 보이게 된다.  
                               이 값이 크면 클수록 보고있는 사람의 위치를 더 멀리 떨어져서 보는 것으로 처리하므로 엘리먼트(요소)가 작게 보이게 된다. */ 
      }
      
      .flip-card-inner {
            position: relative;
            width: 100%;  
            height: 100%; 
            text-align: center;
         /* transition: transform 2.6s; */ /* 요소(엘리먼트)를 transform(변형) 시키는데 걸리는 시간(단위는 초) 2.6초 */
            transition: transform 0.6s;    /* 요소(엘리먼트)를 transform(변형) 시키는데 걸리는 시간(단위는 초) 0.6초 */
            transform-style: preserve-3d;  /* 요소(엘리먼트)의 자식요소들(엘리먼트들)을 3D 공간에 배치 시킨다. */
         /* box-shadow: 0 4px 8px 0 rgba(0,0,0,1.0); */ /* rgba(빨강, 초록, 파랑, 투명도) */
            box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
      }
      
      .flip-card:hover .flip-card-inner {
        /* transform: rotateX(540deg); */  /* transform 은 요소(엘리먼트)를 변형시키는 것이다.
                                                                                 요소를 회전(rotate), 확대 또는 축소(scale), 기울이기(skew), 이동(translate) 효과를 부여할 수 있다. 
                                                                                 이를 통해 CSS 시각적 서식 모델의 좌표 공간을 변경한다.
                                              transform 이 지원되는 웹브라우저는 IE는 버전 10 이상부터 지원한다.
                                              
                                              rotateX는 x축을 기준으로 요소(엘리먼트)를 회전시키는 것이다. 
                                              1회전이 360deg 이므로 540deg 는 1바퀴 반을 회전시키는 것이다.*/
                                              
            transform: rotateY(180deg);    /* rotateY는 y축을 기준으로 회전한다. 180deg 반바퀴를 도는 것이다.*/
      }
      
      .flip-card-front, .flip-card-back {
        /* position: static; */
        /* position: relative;*/
           position: absolute;  /* .flip-card-front 엘리먼트(앞면)와  .flip-card-back 엘리먼트(뒷면)가 서로 겹쳐야 하므로 
                                   position 을 반드시 absolute; 로  주어야 한다. */
           width: 100%;  
           height: 100%; 
        /* backface-visibility: visible; */ /* backface-visibility 을 생략하더라도 기본값은 visible 이다. */
           backface-visibility: hidden;     /* 3D Transform에서 요소의 뒷면을 숨기는 역할을 한다.
                                                                                   이것을 hidden 처리하지 않으면 앞면/뒷면이 함께 보이기 때문에 이상하게 나오게 된다. */
      }
      
      .flip-card-front {
           background-color: #bbb;
           color: black;
           z-index: 2; /* position 속성을 이용하다 보면 엘리먼트(요소)를 겹치게 놓게될 수 있다. 
                                         이때 엘리먼트(요소)들의 수직 위치(쌓이는 순서)를 z-index 속성으로 정한다. 
                                         값은 정수이며, 숫자가 클 수록 위로 올라오고, 숫자가 작을 수록 아래로 내려간다. */
      }
      
      .flip-card-back {
        /* background-color: #2980b9; 파랑 */
           background-color: #ff8080; /* 빨강 */
           color: white;
        /* transform: rotateX(540deg); */   /* transform 은 IE는 버전 10 이상부터 지원한다.
                                               rotateX는 x축을 기준으로 요소(엘리먼트)를 회전시킨다. 
                                               1회전이 360deg 이므로 540deg 는 1바퀴 반을 회전시키는 것이다.*/
           transform: rotateY(180deg);      /* rotateY는 y축을 기준으로 요소(엘리먼트)를 회전시킨다. 
                                               180deg 반바퀴를 회전시키는 것이다.*/
           z-index: 1; /* position 속성을 이용하다 보면 엘리먼트(요소)를 겹치게 놓게될 수 있다. 
                                           이때 엘리먼트(요소)들의 수직 위치(쌓이는 순서)를 z-index 속성으로 정한다. 
                                           값은 정수이며, 숫자가 클 수록 위로 올라오고, 숫자가 작을 수록 아래로 내려간다. */
      }

   }   
   /* ~~~~ 일반적으로 태블릿 PC 가로 및 일반적으로 데스크탑 PC 에서만 CSS transform 을 사용하여 3D 효과를 주는 flip-card 를 만들어 보기 끝 ~~~~ */         

</style>

<script type="text/javascript">

   let isOrderOK = false;
   // 로그인한 사용자가 해당 제품을 구매한 상태인지 구매하지 않은 상태인지 알아오는 용도.
   // isOrderOK 값이 true 이면   로그인한 사용자가 해당 제품을 구매한 상태이고,
   // isOrderOK 값이 false 이면  로그인한 사용자가 해당 제품을 구매하지 않은 상태로 할 것임.

   $(document).ready(function() {
      <%--
     $("p#order_error_msg").css('display','none'); // 코인잔액 부족시 주문이 안된다는 표시해주는 곳. 
   
     goLikeDislikeCount(); // 좋아요, 싫어요 갯수를 보여주도록 하는 것이다.

     goCommentListView();  // 제품 구매후기를 보여주는 것.
      --%>
      /////////////////////////////////////////////////////////////////////////
      // === 로그인한 사용자가 해당 제품을 구매한 상태인지 구매하지 않은 상태인지 먼저 알아온다 === // 
      <%--
     $.ajax({
           url:"<%= request.getContextPath()%>/shop/isOrder.up",
            type:"GET",
            data:{"fk_pnum":"${pvo.pnum}"
               ,"fk_userid":"${sessionScope.loginuser.userid}"},
            dataType:"JSON",
         
            async:false,   // 동기처리
       // async:true,    // 비동기처리(기본값임) 
         
          success:function(json){
             isOrderOK = json.isOrder; 
               // json.isOrder 값이 true  이면 로그인한 사용자가 해당 제품을 구매한 경우이고
               // json.isOrder 값이 false 이면 로그인한 사용자가 해당 제품을 구매하지 않은 경우이다.
            },
           error: function(request, status, error){
            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
            }
     });
      --%>    
     //////////////////////////////////////////////////////////////////////////
     
     
     $("input#spinner").spinner( {
         spin: function(event, ui) {
            const $target = $(event.target);
            
            if(ui.value > 100) {
               $target.spinner("value", 100);
               return false;
            }
            else if(ui.value < 1) {
               $target.spinner("value", 1);
               return false;
            }
         }
      } );// end of $("input#spinner").spinner({});----------------    
      

      
   // **** 제품후기 쓰기(로그인하여 실제 해당 제품을 구매했을 때만 딱 1번만 작성할 수 있는 것. 제품후기를 삭제했을 경우에는 다시 작성할 수 있는 것임.) ****// 
      $("button#btnCommentOK").click(function(){
         
         if(${empty sessionScope.loginuser}) {
            alert("제품사용 후기를 작성하시려면 먼저 로그인 하셔야 합니다.");
            return;
         }
         
         
         if(isOrderOK == false) {  
            alert("${requestScope.pvo.pname} 제품을 구매하셔야만 후기작성이 가능합니다.");
         }
         else {   
            const commentContents = $("textarea[name=contents]").val().trim();
            
            if(commentContents == "") {
               alert("제품후기 내용을 입력하세요!!");
               return; 
            }
            
            // 보내야할 데이터를 선정하는 첫번째 방법
        <%-- 
            const queryString = {"fk_userid":'${sessionScope.loginuser.userd}', 
                             "fk_pnum" : '${requestScope.pvo.pnum}',
                             "contents" : $("textarea[name=contents]").val()};
        --%>
            // 보내야할 데이터를 선정하는 두번째 방법
            // jQuery에서 사용하는 것으로써,
            // form태그의 선택자.serialize(); 을 해주면 form 태그내의 모든 값들을 name값을 키값으로 만들어서 보내준다. 
            const queryString = $("form[name=commentFrm]").serialize();
            // console.log(queryString);
            // contents=very%20Good&fk_userid=seoyh&fk_pnum=57
            // %20 은 공백이다.
            
            $.ajax({
               url:"<%= request.getContextPath()%>/shop/commentRegister.up",
               type:"POST",
               data:queryString,
               dataType:"JSON",
               success:function(json){ // {"n":1} 또는 {"n":-1} 또는  {"n":0}
                  if(json.n == 1) {
                      // 제품후기 등록(insert)이 성공했으므로 제품후기글을 새로이 보여줘야(select) 한다.
                      goCommentListView(); // 제품후기글을 보여주는 함수 호출하기 
                    }
                    else if(json.n == -1)  {
                    // 동일한 제품에 대하여 동일한 회원이 제품후기를 2번 쓰려고 경우 unique 제약에 위배됨 
                  // alert("이미 후기를 작성하셨습니다.\n작성하시려면 기존의 제품후기를\n삭제하시고 다시 쓰세요.");
                     swal("이미 후기를 작성하셨습니다.\n작성하시려면 기존의 제품후기를\n삭제하시고 다시 쓰세요.");
                  }
                    else  {
                       // 제품후기 등록(insert)이 실패한 경우 
                       alert("제품후기 글쓰기가 실패했습니다.");
                    }
                  
                  $("textarea[name=contents]").val("").focus();
               },
               error: function(request, status, error){
                  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
               }
            });
         
          }// end of if ~ else -------------------------

      });
   
      
   }); // end of $(document).ready();------------------------------
   
   
   // Function Declaration 
   
   // **** 특정제품에 대한 좋아요 등록하기 **** // 
   function golikeAdd(pnum) {
   
      if(${empty sessionScope.loginuser}) {
         alert("좋아요 하시려면 먼저 로그인 하셔야 합니다.");
         return;  // 종료 
      }
      
      if(isOrderOK == false) {  // 해당 제품을 구매하지 않은 경우 이라면 
         alert("${requestScope.pvo.pname} 제품을 구매하셔야만 좋아요 투표가 가능합니다.");
      }
      else { // 해당 제품을 구매한 경우라면
         $.ajax({
            url:"<%= request.getContextPath()%>/shop/likeAdd.up",
            type:"POST",
            data:{"pnum":pnum,
                 "userid":"${sessionScope.loginuser.userid}"},
            dataType:"JSON", 
            success:function(json) {
               //alert(json.msg);
                 swal(json.msg);
                 goLikeDislikeCount();
            },
            error: function(request, status, error){
               alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
            }
         });
      }// end of if ~ else ----------------------------
      
   }// end of golikeAdd(pnum)---------------------------
   
   
   // **** 특정제품에 대한 싫어요 등록하기 **** //
   function godisLikeAdd(pnum) {
 
      if(${empty sessionScope.loginuser}) {
         alert("싫어요 하시려면 먼저 로그인 하셔야 합니다.");
         return;  // 종료 
      }
      
      if(isOrderOK == false) {  // 해당 제품을 구매하지 않은 경우 이라면 
         alert("${requestScope.pvo.pname} 제품을 구매하셔야만 싫어요 투표가 가능합니다.");
      }
      else { // 해당 제품을 구매한 경우라면
         $.ajax({
            url:"<%= request.getContextPath()%>/shop/dislikeAdd.up",
            type:"POST",
            data:{"pnum":pnum,
                 "userid":"${sessionScope.loginuser.userid}"},
            dataType:"JSON", 
            success:function(json) {
               //alert(json.msg);
                 swal(json.msg);
                 goLikeDislikeCount();
            },
            error: function(request, status, error){
               alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
            }
         });
      }// end of if ~ else ----------------------------
      
   }// end of golikeAdd(pnum)---------------
   
   
   // **** 특정 제품에 대한 좋아요, 싫어요 갯수를 보여주기 **** //
   function goLikeDislikeCount() {

      $.ajax({
         url:"<%= request.getContextPath()%>/shop/likeDislikeCount.up",
      // type:"GET",
         data:{"pnum":"${requestScope.pvo.pnum}"},
         dataType:"JSON", 
         success:function(json) {
            $("span#likeCnt").html(json.likecnt);
            $("span#dislikeCnt").html(json.dislikecnt);
         },
         error: function(request, status, error){
            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
         }
      });      
      
   }// end of function goLikeDislikeCount()-------------------
   
   
   // 특정 제품의 제품후기글들을 보여주는 함수
   function goCommentListView() {

      $.ajax({
         url:"<%= request.getContextPath()%>/shop/commentList.up",
         type:"GET",
         data:{"fk_pnum":"${requestScope.pvo.pnum}"},
         dataType:"JSON",
         success:function(json) {
            // [{"contents":"제품후기내용물", "name":"작성자이름", "writeDate":"작성일자", "userid":"사용자아이디", "review_seq":제품후기글번호},{"contents":"제품후기내용물2", "name":"작성자이름2", "writeDate":"작성일자2", "userid":"사용자아이디2", "review_seq":제품후기글번호2}]  
             let html = "";
            
            if (json.length > 0) {    
               $.each(json, function(index, item){ 
                  var writeuserid = item.userid;
                  var loginuserid = "${sessionScope.loginuser.userid}";
                                 
                   html +=  "<div> <span class='markColor'>▶</span> "+item.contents+"</div>"
                          + "<div class='customDisplay'>"+item.name+"</div>"      
                          + "<div class='customDisplay'>"+item.writeDate+"</div>";
                   
                   if( loginuserid == "") {
                      html += "<div class='customDisplay spacediv'>&nbsp;</div>";
                   }      
                   else if( loginuserid != "" && writeuserid != loginuserid ) {
                      html += "<div class='customDisplay spacediv'>&nbsp;</div>";
                   }    
                   else if( loginuserid != "" && writeuserid == loginuserid ) {
                      html += "<div class='customDisplay spacediv commentDel' onclick='delMyReview("+item.review_seq+")'>후기삭제</div>";
                   }
               } ); 
            }// end of if -----------------------
            
            else {
               html += "<div>등록된 상품후기가 없습니다.</div>";
            }// end of else ---------------------
            
            $("div#viewComments").html(html);               
               
            
         },
         error: function(request, status, error){
            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
         }
      });      
      
   }
   
   
   // 특정 제품의 제품후기를 삭제하는 함수
   function delMyReview(review_seq) {
      
      const bool = confirm("정말로 제품후기를 삭제하시겠습니까?");
   //  console.log("bool => " + bool); // bool => true , bool => false
      
      if(bool) {
      
         $.ajax({
            url:"<%= request.getContextPath()%>/shop/commentDel.up",
            type:"POST",
            data:{"review_seq":review_seq},
            dataType:"JSON",
            success:function(json) { // {"n":1} 또는 {"n":0}
               if(json.n == 1) {
                  alert("제품후기 삭제가 성공되었습니다.");
                  goCommentListView();
               }
               else {
                  alert("제품후기 삭제가 실패했습니다.");
               }
            },
            error: function(request, status, error){
               alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
            }
         });
      
      }
      
   }// end of function delMyReview(review_seq) {}--------------------------  
   
   
   
   // *** 장바구니 담기 ***//
   function goCart() {
   
      // === 주문량에 대한 유효성 검사하기 === //
      const frm = document.cartOrderFrm;
      
      const regExp = /^[0-9]+$/;  // 숫자만 체크하는 정규표현식
      let oqty = frm.oqty.value;
      const bool = regExp.test(oqty);
      
      if(!bool) {
         // 숫자 이외의 값이 들어온 경우 
         alert("주문갯수는 1개 이상이어야 합니다.");
         frm.oqty.value = "1";
         frm.oqty.focus();
         return; // 종료 
      }
      
      // 문자형태로 숫자로만 들어온 경우
      //oqty = Number(oqty);
      oqty = parseInt(oqty);
      if(oqty < 1) {
         alert("주문갯수는 1개 이상이어야 합니다.");
         frm.oqty.value = "1";
         frm.oqty.focus();
         return; // 종료 
      }
      
      // 주문개수가 1개 이상인 경우
      frm.method = "POST";
      frm.action = "<%= request.getContextPath()%>/shop/cartAdd.up";
      frm.submit();
   
   }// end of function goCart()-------------------------
   
   
   
   // *** 바로주문하기 (form 태그를 사용하지 않고 Ajax 를 사용하여 처리해 보겠습니다.) *** // 
   function goOrder() {
      
      if( ${not empty sessionScope.loginuser} ) {
      
                const regExp = /^[0-9]+$/;  // 숫자만 체크하는 정규표현식
      const bool = regExp.test($("input#spinner").val());
         
          if(!bool) {
            // 주문개수에 숫자 이외의 값(-1 도 - 때문에 숫자 이외의 값임)이 들어온 경우 
            $("p#order_error_msg").html("주문개수는 숫자로만 입력하셔야 합니다.").css('display','block');
            $("input#spinner").val("1");
            $("input#spinner").focus();
            return; // 종료 
          }
          
          const jangoCnt = Number("${requestScope.pvo.pqty}"); // 잔고개수 
          const jumunCnt = Number( $("input#spinner").val() ); // 주문개수
          
          if(jumunCnt == 0) {
             $("p#order_error_msg").html("주문개수는 1개 이상 입력하셔야 합니다.").css('display','block');
            $("input#spinner").val("1");
            $("input#spinner").focus();
            return; // 종료 
          }
          
          if(jangoCnt < jumunCnt) { 
                // 잔고개수 < 주문개수 
            $("p#order_error_msg").html("잔고개수가 주문개수 보다 적으므로 잔고부족으로 인해 주문이 불가합니다.").css('display','block');
            return; // 종료                             
         }

           const currentcoin = Number("${sessionScope.loginuser.coin}"); // 현재코인액
           const totalSaleprice = Number("${requestScope.pvo.saleprice}") * jumunCnt; // 제품총판매가 
         
           const str_totalSaleprice = totalSaleprice.toLocaleString('en'); // 자바스크립트에서 숫자 3자리마다 콤마 찍어주기  
          const str_currentcoin = currentcoin.toLocaleString('en');       // 자바스크립트에서 숫자 3자리마다 콤마 찍어주기 
          
           if( totalSaleprice > currentcoin ) {
            $("p#order_error_msg").html("코인잔액이 부족하므로 주문이 불가합니다.<br>총주문액 : "+str_totalSaleprice+"원, 현재코인액 : "+str_currentcoin+"원").css('display','block');
            return; // 종료
         }
         
           else {
                  $("p#order_error_msg").css('display','none');
              
                  const bool = confirm("총주문액 : "+str_totalSaleprice+"원 결제하시겠습니까?");
                 
                 if(bool) {
                 
                    $.ajax({
                        url:"/MyMVC/shop/orderAdd.up",
                        type:"POST",
                        data:{"pnumjoin":"${requestScope.pvo.pnum}",
                             "oqtyjoin":$("input#spinner").val(), 
                             "totalPricejoin":Number("${requestScope.pvo.saleprice}")*jumunCnt,
                             "sumtotalPrice":Number("${requestScope.pvo.saleprice}")*jumunCnt,
                             "sumtotalPoint":Number("${requestScope.pvo.point}")*jumunCnt},
                        dataType:"JSON",     
                        success:function(json){
                           if(json.isSuccess == 1) {
                              location.href="/MyMVC/shop/orderList.up";
                           }
                           else {
                              location.href="/MyMVC/shop/orderError.up";
                           }
                        },
                        error: function(request, status, error){
                           alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
                        }
                     });
                 }
                 
            }   
      }

      else {
         alert("바로주문하기를 하려면 먼저 로그인 부터 하세요!!");
      }
      
   } // end of function goOrder()----------------------
   
   
</script>

<div style="width: 95%;">
   <div class="my-3">
      <p class="h4 text-center">&raquo;&nbsp;&nbsp;제품 상세 정보&nbsp;&nbsp;&laquo;</p>
   </div>
   
   <div class="row my-3">
      <div class="col-md-6">
          <div class="flip-card">
              <div class="flip-card-inner">         
              <div class="flip-card-front">
                  <img src="/MyMVC/images/${requestScope.pvo.pimage1}" class="img-fluid" style="width:100%;" />
               </div>
               <div class="flip-card-back">
                   <img src="/MyMVC/images/${requestScope.pvo.pimage2}" class="img-fluid" style="width: 100%;" />
               </div>
            </div>
         </div>
      </div>
      
      <div class="col-md-6 pl-5">
          <ul class="list-unstyled">
            <li><span style="color: red; font-size: 12pt; font-weight: bold;">${requestScope.pvo.spvo.sname}</span></li>
            <li>제품번호: ${requestScope.pvo.pnum}</li>
            <li>제품이름: ${requestScope.pvo.pname}</li>
            <li>제조회사: ${requestScope.pvo.pcompany}</li>
            <li>제품설명서: 
              <c:if test="${requestScope.pvo.prdmanual_orginFileName ne '없음'}">
                 <a href="<%= ctxPath%>/shop/fileDownload.up?pnum=${pvo.pnum}">${pvo.prdmanual_orginFileName}</a>
              </c:if>
              <c:if test="${requestScope.pvo.prdmanual_orginFileName eq '없음'}">
                 첨부파일없음
              </c:if>
              </li>
              <li>제품정가: <span style="text-decoration: line-through;"><fmt:formatNumber value="${requestScope.pvo.price}" pattern="###,###" />원</span></li>
              <li>제품판매가: <span style="color: blue; font-weight: bold;"><fmt:formatNumber value="${requestScope.pvo.saleprice}" pattern="###,###" />원</span></li>
              <li>할인율: <span style="color: maroon; font-weight: bold;">[${requestScope.pvo.discountPercent}% 할인]</span></li>
              <li>포인트: <span style="color: green; font-weight: bold;">${requestScope.pvo.point} POINT</span></li>
              <li>잔고갯수: <span style="color: maroon; font-weight: bold;">${requestScope.pvo.pqty} 개</span></li>          
          </ul>
          <%-- ==== 장바구니 담기 폼 ==== --%>
          <form name="cartOrderFrm">       
             <ul class="list-unstyled mt-3">
                <li>
                    <label for="spinner">주문개수&nbsp;</label>
                    <input id="spinner" name="oqty" value="1" style="width: 110px;">
               </li>
               <li>
                  <button type="button" class="btn btn-secondary btn-sm mr-3" onClick="goCart()">장바구니담기</button>
                  <button type="button" class="btn btn-danger btn-sm" onClick="">바로주문하기</button>
               </li>
            </ul>
            <input type="hidden" name="pnum" value="${requestScope.pvo.pnum}" />
         </form>   
              
      </div>
   </div>
   
   <c:if test="${not empty requestScope.imgList}">
      <div class="row">
        <c:forEach var="imgfilename" items="${requestScope.imgList}">
          <div class="col-md-6 my-3">
             <img src="/MyMVC/images/${imgfilename}" class="img-fluid" style="width:100%;" />
          </div>
        </c:forEach>
      </div>
   </c:if>
   
   <div>
        <p id="order_error_msg" class="text-center text-danger font-weight-bold h4"></p>
    </div>
      
   <div class="jumbotron mt-5">
        
        <p class="h4 bg-secondary text-white w-50">${requestScope.pvo.pname} 제품의 특징</p>
        <p>${requestScope.pvo.pcontent}</p>   
   
      <div class="row">
         <div class="col" style="display: flex">
               <h3 style="margin: auto">
                  <i class="fas fa-thumbs-up fa-2x" style="cursor: pointer;" onClick=""></i>
                  <span id="likeCnt" class="badge badge-primary"></span>
                </h3>
         </div>
         
         <div class="col" style="display: flex">
             <h3 style="margin: auto">
                <i class="fas fa-thumbs-down fa-2x" style="cursor: pointer;" onClick=""></i>
                <span id="dislikeCnt" class="badge badge-danger"></span>
             </h3>       
         </div>
      </div>
   
   </div>
   
   
    <p class="h4 text-muted">${requestScope.pvo.pname} 제품 사용후기</p>
    
    <div id="viewComments">
       <%-- 여기가 제품사용 후기 내용이 들어오는 곳이다. --%>
    </div>
     
    <div class="row">
        <div class="col-lg-10">
          <form name="commentFrm">
              <textarea name="contents" style="font-size: 12pt; width: 100%; height: 150px;"></textarea>
              <input type="hidden" name="fk_userid" value="${sessionScope.loginuser.userid}" />
                  <input type="hidden" name="fk_pnum" value="${requestScope.pvo.pnum}" />
          </form>
       </div>
       <div class="col-lg-2" style="display: flex;">
          <button type="button" class="btn btn-outline-secondary w-100 h-100" id="btnCommentOK" style="margin: auto;"><span class="h5">후기등록</span></button>
       </div>
    </div>
   
</div>

<jsp:include page="../footer2.jsp" />
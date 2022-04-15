<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="../header.jsp" /> 

<style>
   
   div#title {
      font-size: 20pt;
    /* border: solid 1px red; */
      padding: 12px 0;
   }
   
   div.mycontent {
        width: 300px;
        padding: 5px 3px;
     }
     
     div.mycontent>.title {
        font-size: 12pt;
        font-weight: bold;
        background-color: #d95050;
        color: #fff;
     }
     
     div.mycontent>.title>a {
        text-decoration: none;
        color: #fff;
     }
     
     
     div.mycontent>.desc {
      /* border: solid 1px red; */
        padding: 10px 0 0 0;
        color: #000;
        font-weight: normal;
        font-size: 9pt;
     }
     
     div.mycontent>.desc>img {
        width: 50px;
        height: 50px;
     }
 
</style>

 <div id="title">매장지도</div>
 <div id="map" style="width:90%; height:600px;"></div>
 <div id="latlngResult"></div>
   
 
   <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=87fdc486b939f17b7c8caa78ea910b5e"></script> 
 
 
 <script type="text/javascript">
  $(document).ready(function(){
   
     // 지도를 담을 영역의 DOM 레퍼런스
     const mapContainer = document.getElementById('map');
     
     // 지도를 생성할때 필요한 기본 옵션
     const options = {
           center: new kakao.maps.LatLng(37.556513150417395, 126.91951995383943), // 지도의 중심좌표. 반드시 존재해야함.
           <%--
               center 에 할당할 값은 kakao.maps.LatLng 클래스를 사용하여 생성한다.
               kakao.maps.LatLng 클래스의 2개 인자값은 첫번째 파라미터는 위도(latitude)이고, 두번째 파라미터는 경도(longitude)이다.
           --%>
           level: 4  // 지도의 레벨(확대, 축소 정도). 숫자가 적을수록 확대된다. 4가 적당함.
     };
     
    
   // 지도 생성 및 생성된 지도객체 리턴
   const mapobj = new kakao.maps.Map(mapContainer, options);
     
   // 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성함.    
   const mapTypeControl = new kakao.maps.MapTypeControl();
 
   // 지도 타입 컨트롤을 지도에 표시함.
   // kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 TOPRIGHT는 오른쪽 위를 의미함.   
   mapobj.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);
   
   // 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성함.   
   const zoomControl = new kakao.maps.ZoomControl();

   // 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 지도에 표시함.
   // kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 RIGHT는 오른쪽을 의미함.    
   mapobj.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

   if (navigator.geolocation) { 
      // HTML5의 geolocation으로 사용할 수 있는지 확인한다 
         
      // GeoLocation을 이용해서 웹페이지에 접속한 사용자의 현재 위치를 확인하여 그 위치(위도,경도)를 지도의 중앙에 오도록 한다. 
      navigator.geolocation.getCurrentPosition(function(position) {
         const latitude = position.coords.latitude;   // 현위치의 위도
         const longitude = position.coords.longitude; // 현위치의 경도
      //   console.log("현위치의 위도: "+latitude+", 현위치의 경도: "+longitude);
         
         // 마커가 표시될 위치를 geolocation으로 얻어온 현위치의 위.경도 좌표로 한다   
         const locPosition = new kakao.maps.LatLng(latitude, longitude);

         // 마커이미지를 기본이미지를 사용하지 않고 다른 이미지로 사용할 경우의 이미지 주소 
           const imageSrc = 'http://localhost:9090/MyMVC/images/pointerPink.png'; 

           // 마커이미지의 크기 
          const imageSize = new kakao.maps.Size(34, 39);
           
          // 마커이미지의 옵션. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정한다. 
          const imageOption = {offset: new kakao.maps.Point(15, 39)};

          // 마커의 이미지정보를 가지고 있는 마커이미지를 생성한다. 
          const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

          // == 마커 생성하기 == //
         const marker = new kakao.maps.Marker({ 
            map: mapobj, 
              position: locPosition, // locPosition 좌표에 마커를 생성 
              image: markerImage     // 마커이미지 설정
         }); 
          
         marker.setMap(mapobj); // 지도에 마커를 표시한다
         
         
         // === 인포윈도우(텍스트를 올릴 수 있는 말풍선 모양의 이미지) 생성하기 === //
         
         // 인포윈도우에 표출될 내용으로 HTML 문자열이나 document element가 가능함.
         const iwContent = "<div style='padding:5px; font-size:9pt;'>여기에 계신가요?<br/><a href='https://map.kakao.com/link/map/현위치(약간틀림),"+latitude+","+longitude+"' style='color:blue;' target='_blank'>큰지도</a> <a href='https://map.kakao.com/link/to/현위치(약간틀림),"+latitude+","+longitude+"' style='color:blue' target='_blank'>길찾기</a></div>";

         // 인포윈도우 표시 위치
          const iwPosition = locPosition;
         
          // removeable 속성을 ture 로 설정하면 인포윈도우를 닫을 수 있는 x버튼이 표시됨
          const iwRemoveable = true; 

          // == 인포윈도우를 생성하기 == 
         const infowindow = new kakao.maps.InfoWindow({
             position : iwPosition, 
             content : iwContent,
             removable : iwRemoveable
         });

         // == 마커 위에 인포윈도우를 표시하기 == //
         infowindow.open(mapobj, marker);

         // == 지도의 센터위치를 locPosition로 변경한다.(사이트에 접속한 클라이언트 컴퓨터의 현재의 위.경도로 변경한다.)
          mapobj.setCenter(locPosition);  //얘를 빼면 고정값 center로 잡힌다.
      });
   }   
   else {
      // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정한다.
      const locPosition = new kakao.maps.LatLng(37.556513150417395, 126.91951995383943);     
        
      // 위의 
      // 마커이미지를 기본이미지를 사용하지 않고 다른 이미지로 사용할 경우의 이미지 주소 
      // 부터
      // 마커 위에 인포윈도우를 표시하기 
      // 까지 동일함.
      
     // 지도의 센터위치를 위에서 정적으로 입력한 위.경도로 변경한다.
       mapobj.setCenter(locPosition);
      
   }// end of if~else------------------------------------------
   
   
   // ================== 지도에 클릭 이벤트를 등록하기 시작======================= //
   // 지도를 클릭하면 클릭한 위치에 마커를 표시하면서 위,경도를 보여주도록 한다.
   
   // == 마커 생성하기 == //
   // 1. 마커이미지 변경
   const click_imageSrc = 'http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png';       
           
   // 2. 마커이미지의 크기 
    const click_imageSize = new kakao.maps.Size(34, 39);   
            
    // 3. 마커이미지의 옵션. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정한다. 
    const click_imageOption = {offset: new kakao.maps.Point(15, 39)};   
      
    // 4. 이미지정보를 가지고 있는 마커이미지를 생성한다. 
    const click_markerImage = new kakao.maps.MarkerImage(click_imageSrc, click_imageSize, click_imageOption);
          
    const movingMarker = new kakao.maps.Marker({ 
      map: mapobj, 
        image: click_markerImage  // 마커이미지 설정
   });
    
    // === 인포윈도우(텍스트를 올릴 수 있는 말풍선 모양의 이미지) 생성하기 === //
   const movingInfowindow = new kakao.maps.InfoWindow({
       removable : false
     //removable : true   // removeable 속성을 ture 로 설정하면 인포윈도우를 닫을 수 있는 x버튼이 표시됨
   });
   
    
   kakao.maps.event.addListener(mapobj, 'click', function(mouseEvent) {         
          
       // 클릭한 위도, 경도 정보를 가져옵니다 
       const latlng = mouseEvent.latLng;
       
       // 마커 위치를 클릭한 위치로 옮긴다.
       movingMarker.setPosition(latlng);
       
       // 인포윈도우의 내용물 변경하기  //말풍선
       movingInfowindow.setContent("<div style='padding:5px; font-size:9pt;'>여기가 어디에요?<br/><a href='https://map.kakao.com/link/map/여기,"+latlng.getLat()+","+latlng.getLng()+"' style='color:blue;' target='_blank'>큰지도</a> <a href='https://map.kakao.com/link/to/여기,"+latlng.getLat()+","+latlng.getLng()+"' style='color:blue' target='_blank'>길찾기</a></div>");  
       
       // == 마커 위에 인포윈도우를 표시하기 == //
       movingInfowindow.open(mapobj, movingMarker);
       
       let htmlMessage = '클릭한 위치의 위도는 ' + latlng.getLat() + ' 이고, '; 
           htmlMessage += '경도는 ' + latlng.getLng() + ' 입니다';
          
       const resultDiv = document.getElementById("latlngResult"); 
       resultDiv.innerHTML = htmlMessage;
   });
   // ================== 지도에 클릭 이벤트를 등록하기 끝======================= //
   
   
   // ================== 지도에 매장위치 마커 보여주기 시작 ====================== //
   // 매장 마커를 표시할 위치와 내용을 가지고 있는 객체 배열 
   const positionArr = [];
   
   $.ajax({
      url:"/MyMVC/shop/locationJSON.up",
      async:false, // !!!!! 지도는 비동기 통신이 아닌 동기 통신으로 해야 한다.!!!!!! ㅁ마커는 비동식 처리.  마크는 시간이 오래걸림  
      //지도는 석세스가 되어진 다음에야 넘어간다.
      dataType:"json",
      success:function(json){
         
         $.each(json, function(index, item){
            const position = {};
            
            position.content = "<div class='mycontent'>"+ 
                               "  <div class='title'>"+ 
                             "    <a href='"+item.storeurl+"' target='_blank'><strong>"+item.storename+"</strong></a>"+  
                             "  </div>"+
                             "  <div class='desc'>"+ 
                             "    <img src='/MyMVC/images/"+item.storeimg+"'>"+  
                             "    <span class='address'>"+item.storeaddress+"</span>"+ 
                             "  </div>"+ 
                             "</div>";
                             
             position.latlng = new kakao.maps.LatLng(item.lat, item.lng);  //위경도
             position.zIndex = item.zIndex;
            
             positionArr.push(position);
         });
         
      },
      error: function(request, status, error){
         alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
       }

   });
   
   
   // store_infowindowArr 은 매장 인포윈도우를 가지고 있는 객체 배열의 용도이다. 
   const store_infowindowArr = new Array(); 
   
   // === 객체 배열 만큼 마커 및 인포윈도우를 생성하여 지도위에 표시한다. === //
   for(let i=0; i<positionArr.length; i++) { //반복문 
      
      // == 매장마커 생성하기 == //
      const store_marker = new kakao.maps.Marker({
         map: mapobj,
         position: positionArr[i].latlng
      });
      
      // 지도에 매장마커를 표시한다.
      store_marker.setMap(mapobj);   //생성되어진 매장만큼 표시
      
      // == 매장 인포윈도우를 생성하기 ==   말풍선
      const store_infowindow = new kakao.maps.InfoWindow({
         content: positionArr[i].content,
         removable: true,
         zIndex : i+1
      });
      
      // 매장 인포윈도우를 가지고 있는 객체를 배열에 넣기 
      store_infowindowArr.push(store_infowindow);
      
      // == 매장마커 위에 매장인포윈도우를 표시하기 == //
      // store_infowindow.open(mapobj, store_marker);
      
      // == 매장마커 위에 매장인포윈도우를 표시하기 == //
      // 마커에 mouseover 이벤트와 mouseout 이벤트를 등록합니다
       // 이벤트 리스너로는 클로저(closure => 함수 내에서 함수를 정의하고 사용하도록 만든것)를 만들어 등록합니다 
       // for문에서 클로저(closure => 함수 내에서 함수를 정의하고 사용하도록 만든것)를 만들어 주지 않으면 마지막 마커에만 이벤트가 등록됩니다
       kakao.maps.event.addListener(store_marker, 'mouseover', makeOverListener(mapobj, store_marker, store_infowindow, store_infowindowArr));
      
   }// end of for----------------------------------------------
   // ================== 지도에 매장위치 마커 보여주기 종료 ====================== //
   
  }); // end of $(document).ready()--------------------------------
  
  
  // !! 인포윈도우를 표시하는 클로저(closure => 함수 내에서 함수를 정의하고 사용하도록 만든것)를 만드는 함수(카카오에서 제공해준것임)입니다 !! // 
  function makeOverListener(mapobj, marker, infowindow, infowindowArr) {
       return function() {
          // alert("infowindow.getZIndex()-1:"+ (infowindow.getZIndex()-1));
          
          for(let i=0; i<infowindowArr.length; i++) {
             if(i == infowindow.getZIndex()-1) {
                infowindowArr[i].open(mapobj, marker);
             }
             else{
                infowindowArr[i].close();
             }
          }
       };
   }
  
 </script>
 
<jsp:include page="../footer.jsp" />    

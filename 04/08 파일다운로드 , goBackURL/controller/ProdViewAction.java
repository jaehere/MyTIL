package myshop.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.controller.AbstractController;
import myshop.model.*;

public class ProdViewAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		 // 로그인 또는 로그아웃을 하면 시작페이지로 가는 것이 아니라 방금 보았던 그 페이지로 그대로 가기 위한 것임.
		 super.goBackURL(request);
		 // 로그인을 하지 않은 상태에서 특정제품을 조회한 후 "장바구니 담기"나 "바로주문하기" 할때와 "제품후기쓰기" 를 할때 
	     // 로그인 하라는 메시지를 받은 후 로그인 하면 시작페이지로 가는 것이 아니라 방금 조회한 특정제품 페이지로 돌아가기 위한 것임.
		 
		 // 카테고리 목록을 조회해오기
	      super.getCategoryList(request);
	      
	      String pnum = request.getParameter("pnum"); // 제품번호 
	      
	      InterProductDAO pdao = new ProductDAO();
	      
	      // 제품번호를 가지고서 해당 제품의 정보를 조회해오기 
	      ProductVO pvo = pdao.selectOneProductByPnum(pnum);
	      
	      // 제품번호를 가지고서 해당 제품의 추가된 이미지 정보를 조회해오기
	      List<String> imgList = pdao.getImagesByPnum(pnum);
	      
	      if(pvo == null) {
	         // GET 방식이므로 사용자가 웹브라우저 주소창에서 장난쳐서 존재하지 않는 제품번호를 입력한 경우
	         String message = "검색하신 제품은 존재하지 않습니다.";
	         String loc = "javascript:history.back()";
	         
	         request.setAttribute("message", message);
	         request.setAttribute("loc", loc);
	         
	      //   super.setRedirect(false);
	         super.setViewPage("/WEB-INF/msg.jsp");
	         
	         return;
	      }
	      else {
	         // 제품이 있는 경우
	         request.setAttribute("pvo", pvo);         // 제품의 정보 
	         request.setAttribute("imgList", imgList); // 해당 제품의 추가된 이미지 정보
	         
	       // super.setRedirect(false);
	         super.setViewPage("/WEB-INF/myshop/prodView.jsp");
	      }
	      

		
	}

}

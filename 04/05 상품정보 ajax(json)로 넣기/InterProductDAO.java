  package myshop.model;

import java.sql.SQLException;
import java.util.*;

public interface InterProductDAO {

	// 시작(메인)페이지에 보여주는 상품이미지파일명을 모두 조회(select)하는 메소드
	// DTO(Data Transfer Object) == VO(Value Object)
	List<ImageVO> imageSelectAll() throws SQLException;

	// tbl_category 테이블에서 카테고리 대분류 번호(cnum), 카테고리코드(code), 카테고리명(cname)을 조회해오기 
	// VO 를 사용하지 않고 Map 으로 처리해보겠습니다.
	List<HashMap<String, String>> getCategoryList() throws SQLException;

	// Ajax(JSON)를 사용하여 상품목록을 "더보기" 방식으로 페이징처리 해주기 위해  스펙별로 제품의 전체개수 알아오기 //
	int totalPspecCount(String string) throws SQLException;

	// Ajax(JSON)를 사용하여 더보기 방식(페이징처리)으로 상품정보를 8개씩 잘라서(start ~ end) 조회해오기
	List<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException;
	
	
}

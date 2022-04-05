package myshop.model;

import java.sql.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ProductDAO implements InterProductDAO {

	// 2. 데이터베이스에가서 이미지파일명을 읽어오기 위해서 커넥션이 필요하다 . 우리는 DBCP를 써올거임. 알면 z컨씨컨븨 하면 됨. jspServlet에서 chap05패키지-PersonDAO_04.java
	private DataSource ds; // DataSource ds 는 아파치톰캣이 제공하는 DBCP(DB Connection Pool) 이다.
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs; 
	
	// 3. 기본생성자 //PersonDAO_04.java에서 긁어와 class명만 바꿔줌..ProductDAO()
		//배치서술자 web.xml , context.xml에 가서 ip username password등을 알 수 있음 . 거기서 jdbc/mymvc_oracle 를 카피해와서 lookup에 넣어준다.
	public ProductDAO() {
		try {
			Context initContext = new InitialContext();
		    Context envContext  = (Context)initContext.lookup("java:/comp/env");
		    ds = (DataSource)envContext.lookup("jdbc/mymvc_oracle");
		}catch(NamingException e) {
			e.printStackTrace();
		}
	}
	
	
	// 4. 자원반납 해주는 메소드
		private void close() {
			
			try {
				if(rs != null) {rs.close(); rs=null;}
				if(pstmt != null) {pstmt.close(); pstmt=null;}
				if(conn != null) {conn.close(); conn=null;}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}// end of private void close()-----------------
	
	// 1. 시작(메인)페이지에 보여주는 상품이미지파일명을 모두 조회(select)하는 메소드
	@Override
	public List<ImageVO> imageSelectAll() throws SQLException {
		
		List<ImageVO> imgList = new ArrayList<>();
		
		//5. try-finally
		try {
			conn= ds.getConnection();
			
			String sql = " select imgno, imgfilename "+
						 " from tbl_main_image "+
						 " order by imgno asc ";
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			
			//우편배달부가 전달해서 sql문 실행해야함
			rs = pstmt.executeQuery(); // select문은 익스큐트쿼리 .리턴타입은 rs
			
			//select되어진 개수만큼 반복문
			while(rs.next()) {
				
				//vo 에 담아야 한다.
				ImageVO imgvo = new ImageVO();
				
				//imgvo에 넣는다
				imgvo.setImgno(rs.getInt(1));
				imgvo.setImgfilename(rs.getString(2));
				
				//한개한개를 담은걸 list에 담아야add한다.  /map은 put
				imgList.add(imgvo);
				
				
			}//end of while-----
			
		} finally {
			close(); //자원반납
		}
		
		return imgList;
		
	}// end of public List<ImageVO> imageSelectAll()--------

	
	
	// tbl_category 테이블에서 카테고리 대분류 번호(cnum), 카테고리코드(code), 카테고리명(cname)을 조회해오기 
	// VO 를 사용하지 않고 Map 으로 처리해보겠습니다.
	@Override
	public List<HashMap<String, String>> getCategoryList() throws SQLException {
		List<HashMap<String, String>> categoryList = new ArrayList<>();
		
		try {
			conn= ds.getConnection();
			
			String sql = " select cnum, code, cname "+
						 " from tbl_category "+
						 " order by cnum asc ";
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			
			//우편배달부가 전달해서 sql문 실행해야함
			rs = pstmt.executeQuery(); // select문은 익스큐트쿼리 .리턴타입은 rs
			
			//select되어진 개수만큼 반복문
			while(rs.next()) {
				HashMap<String, String> map = new HashMap<>();
				map.put("cnum", rs.getString(1));
				map.put("code", rs.getString(2));
				map.put("cname", rs.getString(3));
				
				categoryList.add(map);
				//map을 list에 담는다.
				
				
			}//end of while-----
			
		} finally {
			close(); //자원반납
		}
		return categoryList;
	}

	
	// Ajax(JSON)를 사용하여 상품목록을 "더보기" 방식으로 페이징처리 해주기 위해  스펙별로 제품의 전체개수 알아오기 //
	@Override
	public int totalPspecCount(String fk_snum) throws SQLException {
		
		int totalCount = 0;
		
		
		try {
			conn= ds.getConnection();
			
			String sql = " select count(*) "+
						 " from tbl_product "+
						 " where fk_snum = ? ";
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fk_snum);
			
			//우편배달부가 전달해서 sql문 실행해야함
			rs = pstmt.executeQuery(); // select문은 익스큐트쿼리 .리턴타입은 rs
			
			rs.next();
			
			
			totalCount = rs.getInt(1);
		} finally {
			close(); //자원반납
		}
		
		return totalCount;
	}

	
	
	// Ajax(JSON)를 사용하여 더보기 방식(페이징처리)으로 상품정보를 8개씩 잘라서(start ~ end) 조회해오기
	@Override
	public List<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException {
		
		List<ProductVO> prodList = new ArrayList<>();


		try {
			conn= ds.getConnection();
			
			String sql = "  select pnum, pname, code, pcompany, pimage1, pimage2, pqty, price, saleprice, sname, pcontent, point, pinputdate "
						+ " from  "
						+ " ( "
						+ "    select row_number() over(order by pnum desc) AS RNO  "
						+ "         , P.pnum, P.pname, C.code, P.pcompany, P.pimage1, P.pimage2, P.pqty, P.price, P.saleprice, S.sname, P.pcontent, P.point "
						+ "         , to_char(P.pinputdate, 'yyyy-mm-dd') AS pinputdate "
						+ "    from tbl_product P "
						+ "    JOIN tbl_category C "
						+ "    ON P.fk_cnum = C.cnum "
						+ "    JOIN tbl_spec S "
						+ "    ON P.fk_snum = S.snum "
						+ "    where S.sname = ? "
						+ " ) V "
						+ " where V.RNO between ? and ? ";
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("sname"));
			pstmt.setString(2, paraMap.get("start"));
			pstmt.setString(3, paraMap.get("end"));
			
			
			//우편배달부가 전달해서 sql문 실행해야함
			rs = pstmt.executeQuery(); // select문은 익스큐트쿼리 .리턴타입은 rs
			
			
			//select되어진 개수만큼 반복문
			while(rs.next()) {
				ProductVO pvo = new ProductVO();
				
				pvo.setPnum(rs.getInt(1));     // 제품번호
	             pvo.setPname(rs.getString(2)); // 제품명
	             
	             CategoryVO categvo = new CategoryVO(); 
	             categvo.setCode(rs.getString(3)); 
	             
	             pvo.setCategvo(categvo);           // 카테고리코드 
	             pvo.setPcompany(rs.getString(4));  // 제조회사명
	             pvo.setPimage1(rs.getString(5));   // 제품이미지1   이미지파일명
	             pvo.setPimage2(rs.getString(6));   // 제품이미지2   이미지파일명
	             pvo.setPqty(rs.getInt(7));         // 제품 재고량
	             pvo.setPrice(rs.getInt(8));        // 제품 정가
	             pvo.setSaleprice(rs.getInt(9));    // 제품 판매가(할인해서 팔 것이므로)
	               
	             SpecVO spvo = new SpecVO(); 
	             spvo.setSname(rs.getString(10)); 
	             
	             pvo.setSpvo(spvo); // 스펙 
	               
	             pvo.setPcontent(rs.getString(11));     // 제품설명 
	             pvo.setPoint(rs.getInt(12));         // 포인트 점수        
	             pvo.setPinputdate(rs.getString(13)); // 제품입고일자
				
	             
	             prodList.add(pvo);
			}//end of while-----
			
		} finally {
			close(); //자원반납

		}
		
		return prodList;
	}
	
	
	
	
	

}

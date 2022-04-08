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
	
	
	// spec 목록을 보여주고자 한다.
	@Override
	public List<SpecVO> selectSpecList() throws SQLException {
		
		List<SpecVO> specList = new ArrayList<>();
		
		try {
			conn= ds.getConnection();
			
			String sql = " select snum, sname "+
						 " from tbl_spec "+
						 " order by snum asc ";
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			
			//우편배달부가 전달해서 sql문 실행해야함
			rs = pstmt.executeQuery(); // select문은 익스큐트쿼리 .리턴타입은 rs
			
			while(rs.next()) {
				SpecVO spvo = new SpecVO(); //리턴타입이 specVO이니깐
				spvo.setSnum(rs.getInt(1));
				spvo.setSname(rs.getString(2));
				
				specList.add(spvo);
				
			}
			
			
		} finally {
			close(); //자원반납
		}
		
		
		return specList;
	}

	
	// 제품번호 채번 해오기
	@Override
	public int getPnumOfProduct() throws SQLException {
		
		int pnum = 0;
		
		
		try {
			conn= ds.getConnection();
			
			String sql = " select SEQ_TBL_PRODUCT_PNUM.nextval "
					   + " from dual ";
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			
			//우편배달부가 전달해서 sql문 실행해야함
			rs = pstmt.executeQuery(); // select문은 익스큐트쿼리 .리턴타입은 rs
			
			rs.next();
			pnum = rs.getInt(1);
				
			
			
		} finally {
			close(); //자원반납
		}
		
		return pnum;
	}

	
	//  tbl_product 테이블에 제품정보 insert 하기  
	@Override
	public int productInsert(ProductVO pvo) throws SQLException {
		
		int result = 0;
		
		//dml문
		
		try {
		
			conn= ds.getConnection();
			String sql = " insert into tbl_product(pnum, pname, fk_cnum, pcompany, pimage1, pimage2, prdmanual_systemFileName, prdmanual_orginFileName, pqty, price, saleprice, fk_snum, pcontent, point) " +  
                    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pstmt = conn.prepareStatement(sql);
			
			//제품번호는 채번된 번호를 setPnum 해왔다. getPnum 하면된다. 
			pstmt.setInt(1, pvo.getPnum());
			pstmt.setString(2, pvo.getPname());
	        pstmt.setInt(3, pvo.getFk_cnum());    
	        pstmt.setString(4, pvo.getPcompany()); 
	        pstmt.setString(5, pvo.getPimage1());    
	        pstmt.setString(6, pvo.getPimage2()); 
	        pstmt.setString(7, pvo.getPrdmanual_systemFileName());
	        pstmt.setString(8, pvo.getPrdmanual_orginFileName());
	        pstmt.setInt(9, pvo.getPqty()); 
	        pstmt.setInt(10, pvo.getPrice());
	        pstmt.setInt(11, pvo.getSaleprice());
	        pstmt.setInt(12, pvo.getFk_snum());
	        pstmt.setString(13, pvo.getPcontent());
	        pstmt.setInt(14, pvo.getPoint());
			
	        result = pstmt.executeUpdate();
	        
		}finally {
			close();
		}
		
		
		return result;
	}

	// tbl_product_imagefile 테이블에 insert 하기
	@Override
	public int product_imagefile_Insert(Map<String, String> paraMap) throws SQLException {
		int result = 0;
		
		//dml문
		
		try {
		
			conn= ds.getConnection();
			
			String sql = " insert into tbl_product_imagefile(imgfileno, fk_pnum, imgfilename) "+ 
                    " values(seqImgfileno.nextval, ?, ?) ";
                  
			pstmt = conn.prepareStatement(sql);
	
			pstmt.setInt(1, Integer.parseInt(paraMap.get("pnum")));
			pstmt.setString(2, paraMap.get("attachFileName"));
			
	        result = pstmt.executeUpdate();
	        
		}finally {
			close();
		}
		
		
		return result;
	}

	
	// 제품번호를 가지고서 해당 제품의 정보를 조회해오기
	@Override
	public ProductVO selectOneProductByPnum(String pnum) throws SQLException {
	
		ProductVO pvo = null;
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select S.sname, pnum, pname, pcompany, price, saleprice, point, pqty, pcontent, pimage1, pimage2, prdmanual_systemFileName, nvl(prdmanual_orginFileName,'없음') AS prdmanual_orginFileName "
						+ " from "
						+ " ( "
						+ " select fk_snum, pnum, pname, pcompany, price, saleprice, point, pqty, pcontent, pimage1, pimage2, prdmanual_systemFileName, prdmanual_orginFileName "
						+ " from tbl_product "
						+ " where pnum = ? "
						+ " ) P JOIN tbl_spec S "
						+ " ON P.fk_snum = S.snum ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				 String sname = rs.getString(1);     // "HIT", "NEW", "BEST" 값을 가짐 
	             int    npnum = rs.getInt(2);        // 제품번호
	             String pname = rs.getString(3);     // 제품명
	             String pcompany = rs.getString(4);  // 제조회사명
	             int    price = rs.getInt(5);        // 제품 정가
	             int    saleprice = rs.getInt(6);    // 제품 판매가
	             int    point = rs.getInt(7);        // 포인트 점수
	             int    pqty = rs.getInt(8);         // 제품 재고량
	             String pcontent = rs.getString(9);  // 제품설명
	             String pimage1 = rs.getString(10);  // 제품이미지1
	             String pimage2 = rs.getString(11);  // 제품이미지2
	             String prdmanual_systemFileName = rs.getString(12); // 파일서버에 업로드되어지는 실제 제품설명서 파일명
	             String prdmanual_orginFileName = rs.getString(13);  // 웹클라이언트의 웹브라우저에서 파일을 업로드 할때 올리는 제품설명서 파일명
				
	             pvo = new ProductVO();
	             
	             SpecVO spvo = new SpecVO();
	             spvo.setSname(sname);
	             
	             pvo.setSpvo(spvo);
	             
	             pvo.setPnum(npnum);
	             pvo.setPname(pname);
	             pvo.setPcompany(pcompany);
	             pvo.setPrice(price);
	             pvo.setSaleprice(saleprice);
	             pvo.setPoint(point);
	             pvo.setPqty(pqty);
	             pvo.setPcontent(pcontent);
	             pvo.setPimage1(pimage1);
	             pvo.setPimage2(pimage2);
	             pvo.setPrdmanual_systemFileName(prdmanual_systemFileName);
	             pvo.setPrdmanual_orginFileName(prdmanual_orginFileName);
	             
	             
			}//end of if(rs.next())--------------
			
			
		}finally {
			close();
		}
		
		return pvo;
	}

	
	// 제품번호를 가지고서 해당 제품의 추가된 이미지 정보를 조회해오기
	@Override
	public List<String> getImagesByPnum(String pnum) throws SQLException  {
		
		List<String> imgList = new ArrayList<>();
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select imgfilename "
						+ " from tbl_product_imagefile "
						+ " where fk_pnum = ? ";
			

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) { //select된게 있다.
				String imgfilename = rs.getString(1); // 이미지파일명
				imgList.add(imgfilename);
				
			}
			
		}finally {
			close();
		}
		
		
		return imgList;
	}

	// 제품번호를 가지고서 해당 제품의 제품설명서 첨부파일의 서버에 업로드되어진 파일명과 오리지널 파일명을 조회해오기
	@Override
	public Map<String, String> getPrdmanualFileName(String pnum) throws SQLException {
		
		Map<String, String> map = new HashMap<>();
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select prdmanual_systemFileName, prdmanual_orginFileName  "
						+ " from tbl_product "
						+ " where pnum = ? ";
			

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pnum);
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) { //select된게 있다.
				map.put("prdmanual_systemFileName",rs.getString(1));
				// 파일서버에 업로드되어지는 실제 제품설명서 파일명
				
				map.put("prdmanual_orginFileName",rs.getString(2));
				//웹클라이언트의 웹브라우저에서 파일을 업로드 할 때 올리는 제품설명서 파일명
			}//end of if -----------------
			
		}finally {
			close();
		}
		
		
		return map ;
		
	}
	

	//== 장바구니 담기 == //
	// 장바구니 테이블에 해당 제품이 존재하지 않는 경우에는 tbl_cart 테이블에 insert를 해야 하고,
	// 장바구니 테이블에 해당 제품이 존재하는 경우에는 또 그 제품을 추가해서 장바구니 담기를 한다라면 tbl_cart 테이블에 update를 해야한다. 
	@Override
	public int addCart(Map<String, String> paraMap) throws SQLException {

		int n = 0;
		
		try {
			conn = ds.getConnection();
			
			/*
	            먼저 장바구니 테이블(tbl_cart)에 어떤 회원이 새로운 제품을 넣는 것인지,
	            아니면 또 다시 제품을 추가로 더 구매하는 것인지를 알아야 한다.
	            이것을 알기위해서 어떤 회원이 어떤 제품을  장바구니 테이블(tbl_cart) 넣을때
	            그 제품이 이미 존재하는지 select 를 통해서 알아와야 한다.
	            
	          -------------------------------------------
	           cartno   fk_userid     fk_pnum   oqty  
	          -------------------------------------------
	             1      leejh          7         2     
	             2      seoyh          6         3     
	             3      leess          7         5     
	         */
			
			String sql = " select * "
					   + " from tbl_cart "
					   + " where fk_userid = ? and "
					   + " fk_pnum = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				//어떤 제품을 추가로 장바구니 에 넣고자 하는 경우
				
				int cartno = rs.getInt(1);
				
				sql = " update tbl_cart set oqty = oqty +? "
					+ " where cartno = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(paraMap.get("oqty")) );
				pstmt.setInt(2, cartno);
				
				n = pstmt.executeUpdate(); //insert 해라
				
			}
			else{
				//장바구니에 존재하지 않는 새로운 제품을 넣고자 하는 경우
				sql = " insert into tbl_cart( cartno, fk_userid, fk_pnum, oqty , registerday ) "
					+ " values(seq_tbl_cart_cartno.nextval, ?, ?, ?, default ) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("userid"));
				pstmt.setInt(2, Integer.parseInt(paraMap.get("pnum")) );
				pstmt.setInt(3, Integer.parseInt(paraMap.get("oqty")));
				
				n = pstmt.executeUpdate(); //insert 해라
			}
		}finally {
			close();
		}
		
		return n ; 
		
		
	}

	// 로그인한 사용자의 장바구니 목록을 조회하기 
	@Override
	public List<CartVO> selectProductCart(String userid) throws SQLException {
		
		List<CartVO> cartList = new ArrayList(); // null로 하고 그 뒤에 new 해도 괜찮다!

		
		try {
			conn = ds.getConnection();
			
			
			String sql = " select A.cartno, A.fk_userid, A.fk_pnum, "+
						 "        B.pname, B.pimage1, B.price, B.saleprice, B.point, A.oqty "+
						 " from tbl_cart A join tbl_product B "+
						 " on A.fk_pnum = B.pnum "+
						 " where A.fk_userid = ? "+
						 " order by A.cartno desc ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				int cartno = rs.getInt("cartno");
	            String fk_userid = rs.getString("fk_userid");
	            int fk_pnum = rs.getInt("fk_pnum");
	            String pname = rs.getString("pname");
	            String pimage1 = rs.getString("pimage1");
	            int price = rs.getInt("price");
	            int saleprice = rs.getInt("saleprice");
	            int point = rs.getInt("point");
	            int oqty = rs.getInt("oqty");  // 주문량
				
	            ProductVO prodvo = new ProductVO();
	            prodvo.setPnum(fk_pnum);
	            prodvo.setPname(pname);
	            prodvo.setPimage1(pimage1);
	            prodvo.setPrice(price);
	            prodvo.setSaleprice(saleprice);
	            prodvo.setPoint(point);
	            
	            // ***** !!!! 중요함 !!! ***** //
	            
	            // ***** !!!! 중요함 !!! ***** //
	            
	            CartVO cvo = new CartVO();
	            cvo.setCartno(cartno);
	            cvo.setUserid(fk_userid);
	            cvo.setPnum(fk_pnum);
	            cvo.setOqty(oqty);
	            cvo.setProd(prodvo);
	            
	            cartList.add(cvo); //카트리스트에 cvo를 담아서
			}//end of while(rs.next()) ----------------------
			
		}finally {
			close();
		}
		
		
		return cartList; //넘긴다.
	}
	
	
	
	
	

}

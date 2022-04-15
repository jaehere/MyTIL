package myshop.model;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import member.model.MemberVO;
import util.security.AES256;
import util.security.SecretMyKey;

public class ProductDAO implements InterProductDAO {

	// 2. 데이터베이스에가서 이미지파일명을 읽어오기 위해서 커넥션이 필요하다 . 우리는 DBCP를 써올거임. 알면 z컨씨컨븨 하면 됨. jspServlet에서 chap05패키지-PersonDAO_04.java
	private DataSource ds; // DataSource ds 는 아파치톰캣이 제공하는 DBCP(DB Connection Pool) 이다.
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs; 
	private AES256 aes;//AES를 위한 객체를 하나 만든다.
	
	// 3. 기본생성자 //PersonDAO_04.java에서 긁어와 class명만 바꿔줌..ProductDAO()
		//배치서술자 web.xml , context.xml에 가서 ip username password등을 알 수 있음 . 거기서 jdbc/mymvc_oracle 를 카피해와서 lookup에 넣어준다.
	public ProductDAO() {
		try {
			Context initContext = new InitialContext();
		    Context envContext  = (Context)initContext.lookup("java:/comp/env");
		    ds = (DataSource)envContext.lookup("jdbc/mymvc_oracle");
		    
		    aes = new AES256(SecretMyKey.KEY);
		    //SecretMyKey.KEY 은 우리가 만든비밀키이다.
		    
		}catch(NamingException e) {
			e.printStackTrace();
		}catch(UnsupportedEncodingException e) {
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
	
	
	
	// 특정 카테고리에 속하는 제품들을 페이지바를 이용한 페이징 처리하여 조회(select)해오기
	@Override
	public List<ProductVO> selectProductByCategory(Map<String, String> paraMap) throws SQLException {
		
		  List<ProductVO> prodList = new ArrayList<>();
	      
	      try {
	          conn = ds.getConnection();
	          
	          String sql = "select cname, sname, pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point, pinputdate "+
	                "from "+
	                "( "+
	                "    select rownum AS RNO, cname, sname, pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point, pinputdate "+ 
	                "    from "+
	                "    ( "+
	                "        select C.cname, S.sname, pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point, pinputdate "+
	                "        from "+
	                "            (select pnum, pname, pcompany, pimage1, pimage2, pqty, price, saleprice, pcontent, point "+
	                "                  , to_char(pinputdate, 'yyyy-mm-dd') as pinputdate, fk_cnum, fk_snum  "+
	                "             from tbl_product  "+
	                "             where fk_cnum = ? "+
	                "             order by pnum desc "+
	                "        ) P "+
	                "        JOIN tbl_category C "+
	                "        ON P.fk_cnum = C.cnum "+
	                "        JOIN tbl_spec S "+
	                "        ON P.fk_snum = S.snum "+
	                "    ) V "+
	                ") T "+
	                "where T.RNO between ? and ? ";
	          
	          pstmt = conn.prepareStatement(sql);
	          
	          int currentShowPageNo = Integer.parseInt( paraMap.get("currentShowPageNo") );
	          int sizePerPage = 10; // 한 페이지당 화면상에 보여줄 제품의 개수는 10 으로 한다.
	          
	          pstmt.setString(1, paraMap.get("cnum"));
	          pstmt.setInt(2, (currentShowPageNo * sizePerPage) - (sizePerPage - 1)); // 공식
	          pstmt.setInt(3, (currentShowPageNo * sizePerPage)); // 공식 
	          
	          rs = pstmt.executeQuery();
	          
	          while( rs.next() ) {
	             
	             ProductVO pvo = new ProductVO();
	             
	             pvo.setPnum(rs.getInt("pnum"));      // 제품번호
	             pvo.setPname(rs.getString("pname")); // 제품명
	             
	             CategoryVO categvo = new CategoryVO(); 
	             categvo.setCname(rs.getString("cname"));  // 카테고리명  
	             
	             pvo.setCategvo(categvo);                   // 카테고리 
	             pvo.setPcompany(rs.getString("pcompany")); // 제조회사명
	             pvo.setPimage1(rs.getString("pimage1"));   // 제품이미지1   이미지파일명
	             pvo.setPimage2(rs.getString("pimage2"));   // 제품이미지2   이미지파일명
	             pvo.setPqty(rs.getInt("pqty"));            // 제품 재고량
	             pvo.setPrice(rs.getInt("price"));          // 제품 정가
	             pvo.setSaleprice(rs.getInt("saleprice"));  // 제품 판매가(할인해서 팔 것이므로)
	               
	             SpecVO spvo = new SpecVO(); 
	             spvo.setSname(rs.getString("sname")); // 스펙이름 
	             
	             pvo.setSpvo(spvo); // 스펙 
	               
	             pvo.setPcontent(rs.getString("pcontent"));       // 제품설명 
	             pvo.setPoint(rs.getInt("point"));              // 포인트 점수        
	             pvo.setPinputdate(rs.getString("pinputdate")); // 제품입고일자                                             
	             
	             prodList.add(pvo);
	          }// end of while-----------------------------------------
	          
	      } finally {
	         close();
	      }      
	      
	      return prodList;
	}
	
	//  페이지바를 만들기 위해서 특정카테고리의 제품개수에 대한 총페이지수 알아오기(select)  
	@Override
	public int getTotalPage(String cnum) throws SQLException {
		  int totalPage = 0;
	      
	      try {
	         conn = ds.getConnection();
	         
	         	//ceil로 DB에서 바로 구해오는게 편하다.
	         String sql = " select ceil( count(*)/10 ) "  // 10 이 sizePerPage 이다.
	                  + " from tbl_product "
	                  + " where fk_cnum = ? "; 
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         pstmt.setString(1, cnum);
	               
	         rs = pstmt.executeQuery();
	         
	         rs.next();
	         
	         totalPage = rs.getInt(1);
	         
	      } finally {
	         close();
	      }      
	      
	      return totalPage;
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
	            
	            prodvo.setTotalPriceTotalPoint(oqty);            
	            
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


	//장바구니 테이블에서 특정제품을 제거하기 
	@Override
	public int delCart(String cartno) throws SQLException {
		int n = 0;
		
		try {
			conn = ds.getConnection();
			
			String sql = " delete from tbl_cart "
					   + " where cartno = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,  cartno);
			
			n = pstmt.executeUpdate();
			
			
		}finally {
			close();
		}
		
		return n;
	}

	
	//장바구니 테이블에서 특정제품에 주문량을 변경하기 
	@Override
	public int updateCart(Map<String, String> paraMap) throws SQLException {
		int n = 0;
		
		try {
			conn = ds.getConnection();
			
			String sql = " update tbl_cart set oqty = ? "
					   + " where cartno = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("oqty")); //map에서 끌어온다.
			pstmt.setString(2, paraMap.get("cartno"));
			
			n = pstmt.executeUpdate();
			
			
		}finally {
			close();
		}
		
		return n;
	}

	
	// 로그인한 사용자의 장바구니에 담긴 주문총액합계 및 총포인트합계 알아오기
	@Override
	public Map<String, String> selectCartSumPricePoint(String userid) throws SQLException {
		Map<String, String> resultMap = new HashMap<>();
		
		try {
			conn = ds.getConnection();
			
			
			String sql =  " select NVL(sum( B.saleprice * A.oqty),0) as  sumtotalPrice "
						+ "      , NVL(sum(B.point * A.oqty),0)  as sumtotalPoint "
						+ " from tbl_cart A join tbl_product B  "
						+ " on A.fk_pnum = B.pnum  "
						+ " where A.fk_userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			
			rs = pstmt.executeQuery();
			rs.next();
			
			resultMap.put("sumtotalPrice", rs.getString(1));
			resultMap.put("sumtotalPoint", rs.getString(2));
			
			
		}finally {
			close();
		}
		
		
		return resultMap;
	}

	
	// 주문번호(시퀀스 seq_tbl_order 값)을 채번해오는 것.
	@Override
	public int getSeq_tbl_order() throws SQLException {
		
		int seq = 0;
		try {
			conn = ds.getConnection();
			
			
			String sql =  " select seq_tbl_order.nextval "
						+ " from dual ";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			rs.next();
			
			seq = rs.getInt(1);
			
		}finally {
			close();
		}
		
		return seq;
	}

	// ===== Transaction 처리하기 ===== // 
    // >> 앞에서 미리 했으므로 안함. 1. 주문 테이블에 입력되어야할 주문전표를 채번(select)하기
	
    // 2. 주문 테이블에 채번해온 주문전표, 로그인한 사용자, 현재시각을 insert 하기(수동커밋처리)
    // 3. 주문상세 테이블에 채번해온 주문전표, 제품번호, 주문량, 주문금액을 insert 하기(수동커밋처리)
    // 4. 제품 테이블에서 제품번호에 해당하는 잔고량을 주문량 만큼 감하기(수동커밋처리) 
    
    // 5. 장바구니 테이블에서 cartnojoin 값에 해당하는 행들을 삭제(delete OR update)하기(수동커밋처리) 
    // >> 장바구니에서 주문을 한 것이 아니라 특정제품을 바로주문하기를 한 경우에는 장바구니 테이블에서 행들을 삭제할 작업은 없다. << 

    // 6. 회원 테이블에서 로그인한 사용자의 coin 액을 sumtotalPrice 만큼 감하고, point 를 sumtotalPoint 만큼 더하기(update)(수동커밋처리) 
    // 7. **** 모든처리가 성공되었을시 commit 하기(commit) **** 
    // 8. **** SQL 장애 발생시 rollback 하기(rollback) **** 
	@Override
	public int orderAdd(Map<String, Object> paraMap) throws SQLException {
		
		int isSuccess = 0;
		int n1=0, n2=0, n3=0, n4=0, n5=0;
		
		try {
			conn = ds.getConnection();
			
			conn.setAutoCommit(false); // 수동커밋
			
			 // 2. 주문개요 테이블에 채번해온 주문전표, 로그인한 사용자, 현재시각을 insert 하기(수동커밋처리)
			String sql = " insert into tbl_order(odrcode, fk_userid, odrtotalPrice, odrtotalPoint, odrdate) "
						+" values(?,?,?,?, default) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, (String)paraMap.get("odrcode"));   //현재 이 map은 object 인데, odrcode는 put되어진게 string타입이기 때문에 캐스팅
			pstmt.setString(2, (String)paraMap.get("userid"));
			pstmt.setInt(3, Integer.parseInt( (String)paraMap.get("sumtotalPrice") ));
			pstmt.setInt(4, Integer.parseInt((String)paraMap.get("sumtotalPoint")));
			
			n1 = pstmt.executeUpdate();
			System.out.println("~~~~ n1 : "+ n1);
			
			// 3. 주문상세 테이블에 채번해온 주문전표, 제품번호, 주문량, 주문금액을 insert 하기(수동커밋처리)
			if(n1 == 1) {
				
				String[] pnumArr = (String[])paraMap.get("pnumArr");
				String[] oqtyArr = (String[])paraMap.get("oqtyArr");
				String[] totalPriceArr = (String[])paraMap.get("totalPriceArr");
				
				int cnt = 0;
				for(int i=0; i<pnumArr.length; i++) {
					sql = " insert into tbl_orderdetail(odrseqnum, fk_odrcode, fk_pnum, oqty, odrprice, deliverStatus) "
						+ " values(seq_tbl_orderdetail.nextval, ? , to_number(?), to_number(?) ,to_number(?), default )  ";
					
					/*
					 	위치홀더에 값을 넣어준다.

						오라클에서 주문상세에 가보기
						
						,fk_pnum        number(8)            not null   -- 제품번호
						,oqty           number               not null   -- 주문량
						,odrprice       number               not null   -- "주문할 그때 그당시의 실제 판매가격" ==> insert 시 tbl_product 테이블에서 해당제품의 saleprice 컬럼값을 읽어다가 넣어주어야 한다.
						전부다 number타입
						
						select할 때는 숫자가 문자로 호환되어지지만
						입력될떄는 넘버타입은 넘버ㅂ만 들어오는게 원칙이다.
						//배열은 String이고, 데이터베이스 상에선 number타입인 것들을 to_number()로 처리해줌. 
					 */
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, (String)paraMap.get("odrcode"));
					pstmt.setString(2, pnumArr[i]);  //pnumArr[i]는  String타입이고, 원래 데이터는 number이므로, oracle에서 to_number 처리를 해주는 걸 한다. 
					pstmt.setString(3, oqtyArr[i]);
					pstmt.setString(4, totalPriceArr[i]);
					
					pstmt.executeUpdate();
					cnt++;
				}//end of for-----------
				
				if(cnt == pnumArr.length) {
					//cnt와 배열의 갯수가 같다라면 성공이다.
					n2=1;
					
				}
				System.out.println("~~~~~~~~n2 : "+ n2);
				
				
			}//end of if(n1 == 1)-------------------
			
			// 4. 제품 테이블에서 제품번호에 해당하는 잔고량을 주문량 만큼 감하기(수동커밋처리) 
			if(n2==1) {
				//당연히 n1이 1일때, n2가 1일 수 있따.
				
				//잔고량만큼 감하겠다. 반복문 필요
				String[] pnumArr = (String[])paraMap.get("pnumArr");
				String[] oqtyArr = (String[])paraMap.get("oqtyArr");
				
				int cnt = 0;
				for(int i=0; i<pnumArr.length; i++) {
					sql = " update tbl_product set pqty = pqty - ? "
						+ " where pnum = ?  ";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt( oqtyArr[i] ));
					pstmt.setString(2, pnumArr[i]);
					
					
					pstmt.executeUpdate();
					cnt++;
				}//end of for-----------
				
				if(cnt == pnumArr.length) {
					//cnt와 배열의 갯수가 같다라면 성공이다.
					n3=1;
					
				}
				System.out.println("~~~~~~~~n3 : "+ n3);
				
			}//end of if(n2==1)-------------------------
			
			// 5. 장바구니 테이블에서 cartnojoin 값에 해당하는 행들을 삭제(delete OR update)하기(수동커밋처리) 
		    // >> 장바구니에서 주문을 한 것이 아니라 특정제품을 바로주문하기를 한 경우에는 장바구니 테이블에서 행들을 삭제할 작업은 없다. << 
			if( paraMap.get("cartnojoin") != null && n3==1 ) { //위에서 전부다 성공되었더라면 장바구니 비우기를 한다.
				// paraMap.get("cartnoArr") String타입의 배열을 join으로 바꾸어와야 한다.원래는 그랬는데 OrderAddAction 에서 cartnoArr배열이 아닌  cartnojoin으로 바꿔놈. 
				
				String cartnojoin = (String) paraMap.get("cartnojoin");
				
				sql = " delete from tbl_cart "
					+ " where cartno in("+cartnojoin+") ";
					

				
					//  !!! in 절은 위와 같이 직접 변수로 처리해야 함. !!!  in절은 위치홀더(?) 쓰면 안됨
				    //  in 절에 사용되는 것들은 컬럼의 타입을 웬만하면 number 로 사용하는 것이 좋다. 
				    //  왜냐하면 varchar2 타입으로 되어지면 데이터 값에 앞뒤로 홑따옴표 ' 를 붙여주어야 하는데 이것을 만들수는 있지만 귀찮기 때문이다.     in('10','20','30')
				            
			        /*   
			            sql = " delete from tbl_cart "
			               + " where cartno in (?) ";
			            // !!! 위와 같이 위치홀더 ? 를 사용하면 하면 안됨. !!!       
			        */
				
				pstmt = conn.prepareStatement(sql);
				n4 = pstmt.executeUpdate();
				System.out.println("~~~~~n4 : "+ n4);
				//만약에 장바구니 비우기를 할 행이 3개이라면
				// ~~~~~n4 : 3 이 나올 것이다.
				
			}//end of if(paraMap.get("cartnojoin") != null && n3==1 )------------
			
			if( paraMap.get("cartnojoin") == null && n3==1 ) {//장바구니 비우기는 없지만 n3까지는 성공했을 때 이다.
				// "제품 상세 정보" 페이지에서 "바로주문하기"를 한 경우
				// 장바구니 번호인 paraMap.get("cartnojoin") 이 없는 것이다.
				
				
				n4=1;
				
				System.out.println("~~~ 바로주문하기인 경우 n4 : "+n4);
				//바로주문하기인 경우 n4 : 1
			}//end of if( paraMap.get("cartnojoin") == null && n3==1 ) ---------
			
			// 6. 회원 테이블에서 로그인한 사용자의 coin 액을 sumtotalPrice 만큼 감하고, point 를 sumtotalPoint 만큼 더하기(update)(수동커밋처리) 
			if(n4>0) {
				sql = " update tbl_member set coin = coin - ? "
					+ "                     , point = point + ? "
					+ " where userid = ? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, Integer.parseInt( (String)paraMap.get("sumtotalPrice")) );  // 연산 해야하므로 배열->String->int로 캐스팅 해줌.
				pstmt.setInt(2, Integer.parseInt( (String)paraMap.get("sumtotalPoint")));
				pstmt.setString(3, (String)paraMap.get("userid"));
				
				n5 = pstmt.executeUpdate();//n5에 넣어준다.
				
				System.out.println("~~~~n5 : "+n5);
				//n5는 1값이 나올 것이다.
				
				
			}//end of if(n4>0) ---------------
			
			// 7. **** 모든처리가 성공되었을시 commit 하기(commit) **** 
			if(n1*n2*n3*n4*n5 > 0) { //default 가 0이니깐 위에서 전부다 성공되었으면 다 0보다 크기 때문에 쟤네를 다 곱했을 때 0이 될리가 없음
				conn.commit();
				conn.setAutoCommit(false); // 자동커밋으로 전환
				
				System.out.println("n1*n2*n3*n4*n5 = "+(n1*n2*n3*n4*n5));
			
				isSuccess = 1; //commit해주고 1값을 준다.
				
			}
			
			/*
			  
			 ~~~~ n1 : 1
			 ~~~~~~~~n2 : 1
			 ~~~~~~~~n3 : 1
			 ~~~~~n4 : 1
			 ~~~~n5 : 1
			 n1*n2*n3*n4*n5 = 1
			 >>> /shop/orderList.up 은 URI 패턴에 매핑된 클래스는 없습니다. <<<
			 
			 */
			
			
		}catch(SQLException e) {
			e.printStackTrace(); 
			// 8. **** SQL 장애 발생시 rollback 하기(rollback) **** 
			conn.rollback();
			conn.setAutoCommit(false); 
			// 자동커밋으로 전환
			
			isSuccess = 0; //default 가 0값 굳히 할 필요는 없음.
		}finally {
			close();
		}
		
		
		return isSuccess;
	}

	
	// 주문한 제품에 대해 email 보내기 시 email 내용에 넣을 주문한 제품번호들에 대한 제품정보를 얻어오는 것.
	@Override
	public List<ProductVO> getJumunProductList(String pnumes) throws SQLException {
		
		List<ProductVO> jumunProductList = new ArrayList<>();
		


		try {
			conn= ds.getConnection();
			
			String sql = " select pnum, pname, fk_cnum, pcompany, pimage1, pimage2, prdmanual_systemFileName, prdmanual_orginFileName, pqty, price, saleprice, fk_snum, pcontent, point "+
                    	 "      , to_char(pinputdate, 'yyyy-mm-dd') as pinputdate "+
                    	 " from tbl_product " +
                    	 " where pnum in("+pnumes+") ";
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			
			
			
			//우편배달부가 전달해서 sql문 실행해야함
			rs = pstmt.executeQuery(); // select문은 익스큐트쿼리 .리턴타입은 rs
			
			
			//select되어진 개수만큼 반복문
			while(rs.next()) {
				 int pnum = rs.getInt("pnum");
	             String pname = rs.getString("pname");
	             int fk_cnum = rs.getInt("fk_cnum");
	             String pcompany = rs.getString("pcompany");
	             String pimage1 = rs.getString("pimage1");
	             String pimage2 = rs.getString("pimage2");
	             String prdmanual_systemFileName = rs.getString("prdmanual_systemFileName");
	             String prdmanual_orginFileName = rs.getString("prdmanual_orginFileName");
	             int pqty = rs.getInt("pqty");
	             int price = rs.getInt("price");
	             int saleprice = rs.getInt("saleprice");
	             int fk_snum = rs.getInt("fk_snum");
	             String pcontent = rs.getString("pcontent");
	             int point = rs.getInt("point");
	             String pinputdate = rs.getString("pinputdate");
	             
	             ProductVO productvo = new ProductVO(pnum, pname, fk_cnum, pcompany, pimage1, pimage2, prdmanual_systemFileName, prdmanual_orginFileName, pqty, price, saleprice, fk_snum, pcontent, point, pinputdate); 
	             
	             jumunProductList.add(productvo);
			}//end of while-----
			
		} finally {
			close(); //자원반납

		}
		
		return jumunProductList;
	}

	// *** 주문내역에 대한 페이징 처리를 위해 주문 갯수를 알아오기 위한 것으로
    //     관리자가 아닌 일반사용자로 로그인 했을 경우에는 자신이 주문한 갯수만 알아오고,
    //     관리자로 로그인을 했을 경우에는 모든 사용자들이 주문한 갯수를 알아온다.
	@Override
	public int getTotalCountOrder(String userid) throws SQLException {
		int totalCountOrder = 0;
		
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select count(*) "
						+ " from tbl_order A join tbl_orderdetail B "
						+ " on A.odrcode = B.fk_odrcode ";
			
			if("admin".equals(userid)) {
				pstmt = conn.prepareStatement(sql);
			}
			else {
				//관리자가 아닌 일반 사용자의 경우
				sql += " where A.fk_userid = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, userid);
			}
			
			rs = pstmt.executeQuery();
			rs.next();
			
			totalCountOrder = rs.getInt(1);
			
		}finally {
			close();
		}
		
		
		return totalCountOrder;
	}

	// *** 관리자가 아닌 일반사용자로 로그인 했을 경우에는 자신이 주문한 내역만 페이징 처리하여 조회를 해오고,
    //     관리자로 로그인을 했을 경우에는 모든 사용자들의 주문내역을 페이징 처리하여 조회해온다.
   @Override
   public List<Map<String, String>> getOrderList(String userid, int currentShowPageNo, int sizePerPage)
         throws SQLException {
      
      List<Map<String, String>> orderList = new ArrayList<>();
      
      try {
          conn = ds.getConnection();
          
          String sql = " select odrcode, fk_userid, odrdate, odrseqnum, fk_pnum, oqty, odrprice, deliverstatus "  
                  + "      , pname, pimage1, price, saleprice, point "
                  + " from "
                  + " ( "
                  + " select row_number() over (order by B.fk_odrcode desc, B.odrseqnum desc) AS RNO "
                  + "       , A.odrcode, A.fk_userid "
                  + "       , to_char(A.odrdate, 'yyyy-mm-dd hh24:mi:ss') AS odrdate "
                  + "       , B.odrseqnum, B.fk_pnum, B.oqty, B.odrprice "
                  + "       , case B.deliverstatus "
                  + "         when 1 then '주문완료' "
                  + "         when 2 then '배송중' "
                  + "         when 3 then '배송완료' "
                  + "         end AS deliverstatus "
                  + "     , C.pname, C.pimage1, C.price, C.saleprice, C.point "
                  + " from tbl_order A join tbl_orderdetail B "
                  + " on A.odrcode = B.fk_odrcode "
                  + " join tbl_product C "
                  + " on B.fk_pnum = C.pnum ";
         
         if(!"admin".equals(userid)) { 
            // 관리자가 아닌 일반사용자로 로그인 한 경우 
            sql += " where A.fk_userid = ? ";
         }
         
         sql += " ) V "
              + " where RNO between ? and ? "; 
         
         pstmt = conn.prepareStatement(sql);

          
          if(!"admin".equals(userid)) { 
             // 관리자가 아닌 일반사용자로 로그인 한 경우 
             pstmt.setString(1, userid);
             pstmt.setInt(2, (currentShowPageNo*sizePerPage)-(sizePerPage-1) ); // 공식
             pstmt.setInt(3, currentShowPageNo*sizePerPage ); // 공식
          }
          else {
             // 관리자로 로그인 한 경우
             pstmt.setInt(1, (currentShowPageNo*sizePerPage)-(sizePerPage-1) ); // 공식
              pstmt.setInt(2, currentShowPageNo*sizePerPage ); // 공식
          }
          
          rs = pstmt.executeQuery();
          
          while(rs.next()) {
             String odrcode = rs.getString("odrcode");
             String fk_userid = rs.getString("fk_userid");
             String odrdate = rs.getString("odrdate");
             String odrseqnum = rs.getString("odrseqnum");
             String fk_pnum = rs.getString("fk_pnum");
             String oqty = rs.getString("oqty");
             String odrprice = rs.getString("odrprice");
             String deliverstatus = rs.getString("deliverstatus");
             String pname = rs.getString("pname");
             String pimage1 = rs.getString("pimage1");
             String price = rs.getString("price");
             String saleprice = rs.getString("saleprice");
             String point = rs.getString("point");
             
             Map<String, String> odrmap = new HashMap<>();
             odrmap.put("ODRCODE", odrcode);
             odrmap.put("FK_USERID", fk_userid);
             odrmap.put("ODRDATE", odrdate);
             odrmap.put("ODRSEQNUM", odrseqnum);
             odrmap.put("FK_PNUM", fk_pnum);
             odrmap.put("OQTY", oqty);
             odrmap.put("ODRPRICE", odrprice);
             odrmap.put("DELIVERSTATUS", deliverstatus);
             odrmap.put("PNAME", pname);
             odrmap.put("PIMAGE1", pimage1);
             odrmap.put("PRICE", price);
             odrmap.put("SALEPRICE", saleprice);
             odrmap.put("POINT", point);
             
             orderList.add(odrmap);
             
          }// end of while(rs.next())-----------------------------------
          
      } finally {
         close();
      }
      
      return orderList;
   }

	// Ajax 를 이용한 제품후기를 작성하기전 해당 제품을 사용자가 실제 구매했는지 여부를 알아오는 것임. 구매했다라면 true, 구매하지 않았다면 false 를 리턴함.
	@Override
	public boolean isOrder(Map<String, String> paraMap) throws SQLException {
		
		boolean bool = false;
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select O.odrcode "
						+ " from tbl_order O join tbl_orderdetail D "
						+ " on O.odrcode = D.fk_odrcode "
						+ " where O.fk_userid = ? and D.fk_pnum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("fk_userid"));
			pstmt.setString(2, paraMap.get("fk_pnum"));
			
			rs = pstmt.executeQuery();
			
			bool = rs.next();
			
		} finally {
			close();
		}
		
		return bool;
	}

	
	// 특정 회원이 특정 제품에 대해 좋아요에 투표하기(insert) 
	@Override
	public int likeAdd(Map<String, String> paraMap) throws SQLException {
		
		int n = 0;
		
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);//수동커밋으로 전환
			
			String sql =  " delete from tbl_product_dislike "
						+ " where fk_userid = ? and fk_pnum = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			pstmt.executeUpdate();
			
		    sql =  " insert into tbl_product_like(fk_userid, fk_pnum) "
				 + " values(?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			n = pstmt.executeUpdate(); //insert가 성공되어지면 n 은 1값이 나올 것이다.
			
			if(n==1) {
				conn.commit();
				
			}
			
		} catch(SQLIntegrityConstraintViolationException e ) {	
		//	e.printStackTrace();
			conn.rollback();
		} finally {
			close();
		}
		
		return n;
	}

	// 특정 회원이 특정 제품에 대해 싫어요에 투표하기(insert)
	@Override
	public int dislikeAdd(Map<String, String> paraMap) throws SQLException {
		
		int n = 0;
		
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);//수동커밋으로 전환
			
			String sql =  " delete from tbl_product_like "
						+ " where fk_userid = ? and fk_pnum = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			pstmt.executeUpdate();
			

			
		    sql =  " insert into tbl_product_dislike(fk_userid, fk_pnum) "
				 + " values(?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("pnum"));
			
			n = pstmt.executeUpdate(); //insert가 성공되어지면 n 은 1값이 나올 것이다.
			
			if(n==1) {
				conn.commit();
				
			}
			
		} catch(SQLIntegrityConstraintViolationException e ) {	
		//	e.printStackTrace();
			conn.rollback();
		} finally {
			close();
		}
		
		return n;
	}

	
	// 특정 제품에 대한 좋아요,싫어요의 투표결과(select)
	@Override
	public Map<String, Integer> getLikeDislikeCnt(String pnum) throws SQLException {
		
		Map<String, Integer> map = new HashMap<>();
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select "
					+ "          (select count(*) "
					+ "           from tbl_product_like "
					+ "           where fk_pnum = ? ) AS LIKECNT "
					+ " , "
					+ "          (select count(*) "
					+ "           from tbl_product_dislike "
					+ "           where fk_pnum = ?) AS DISLIKECNT "
					+ "  from dual ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, pnum);
			pstmt.setString(2, pnum);
			
			rs = pstmt.executeQuery();
			
			rs.next(); //count는 값이 무조건 있다. 0이든 1이든 
			
			map.put("likecnt", rs.getInt(1));
			map.put("dislikecnt", rs.getInt(2));
			
			
		} finally {
			close();
		}
		
		
		
		return map;
	}

	
	// Ajax 를 이용한 특정 제품의 상품후기를 입력(insert)하기
	@Override
	public int addComment(PurchaseReviewsVO reviewsvo) throws SQLException {
		int n = 0;
		
	      
		try {
		     conn = ds.getConnection();
		     
		     String sql = " insert into tbl_purchase_reviews(review_seq, fk_userid, fk_pnum, contents, writeDate) "
		    		    + " values(seq_purchase_reviews.nextval, ?, ?, ?, default) ";
		                  
		         pstmt = conn.prepareStatement(sql);
		         pstmt.setString(1, reviewsvo.getFk_userid());
		         pstmt.setInt(2, reviewsvo.getFk_pnum());
		         pstmt.setString(3, reviewsvo.getContents());
		         
		         n = pstmt.executeUpdate();
		         
		} finally {
		      close();
		}
		  
		
		return n;
	}

	// Ajax 를 이용한 특정 제품의 상품후기를 조회(select)하기 
	@Override
	public List<PurchaseReviewsVO> commentList(String fk_pnum) throws SQLException {
		List<PurchaseReviewsVO> commentList = new ArrayList<>();
	      
	    try {
	         conn = ds.getConnection();
	         
	         String sql = " select review_seq, fk_userid, name, fk_pnum, contents, to_char(writeDate, 'yyyy-mm-dd hh24:mi:ss') AS writeDate "+
	                      " from tbl_purchase_reviews R join tbl_member M "+
	                      " on R.fk_userid = M.userid  "+
	                      " where R.fk_pnum = ? "+
	                      " order by review_seq desc ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, fk_pnum);
	         
	         rs = pstmt.executeQuery();
	         
	         while(rs.next()) {
	            String contents = rs.getString("contents");
	            String name = rs.getString("name");
	            String writeDate = rs.getString("writeDate");
	            String fk_userid = rs.getString("fk_userid");
	            int review_seq = rs.getInt("review_seq");
	                                    
	            PurchaseReviewsVO reviewvo = new PurchaseReviewsVO();
	            reviewvo.setContents(contents);
	            
	            MemberVO mvo = new MemberVO();
	            mvo.setName(name);
	            
	            reviewvo.setMvo(mvo);
	            reviewvo.setWriteDate(writeDate);
	            reviewvo.setFk_userid(fk_userid);
	            reviewvo.setReview_seq(review_seq);
	            
	            commentList.add(reviewvo);
	         }         
	         
	    } finally {
	         close();
	    }
	      
	    return commentList;
	}

	
	
	// Ajax 를 이용한 특정 제품의 상품후기를 삭제(delete)하기 
	@Override
	public int reviewDel(String review_seq) throws SQLException {
		int n = 0;
	      
		try {
		     conn = ds.getConnection();
		     
		     String sql = " delete from tbl_purchase_reviews "
		    		    + " where review_seq = ?  ";
		                  
		         pstmt = conn.prepareStatement(sql);
		         pstmt.setString(1, review_seq);
		         
		         n = pstmt.executeUpdate();
		         
		} finally {
		      close();
		}
		
		return n;
	}

	
	
	// Ajax 를 이용한 특정 제품의 상품후기를 수정(update)하기 
	@Override
	public int reviewUpdate(Map<String, String> paraMap) throws SQLException {
		  
		int n = 0;
		  
		  try {
		     conn = ds.getConnection();
		     
		     String sql = " update tbl_purchase_reviews set contents = ? , writedate = sysdate "
		                + " where review_seq = ? ";
		          
		  pstmt = conn.prepareStatement(sql);
		  pstmt.setString(1, paraMap.get("contents"));
		  pstmt.setString(2, paraMap.get("review_seq"));
		     
		     n = pstmt.executeUpdate();
		     
		  } finally {
		     close();
		  }
		  
		  return n;
		
	}
	

	//영수증 전표(odrcode) 소유주에 대한 사용자 정보를 조회해오는 것.
	@Override
	public MemberVO odrcodeOwnerMemberInfo(String odrcode) throws SQLException {
		MemberVO mvo = null;
		
		try {
			//커넥션을 받아온다.
			conn = ds.getConnection();
			
			//sql문  서브쿼리
			String sql = " select  userid, name, email, mobile, postcode, address, detailaddress , extraaddress, gender   "+
						 "        , substr(birthday,1,4) AS birthyyyy, substr(birthday,6,2) AS birthmm, substr(birthday, 9,2) AS birthdd  "+
						 "		, coin, point, to_char(registerday, 'yyyy-mm-dd') AS registerday "+
						 " from tbl_member "+
						 " where userid = ( select fk_userid "
						 + "                from tbl_order "
						 + "                where odrcode =  ? ) ";
			
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			
			//sql에 매핑
			pstmt.setString(1,  odrcode );
			
			//sql문을 돌려라 select문은 executeAuery();
			rs = pstmt.executeQuery();
			
			if(rs.next()) {//결과값 한개뿐이니 while문 아니고 if로 rs.next()
				mvo = new MemberVO();
				
				mvo.setUserid(rs.getString(1));
				mvo.setName(rs.getString(2));
				mvo.setEmail(aes.decrypt( rs.getString(3)) ); //복호화 , 빨간줄은 exeception처리 해라암호화되어져있는 이메일을 복호화 한다.
				mvo.setMobile(aes.decrypt(rs.getString(4))); //복호화 
				mvo.setPostcode(rs.getString(5));
				mvo.setAddress(rs.getString(6));
				mvo.setDetailaddress(rs.getString(7));
				mvo.setExtraaddress(rs.getString(8));
				mvo.setGender(rs.getString(9));
				mvo.setBirthday(rs.getString(10) + rs.getString(11) + rs.getString(12));
				mvo.setCoin(rs.getInt(13));
				mvo.setPoint(rs.getInt(14));
				mvo.setRegisterday(rs.getString(15));
	            
	            
	            
			}
		}catch(GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();	
		} finally {
			close();
		}
		
		return mvo;
	}

	
	//tbl_orderdetail 테이블의 deliverstatus(배송상태) 컬럼의 값을 2(배송시작)로 변경하기
	@Override
	public int updateDeliverStart(String odrcodePnum) throws SQLException {
		int n = 0;
		  
		  try {
		     conn = ds.getConnection();
		     
		     String sql = " update tbl_orderdetail set deliverstatus = 2 "
		                + "where fk_odrcode || '/' || fk_pnum in( "+odrcodePnum +") ";
		          
		     //in절은 위치홀더가 안된다. 반드시 변수처리
		  pstmt = conn.prepareStatement(sql);
		  
		     
		     n = pstmt.executeUpdate();
		     
		  } finally {
		     close();
		  }
		  
		  return n;
	}

	// tbl_orderdetail 테이블의 deliverstatus(배송상태) 컬럼의 값을 3(배송시작)로 변경하기
	@Override
	public int updateDeliverEnd(String odrcodePnum) throws SQLException {
		int n = 0;
		  
		  try {
		     conn = ds.getConnection();
		     
		     String sql = " update tbl_orderdetail set deliverstatus = 3 "
		                + "where fk_odrcode || '/' || fk_pnum in( "+odrcodePnum +") ";
		          
		     //in절은 위치홀더가 안된다. 반드시 변수처리
		  pstmt = conn.prepareStatement(sql);
		  
		     
		     n = pstmt.executeUpdate();
		     
		  } finally {
		     close();
		  }
		  
		  return n;
	}

	// tbl_map(위, 경도) 테이블에 있는 정보 가져오기(select)
	@Override
	public List<Map<String, String>> selectStoreMap() throws SQLException {
		List<Map<String, String>> storeMapList =new ArrayList<>();
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select storeID, storeName, storeUrl, storeImg, storeAddress, lat, lng, zindex " + 
                    	  " from tbl_map " + 
                    	  " order by zindex asc ";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
	            HashMap<String, String> map = new HashMap<>();
	            map.put("STOREID", rs.getString("STOREID"));
	            map.put("STORENAME", rs.getString("STORENAME"));
	            map.put("STOREURL", rs.getString("STOREURL"));
	            map.put("STOREIMG", rs.getString("STOREIMG"));
	            map.put("STOREADDRESS", rs.getString("STOREADDRESS"));
	            map.put("LAT", rs.getString("LAT"));
	            map.put("LNG", rs.getString("LNG"));
	            map.put("ZINDEX", rs.getString("ZINDEX"));
	                        
	            storeMapList.add(map); 
	         }
			
		} finally {
			close();
		}
		
		
		return storeMapList;
	}





	
		
	
	

}

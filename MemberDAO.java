package member.model;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import util.security.*;

public class MemberDAO implements InterMemberDAO {

	private DataSource ds; // DataSource ds 는 아파치톰캣이 제공하는 DBCP(DB Connection Pool) 이다.
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs; 
	
	private AES256 aes;//AES를 위한 객체를 하나 만든다.
	
	// 3. 기본생성자 //PersonDAO_04.java에서 긁어와 class명만 바꿔줌..ProductDAO()
		//배치서술자 web.xml , context.xml에 가서 ip username password등을 알 수 있음 . 거기서 jdbc/mymvc_oracle 를 카피해와서 lookup에 넣어준다.
	public MemberDAO() {
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
	
	//ID중복검사 (tbl_member 테이블에서 userid가 존재하면 true를 리턴해주고, userid가 존재하지 않으면 false를 리턴한다)
	@Override
	public boolean idDuplicateCheck(String userid) throws SQLException {

		boolean isExist = false;
		
		try {
			conn = ds.getConnection();
			
			String sql = " select * "
						+" from tbl_member "
						+" where userid= ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			
			rs = pstmt.executeQuery();
			
			isExist = rs.next();  //행이 있으면 (중복된 userid)  true,
								  //행이 없으면(사용가능한 userid) false
		} finally {
			close();
		}
		
		
		return isExist;
	}

	// email 중복검사 (tbl_member 테이블에서 email 가 존재하면 true를 리턴해주고, email 가 존재하지 않으면 false를 리턴한다)
	@Override
	public boolean emailDuplicateCheck(String email) throws SQLException {
		
		boolean isExist = false;
		
		try {
			conn = ds.getConnection();
			
			String sql = " select * "
						+" from tbl_member "
						+" where email = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aes.encrypt(email)); //이메일을 암호화해서 있냐없냐를  비교해야 한다. 
			
			rs = pstmt.executeQuery();
			
			isExist = rs.next();  //행이 있으면 (중복된 email)  true,
								  //행이 없으면(사용가능한 email) false
		}catch(GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		
		} finally {
			close();
		}
		
		
		return isExist;
	}

	
	//회원가입을 해주는 메소드(tbl_member 테이블에 insert)
	@Override
	public int registerMember(MemberVO member) throws SQLException {
		
		int result = 0;
		
		try {
			conn = ds.getConnection();
			
			String sql = " insert into tbl_member(userid, pwd, name, email, mobile, postcode, address, detailaddress, extraaddress, gender, birthday) "
						+" values(?,?,?,?,?,?,?,?,?,?,?) ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, member.getUserid());
			pstmt.setString(2, Sha256.encrypt( member.getPwd())); 		// 암호를 SHA256 알고리즘으로 단방향 암호화 시킨다.
			pstmt.setString(3, member.getName());
			pstmt.setString(4, aes.encrypt(member.getEmail()) );		// 이메일을 AES256 알고리즘으로 양방향 암호화 시킨다.
			pstmt.setString(5, aes.encrypt(member.getMobile()) );		// 휴대폰번호를 AES256 알고리즘으로 양방향 암호화 시킨다.
			pstmt.setString(6, member.getPostcode());
			pstmt.setString(7, member.getAddress());
			pstmt.setString(8, member.getDetailaddress());
			pstmt.setString(9, member.getExtraaddress());
			pstmt.setString(10, member.getGender());
			pstmt.setString(11, member.getBirthday());
			
			result = pstmt.executeUpdate();
			
			
		}catch(GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
			
			
		}finally {
			close();
		}
		
		return result;
	}

	// 입력받은 paraMap을 가지고 한명의 회원정보를 리턴시켜주는 메소드(로그인 처리)
	@Override
	public MemberVO selectOneMember(Map<String, String> paraMap) throws SQLException {

		MemberVO member = null;
		
		try {
			conn = ds.getConnection();
			
			String sql = "SELECT userid, name, email, mobile, postcode, address, detailaddress , extraaddress, gender "+
					"     , birthyyyy, birthmm, birthdd, coin, point, registerday, pwdchangegap "+
					"     , nvl(lastlogingap, trunc( months_between(sysdate, registerday) ) ) AS lastlogingap "+
					"FROM "+
					"(select userid, name, email, mobile, postcode, address, detailaddress , extraaddress, gender "+
					"      , substr(birthday,1,4) AS birthyyyy, substr(birthday,6,2) AS birthmm, substr(birthday, 9,2) AS birthdd "+
					"      , coin, point, to_char(registerday, 'yyyy-mm-dd') AS registerday "+
					"      , trunc(months_between(sysdate, lastpwdchangedate) ) AS pwdchangegap "+
					"from tbl_member\n"+
					"where status = 1 and userid = ? and pwd= ? "+
					")M "+
					"CROSS JOIN  "+
					"( "+
					"select trunc( months_between( sysdate, max(logindate)) , 0) AS lastlogingap "+
					"from tbl_loginhistory "+
					"where fk_userid = ? "+
					")H";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("userid")); //key값은 LoginAction.java에서 정의해줌
			pstmt.setString(2, Sha256.encrypt(paraMap.get("pwd"))); // pwd 는 qwer1234$  데이터베이스에서는 암호가 pwd='9695b88a59a1610320897fa84cb7e144cc51f2984520efb77111d94b402a8382'
			pstmt.setString(3, paraMap.get("userid"));
			
			rs = pstmt.executeQuery();
			//이값은 rs가 있을수도 있고, 없을 수도 있다. 암호가 틀린경우는 데이터베이스에서 select해도 안맞기 떄문에 
			
			if(rs.next()) { //select되어진게 있냐 . 어차피 한개밖에 안나오기 때문에 while 아니고 if를 쓴다.
				member = new MemberVO(); //셀렉되어진게 있으면 넣겠다.
				
				member.setUserid(rs.getString(1));
				member.setName(rs.getString(2));
				member.setEmail(aes.decrypt( rs.getString(3)) ); //복호화 , 빨간줄은 exeception처리 해라암호화되어져있는 이메일을 복호화 한다.
				member.setMobile(aes.decrypt(rs.getString(4))); //복호화 
				member.setPostcode(rs.getString(5));
	            member.setAddress(rs.getString(6));
	            member.setDetailaddress(rs.getString(7));
	            member.setExtraaddress(rs.getString(8));
	            member.setGender(rs.getString(9));
	            
	            member.setBirthday(rs.getString(10) + rs.getString(11) + rs.getString(12));
	            member.setCoin(rs.getInt(13));
	            member.setPoint(rs.getInt(14));
	            member.setRegisterday(rs.getString(15));
	            
	            if( rs.getInt(16) >= 3) {
	            	// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 true
	            	// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지나지 않았으면 false
	            	member.setRequirePwdChange(true); //로그인 시 암호를 변경해라는 alert를 띄우도록 할 때 사용한다.
	            }
	            
	            if(rs.getInt(17) >= 12) {
	            	// 마지막으로 로그인한 날짜시간이 현재시각으로 부터 1년이 지났으면 휴면으로 지정
	            	// 휴면처리는 데이터베이스에서 업데이트를 해줘야한다.
	            	member.setIdle(1); //0은 활동, 1은 휴면
	            	
	            	// === tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기 === //
	            	sql = " update tbl_member set idle = 1 "
	            		+ " where userid = ? ";
	            	pstmt = conn.prepareStatement(sql);   //sql문이 바뀌었으니 새로운 우편배달부를 만든다.
	            	pstmt.setString(1, paraMap.get("userid"));
	            	
	            	pstmt.executeUpdate(); //dml문
	            }
	            
	            // === tbl_loginhistory(로그인 기록) 테이블에 insert 하기 === //
	            if(member.getIdle() != 1) {
	            	sql = " insert into tbl_loginhistory(fk_userid, clientip) "
	            		+ " values(?,?) ";
	            	pstmt= conn.prepareStatement(sql);
	            	pstmt.setString(1, paraMap.get("userid"));
	            	pstmt.setString(2, paraMap.get("clientip")); //ip는 map에 있다 LoginAction에 가보면 있다.
	            	
	            	pstmt.executeUpdate(); //dml문
	            }
	            
	               	
			}
		}catch(GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
		return member;
	}

	// 아이디 찾기(성명, 이메일을 입력받아서 해당 사용자의 아이디를 알려준다)
	@Override
	public String findUserid(Map<String, String> paraMap) throws SQLException {
		
		String userid = null;
		
		try {
			//커넥션을 받아온다.
			conn = ds.getConnection();
			
			//sql문
			String sql = " select userid "
					   + " from tbl_member "
					   + " where status = 1 and name =? and email = ? ";
			
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			
			//sql에 매핑
			pstmt.setString(1, paraMap.get("name") );
			pstmt.setString(2, aes.encrypt(paraMap.get("email") ) ); //현재 데이터베이스에서는 암호화가 되어있다. 매핑하려면 암호화해줘야함
			
			//sql문을 돌려라 select문은 executeAuery();
			rs = pstmt.executeQuery();
			
			if(rs.next()) {//select되어진 값이 있냐
				userid = rs.getString(1); //select문의 첫번째 컬럼값을 userid에 넣어
				
			}  //값이 없으면 false이다. userid = null;이 넘어감.
			
		}catch(GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();	
		} finally {
			close();
		}

		return userid;
	}

	// 비밀번호 찾기(아이디, 이메일을 입력받아서 해당 사용자가 존재하는지 유무를 알려준다)
	@Override
	public boolean isUserExist(Map<String, String> paraMap) throws SQLException {
		
		boolean isUserExist = false;
		try {
			//커넥션을 받아온다.
			conn = ds.getConnection();
			
			//sql문
			String sql = " select userid "
					   + " from tbl_member "
					   + " where status = 1 and userid =? and email = ? ";
			
			//우편배달부
			pstmt = conn.prepareStatement(sql);
			
			//sql에 매핑
			pstmt.setString(1, paraMap.get("userid") );
			pstmt.setString(2, aes.encrypt(paraMap.get("email") ) ); //현재 데이터베이스에서는 암호화가 되어있다. 매핑하려면 암호화해줘야함
			
			//sql문을 돌려라 select문은 executeAuery();
			rs = pstmt.executeQuery();
			
			isUserExist = rs.next();
		}catch(GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();	
		} finally {
			close();
		}
		return isUserExist;
	}

	
	// 암호 변경하기
	@Override
	public int pwdUpdate(Map<String, String> paraMap) throws SQLException {
		
		int result = 0;
		
		try {
			conn = ds.getConnection();
			
			String sql = " update tbl_member set pwd = ? "
					   + "                     , lastpwdchangedate = sysdate "
					   + " where userid = ? ";
			
			//암호를 바꾸면 방금 바꿨으면 마지막으로 암호를 바꾼 날짜를 lastpwdchangedate를 지금으로 바꿔줘야 한다.
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, Sha256.encrypt(paraMap.get("pwd"))); //암호를 SHA256 알고리즘으로 단방향 암호화 시킨다.
			pstmt.setString(2, paraMap.get("userid")); //map에 있다
			
			result = pstmt.executeUpdate();
			
			
		} finally {
			close();
		}
		
		return result;
	}

	
	
	// 회원의 코인 및 포인트 증가하기
	@Override
	public int coinUpdate(Map<String, String> paraMap) throws SQLException {
		int result = 0;
		
		try {
			
			conn = ds.getConnection();
			
			String sql =  " update tbl_member set coin = coin + ? , point = point + ? "
						+ " where userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("coinmoney"));
			pstmt.setInt(2, (int) (Integer.parseInt(paraMap.get("coinmoney")) * 0.01) ); // 스트링을 인트로, 실수를 인트로 형변환 
			pstmt.setString(3, paraMap.get("userid"));
			
			result = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		
		return result;
	}

	// 회원의 개인 정보 변경하기
	@Override
	public int updateMember(MemberVO member) throws SQLException {
		
		int result = 0;
		
		try {
			
			conn = ds.getConnection();
			
			String sql =  " update tbl_member set name          = ? "
						+ "                     , pwd           = ? "
						+ "                     , email         = ? "
						+ "                     , mobile        = ? "
						+ "                     , postcode      = ? "
						+ "                     , address       = ? "
						+ "                     , detailaddress = ? "
						+ "                     , extraaddress  = ? "
						+ "                     , lastpwdchangedate = sysdate "
						+ " where userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, member.getName());
			pstmt.setString(2, Sha256.encrypt( member.getPwd())); 		// 암호를 SHA256 알고리즘으로 단방향 암호화 시킨다.
			pstmt.setString(3, aes.encrypt(member.getEmail()));         // 이메일을 AES256 알고리즘으로 양방향 암호화 시킨다.
			pstmt.setString(4, aes.encrypt(member.getMobile()) );		// 휴대폰번호를 AES256 알고리즘으로 양방향 암호화 시킨다.
			pstmt.setString(5, member.getPostcode());		
			pstmt.setString(6, member.getAddress());
			pstmt.setString(7, member.getDetailaddress());
			pstmt.setString(8, member.getExtraaddress());
			pstmt.setString(9, member.getUserid());
			
			
			result = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		
		return result;
	}

	
	// 페이징 처리가 되어진 모든 회원 또는 검색 한 회원 목록 보여주기
	@Override
	public List<MemberVO> selectPagingMember(Map<String, String> paraMap) throws SQLException {
		
		List<MemberVO> memberList = new ArrayList<>();
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select userid, name, email, gender "
						+ " from "
						+ " (  "
						+ "		select rownum AS rno, userid, name, email, gender "
						+ "		from "
						+ "    	( "
						+ "    		select userid, name, email, gender "
						+ "    		from tbl_member "
						+ "    		where userid !='admin' "
						+ "    		order by registerday desc "
						+ "    	) V "
						+ " ) T "
						+ " where rno between ? and ? " ;
			
			pstmt = conn.prepareStatement(sql);
			
			int currentShowPageNo = Integer.parseInt( paraMap.get("currentShowPageNo") );
			
			int sizePerPage = Integer.parseInt(paraMap.get("sizePerPage") );
			
			/*
			 
			  >>> where rno between A and B
	            A 와 B 를 구하는 공식 <<<
		            
		        currentShowPageNo 은 보고자 하는 페이지 번호이다. 즉, 1페이지, 2페이지, 3페이지... 를 말한다.
		        sizePerPage 는 한페이지당 보여줄 행의 개수를 말한다. 즉, 3개, 5개, 10개를 보여줄떄의 개수를 말한다.
		        
		        A 는 (currentShowPageNo * sizePerPage) - (sizePerPage - 1) 이다.
		        
		        B 는 (currentShowPageNo * sizePerPage) 이다.
					 
			 */
			
			pstmt.setInt(1, (currentShowPageNo * sizePerPage) - (sizePerPage - 1));
			pstmt.setInt(2, (currentShowPageNo * sizePerPage));
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				//셀렉되어진 개수만큼 나온다
				 
				MemberVO mvo = new MemberVO();
				mvo.setUserid(rs.getString(1));
				mvo.setName(rs.getString(2));
				mvo.setEmail( aes.decrypt( rs.getString(3) ));//복호화 필요
				mvo.setGender(rs.getString(4));
				//db에는 이메일이 암호화되어진 것을 웹상에 뿌릴 떄는 복호화 해준다.
				
				memberList.add(mvo);
				
			}// end of while----
		
		}catch(GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
			
		} finally {
			close();
		}
		
		return memberList;
	}

	
	// 페이징처리를 위한 검색이 있는 또는 검색이 없는 전체회원에 대한 총페이지 알아오기
	@Override
	public int getTotalPage(Map<String, String> paraMap) throws SQLException {
		
		int totalPage = 0;
		
		try {
			conn = ds.getConnection();
			
			String sql = " select ceil( count(*)/?) "
					   + " from tbl_member "
					   + " where userid != 'admin' ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("sizePerPage"));
			
			rs = pstmt.executeQuery();
			
			rs.next(); //꼭 해주기
			
			totalPage = rs.getInt(1);
			
		} finally {
			close();
		}
		return totalPage;
	}
	

}

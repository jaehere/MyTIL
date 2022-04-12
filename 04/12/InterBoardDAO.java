package board.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface InterBoardDAO {
	
	   // 페이징 처리가 되어진 모든 QnA 게시판 게시글 목록 보여주기
	   List<QnABoardVO> selectPagingQnaBoard(Map<String, String> paraMap) throws SQLException;
	    
	   // 페이징 처리를 위한 검색이 있는 또는 검색이 없는 전체 qna상품문의게시글에 대한 페이지 알아오기
	   int getTotalqnaPage(Map<String, String> paraMap) throws SQLException;
	   
	   //Qna 게시판에 글 작성하기
	   int writeQnaBoard(Map<String, String> paraMap) throws SQLException;
	   
	   // Qna 상세글 읽어오기
	   QnABoardVO readqnaContent(int pk_qna_num) throws SQLException;
	   
	   // 번호 하나를 받아 Qna글 정보 받아오기 
	   QnABoardVO selectqnaContent(int pk_qna_num) throws SQLException;
	   
	   //Qna 게시판 값을 수정이나 삭제하기 위해 정보 받아오기
	   QnABoardVO getqnaContent(int pk_qna_num) throws SQLException;

	   //Qna 게시글 수정하기
	   int UpdateQnaBoard(Map<String, String> paraMap) throws SQLException;

	   //Qna 게시글 삭제하기
	   int deleteQnaBoard(QnABoardVO qnaVO) throws SQLException;
	   
	   
		/*
		 * //Qna 게시글에 댓글 작성하기 int writeCmtBoard(CommentVO cmtVO) throws SQLException;
		 */
	   
	   //Qna 게시글 댓글 읽어오기
	   List<CommentVO> readCmtContent(String fk_qna_num) throws SQLException;

	
	   // Qna 게시판 조회수 증가
	   int qnaReadCountUp(int pk_qna_num) throws SQLException;
	   
	   // Qna 게시판 이전글, 다음글 정보를 가져오기
	   QnABoardVO getqnaPrevNextContent(Map<String, String> paraMap) throws SQLException;
	
	
	   // 페이징 처리를 위한 하나의 상품에 대한 Qna게시글 페이지 알아오기
	   int getProductQnaPage(Map<String, String> paraMap) throws SQLException;
	
	   // 제품상세페이지에 보여줄 한 제품에 대한 qna게시글 불러오기
	   List<QnABoardVO> selectPagingProductQna(Map<String, String> paraMap) throws SQLException;
	
	   // 한 제품에 대한 Qna게시글 갯수 알아오기
	   int countOneProductQna(Map<String, String> paraMap) throws SQLException;
	
	   //댓글 작성하기
	   int writeCmtBoard(CommentVO cvo) throws SQLException;

	   //비밀글 유무 알아오기
	   int searchIssecret(int pk_qna_num) throws SQLException;

	   // 댓글 삭제하기 
	   int deleteComment(CommentVO cVO) throws SQLException;
	
	
	   // 댓글 정보 알아오기
	   CommentVO getCmtContent(int pk_cmt_num) throws SQLException;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	   
	//////////////////////////////////////////////////////////////////////////////  [115] 
	//////////////////정환모 작업 (안겹치도록 방파제) //////////////////////////////////

	   
	// FAQ 글목록보기
	List<FaqBoardVO> selectPagingFaqBoard(Map<String, String> paraMap) throws SQLException;	
	
	// 페이징 처리를 위한 검색이 있는 또는 검색이 없는 전체 FAQ게시판에 대한 페이지 알아오기
	int getTotalfaqPage(Map<String, String> paraMap) throws SQLException;
	
	// FAQ 게시판에 글 작성하기
	int writeFaqBoard(Map<String, String> paraMap) throws SQLException;	
	
	// 리뷰게시판 글 목록보기
	List<ReviewBoardVO> selectPagingRevBoard(Map<String, String> paraMap) throws SQLException;

	// 페이징 처리를 위한 검색이 있는 또는 검색이 없는 전체 리뷰게시글에 대한 페이지 알아오기
	int getTotalRevPage(Map<String, String> paraMap) throws SQLException;

	// FAQ 상세글 읽어오기 
	FaqBoardVO readContent(int pk_faq_board_num) throws SQLException;

	// 번호 하나를 받아 FAQ글 정보 받아오기 
	FaqBoardVO selectContent(int pk_faq_board_num) throws SQLException;

	// FAQ 게시판 값을 수정이나 삭제하기 위해 정보 받아오기
	FaqBoardVO getContent(int pk_faq_board_num) throws SQLException;

	// FAQ 게시판 값을 수정해주기
	int UpdateFaqBoard(Map<String, String> paraMap) throws SQLException;
	
	// FAQ 게시판 값을 삭제하기
	int deleteFaqBoard(FaqBoardVO faqVO) throws SQLException;

	// FAQ 게시판 이전글, 다음글 정보를 가져오기
	FaqBoardVO getPrevNextContent(Map<String, String> paraMap) throws SQLException;

	// 리뷰게시판 상세글 읽어오기 
	ReviewBoardVO readReviewContent(int pk_rnum) throws SQLException;
	
	// 번호 하나를 받아 리뷰게시판 정보 받아오기 
	ReviewBoardVO selectReviewContent(int pk_rnum) throws SQLException;

	// 리뷰게시판 이전글, 다음글 정보를 가져오기
	ReviewBoardVO getPrevNextReviewContent(Map<String, String> paraMap) throws SQLException;

	// 리뷰게시판 값을 수정해주기
	int UpdateReviewBoard(Map<String, String> paraMap) throws SQLException;

	// 리뷰게시판 값을 삭제하기
	int deleteReviewBoard(ReviewBoardVO revVO) throws SQLException;


	// 페이징 처리를 위한 하나의 상품에 대한 리뷰게시글 페이지 알아오기
	int getProductRevPage(Map<String, String> paraMap) throws SQLException;

	// 제품상세페이지에 보여줄 한 제품에 대한 게시글 불러오기
	List<ReviewBoardVO> selectPagingProductRev(Map<String, String> paraMap) throws SQLException;

	// 리뷰게시판 글 작성하기
	int writeRevBoard(Map<String, String> paraMap) throws SQLException;

	// 한 제품에 대한 리뷰게시글 갯수 알아오기
	int countOneProductReview(Map<String, String> paraMap) throws SQLException;

	// 페이징 처리를 위한 검색이 있는 또는 검색이 없는 전체 내게시글에 대한 페이지 알아오기
	int getTotalMyPage(Map<String, String> paraMap) throws SQLException;

	// 마이페이지에 보여줄 내가 쓴 게시글 불러오기
	List<MyBoardVO> selectPagingMyBoard(Map<String, String> paraMap) throws SQLException;

	// FAQ 카테고리 불러오기
	List<HashMap<String, String>> getFaqCateList() throws SQLException;
	
	// qna글번호를 가지고서 해당 글 첨부파일의 서버에 업로드되어진 파일명과 오리지널 파일명을 조회해오기 
	Map<String, String> getQnaImgFileName(String pk_qna_num) throws SQLException;

	



	
	
	
	

	

   












































}

	
	
	
	

   


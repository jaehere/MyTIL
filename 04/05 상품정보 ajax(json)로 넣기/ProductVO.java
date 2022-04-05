package myshop.model;

public class ProductVO {
	   private int    pnum;       // 제품번호
	   private String    pname;      // 제품명
	   private int     fk_cnum;    // 카테고리코드(Foreign Key)의 시퀀스번호 참조
	   private String  pcompany;   // 제조회사명
	   private String  pimage1;    // 제품이미지1   이미지파일명
	   private String  pimage2;    // 제품이미지2   이미지파일명 
	   private String  prdmanual_systemFileName;  // 파일서버에 업로드되어지는 실제 제품설명서 파일명 (파일명이 중복되는 것을 피하기 위해서 중복된 파일명이 있으면 파일명뒤에 숫자가 자동적으로 붙어 생성됨) 
	   private String  prdmanual_orginFileName;   // 웹클라이언트의 웹브라우저에서 파일을 업로드 할때 올리는 제품설명서 파일명 
	   private int    pqty;       // 제품 재고량
	   private int    price;      // 제품 정가
	   private int    saleprice;  // 제품 판매가(할인해서 팔 것이므로)
	   private int    fk_snum;    // 'HIT', 'NEW', 'BEST' 에 대한 스펙번호인 시퀀스번호를 참조
	   private String    pcontent;   // 제품설명 
	   private int    point;      // 포인트 점수                                         
	   private String    pinputdate; // 제품입고일자   
	   
	   private CategoryVO categvo; // 카테고리VO 
	   private SpecVO spvo;        // 스펙VO 
	   
	   /*
	       제품판매가와 포인트점수 컬럼의 값은 관리자에 의해서 변경(update)될 수 있으므로
	       해당 제품의 판매총액과 포인트부여 총액은 판매당시의 제품판매가와 포인트 점수로 구해와야 한다.  
	   */
	   private int totalPrice;         // 판매당시의 제품판매가 * 주문량
	   private int totalPoint;         // 판매당시의 포인트점수 * 주문량 
	      
	   
	   public ProductVO() { }
	   
	   public ProductVO(int pnum, String pname, int fk_cnum, String pcompany, 
	                  String pimage1, String pimage2, 
	                  String prdmanual_systemFileName, String prdmanual_orginFileName,
	                  int pqty, int price, int saleprice, int fk_snum, 
	                  String pcontent, int point, String pinputdate) {
	   
	      this.pnum = pnum;
	      this.pname = pname;
	      this.fk_cnum = fk_cnum;
	      this.pcompany = pcompany;
	      this.pimage1 = pimage1;
	      this.pimage2 = pimage2;
	      this.prdmanual_systemFileName = prdmanual_systemFileName;
	      this.prdmanual_orginFileName = prdmanual_orginFileName;
	      this.pqty = pqty;
	      this.price = price;
	      this.saleprice = saleprice;
	      this.fk_snum = fk_snum;
	      this.pcontent = pcontent;
	      this.point = point;
	      this.pinputdate = pinputdate;
	   }

	   
	   public int getPnum() {
	      return pnum;
	   }

	   public void setPnum(int pnum) {
	      this.pnum = pnum;
	   }

	   public String getPname() {
	      return pname;
	   }

	   public void setPname(String pname) {
	      this.pname = pname;
	   }

	   public int getFk_cnum() {
	      return fk_cnum;
	   }

	   public void setFk_cnum(int fk_cnum) {
	      this.fk_cnum = fk_cnum;
	   }

	   public String getPcompany() {
	      return pcompany;
	   }

	   public void setPcompany(String pcompany) {
	      this.pcompany = pcompany;
	   }

	   public String getPimage1() {
	      return pimage1;
	   }

	   public void setPimage1(String pimage1) {
	      this.pimage1 = pimage1;
	   }

	   public String getPimage2() {
	      return pimage2;
	   }

	   public void setPimage2(String pimage2) {
	      this.pimage2 = pimage2;
	   }

	   public String getPrdmanual_systemFileName() {
	      return prdmanual_systemFileName;
	   }

	   public void setPrdmanual_systemFileName(String prdmanual_systemFileName) {
	      this.prdmanual_systemFileName = prdmanual_systemFileName;
	   }

	   public String getPrdmanual_orginFileName() {
	      return prdmanual_orginFileName;
	   }

	   public void setPrdmanual_orginFileName(String prdmanual_orginFileName) {
	      this.prdmanual_orginFileName = prdmanual_orginFileName;
	   }   
	   
	   public int getPqty() {
	      return pqty;
	   }

	   public void setPqty(int pqty) {
	      this.pqty = pqty;
	   }

	   public int getPrice() {
	      return price;
	   }

	   public void setPrice(int price) {
	      this.price = price;
	   }

	   public int getSaleprice() {
	      return saleprice;
	   }

	   public void setSaleprice(int saleprice) {
	      this.saleprice = saleprice;
	   }

	   public int getFk_snum() {
	      return fk_snum;
	   }

	   public void setFk_snum(int fk_snum) {
	      this.fk_snum = fk_snum;
	   }

	   public String getPcontent() {
	      return pcontent;
	   }

	   public void setPcontent(String pcontent) {
	      this.pcontent = pcontent;
	   }

	   public int getPoint() {
	      return point;
	   }

	   public void setPoint(int point) {
	      this.point = point;
	   }

	   public String getPinputdate() {
	      return pinputdate;
	   }

	   public void setPinputdate(String pinputdate) {
	      this.pinputdate = pinputdate;
	   }

	   public CategoryVO getCategvo() {
	      return categvo;
	   }

	   public void setCategvo(CategoryVO categvo) {
	      this.categvo = categvo;
	   }

	   public SpecVO getSpvo() {
	      return spvo;
	   }

	   public void setSpvo(SpecVO spvo) {
	      this.spvo = spvo;
	   }
	   
	   ///////////////////////////////////////////////
	   // *** 제품의 할인률 ***
	   public int getDiscountPercent() {  //항상 get이나 set이 붙어야 한다.
	      // 정가   :  판매가 = 100 : x
	      
	      // 5000 : 3800 = 100 : x
	      // x = (3800*100)/5000 
	      // x = 76
	      // 100 - 76 ==> 24% 할인
	      
	      // 할인률 = 100 - (판매가 * 100) / 정가
	      return 100 - (saleprice * 100)/price;
	   }
	   
	   
	   /////////////////////////////////////////////////
	   // *** 제품의 총판매가(실제판매가 * 주문량) 구해오기 ***
	   public void setTotalPriceTotalPoint(int oqty) {   
	      // int oqty 이 주문량이다.
	   
	      totalPrice = saleprice * oqty; // 판매당시의 제품판매가 * 주문량
	      totalPoint = point * oqty;     // 판매당시의 포인트점수 * 주문량 
	   }
	   
	   public int getTotalPrice() {
	      return totalPrice;
	   }
	   
	   public int getTotalPoint() {
	      return totalPoint;
	   }
}

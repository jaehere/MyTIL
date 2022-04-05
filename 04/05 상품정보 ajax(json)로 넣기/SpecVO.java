package myshop.model;

public class SpecVO {
		private int snum;      // 스펙번호       
		private String sname;  // 스펙명
		
		public SpecVO() {}; //기본생성자
		
		public SpecVO(int snum, String sname) {  //파라미터가 있는 생성자
			this.snum = snum;
			this.sname = sname;
		}

		public int getSnum() {
			return snum;
		}

		public void setSnum(int snum) {
			this.snum = snum;
		}

		public String getSname() {
			return sname;
		}

		public void setSname(String sname) {
			this.sname = sname;
		}
		
		
		
		
}

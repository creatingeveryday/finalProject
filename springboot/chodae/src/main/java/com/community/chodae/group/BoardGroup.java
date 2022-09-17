package com.community.chodae.group;

//게시판 그룹 enum으로 정리  =  생성자 첫번째 파라미터 : 게시판 번호, 두번째 파라미터: 게시판의 한글이름 
public enum BoardGroup { 
	notice(1,"공지사항"),
	faq(2,"자주하는질문"),
	study(5,"스터디 모집"),
	edu(6,"국비 교육"),//국비교육
	review(7,"국비교육 리뷰"), //국비교육리뷰게시판
	worry(8,"고민상담"),
	career(9,"취업 준비"),
	news(10,"IT뉴스"),
	event(11,"이벤트"),
	book(12,"리뷰");//	책/강의 리뷰
	
	private final int value;
	private final String korName;
	
	
	private BoardGroup(int value,String korName) {
		this.value = value;
		this.korName = korName;
	}

	public int getValue() {
		return value;
	}
	
	public String getKorName() {
		return korName;
	}
	
	public static BoardGroup getBoardGroupByNo(int boardNo) {
        for (BoardGroup m : BoardGroup.values()) {
            if (m.value == boardNo) {
                return m;
            }
        }
        return null;
    }
	

}

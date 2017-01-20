package core.store;

public class SetItem {
	private double score;
	private String member;
	
	
	public SetItem(double score, String member) {
		super();
		this.score = score;
		this.member = member;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
	
	public boolean equals(SetItem another) {
		return another==null?false:this.getMember().equals(another.getMember());
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return member.hashCode();
	}
	

}

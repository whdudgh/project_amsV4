package ezen.ams.domain;

public enum AccountType {
	ALL_ACCOUNT("전체계좌"), GENERAL_ACCOUNT("입출금계좌"), MINUS_ACCOUNT("마이너스계좌");
	
	private final String name;
	
	private AccountType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}

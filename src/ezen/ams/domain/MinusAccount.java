package ezen.ams.domain;

import ezen.ams.exception.NotBalanceException;

public class MinusAccount extends Account {
// 부모클래스에 없는 필드나 메소드 추가
	private long borrowMoney;

	public MinusAccount() {
//		컴파일시 부모클래스의 디폴트생성자를 호출하는 super(); 자동 추가
		// super();
	}

	public MinusAccount(String accountOwner, int passwd, long restMoney, long borrowMoney) {
//		부모클래스의 생성자 호출을 이용한 초기화
		// super();
		super(accountOwner, passwd, restMoney);
		this.borrowMoney = borrowMoney;
	}
	
	//생성자 오버로딩 FileAccess를 위함
	public MinusAccount(String accountOwner, int maxAccountNum, int passwd, long restMoney, long borrowMoney) {
		super(accountOwner, maxAccountNum, passwd, restMoney);
		this.borrowMoney = borrowMoney;
	}

	// 메소드 추가
	public long getBorrowMoney() {
		return borrowMoney;
	}
	public void setBorrowMoney(long borrowMoney) {
		this.borrowMoney = borrowMoney;
	}

	// 필요에 따라 부모클래스의 메소드 재정의(Overriding)
	@Override
	public long getRestMoney() {
		return -getBorrowMoney();
	}

	@Override
	public String toString() {
		return String.format("%-7s%10s%12s%14s%,20d%,20d","마이너스", getAccountNum(), getAccountOwner(), "****" , getRestMoney(), getBorrowMoney());
	}

	@Override
	public long deposit(long money) throws NotBalanceException {

		if (money <= 0) {
			throw new NotBalanceException("상환금액은 0이거나 음수일 수 없습니다.");
		}
		return borrowMoney -= money;
	}

	@Override
	public long withdraw(long money) throws NotBalanceException {

		if (money <= 0) {
			throw new NotBalanceException("대출금액은 0이거나 음수일 수 없습니다.");
		}
		return borrowMoney += money;
	}

}

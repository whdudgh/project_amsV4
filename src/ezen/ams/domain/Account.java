package ezen.ams.domain;

import java.util.Objects;

import ezen.ams.exception.NotBalanceException;

/*
 * 은행계좌 추상화
 */
public class Account/* extends Object */ {
//	필드 캡슐화(은닉화)
//	인스턴스변수들(인스턴스필드)
	private String accountNum;
	private String accountOwner;
	private int passwd;
	private long restMoney;

//	스태틱변수, 정적변수, 클래스변수들(클래스필드)
//	public static String bankName;
	public final static String BANK_NAME = "이젠은행";
//	private static int accountId = 1000;
	private static int accountId;

//	초기화 블럭
//	static 초기화 블럭

	static {
//		System.out.println("초기화 블럭 실행됨...");
		// 주로 애플리케이션 환경설정파일의 내용을 읽어 오는 코드...

		accountId = 1000;
	}

//	생성자 오버로딩
	public Account() {
	}

	public Account(String accountOwner, int passwd, long restMoney) {
		this.accountNum = (accountId++) + " ";// 일단 문자열로 변환하기 위해 사용 나중엔 메서드 이용해 변환 배움			
		this.accountOwner = accountOwner;
		this.passwd = passwd;
		this.restMoney = restMoney;
	}
	
	//파일에저장된 최대계좌번호부터 생성되게끔 한것.
	public Account(String accountOwner, int maxAccountNum, int passwd, long restMoney) {
		if(maxAccountNum == 0) {
			this.accountNum = String.valueOf(accountId);
		} else {
			accountId = maxAccountNum+1;
			this.accountNum = String.valueOf(accountId = maxAccountNum+1);
		}
		this.accountOwner = accountOwner;
		this.passwd = passwd;
		this.restMoney = restMoney;
	}

	// setter/getter 메서드
	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public String getAccountOwner() {
		return accountOwner;
	}

	public void setAccountOwner(String accountOwner) {
		this.accountOwner = accountOwner;
	}

	public int getPasswd() {
		return passwd;
	}

	public void setPasswd(int passwd) {
		this.passwd = passwd;
	}

	public void setRestMoney(long restMoney) {
		this.restMoney = restMoney;
	}
	/*
	 * 입금기능
	 */
	public long deposit(long money) throws NotBalanceException {
		// 데이터 검증 및 예외처리
		if (money <= 0) {
			// 예외 일부러 발생 필요함.
			throw new NotBalanceException("입금금액은 0이거나 음수일 수 없습니다.");
		} else if (money >= 100000000) {
			throw new NotBalanceException("1억이상 입금할 수 없습니다.");
		}
		return restMoney += money;
	}

	/*
	 * 출금기능
	 */
	public long withdraw(long money) throws NotBalanceException {
		// 데이터 검증 및 예외처리
		if (money <= 0) {
			// 예외 일부러 발생 필요함.
			throw new NotBalanceException("출금금액은 0이거나 음수일 수 없습니다.");
		} else if (money >= restMoney) {
			throw new NotBalanceException("잔액이 부족하여 출금할 수 없습니다.");
		}
		return restMoney -= money;
	}

	/*
	 * 잔액조회 기능
	 */
	public long getRestMoney() {
		return restMoney;
	}

	/*
	 * 비밀번호 확인
	 */
	public boolean checkPasswd(int pwd) {
		return passwd == pwd ? true : false;
	}

	public void printInfo() {
		System.out.println(accountNum + "\t" + accountOwner + "\t****\t" + restMoney);
	}

	@Override
	public String toString() {
//		return "  "+accountNum + "\t" +"        "+accountOwner + "\t" + "  ****" + "\t" + "         " + restMoney;
//		return String.format("%9s %10s           ****%,20d", accountNum, accountOwner, restMoney);
		return String.format("%-7s%10s%12s%14s%,20d","일반계좌", accountNum, accountOwner, "****" , restMoney);
	}

	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

	// 스태틱(클래스) 메서드
	public static int getAccountId() {
		return accountId;
	}
}

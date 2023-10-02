package ezen.ams.domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * RDB를 이용한 은행계좌 목록 저장 및 관리(검색, 수정, 삭제) 구현체
 * 
 * @author 조영호
 * @author 조영호
 * @since 1.0
 */
public class JdbcAccountRepository implements AccountRepository {

	// 나중에 propertie 파일로 변경할 것임...
	private static String driver = "oracle.jdbc.driver.OracleDriver";
	private static String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private static String userId = "hr";
	private static String password = "hr";

	private Connection con;

	public JdbcAccountRepository() throws Exception {
		Class.forName(driver);
		con = DriverManager.getConnection(url, userId, password);
	}

	/**
	 * 전체계좌 목록 수 반환
	 * @return 목록수
	 * @throws SQLException
	 */
	public int getCount() {
		int count = 0;
		StringBuilder stb = new StringBuilder();
		stb.append(" SELECT").append("   COUNT(*) cnt").append(" FROM account");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(stb.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt("cnt");
			}

		} catch (Exception e) {
			// 컴파일 예외를 런타임 예외로 변환
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
		}
		return count;
	}

	/**
	 * 23-06-13일 추가 
	 * DB에 저장된 정보로 어카운트/마이너스어카운트 객체 생성후 리스트에 옮기는 메서드
	 * @return 전체계좌 목록
	 */
	public List<Account> getAccounts() {
		List<Account> list = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT")
		  .append("   account_id,")
		  .append("   account_num,")
		  .append("   account_owner,")
		  .append("   account_password,")
		  .append("   restmoney,")
		  .append("   borrowmoney")
		  .append(" FROM")
		  .append("   account")
		  .append(" ORDER BY")
		  .append("   account_num");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();

			list = new ArrayList<Account>();
			//읽어온정보를 어카운트타입으로 받기 위해 Account선언
			Account account = null;
			while (rs.next()) {
				int accountId = rs.getInt("account_id");
				int accountNum = rs.getInt("account_num");
				String accountOwner = rs.getString("account_owner");
				int accountPassword = rs.getInt("account_password");
				long restmoney = rs.getLong("restmoney");
				long borrowmoney = rs.getLong("borrowmoney");
				
				//DB에서 account_id의 값이 1이면 일반계좌 2면 마이너스계좌라서 분활생성
				if (accountId == 1) {
					account = new Account();
				} else {
					account = new MinusAccount();
					((MinusAccount)account).setBorrowMoney(borrowmoney);
				}
				account.setAccountNum(String.valueOf(accountNum));
				account.setAccountOwner(accountOwner);
				account.setPasswd(accountPassword);
				account.setRestMoney(restmoney);
				list.add(account);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
		}
		return list;
	}

	/**
	 * 신규계좌 등록
	 * @param account 신규계좌
	 * @return 성공여부(등록여부)
	 */
	public boolean addAccount(Account account) {
		StringBuilder sb = new StringBuilder();

		sb.append(" INSERT INTO account(")
		  .append("   account_id,")
		  .append("   account_num,")
		  .append("   account_owner,")
		  .append("   account_password,")
		  .append("   restmoney,")
		  .append("   borrowmoney)")
		  .append(" VALUES(?, account_num_seq.NEXTVAL, ?, ?, ?, ?)");
		PreparedStatement pstmt = null;

		String name = account.getAccountOwner();
		int pass = account.getPasswd();
		long restMoney = account.getRestMoney();

		try {
			pstmt = con.prepareStatement(sb.toString());
			
			pstmt.setString(2, name);
			pstmt.setInt(3, pass);
			pstmt.setLong(4, restMoney);
			if (account instanceof MinusAccount) {
				long borrowMoney = ((MinusAccount) account).getBorrowMoney();
				pstmt.setInt(1, 2);
				pstmt.setLong(5, borrowMoney);
			} else if(account instanceof Account){
				pstmt.setInt(1, 1);
				pstmt.setLong(5, 0L);
			}
			pstmt.executeUpdate();
			return true;
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 계좌로 사용자 조회
	 * 
	 * @param accountNum 검색 계좌번호
	 * @return 검색된 계좌
	 */
	public Account searchAccount(String accountNum) {
		Account account = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT")
		  .append("   account_id,")
		  .append("   account_num,")
		  .append("   account_owner,")
		  .append("   account_password,")
		  .append("   restmoney,")
		  .append("   borrowmoney")
		  .append(" FROM")
		  .append("   account")
		  .append(" WHERE")
		  .append("   account_num = ?");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setString(1, accountNum);
			rs = pstmt.executeQuery();
			//account_num은 고유번호 시퀸스로 DB에 저장되기 때문에 한사람만 검색되어 반복문 필요X
			if(rs.next()) {
				int accountId = rs.getInt("account_id");
				int accNum = rs.getInt("account_num");
				String accountOwner = rs.getString("account_owner");
				int accountPassword = rs.getInt("account_password");
				long restMoney = rs.getLong("restmoney");
				long borrowMoney = rs.getLong("borrowmoney");
				// 생성시 정한 account_id로 1은 일반계좌 2는 마이너스계좌로 생성하여 목록에 띄움.
				if(accountId == 2) {
					account = new MinusAccount();
					((MinusAccount)account).setBorrowMoney(borrowMoney);
				} else {
					account = new Account();
				}
				account.setAccountNum(String.valueOf(accNum));
				account.setAccountOwner(accountOwner);
				account.setPasswd(accountPassword);
				account.setRestMoney(restMoney);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
		}
		return account;
	}

	/**
	 * 예금주명으로 계좌조회
	 * @param accountOwner 검색 예금주명
	 * @return 검색된 계좌목록
	 */
	public List<Account> searchAccountByOwner(String accountOwner) {
		// 예금주 담을 그릇생성
		List<Account> nameList = null;
		Account account = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT")
		  .append("   account_id,")
		  .append("   account_num,")
		  .append("   account_owner,")
		  .append("   account_password,")
		  .append("   restmoney,")
		  .append("   borrowmoney")
		  .append(" FROM")
		  .append("   account")
		  .append(" WHERE")
		  .append("   account_owner LIKE ?")
		  .append(" ORDER BY")
		  .append("    account_num");
		  
		PreparedStatement pstmt = null;
		// 결과집합처리할 변수선언
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setString(1, accountOwner);
			rs = pstmt.executeQuery();
			nameList = new ArrayList<Account>();
			// 동명이인 있을 수 있기 때문에 와일문으로 반복
			while (rs.next()) {
				int accountId = rs.getInt("account_id");
				int accNum = rs.getInt("account_num");
				String accOwner = rs.getString("account_owner");
				int accountPassword = rs.getInt("account_password");
				long restMoney = rs.getLong("restmoney");
				long borrowMoney = rs.getLong("borrowmoney");
				// 생성을 나누면 중복 줄일 수 있음.(accoun_id받아서 1이면 일반생성, 2면 마이너스생성)
				if (accountId == 2) {
					account = new MinusAccount();
					((MinusAccount)account).setBorrowMoney(borrowMoney);
				} else {
					account = new Account();
				}
					account.setAccountNum(String.valueOf(accNum));
					account.setAccountOwner(accOwner);
					account.setPasswd(accountPassword);
					account.setRestMoney(restMoney);
					nameList.add(account);
				}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
		}
		return nameList;
	}

	/**
	 * 입력된 계좌번호 삭제
	 * 
	 * @param accountNum 삭제할 계좌번호
	 * @return 삭제여부
	 */
	public boolean removeAccount(String accountNum) {
		StringBuilder sb = new StringBuilder();
		sb.append(" DELETE FROM account").append(" WHERE account_num = ?");

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setInt(1, Integer.parseInt(accountNum));
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
		}
	}

	// 프로그램종료시 커넥션끊는 메서드
	public void close() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 테스트용 메인
	public static void main(String[] args) throws Exception {
//		AccountRepository accountRepository = new JdbcAccountRepository();

//		int count = accountRepository.getCount();
//		System.out.println(count);

		// 전체계좌목록 메서드
//		List<Account> allList = accountRepository.getAccounts();
//		for (Account account : allList) {
//			System.out.println(account);
//		}

		// account 계좌 등록
//		Account account2 = new Account();
//		account2.setAccountOwner("입그미");
//		account2.setPasswd(8005);
//		account2.setRestMoney(20000);
//		boolean sucess2 = accountRepository.addAccount(account2);
//		System.out.println(sucess2);

		// account 삭제
//		System.out.println();
//		System.out.println();
//		boolean success = accountRepository.removeAccount("1007");
//		System.out.println(success);

		// account 생성
//		MinusAccount account = new MinusAccount();
//		account.setAccountOwner("대추리");
//		account.setPasswd(1111);
//		account.setBorrowMoney(50000);
//		boolean sucess = accountRepository.addAccount(account);
//		System.out.println(sucess);
	}

}
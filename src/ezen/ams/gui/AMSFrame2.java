package ezen.ams.gui;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JOptionPane;

import ezen.ams.app.AMS4;
import ezen.ams.domain.Account;
import ezen.ams.domain.AccountRepository;
import ezen.ams.domain.AccountType;
import ezen.ams.domain.JdbcAccountRepository;
import ezen.ams.domain.MinusAccount;
import ezen.ams.util.Validator;

/**
 * AMS의 GUI화
 * 
 * @author 조영호
 *
 */
public class AMSFrame2 extends Frame {
	GridBagLayout grid;
	GridBagConstraints conts;

	Choice choice;
	Label accountType, accountNum, accountOwner, password, borrowMoney, accountList, inputMoney, moneyType;
	Button showAccount, deletAccount, searchAccount, createNewAccount, showAll;
	TextField accNumTF, accOwnerTF, passTF, inMoneyTF, boMoneyTF;
	TextArea accListTA;

	Account account;

	private AccountRepository repository;

	public AMSFrame2() {
		this("no-Title");
	}

	public AMSFrame2(String title) {
		super(title);

		try {
			repository = new JdbcAccountRepository();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			System.exit(0);
		}
		grid = new GridBagLayout();
		conts = new GridBagConstraints();
		setLayout(grid);

		conts.insets = new Insets(5, 10, 5, 10);
		conts.fill = GridBagConstraints.HORIZONTAL;

		// Label(라벨) 초기화
		accountType = new Label("계좌종류", Label.CENTER);
		accountNum = new Label("계좌번호", Label.CENTER);
		accountOwner = new Label("예금주명", Label.CENTER);
		password = new Label("비밀번호", Label.CENTER);
		borrowMoney = new Label("대출금액", Label.CENTER);
		accountList = new Label("계좌목록", Label.CENTER);
		inputMoney = new Label("입금금액", Label.CENTER);
		moneyType = new Label("(단위 : 원)", Label.CENTER);

		// choice 항목 add메서드로 추가
		choice = new Choice();
//		choice.add("전체");
//		choice.add("입출금계좌");
//		choice.add("마이너스계좌");
//		Enum 이용
		AccountType[] accountTypes = AccountType.values();
		for (AccountType accountType : accountTypes) {
			choice.add(accountType.getName());
		}

		// Button 초기화
		showAccount = new Button("조 회");
		deletAccount = new Button("삭 제");
		searchAccount = new Button("검 색");
		createNewAccount = new Button("신규등록");
		showAll = new Button("전체조회");

		// TextField 초기화
		accNumTF = new TextField();
		accOwnerTF = new TextField();
		passTF = new TextField();
		inMoneyTF = new TextField();
		boMoneyTF = new TextField();
		accListTA = new TextArea(15, 13);

		// 배치 시작*************************************************

		// 계좌 종류-------------------------------------------------
		gblInsert(accountType, 0, 0, 1, 1, 0.0);
		gblInsert(choice, 1, 0, 3, 1, 0.0);

		// 계좌번호--------------------------------------------------
		gblInsert(accountNum, 0, 1, 1, 1, 0.0);
		gblInsert(accNumTF, 1, 1, 5, 1, 0.5);
		// 버튼
		gblInsert(showAccount, 6, 1, 1, 1, 0.0);
		gblInsert(deletAccount, 7, 1, 1, 1, 0.0);

		// 예금주명--------------------------------------------------
		gblInsert(accountOwner, 0, 2, 1, 1, 0.0);
		gblInsert(accOwnerTF, 1, 2, 5, 1, 0.5);
		// 버튼
		gblInsert(searchAccount, 6, 2, 1, 1, 0.0);

		// 비밀번호, 입금금액----------------------------------------
		gblInsert(password, 0, 3, 1, 1, 0.0);
		gblInsert(passTF, 1, 3, 5, 1, 0.5);

		gblInsert(inputMoney, 6, 3, 1, 1, 0.0);
		gblInsert(inMoneyTF, 7, 3, 5, 1, 0.5);

		// 대출금액, 신규등록, 전체조회-----------------------------
		gblInsert(borrowMoney, 0, 4, 1, 1, 0.0);
		gblInsert(boMoneyTF, 1, 4, 5, 1, 0.5);
		// 버튼
		gblInsert(createNewAccount, 6, 4, 1, 1, 0.0);
		gblInsert(showAll, 7, 4, 1, 1, 0.0);

		// 계좌목록, 머니타입---------------------------------------
		gblInsert(accountList, 0, 5, 1, 1, 0.0);
		gblInsert(moneyType, 11, 5, 1, 1, 0.0);

		// 계좌목록 텍스트필드--------------------------------------
		gblInsert(accListTA, 0, 6, 13, 1, 1.0);

		// 배치 종료************************************************

		pack();
		setResizable(false);
	}

	/**
	 * 데코레이션용 헤더(텍스트 에이리어 꾸미기용) 추가 : 2023-05-26
	 */
	private void printHeader() {
		accListTA.append("==========================================================================\n");
		accListTA.append("계좌타입  ||  계좌번호  ||  예금주  ||  비밀번호  ||      잔액      ||      대출금액\n");
		accListTA.append("==========================================================================\n");
	}

	/**
	 * accOwnerTF의 유효성 검사 메서드 추가 : 2023-05-27
	 * 
	 * @param OwnerValid(입력받은 예금주명)
	 */
	public boolean ownerValid(String OwnerValid) {
		if (!Validator.hasText(OwnerValid)) {
			JOptionPane.showMessageDialog(this, "예금주명을 입력하지 않으셨습니)다.");
			return false;
		} else if (Validator.isNumber(OwnerValid)) {
			JOptionPane.showMessageDialog(this, "예금주명에 숫자 입력 불가.");
			return false;
		}
		return true;
	}

	/**
	 * passTF의 유효성 검사 메서드 추가 : 2023-05-27
	 */
	public boolean passValid(String passCheck) {
		if (!(Validator.isNumber(passCheck))) {
			JOptionPane.showMessageDialog(this, "비밀번호에 숫자이외에는 입력 불가능 합니다.");
			return false;
		}
		return true;
	}

	/**
	 * accNumTF의 유효성 검사 메서드 추가 : 2023-05-27
	 */
	public boolean accNumValid(String accountNum) {
		if (!(Validator.isNumber(accountNum))) {
			JOptionPane.showMessageDialog(this, "계좌번호에 숫자이외에는 입력 불가능 합니다.");
			return false;
		}
		return true;
	}

	/**
	 * 계좌목록 보여주기 showAll 버튼 클릭시 호출 추가 : 2023-05-26
	 * 수정일 : 2023-06-14
	 * 수정내용 : 초이스 선택지에 따라 전체조회버튼의 기능이 달라지는 기능 추가
	 */
	public void allList() {
		accListTA.setText("");
		printHeader();
		List<Account> list = repository.getAccounts();
		if (choice.getSelectedItem().equals("입출금계좌")) {
			for (Account account : list) {
				if (account instanceof MinusAccount) {

				} else if (account instanceof Account) {
					accListTA.append(account.toString() + "\n");
				}
			}
		} else if (choice.getSelectedItem().equals("마이너스계좌")) {
			for (Account account : list) {
				if (account instanceof MinusAccount) {
					accListTA.append(account.toString() + "\n");
				}
			}
		} else {
			for (Account account : list) {
				accListTA.append(account.toString() + "\n");
			}
		}
	}

	/**
	 * 계좌번호or예금주에 해당하는 계좌 보여주기 추가 : 2023-05-26 최종 수정일 : 2023-05-28 수정 내용 : 각 TF에 알맞는
	 * 유효성 검사 메서드 추가.
	 */
	public void searchAcc(boolean category) {
		accListTA.setText("");
		printHeader();
		if (category) {
			String accountNum = accNumTF.getText();
			if (accNumValid(accountNum)) {
				account = repository.searchAccount(accountNum);
				accListTA.append(account.toString() + "\n");
				JOptionPane.showMessageDialog(this, "해당하는 계좌를 찾았습니다!");
			} else {
				return;
			}
		} else if (!category) {
			String ownerName = accOwnerTF.getText();
			if (ownerValid(ownerName)) {
				List<Account> ownerList = repository.searchAccountByOwner(ownerName);
				Account[] ownerArray = ownerList.toArray(new Account[ownerList.size()]);
				for (int i = 0; i < ownerList.size(); i++) {
					accListTA.append(ownerArray[i].toString() + "\n");
				}
				JOptionPane.showMessageDialog(this, "해당하는 계좌를 찾았습니다!");
			} else {
				return;
			}
		}

	}

	/**
	 * 사용자 승인 후 계좌번호로 계좌 삭제 추가 : 2023-05-25 최종 수정일 : 2023-05-28 수정 내용 : 각 TF에 알맞는
	 * 유효성 검사 메서드 추가
	 */
	public void deleteAccount() {
		String accountNum = accNumTF.getText();
		if (accNumValid(accountNum)) {
			int answer = JOptionPane.showConfirmDialog(this, "**주의**\n한번 삭제한 계좌는 복구할 수 없습니다.\n정말로 삭제 하시겠습니까?",
					"사용자 승인요청", JOptionPane.YES_NO_OPTION);

			if (answer == JOptionPane.YES_OPTION) {
				repository.removeAccount(accountNum);
				JOptionPane.showMessageDialog(this, "해당 계좌를 삭제하였습니다.");
			} else {
				JOptionPane.showMessageDialog(this, "아니오를 선택 하셨습니다.\n확인을 누르시면 메인화면으로 돌아갑니다.");
			}
		} else {
			return;
		}
	}

	/**
	 * 텍스트필드의 활성, 비활성화 choice의 반응으로 호출됨. 추가 : 2023-05-25
	 * 
	 * @param accountType
	 */
	public void selectAccountType(AccountType accountType) {
		switch (accountType) {
		case GENERAL_ACCOUNT:
			boMoneyTF.setEnabled(false);
			inMoneyTF.setEnabled(true);
			passTF.setEnabled(true);
			accNumTF.setEnabled(false);
			break;

		case MINUS_ACCOUNT:
			inMoneyTF.setEnabled(false);
			boMoneyTF.setEnabled(true);
			passTF.setEnabled(true);
			accNumTF.setEnabled(false);
			break;

		case ALL_ACCOUNT:
			boMoneyTF.setEnabled(false);
			inMoneyTF.setEnabled(false);
			passTF.setEnabled(false);
			accNumTF.setEnabled(true);
			break;
		}
	}

	/**
	 * 계좌추가 메서드 2023-05-26추가 최종 수정일 : 2023-05-28 수정 내용 : 각 TF에 알맞는 유효성 검사 메서드 추가.
	 */
	public void addAccount() {
		String accountOwner = accOwnerTF.getText();
		String passCheck = passTF.getText();
		// 예금주와 페스워드가 잘 입력된 후 반환되는 true, false로 갈라지는 기능.
		if (ownerValid(accountOwner) && passValid(passCheck)) {
			int password = Integer.parseInt(passTF.getText());
			String selectedItem = choice.getSelectedItem();
			long inMoney;
			if (selectedItem.equals(AccountType.GENERAL_ACCOUNT.getName())) {
				inMoney = Long.parseLong(inMoneyTF.getText());
				account = new Account(accountOwner, password, inMoney);
			} else if (selectedItem.equals(AccountType.MINUS_ACCOUNT.getName())) {
				inMoney = 0;
				long borrowMoney = Long.parseLong(boMoneyTF.getText());
				account = new MinusAccount(accountOwner, password, inMoney, borrowMoney);
			}
			repository.addAccount(account);
			JOptionPane.showMessageDialog(this, "정상 등록 처리되었습니다.");
		} else {
			return;
		}
	}

	/**
	 * 프로그램 종료 메소드 추가 : 2023-05-26
	 */
	public void exit() {
		int answer = JOptionPane.showConfirmDialog(this, "정말로 종료 하시겠 습니까?", "사용자 승인요청", JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.YES_OPTION) {
			((JdbcAccountRepository)repository).close();
			setVisible(false);
			dispose();
			System.exit(0);
		} else {
			return;
		}
	}

	/**
	 * 반응 이벤트메서드 추가 : 2023-05-26
	 */
	public void addEventListner() {
		// 여기서만 쓰기 위해 지역 내부클래스로 ActionHandler선언
		class ActionHandler implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object eventSource = e.getSource();
				if (eventSource == createNewAccount) {
					addAccount();
				} else if (eventSource == showAll) {
					allList();
				} else if (eventSource == showAccount) {
					searchAcc(true);
				} else if (eventSource == deletAccount) {
					deleteAccount();
				} else if (eventSource == searchAccount) {
					searchAcc(false);
				}
			}
		}
		// 버튼 상호작용을 위해 Listener선언
		ActionListener actionListener = new ActionHandler();

		// 계좌 등록 버튼 상호작용
		createNewAccount.addActionListener(actionListener);

		// 계좌 삭제 버튼 상호작용
		deletAccount.addActionListener(actionListener);

		// 계좌 선택 버튼 상호작용
		choice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (choice.getSelectedItem().equals("입출금계좌")) {
						selectAccountType(AccountType.GENERAL_ACCOUNT);
					} else if (choice.getSelectedItem().equals("마이너스계좌")) {
						selectAccountType(AccountType.MINUS_ACCOUNT);
					} else if (choice.getSelectedItem().equals("전체계좌")) {
						selectAccountType(AccountType.ALL_ACCOUNT);
					}
				}

			}
		});

		// 전체계좌 조회 버튼 상호작용
		showAll.addActionListener(actionListener);

		// 계좌번호 검색 조회 버튼 상호작용
		showAccount.addActionListener(actionListener);

		// 성함으로 계좌 검색 버튼 상호작용
		searchAccount.addActionListener(actionListener);

		// WindowListener 관련 event처리
		// 창이 열릴때
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				allList();
				boMoneyTF.setEnabled(false);
				inMoneyTF.setEnabled(false);
				passTF.setEnabled(false);
			}
		});

		// 종료 처리
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

	}// 반응 이벤트메서드 끝

	/**
	 * 컴포넌트 배치메서드 2023-05-24추가
	 * 
	 * @param 배치할   컴포넌트
	 * @param 좌표x값
	 * @param 좌표y값
	 * @param 오른쪽으로 W만큼 차지할 값
	 * @param 아래로   h만큼 차지할 값
	 * @param 컴포넌트별 중량값 설정
	 */
	private void gblInsert(Component c, int x, int y, int w, int h, double weightx) {
		conts.gridx = x;
		conts.gridy = y;
		conts.gridwidth = w;
		conts.gridheight = h;
		conts.weightx = weightx;
		conts.weighty = 0.0;
		grid.setConstraints(c, conts);
		this.add(c);
	}
}

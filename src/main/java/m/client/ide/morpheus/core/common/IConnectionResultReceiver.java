package m.client.ide.morpheus.core.common;

public interface IConnectionResultReceiver {
	final int CONNECTION_OK = 1000;
	final int CONNECTION_FAIL = 1001;
	final int DISCONNECTION_OK = 1002;
	final int DISCONNECTION_FAIL = 1003;
	final int RESULT_OK = 1004;
	final int RESULT_FAIL = 1005;
	
	/**
	 * 커넥션 상태 또는 결과를 전송받는다.
	 * @param id : 상태 
	 * @param data : 데이터
	 */
	void onReceiveResult(int id, Object data);
	
}

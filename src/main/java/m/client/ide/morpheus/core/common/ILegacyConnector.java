package m.client.ide.morpheus.core.common;

public interface ILegacyConnector {
	
	/**
	 * 연결
	 */
	public boolean connect();
	
	/**
	 * 연결 종료
	 */
	public boolean disconnect();
	
}

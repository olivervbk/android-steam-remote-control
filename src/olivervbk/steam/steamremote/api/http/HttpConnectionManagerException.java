package olivervbk.steam.steamremote.api.http;

public class HttpConnectionManagerException extends Throwable{
	public HttpConnectionManagerException(Throwable cause){
		super();
		initCause(cause);
	}
}
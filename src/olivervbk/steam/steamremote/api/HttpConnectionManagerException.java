package olivervbk.steam.steamremote.api;

public class HttpConnectionManagerException extends Throwable{
	public HttpConnectionManagerException(Throwable cause){
		super();
		initCause(cause);
	}
}
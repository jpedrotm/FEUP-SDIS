package sdis.wetranslate.utils;

public class RequestMethod {
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String GET = "GET";
	public static final String DELETE = "DELETE";
	
	
	public static String[] getInsertMethods() {
		return new String[]{RequestMethod.POST, RequestMethod.PUT};
	}
	
	public static String[] getReadMethods() {
		return new String[]{RequestMethod.GET};
	}
	
	public static String[] getUpdateMethods() {
		return new String[]{RequestMethod.PUT};
	}
	
	public static String[] getDeleteMethods() {
		return new String[]{RequestMethod.DELETE};
	}
}

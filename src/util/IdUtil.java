package util;
import java.util.UUID;

public class IdUtil {
    public static String newsUserId() { return "u_" + UUID.randomUUID();}
    public static String newProductsId() { return "p_" + UUID.randomUUID();}
}

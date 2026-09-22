package cloud.zenixapp.zenix.configs;

public class TenantContext {

    private static final ThreadLocal<String> threadByTenant = new ThreadLocal<>();

    public static void setTenantId(String uuidTenant){
        threadByTenant.set(uuidTenant);
    }

    public static String getTenantId(){
        return threadByTenant.get();
    }

    public static void clear(){
        threadByTenant.remove();
    }
}

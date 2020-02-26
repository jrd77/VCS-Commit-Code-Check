package com.atzuche.order.photo.common;

/**
 * 该类用于获取当前用户的信息
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 3:25 下午
 **/
public class AdminUserUtil {
    private static InheritableThreadLocal<AdminUser> local= new InheritableThreadLocal<AdminUser>(){
        @Override
        protected AdminUser initialValue() {
                  return new AdminUser("init","init");
          }
    };


    public static void clear(){
        local.remove();
    }

    public static void put(AdminUser adminUser){
        local.set(adminUser);
    }

    public static AdminUser getAdminUser(){
        return local.get();
    }



}

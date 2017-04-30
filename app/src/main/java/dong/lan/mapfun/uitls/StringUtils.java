
package dong.lan.mapfun.uitls;

import com.baidu.location.BDLocation;

import dong.lan.avoscloud.bean.AVOUser;

/**
 */

public final class StringUtils {
    private StringUtils() {
        //no instance
    }


    public static String GuidingText(BDLocation location) {
        return "导航位置【" + location.getLatitude() + "," + location.getLongitude() + "】\n"+location.getAddrStr();
    }

    public static String partnerInfo(AVOUser ...user) {
        StringBuilder sb = new StringBuilder();
        if(user!=null){
            for (AVOUser u : user) {
                sb.append("#");
                sb.append(u.getUserName());
                sb.append("  ");
            }
        }
        return sb.toString();
    }
}

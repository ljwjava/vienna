package lerrain.service.image.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件类型相关枚举
 */
public class FileTypeEnum {
    private static Map<String, FILETYPE> map = new HashMap<String, FILETYPE>(); // 文件类型枚举数据

    public enum FILETYPE {
        /** 图片-jpg **/
        JPG("image/jpg", ".jpg", 0),
        JPG_JPEG("image/jpeg", ".jpg", 1),
        /** 账号绑卡信息同步 **/
        JPEG("image/jpeg", ".jpeg", 1),
        /** 账号信息同步 **/
        PNG("image/png", ".png", 0),
        /** 未知服务 **/
        UNKNOWN("unknown", "unknown", 0);

        private String mime; // 文件类型
        private String suffix;      // 文件后缀
        private int weight; // 后缀映射文件类型优先级权重（数值越大越优先）

        private FILETYPE(String mime, String suffix, int weight) {
            this.mime = mime;
            this.suffix = suffix;
            this.weight = weight;
            map.put(mime+suffix+weight, this);
        }

        public String getMime() {
            return this.mime;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public int getWeight() {
            return this.weight;
        }

        public static String getSuffix(String mime) {
            return getEnum(mime).getSuffix();
        }

        public static String getMime(String suffix) {
            FILETYPE s = UNKNOWN;
            int max = Integer.MIN_VALUE;
            if (map != null && map.size() > 0) {
                for (FILETYPE o : map.values()) {
                    if (o.getSuffix().equals(suffix) && max < o.getWeight()) {
                        s = o;
                        max = o.getWeight();
                    }
                }
            }
            return s == UNKNOWN ? null : s.getMime();
        }

        public static FILETYPE getEnum(String mime) {
            FILETYPE s = UNKNOWN;
            if (map != null && map.size() > 0) {
                for (FILETYPE o : map.values()) {
                    if (o.getMime().equals(mime)) {
                        return o;
                    }
                }
            }
            return s;
        }
    }
}

package lerrain.service.user.util;

import java.util.Random;

public class PasswordUtil {
    /** 验证码字符个数 */
    private static final int    codeCount    = 6;

    private static final char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9'                             };

    public static String createPassword() {
        // 创建一个随机数生成器类
        Random random = new Random();
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String strRand = String.valueOf(codeSequence[random.nextInt(36)]);
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        return randomCode.toString();
    }

    public static boolean validatePassword(String password) {
        String reg = "(?=.*[a-zA-Z])(?=.*\\d)^[\\w\\\\-_-=+~`!-@#$%^&*<>\\(\\)\\[\\],.:;'?/|\\{\\}\\\"]{6,20}";
        return password.matches(reg);
    }

    public static void main(String[] args) {
        System.out.println(PasswordUtil.createPassword());
    }
}

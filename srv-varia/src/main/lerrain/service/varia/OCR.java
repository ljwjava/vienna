package lerrain.service.varia;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class OCR
{
    boolean[][][] num = new boolean[10][9][13];

    public static void main(String[] arg) throws Exception
    {
        OCR ocr = new OCR();

        for (int i = 1; i <= 10; i++)
        try (FileInputStream fis = new FileInputStream(new File("x:/1/vc"+i+".jpg")))
        {
            System.out.println(ocr.scan(fis));
        }
    }

    public OCR()
    {
        try
        {
            boolean[][][] vc1 = load("./vc1.jpg");
            boolean[][][] vc2 = load("./vc2.jpg");
            boolean[][][] vc3 = load("./vc3.jpg");
            boolean[][][] vc4 = load("./vc4.jpg");
            boolean[][][] vc5 = load("./vc5.jpg");

            num[0] = vc1[0];
            num[1] = vc2[3];
            num[2] = vc3[2];
            num[3] = vc5[1];
            num[4] = vc5[0];
            num[5] = vc1[3];
            num[6] = vc1[1];
            num[7] = vc3[0];
            num[8] = vc5[2];
            num[9] = vc4[2];
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String scan(InputStream is) throws Exception
    {
        BufferedImage img = ImageIO.read(is);

        String res = "";

        for (int k = 0; k < 4; k++)
            res += match(img, 7 + k * 13, 3);

        return res;
    }

    private int match(BufferedImage img, int x, int y)
    {
        int[] m = new int[10];
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 13; j++)
            {
                int c = img.getRGB(x + i, y + j);

                int r = (c & 0x00FF0000) >> 16;
                int g = (c & 0x0000FF00) >> 8;
                int b = (c & 0x000000FF);

                for (int k = 0; k < 10; k++)
                {
                    if (num[k][i][j] != r + g + b <= 384)
                        m[k]++;
                }
            }
        }

        int match = 0;
        for (int k = 1; k < 10; k++)
            if (m[match] > m[k])
                match = k;

        return match;
    }

    private boolean[][][] load(String file) throws Exception
    {
//        BufferedImage img = ImageIO.read(new File(file));
        BufferedImage img = ImageIO.read(new File(file));

//        int w = img.getWidth();
//        int h = img.getHeight();

        boolean[][][] num = new boolean[4][9][13];

        for (int k = 0; k < 4; k++)
        {
            for (int x = 7 + k * 13; x < 16 + k * 13; x++)
            {
                for (int y = 3; y < 16; y++)
                {
                    int c = img.getRGB(x, y);

                    int r = (c & 0x00FF0000) >> 16;
                    int g = (c & 0x0000FF00) >> 8;
                    int b = (c & 0x000000FF);

                    num[k][(x - 7) % 13][y-3] = r + g + b <= 384;
                }
            }
        }

//        for (int k = 0; k < 4; k++)
//        {
//            for (int x = 0; x < 9; x++)
//            {
//                for (int y = 0; y < 13; y++)
//                {
//                    if (num[k][x][y])
//                        System.out.print("@");
//                }
//                System.out.println();
//            }
//            System.out.println();
//        }

        return num;
    }
}

package lerrain.project.vienna.service;

import lerrain.tool.Disk;

import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebService
{
    @Value("${path.root}")
    String dir;

    Map<String, byte[]> cache = new HashMap<>();

    Map<String, Script> script = new HashMap<>();

    Map<String, String> map = new HashMap<>();

    @PostConstruct
    private void reset()
    {
        System.out.println(new File(dir).getAbsolutePath());

        loadWidget("", new File(dir));

        String[] type = new String[]{"web", "mobile"};

        for (String t : type)
        {
            String html = Disk.load(new File(dir + "frame." + t), "utf-8");
            if (html != null)
            {
                int pos1 = html.indexOf("<!--HEAD-->");
                int pos2 = html.indexOf("<!--HTML-->");
                String header = html.substring(0, pos1);
                String center = html.substring(pos1 + 11, pos2);
                String footer = html.substring(pos2 + 11);

                cache.put(t + "/header", header.getBytes());
                cache.put(t + "/center", center.getBytes());
                cache.put(t + "/footer", footer.getBytes());

                cache.put(t + "/header2", header.replaceAll("[.][/]", "../").getBytes());
                cache.put(t + "/center2", center.replaceAll("[.][/]", "../").getBytes());
                cache.put(t + "/footer2", footer.replaceAll("[.][/]", "../").getBytes());
            }
        }
    }

    private void loadWidget(String root, File dir)
    {
        File[] fs = dir.listFiles();
        if (fs == null)
            return;

        for (File f : fs)
        {
            if (f.isDirectory())
            {
                loadWidget(root + f.getName() + "/", f);
                continue;
            }

            String name = f.getName();

            if (name.endsWith(".jsx"))
            {
                String str = Disk.load(f, "utf-8");

//            System.out.println(root + f.getName() + ": " + str);

                int pos2 = str.lastIndexOf("module.exports");
                if (pos2 < 0)
                    continue;

                int p1 = str.lastIndexOf("import ");
                int pos1 = str.indexOf(";", p1);

                map.put(root + f.getName(), str.substring(pos1 + 1, pos2));
            }
            else if (name.endsWith(".script"))
            {
                String str = Disk.load(f, "utf-8");
                script.put(name.substring(0, name.indexOf(".")), Script.scriptOf(str));
            }
        }
    }

    public Map run(String filePre)
    {
        Script s = (Script)script.get(filePre);
        if (s == null)
            return null;

        Map m = new HashMap();
        Stack stack = new Stack(m);
        s.run(stack);

        return m;
    }

    public boolean exists(String file)
    {
        return new File(dir + file).exists();
    }

    public File getFile(String file)
    {
        return new File(dir + file);
    }

    public byte[] getCache(String type, String key)
    {
        return cache.get(type + "/" + key);
    }

    public String loadJsx(String file)
    {
        File f = new File(dir + file);
        String res = Disk.load(f, "utf-8");

        int pos = file.indexOf("/");
        String prefix = pos < 0 ? "" : (file.substring(0, pos) + "/");

        StringBuffer s = new StringBuffer();

        int p = 0, p1 = 0;
        while ((p = res.indexOf(" from ", p + 1)) >= 0)
        {
            p1 = res.indexOf(";", p);
            String ff = res.substring(p + 6, p1).trim();
            ff = ff.substring(1, ff.length() - 1);

            if (ff.startsWith("./"))
                ff = prefix + ff.substring(2);
            else if (ff.startsWith("../"))
                ff = ff.substring(3);

            if (map.containsKey(ff))
                s.append(map.get(ff) + "\n");

//            for (String key : map.keySet())
//            {
//                if (ff.indexOf(key) >= 0)
//                {
//                    s.append(map.get(key) + "\n");
//                }
//            }
        }

        s.append(res.substring(p1 + 1));
        return s.toString();
    }
}

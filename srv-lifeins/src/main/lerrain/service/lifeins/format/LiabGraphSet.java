package lerrain.service.lifeins.format;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LiabGraphSet
{
    String name;

    List<LiabGraphType> types = new ArrayList<>();

    public LiabGraphSet(String name)
    {
        this.name = name;
    }

    public LiabGraphType newType(String type, String name)
    {
        LiabGraphType lgt = new LiabGraphType(type, name);
        types.add(lgt);

        return lgt;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<LiabGraphType> toList()
    {
        return types;
    }

    class LiabGraphType
    {
        String type;
        String name;

        Map<String, ScriptText> content = new LinkedHashMap<>();

        public LiabGraphType(String type, String name)
        {
            this.type = type;
            this.name = name;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public void addMode(String code, ScriptText text)
        {
            content.put(code, text);
        }

        public Map<String, ScriptText> getContent()
        {
            return content;
        }
    }
}

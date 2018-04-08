package lerrain.service.underwriting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Underwriting
{
    public static final char RESULT_NULL     = 'N';
    public static final char RESULT_PASS     = 'P';
    public static final char RESULT_FAIL     = 'F';
    public static final char RESULT_CONTINUE = 'C';

    public static final int STEP_APPLY       = 1;
    public static final int STEP_HEALTH1     = 101;
    public static final int STEP_HEALTH2     = 102;

    Long id;

    Map<Integer, Map<String, Object>> val = new HashMap<>();
    Map<Integer, Character> res = new HashMap<>();
    Map<Integer, List<Quest>> quest = new HashMap<>();

    public void setAnswer(int step, Map<String, Object> answer)
    {
        val.put(step, answer);
    }

    public void setResult(int step, char result)
    {
        res.put(step, result);
    }

    public void setQuests(int step, List<Quest> list)
    {
        quest.put(step, list);
    }

    public char getResult(int step)
    {
        if (!res.containsKey(step))
            return RESULT_NULL;

        return res.get(step);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
}

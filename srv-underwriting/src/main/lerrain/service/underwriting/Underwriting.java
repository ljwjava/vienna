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

    public static final int STEP_APPLY = 1;

    Long id;

    Map<Integer, Map<String, Object>> val = new HashMap<>();
    Map<Integer, Character> res = new HashMap<>();
    Map<Integer, List<Quest>> quest = new HashMap<>();

    public void setAnswer(int step, Map<String, Object> answer, char result)
    {
        val.put(step, answer);
        res.put(step, result);
    }

    public void setQuestList(int step, List<Quest> list)
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

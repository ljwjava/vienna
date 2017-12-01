package lerrain.service.lifeins.plan.quest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QusetService
{
    @Autowired
    QuestDao questDao;

    Map<String, QuestDefine> map;

    public void reset()
    {
        questDao.loadClauseFactors();

        map = new HashMap<>();
        List<QuestDefine> list = questDao.loadAllQuests();
        for (QuestDefine qd : list)
        {
            map.put(qd.getCode(), qd);
        }
    }
}

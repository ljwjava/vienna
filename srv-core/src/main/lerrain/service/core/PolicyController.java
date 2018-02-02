package lerrain.service.core;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PolicyController
{
    @Autowired
    PolicyService policySrv;

    @RequestMapping("/upload/excel.json")
    @ResponseBody
    public JSONObject uploadExcel(@RequestBody JSONObject p)
    {
        for (Object str : p.values())
        {
            List<Object[]> tab = new ArrayList<>();

            try (InputStream is = new ByteArrayInputStream(Common.decodeBase64ToByte((String)str)))
            {
                Workbook workbook = WorkbookFactory.create(is);
                Sheet sheet = workbook.getSheetAt(0);

                int len = sheet.getLastRowNum();
                for (int i = 0; i < len; i++)
                {
                    Row row = sheet.getRow(i);
                    if (row == null)
                        continue;

                    int cols = row.getLastCellNum();

                    Object[] line = new Object[cols];
                    for (int j = 0; j < cols; j++)
                    {
                        Cell cell = row.getCell(j);
                        if (cell != null)
                        {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            line[j] = cell.getStringCellValue();
                        }
                    }

                    boolean pass = false;
                    for (int j = 0; j < line.length; j++)
                    {
                        if (line[j] != null && !"".equals(Common.trimStringOf(line[j])))
                        {
                            pass = true;
                            break;
                        }
                    }

                    if (pass)
                        tab.add(line);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (!tab.isEmpty())
            {
                String batchUUID = policySrv.upload(tab);

            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}

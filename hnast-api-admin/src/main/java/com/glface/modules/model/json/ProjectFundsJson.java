package com.glface.modules.model.json;

import com.glface.base.utils.StringUtils;
import com.glface.modules.model.ProjectFunds;
import com.glface.modules.model.ProjectStage;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 经费支出预算明细
 */
@Data
public class ProjectFundsJson {
    private String name;//支出内容
    private float money;//支出金额（万元）
    private String remark;//预算说明

    public static ProjectFundsJson fromProjectFunds(ProjectFunds projectFunds){
        if(projectFunds==null){
            return null;
        }
        ProjectFundsJson fundsJson = new ProjectFundsJson();
        fundsJson.setName(projectFunds.getName());
        fundsJson.setMoney(projectFunds.getMoney());
        fundsJson.setRemark(StringUtils.replaceHtmlBr(projectFunds.getRemark()));
        return fundsJson;
    }

    public static List<ProjectFundsJson> fromProjectFundsList(List<ProjectFunds> projectFundsList){
        List<ProjectFundsJson> list = new ArrayList<>();
        if(projectFundsList==null){
            return list;
        }
        Collections.sort(projectFundsList, new Comparator<ProjectFunds>() {
            @Override
            public int compare(ProjectFunds o1, ProjectFunds o2) {
                if(o1.getSort()>o2.getSort()){
                    return 1;
                }else if(o1.getSort()<o2.getSort()){
                    return -1;
                }
                return  0;
            }
        });
        for(ProjectFunds funds:projectFundsList){
            ProjectFundsJson fundsJson = fromProjectFunds(funds);
            if(fundsJson!=null){
                list.add(fundsJson);
            }
        }
        return list;
    }

}

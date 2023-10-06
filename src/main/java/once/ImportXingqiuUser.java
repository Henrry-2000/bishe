package once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: bisheBackend-master
 * @description: 用于导入星球用户的信息
 * @author: HenryYang
 * @create: 2023-03-14 11:03
 **/
public class ImportXingqiuUser {
    public static void main(String[] args) {
        String fileName = "xxx";
//        缺少数据
        List<XingqiuUserInfo> userInfoList = EasyExcel.read(fileName).head(XingqiuUserInfo.class).sheet().doReadSync();
        System.out.println("总数："+ userInfoList.size());
//        把昵称相同的用户分为一个组
        Map<String,List<XingqiuUserInfo>> listMap = userInfoList.stream()
                .filter(xingqiuUserInfo -> StringUtils.isNotEmpty(xingqiuUserInfo.getUsername()))
                .collect(Collectors.groupingBy(XingqiuUserInfo::getUsername));
        System.out.println("不重复的用户数"+listMap.keySet().size());
    }
}
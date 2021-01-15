package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.entity.Result;
import com.example.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {

    private Log log = LogFactory.get(VoteService.class);

    @Autowired
    GlobalVariable globalVariable;

    public String voteResult(List<Result> list, String jobType){
        String res = "";

        if (list.size() == 1){
            res = list.get(0).getResult();
            if (!Util.hasElement(globalVariable.getBypass_failed_result_list(), list.get(0).getResult())){
                list.get(0).setFlag(1);
            }
        } else if (list.size() > 1){
            if (jobType.equals("6")){
                res = angleVote(list);
            } else if (jobType.equals("5")){
                res = charactorVote(list);
            } else {
                res = charactorVote(list);
            }
        }

        return res;
    }

    // 单纯的字符串比对投票
    public String charactorVote(List<Result> list){
        // 初始化结果变量和中间列表
        String res = "";
        List<Result> temp = preProcess(list);
        float max = -100;
        int index1 = 0, index2 = 1;

        if (temp.size()==0){
            res = list.get(0).getResult();
        }
        if (temp.size()==1){
            res = temp.get(0).getResult();
        }
        if (temp.size()>1){
            for (int i = 0; i < temp.size() - 1 ; i ++ ){
                for (int j = i + 1 ; j < temp.size() ; j ++ ){
                    float tempMax = levenshtein(temp.get(i).getResult(), temp.get(j).getResult());
                    if (tempMax>max){
                        max = tempMax;
                        index1 = i;
                        index2 = j;
                    }
                }
            }
            temp.get(index1).setFlag(1);
            temp.get(index2).setFlag(1);
            res = temp.get(index1).getResult();
        }

        return res;
    }

    // 角度比对算法
    public String angleVote(List<Result> list){
        String res = "";
        List<Result> temp = preProcess(list);

        int min = 361, tempMin = 0;
        int index1 = 0, index2 = 0, tempAngle1 = 0, tempAngle2 = 0;

        if (temp.size()==0){
            res = list.get(0).getResult();
        }
        if (temp.size()==1){
            res = temp.get(0).getResult();
        }
        if (temp.size()>1){
            for (int i = 0; i<list.size()-1; i++){
                for (int j = i + 1 ; j<list.size(); j++){
                    tempAngle1 = Integer.parseInt(list.get(i).getResult().trim());
                    tempAngle2 = Integer.parseInt(list.get(j).getResult().trim());
                    tempMin = Math.abs(tempAngle1 - tempAngle2);
                    if ( tempMin < min ){
                        min = tempMin;
                        index1 = i;
                        index2 = j;
                    }
                }
            }
            temp.get(index1).setFlag(1);
            temp.get(index2).setFlag(1);
            res = Integer.parseInt(temp.get(index1).getResult()) + "";
        }

        return res;
    }

    // 轨迹比对算法
    public String trackVote(List<Result> list){
        String res = "";
        List<Result> temp = preProcess(list);

        return res;
    }

    // 将出错的结果去掉
    public List<Result> preProcess(List<Result> list){
        List<Result> res = new ArrayList<>();
        for (Result result : list){
            if (!Util.hasElement(globalVariable.getBypass_failed_result_list(),result.getResult())){
                res.add(result);
            } else {
                result.setFlag(-1);
            }
        }
        return res;
    }

    // 字符串相似度算法
    public float levenshtein(String str1,String str2) {
        //计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        //建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
                        dif[i - 1][j] + 1);
            }
        }
        log.info("字符串\""+str1+"\"与\""+str2+"\"的比较");
        //取数组右下角的值，同样不同位置代表不同字符串的比较
        log.info("差异步骤："+dif[len1][len2]);
        //计算相似度
        float similarity =1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
        return similarity;
    }
    //得到最小值
    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }
}

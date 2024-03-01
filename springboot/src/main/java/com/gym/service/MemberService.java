package com.gym.service;

import cn.hutool.core.util.IdUtil;
import com.gym.entity.Admin;
import com.gym.entity.Common;
import com.gym.entity.Member;
import com.gym.mapper.MemberMapper;
import com.gym.mapper.RegisterMapper;
import com.gym.utils.JwtUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private RegisterMapper registerMapper;

    public List<Member> getMemberMapper(int page, int size) {
        return memberMapper.getAllMember(page, size);
    }

    public List<Member> getAllMemberNoPage() {
        return memberMapper.getAllMemberNoPage();
    }

    public Map<String, Object> saveMemberBatch(List<Member> list) {
        Map<String, Object> resultMap = new HashMap<>();

        int result = memberMapper.saveMemberBatch(list);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "导入成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "导入失败");
        }

        return resultMap;
    }

    public Map<String, Object> addMember(Member member) {

        Map<String, Object> resultMap = new HashMap<>();


        /*随机生成用户名*/
        int Username = java.util.UUID.randomUUID().toString().hashCode();
        if (Username < 0) {
            Username = -Username;
        }
        // 0 代表前面补充0
        // 10 代表长度为10
        // d 代表参数为正数型
        String format = String.format("%010d", Username).substring(0, 10);
        member.setMemberUsername(format);

        /*默认密码*/
        member.setMemberPassword("123456");
        member.setCardTime(LocalDateTime.now());

        /*新增用户时将购买时间=剩余时间*/
        member.setCardNextClass(member.getCardClass());

        int result = memberMapper.addMember(member);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "创建成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "创建失败");
        }

        return resultMap;
    }

    public Map<String, Object> deleteMember(int memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        int result = memberMapper.deleteMember(memberNo);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "删除成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "删除失败");
        }
        return resultMap;
    }

    public Map<String, Object> updateMember(Member member) {

        Map<String, Object> resultMap = new HashMap<>();


        int result = memberMapper.updateMember(member);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "修改成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "修改失败");
        }
        return resultMap;
    }

    public Map<String, Object> updateMemberByMemberNo(Member member) {

        Map<String, Object> resultMap = new HashMap<>();

        int result = memberMapper.updateMemberByMemberNo(member);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "修改成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "修改失败");
        }
        return resultMap;
    }

    public Common totalMember() {
        return memberMapper.totalMember();
    }

    public List<Member> getByKeywordMember(String keyWord, int page, int size) {
        return memberMapper.getByKeywordMember(keyWord, page, size);
    }

    public Common totalMemberFuzzy(String keyWord) {
        return memberMapper.totalMemberFuzzy(keyWord);
    }


    public Map<String, Object> getMemberPassword(String memberPhone, String memberPassword) {
        Map<String, Object> resultMap = new HashMap<>();
        Member result = memberMapper.getMemberPassword(memberPhone, memberPassword);

        if (result != null) {
            result.setToken(JwtUtil.createTokenToMember());
            resultMap.put("token", result.getToken());
            resultMap.put("memberUsername", result.getMemberUsername());
            resultMap.put("memberNo", result.getMemberNo());
            resultMap.put("memberPhone", result.getMemberPhone());
            resultMap.put("code", 200);
            resultMap.put("message", "登录成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "登录失败");
        }
        return resultMap;
    }

    public Member getMemberByMemberNo(int memberNo) {
        return memberMapper.getMemberByMemberNo(memberNo);
    }

    public double getMemberIntegral(int memberNo) {
        return memberMapper.getMemberIntegral(memberNo);
    }


    public double getMemberPower(int memberNo) {
        return memberMapper.getMemberPower(memberNo);
    }

    public Double getMemberChange(int memberNo) {
        return memberMapper.getMemberChange(memberNo);
    }

    public Double getTotalMoney(int memberNo) {
        Double totalMoney = memberMapper.getTotalMoney(memberNo);
        if (totalMoney == null) {
            totalMoney = (double) 0;
        }
        return totalMoney;
    }


    public int updateMemberChange(int memberNo) {
        //获取购物总额
        Double totalBuy = registerMapper.getTotalBuyByMemberNo(memberNo);
        if (totalBuy == null) {
            totalBuy = Double.valueOf(0);
        }
        return memberMapper.updateMemberChange(memberNo, totalBuy);
    }

    public Map<String,Object> updateMemberChangeByMemberNo(int memberNo, double coursePrice) {
        Map<String, Object> resultMap = new HashMap<>();
        if (memberMapper.updateMemberChangeByMemberNo(memberNo, coursePrice) > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "余额不足");
        }
        return resultMap;
    }

    public Map<String, Object> updateMemberIntegral(double price, int memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        if (memberMapper.updateMemberIntegral(price, memberNo) > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "积分不足");
        }
        return resultMap;
    }

    public Map<String, Object> updateMemberPassword(String memberPhone, String memberPassword) {
        Map<String, Object> resultMap = new HashMap<>();

        int result = memberMapper.updateMemberPassword(memberPhone, memberPassword);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "密码修改成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "密码修改失败");
        }
        return resultMap;
    }

    public Map<String, Object> updateMemberPower(int memberPower, int memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        int result = memberMapper.updateMemberPower(memberPower, memberNo);
        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "兑换成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "兑换失败");
        }
        return resultMap;
    }

}

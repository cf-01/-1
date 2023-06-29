package com.gym.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.gym.entity.Common;
import com.gym.entity.Member;
import com.gym.service.MemberService;
import com.gym.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(path="/getAllMember")
    public List<Member> getAllMember(int page,int size){
        return memberService.getMemberMapper(page,size);
    }

    @RequestMapping(path = "/addMember")
    public Map<String,Object> addMember(Member member) {
        return memberService.addMember(member);
    }

    @RequestMapping(path="/deleteMember")
    public Map<String,Object> deleteMember(int memberNo) {
        return memberService.deleteMember(memberNo);
    }


    @RequestMapping(path="/updateMember")
    public Map<String,Object> updateMember(Member member) {
        return memberService.updateMember(member);
    }

    @RequestMapping(path="/updateMemberByMemberNo")
    public Map<String,Object> updateMemberByMemberNo(Member member) {
        return memberService.updateMemberByMemberNo(member);
    }

    @RequestMapping(path = "/totalMember")
    public Common totalMember() {
        return memberService.totalMember();
    }


    @RequestMapping(path="/getMemberPassword")
    @CrossOrigin
    public Map<String,Object> getMemberPassword(String memberPhone,String memberPassword){
        return memberService.getMemberPassword(memberPhone,memberPassword);
    }


    @RequestMapping(path = "/getByKeywordMember")
        public List<Member> getByKeywordMember(String keyWord,int page,int size){
            return memberService.getByKeywordMember(keyWord,page,size);
    }

    @RequestMapping(path = "/totalMemberFuzzy")
    public Common totalMemberFuzzy(String keyWord) {
        return memberService.totalMemberFuzzy(keyWord);
    }

    @RequestMapping(path = "/getMemberIntegral")
    public double getMemberIntegral(int memberNo) {
        return memberService.getMemberIntegral(memberNo);
    }

    @ResponseBody
    @RequestMapping(path = "/getMemberChange")
    public double getMemberChange(int memberNo) {
        return memberService.getMemberChange(memberNo);
    }

    @RequestMapping(path = "/getMemberByMemberNo")
    public Member getMemberByMemberNo(Integer memberNo) {
        return memberService.getMemberByMemberNo(memberNo);
    }

    @RequestMapping(path = "/getTotalMoney")
    public Double getTotalMoney(Integer memberNo) {
        return memberService.getTotalMoney(memberNo);
    }

    @RequestMapping(path = "/getMemberPower")
    public double getMemberPower(Integer memberNo) {
        return memberService.getMemberPower(memberNo);
    }


    @RequestMapping(path = "/updateMemberChange")
    public double updateMemberChange(Integer memberNo) {
        return memberService.updateMemberChange(memberNo);
    }

    @RequestMapping(path = "/updateMemberIntegral")
    public double updateMemberIntegral(Double price,Integer memberNo) {
        return memberService.updateMemberIntegral(price,memberNo);
    }

    @RequestMapping(path = "/updateMemberChangeByMemberNo")
    public double updateMemberChangeByMemberNo(int memberNo,double coursePrice) {
        return memberService.updateMemberChangeByMemberNo(memberNo,coursePrice);
    }

    @RequestMapping(path = "/updateMemberPassword")
    public Map<String,Object> updateMemberPassword(String memberPhone,String memberPassword) {
        return memberService.updateMemberPassword(memberPhone,memberPassword);
    }

    @RequestMapping(path = "/updateMemberPower")
    public  Map<String,Object> updateMemberPower(int memberPower,int memberNo) {
        return memberService.updateMemberPower(memberPower,memberNo);
    }

    /**
     * 导出接口
     */
    @GetMapping("/memberExport")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Member> list = memberService.getAllMemberNoPage();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用户信息.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        /*writer.addHeaderAlias("memberNo", "用户编号");
        writer.addHeaderAlias("memberUsername", "用户名");
        writer.addHeaderAlias("memberName", "姓名");
        writer.addHeaderAlias("memberAge", "年龄");
        writer.addHeaderAlias("memberGender", "性别");
        writer.addHeaderAlias("memberPhone", "电话");
        writer.addHeaderAlias("memberHeight", "身高");
        writer.addHeaderAlias("memberWeight", "体重");
        writer.addHeaderAlias("cardTime", "开卡时间");
        writer.addHeaderAlias("cardClass", "购买课时");
        writer.addHeaderAlias("cardNextClass", "剩余课时");
        writer.addHeaderAlias("memberPassword", "用户密码");*/

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("会员信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

    }


    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @RequestMapping("/memberImport")
    @CrossOrigin
    public Map<String, Object> imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
//        List<User> list = reader.readAll(User.class);

        // 方式2：忽略表头的中文，直接读取表的内容 但是是手动的不推荐
        // List<List<Object>> list = reader.read(1);
        // List<User> users = CollUtil.newArrayList();
        // for (List<Object> row : list) {
        //     User user = new User();
        //     user.setUsername(row.get(0).toString());
        //     user.setPassword(row.get(1).toString());
        //     user.setNickname(row.get(2).toString());
        //     user.setEmail(row.get(3).toString());
        //     user.setPhone(row.get(4).toString());
        //     user.setAddress(row.get(5).toString());
        //     user.setAvatarUrl(row.get(6).toString());
        //     users.add(user);
        // }
        /*HashMap<String, String> headerAlias = new HashMap<>(6);
        headerAlias.put("用户名", "username");
        headerAlias.put("昵称", "nickname");
        headerAlias.put("邮箱", "email");
        headerAlias.put("电话", "phone");
        headerAlias.put("地址", "address");
        headerAlias.put("头像", "avatarUrl");
        reader.setHeaderAlias(headerAlias);*/

        List<Member> list = reader.readAll(Member.class);

        System.out.println(list);

        //上传的excel数据,保存到数据库中
        Map<String, Object> stringObjectMap = memberService.saveMemberBatch(list);
        return stringObjectMap;
    }

}
